package moe.plushie.armourers_workshop.core.capability;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.slot.SkinSlotType;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

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

    private static final ImmutableMap<String, BiConsumer<SkinWardrobe, Boolean>> OPTIONS = new ImmutableMap.Builder<String, BiConsumer<SkinWardrobe, Boolean>>()
            .put("render.head", (w, f) -> w.setRenderEquipment(EquipmentSlot.HEAD, f))
            .put("render.chest", (w, f) -> w.setRenderEquipment(EquipmentSlot.CHEST, f))
            .put("render.legs", (w, f) -> w.setRenderEquipment(EquipmentSlot.LEGS, f))
            .put("render.feet", (w, f) -> w.setRenderEquipment(EquipmentSlot.FEET, f))
            .put("render.extra", SkinWardrobe::setRenderExtra)
            .build();

    private final SkinWardrobe wardrobe;

    public SkinWardrobeJS(Entity entity) {
        this.wardrobe = SkinWardrobe.of(entity);
    }

    public SkinDescriptorJS loadSkin(String identifier) {
        Skin skin = SkinLoader.getInstance().loadSkin(identifier);
        if (skin != null) {
            return new SkinDescriptorJS(new SkinDescriptor(identifier, skin.getType(), ColorScheme.EMPTY));
        }
        return null;
    }

    public SkinDescriptorJS loadSkinFromDB(String identifier) {
        SkinDescriptor descriptor = SkinLoader.getInstance().loadSkinFromDB(identifier, ColorScheme.EMPTY, true);
        if (!descriptor.isEmpty()) {
            return new SkinDescriptorJS(descriptor);
        }
        return null;
    }

    public SkinDescriptorJS loadSkinByItem(ItemStack itemStack) {
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        if (!descriptor.isEmpty()) {
            return new SkinDescriptorJS(descriptor);
        }
        return null;
    }

    public void setItem(String slotType, int slot, ItemStack itemStack) {
        wardrobe.setItem(SkinSlotType.of(slotType), slot, itemStack);
    }

    public ItemStack getItem(String slotType, int slot) {
        return wardrobe.getItem(SkinSlotType.of(slotType), slot);
    }

    public void clear() {
        wardrobe.clear();
    }

    public void setUnlockedSize(String slotType, int size) {
        wardrobe.setUnlockedSize(SkinSlotType.of(slotType), size);
    }

    public int getUnlockedSize(String slotType) {
        return wardrobe.getUnlockedSize(SkinSlotType.of(slotType));
    }

    public int getMaximumSize(String slotType) {
        return wardrobe.getMaximumSize(SkinSlotType.of(slotType));
    }

    public int getFreeSize(String slotType) {
        return wardrobe.getFreeSlot(SkinSlotType.of(slotType));
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
        BiConsumer<SkinWardrobe, Boolean> o = OPTIONS.get(opt);
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
            SkinDescriptor.setDescriptor(itemStack, descriptor);
        }

        public void removeFrom(ItemStack itemStack) {
            SkinDescriptor.setDescriptor(itemStack, SkinDescriptor.EMPTY);
        }

        public String getIdentifier() {
            return descriptor.getIdentifier();
        }

        public String getType() {
            return descriptor.getType().getRegistryName().getPath();
        }

        public ItemStack asItemStack() {
            return descriptor.asItemStack();
        }
    }
}
