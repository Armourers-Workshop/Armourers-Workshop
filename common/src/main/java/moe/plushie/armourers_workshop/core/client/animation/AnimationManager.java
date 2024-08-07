package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.other.BlockEntityRenderData;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.data.EntityAction;
import moe.plushie.armourers_workshop.core.data.EntityActionSet;
import moe.plushie.armourers_workshop.core.data.EntityActionTarget;
import moe.plushie.armourers_workshop.core.data.EntityActions;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.TickUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class AnimationManager {

    public static final AnimationManager NONE = new AnimationManager();

    private final HashMap<BakedSkin, Entry> allEntries = new HashMap<>();
    private final HashMap<BakedSkin, Entry> activeEntries = new HashMap<>();

    private final ArrayList<Entry> triggerableEntries = new ArrayList<>();

    private final ArrayList<Pair<AnimationController.PlayState, Runnable>> removeOnCompletion = new ArrayList<>();

    private EntityActionSet lastActionSet;

    public static AnimationManager of(Entity entity) {
        var renderData = EntityRenderData.of(entity);
        if (renderData != null) {
            return renderData.getAnimationManager();
        }
        return null;
    }

    public static AnimationManager of(BlockEntity blockEntity) {
        var renderData = BlockEntityRenderData.of(blockEntity);
        if (renderData != null) {
            return renderData.getAnimationManager();
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
            entry.autoplay();
        });
        expiredEntries.forEach((key, entry) -> {
            activeEntries.remove(key);
            entry.stop();
        });
    }

    public void tick(Object source, float animationTicks) {
        // clear invalid animation.
        if (!removeOnCompletion.isEmpty()) {
            var iterator = removeOnCompletion.iterator();
            while (iterator.hasNext()) {
                var entry = iterator.next();
                var state = entry.getKey();
                state.tick(animationTicks);
                if (state.isCompleted()) {
                    iterator.remove();
                    entry.getRight().run();
                }
            }
        }
        // play triggerable animation by the state.
        if (!triggerableEntries.isEmpty() && source instanceof Entity entity) {
            var actionSet = entity.getActionSet();
            if (actionSet != null && !actionSet.equals(lastActionSet)) {
                debugLog("{} action did change: {}", entity, actionSet);
                triggerableEntries.forEach(entry -> entry.autoplay(actionSet, animationTicks));
                lastActionSet = actionSet.copy();
            }
        }
    }

    public void play(String name, float atTime, int playCount) {
        for (var entry : activeEntries.values()) {
            for (var animationController : entry.getAnimationControllers()) {
                if (name.equals(animationController.getName())) {
                    entry.play(animationController, atTime, playCount);
                }
            }
        }
    }

    public void stop(String name) {
        for (var entry : activeEntries.values()) {
            for (var animationController : entry.getAnimationControllers()) {
                if (name.isEmpty() || name.equals(animationController.getName())) {
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

    public void setChanged() {
        lastActionSet = null;
    }

    public AnimationContext getAnimationContext(BakedSkin skin) {
        var entry = allEntries.get(skin);
        if (entry != null) {
            return entry;
        }
        return skin.getAnimationContext();
    }


    private void rebuildTriggerableEntities() {
        triggerableEntries.clear();
        triggerableEntries.addAll(ObjectUtils.filter(allEntries.values(), Entry::hasTriggerableAnimation));
    }

    protected void debugLog(String message, Object... arguments) {
        if (ModConfig.Client.enableAnimationDebug) {
            ModLog.debug(message, arguments);
        }
    }

    public class Entry extends AnimationContext {

        protected final List<AnimationController> animationControllers;
        protected final List<TriggerableController> triggerableControllers = new ArrayList<>();

        protected final HashMap<String, String> actionToName = new HashMap<>();

        protected TriggerableController playing;
        protected boolean isLocking;

        public Entry(BakedSkin skin) {
            super(skin.getAnimationContext());
            this.animationControllers = skin.getAnimationControllers();
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
                startPlay(it, TickUtils.animationTicks(), 0);
            });
        }

        public void autoplay(EntityActionSet actionSet, float atTime) {
            // If it's already locked, we won't switch.
            if (isLocking && playing != null) {
                return;
            }
            var newValue = findTriggerableController(actionSet);
            var oldValue = playing;
            if (oldValue == newValue) {
                return; // ignore when no any changes.
            }
            if (oldValue != null) {
                stopPlayIfNeeded(oldValue.animationController);
            }
            isLocking = false;
            playing = newValue;
            if (newValue != null) {
                startPlay(newValue.animationController, atTime, newValue.getPlayCount());
            }
            applyTransiting(oldValue, newValue, atTime);
        }

        public void play(AnimationController animationController, float atTime, int playCount) {
            // play parallel animation (simple).
            if (animationController.isParallel()) {
                startPlay(animationController, atTime, playCount);
                return;
            }
            // play triggerable animation (lock).
            var newValue = findTriggerableController(animationController);
            var oldValue = playing;
            if (newValue == null || newValue == oldValue) {
                return; // ignore when no any changes.
            }
            if (oldValue != null) {
                stopPlayIfNeeded(oldValue.animationController);
            }
            isLocking = true;
            playing = newValue;
            startPlay(newValue.animationController, atTime, playCount);
            applyTransiting(oldValue, newValue, atTime);
        }

        public void stop(AnimationController animationController) {
            var playState = playStates.get(animationController);
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

        public List<AnimationController> getAnimationControllers() {
            return animationControllers;
        }

        public boolean hasTriggerableAnimation() {
            return !triggerableControllers.isEmpty();
        }

        private void startPlay(AnimationController animationController, float atTime, int playCount) {
            stopPlayIfNeeded(animationController);
            var newPlayState = new AnimationController.PlayState(animationController, atTime, playCount);
            playStates.put(animationController, newPlayState);
            debugLog("start play {}", animationController);
            if (newPlayState.getPlayCount() > 0) {
                removeOnCompletion.add(Pair.of(newPlayState, () -> stop(animationController)));
            }
        }

        private void stopPlayIfNeeded(AnimationController animationController) {
            var oldPlayState = playStates.remove(animationController);
            if (oldPlayState != null) {
                debugLog("stop play {}", animationController);
                removeOnCompletion.removeIf(it -> it.getLeft() == oldPlayState);
            }
        }

        private void applyTransiting(TriggerableController from, TriggerableController to, float atTime) {
            ModLog.debug("start transiting: {} => {}", from, to);
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

        private TriggerableController findTriggerableController(EntityActionSet tracker) {
            for (var entry : triggerableControllers) {
                if (entry.isIdle || entry.test(tracker)) {
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
                    var name = resolveMappingName(animationController.getName());
                    var controller = new TriggerableController(name, animationController);
                    newValues.add(controller);
                }
            }
            newValues.sort(Comparator.comparingDouble(TriggerableController::getPriority).reversed());
            triggerableControllers.clear();
            triggerableControllers.addAll(newValues);
            if (playing == null) {
                return;
            }
            playing = findTriggerableController(playing.animationController);
        }
    }

    public static class TriggerableController {

        private final String name;
        private final EntityActionTarget target;
        private final AnimationController animationController;
        private final boolean isIdle;

        public TriggerableController(String name, AnimationController animationController) {
            this.name = name;
            this.target = EntityActions.by(name);
            this.animationController = animationController;
            this.isIdle = target.getActions().contains(EntityAction.IDLE);
        }

        public boolean test(EntityActionSet actionSet) {
            int hit = 0;
            for (var action : target.getActions()) {
                if (!actionSet.contains(action)) {
                    return false;
                }
                hit += 1;
            }
            return hit != 0;
        }

        public String getName() {
            return name;
        }

        public double getPriority() {
            return target.getPriority();
        }

        public int getPlayCount() {
            return target.getPlayCount();
        }

        @Override
        public String toString() {
            return animationController.toString();
        }
    }
}
