package moe.plushie.armourers_workshop.core.skin.serializer.v20.chunk;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleData;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleMaterial;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.EmitterInitialLocalSpace;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.EmitterInitialization;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.lifetime.EmitterEventLifetime;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.lifetime.EmitterExpressionLifetime;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.lifetime.EmitterLoopingLifetime;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.lifetime.EmitterOnceLifetime;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.rate.EmitterInstantRate;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.rate.EmitterManualRate;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.rate.EmitterSteadyRate;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.shape.EmitterBoxShape;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.shape.EmitterDiscShape;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.shape.EmitterEntityShape;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.shape.EmitterPointShape;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.shape.EmitterSphereShape;
import moe.plushie.armourers_workshop.core.skin.particle.component.particle.ParticleInitialSpeed;
import moe.plushie.armourers_workshop.core.skin.particle.component.particle.ParticleInitialSpin;
import moe.plushie.armourers_workshop.core.skin.particle.component.particle.ParticleInitialization;
import moe.plushie.armourers_workshop.core.skin.particle.component.particle.appearance.ParticleBillboardAppearance;
import moe.plushie.armourers_workshop.core.skin.particle.component.particle.appearance.ParticleLightingAppearance;
import moe.plushie.armourers_workshop.core.skin.particle.component.particle.appearance.ParticleTintingAppearance;
import moe.plushie.armourers_workshop.core.skin.particle.component.particle.lifetime.ParticleEventLifetime;
import moe.plushie.armourers_workshop.core.skin.particle.component.particle.lifetime.ParticleExpressLifetime;
import moe.plushie.armourers_workshop.core.skin.particle.component.particle.lifetime.ParticleKillInBlocksLifetime;
import moe.plushie.armourers_workshop.core.skin.particle.component.particle.lifetime.ParticleKillInPlaneLifetime;
import moe.plushie.armourers_workshop.core.skin.particle.component.particle.lifetime.ParticleOnlyInBlocksLifetime;
import moe.plushie.armourers_workshop.core.skin.particle.component.particle.motion.ParticleCollisionMotion;
import moe.plushie.armourers_workshop.core.skin.particle.component.particle.motion.ParticleDynamicMotion;
import moe.plushie.armourers_workshop.core.skin.particle.component.particle.motion.ParticleParametricMotion;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IInputStream;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOFunction;
import moe.plushie.armourers_workshop.core.utils.Collections;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkParticleData {

    private SkinParticleData particle;

    public ChunkParticleData() {
    }

    public ChunkParticleData(SkinParticleData particle) {
        this.particle = particle;
    }

    public void readFromStream(ChunkInputStream stream) throws IOException {
        var file = stream.readFile();
        var context = stream.context();
        var inputStream = new DataInputStream(new ByteBufInputStream(file.bytes()));
        this.particle = readContentFromStream(file.name(), new ChunkInputStream() {

            @Override
            public DataInputStream inputStream() {
                return inputStream;
            }

            @Override
            public ChunkContext context() {
                return context;
            }
        });
    }

    public void writeToStream(ChunkOutputStream stream) throws IOException {
        var bytes = Unpooled.buffer(1024);
        var context = stream.context();
        var outputStream = new DataOutputStream(new ByteBufOutputStream(bytes));
        writeContentToStream(particle, new ChunkOutputStream() {

            @Override
            public DataOutputStream outputStream() {
                return outputStream;
            }

            @Override
            public ChunkContext context() {
                return context;
            }
        });
        outputStream.close();
        stream.writeFile(ChunkFile.particle(particle.name(), bytes));
    }

    private SkinParticleData readContentFromStream(String name, ChunkInputStream stream) throws IOException {
        var textureData = new ChunkTextureData();
        var material = stream.readEnum(SkinParticleMaterial.class);
        textureData.readFromStream(stream);
        var components = new ArrayList<SkinParticleComponent>();
        var componentSize = stream.readVarInt();
        for (int i = 0; i < componentSize; i++) {
            var componentId = stream.readVarInt();
            var serializer = Serializer.ID_TO_SERIALIZERS.get(componentId);
            if (serializer == null) {
                throw new IOException("can't found serializer of the component id: " + componentId);
            }
            components.add(serializer.decoder.apply(stream));
        }
        return new SkinParticleData(name, material, textureData.texture(), components);
    }

    private void writeContentToStream(SkinParticleData particle, ChunkOutputStream stream) throws IOException {
        var textureData = new ChunkTextureData(particle.texture());
        textureData.setId(1);
        textureData.freeze(0, 0, p -> null);
        stream.writeEnum(particle.material());
        textureData.writeToStream(stream);
        stream.writeVarInt(particle.components().size());
        for (var component : particle.components()) {
            var serializer = Serializer.CLASS_TO_SERIALIZERS.get(component.getClass());
            if (serializer == null) {
                throw new IOException("can't found serializer of the component: " + component.getClass());
            }
            stream.writeVarInt(serializer.id);
            component.writeToStream(stream);
        }
    }

    public SkinParticleData particle() {
        return particle;
    }


    @SuppressWarnings("unused")
    private static class Serializer<T extends SkinParticleComponent> {

        private static final Map<Integer, Serializer<?>> ID_TO_SERIALIZERS = new HashMap<>();
        private static final Map<Class<?>, Serializer<?>> CLASS_TO_SERIALIZERS = new HashMap<>();

        private static final List<Serializer<?>> ALL_SERIALIZERS = Collections.immutableList(it -> {
            it.add(define(0, EmitterInitialization.class, EmitterInitialization::new));
            it.add(define(1, EmitterInitialLocalSpace.class, EmitterInitialLocalSpace::new));

            it.add(define(2, EmitterEventLifetime.class, EmitterEventLifetime::new));
            it.add(define(3, EmitterExpressionLifetime.class, EmitterExpressionLifetime::new));
            it.add(define(4, EmitterLoopingLifetime.class, EmitterLoopingLifetime::new));
            it.add(define(5, EmitterOnceLifetime.class, EmitterOnceLifetime::new));

            it.add(define(6, EmitterInstantRate.class, EmitterInstantRate::new));
            it.add(define(7, EmitterManualRate.class, EmitterManualRate::new));
            it.add(define(8, EmitterSteadyRate.class, EmitterSteadyRate::new));

            it.add(define(9, EmitterBoxShape.class, EmitterBoxShape::new));
            it.add(define(10, EmitterDiscShape.class, EmitterDiscShape::new));
            it.add(define(11, EmitterEntityShape.class, EmitterEntityShape::new));
            it.add(define(12, EmitterPointShape.class, EmitterPointShape::new));
            it.add(define(13, EmitterSphereShape.class, EmitterSphereShape::new));

            it.add(define(14, ParticleInitialization.class, ParticleInitialization::new));
            it.add(define(15, ParticleInitialSpeed.class, ParticleInitialSpeed::new));
            it.add(define(16, ParticleInitialSpin.class, ParticleInitialSpin::new));

            it.add(define(17, ParticleEventLifetime.class, ParticleEventLifetime::new));
            it.add(define(18, ParticleExpressLifetime.class, ParticleExpressLifetime::new));
            it.add(define(19, ParticleKillInBlocksLifetime.class, ParticleKillInBlocksLifetime::new));
            it.add(define(20, ParticleKillInPlaneLifetime.class, ParticleKillInPlaneLifetime::new));
            it.add(define(21, ParticleOnlyInBlocksLifetime.class, ParticleOnlyInBlocksLifetime::new));

            it.add(define(22, ParticleCollisionMotion.class, ParticleCollisionMotion::new));
            it.add(define(23, ParticleDynamicMotion.class, ParticleDynamicMotion::new));
            it.add(define(24, ParticleParametricMotion.class, ParticleParametricMotion::new));

            it.add(define(25, ParticleBillboardAppearance.class, ParticleBillboardAppearance::new));
            it.add(define(26, ParticleLightingAppearance.class, ParticleLightingAppearance::new));
            it.add(define(27, ParticleTintingAppearance.class, ParticleTintingAppearance::new));
        });

        private final int id;
        private final Class<T> clazz;
        private final IOFunction<IInputStream, T> decoder;

        private Serializer(int id, Class<T> clazz, IOFunction<IInputStream, T> decoder) {
            this.id = id;
            this.clazz = clazz;
            this.decoder = decoder;
            ID_TO_SERIALIZERS.put(id, this);
            CLASS_TO_SERIALIZERS.put(clazz, this);
        }

        private static <T extends SkinParticleComponent> Serializer<T> define(int id, Class<T> clazz, IOFunction<IInputStream, T> decoder) {
            return new Serializer<>(id, clazz, decoder);
        }
    }
}
