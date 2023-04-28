package moe.plushie.armourers_workshop.init.provider;

import moe.plushie.armourers_workshop.api.client.IBufferBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public interface ClientNativeFactory {

    IBufferBuilder createBuilderBuffer(int size);
}
