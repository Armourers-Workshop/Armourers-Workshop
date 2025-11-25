package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Scheduler;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class SkinContainerEvaluator {

    private final ArrayList<Consumer<List<Skin>>> handlers = new ArrayList<>();

    private final Map<Integer, ItemStack> lastSlots = new HashMap<>();
    private final Map<Integer, SkinDescriptor> lastSkinDescriptors = new HashMap<>();
    private final Map<String, Info> loadedSkins = new ConcurrentHashMap<>();

    private boolean updating;

    public SkinContainerEvaluator(SimpleContainer container) {
        container.addListener(this::containerDidChange);
    }

    public void reset() {
        lastSlots.clear();
        lastSkinDescriptors.clear();
        loadedSkins.clear();
    }

    public void beginUpdates() {
        this.updating = true;
    }

    public void endUpdates() {
        this.updating = false;
    }

    public void addListener(Consumer<List<Skin>> handler) {
        this.handlers.add(handler);
    }

    public void removeListener(Consumer<List<Skin>> handler) {
        this.handlers.remove(handler);
    }

    private void containerDidChange(Container container) {
        // when updating it means the restore the slot from storage, we don't need to recompute it
        if (updating) {
            return;
        }
        int changes = 0;
        int size = container.getContainerSize();
        for (int i = 0; i < size; i++) {
            var itemStack = container.getItem(i);
            if (itemStack == lastSlots.get(i)) {
                continue;
            }
            lastSlots.put(i, itemStack);
            lastSkinDescriptors.put(i, SkinDescriptor.of(itemStack));
            changes += 1;
        }
        if (changes == 0) {
            return; // no changes.
        }
        var newValue = new HashSet<>(lastSkinDescriptors.values());
        var oldValue = new HashSet<>(loadedSkins.keySet());
        newValue.removeIf(SkinDescriptor::isEmpty);
        if (newValue.isEmpty() && oldValue.isEmpty()) {
            // when all empty, we need to update immediately. because it not wait any task.
            infoDidChange();
            return;
        }
        newValue.forEach(it -> {
            // the skin request exists?
            var identifier = it.identifier();
            if (oldValue.remove(identifier)) {
                return;
            }
            // request a new skin, and then recompute.
            var info = new Info();
            loadedSkins.put(identifier, info);
            SkinLoader.getInstance().loadSkinInfo(identifier, (skin, exception) -> {
                info.update(skin, exception);
                Scheduler.SERVER.next(this::infoDidChange);
            });
        });
        oldValue.forEach(it -> {
            // remove a skin and recompute.
            loadedSkins.remove(it);
            Scheduler.SERVER.next(this::infoDidChange);
        });
    }

    private void infoDidChange() {
        var values = Collections.compactMap(loadedSkins.values(), it -> it.skin);
        handlers.forEach(it -> it.accept(values));
    }

    private static class Info {

        private Skin skin;

        public void update(Skin skin, Throwable exception) {
            this.skin = skin;
        }
    }
}
