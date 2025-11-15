package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.api.common.IGlobalPos;
import moe.plushie.armourers_workshop.api.common.IMenuType;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.compat.core.AbstractMenuType;
import moe.plushie.armourers_workshop.core.utils.OpenInteractionResult;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import moe.plushie.armourers_workshop.init.ModPermissions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Optional;

public class MenuManager {

    public static OpenInteractionResult openMenu(IRegistryHolder<? extends IMenuType<?>> menuType, Player player, Level level, BlockPos pos) {
        return openMenu(menuType, player, IGlobalPos.create(level, pos));
    }

    public static OpenInteractionResult openMenu(IRegistryHolder<? extends IMenuType<?>> menuType, Player player, Object value) {
        // we can't open when null value.
        if (player == null || value == null) {
            return OpenInteractionResult.FAIL;
        }
        // only handle server side, in the client side it always succeeds.
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return OpenInteractionResult.CONSUME;
        }
        var globalPos = getGlobalPos(value);
        if (globalPos != null) {
            return openMenu(menuType, serverPlayer, globalPos, null).orElse(OpenInteractionResult.FAIL);
        }
        return openMenu(menuType, serverPlayer, value);
    }

    private static Optional<OpenInteractionResult> openMenu(IRegistryHolder<? extends IMenuType<?>> menuType, ServerPlayer player, IGlobalPos globalPos, Object extraData) {
        return globalPos.evaluate((level, blockPos) -> {
            // the player must have sufficient permissions to open the GUI.
            // note: only check in the server side.
            if (!ModPermissions.OPEN.accept(menuType, level, blockPos, player)) {
                return OpenInteractionResult.FAIL;
            }
            return openMenu(menuType, player, globalPos);
        });
    }

    private static <T> OpenInteractionResult openMenu(IRegistryHolder<? extends IMenuType<?>> menuType, ServerPlayer player, T value) {
        var title = TranslateUtils.title(menuType.registryName().toLanguageKey("inventory"));
        if (menuType.get() instanceof AbstractMenuType<?> menuType1) {
            return menuType1.openMenu(player, title, value);
        }
        return OpenInteractionResult.FAIL; // an unknown error occurred.
    }

    private static <T> IGlobalPos getGlobalPos(T value) {
        if (value instanceof IGlobalPos globalPos) {
            return globalPos;
        }
        if (value instanceof BlockEntity blockEntity) {
            return IGlobalPos.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        }
        return null;
    }
}
