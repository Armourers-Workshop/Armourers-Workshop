package moe.plushie.armourers_workshop.core.network.packet;

import moe.plushie.armourers_workshop.core.network.NetworkHandler;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobe;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeOption;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;

import javax.annotation.Nullable;

public class UpdateWardrobePacket extends CustomPacket {

    private final Mode mode;
    private final int entityId;

    private final Object optionData;
    private final SkinWardrobeOption option;

    private final CompoundNBT compoundNBT;

    public UpdateWardrobePacket(PacketBuffer buffer) {
        this.mode = buffer.readEnum(Mode.class);
        this.entityId = buffer.readInt();
        if (this.mode != Mode.SYNC_OPTION) {
            this.optionData = null;
            this.option = null;
            this.compoundNBT = buffer.readNbt();
        } else {
            this.option = buffer.readEnum(SkinWardrobeOption.class);
            this.optionData = option.getDataSerializer().read(buffer);
            this.compoundNBT = null;
        }
    }

    public UpdateWardrobePacket(SkinWardrobe wardrobe, Mode mode, CompoundNBT compoundNBT, SkinWardrobeOption option, Object optionData) {
        this.mode = mode;
        this.entityId = wardrobe.getId();
        this.option = option;
        this.optionData = optionData;
        this.compoundNBT = compoundNBT;
    }

    public static UpdateWardrobePacket sync(SkinWardrobe wardrobe) {
        return new UpdateWardrobePacket(wardrobe, Mode.SYNC, wardrobe.serializeNBT(), null, null);
    }

    public static UpdateWardrobePacket pick(SkinWardrobe wardrobe, int slot, ItemStack itemStack) {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putInt("Slot", slot);
        compoundNBT.put("Item", itemStack.serializeNBT());
        return new UpdateWardrobePacket(wardrobe, Mode.SYNC_ITEM, compoundNBT, null, null);
    }

    public static UpdateWardrobePacket opt(SkinWardrobe wardrobe, SkinWardrobeOption option, Object value) {
        return new UpdateWardrobePacket(wardrobe, Mode.SYNC_OPTION, null, option, value);
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeEnum(mode);
        buffer.writeInt(entityId);
        if (compoundNBT != null) {
            buffer.writeNbt(compoundNBT);
        }
        if (option != null) {
            buffer.writeEnum(option);
            option.getDataSerializer().write(buffer, optionData);
        }
    }

    @Override
    public void accept(ServerPlayNetHandler netHandler, ServerPlayerEntity player) {
        // TODO: check operator permission
        SkinWardrobe wardrobe = apply(player);
        if (wardrobe != null) {
            NetworkHandler.getInstance().sendToAll(this);
        }
    }

    @Override
    public void accept(INetHandler netHandler, PlayerEntity player) {
        apply(player);
    }

    @Nullable
    private SkinWardrobe apply(PlayerEntity player) {
        SkinWardrobe wardrobe = SkinWardrobe.of(player.level.getEntity(entityId));
        if (wardrobe == null) {
            return null;
        }
        switch (mode) {
            case SYNC:
                wardrobe.deserializeNBT(compoundNBT);
                return wardrobe;

            case SYNC_ITEM:
                IInventory inventory = wardrobe.getInventory();
                int slot = compoundNBT.getInt("Slot");
                if (slot < inventory.getContainerSize()) {
                    inventory.setItem(slot, ItemStack.of(compoundNBT.getCompound("Item")));
                    return wardrobe;
                }
                break;

            case SYNC_OPTION:
                if (option != null) {
                    option.set(wardrobe, optionData);
                    if (option.isBroadcastChanges()) {
                        return wardrobe;
                    }
                }
        }
        return null;
    }

    public enum Mode {
        SYNC, SYNC_ITEM, SYNC_OPTION
    }
}
