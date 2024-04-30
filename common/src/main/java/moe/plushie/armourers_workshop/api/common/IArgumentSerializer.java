package moe.plushie.armourers_workshop.api.common;

import com.google.gson.JsonObject;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;

public interface IArgumentSerializer<A extends IArgumentType<?>> {

    A deserializeFromNetwork(IFriendlyByteBuf buffer);

    void serializeToNetwork(A argument, IFriendlyByteBuf buffer);

    void serializeToJson(A argument, JsonObject json);
}
