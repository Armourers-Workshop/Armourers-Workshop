package moe.plushie.armourers_workshop.core.skin.animation.runtime;

import moe.plushie.armourers_workshop.api.core.IDataCodec;
import moe.plushie.armourers_workshop.api.core.IDataSerializable;
import moe.plushie.armourers_workshop.api.core.IDataSerializer;
import moe.plushie.armourers_workshop.api.core.IDataSerializerKey;
import moe.plushie.armourers_workshop.core.data.action.EntityAction;
import moe.plushie.armourers_workshop.core.data.action.EntityActionSet;
import moe.plushie.armourers_workshop.core.data.action.EntityActionTarget;
import moe.plushie.armourers_workshop.core.data.action.EntityActions;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimation;
import moe.plushie.armourers_workshop.core.skin.animation.core.SkinAnimationEffect;
import moe.plushie.armourers_workshop.core.skin.animation.core.SkinAnimationPose;
import moe.plushie.armourers_workshop.core.skin.animation.core.SkinAnimationTransform;
import moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind.ExecutionContextImpl;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.ExtraCodecs;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.ScheduledExpression;
import moe.plushie.armourers_workshop.core.utils.TagSerializer;
import moe.plushie.armourers_workshop.core.utils.TickUtils;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModLog;
import net.minecraft.nbt.CompoundTag;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public abstract class SkinAnimationManager<K, V> {

    protected final Map<V, Item> allItems = new HashMap<>();
    protected final Map<V, Item> activeItems = new HashMap<>();

    protected final Map<String, Action> activeActions = new HashMap<>();

    protected final List<Item> triggerableItems = new ArrayList<>();


    /**
     * Loads skins into the animation player when they are equipped into the wardrobe.
     * At this stage the skins are registered for animation, but they are not necessarily in active use yet.
     */
    public void load(Map<? extends K, ? extends V> values) {
        var expiredItems = new HashMap<>(allItems);
        values.forEach((key, value) -> {
            expiredItems.remove(value);
            allItems.computeIfAbsent(value, this::createItem);
        });
        expiredItems.forEach((key, item) -> {
            allItems.remove(key);
            item.stopAll();
        });
    }

    /**
     * Marks loaded skins as active when they are actually being used.
     * For example, a weapon or shield skin becomes active while the entity is holding it.
     */
    public void active(Map<? extends K, ? extends V> values) {
        var expiredItems = new HashMap<>(activeItems);
        values.forEach((key, value) -> {
            var removed = expiredItems.remove(value);
            if (removed != null) {
                return; // no change, ignore.
            }
            var item = allItems.get(value);
            if (item == null) {
                return; // no found, ignore.
            }
            item.resume(TickUtils.animationTick());
            activeItems.put(value, item);
            activeActions.forEach((name, action) -> item.play(name, action.time(), action.tag()));
        });
        expiredItems.forEach((key, item) -> {
            item.animations().forEach(it -> activeActions.remove(it.name()));
            activeItems.remove(key);
            item.stopAll();
        });
        rebuildTriggerableItems();
    }

    /**
     * Maps an existing animation action identifier to a new identifier across all items.
     *
     * @param from the original action identifier to be remapped
     * @param to   the new action identifier to map the original identifier to
     */
    public void map(String from, String to) {
        allItems.forEach((value, item) -> item.map(from, to));
        rebuildTriggerableItems();
    }

    /**
     * Initiates the playback of an animation associated with the given name at the specified time,
     * using the provided tag for additional contextual data. This method also propagates the play
     * call to all active items.
     *
     * @param name   the name of the animation to be played
     * @param atTime the timestamp, in seconds, at which the animation should be triggered
     * @param tag    a {@code CompoundTag} containing additional data or parameters for the animation
     */
    public void play(String name, double atTime, CompoundTag tag) {
        var action = new Action(name, atTime, tag);
        activeActions.put(name, action);
        activeItems.forEach((value, item) -> item.play(name, atTime, tag));
    }

    /**
     * Stops the animation associated with the specified name. If the name is empty,
     * all active actions and animations are stopped. The stop operation is propagated
     * to all active items.
     *
     * @param name the name of the animation to stop. If empty, all animations and actions
     *             will be cleared.
     */
    public void stop(String name) {
        activeItems.forEach((value, item) -> item.stop(name));
        if (name.isEmpty()) {
            activeActions.clear();
        } else {
            activeActions.remove(name);
        }
    }

    public void tick(@Nullable Object source, double animationTick) {
        // tick the animation and the remove invalid animation state.
        if (!activeItems.isEmpty()) {
            activeItems.forEach((value, item) -> item.tick(animationTick));
        }
        // play triggerable animation by the entity state.
        if (!triggerableItems.isEmpty()) {
            var entityState = extractState(source);
            triggerableItems.forEach(it -> it.play(entityState, animationTick));
        }
    }

    public void process(V value, double animationTick, float partialTick) {
        var item = allItems.get(value);
        if (item != null) {
            item.process(animationTick, partialTick);
        }
    }

    /**
     * Creates a new {@code Item} instance based on the provided value. This method is intended
     * to be implemented by subclasses to define how items are generated or constructed.
     *
     * @param value the input parameter used to generate or construct the item
     * @return the newly created {@code Item} instance based on the provided value
     */
    protected abstract Item createItem(V value);

    /**
     * Extracts the current entity state for the specified source object and returns
     * an {@code EntityActionSet}. This method is intended to be implemented by subclasses
     * to determine the set of actions based on the specific state of the provided source object.
     *
     * @param source the source object whose animation state is to be extracted
     * @return an {@code EntityActionSet} representing the extracted state of the source object
     */
    protected abstract EntityActionSet extractState(Object source);


    private void rebuildTriggerableItems() {
        triggerableItems.clear();
        triggerableItems.addAll(Collections.filter(activeItems.values(), Item::isTriggerable));
    }

    public static class Item {

        private final int id;
        private final List<SkinAnimation> animations;

        private final NameResolver resolver;
        private final ExecutionContextImpl context;

        private final BoneHandler boneHandler;

        private final Map<SkinAnimation, State> lastStates = new HashMap<>();

        private final List<Pair<State, Runnable>> removeOnCompletion = new ArrayList<>();

        private boolean isLocking = false;
        private boolean isFirstTransitionAnimation = true;

        private Controller playing;
        private EntityActionSet lastActionSet;

        public Item(int id, List<SkinAnimation> animations, ExecutionContextImpl context) {
            this.id = id;
            this.animations = animations;

            this.context = context;
            this.resolver = new NameResolver(animations);

            this.boneHandler = new BoneHandler(animations);
        }

        protected void tick(double animationTick) {
            if (removeOnCompletion.isEmpty()) {
                return;
            }
            var iterator = removeOnCompletion.iterator();
            while (iterator.hasNext()) {
                var entry = iterator.next();
                var state = entry.getKey();
                state.tick(animationTick);
                if (state.isEnded()) {
                    iterator.remove();
                    entry.getValue().run();
                }
            }
        }

        protected void process(double animationTick, float partialTick) {
            beginUpdates(animationTick);
            for (var animation : animations) {
                // query the current play state of the animation controller.
                var state = lastStates.get(animation);
                if (state == null) {
                    continue;
                }

                // we only upload it when animation requires the molang environment.
                var adjustedTime = state.adjustedTime(animationTick);
                if (animation.isRequiresVirtualMachine()) {
                    context.upload(id, state.time(), adjustedTime, animationTick, partialTick);
                }

                // check/switch frames of animation and write to applier.
                animation.process(adjustedTime, context);

                // apply the effects by the state.
                for (var effect : animation.affectedEffects()) {
                    process(effect, animationTick, partialTick, state);
                }
            }
            commitUpdates();
        }

        protected void process(SkinAnimationEffect effect, double animationTick, float partialTick, State state) {
            var channels = Collections.filter(SkinAnimation.Channel.values(), SkinAnimation.Channel::isEffect);
            for (var channel : channels) {
                var expr = effect.process(channel);
                var holder = state.get(channel);
                holder.apply(expr, context);
            }
        }

        protected void resume(double atTime) {
            animations.stream().filter(SkinAnimation::isParallel).forEach(it -> {
                // autoplay the parallel animation.
                startPlay(it, atTime, 1, 0);
            });
        }

        protected void map(String from, String to) {
            resolver.map(from, to);
            // keep the entry object.
            if (playing != null) {
                playing = resolver.find(playing.animation);
            }
        }

        protected void play(EntityActionSet actionSet, double atTime) {
            // If it's already locked, we won't switch.
            if (isLocking && playing != null) {
                return;
            }
            // the action is changed?
            if (actionSet.equals(lastActionSet)) {
                return;
            }
            var newValue = resolver.resolve(actionSet);
            if (newValue != null && newValue != playing) {
                startPlay(newValue, playing, atTime, 1, newValue.playCount(), false);
            }
            lastActionSet = actionSet.copy();
        }

        protected void play(String name, double atTime, CompoundTag tag) {
            for (var animation : animations) {
                if (name.equals(animation.name())) {
                    addAnimation(animation, atTime, new Properties(new TagSerializer(tag)));
                }
            }
        }

        protected void stop(String name) {
            for (var animation : animations) {
                if (name.isEmpty() || name.equals(animation.name())) {
                    removeAnimation(animation);
                }
            }
        }

        protected void stopAll() {
            animations.forEach(this::removeAnimation);
        }

        protected void beginUpdates(double animationTick) {
            boneHandler.begin(animationTick);
        }

        protected void commitUpdates() {
            boneHandler.commit(this);
        }

        public boolean isTriggerable() {
            return !resolver.entries.isEmpty();
        }

        public List<SkinAnimation> animations() {
            return animations;
        }

        protected void addAnimation(SkinAnimation animation, double atTime, Properties properties) {
            // play parallel animation (simple).
            if (animation.isParallel()) {
                startPlay(animation, atTime, properties.speed, properties.playCount);
                return;
            }
            // play triggerable animation (lock).
            var newValue = resolver.find(animation);
            if (newValue != null && newValue != playing) {
                startPlay(newValue, playing, atTime, properties.speed, properties.playCount, properties.needsLock);
            }
        }

        protected void removeAnimation(SkinAnimation animation) {
            var state = lastStates.get(animation);
            if (state == null) {
                return; // ignore non-playing animation.
            }
            // stop parallel animation (simple).
            if (animation.isParallel()) {
                stopPlay(animation);
                return;
            }
            // ignore, when not found.
            var oldValue = playing;
            if (oldValue == null || oldValue.animation != animation) {
                return;
            }
            playing = null;
            isLocking = false;
            lastActionSet = null;
            stopPlay(animation);
        }

        private void startPlay(SkinAnimation animation, double time, double speed, int playCount) {
            stopPlay(animation);
            var newState = State.create(animation, time, playCount, speed);
            lastStates.put(animation, newState);
            if (ModConfig.Client.enableAnimationDebug) {
                ModLog.debug("start play {}", animation);
            }
            if (newState.loopCount() > 0) {
                removeOnCompletion.add(Pair.of(newState, () -> removeAnimation(animation)));
            }
        }

        private void startPlay(Controller newValue, @Nullable Controller oldValue, double time, double speed, int playCount, boolean needLock) {
            var toAnimation = newValue.animation;
            var fromAnimation = Objects.flatMap(oldValue, it -> it.animation);

            // clear prev play state.
            if (fromAnimation != null) {
                stopPlay(fromAnimation);
            }

            // set the next play state.
            startPlay(toAnimation, time, speed, playCount);

            isLocking = needLock;
            playing = newValue;

            // TODO: @SAGESSE Add transition duration support.
            var duration = newValue.transitionDuration();
            addTransitingAnimation(fromAnimation, toAnimation, time, speed, duration);
        }

        private void stopPlay(SkinAnimation animation) {
            var oldState = lastStates.remove(animation);
            if (oldState == null) {
                return;
            }
            if (ModConfig.Client.enableAnimationDebug) {
                ModLog.debug("stop play {}", animation);
            }
            oldState.reset();
            removeOnCompletion.removeIf(it -> it.getLeft() == oldState);
        }

        private void addTransitingAnimation(SkinAnimation fromAnimation, SkinAnimation toAnimation, double time, double speed, double duration) {
            // we need to ignore the first transition animation, because have some very strange effects.
            if (isFirstTransitionAnimation) {
                isFirstTransitionAnimation = false;
                return;
            }
            // delay the animation start time.
            var playState = lastStates.get(toAnimation);
            if (playState != null) {
                playState.setTime(playState.time() + duration);
            }
            boneHandler.apply(fromAnimation, toAnimation, time, speed, duration);
        }

        private static class Controller {

            private final String name;
            private final EntityActionTarget target;
            private final SkinAnimation animation;
            private final boolean isIdle;

            public Controller(String name, SkinAnimation animation) {
                this.name = name;
                this.target = EntityActions.by(name);
                this.animation = animation;
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
                return animation.toString();
            }
        }

        private static class NameResolver {

            private final Map<String, String> names = new HashMap<>();

            private final List<Controller> entries;
            private final List<SkinAnimation> animations;

            public NameResolver(List<SkinAnimation> animations) {
                this.entries = new ArrayList<>();
                this.animations = Collections.filter(animations, it -> !it.isParallel());
                this.rebuild();
            }

            public Controller find(SkinAnimation animation) {
                for (var entry : entries) {
                    if (entry.animation == animation) {
                        return entry;
                    }
                }
                return null;
            }

            public Controller resolve(EntityActionSet actionSet) {
                for (var entry : entries) {
                    if (entry.isIdle || entry.test(actionSet)) {
                        return entry;
                    }
                }
                return null;
            }

            public void map(String from, String to) {
                if (from.equals(to) || to.isEmpty()) {
                    names.remove(from);
                } else {
                    names.put(from, to);
                }
                rebuild();
            }

            private String apply(String name) {
                // map idle sit/idle
                for (var entry : names.entrySet()) {
                    if (entry.getValue().equals(name)) {
                        return entry.getKey(); // name to action.
                    }
                    if (entry.getKey().equals(name)) {
                        return "redirected:" + name;  // name is action, but it was redirected.
                    }
                }
                return name;
            }

            private void rebuild() {
                entries.clear();
                entries.addAll(Collections.compactMap(animations, it -> new Controller(apply(it.name()), it)));
                entries.sort(Comparator.comparingDouble(Controller::priority).reversed());
            }
        }

        private static class BoneHandler {

            private final List<Snapshot> snapshots;

            BoneHandler(List<SkinAnimation> animations) {
                this.snapshots = this.generate(animations);
            }

            private List<Snapshot> generate(List<SkinAnimation> animations) {
                var results = new ArrayList<Snapshot>();
                var transforms = new LinkedHashSet<>(Collections.flatMap(animations, SkinAnimation::affectedTransforms));
                for (var transform : transforms) {
                    results.add(new Snapshot(transform));
                }
                return results;
            }

            public void begin(double animationTick) {
                for (var snapshot : snapshots) {
                    snapshot.beginUpdates(animationTick);
                }
            }

            public void commit(Item item) {
                for (var snapshot : snapshots) {
                    snapshot.commitUpdates();
                }
            }

            public void apply(SkinAnimation fromAnimation, SkinAnimation toAnimation, double time, double speed, double duration) {
                // Find affected transform by from/to animation.
                var affectedTransforms = new ArrayList<SkinAnimationTransform>();
                affectedTransforms.addAll(Objects.flatMap(fromAnimation, SkinAnimation::affectedTransforms, Collections.emptyList()));
                affectedTransforms.addAll(Objects.flatMap(toAnimation, SkinAnimation::affectedTransforms, Collections.emptyList()));
                for (var snapshot : snapshots) {
                    if (affectedTransforms.contains(snapshot.transform)) {
                        var transiting = new Transiting(time, duration);
                        snapshot.addTransitingAnimation(transiting);
                    }
                }
            }

            private static class Snapshot {

                protected final SkinAnimationPose currentValue = new SkinAnimationPose();

                protected final SkinAnimationTransform transform;

                protected Transiting transitingAnimation;

                protected boolean isExported = false;

                public Snapshot(SkinAnimationTransform transform) {
                    this.transform = transform;
                }

                public void beginUpdates(double animationTick) {
                    // set the snapshot to null, the transform will skip calculations.
                    transform.set(null);
                    transform.clear();
                    //
                    if (transitingAnimation != null) {
                        transitingAnimation.update(animationTick);
                        if (transitingAnimation.isCompleted()) {
                            transitingAnimation = null;
                        }
                    }
                }

                public void commitUpdates() {
                    // when no transiting or no change, we will need skip calculate.
                    if (transitingAnimation == null && !transform.isDirty()) {
                        isExported = false;
                        return; // keep snapshot is null.
                    }
                    isExported = true;
                    transform.export(currentValue);
                    transform.set(currentValue);
                    // when the snapshot is transiting, we're mix tow snapshot calculation.
                    if (transitingAnimation != null) {
                        transitingAnimation.apply(currentValue);
                        transform.set(transitingAnimation.outputValue());
                    }
                }

                protected void addTransitingAnimation(Transiting transiting) {
                    transitingAnimation = transiting;
                    var snapshotValue = transitingAnimation.snapshotValue();
                    if (isExported) {
                        snapshotValue.setTranslation(currentValue.translation());
                        snapshotValue.setRotation(currentValue.rotation());
                        snapshotValue.setScale(currentValue.scale());
                    } else {
                        var original = transform.parent();
                        snapshotValue.setTranslation(original.translate());
                        snapshotValue.setRotation(original.rotation());
                        snapshotValue.setScale(original.scale());
                    }
                }

                @Override
                public String toString() {
                    return Objects.toString(this);
                }
            }

            private static class Transiting {

                private final SkinAnimationPose snapshotValue = new SkinAnimationPose();
                private final SkinAnimationPose outputValue = new SkinAnimationPose();

                private final double beginTime;
                private final double endTime;
                private final double duration;

                private float progress;
                private boolean isCompleted;

                public Transiting(double time, double duration) {
                    this.beginTime = time;
                    this.endTime = time + duration;
                    this.duration = duration;
                }

                public void update(double time) {
                    this.progress = (float) OpenMath.clamp((time - beginTime) / duration, 0.0, 1.0);
                    this.isCompleted = time > endTime;
                }

                public void apply(SkinAnimationPose currentValue) {
                    var lt = snapshotValue.translation();
                    var lr = snapshotValue.rotation();
                    var ls = snapshotValue.scale();
                    var rt = currentValue.translation();
                    var rr = currentValue.rotation();
                    var rs = currentValue.scale();
                    var tx = OpenMath.lerp(progress, lt.x(), rt.x());
                    var ty = OpenMath.lerp(progress, lt.y(), rt.y());
                    var tz = OpenMath.lerp(progress, lt.z(), rt.z());
                    var sx = OpenMath.lerp(progress, ls.x(), rs.x());
                    var sy = OpenMath.lerp(progress, ls.y(), rs.y());
                    var sz = OpenMath.lerp(progress, ls.z(), rs.z());
                    var rx = OpenMath.lerp(progress, lr.x(), rr.x());
                    var ry = OpenMath.lerp(progress, lr.y(), rr.y());
                    var rz = OpenMath.lerp(progress, lr.z(), rr.z());
                    outputValue.setTranslation(tx, ty, tz);
                    outputValue.setScale(sx, sy, sz);
                    outputValue.setRotation(rx, ry, rz);
                }

                public SkinAnimationPose snapshotValue() {
                    return snapshotValue;
                }

                public SkinAnimationPose outputValue() {
                    return outputValue;
                }

                public float progress() {
                    return progress;
                }

                public boolean isCompleted() {
                    return isCompleted;
                }
            }
        }
    }

    public static class State {

        protected double time = 0.0;
        protected double duration = 0.0;

        protected int loopCount = 0;

        protected double adjustedTime = 0.0;
        protected boolean isEnded = false;

        private final Map<Object, Holder> holders = new HashMap<>();

        public static State create(SkinAnimation animation, double time, int loopCount, double speed) {
            // ..
            if (loopCount == 0) {
                loopCount = switch (animation.loop()) {
                    case NONE -> 1;
                    case LAST_FRAME -> 0;
                    case LOOP -> -1;
                };
            }
            var playState = createVariant(speed, animation.duration());
            playState.setTime(time);
            playState.setDuration(animation.duration());
            playState.setLoopCount(loopCount);
            return playState;
        }

        private static State createVariant(double speed, double duration) {
            // if speed or duration is zero, this means no need adjusted time.
            if (Double.compare(speed * duration, 0.0) == 0) {
                return new None();
            }
            // If speed is one, this means no need speed adjust, we will use better performing version.
            if (Double.compare(speed, 1.0) == 0) {
                return new Normal();
            }
            return new Modulate(speed);
        }

        protected boolean walk(double animationTime) {
            return false;
        }

        protected void reset() {
            holders.values().forEach(Holder::clean);
        }

        public void tick(double animationTime) {
            // when loop count is 0 (keep last frame), we never call reset again.
            var working = walk(animationTime);
            if (working || loopCount == 0) {
                return;
            }
            // when the loop counts > 0, we will reduce it until to 0, and then keep last frame.
            // when the loop counts < 0, we never reduce it, because it is infinite.
            if (loopCount > 0) {
                loopCount -= 1;
            }
            // when loop count is manually reduced to 0, it means the animation complete.
            if (loopCount == 0) {
                isEnded = true;
            }
            reset();
        }

        public double adjustedTime(double animationTime) {
            // this is a future animation?
            if (animationTime < time) {
                return 0;
            }
            tick(animationTime);
            return adjustedTime;
        }


        public void setTime(double time) {
            this.time = time;
        }

        public double time() {
            return time;
        }

        public double adjustedTime() {
            return adjustedTime;
        }

        public void setDuration(double duration) {
            this.duration = duration;
        }

        public double duration() {
            return duration;
        }

        public void setLoopCount(int playCount) {
            this.loopCount = playCount;
        }

        public int loopCount() {
            return loopCount;
        }

        public boolean isEnded() {
            return isEnded;
        }

        @Override
        public String toString() {
            return Objects.toString(this, "time", time, "duration", duration, "loop", loopCount, "ended", isEnded);
        }

        private Holder get(Object key) {
            return holders.computeIfAbsent(key, Holder::new);
        }

        private static class None extends State {
        }

        private static class Normal extends State {

            private double lastResetTime = 0.0;

            @Override
            protected boolean walk(double animationTime) {
                adjustedTime = animationTime - lastResetTime;
                return adjustedTime < duration;
            }

            @Override
            protected void reset() {
                super.reset();
                if (loopCount != 0) {
                    lastResetTime += duration;
                }
            }

            @Override
            public void setTime(double time) {
                this.time = time;
                this.lastResetTime = time;
            }
        }

        private static class Modulate extends State {

            private double lastResetTime = 0.0;
            private double adjustedDuration = 0.0;

            private final double speed;

            public Modulate(double speed) {
                this.speed = speed;
            }

            @Override
            protected boolean walk(double animationTime) {
                // convert time to progress and then remap to duration.
                var progress = (animationTime - lastResetTime) / adjustedDuration;
                adjustedTime = progress * duration;
                return progress < 1.0;
            }

            @Override
            protected void reset() {
                super.reset();
                if (loopCount != 0) {
                    lastResetTime += adjustedDuration;
                }
            }

            @Override
            public void setTime(double time) {
                this.time = time;
                this.lastResetTime = time;
            }

            @Override
            public void setDuration(double duration) {
                this.duration = duration;
                this.adjustedDuration = Math.max(duration / speed, 0.00001);
            }
        }

        private static class Holder {

            protected Runnable cleaner;
            protected ScheduledExpression<?> owner;

            public Holder(Object key) {
                // nop
            }

            public <T> void apply(ScheduledExpression<T> expr, ExecutionContextImpl context) {
                // the expression have any changes?
                if (owner == expr) {
                    return;
                }
                this.clean();
                this.owner = expr;
                this.cleaner = create(expr, context);
            }

            public void clean() {
                // when the cleaner is null, it means no use effect.
                if (this.cleaner == null) {
                    return;
                }
                this.cleaner.run();
                this.owner = null;
                this.cleaner = null;
            }

            private <T> Runnable create(ScheduledExpression<T> expr, ExecutionContextImpl context) {
                // when the expression is null, it means no use effect.
                if (expr == null) {
                    return null;
                }
                var task = expr.submit(context);
                return () -> expr.cancel(task);
            }
        }
    }

    public static class Action {

        private final double time;
        private final String name;
        private final CompoundTag tag;

        public Action(String name, double time, CompoundTag tag) {
            this.name = name;
            this.time = time;
            this.tag = tag;
        }

        public String name() {
            return name;
        }

        public double time() {
            return time;
        }

        public CompoundTag tag() {
            return tag;
        }
    }

    public static class Properties implements IDataSerializable.Immutable {

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
}
