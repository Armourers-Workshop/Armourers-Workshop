package moe.plushie.armourers_workshop.init.common;

import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
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
    public static File getSkinLibraryDirectory() {
        return new File(getRootDirectory(), "skin-library");
    }
}
