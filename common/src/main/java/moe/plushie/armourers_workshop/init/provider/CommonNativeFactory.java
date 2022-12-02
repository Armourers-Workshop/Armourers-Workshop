package moe.plushie.armourers_workshop.init.provider;

import net.minecraft.network.chat.MutableComponent;

public interface CommonNativeFactory {

    MutableComponent createTranslatableComponent(String key, Object... args);
}
