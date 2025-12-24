package moe.plushie.armourers_workshop.core.client.particle;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.api.client.IGraphicsElement;
import moe.plushie.armourers_workshop.api.client.IGraphicsRenderable;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.api.client.IVertexConsumer;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compat.client.AbstractCamera;
import moe.plushie.armourers_workshop.compat.client.particle.AbstractParticleRenderer;
import moe.plushie.armourers_workshop.compat.core.AbstractRegistryManager;
import moe.plushie.armourers_workshop.core.client.animation.AnimationEngine;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderType;
import moe.plushie.armourers_workshop.core.client.render.element.ParticleElement;
import moe.plushie.armourers_workshop.core.client.texture.LightmapTexture;
import moe.plushie.armourers_workshop.core.client.texture.OverlayTexture;
import moe.plushie.armourers_workshop.core.client.texture.SmartTextureManager;
import moe.plushie.armourers_workshop.core.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.core.math.OpenRectangle2f;
import moe.plushie.armourers_workshop.core.math.OpenSize2f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleData;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleGenerator;
import moe.plushie.armourers_workshop.core.skin.particle.math.ParticleCameraFacing;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureData;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import moe.plushie.armourers_workshop.core.utils.ReferenceCounted;
import moe.plushie.armourers_workshop.init.ModConstants;

import java.util.ArrayList;
import java.util.Comparator;

@OnlyIn(Dist.CLIENT)
public class SmartParticle extends ReferenceCounted {

    private final String name;
    private final Renderer renderer;
    private final Generator generator;

    public SmartParticle(SkinParticleData provider) {
        this.name = provider.name();
        this.renderer = new Renderer(resolveRenderType(provider.texture()));
        this.generator = new Generator(provider);
    }

    public SmartParticleEmitter spawn(ExecutionContext context) {
        return generator.build(renderer, context);
    }

    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    protected void init() {
        SmartTextureManager.getInstance().open(renderer.renderType);
    }

    @Override
    protected void dispose() {
        SmartTextureManager.getInstance().close(renderer.renderType);
    }

    protected void unbind() {
        // when unbind the object, we must ensure that all resources release.
        while (refCnt() > 0) {
            release();
        }
    }

    private IRenderType resolveRenderType(SkinTextureData textureData) {
        // this is a builtin particle texture.
        if (textureData.name().startsWith(ModConstants.MOD_ID)) {
            var location = OpenResourceLocation.parse(textureData.name() + ".png");
            return SkinRenderType.particle(location, true, true);
        }
        // this is a custom particle texture.
        return SmartTextureManager.getInstance().register(textureData).create(it -> {
            var location = it.location();
            return SkinRenderType.particle(location, true, true);
        });
    }

    private static class Renderer implements AbstractParticleRenderer {

        private int tintColor = -1;

        private Group group;
        private AbstractCamera camera;

        private OpenVector3f emitterGlobalPosition = OpenVector3f.ZERO;
        private OpenQuaternionf emitterGlobalRotation = OpenQuaternionf.ONE;

        private OpenVector3f initialGlobalPosition;
        private OpenQuaternionf initialGlobalRotation;

        private final IRenderType renderType;

        private Renderer(IRenderType renderType) {
            this.renderType = renderType;
        }

        @Override
        public void prepare(AbstractCamera camera, float partialTick, SmartParticleEmitter emitter) {
            this.group = new Group(renderType);
            this.camera = camera;

            // em.globalPos = bone.globalPos + bone.globalRot * em.localPos
            // em.globalRot = bone.globalRot * em.localRot
            this.emitterGlobalPosition = emitter.globalPositionAt(partialTick);
            this.emitterGlobalRotation = emitter.globalRotationAt(partialTick);
        }

        @Override
        public void submit(OpenVector3f position, OpenQuaternionf rotation, OpenSize2f size, OpenRectangle2f textureBox, ParticleCameraFacing cameraFacing) {
            // find the a anchored global position.
            var globalPosition = Objects.compactMap(initialGlobalPosition, emitterGlobalPosition);
            var globalRotation = Objects.compactMap(initialGlobalRotation, emitterGlobalRotation);

            // particle.globalPos = emitter.globalPos + emitter.globalRot * particle.localPos
            var pos = globalPosition.copy();
            pos.add(position.transforming(globalRotation));

            // particle.globalRot = emitter.globalRot * camera.lookAt(particle.globalPos) * particle.localRot
            var quat = globalRotation.copy();
            quat.multiply(getFacingRotation(cameraFacing, pos));
            quat.multiply(rotation);

            // merge all particles into a group.
            group.add(ParticleElement.newInstance(pos, quat, size, textureBox, tintColor, LightmapTexture.DEFAULT, OverlayTexture.NO_OVERLAY, renderType));
        }

        @Override
        public void flush(IGraphicsContext context) {
            // submit a group into scene graphics pipeline.
            context.draw(group);
        }

        @Override
        public void setTintColor(int tintColor) {
            this.tintColor = tintColor;
        }

        @Override
        public void setInitialPosition(OpenVector3f position) {
            this.initialGlobalPosition = position;
        }

        @Override
        public void setInitialRotation(OpenQuaternionf position) {
            this.initialGlobalRotation = position;
        }

        private OpenQuaternionf getFacingRotation(ParticleCameraFacing facing, OpenVector3f pos) {
            return switch (facing) {
                case ROTATE_XYZ -> camera.rotation();
                case ROTATE_Y -> {
                    var tmp = camera.rotation().copy();
                    tmp.setX(0);
                    tmp.setZ(0);
                    yield tmp;
                }
                case LOOKAT_XYZ -> camera.lookAt(pos);
                case LOOKAT_Y -> {
                    var tmp = pos.copy();
                    tmp.setY(camera.position().y());
                    yield camera.lookAt(tmp);
                }
                default -> OpenQuaternionf.identity();
            };
        }
    }

    private static class Generator implements SkinParticleGenerator {

        private final ParticleEmitterUpdater.Builder emitter = new ParticleEmitterUpdater.Builder();
        private final ParticleInstanceUpdater.Builder instance = new ParticleInstanceUpdater.Builder();

        private Generator(SkinParticleData provider) {
            var components = new ArrayList<>(provider.components());
            components.sort(Comparator.comparingInt(SkinParticleComponent::priority));
            for (var component : components) {
                component.compile(this);
            }
        }

        @Override
        public Expression compile(String value) throws Exception {
            return AnimationEngine.compile(value);
        }

        @Override
        public ParticleEmitterUpdater.Builder emitter() {
            return emitter;
        }

        @Override
        public ParticleInstanceUpdater.Builder instance() {
            return instance;
        }

        @Override
        public Registry registry() {
            return AbstractRegistryManager::getBlock;
        }

        public SmartParticleEmitter build(AbstractParticleRenderer renderer, ExecutionContext context) {
            return new SmartParticleEmitter(emitter.build(), instance.build(), renderer, context);
        }
    }

    private static class Group implements IGraphicsElement, IGraphicsRenderable {

        private final IRenderType type;
        private final ArrayList<ParticleElement> elements = new ArrayList<>();

        private Group(IRenderType type) {
            this.type = type;
        }

        public void add(ParticleElement element) {
            elements.add(element);
        }

        @Override
        public void prepare(IGraphicsContext context) {
            for (var element : elements) {
                element.prepare(context);
            }
        }

        @Override
        public void render(IPoseStack.Pose pose, IVertexConsumer builder) {
            for (var element : elements) {
                element.render(pose, builder);
            }
        }

        @Override
        public IRenderType renderType() {
            return type;
        }
    }
}
