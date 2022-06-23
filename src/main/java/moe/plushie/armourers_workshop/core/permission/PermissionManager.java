package moe.plushie.armourers_workshop.core.permission;

import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.init.common.ModBlocks;
import moe.plushie.armourers_workshop.init.common.ModContainerTypes;
import moe.plushie.armourers_workshop.init.common.ModLog;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.BlockPosContext;
import net.minecraftforge.server.permission.context.IContext;
import net.minecraftforge.server.permission.context.TargetContext;

import java.util.Objects;

public final class PermissionManager {

    private static String getPermissionName(IForgeRegistryEntry<?> entry, String op) {
        ResourceLocation registryName = Objects.requireNonNull(entry.getRegistryName());
        return String.format("%s.%s.%s", registryName.getNamespace(), registryName.getPath(), op);
    }

    private static boolean hasPermission(PlayerEntity player, IForgeRegistryEntry<?> entry, String op, IContext context) {
        return PermissionAPI.hasPermission(player.getGameProfile(), getPermissionName(entry, op), context);
    }

    private static void registerPermission(IForgeRegistryEntry<?> entry, String op) {
        String key = getPermissionName(entry, op);
        String desc = TranslateUtils.title("permission." + key).getContents();
        ModLog.debug("Registering permission: {}", key);
        PermissionAPI.registerNode(key, DefaultPermissionLevel.ALL, desc);
    }

    public static void registerPermissions() {

        registerPermission(ModBlocks.SKINNABLE, "sit");
        registerPermission(ModBlocks.SKINNABLE, "sleep");

        ModContainerTypes.forEach(containerType -> registerPermission(containerType, "open-gui"));
    }

    public static boolean shouldOpenGui(SkinWardrobe wardrobe, PlayerEntity player) {
        TargetContext context = new TargetContext(player, wardrobe.getEntity());
        return hasPermission(player, ModContainerTypes.WARDROBE, "open-gui", context);
    }

    public static boolean shouldOpenGui(ContainerType<?> containerType, PlayerEntity player, BlockPos pos) {
        BlockPosContext context = new BlockPosContext(player, pos, null, null);
        return hasPermission(player, containerType, "open-gui", context);
    }

    public static boolean shouldSit(TileEntity tileEntity, PlayerEntity player) {
        BlockPosContext context = new BlockPosContext(player, tileEntity.getBlockPos(), null, null);
        return hasPermission(player, ModBlocks.SKINNABLE, "sit", context);
    }

    public static boolean shouldSleep(TileEntity tileEntity, PlayerEntity player) {
        BlockPosContext context = new BlockPosContext(player, tileEntity.getBlockPos(), null, null);
        return hasPermission(player, ModBlocks.SKINNABLE, "sleep", context);
    }
}
