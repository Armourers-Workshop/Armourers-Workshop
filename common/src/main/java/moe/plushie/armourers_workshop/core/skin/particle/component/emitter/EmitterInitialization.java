package moe.plushie.armourers_workshop.core.skin.particle.component.emitter;

import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleCompiler;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOutputStream;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

import java.io.IOException;

/**
 * This component allows the emitter to run some Molang at creation, primarily to populate any Molang variables that get used later.
 */
public class EmitterInitialization implements SkinParticleComponent {

    /// this is run once at emitter startup.
    private final OpenPrimitive creation;

    /// this is run once per emitter update.
    private final OpenPrimitive update;

    public EmitterInitialization(OpenPrimitive creation, OpenPrimitive update) {
        this.creation = creation;
        this.update = update;
    }

    public EmitterInitialization(IInputStream stream) throws IOException {
        this.creation = stream.readPrimitiveObject();
        this.update = stream.readPrimitiveObject();
    }

    @Override
    public void writeToStream(IOutputStream stream) throws IOException {
        stream.writePrimitiveObject(creation);
        stream.writePrimitiveObject(update);
    }

    @Override
    public void compile(SkinParticleCompiler compiler) {
        var creation = compiler.compile(this.creation, 0.0);
        var update = compiler.compile(this.update, 0.0);
        compiler.emitter().prepare((emitter, context) -> {
            creation.evaluate(context);
        });
        compiler.emitter().tick((emitter, context) -> {
            update.evaluate(context);
        });
    }
}
