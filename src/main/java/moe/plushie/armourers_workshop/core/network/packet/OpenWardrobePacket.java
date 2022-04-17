package moe.plushie.armourers_workshop.core.network.packet;

import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.init.common.ModConfig;
import moe.plushie.armourers_workshop.init.common.ModContainerTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;

public class OpenWardrobePacket extends CustomPacket {

    private final int entityId;

    public OpenWardrobePacket(PacketBuffer buffer) {
        this.entityId = buffer.readInt();
    }

    public OpenWardrobePacket(Entity entity) {
        this.entityId = entity.getId();
    }

    @Override
    public void encode(PacketBuffer buffer) {
        buffer.writeInt(entityId);
    }

    @Override
    public void accept(ServerPlayNetHandler netHandler, ServerPlayerEntity player) {
        Entity entity = player.level.getEntity(entityId);
        SkinWardrobe wardrobe = SkinWardrobe.of(entity);
        if (wardrobe != null && ModConfig.Common.canOpenWardrobe(player, player)) {
            ModContainerTypes.open(ModContainerTypes.WARDROBE, player, wardrobe);
        }
    }
}
