package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.core.client.animation.bind.ClientExecutionContextImpl;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.other.BlockEntityRenderData;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.data.BlockEntityAnimationState;
import moe.plushie.armourers_workshop.core.data.EntityAnimationState;
import moe.plushie.armourers_workshop.core.data.action.EntityAction;
import moe.plushie.armourers_workshop.core.data.action.EntityActionSet;
import moe.plushie.armourers_workshop.core.data.action.EntityActionTarget;
import moe.plushie.armourers_workshop.core.data.action.EntityActions;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimationLoop;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.ExtraCodecs;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import moe.plushie.armourers_workshop.core.utils.TickUtils;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimationManager {

    public static final AnimationManager NONE = new AnimationManager(null);

    private final HashMap<BakedSkin, Entry> allEntries = new HashMap<>();
    private final HashMap<BakedSkin, Entry> activeEntries = new HashMap<>();

    private final ArrayList<Entry> triggerableEntries = new ArrayList<>();

    private final ArrayList<Pair<AnimationPlayState, Runnable>> removeOnCompletion = new ArrayList<>();

    private final HashMap<String, PlayAction> lastActions = new HashMap<>();

    private EntityActionSet lastAnimationState;
    private double lastAnimationTick = 0;

    private final ClientExecutionContextImpl executionContext;

    public AnimationManager(Object entity) {
        this.executionContext = new ClientExecutionContextImpl(entity);
    }

    public static AnimationManager of(Entity entity) {
        var renderData = EntityRenderData.of(entity);
        if (renderData != null) {
            return renderData.animationManager();
        }
        return null;
    }

    public static AnimationManager of(BlockEntity blockEntity) {
        var renderData = BlockEntityRenderData.of(blockEntity);
        if (renderData != null) {
            return renderData.animationManager();
        }
        return null;
    }

    public void load(Map<SkinDescriptor, BakedSkin> skins) {
        var expiredEntries = new HashMap<>(allEntries);
        skins.forEach((key, skin) -> {
            expiredEntries.remove(skin);
            allEntries.computeIfAbsent(skin, Entry::new);
        });
        expiredEntries.forEach((key, entry) -> {
            allEntries.remove(key);
            entry.stop();
        });
        rebuildTriggerableEntities();
        setChanged();
    }

    public void active(Map<SkinDescriptor, BakedSkin> skins) {
        var expiredEntries = new HashMap<>(activeEntries);
        skins.forEach((key, skin) -> {
            var entry = expiredEntries.remove(skin);
            if (entry != null) {
                return; // no change, ignore.
            }
            entry = allEntries.get(skin);
            if (entry == null) {
                return; // no found, ignore.
            }
            activeEntries.put(skin, entry);
            resumeState(entry);
        });
        expiredEntries.forEach((key, entry) -> {
            entry.animationControllers().forEach(it -> lastActions.remove(it.name()));
            activeEntries.remove(key);
            entry.stop();
        });
    }

    public void tick(Object source, double animationTime) {
        // clear invalid animation.
        if (!removeOnCompletion.isEmpty()) {
            var iterator = removeOnCompletion.iterator();
            while (iterator.hasNext()) {
                var entry = iterator.next();
                var state = entry.getKey();
                state.tick(animationTime);
                if (state.isCompleted()) {
                    iterator.remove();
                    entry.getValue().run();
                }
            }
        }
        // play triggerable animation by the entity state.
        if (!triggerableEntries.isEmpty()) {
            var animationState = getAnimationState(source);
            if (animationState != null && !animationState.equals(lastAnimationState)) {
                if (ModConfig.Client.enableAnimationDebug) {
                    ModLog.debug("{} action did change: {}", source, animationState);
                }
                triggerableEntries.forEach(entry -> entry.autoplay(animationState, animationTime));
                lastAnimationState = animationState.copy();
            }
        }
        lastAnimationTick = animationTime;
    }

    public void play(String name, double atTime, CompoundTag tag) {
        lastActions.put(name, new PlayAction(name, atTime, tag));
        for (var entry : activeEntries.values()) {
            for (var animationController : entry.animationControllers()) {
                if (name.equals(animationController.name())) {
                    entry.play(animationController, atTime, tag);
                }
            }
        }
    }

    public void stop(String name) {
        if (name.isEmpty()) {
            lastActions.clear();
        } else {
            lastActions.remove(name);
        }
        for (var entry : activeEntries.values()) {
            for (var animationController : entry.animationControllers()) {
                if (name.isEmpty() || name.equals(animationController.name())) {
                    entry.stop(animationController);
                }
            }
        }
    }

    public void map(String from, String to) {
        allEntries.forEach((skin, entry) -> entry.map(from, to));
        rebuildTriggerableEntities();
        setChanged();
    }

    @Nullable
    public AnimationContext getAnimationContext(BakedSkin skin) {
        return allEntries.get(skin);
    }

    @Nullable
    private EntityActionSet getAnimationState(Object source) {
        if (source instanceof Entity entity) {
            var animationState = EntityAnimationState.of(entity);
            if (animationState != null) {
                animationState.tick(entity);
                return animationState;
            }
        }
        if (source instanceof BlockEntity entity) {
            var animationState = BlockEntityAnimationState.of(entity);
            if (animationState != null) {
                animationState.tick(entity);
                return animationState;
            }
        }
        return null;
    }

    private void setChanged() {
        lastAnimationState = null;
    }

    private void resumeState(Entry entry) {
        entry.autoplay();
        lastActions.forEach((name, action) -> {
            for (var animationController : entry.animationControllers()) {
                if (name.equals(animationController.name())) {
                    action.resume(entry, animationController);
                }
            }
        });
    }

    private void rebuildTriggerableEntities() {
        triggerableEntries.clear();
        triggerableEntries.addAll(Collections.filter(allEntries.values(), Entry::hasTriggerableAnimation));
    }

    protected class Entry extends AnimationContext {

        protected final List<TriggerableController> triggerableControllers = new ArrayList<>();

        protected final HashMap<String, String> actionToName = new HashMap<>();

        protected TriggerableController playing;
        protected boolean isLocking;
        protected boolean isFirstTransitionAnimation = true;

        public Entry(BakedSkin skin) {
            super(AnimationManager.this.executionContext, skin.animationControllers());
            this.rebuildTriggerableControllers();
        }

        public void map(String action, String newName) {
            if (action.equals(newName) || newName.isEmpty()) {
                actionToName.remove(action);
            } else {
                actionToName.put(action, newName);
            }
            rebuildTriggerableControllers();
        }

        public void autoplay() {
            animationControllers.stream().filter(AnimationController::isParallel).forEach(it -> {
                // autoplay the parallel animation.
                startPlay(it, TickUtils.animationTick(), 1, 0);
            });
        }

        public void autoplay(EntityActionSet actionSet, double time) {
            // If it's already locked, we won't switch.
            if (isLocking && playing != null) {
                return;
            }
            var newValue = findTriggerableController(actionSet);
            if (newValue != null && newValue != playing) {
                play(newValue, playing, time, 1, newValue.playCount(), false);
            }
        }

        public void play(AnimationController animationController, double time, CompoundTag tag) {
            // play parallel animation (simple).
            var properties = new Properties(new TagSerializer(tag));
            if (animationController.isParallel()) {
                startPlay(animationController, time, properties.speed, properties.playCount);
                return;
            }
            // play triggerable animation (lock).
            var newValue = findTriggerableController(animationController);
            if (newValue != null && newValue != playing) {
                play(newValue, playing, time, properties.speed, properties.playCount, properties.needsLock);
            }
        }

        public void stop(AnimationController animationController) {
            var playState = getPlayState(animationController);
            if (playState == null) {
                return; // ignore non-playing animation.
            }
            // stop parallel animation (simple).
            if (animationController.isParallel()) {
                stopPlayIfNeeded(animationController);
                return;
            }
            // ignore, when not found.
            var oldValue = playing;
            if (oldValue == null || oldValue.animationController != animationController) {
                return;
            }
            playing = null;
            isLocking = false;
            stopPlayIfNeeded(animationController);
            setChanged();
        }

        public void stop() {
            animationControllers.forEach(this::stop);
        }


        public boolean hasTriggerableAnimation() {
            return !triggerableControllers.isEmpty();
        }

        private void play(TriggerableController newValue, @Nullable TriggerableController oldValue, double time, double speed, int playCount, boolean needLock) {
            var toAnimationController = newValue.animationController;
            var fromAnimationController = Objects.flatMap(oldValue, it -> it.animationController);

            // clear prev play state.
            if (fromAnimationController != null) {
                stopPlayIfNeeded(fromAnimationController);
            }

            // set next play state.
            startPlay(toAnimationController, time, speed, playCount);

            isLocking = needLock;
            playing = newValue;

            // TODO: @SAGESSE Add transition duration support.
            var duration = newValue.transitionDuration();
            applyTransiting(fromAnimationController, toAnimationController, time, speed, duration);
        }

        private void startPlay(AnimationController animationController, double time, double speed, int playCount) {
            stopPlayIfNeeded(animationController);
            var newPlayState = AnimationPlayState.create(time, playCount, speed, animationController);
            addPlayState(animationController, newPlayState);
            if (ModConfig.Client.enableAnimationDebug) {
                ModLog.debug("start play {}", animationController);
            }
            if (newPlayState.loopCount() > 0) {
                removeOnCompletion.add(Pair.of(newPlayState, () -> stop(animationController)));
            }
        }

        private void stopPlayIfNeeded(AnimationController animationController) {
            var oldPlayState = removePlayState(animationController);
            if (oldPlayState != null) {
                if (ModConfig.Client.enableAnimationDebug) {
                    ModLog.debug("stop play {}", animationController);
                }
                oldPlayState.reset();
                removeOnCompletion.removeIf(it -> it.getLeft() == oldPlayState);
            }
        }

        private void applyTransiting(AnimationController fromAnimationController, AnimationController toAnimationController, double time, double speed, double duration) {
            // we need to ignore the first transition animation, because have some very strange effects.
            if (isFirstTransitionAnimation) {
                isFirstTransitionAnimation = false;
                return;
            }
            // delay the animation start time.
            var playState = getPlayState(toAnimationController);
            if (playState != null) {
                playState.setTime(playState.time() + duration);
            }
            addAnimation(fromAnimationController, toAnimationController, time, speed, duration);
        }

        private String resolveMappingName(String name) {
            // map idle sit/idle
            for (var entry : actionToName.entrySet()) {
                if (entry.getValue().equals(name)) {
                    return entry.getKey(); // name to action.
                }
                if (entry.getKey().equals(name)) {
                    return "redirected:" + name;  // name is action, but it was redirected.
                }
            }
            return name;
        }

        private TriggerableController findTriggerableController(EntityActionSet actionSet) {
            for (var entry : triggerableControllers) {
                if (entry.isIdle || entry.test(actionSet)) {
                    return entry;
                }
            }
            return null;
        }

        private TriggerableController findTriggerableController(AnimationController animationController) {
            for (var entry : triggerableControllers) {
                if (entry.animationController == animationController) {
                    return entry;
                }
            }
            return null;
        }

        private void rebuildTriggerableControllers() {
            var newValues = new ArrayList<TriggerableController>();
            for (var animationController : animationControllers) {
                if (!animationController.isParallel()) {
                    var name = resolveMappingName(animationController.name());
                    var controller = new TriggerableController(name, animationController);
                    newValues.add(controller);
                }
            }
            newValues.sort(Comparator.comparingDouble(TriggerableController::priority).reversed());
            triggerableControllers.clear();
            triggerableControllers.addAll(newValues);
            if (playing == null) {
                return;
            }
            playing = findTriggerableController(playing.animationController);
        }
    }

    protected static class Properties implements IDataSerializable.Immutable {

        public static final IDataSerializerKey<Float> SPEED = IDataSerializerKey.create("speed", IDataCodec.FLOAT, 1.0f);
        public static final IDataSerializerKey<Integer> REPEAT = IDataSerializerKey.create("repeat", IDataCodec.INT, 0);
        public static final IDataSerializerKey<Boolean> LOCK = IDataSerializerKey.create("lock", IDataCodec.BOOL, true);

        public static final IDataCodec<Properties> CODEC = ExtraCodecs.serializable(Properties::new);

        private final float speed;
        private final int playCount;
        private final boolean needsLock;

        public Properties(IDataSerializer serializer) {
            this.speed = OpenMath.clamp(serializer.read(SPEED), 0.0001f, 1000.0f);
            this.playCount = OpenMath.clamp(serializer.read(REPEAT), -1, 1000);
            this.needsLock = serializer.read(LOCK);
        }

        @Override
        public void serialize(IDataSerializer serializer) {
            serializer.write(SPEED, speed);
            serializer.write(REPEAT, playCount);
            serializer.write(LOCK, needsLock);
        }
    }

    protected static class TriggerableController {

        private final String name;
        private final EntityActionTarget target;
        private final AnimationController animationController;
        private final boolean isIdle;

        public TriggerableController(String name, AnimationController animationController) {
            this.name = name;
            this.target = EntityActions.by(name);
            this.animationController = animationController;
            this.isIdle = target.actions().contains(EntityAction.IDLE);
        }

        public boolean test(EntityActionSet actionSet) {
            int hit = 0;
            for (var action : target.actions()) {
                if (!actionSet.contains(action)) {
                    return false;
                }
                hit += 1;
            }
            return hit != 0;
        }

        public String name() {
            return name;
        }

        public double priority() {
            return target.priority();
        }

        public double transitionDuration() {
            return target.transitionDuration();
        }

        public int playCount() {
            return target.playCount();
        }

        @Override
        public String toString() {
            return animationController.toString();
        }
    }

    protected class PlayAction {

        private final double time;
        private final String name;
        private final CompoundTag tag;

        public PlayAction(String name, double time, CompoundTag tag) {
            this.name = name;
            this.time = time;
            this.tag = tag;
        }

        public void resume(Entry entry, AnimationController animationController) {
            // check it still playing.
            if (animationController.loop() == SkinAnimationLoop.NONE) {
                var endTime = time + animationController.duration();
                if (endTime < lastAnimationTick) {
                    return; // can't play
                }
            }
            if (ModConfig.Client.enableAnimationDebug) {
                ModLog.debug("resume animation {}", name);
            }
            entry.play(animationController, time, tag);
        }
    }
}
