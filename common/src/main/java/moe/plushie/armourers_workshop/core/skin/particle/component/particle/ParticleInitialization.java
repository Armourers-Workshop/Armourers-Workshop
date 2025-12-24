package moe.plushie.armourers_workshop.core.skin.particle.component.particle;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleGenerator;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

/**
 * Starts the particle with a specified render expression.
 */
public class ParticleInitialization implements SkinParticleComponent {

    private final OpenPrimitive update;
    private final OpenPrimitive render;

    public ParticleInitialization(OpenPrimitive update, OpenPrimitive render) {
        this.update = update;
        this.render = render;
    }

    public ParticleInitialization(IInputStream stream) throws IOException {
        this.update = stream.readPrimitiveObject();
        this.render = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(update);
        stream.writePrimitiveObject(render);
    }

    @Override
    public void compile(SkinParticleGenerator generator) {
        var update = generator.compile(this.update, 0.0);
        var render = generator.compile(this.render, 0.0);
        generator.instance().tick((emitter, particle, context) -> {
            update.evaluate(context);
        });
        generator.instance().render((emitter, particle, context) -> {
            render.evaluate(context);
        });
    }
}
