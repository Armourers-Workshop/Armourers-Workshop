package moe.plushie.armourers_workshop.api.common;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;

public interface IArgumentSerializer<A extends IArgumentType<?>> {

    A deserializeFromNetwork(FriendlyByteBuf buffer);

    void serializeToNetwork(A argument, FriendlyByteBuf buffer);

    void serializeToJson(A argument, JsonObject json);
}
