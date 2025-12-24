package moe.plushie.armourers_workshop.core.skin.particle;

import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;

import java.io.IOException;

public interface SkinParticleComponent {

    void writeToStream(IOutputStream stream) throws IOException;

    void compile(SkinParticleGenerator generator);

    default int priority() {
        return 0;
    }
}
