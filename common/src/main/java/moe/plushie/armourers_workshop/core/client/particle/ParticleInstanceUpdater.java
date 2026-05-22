package moe.plushie.armourers_workshop.core.client.particle;

import com.google.common.collect.ImmutableList;
import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticle;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleEmitter;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleCompiler;

import java.util.ArrayList;
import java.util.List;

public class ParticleInstanceUpdater {

    private final List<Builder.Event> prepare;
    private final List<Builder.Event> tick;
    private final List<Builder.Event> render;

    private ParticleInstanceUpdater(List<Builder.Event> prepare, List<Builder.Event> tick, List<Builder.Event> render) {
        this.prepare = prepare;
        this.tick = tick;
        this.render = render;
    }

    public void prepare(SkinParticleEmitter emitter, SkinParticle particle, ExecutionContext context) {
        for (var event : prepare) {
            event.apply(emitter, particle, context);
        }
    }

    public void tick(SkinParticleEmitter emitter, SkinParticle particle, ExecutionContext context) {
        for (var event : tick) {
            event.apply(emitter, particle, context);
        }
    }

    public void render(SkinParticleEmitter emitter, SkinParticle particle, ExecutionContext context) {
        for (var event : render) {
            event.apply(emitter, particle, context);
        }
    }

    public static class Builder implements SkinParticleCompiler.InstanceFactory {

        private final ArrayList<Event> prepare = new ArrayList<>();
        private final ArrayList<Event> tick = new ArrayList<>();
        private final ArrayList<Event> render = new ArrayList<>();

        @Override
        public void prepare(Event event) {
            prepare.add(event);
        }

        @Override
        public void tick(Event event) {
            tick.add(event);
        }

        @Override
        public void render(Event event) {
            render.add(event);
        }

        public ParticleInstanceUpdater build() {
            return new ParticleInstanceUpdater(ImmutableList.copyOf(prepare), ImmutableList.copyOf(tick), ImmutableList.copyOf(render));
        }
    }
}
