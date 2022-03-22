package moe.plushie.armourers_workshop.core;

import moe.plushie.armourers_workshop.core.capability.Wardrobe;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.utils.SkinSlotType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Path;

public class AWCore {

    public static ResourceLocation resource(String path) {
        return new ResourceLocation(getModId(), path);
    }

    public static String getModId() {
        return "armourers_workshop";
    }

    public static void init() {
    }


    public static ResourceLocation getSlotIcon(String name) {
        return AWCore.resource("textures/item/slot/" + name + ".png");
    }

    public static ResourceLocation getItemIcon(String name) {
        return AWCore.resource("textures/item/template/" + name + ".png");
    }

    public static File getRootDirectory() {
        return new File(FMLPaths.GAMEDIR.get().toFile(), "armourers_workshop");
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
        Wardrobe wardrobe = Wardrobe.of(entity);
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
