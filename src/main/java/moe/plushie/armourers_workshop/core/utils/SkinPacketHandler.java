package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobe;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import javax.annotation.Nullable;

public class SkinPacketHandler {


    private static final String CHANNEL_VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SkinCore.getModId(), SkinCore.getModChannel()),
            () -> CHANNEL_VERSION,
            CHANNEL_VERSION::equals,
            CHANNEL_VERSION::equals
    );

    private static int CHANNEL_PACKET_ID = 0;

    public static void init() {
        // Register Property Message
        CHANNEL.messageBuilder(EntityPropertyMessage.class, CHANNEL_PACKET_ID++)
                .decoder(EntityPropertyMessage::new)
                .encoder(EntityPropertyMessage::write)
                .consumer(EntityPropertyMessage::execute)
                .add();
        // Register Property Type
        EntityPropertyType.register(SkinWardrobe.class, SkinWardrobe::getEntity, SkinWardrobe::of);
    }

    public static <T extends INBTSerializable<CompoundNBT>> void sendTo(T value) {
        EntityPropertyType<?> type = EntityPropertyType.byClass(value.getClass());
        if (type == null) {
            return;
        }
        Entity entity = type.getEntity(value);
        if (entity == null) {
            return;
        }
        EntityPropertyMessage message = new EntityPropertyMessage(type, entity, value.serializeNBT());
        CHANNEL.send(PacketDistributor.SERVER.noArg(), message);
    }

    public static <T extends INBTSerializable<CompoundNBT>> void sendTo(T value, @Nullable ServerPlayerEntity player) {
        EntityPropertyType<?> type = EntityPropertyType.byClass(value.getClass());
        if (type == null) {
            return;
        }
        Entity entity = type.getEntity(value);
        if (entity == null) {
            return;
        }
        EntityPropertyMessage message = new EntityPropertyMessage(type, entity, value.serializeNBT());
        if (player != null) {
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
        } else {
            CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), message);
        }
    }
}


