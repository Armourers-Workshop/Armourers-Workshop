package moe.plushie.armourers_workshop.compat.forge.extensions.net.minecraft.network.Connection;

import moe.plushie.armourers_workshop.api.annotation.Available;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.21, )")
@Extension
public class DistributorExt {

    public static void sendToPlayer(@ThisClass Class<?> clazz, ServerPlayer player, CustomPacketPayload payload, CustomPacketPayload... payloads) {
        PacketDistributor.sendToPlayer(player, payload, payloads);
    }

    public static void sendToPlayersInDimension(@ThisClass Class<?> clazz, ServerLevel level, CustomPacketPayload payload, CustomPacketPayload... payloads) {
        PacketDistributor.sendToPlayersInDimension(level, payload, payloads);
    }

    public static void sendToPlayersNear(@ThisClass Class<?> clazz, ServerLevel level, @Nullable ServerPlayer excluded, double x, double y, double z, double radius, CustomPacketPayload payload, CustomPacketPayload... payloads) {
        PacketDistributor.sendToPlayersNear(level, excluded, x, y, z, radius, payload, payloads);
    }

    public static void sendToAllPlayers(@ThisClass Class<?> clazz, CustomPacketPayload payload, CustomPacketPayload... payloads) {
        PacketDistributor.sendToAllPlayers(payload, payloads);
    }

    public static void sendToPlayersTrackingEntity(@ThisClass Class<?> clazz, Entity entity, CustomPacketPayload payload, CustomPacketPayload... payloads) {
        PacketDistributor.sendToPlayersTrackingEntity(entity, payload, payloads);
    }

    public static void sendToPlayersTrackingEntityAndSelf(@ThisClass Class<?> clazz, Entity entity, CustomPacketPayload payload, CustomPacketPayload... payloads) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, payload, payloads);
    }

    public static void sendToPlayersTrackingChunk(@ThisClass Class<?> clazz, ServerLevel level, ChunkPos chunkPos, CustomPacketPayload payload, CustomPacketPayload... payloads) {
        PacketDistributor.sendToPlayersTrackingChunk(level, chunkPos, payload, payloads);
    }
}
