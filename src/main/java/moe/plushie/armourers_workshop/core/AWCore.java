package moe.plushie.armourers_workshop.core;

import moe.plushie.armourers_workshop.core.render.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.render.bake.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobe;
import moe.plushie.armourers_workshop.core.utils.SkinSlotType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class AWCore {

    public final static SkinBakery bakery = new SkinBakery();
    public final static SkinLoader loader = new SkinLoader();

    public static ResourceLocation resource(String path) {
        return new ResourceLocation(getModId(), path);
    }

    public static String getModId() {
        return "armourers_workshop";
    }

    public static void init() {

    }


//    public static Skin loadSkin(String identifier) {
//        BakedSkin skin = loadBakedSkin(identifier);
//        if (skin != null) {
//            return skin.getSkin();
//        }
//        return null;
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    public static BakedSkin loadBakedSkin(String identifier) {
//        if (identifier.isEmpty()) {
//            return null;
//        }
//        int iq = Integer.parseInt(identifier);
//        if (iq < skins.size()) {
//            return skins.get(iq);
//        }
//        return null;
//    }

    public static ResourceLocation getSlotIcon(String name) {
        return AWCore.resource("textures/items/slot/" + name + ".png");
    }

    public static ResourceLocation getItemIcon(String name) {
        return AWCore.resource("textures/items/template/" + name + ".png");
    }

    public static ItemStack getSkinFromEquipment(@Nullable Entity entity, SkinSlotType skinSlotType, EquipmentSlotType equipmentSlotType) {
        ItemStack itemStack = ItemStack.EMPTY;
        if (entity instanceof LivingEntity) {
            itemStack = ((LivingEntity) entity).getItemBySlot(equipmentSlotType);
        }
        if (itemStack.isEmpty()) {
            return itemStack;
        }
        // embedded skin is the highest priority
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.accept(itemStack)) {
            return itemStack;
        }
        SkinWardrobe wardrobe = SkinWardrobe.of(entity);
        if (wardrobe != null) {
            ItemStack itemStack1 = wardrobe.getItem(skinSlotType, 0);
            descriptor = SkinDescriptor.of(itemStack1);
            if (descriptor.accept(itemStack)) {
                return itemStack1;
            }
        }
        return itemStack;
    }


}
