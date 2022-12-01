package moe.plushie.armourers_workshop.compatibility;

import com.google.gson.JsonObject;
import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.common.IArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;

public class AbstractArgumentTypeInfo<A extends IArgumentType<?>> implements ArgumentTypeInfo<A, AbstractArgumentTypeInfo.Template<A>> {

    private final IArgumentSerializer<A> serializer;

    public AbstractArgumentTypeInfo(IArgumentSerializer<A> serializer) {
        this.serializer = serializer;
    }

    @Override
    public void serializeToNetwork(Template<A> template, FriendlyByteBuf friendlyByteBuf) {
        template.serializer.serializeToNetwork(template.instance, friendlyByteBuf);
    }

    @Override
    public Template<A> deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
        return unpack(serializer.deserializeFromNetwork(friendlyByteBuf));
    }

    @Override
    public void serializeToJson(Template<A> template, JsonObject jsonObject) {
        template.serializer.serializeToJson(template.instance, jsonObject);
    }

    @Override
    public Template<A> unpack(A argumentType) {
        return new Template<>(argumentType, serializer);
    }

    public static class Template<A extends IArgumentType<?>> implements ArgumentTypeInfo.Template<A> {

        private final A instance;
        private final IArgumentSerializer<A> serializer;

        public Template(A instance, IArgumentSerializer<A> serializer) {
            this.instance = instance;
            this.serializer = serializer;
        }

        @Override
        public A instantiate(CommandBuildContext commandBuildContext) {
            return instance;
        }

        @Override
        public ArgumentTypeInfo<A, ?> type() {
            return new AbstractArgumentTypeInfo<>(serializer);
        }
    }
}
