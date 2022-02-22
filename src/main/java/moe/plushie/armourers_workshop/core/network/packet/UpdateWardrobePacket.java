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

public class UpdateWardrobePacket extends CustomPacket {

    private final Mode mode;
    private final int entityId;
    private final CompoundNBT compoundNBT;

    public UpdateWardrobePacket(PacketBuffer buffer) {
        this.mode = buffer.readEnum(Mode.class);
        this.entityId = buffer.readInt();
        this.compoundNBT = buffer.readNbt();
    }

    public UpdateWardrobePacket(SkinWardrobe wardrobe, Mode mode, CompoundNBT compoundNBT) {
        this.mode = mode;
        this.entityId = wardrobe.getId();
        this.compoundNBT = compoundNBT;
    }

    public static UpdateWardrobePacket all(SkinWardrobe wardrobe) {
        return new UpdateWardrobePacket(wardrobe, Mode.SYNC, wardrobe.serializeNBT());
    }

    public static UpdateWardrobePacket item(SkinWardrobe wardrobe, int slot, ItemStack itemStack) {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putInt("Slot", slot);
        compoundNBT.put("Item", itemStack.serializeNBT());
        return new UpdateWardrobePacket(wardrobe, Mode.SYNC_ITEM, compoundNBT);
    }

    public static UpdateWardrobePacket option(SkinWardrobe wardrobe, SkinWardrobeOption option, boolean value) {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putInt("Option", option.ordinal());
        compoundNBT.putBoolean("Value", value);
        return new UpdateWardrobePacket(wardrobe, Mode.SYNC_OPTION, compoundNBT);
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeEnum(mode);
        buffer.writeInt(entityId);
        buffer.writeNbt(compoundNBT);
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

    public SkinWardrobe apply(PlayerEntity player) {
        SkinWardrobe wardrobe = SkinWardrobe.of(player.level.getEntity(entityId));
        if (wardrobe == null) {
            return null;
        }
        switch (mode) {
            case SYNC:
                wardrobe.deserializeNBT(compoundNBT);
                break;
            case SYNC_ITEM:
                IInventory inventory = wardrobe.getInventory();
                int slot = compoundNBT.getInt("Slot");
                if (slot >= inventory.getContainerSize()) {
                    return null;
                }
                inventory.setItem(slot, ItemStack.of(compoundNBT.getCompound("Item")));
                break;
            case SYNC_OPTION:
                SkinWardrobeOption[] values = SkinWardrobeOption.values();
                int index = compoundNBT.getInt("Option");
                if (index >= values.length) {
                    return null;
                }
                values[index].set(wardrobe, compoundNBT.getBoolean("Value"));
                return wardrobe;
        }
        return wardrobe;
    }

    public enum Mode {
        SYNC,
        SYNC_ITEM,
        SYNC_OPTION
    }
}
