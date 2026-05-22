package moe.plushie.armourers_workshop.core.skin.particle.component.particle.appearance;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleCompiler;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;

import java.io.IOException;

/**
 * When this component exists, particle will be tinted by local lighting conditions in-game.
 */
public class ParticleLightingAppearance implements SkinParticleComponent {

    public ParticleLightingAppearance() {
    }

    public ParticleLightingAppearance(IInputStream stream) {
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
    }

    @Override
    public void compile(SkinParticleCompiler compiler) {
        compiler.emitter().prepare((emitter, context) -> {
            emitter.setEmissiveMode(false);
        });
    }
}
