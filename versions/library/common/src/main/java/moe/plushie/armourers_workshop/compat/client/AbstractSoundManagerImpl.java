package moe.plushie.armourers_workshop.compat.client;

import net.minecraft.resources.ResourceLocation;

public interface AbstractSoundManagerImpl {

    void aw2$register(ResourceLocation relocation, AbstractSimpleSound sound);

    void aw2$unregister(ResourceLocation relocation);
}
