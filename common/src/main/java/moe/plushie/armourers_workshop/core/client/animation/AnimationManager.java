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
import moe.plushie.armourers_workshop.utils.TickUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class AnimationManager {

    private final HashMap<SkinDescriptor, Entry> entries = new HashMap<>();
    private final HashMap<AnimationController, AnimationState> states = new HashMap<>();

    private final HashMap<SkinDescriptor, Entry> activeEntries = new HashMap<>();

    private final ArrayList<Entry> triggerableEntries = new ArrayList<>();
    private final ArrayList<AnimationController> animations = new ArrayList<>();
    private final ArrayList<AnimationController> removeOnCompletion = new ArrayList<>();

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
        var oldEntries = new HashMap<>(entries);
        skins.forEach((key, skin) -> {
            oldEntries.remove(key);
            var entry = entries.computeIfAbsent(key, Entry::new);
            if (entry.isLoaded || skin == null) {
                return;
            }
            entry.isLoaded = true;
            entry.animationControllers.addAll(skin.getAnimationControllers());
            entry.rebuild();
            animations.addAll(entry.animationControllers);
        });
        oldEntries.forEach((key, entry) -> {
            entries.remove(key);
            activeEntries.remove(key);
            entry.animationControllers.forEach(this::stop);
            animations.removeAll(entry.animationControllers);
        });
        rebuildTriggerableEntities();
        setChanged();
    }

    public void parallelTick(Map<SkinDescriptor, BakedSkin> skins) {
        var oldEntries = new HashMap<>(activeEntries);
        skins.forEach((key, skin) -> {
            var entry = oldEntries.remove(key);
            if (entry != null) {
                return; // no change, ignore.
            }
            entry = entries.get(key);
            if (entry == null) {
                return; // no found, ignore.
            }
            activeEntries.put(key, entry);
            entry.animationControllers.stream().filter(AnimationController::isParallel).forEach(it -> {
                // autoplay the parallel animation.
                play(it, TickUtils.animationTicks(), 0);
            });
        });
        oldEntries.forEach((key, entry) -> {
            // when skin enter inactive state, stop all animations.
            activeEntries.remove(key);
            entry.animationControllers.forEach(this::stop);
        });
    }

    public void serialTick(Object source, float animationTicks) {
        // clear invalid animation.
        if (!removeOnCompletion.isEmpty()) {
            var animations = new ArrayList<>(removeOnCompletion);
            for (var animation : animations) {
                var state = states.get(animation);
                if (state != null && state.isCompleted(animationTicks)) {
                    stop(animation);
                }
            }
        }
        // play triggerable animation by the state.
        if (!triggerableEntries.isEmpty() && source instanceof Entity entity) {
            var actionSet = EntityActionSet.of(entity);
            if (actionSet != null) {
                actionSet.tick(entity);
                if (!actionSet.equals(lastActionSet)) {
                    if (ModConfig.Client.enableAnimationDebug) {
                        ModLog.debug("{} => {}", entity, actionSet);
                    }
                    play(actionSet, animationTicks);
                    lastActionSet = actionSet.copy();
                }
            }
        }
    }

    public void play(EntityActionSet tracker, float animationTicks) {
        // we allow each skin to perform a serial animation.
        for (var entry : triggerableEntries) {
            // If it's already locked, we won't switch.
            if (entry.isLocked && entry.selectedEntry != null) {
                continue;
            }
            var newValue = entry.findTriggerableEntry(tracker);
            var oldValue = entry.selectedEntry;
            if (oldValue != newValue) {
                if (oldValue != null) {
                    stop(oldValue.animationController);
                }
                entry.isLocked = false;
                entry.selectedEntry = newValue;
                if (newValue != null) {
                    play(newValue.animationController, animationTicks, newValue.getPlayCount());
                }
            }
        }
    }

    public void play(String name, float atTime, int playCount) {
        animations.forEach(animationController -> {
            if (!name.equals(animationController.getName())) {
                return;
            }
            if (animationController.isParallel()) {
                play(animationController, atTime, playCount);
                return;
            }
            entries.forEach((key, entry) -> {
                var entry1 = entry.findTriggerableEntry(animationController);
                if (entry1 != null && entry1 != entry.selectedEntry) {
                    if (entry.selectedEntry != null) {
                        stop(entry.selectedEntry.animationController);
                    }
                    entry.selectedEntry = entry1;
                    entry.isLocked = true;
                    play(animationController, atTime, playCount);
                }
            });
        });
    }

    public void stop(String name) {
        animations.forEach(animation -> {
            if (name.isEmpty() || name.equals(animation.getName())) {
                stop(animation);
            }
        });
    }

    public void play(AnimationController animationController, float atTime, int playCount) {
        var state = new AnimationState(animationController);
        if (playCount == 0) {
            playCount = switch (animationController.getLoop()) {
                case NONE -> 1;
                case LAST_FRAME -> 0;
                case LOOP -> -1;
            };
        }
        state.setStartTime(atTime);
        state.setPlayCount(playCount);
        states.put(animationController, state);
        if (ModConfig.Client.enableAnimationDebug) {
            ModLog.debug("start play {}", animationController);
        }
        // automatically remove on animation completion.
        if (playCount > 0) {
            removeOnCompletion.add(animationController);
        } else {
            removeOnCompletion.remove(animationController);
        }
    }

    public void stop(AnimationController animationController) {
        var state = states.remove(animationController);
        if (state == null) {
            return;
        }
        if (ModConfig.Client.enableAnimationDebug) {
            ModLog.debug("stop play {}", animationController);
        }
        removeOnCompletion.remove(animationController);
        entries.forEach((key, entry) -> {
            if (entry.selectedEntry != null && entry.selectedEntry.animationController == animationController) {
                entry.selectedEntry = null;
                setChanged();
            }
        });
    }

    public void rewrite(String from, String to) {
        entries.forEach((key, entry) -> {
            entry.addMapping(from, to);
            entry.rebuild();
        });
        rebuildTriggerableEntities();
        setChanged();
    }

    public void setChanged() {
        lastActionSet = null;
    }

    public AnimationState getAnimationState(AnimationController animationController) {
        return states.get(animationController);
    }

    private void rebuildTriggerableEntities() {
        triggerableEntries.clear();
        triggerableEntries.addAll(entries.values().stream().filter(Entry::hasTriggerableAnimation).toList());
    }

    protected static class Entry {

        private final SkinDescriptor descriptor;

        private boolean isLoaded = false;

        private final HashMap<String, String> actionToName = new HashMap<>();
        private final ArrayList<TriggerableEntry> triggerableAnimations = new ArrayList<>();

        private final List<AnimationController> animationControllers = new ArrayList<>();

        private boolean isLocked = false;
        private TriggerableEntry selectedEntry;

        protected Entry(SkinDescriptor descriptor) {
            this.descriptor = descriptor;
        }

        protected void addMapping(String action, String newName) {
            if (action.equals(newName) || newName.isBlank()) {
                actionToName.remove(action);
            } else {
                actionToName.put(action, newName);
            }
        }

        protected String resolve(String name) {
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

        protected void rebuild() {
            var newValues = new ArrayList<TriggerableEntry>();
            for (var animationController : animationControllers) {
                if (!animationController.isParallel()) {
                    var name = animationController.getName();
                    var entry = new TriggerableEntry(resolve(name), animationController);
                    newValues.add(entry);
                }
            }
            newValues.sort(Comparator.comparingDouble(TriggerableEntry::getPriority).reversed());
            triggerableAnimations.clear();
            triggerableAnimations.addAll(newValues);
            if (selectedEntry != null) {
                selectedEntry = findTriggerableEntry(selectedEntry.animationController);
                isLoaded = false;
            }
        }

        protected TriggerableEntry findTriggerableEntry(EntityActionSet tracker) {
            for (var entry : triggerableAnimations) {
                if (entry.isIdle || entry.test(tracker)) {
                    return entry;
                }
            }
            return null;
        }

        protected TriggerableEntry findTriggerableEntry(AnimationController animationController) {
            for (var entry : triggerableAnimations) {
                if (entry.animationController == animationController) {
                    return entry;
                }
            }
            return null;
        }

        protected boolean hasTriggerableAnimation() {
            return !triggerableAnimations.isEmpty();
        }
    }

    protected static class TriggerableEntry {

        private final String name;
        private final EntityActionTarget target;
        private final boolean isIdle;

        private AnimationController animationController;

        protected TriggerableEntry(String name, AnimationController animationController) {
            this.name = name;
            this.target = EntityActions.by(name);
            this.animationController = animationController;
            this.isIdle = target.getActions().contains(EntityAction.IDLE);
        }

        protected boolean test(EntityActionSet tracker) {
            int hit = 0;
            for (var action : target.getActions()) {
                if (!tracker.get(action)) {
                    return false;
                }
                hit += 1;
            }
            return hit != 0;
        }

        protected String getName() {
            return name;
        }

        protected double getPriority() {
            return target.getPriority();
        }

        protected int getPlayCount() {
            return target.getPlayCount();
        }
    }
}
