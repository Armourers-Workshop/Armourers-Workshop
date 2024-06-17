package moe.plushie.armourers_workshop.core.network;

import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;
import moe.plushie.armourers_workshop.api.network.IServerPacketHandler;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.init.ModMenuTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class OpenWardrobePacket extends CustomPacket {

    private final int entityId;

    public OpenWardrobePacket(IFriendlyByteBuf buffer) {
        this.entityId = buffer.readInt();
    }

    public OpenWardrobePacket(Entity entity) {
        this.entityId = entity.getId();
    }

    @Override
    public void encode(IFriendlyByteBuf buffer) {
        buffer.writeInt(entityId);
    }

    @Override
    public void accept(IServerPacketHandler packetHandler, ServerPlayer player) {
        var entity = player.getLevel().getEntity(entityId);
        var wardrobe = SkinWardrobe.of(entity);
        if (wardrobe != null && wardrobe.isEditable(player)) {
            ModMenuTypes.WARDROBE.get().openMenu(player, wardrobe);
        }
    }
}
