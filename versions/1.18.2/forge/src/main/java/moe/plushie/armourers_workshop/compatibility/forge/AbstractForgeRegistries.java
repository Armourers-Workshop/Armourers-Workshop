package moe.plushie.armourers_workshop.compatibility.forge;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public abstract class AbstractForgeRegistries extends ForgeRegistries {

    public static final IForgeRegistry<MenuType<?>> MENUS = ForgeRegistries.CONTAINERS;
}
