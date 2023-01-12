package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.api.common.IPlayerDataSerializer;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.ModPermissions;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;

public class MenuManager {

    private static final HashMap<MenuType<?>, MenuOpener<Object>> MENU_OPENERS = new HashMap<>();

    public static <T extends AbstractContainerMenu, V> void registerMenuOpener(MenuType<T> menuType, IPlayerDataSerializer<V> serializer, MenuOpener<V> menuOpener) {
        MenuOpener<V> safeMenuOpener = (player, title, value) -> {
            try {
                return menuOpener.openMenu(player, title, value);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        };
        MENU_OPENERS.put(menuType, ObjectUtils.unsafeCast(safeMenuOpener));
    }

    public static <T extends AbstractContainerMenu, V> boolean openMenu(IRegistryKey<MenuType<T>> menuType, Player player, V value) {
        MenuType<T> menuType1 = menuType.get();
        MenuOpener<Object> menuOpener = MENU_OPENERS.get(menuType1);
        if (menuOpener == null) {
            ModLog.warn("Trying to open container for unknown container type {}", menuType1);
            return false;
        }
        if (player instanceof ServerPlayer) {
            Component title = TranslateUtils.title("inventory.armourers_workshop." + menuType.getRegistryName().getPath());
            return menuOpener.openMenu((ServerPlayer) player, title, value);
        }
        return false;
    }

    public static <C extends AbstractContainerMenu> InteractionResult openMenu(IRegistryKey<MenuType<C>> type, BlockEntity blockEntity, Player player) {
        // we assume it is a valid block entity.
        if (blockEntity != null && blockEntity.getLevel() != null) {
            return openMenu(type, blockEntity.getLevel(), blockEntity.getBlockPos(), player);
        }
        return InteractionResult.FAIL;
    }

    public static <C extends AbstractContainerMenu> InteractionResult openMenu(IRegistryKey<MenuType<C>> type, Level level, BlockPos blockPos, Player player) {
        // the player must have sufficient permissions to open the GUI.
        if (!ModPermissions.OPEN.accept(type, level, blockPos, player)) {
            return InteractionResult.FAIL;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (openMenu(type, player, ContainerLevelAccess.create(level, blockPos))) {
            return InteractionResult.CONSUME;
        }
        return InteractionResult.FAIL;
    }

    @FunctionalInterface
    public interface MenuOpener<V> {
        boolean openMenu(ServerPlayer player, Component title, V value);
    }
}

