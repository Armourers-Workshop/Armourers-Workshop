package moe.plushie.armourers_workshop.compatibility;

import com.google.gson.JsonObject;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.common.IArgumentType;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

@Available("[1.16, 1.19)")
public class AbstractArgumentTypeInfo<A extends IArgumentType<?>> implements ArgumentSerializer<A> {

    private final IArgumentSerializer<A> serializer;

    public AbstractArgumentTypeInfo(IArgumentSerializer<A> serializer) {
        this.serializer = serializer;
    }

    public static <T extends IArgumentType<?>> void register(ResourceLocation registryName, Class<T> argumentType, IArgumentSerializer<T> argumentSerializer) {
        ArgumentTypes.register(registryName.toString(), argumentType, new AbstractArgumentTypeInfo<>(argumentSerializer));
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
