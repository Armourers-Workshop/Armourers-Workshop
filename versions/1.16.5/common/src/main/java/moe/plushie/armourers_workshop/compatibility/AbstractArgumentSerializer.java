package moe.plushie.armourers_workshop.compatibility;

import com.google.gson.JsonObject;
import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.common.IArgumentType;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;

public class AbstractArgumentSerializer<A extends IArgumentType<?>> implements ArgumentSerializer<A> {

    private final IArgumentSerializer<A> serializer;

    public AbstractArgumentSerializer(IArgumentSerializer<A> serializer) {
        this.serializer = serializer;
    }

    @Override
    public void serializeToNetwork(A argumentType, FriendlyByteBuf friendlyByteBuf) {
        serializer.serializeToNetwork(argumentType, friendlyByteBuf);
    }

    @Override
    public A deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
        return serializer.deserializeFromNetwork(friendlyByteBuf);
    }

    @Override
    public void serializeToJson(A argumentType, JsonObject jsonObject) {
        serializer.serializeToJson(argumentType, jsonObject);
    }
}
