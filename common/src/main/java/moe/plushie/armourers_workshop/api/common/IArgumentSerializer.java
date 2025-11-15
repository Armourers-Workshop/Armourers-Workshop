package moe.plushie.armourers_workshop.api.common;

import com.google.gson.JsonObject;
import moe.plushie.armourers_workshop.api.network.IFriendlyByteBuf;

public interface IArgumentSerializer<T extends IArgumentType<?>> {

    T deserializeFromNetwork(IFriendlyByteBuf buffer);

    void serializeToNetwork(T argument, IFriendlyByteBuf buffer);

    void serializeToJson(T argument, JsonObject json);

    Class<T> type();
}
