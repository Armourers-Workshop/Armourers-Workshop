package moe.plushie.armourers_workshop.core.utils;

import moe.plushie.armourers_workshop.core.bake.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.data.serialize.SkinSerializer;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobe;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.lwjgl.system.CallbackI;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
        // Register Message
        CHANNEL.messageBuilder(EntityPropertyMessage.class, CHANNEL_PACKET_ID++)
                .decoder(EntityPropertyMessage::new)
                .encoder(EntityPropertyMessage::write)
                .consumer(EntityPropertyMessage::execute)
                .add();
        CHANNEL.messageBuilder(TransmissionMessage.class, CHANNEL_PACKET_ID++)
                .decoder(TransmissionMessage::new)
                .encoder(TransmissionMessage::write)
                .consumer(TransmissionMessage::execute)
                .add();

        // Register Property Type
        EntityPropertyType.register(SkinWardrobe.class, SkinWardrobe::getEntity, SkinWardrobe::of);

        register(PacketBufferCoders.SKIN_DESCRIPTOR, PacketBufferCoders.SKIN, SkinCore.loader::loadSkin);
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

    private final AtomicInteger queueCounter = new AtomicInteger();
    private static HashMap<Integer, Consumer<?>> hm = new HashMap<>();

    public <Request, Response> void sendRequest(Request request, Consumer<Response> complete) {
        // request/buff > send/receive > buff/request > process > response/buff > send/receive > buff/response > complete
        int id = queueCounter.incrementAndGet();
        TransmissionMessage message = new TransmissionMessage(id, request);
        hm.put(id, complete);
        CHANNEL.send(PacketDistributor.SERVER.noArg(), message);
//        PacketDistributor.PacketTarget target = PacketDistributor.SERVER.noArg();
//        target.send(CHANNEL.toVanillaPacket(message, NetworkDirection.PLAY_TO_SERVER));


    }



    void enqueueWork(Runnable runnable) {
        runnable.run();
    }



    public static <Request, Response> void register(PacketBufferCoder<Request> requestCoder, PacketBufferCoder<Response> responseCoder, BiConsumer<Request, Consumer<Optional<Response>>> consumer) {

        TransmissionMessage.requestHandler(requestCoder, responseCoder, (message, context) -> {
            Request request = message.getValue();
            if (request == null) {
                message.reply(null, context, CHANNEL);
                return;
            }
            consumer.accept(request, response -> message.reply(response, context, CHANNEL));
        });

        TransmissionMessage.responseHandler(requestCoder, responseCoder, ((message, context) -> {
            LazyOptional<Response> response = message.value.cast();
            Consumer<Response> r = (Consumer<Response>) hm.remove(message.getId());
            if (r  != null) {
                r.accept(response.resolve().orElse(null));
            }
        }));

    }

    public <Response> void invokeListener(int id, Response response) {

    }


    public <Response> void addListener(int id, NonNullConsumer<Response> listener) {
//        if (isPresent())
//        {
//            this.listeners.add(listener);
//        }
//        else
//        {
//            listener.accept(this);
//        }
    }



}


