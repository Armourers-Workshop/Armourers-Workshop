package moe.plushie.armourers_workshop.core.capability;

import moe.plushie.armourers_workshop.core.menu.SkinSlotType;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenEquipmentSlot;
import moe.plushie.armourers_workshop.init.ModDataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * var wardrobe = entity.wardrobe;
 * var skin = wardrobe.loadSkin("ks:13776")
 * skin = wardrobe.loadSkinFromDB("ks:13776");
 * skin.addTo(newItemStack);
 * if (!wardrobe.isValid()) { return; }
 * newItemStack = wardrobe.getItem("outfit", 0);
 * skin = wardrobe.loadSkinByItem(newItemStack);
 * skin.removeFrom(newItemStack);
 * wardrobe.setItem(skin.type, 0, newItemStack);
 * wardrobe.clear();
 * wardrobe.enable("render.head");
 * wardrobe.enable("render.chest");
 * wardrobe.enable("render.legs");
 * wardrobe.enable("render.feet");
 * wardrobe.enable("render.extra");
 * wardrobe.setUnlockedSize("outfit", wardrobe.getUnlockedSize("outfit") + 1);
 * wardrobe.broadcast();
 */
@SuppressWarnings("unused")
public class SkinWardrobeJS {

    private static final Map<String, BiConsumer<SkinWardrobe, Boolean>> OPTIONS = Collections.immutableMap(it -> {
        it.put("render.head", (w, f) -> w.setRenderEquipment(OpenEquipmentSlot.HEAD, f));
        it.put("render.chest", (w, f) -> w.setRenderEquipment(OpenEquipmentSlot.CHEST, f));
        it.put("render.legs", (w, f) -> w.setRenderEquipment(OpenEquipmentSlot.LEGS, f));
        it.put("render.feet", (w, f) -> w.setRenderEquipment(OpenEquipmentSlot.FEET, f));
        it.put("render.extra", SkinWardrobe::setRenderExtra);
    });

    private final SkinWardrobe wardrobe;

    public SkinWardrobeJS(SkinWardrobe wardrobe) {
        this.wardrobe = wardrobe;
    }

    public SkinDescriptorJS loadSkin(String identifier) {
        var skin = SkinLoader.getInstance().loadSkin(identifier);
        if (skin != null) {
            return new SkinDescriptorJS(new SkinDescriptor(identifier, skin.type(), SkinPaintScheme.EMPTY));
        }
        return null;
    }

    public SkinDescriptorJS loadSkinFromDB(String identifier) {
        var descriptor = SkinLoader.getInstance().loadSkinFromDB(identifier, SkinPaintScheme.EMPTY, true);
        if (!descriptor.isEmpty()) {
            return new SkinDescriptorJS(descriptor);
        }
        return null;
    }

    public SkinDescriptorJS loadSkinByItem(ItemStack itemStack) {
        var descriptor = SkinDescriptor.of(itemStack);
        if (!descriptor.isEmpty()) {
            return new SkinDescriptorJS(descriptor);
        }
        return null;
    }

    public void setItem(String slotType, int slot, ItemStack itemStack) {
        wardrobe.setItem(SkinSlotType.byName(slotType), slot, itemStack);
    }

    public ItemStack getItem(String slotType, int slot) {
        return wardrobe.getItem(SkinSlotType.byName(slotType), slot);
    }

    public void clear() {
        wardrobe.clear();
    }

    public void setUnlockedSize(String slotType, int size) {
        wardrobe.setUnlockedSize(SkinSlotType.byName(slotType), size);
    }

    public int getUnlockedSize(String slotType) {
        return wardrobe.getUnlockedSize(SkinSlotType.byName(slotType));
    }

    public int getMaximumSize(String slotType) {
        return wardrobe.getMaximumSize(SkinSlotType.byName(slotType));
    }

    public int getFreeSize(String slotType) {
        return wardrobe.getFreeSlot(SkinSlotType.byName(slotType));
    }

    public void disable(String opt) {
        setOptions(opt, false);
    }

    public void enable(String opt) {
        setOptions(opt, true);
    }

    public void broadcast() {
        wardrobe.broadcast();
    }

    public boolean isValid() {
        return wardrobe != null;
    }

    public boolean isEditable(Player player) {
        return wardrobe != null && wardrobe.isEditable(player);
    }

    private void setOptions(String opt, boolean flag) {
        var o = OPTIONS.get(opt);
        if (o != null) {
            o.accept(wardrobe, flag);
        }
    }

    public static class SkinDescriptorJS {

        private final SkinDescriptor descriptor;

        public SkinDescriptorJS(SkinDescriptor descriptor) {
            this.descriptor = descriptor;
        }

        public void addTo(ItemStack itemStack) {
            itemStack.set(ModDataComponents.SKIN.get(), descriptor);
        }

        public void removeFrom(ItemStack itemStack) {
            itemStack.remove(ModDataComponents.SKIN.get());
        }

        public String getIdentifier() {
            return descriptor.identifier();
        }

        public String getType() {
            return descriptor.type().registryName().path();
        }

        public ItemStack asItemStack() {
            return descriptor.asItemStack();
        }
    }
}
