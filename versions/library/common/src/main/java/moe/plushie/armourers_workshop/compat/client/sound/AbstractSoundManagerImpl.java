package moe.plushie.armourers_workshop.compat.client.sound;

import moe.plushie.armourers_workshop.core.utils.OpenResourceKey;

public interface AbstractSoundManagerImpl {

    void aw2$register(OpenResourceKey relocation, AbstractSimpleSound sound);

    void aw2$unregister(OpenResourceKey relocation);
}
