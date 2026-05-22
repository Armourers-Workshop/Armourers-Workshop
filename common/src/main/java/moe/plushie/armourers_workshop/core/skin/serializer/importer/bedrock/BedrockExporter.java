package moe.plushie.armourers_workshop.core.skin.serializer.importer.bedrock;

import moe.plushie.armourers_workshop.core.skin.molang.MolangVirtualMachine;
import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.OptimizeContext;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleComponent;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleCurve;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleData;
import moe.plushie.armourers_workshop.core.skin.particle.SkinParticleEvent;
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
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.shape.EmitterCustomShape;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.shape.EmitterDiscShape;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.shape.EmitterEntityShape;
import moe.plushie.armourers_workshop.core.skin.particle.component.emitter.shape.EmitterPointShape;
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
import moe.plushie.armourers_workshop.core.skin.particle.math.EmitterShapeDirection;
import moe.plushie.armourers_workshop.core.skin.particle.math.EmitterSphereShape;
import moe.plushie.armourers_workshop.core.skin.particle.math.ParticleCameraFacing;
import moe.plushie.armourers_workshop.core.skin.particle.math.ParticleMaterial;
import moe.plushie.armourers_workshop.core.skin.texture.SkinTextureData;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenExpression;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;
import moe.plushie.armourers_workshop.init.ModConstants;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class BedrockExporter {

    protected final BedrockPack pack;
    protected final MolangVirtualMachine virtualMachine;

    public BedrockExporter(BedrockPack pack, MolangVirtualMachine virtualMachine) {
        this.pack = pack;
        this.virtualMachine = virtualMachine;
    }


    public SkinParticleData exportParticle(BedrockParticle particle) {
        var name = particle.name();
        var meterial = toParticleMaterial(particle.material());
        var texutre = toParticleTexture(particle.texture());

        var components = new ArrayList<SkinParticleComponent>();
        for (var component : particle.components().values()) {
            components.add(exportParticleComponent(component));
        }
        var curves = new LinkedHashMap<String, SkinParticleCurve>();
        for (var entry : particle.curves().entrySet()) {
            curves.put(entry.getKey(), exportParticleCurve(entry.getValue()));
        }
        var events = new LinkedHashMap<String, SkinParticleEvent>();
        for (var entry : particle.events().entrySet()) {
            events.put(entry.getKey(), exportParticleEvent(entry.getValue()));
        }
        return new SkinParticleData(name, meterial, texutre, curves, events, components);
    }

    protected SkinParticleCurve exportParticleCurve(BedrockCurve curve) {
        return switch (curve.type()) {
            case "linear" -> SkinParticleCurve.linear(convertToFloatExpression(curve.input()), convertToFloatExpression(curve.range()));
            case "catmull_rom" -> new SkinParticleCurve();
            case "bezier" -> new SkinParticleCurve();
            case "bezier_chain" -> new SkinParticleCurve();
            default -> throw new IllegalArgumentException("Unknown particle curve type: " + curve.type());
        };
    }

    protected SkinParticleEvent exportParticleEvent(BedrockEvent event) {
        return new SkinParticleEvent();
    }

    protected SkinParticleComponent exportParticleComponent(BedrockComponent component) {

        if (component instanceof BedrockComponent.EmitterInitialization comp) {
            var creation = convertToExpression(comp.creation());
            var update = convertToExpression(comp.update());
            return new EmitterInitialization(creation, update);
        }
        if (component instanceof BedrockComponent.EmitterLocalSpace comp) {
            var position = comp.isPosition();
            var rotation = comp.isRotation();
            var velocity = comp.isVelocity();
            return new EmitterInitialLocalSpace(position, rotation, velocity);
        }

        if (component instanceof BedrockComponent.EmitterSteadyRate comp) {
            var spawnRate = convertToFloatExpression(comp.spawnRate());
            var maxParticles = convertToIntExpression(comp.maxParticles());
            return new EmitterSteadyRate(spawnRate, maxParticles);
        }
        if (component instanceof BedrockComponent.EmitterInstantRate comp) {
            var particles = convertToIntExpression(comp.particles());
            return new EmitterInstantRate(particles);
        }
        if (component instanceof BedrockComponent.EmitterManualRate comp) {
            var maxParticles = convertToIntExpression(comp.maxParticles());
            return new EmitterManualRate(maxParticles);
        }


        if (component instanceof BedrockComponent.EmitterEventLifetime comp) {
            var creation = comp.creation();
            var expiration = comp.expiration();
            var timelineEvents = new LinkedHashMap<Float, List<String>>();
            var travelDistanceEvents = new LinkedHashMap<Float, List<String>>();
            var travelDistanceLoopEvents = comp.travelDistanceLoopEvents();
            comp.timelineEvents().forEach((key, value) -> timelineEvents.put(Float.parseFloat(key), value));
            comp.travelDistanceEvents().forEach((key, value) -> travelDistanceEvents.put(Float.parseFloat(key), value));
            return new EmitterEventLifetime(creation, expiration, timelineEvents, travelDistanceEvents, travelDistanceLoopEvents);
        }
        if (component instanceof BedrockComponent.EmitterExpressionLifetime comp) {
            var activation = convertToExpression(comp.activation());
            var expiration = convertToExpression(comp.expiration());
            return new EmitterExpressionLifetime(activation, expiration);
        }
        if (component instanceof BedrockComponent.EmitterLoopingLifetime comp) {
            var activeTime = convertToFloatExpression(comp.activeTime());
            var sleepTime = convertToFloatExpression(comp.sleepTime());
            return new EmitterLoopingLifetime(activeTime, sleepTime);
        }
        if (component instanceof BedrockComponent.EmitterOnceLifetime comp) {
            var activeTime = convertToFloatExpression(comp.activeTime());
            return new EmitterOnceLifetime(activeTime);
        }

        if (component instanceof BedrockComponent.EmitterBoxShape comp) {
            var offsetX = convertToFloatExpression(comp.offsetX());
            var offsetY = convertToFloatExpression(comp.offsetY());
            var offsetZ = convertToFloatExpression(comp.offsetZ());
            var width = convertToFloatExpression(comp.sizeWidth());
            var height = convertToFloatExpression(comp.sizeHeight());
            var depth = convertToFloatExpression(comp.sizeDepth());
            var surfaceOnly = comp.isSurfaceOnly();
            var direction = toParticleDirection(comp.direction());
            return new EmitterBoxShape(offsetX, offsetY, offsetZ, width, height, depth, direction, surfaceOnly);
        }
        if (component instanceof BedrockComponent.EmitterDiscShape comp) {
            var offsetX = convertToFloatExpression(comp.offsetX());
            var offsetY = convertToFloatExpression(comp.offsetY());
            var offsetZ = convertToFloatExpression(comp.offsetZ());
            var radius = convertToFloatExpression(comp.radius());
            var planeNormalX = convertToFloatExpression(comp.planeNormalX());
            var planeNormalY = convertToFloatExpression(comp.planeNormalY());
            var planeNormalZ = convertToFloatExpression(comp.planeNormalZ());
            var surfaceOnly = comp.isSurfaceOnly();
            var direction = toParticleDirection(comp.direction());
            return new EmitterDiscShape(offsetX, offsetY, offsetZ, radius, planeNormalX, planeNormalY, planeNormalZ, direction, surfaceOnly);
        }
        if (component instanceof BedrockComponent.EmitterEntityShape comp) {
            var offsetX = convertToFloatExpression(comp.offsetX());
            var offsetY = convertToFloatExpression(comp.offsetY());
            var offsetZ = convertToFloatExpression(comp.offsetZ());
            var surfaceOnly = comp.isSurfaceOnly();
            var direction = toParticleDirection(comp.direction());
            return new EmitterEntityShape(offsetX, offsetY, offsetZ, direction, surfaceOnly);
        }
        if (component instanceof BedrockComponent.EmitterPointShape comp) {
            var offsetX = convertToFloatExpression(comp.offsetX());
            var offsetY = convertToFloatExpression(comp.offsetY());
            var offsetZ = convertToFloatExpression(comp.offsetZ());
            var direction = toParticleDirection(comp.direction());
            return new EmitterPointShape(offsetX, offsetY, offsetZ, direction);
        }
        if (component instanceof BedrockComponent.EmitterSphereShape comp) {
            var offsetX = convertToFloatExpression(comp.offsetX());
            var offsetY = convertToFloatExpression(comp.offsetY());
            var offsetZ = convertToFloatExpression(comp.offsetZ());
            var radius = convertToFloatExpression(comp.radius());
            var surfaceOnly = comp.isSurfaceOnly();
            var direction = toParticleDirection(comp.direction());
            return new EmitterSphereShape(offsetX, offsetY, offsetZ, radius, direction, surfaceOnly);
        }
        if (component instanceof BedrockComponent.EmitterCustomShape comp) {
            var offsetX = convertToFloatExpression(comp.offsetX());
            var offsetY = convertToFloatExpression(comp.offsetY());
            var offsetZ = convertToFloatExpression(comp.offsetZ());
            var direction = toParticleDirection(comp.direction());
            return new EmitterCustomShape(offsetX, offsetY, offsetZ, direction);
        }

        if (component instanceof BedrockComponent.ParticleInitialization comp) {
            var update = convertToExpression(comp.update());
            var render = convertToExpression(comp.render());
            return new ParticleInitialization(update, render);
        }
        if (component instanceof BedrockComponent.ParticleInitialSpeed comp) {
            var speed = convertToFloatExpression(comp.speed());
            return new ParticleInitialSpeed(speed);
        }
        if (component instanceof BedrockComponent.ParticleInitialSpin comp) {
            var rotation = convertToFloatExpression(comp.rotation());
            var rotationRate = convertToFloatExpression(comp.rotationRate());
            return new ParticleInitialSpin(rotation, rotationRate);
        }

        if (component instanceof BedrockComponent.ParticleEventLifetime comp) {
            var creation = comp.creation();
            var expiration = comp.expiration();
            var timelineEvents = new LinkedHashMap<Float, List<String>>();
            comp.timelineEvents().forEach((key, value) -> timelineEvents.put(Float.parseFloat(key), value));
            return new ParticleEventLifetime(creation, expiration, timelineEvents);
        }
        if (component instanceof BedrockComponent.ParticleExpressLifetime comp) {
            var maxAge = convertToIntExpression(comp.maxAge());
            var expiration = convertToExpression(comp.expiration());
            return new ParticleExpressLifetime(maxAge, expiration);
        }
        if (component instanceof BedrockComponent.ParticleKillInBlocksLifetime comp) {
            var blocks = comp.blocks();
            return new ParticleKillInBlocksLifetime(blocks);
        }
        if (component instanceof BedrockComponent.ParticleKillInPlaneLifetime comp) {
            float a = comp.parameters().get(0);
            float b = comp.parameters().get(1);
            float c = comp.parameters().get(2);
            float d = comp.parameters().get(3);
            return new ParticleKillInPlaneLifetime(a, b, c, d);
        }
        if (component instanceof BedrockComponent.ParticleOnlyInBlocksLifetime comp) {
            var blocks = comp.blocks();
            return new ParticleOnlyInBlocksLifetime(blocks);
        }

        if (component instanceof BedrockComponent.ParticleCollisionMotion comp) {
            var enabled = convertToFloatExpression(comp.enabled());
            var collisionDrag = comp.collisionDrag();
            var collisionRadius = comp.collisionRadius();
            var coefficientOfRestitution = comp.coefficientOfRestitution();
            var expireOnContact = comp.isExpireOnContact();
            var events = comp.events();
            return new ParticleCollisionMotion(enabled, collisionDrag, collisionRadius, coefficientOfRestitution, expireOnContact, events);
        }
        if (component instanceof BedrockComponent.ParticleDynamicMotion comp) {
            var linearAccelerationX = convertToFloatExpression(comp.linearAccelerationX());
            var linearAccelerationY = convertToFloatExpression(comp.linearAccelerationY());
            var linearAccelerationZ = convertToFloatExpression(comp.linearAccelerationZ());
            var linearDragCoefficient = convertToFloatExpression(comp.linearDragCoefficient());
            var rotationAcceleration = convertToFloatExpression(comp.rotationAcceleration());
            var rotationDragCoefficient = convertToFloatExpression(comp.rotationDragCoefficient());
            return new ParticleDynamicMotion(linearAccelerationX, linearAccelerationY, linearAccelerationZ, linearDragCoefficient, rotationAcceleration, rotationDragCoefficient);
        }
        if (component instanceof BedrockComponent.ParticleParametricMotion comp) {
            var relativePositionX = convertToFloatExpression(comp.relativePositionX());
            var relativePositionY = convertToFloatExpression(comp.relativePositionY());
            var relativePositionZ = convertToFloatExpression(comp.relativePositionZ());
            var directionX = convertToFloatExpression(comp.directionX());
            var directionY = convertToFloatExpression(comp.directionY());
            var directionZ = convertToFloatExpression(comp.directionZ());
            var rotation = convertToFloatExpression(comp.rotation());
            return new ParticleParametricMotion(relativePositionX, relativePositionY, relativePositionZ, directionX, directionY, directionZ, rotation);
        }

        if (component instanceof BedrockComponent.ParticleLightingAppearance comp) {
            return new ParticleLightingAppearance();
        }
        if (component instanceof BedrockComponent.ParticleBillboardAppearance comp) {
            var width = convertToFloatExpression(comp.width());
            var height = convertToFloatExpression(comp.height());
            var facingCameraMode = toParticleFacing(comp.facingCameraMode());
            var textureSize = comp.textureSize();
            var textureCoordsX = convertToFloatExpression(comp.textureCoordsX());
            var textureCoordsY = convertToFloatExpression(comp.textureCoordsY());
            var textureCoordsWidth = convertToFloatExpression(comp.textureCoordsWidth());
            var textureCoordsHeight = convertToFloatExpression(comp.textureCoordsHeight());
            var stepX = convertToFloatExpression(comp.stepX());
            var stepY = convertToFloatExpression(comp.stepY());
            var flipbook = comp.flipbook();
            var fps = comp.fps();
            var maxFrame = convertToIntExpression(comp.maxFrame());
            var isStretchToLifetime = comp.isStretchToLifetime();
            var isLoop = comp.isLoop();
            return new ParticleBillboardAppearance(width, height, facingCameraMode, textureSize, textureCoordsX, textureCoordsY, textureCoordsWidth, textureCoordsHeight, stepX, stepY, flipbook, fps, maxFrame, isStretchToLifetime, isLoop);
        }
        if (component instanceof BedrockComponent.ParticleTintingAppearance comp) {
            var colors = comp.values();
            if (!colors.isEmpty()) {
                var red = convertToFloatExpression(colors.get(0));
                var green = convertToFloatExpression(colors.get(1));
                var blue = convertToFloatExpression(colors.get(2));
                var alpha = convertToFloatExpression(colors.get(3));
                return new ParticleTintingAppearance(red, green, blue, alpha);
            }
            var interpolation = convertToFloatExpression(comp.interpolation());
            var gradientColors = new LinkedHashMap<Float, Integer>();
            comp.gradientValues().forEach((key, value) -> gradientColors.put(Float.parseFloat(key), Long.decode(value).intValue()));
            return new ParticleTintingAppearance(interpolation, gradientColors);
        }

        throw new RuntimeException("can't parse particle component!!");
    }

    private ParticleMaterial toParticleMaterial(String material) {
        return switch (material) {
            case "particles_alpha" -> ParticleMaterial.ALPHA;
            case "particles_add" -> ParticleMaterial.ADDITIVE;
            case "particles_blend" -> ParticleMaterial.BLEND;
            case "particles_opaque" -> ParticleMaterial.OPAQUE;
            default -> throw new IllegalArgumentException("Unknown particle material: " + material);
        };
    }

    private SkinTextureData toParticleTexture(String texture) {
        return switch (texture) {
            case "textures/particle/particles" -> createBuiltinTexture("textures/particle/particles", 128, 128);
            case "textures/particle/campfire_smoke" -> createBuiltinTexture("textures/particle/campfire_smoke", 16, 192);
            case "textures/particle/flame_atlas" -> createBuiltinTexture("textures/particle/flame_atlas", 16, 512);
            case "textures/particle/soul" -> createBuiltinTexture("textures/particle/soul", 16, 176);
            default -> throw new IllegalArgumentException("Unknown particle texture: " + texture);
        };
    }

    private EmitterShapeDirection toParticleDirection(Object direction) {
        if (Objects.equals(direction, "inwards")) {
            return EmitterShapeDirection.outwards();
        }
        if (Objects.equals(direction, "outwards")) {
            return EmitterShapeDirection.outwards();
        }
        if (direction instanceof List<?> value) {
            var x = convertToFloatExpression((OpenExpression) value.get(0));
            var y = convertToFloatExpression((OpenExpression) value.get(1));
            var z = convertToFloatExpression((OpenExpression) value.get(2));
            return EmitterShapeDirection.custom(x, y, z);
        }
        throw new IllegalArgumentException("unknown particle shape direction: " + direction);
    }

    private ParticleCameraFacing toParticleFacing(String name) {
        return switch (name) {
            case "rotate_xyz" -> ParticleCameraFacing.ROTATE_XYZ;
            case "rotate_y" -> ParticleCameraFacing.ROTATE_Y;
            case "lookat_xyz" -> ParticleCameraFacing.LOOKAT_XYZ;
            case "lookat_y" -> ParticleCameraFacing.LOOKAT_Y;
            case "lookat_direction" -> ParticleCameraFacing.LOOKAT_DIRECTION;
            case "direction_x" -> ParticleCameraFacing.DIRECTION_X;
            case "direction_y" -> ParticleCameraFacing.DIRECTION_Y;
            case "direction_z" -> ParticleCameraFacing.DIRECTION_Z;
            case "emitter_transform_xy" -> ParticleCameraFacing.EMITTER_TRANSFORM_XY;
            case "emitter_transform_xz" -> ParticleCameraFacing.EMITTER_TRANSFORM_XZ;
            case "emitter_transform_yz" -> ParticleCameraFacing.EMITTER_TRANSFORM_YZ;
            default -> ParticleCameraFacing.ROTATE_XYZ; // unknown.
        };
    }

    private OpenPrimitive convertToExpression(OpenExpression value) {
        var expr = compileExpression(value);
        if (expr != null && expr.isMutable()) {
            return OpenPrimitive.of(value.expression());
        }
        return OpenPrimitive.NULL;
    }

    private OpenPrimitive convertToIntExpression(OpenExpression value) {
        var expr = compileExpression(value);
        if (expr != null && expr.isMutable()) {
            return OpenPrimitive.of(value.expression());
        }
        if (expr != null) {
            return OpenPrimitive.of(expr.evaluate(OptimizeContext.DEFAULT).intValue());
        }
        return OpenPrimitive.NULL;
    }

    private OpenPrimitive convertToFloatExpression(OpenExpression value) {
        var expr = compileExpression(value);
        if (expr != null && expr.isMutable()) {
            return OpenPrimitive.of(value.expression());
        }
        if (expr != null) {
            return OpenPrimitive.of((float) expr.compute(OptimizeContext.DEFAULT));
        }
        return OpenPrimitive.NULL;
    }

    private Expression compileExpression(OpenExpression value) {
        try {
            if (value == null) {
                return null;
            }
            var expr = value.expression();
            if (!expr.isEmpty()) {
                return virtualMachine.compile(value.expression());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private SkinTextureData createBuiltinTexture(String texture, int width, int height) {
        return new SkinTextureData(ModConstants.key(texture).toString(), width, height);
    }
}
