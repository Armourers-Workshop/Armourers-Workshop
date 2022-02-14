package moe.plushie.armourers_workshop.core.network.packet;

import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;

public class UpdateWardrobePacket extends CustomPacket {

    private final int entityId;
    private final CompoundNBT wardrobeNBT;

    public UpdateWardrobePacket(PacketBuffer buffer) {
        this.entityId = buffer.readInt();
        this.wardrobeNBT = buffer.readNbt();
    }

    public UpdateWardrobePacket(SkinWardrobe wardrobe) {
        this.entityId = wardrobe.getEntity().getId();
        this.wardrobeNBT = wardrobe.serializeNBT();
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeInt(entityId);
        buffer.writeNbt(wardrobeNBT);
    }

    @Override
    public void accept(ServerPlayNetHandler netHandler, ServerPlayerEntity player) {
        SkinWardrobe wardrobe = SkinWardrobe.of(player.level.getEntity(entityId));
        if (wardrobe != null) {
            wardrobe.deserializeNBT(wardrobeNBT);
        }
    }

    @Override
    public void accept(INetHandler netHandler, PlayerEntity player) {
        SkinWardrobe wardrobe = SkinWardrobe.of(player.level.getEntity(entityId));
        if (wardrobe != null) {
            wardrobe.deserializeNBT(wardrobeNBT);
        }
    }
}
