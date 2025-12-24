package moe.plushie.armourers_workshop.core.skin.particle;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOExpressionCompiler;
import net.minecraft.world.level.block.Block;

public interface SkinParticleGenerator extends IOExpressionCompiler {

    Emitter emitter();

    Instance instance();

    Registry registry();

    interface Registry {

        Block getBlock(String registryName);
    }

    interface Emitter {

        void prepare(Event event);

        void tick(Event event);

        void render(Event event);

        interface Event {
            void apply(SkinParticleEmitter emitter, ExecutionContext context);
        }
    }

    interface Instance {

        void prepare(Event event);

        void tick(Event event);

        void render(Event event);

        interface Event {
            void apply(SkinParticleEmitter emitter, SkinParticle particle, ExecutionContext context);
        }
    }
}

