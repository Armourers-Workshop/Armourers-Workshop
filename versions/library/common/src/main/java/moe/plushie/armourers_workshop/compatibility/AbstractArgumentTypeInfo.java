package moe.plushie.armourers_workshop.compatibility;

import com.google.gson.JsonObject;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.common.IArgumentType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;

@Available("[1.19, )")
public class AbstractArgumentTypeInfo<A extends IArgumentType<?>> implements ArgumentTypeInfo<A, AbstractArgumentTypeInfo.Template<A>> {

    private final IArgumentSerializer<A> serializer;

    public AbstractArgumentTypeInfo(IArgumentSerializer<A> serializer) {
        this.serializer = serializer;
    }

    @Override
    public void serializeToNetwork(Template<A> template, FriendlyByteBuf friendlyByteBuf) {
        serializer.serializeToNetwork(template.instance, friendlyByteBuf);
    }

    @Override
    public Template<A> deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
        return unpack(serializer.deserializeFromNetwork(friendlyByteBuf));
    }

    @Override
    public void serializeToJson(Template<A> template, JsonObject jsonObject) {
        serializer.serializeToJson(template.instance, jsonObject);
    }

    @Override
    public Template<A> unpack(A argumentType) {
        return new Template<>(argumentType, this);
    }

    public static class Template<A extends IArgumentType<?>> implements ArgumentTypeInfo.Template<A> {

        private final A instance;
        private final AbstractArgumentTypeInfo<A> argumentType;

        public Template(A instance, AbstractArgumentTypeInfo<A> argumentType) {
            this.instance = instance;
            this.argumentType = argumentType;
        }

        @Override
        public A instantiate(CommandBuildContext commandBuildContext) {
            return instance;
        }

        @Override
        public ArgumentTypeInfo<A, ?> type() {
            return argumentType;
        }
    }
}
