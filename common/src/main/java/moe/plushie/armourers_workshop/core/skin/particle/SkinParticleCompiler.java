package moe.plushie.armourers_workshop.core.skin.particle;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOExpressionCompiler;
import net.minecraft.world.level.block.Block;

public interface SkinParticleCompiler extends IOExpressionCompiler {

    Registry registry();

    EmitterFactory emitter();

    InstanceFactory instance();

    interface Registry {

        Block getBlock(String registryName);
    }

    interface EmitterFactory {

        void prepare(Event event);

        void tick(Event event);

        void render(Event event);

        interface Event {
            void apply(SkinParticleEmitter emitter, ExecutionContext context);
        }
    }

    interface InstanceFactory {

        void prepare(Event event);

        void tick(Event event);

        void render(Event event);

        interface Event {
            void apply(SkinParticleEmitter emitter, SkinParticle particle, ExecutionContext context);
        }
    }
}

