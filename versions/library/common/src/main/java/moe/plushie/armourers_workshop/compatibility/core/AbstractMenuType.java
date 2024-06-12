package moe.plushie.armourers_workshop.compatibility.core;

import moe.plushie.armourers_workshop.api.common.IGlobalPos;
import moe.plushie.armourers_workshop.api.common.IMenuType;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.init.ModPermissions;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Optional;

public abstract class AbstractMenuType<C extends AbstractContainerMenu> implements IMenuType<C> {

    private IResourceLocation registryName;

    @Override
    public <T> InteractionResult openMenu(Player player, T value) {
        // we can't open when null value.
        if (player == null || value == null) {
            return InteractionResult.FAIL;
        }
        // only handle server side, in the client side it always succeeds.
        ServerPlayer serverPlayer = ObjectUtils.safeCast(player, ServerPlayer.class);
        if (serverPlayer == null) {
            return InteractionResult.CONSUME;
        }
        IGlobalPos globalPos = getGlobalPos(value);
        if (globalPos != null) {
            return openMenu(serverPlayer, globalPos, null).orElse(InteractionResult.FAIL);
        }
        return openMenu(serverPlayer, getTitle(), value);
    }

    protected Optional<InteractionResult> openMenu(ServerPlayer player, IGlobalPos globalPos, Object extraData) {
        return globalPos.evaluate((level, blockPos) -> {
            // the player must have sufficient permissions to open the GUI.
            // note: only check in the server side.
            if (!ModPermissions.OPEN.accept(this, level, blockPos, player)) {
                return InteractionResult.FAIL;
            }
            return openMenu(player, getTitle(), globalPos);
        });
    }

    protected abstract <T> InteractionResult openMenu(ServerPlayer player, Component title, T value);

    protected <T> IGlobalPos getGlobalPos(T value) {
        if (value instanceof IGlobalPos globalPos) {
            return globalPos;
        }
        if (value instanceof BlockEntity blockEntity) {
            return IGlobalPos.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        }
        return null;
    }

    @Override
    public Component getTitle() {
        return TranslateUtils.title("inventory.armourers_workshop." + getRegistryName().getPath());
    }

    public void setRegistryName(IResourceLocation registryName) {
        this.registryName = registryName;
    }

    @Override
    public IResourceLocation getRegistryName() {
        return registryName;
    }
}
