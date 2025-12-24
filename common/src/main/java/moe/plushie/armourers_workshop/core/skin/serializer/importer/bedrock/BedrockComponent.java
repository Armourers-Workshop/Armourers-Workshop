package moe.plushie.armourers_workshop.core.skin.serializer.importer.bedrock;

import moe.plushie.armourers_workshop.core.math.OpenSize2i;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.OpenExpression;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BedrockComponent {

    protected static class EmitterInitialization extends BedrockComponent {

        private final OpenExpression creation;
        private final OpenExpression update;

        public EmitterInitialization(OpenExpression creation, OpenExpression update) {
            this.creation = creation;
            this.update = update;
        }

        public OpenExpression creation() {
            return creation;
        }

        public OpenExpression update() {
            return update;
        }

        protected static class Builder extends BedrockComponent.Builder {

            private OpenExpression creation;
            private OpenExpression update;

            public void creation(OpenExpression creation) {
                this.creation = creation;
            }

            public void update(OpenExpression update) {
                this.update = update;
            }

            @Override
            public BedrockComponent build() {
                return new EmitterInitialization(creation, update);
            }
        }
    }

    protected static class EmitterLocalSpace extends BedrockComponent {

        private final boolean position;
        private final boolean rotation;
        private final boolean velocity;

        public EmitterLocalSpace(boolean position, boolean rotation, boolean velocity) {
            this.position = position;
            this.rotation = rotation;
            this.velocity = velocity;
        }

        public boolean isPosition() {
            return position;
        }

        public boolean isRotation() {
            return rotation;
        }

        public boolean isVelocity() {
            return velocity;
        }

        protected static class Builder extends BedrockComponent.Builder {

            boolean position = false;
            boolean rotation = false;
            boolean velocity = false;

            public void position(boolean position) {
                this.position = position;
            }

            public void rotation(boolean rotation) {
                this.rotation = rotation;
            }

            public void velocity(boolean velocity) {
                this.velocity = velocity;
            }

            @Override
            public BedrockComponent build() {
                return new EmitterLocalSpace(position, rotation, velocity);
            }
        }
    }

    protected static class EmitterSteadyRate extends BedrockComponent {

        private final OpenExpression spawnRate;
        private final OpenExpression maxParticles;

        public EmitterSteadyRate(OpenExpression spawnRate, OpenExpression maxParticles) {
            this.spawnRate = spawnRate;
            this.maxParticles = maxParticles;
        }

        public OpenExpression spawnRate() {
            return spawnRate;
        }

        public OpenExpression maxParticles() {
            return maxParticles;
        }

        protected static class Builder extends BedrockComponent.Builder {

            OpenExpression spawnRate;
            OpenExpression maxParticles;

            public void spawnRate(OpenExpression spawnRate) {
                this.spawnRate = spawnRate;
            }

            public void maxParticles(OpenExpression maxParticles) {
                this.maxParticles = maxParticles;
            }

            @Override
            public BedrockComponent build() {
                return new EmitterSteadyRate(spawnRate, maxParticles);
            }
        }
    }

    protected static class EmitterInstantRate extends BedrockComponent {

        private final OpenExpression particles;

        public EmitterInstantRate(OpenExpression particles) {
            this.particles = particles;
        }

        public OpenExpression particles() {
            return particles;
        }

        protected static class Builder extends BedrockComponent.Builder {

            OpenExpression particles;

            public void particles(OpenExpression particles) {
                this.particles = particles;
            }

            @Override
            public BedrockComponent build() {
                return new EmitterInstantRate(particles);
            }
        }
    }

    protected static class EmitterManualRate extends BedrockComponent {

        private final OpenExpression maxParticles;

        public EmitterManualRate(OpenExpression maxParticles) {
            this.maxParticles = maxParticles;
        }

        public OpenExpression maxParticles() {
            return maxParticles;
        }

        protected static class Builder extends BedrockComponent.Builder {

            OpenExpression maxParticles;

            public void maxParticles(OpenExpression maxParticles) {
                this.maxParticles = maxParticles;
            }

            @Override
            public BedrockComponent build() {
                return new EmitterManualRate(maxParticles);
            }
        }
    }

    protected static class EmitterEventLifetime extends BedrockComponent {

        private final List<String> creation;
        private final List<String> expiration;

        private final Map<String, List<String>> timelineEvents;
        private final Map<String, List<String>> travelDistanceEvents;
        private final Map<Float, List<String>> travelDistanceLoopEvents;

        public List<String> creation() {
            return creation;
        }

        public List<String> expiration() {
            return expiration;
        }

        public Map<String, List<String>> timelineEvents() {
            return timelineEvents;
        }

        public Map<String, List<String>> travelDistanceEvents() {
            return travelDistanceEvents;
        }

        public Map<Float, List<String>> travelDistanceLoopEvents() {
            return travelDistanceLoopEvents;
        }

        public EmitterEventLifetime(List<String> creation, List<String> expiration, Map<String, List<String>> timelineEvents, Map<String, List<String>> travelDistanceEvents, Map<Float, List<String>> travelDistanceLoopEvents) {
            this.creation = creation;
            this.expiration = expiration;
            this.timelineEvents = timelineEvents;
            this.travelDistanceEvents = travelDistanceEvents;
            this.travelDistanceLoopEvents = travelDistanceLoopEvents;
        }

        protected static class Builder extends BedrockComponent.Builder {

            List<String> creation = Collections.emptyList();
            List<String> expiration = Collections.emptyList();

            Map<String, List<String>> timelineEvents = new LinkedHashMap<>();
            Map<String, List<String>> travelDistanceEvents = new LinkedHashMap<>();
            Map<Float, List<String>> travelDistanceLoopEvents = new LinkedHashMap<>();


            public void creation(List<String> creation) {
                this.creation = creation;
            }

            public void expiration(List<String> expiration) {
                this.creation = expiration;
            }

            public void timeline(String atTime, List<String> events) {
                this.timelineEvents.put(atTime, events);
            }

            public void travelDistance(String atTime, List<String> events) {
                this.travelDistanceEvents.put(atTime, events);
            }

            public void travelDistanceLoop(float atTime, List<String> events) {
                this.travelDistanceLoopEvents.put(atTime, events);
            }

            @Override
            public BedrockComponent build() {
                return new EmitterEventLifetime(creation, expiration, timelineEvents, travelDistanceEvents, travelDistanceLoopEvents);
            }
        }
    }

    protected static class EmitterLoopingLifetime extends BedrockComponent {

        private final OpenExpression activeTime;
        private final OpenExpression sleepTime;

        public EmitterLoopingLifetime(OpenExpression activeTime, OpenExpression sleepTime) {
            this.activeTime = activeTime;
            this.sleepTime = sleepTime;
        }

        public OpenExpression activeTime() {
            return activeTime;
        }

        public OpenExpression sleepTime() {
            return sleepTime;
        }

        protected static class Builder extends BedrockComponent.Builder {

            OpenExpression activeTime;
            OpenExpression sleepTime;

            public void activeTime(OpenExpression activeTime) {
                this.activeTime = activeTime;
            }

            public void sleepTime(OpenExpression sleepTime) {
                this.sleepTime = sleepTime;
            }


            @Override
            public BedrockComponent build() {
                return new EmitterLoopingLifetime(activeTime, sleepTime);
            }
        }
    }

    protected static class EmitterOnceLifetime extends BedrockComponent {

        private final OpenExpression activeTime;

        public EmitterOnceLifetime(OpenExpression activeTime) {
            this.activeTime = activeTime;
        }

        public OpenExpression activeTime() {
            return activeTime;
        }

        protected static class Builder extends BedrockComponent.Builder {

            OpenExpression activeTime;

            public void activeTime(OpenExpression activeTime) {
                this.activeTime = activeTime;
            }

            @Override
            public BedrockComponent build() {
                return new EmitterOnceLifetime(activeTime);
            }
        }
    }

    protected static class EmitterExpressionLifetime extends BedrockComponent {

        private final OpenExpression activation;
        private final OpenExpression expiration;

        public EmitterExpressionLifetime(OpenExpression activation, OpenExpression expiration) {
            this.activation = activation;
            this.expiration = expiration;
        }

        public OpenExpression activation() {
            return activation;
        }

        public OpenExpression expiration() {
            return expiration;
        }

        protected static class Builder extends BedrockComponent.Builder {

            OpenExpression activation;
            OpenExpression expiration;

            public void activation(OpenExpression activation) {
                this.activation = activation;
            }

            public void expiration(OpenExpression expiration) {
                this.expiration = expiration;
            }

            @Override
            public BedrockComponent build() {
                return new EmitterExpressionLifetime(activation, expiration);
            }
        }
    }

    protected static class EmitterPointShape extends BedrockComponent {

        private final OpenExpression offsetX;
        private final OpenExpression offsetY;
        private final OpenExpression offsetZ;
        private final Object direction;

        public EmitterPointShape(OpenExpression offsetX, OpenExpression offsetY, OpenExpression offsetZ, Object direction) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            this.direction = direction;
        }

        public OpenExpression offsetX() {
            return offsetX;
        }

        public OpenExpression offsetY() {
            return offsetY;
        }

        public OpenExpression offsetZ() {
            return offsetZ;
        }

        public Object direction() {
            return direction;
        }

        protected static class Builder extends ShapeBuilder {

            @Override
            public BedrockComponent build() {
                return new EmitterPointShape(offsetX, offsetY, offsetZ, direction);
            }
        }
    }

    protected static class EmitterSphereShape extends BedrockComponent {

        private final OpenExpression offsetX;
        private final OpenExpression offsetY;
        private final OpenExpression offsetZ;

        private final OpenExpression radius;
        private final boolean surfaceOnly;
        private final Object direction;

        public EmitterSphereShape(OpenExpression offsetX, OpenExpression offsetY, OpenExpression offsetZ, OpenExpression radius, boolean surfaceOnly, Object direction) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            this.radius = radius;
            this.surfaceOnly = surfaceOnly;
            this.direction = direction;
        }

        public OpenExpression offsetX() {
            return offsetX;
        }

        public OpenExpression offsetY() {
            return offsetY;
        }

        public OpenExpression offsetZ() {
            return offsetZ;
        }

        public OpenExpression radius() {
            return radius;
        }

        public boolean isSurfaceOnly() {
            return surfaceOnly;
        }

        public Object direction() {
            return direction;
        }

        protected static class Builder extends ShapeBuilder {

            @Override
            public BedrockComponent build() {
                return new EmitterSphereShape(offsetX, offsetY, offsetZ, radius, surfaceOnly, direction);
            }
        }
    }

    protected static class EmitterBoxShape extends BedrockComponent {

        private final OpenExpression offsetX;
        private final OpenExpression offsetY;
        private final OpenExpression offsetZ;

        private final OpenExpression sizeWidth;
        private final OpenExpression sizeHeight;
        private final OpenExpression sizeDepth;

        private final boolean surfaceOnly;
        private final Object direction;

        public EmitterBoxShape(OpenExpression offsetX, OpenExpression offsetY, OpenExpression offsetZ, OpenExpression sizeWidth, OpenExpression sizeHeight, OpenExpression sizeDepth, boolean surfaceOnly, Object direction) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            this.sizeWidth = sizeWidth;
            this.sizeHeight = sizeHeight;
            this.sizeDepth = sizeDepth;
            this.surfaceOnly = surfaceOnly;
            this.direction = direction;
        }

        public OpenExpression offsetX() {
            return offsetX;
        }

        public OpenExpression offsetY() {
            return offsetY;
        }

        public OpenExpression offsetZ() {
            return offsetZ;
        }

        public OpenExpression sizeWidth() {
            return sizeWidth;
        }

        public OpenExpression sizeHeight() {
            return sizeHeight;
        }

        public OpenExpression sizeDepth() {
            return sizeDepth;
        }

        public boolean isSurfaceOnly() {
            return surfaceOnly;
        }

        public Object direction() {
            return direction;
        }

        protected static class Builder extends ShapeBuilder {

            private OpenExpression sizeWidth;
            private OpenExpression sizeHeight;
            private OpenExpression sizeDepth;

            public void width(OpenExpression sizeWidth) {
                this.sizeWidth = sizeWidth;
            }

            public void height(OpenExpression sizeHeight) {
                this.sizeHeight = sizeHeight;
            }

            public void depth(OpenExpression sizeDepth) {
                this.sizeDepth = sizeDepth;
            }

            @Override
            public BedrockComponent build() {
                return new EmitterBoxShape(offsetX, offsetY, offsetZ, sizeWidth, sizeHeight, sizeDepth, surfaceOnly, direction);
            }
        }
    }

    protected static class EmitterDiscShape extends BedrockComponent {

        private final OpenExpression offsetX;
        private final OpenExpression offsetY;
        private final OpenExpression offsetZ;

        private final OpenExpression radius;

        private final OpenExpression planeNormalX;
        private final OpenExpression planeNormalY;
        private final OpenExpression planeNormalZ;

        private final boolean surfaceOnly;
        private final Object direction;

        public EmitterDiscShape(OpenExpression offsetX, OpenExpression offsetY, OpenExpression offsetZ, OpenExpression radius, OpenExpression planeNormalX, OpenExpression planeNormalY, OpenExpression planeNormalZ, boolean surfaceOnly, Object direction) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            this.radius = radius;
            this.planeNormalX = planeNormalX;
            this.planeNormalY = planeNormalY;
            this.planeNormalZ = planeNormalZ;
            this.surfaceOnly = surfaceOnly;
            this.direction = direction;
        }

        public OpenExpression offsetX() {
            return offsetX;
        }

        public OpenExpression offsetY() {
            return offsetY;
        }

        public OpenExpression offsetZ() {
            return offsetZ;
        }

        public OpenExpression radius() {
            return radius;
        }

        public OpenExpression planeNormalX() {
            return planeNormalX;
        }

        public OpenExpression planeNormalY() {
            return planeNormalY;
        }

        public OpenExpression planeNormalZ() {
            return planeNormalZ;
        }

        public boolean isSurfaceOnly() {
            return surfaceOnly;
        }

        public Object direction() {
            return direction;
        }

        protected static class Builder extends ShapeBuilder {

            private OpenExpression planeNormalX;
            private OpenExpression planeNormalY;
            private OpenExpression planeNormalZ;

            public void planeNormalX(OpenExpression planeNormalX) {
                this.planeNormalX = planeNormalX;
            }

            public void planeNormalY(OpenExpression planeNormalY) {
                this.planeNormalY = planeNormalY;
            }

            public void planeNormalZ(OpenExpression planeNormalZ) {
                this.planeNormalZ = planeNormalZ;
            }

            @Override
            public BedrockComponent build() {
                return new EmitterDiscShape(offsetX, offsetY, offsetZ, radius, planeNormalX, planeNormalY, planeNormalZ, surfaceOnly, direction);
            }
        }
    }

    protected static class EmitterEntityShape extends BedrockComponent {

        private final OpenExpression offsetX;
        private final OpenExpression offsetY;
        private final OpenExpression offsetZ;

        private final boolean surfaceOnly;
        private final Object direction;

        public EmitterEntityShape(OpenExpression offsetX, OpenExpression offsetY, OpenExpression offsetZ, boolean surfaceOnly, Object direction) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            this.surfaceOnly = surfaceOnly;
            this.direction = direction;
        }

        public OpenExpression offsetX() {
            return offsetX;
        }

        public OpenExpression offsetY() {
            return offsetY;
        }

        public OpenExpression offsetZ() {
            return offsetZ;
        }

        public boolean isSurfaceOnly() {
            return surfaceOnly;
        }

        public Object direction() {
            return direction;
        }

        protected static class Builder extends ShapeBuilder {

            @Override
            public BedrockComponent build() {
                return new EmitterEntityShape(offsetX, offsetY, offsetZ, surfaceOnly, direction);
            }
        }
    }

    protected static class EmitterCustomShape extends BedrockComponent {

        private final OpenExpression offsetX;
        private final OpenExpression offsetY;
        private final OpenExpression offsetZ;

        private final Object direction;

        public EmitterCustomShape(OpenExpression offsetX, OpenExpression offsetY, OpenExpression offsetZ, Object direction) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            this.direction = direction;
        }

        public OpenExpression offsetX() {
            return offsetX;
        }

        public OpenExpression offsetY() {
            return offsetY;
        }

        public OpenExpression offsetZ() {
            return offsetZ;
        }

        public Object direction() {
            return direction;
        }

        protected static class Builder extends ShapeBuilder {

            @Override
            public BedrockComponent build() {
                return new EmitterCustomShape(offsetX, offsetY, offsetZ, direction);
            }
        }
    }

    protected static class ParticleInitialization extends BedrockComponent {

        private final OpenExpression update;
        private final OpenExpression render;

        public ParticleInitialization(OpenExpression update, OpenExpression render) {
            this.update = update;
            this.render = render;
        }

        public OpenExpression update() {
            return update;
        }

        public OpenExpression render() {
            return render;
        }

        protected static class Builder extends BedrockComponent.Builder {

            OpenExpression update;
            OpenExpression render;

            public void update(OpenExpression update) {
                this.update = update;
            }

            public void render(OpenExpression render) {
                this.render = render;
            }

            @Override
            public BedrockComponent build() {
                return new ParticleInitialization(update, render);
            }
        }
    }

    protected static class ParticleInitialSpeed extends BedrockComponent {

        private final OpenExpression speed;

        public ParticleInitialSpeed(OpenExpression speed) {
            this.speed = speed;
        }

        public OpenExpression speed() {
            return speed;
        }

        protected static class Builder extends BedrockComponent.Builder {

            OpenExpression speed;

            public void speed(OpenExpression speed) {
                this.speed = speed;
            }


            @Override
            public BedrockComponent build() {
                return new ParticleInitialSpeed(speed);
            }
        }

    }

    protected static class ParticleInitialSpin extends BedrockComponent {

        private final OpenExpression rotation;
        private final OpenExpression rotationRate;

        public ParticleInitialSpin(OpenExpression rotation, OpenExpression rotationRate) {
            this.rotation = rotation;
            this.rotationRate = rotationRate;
        }

        public OpenExpression rotation() {
            return rotation;
        }

        public OpenExpression rotationRate() {
            return rotationRate;
        }

        protected static class Builder extends BedrockComponent.Builder {

            OpenExpression rotation;
            OpenExpression rotationRate;

            public void rotation(OpenExpression rotation) {
                this.rotation = rotation;
            }

            public void rotationRate(OpenExpression rotationRate) {
                this.rotationRate = rotationRate;
            }

            @Override
            public BedrockComponent build() {
                return new ParticleInitialSpin(rotation, rotationRate);
            }
        }

    }

    protected static class ParticleEventLifetime extends BedrockComponent {
        private final List<String> creation;
        private final List<String> expiration;
        private final Map<String, List<String>> timelineEvents;

        public ParticleEventLifetime(List<String> creation, List<String> expiration, Map<String, List<String>> timelineEvents) {
            this.creation = creation;
            this.expiration = expiration;
            this.timelineEvents = timelineEvents;
        }

        public List<String> creation() {
            return creation;
        }

        public List<String> expiration() {
            return expiration;
        }

        public Map<String, List<String>> timelineEvents() {
            return timelineEvents;
        }

        protected static class Builder extends BedrockComponent.Builder {

            List<String> creation = Collections.emptyList();
            List<String> expiration = Collections.emptyList();
            Map<String, List<String>> timelineEvents = new LinkedHashMap<>();

            public void creation(List<String> creation) {
                this.creation = creation;
            }

            public void expiration(List<String> expiration) {
                this.creation = expiration;
            }

            public void timeline(String atTime, List<String> events) {
                this.timelineEvents.put(atTime, events);
            }

            @Override
            public BedrockComponent build() {
                return new ParticleEventLifetime(creation, expiration, timelineEvents);
            }
        }
    }

    protected static class ParticleExpressLifetime extends BedrockComponent {

        private final OpenExpression maxAge;
        private final OpenExpression expiration;

        public ParticleExpressLifetime(OpenExpression maxAge, OpenExpression expiration) {
            this.maxAge = maxAge;
            this.expiration = expiration;
        }

        public OpenExpression maxAge() {
            return maxAge;
        }

        public OpenExpression expiration() {
            return expiration;
        }

        protected static class Builder extends BedrockComponent.Builder {

            OpenExpression maxAge;
            OpenExpression expiration;

            public void maxAge(OpenExpression maxAge) {
                this.maxAge = maxAge;
            }

            public void expiration(OpenExpression expiration) {
                this.expiration = expiration;
            }

            @Override
            public BedrockComponent build() {
                return new ParticleExpressLifetime(maxAge, expiration);
            }
        }
    }

    protected static class ParticleKillInPlaneLifetime extends BedrockComponent {

        private final List<Float> parameters;

        public ParticleKillInPlaneLifetime(List<Float> parameters) {
            this.parameters = parameters;
        }

        public List<Float> parameters() {
            return parameters;
        }

        protected static class Builder extends BedrockComponent.Builder {

            List<Float> parameters = new ArrayList<>();

            public void add(float value) {
                this.parameters.add(value);
            }

            @Override
            public BedrockComponent build() {
                return new ParticleKillInPlaneLifetime(parameters);
            }
        }
    }

    protected static class ParticleKillInBlocksLifetime extends BedrockComponent {

        private final List<String> blocks;

        public ParticleKillInBlocksLifetime(List<String> blocks) {
            this.blocks = blocks;
        }

        public List<String> blocks() {
            return blocks;
        }

        protected static class Builder extends BedrockComponent.Builder {

            List<String> blocks = new ArrayList<>();

            public void add(String block) {
                this.blocks.add(block);
            }

            @Override
            public BedrockComponent build() {
                return new ParticleKillInBlocksLifetime(blocks);
            }
        }
    }

    protected static class ParticleOnlyInBlocksLifetime extends BedrockComponent {

        private final List<String> blocks;

        public ParticleOnlyInBlocksLifetime(List<String> blocks) {
            this.blocks = blocks;
        }

        public List<String> blocks() {
            return blocks;
        }

        protected static class Builder extends BedrockComponent.Builder {

            List<String> blocks = new ArrayList<>();

            public void add(String block) {
                this.blocks.add(block);
            }

            @Override
            public BedrockComponent build() {
                return new ParticleOnlyInBlocksLifetime(blocks);
            }
        }
    }

    protected static class ParticleCollisionMotion extends BedrockComponent {

        private final OpenExpression enabled;

        private final float collisionDrag;
        private final float collisionRadius;
        private final float coefficientOfRestitution;

        private final boolean expireOnContact;

        private final Map<Float, String> events;

        public ParticleCollisionMotion(OpenExpression enabled, float collisionDrag, float collisionRadius, float coefficientOfRestitution, boolean expireOnContact, Map<Float, String> events) {
            this.enabled = enabled;
            this.collisionDrag = collisionDrag;
            this.collisionRadius = collisionRadius;
            this.coefficientOfRestitution = coefficientOfRestitution;
            this.expireOnContact = expireOnContact;
            this.events = events;
        }

        public OpenExpression enabled() {
            return enabled;
        }

        public float collisionDrag() {
            return collisionDrag;
        }

        public float collisionRadius() {
            return collisionRadius;
        }

        public float coefficientOfRestitution() {
            return coefficientOfRestitution;
        }

        public boolean isExpireOnContact() {
            return expireOnContact;
        }

        public Map<Float, String> events() {
            return events;
        }

        protected static class Builder extends BedrockComponent.Builder {

            OpenExpression enabled;

            float collisionDrag = 0.0f;
            float collisionRadius = 0.01f;
            float coefficientOfRestitution = 1.0f;

            boolean expireOnContact = false;

            Map<Float, String> events = new LinkedHashMap<>();


            public void enabled(OpenExpression enabled) {
                this.enabled = enabled;
            }

            public void collisionDrag(float collisionDrag) {
                this.collisionDrag = collisionDrag;
            }

            public void collisionRadius(float collisionRadius) {
                this.collisionRadius = collisionRadius;
            }

            public void coefficientOfRestitution(float coefficientOfRestitution) {
                this.coefficientOfRestitution = coefficientOfRestitution;
            }

            public void expireOnContact(boolean expireOnContact) {
                this.expireOnContact = expireOnContact;
            }

            public void event(float minSpeed, String event) {
                this.events.put(minSpeed, event);
            }

            @Override
            public BedrockComponent build() {
                return new ParticleCollisionMotion(enabled, collisionDrag, collisionRadius, coefficientOfRestitution, expireOnContact, events);
            }
        }
    }

    protected static class ParticleDynamicMotion extends BedrockComponent {

        private final OpenExpression linearAccelerationX;
        private final OpenExpression linearAccelerationY;
        private final OpenExpression linearAccelerationZ;

        private final OpenExpression linearDragCoefficient;
        private final OpenExpression rotationAcceleration;
        private final OpenExpression rotationDragCoefficient;

        public ParticleDynamicMotion(OpenExpression linearAccelerationX, OpenExpression linearAccelerationY, OpenExpression linearAccelerationZ, OpenExpression linearDragCoefficient, OpenExpression rotationAcceleration, OpenExpression rotationDragCoefficient) {
            this.linearAccelerationX = linearAccelerationX;
            this.linearAccelerationY = linearAccelerationY;
            this.linearAccelerationZ = linearAccelerationZ;
            this.linearDragCoefficient = linearDragCoefficient;
            this.rotationAcceleration = rotationAcceleration;
            this.rotationDragCoefficient = rotationDragCoefficient;
        }

        public OpenExpression linearAccelerationX() {
            return linearAccelerationX;
        }

        public OpenExpression linearAccelerationY() {
            return linearAccelerationY;
        }

        public OpenExpression linearAccelerationZ() {
            return linearAccelerationZ;
        }

        public OpenExpression linearDragCoefficient() {
            return linearDragCoefficient;
        }

        public OpenExpression rotationAcceleration() {
            return rotationAcceleration;
        }

        public OpenExpression rotationDragCoefficient() {
            return rotationDragCoefficient;
        }

        protected static class Builder extends BedrockComponent.Builder {

            OpenExpression linearAccelerationX;
            OpenExpression linearAccelerationY;
            OpenExpression linearAccelerationZ;

            OpenExpression linearDragCoefficient;
            OpenExpression rotationAcceleration;
            OpenExpression rotationDragCoefficient;

            public void linearAccelerationX(OpenExpression linearAccelerationX) {
                this.linearAccelerationX = linearAccelerationX;
            }

            public void linearAccelerationY(OpenExpression linearAccelerationY) {
                this.linearAccelerationY = linearAccelerationY;
            }

            public void linearAccelerationZ(OpenExpression linearAccelerationZ) {
                this.linearAccelerationZ = linearAccelerationZ;
            }

            public void linearDragCoefficient(OpenExpression linearDragCoefficient) {
                this.linearDragCoefficient = linearDragCoefficient;
            }

            public void rotationAcceleration(OpenExpression rotationAcceleration) {
                this.rotationAcceleration = rotationAcceleration;
            }

            public void rotationDragCoefficient(OpenExpression rotationDragCoefficient) {
                this.rotationDragCoefficient = rotationDragCoefficient;
            }

            @Override
            public BedrockComponent build() {
                return new ParticleDynamicMotion(linearAccelerationX, linearAccelerationY, linearAccelerationZ, linearDragCoefficient, rotationAcceleration, rotationDragCoefficient);
            }
        }
    }

    protected static class ParticleParametricMotion extends BedrockComponent {

        private final OpenExpression relativePositionX;
        private final OpenExpression relativePositionY;
        private final OpenExpression relativePositionZ;

        private final OpenExpression directionX;
        private final OpenExpression directionY;
        private final OpenExpression directionZ;

        private final OpenExpression rotation;

        public ParticleParametricMotion(OpenExpression relativePositionX, OpenExpression relativePositionY, OpenExpression relativePositionZ, OpenExpression directionX, OpenExpression directionY, OpenExpression directionZ, OpenExpression rotation) {
            this.relativePositionX = relativePositionX;
            this.relativePositionY = relativePositionY;
            this.relativePositionZ = relativePositionZ;
            this.directionX = directionX;
            this.directionY = directionY;
            this.directionZ = directionZ;
            this.rotation = rotation;
        }

        public OpenExpression relativePositionX() {
            return relativePositionX;
        }

        public OpenExpression relativePositionY() {
            return relativePositionY;
        }

        public OpenExpression relativePositionZ() {
            return relativePositionZ;
        }

        public OpenExpression directionX() {
            return directionX;
        }

        public OpenExpression directionY() {
            return directionY;
        }

        public OpenExpression directionZ() {
            return directionZ;
        }

        public OpenExpression rotation() {
            return rotation;
        }

        protected static class Builder extends BedrockComponent.Builder {

            OpenExpression relativePositionX;
            OpenExpression relativePositionY;
            OpenExpression relativePositionZ;

            OpenExpression directionX;
            OpenExpression directionY;
            OpenExpression directionZ;

            OpenExpression rotation;

            public void relativePositionX(OpenExpression relativePositionX) {
                this.relativePositionX = relativePositionX;
            }

            public void relativePositionY(OpenExpression relativePositionY) {
                this.relativePositionY = relativePositionY;
            }

            public void relativePositionZ(OpenExpression relativePositionZ) {
                this.relativePositionZ = relativePositionZ;
            }


            public void directionX(OpenExpression directionX) {
                this.directionX = directionX;
            }

            public void directionY(OpenExpression directionY) {
                this.directionY = directionY;
            }

            public void directionZ(OpenExpression directionZ) {
                this.directionZ = directionZ;
            }

            public void rotation(OpenExpression rotation) {
                this.rotation = rotation;
            }

            @Override
            public BedrockComponent build() {
                return new ParticleParametricMotion(relativePositionX, relativePositionY, relativePositionZ, directionX, directionY, directionZ, rotation);
            }
        }
    }

    protected static class ParticleLightingAppearance extends BedrockComponent {

        protected static class Builder extends BedrockComponent.Builder {

            @Override
            public BedrockComponent build() {
                return new ParticleLightingAppearance();
            }
        }
    }

    protected static class ParticleBillboardAppearance extends BedrockComponent {

        private final OpenExpression width;
        private final OpenExpression height;
        private final String facingCameraMode;

        private final OpenSize2i textureSize;

        private final OpenExpression textureCoordsX;
        private final OpenExpression textureCoordsY;

        private final OpenExpression textureCoordsWidth;
        private final OpenExpression textureCoordsHeight;

        private final OpenExpression stepX;
        private final OpenExpression stepY;

        private final boolean flipbook;
        private final int fps;
        private final OpenExpression maxFrame;
        private final boolean stretchToLifetime;
        private final boolean loop;

        public ParticleBillboardAppearance(OpenExpression width, OpenExpression height, String facingCameraMode, OpenSize2i textureSize, OpenExpression textureCoordsX, OpenExpression textureCoordsY, OpenExpression textureCoordsWidth, OpenExpression textureCoordsHeight, OpenExpression stepX, OpenExpression stepY, boolean flipbook, int fps, OpenExpression maxFrame, boolean stretchToLifetime, boolean loop) {
            this.width = width;
            this.height = height;
            this.facingCameraMode = facingCameraMode;
            this.textureSize = textureSize;
            this.textureCoordsX = textureCoordsX;
            this.textureCoordsY = textureCoordsY;
            this.textureCoordsWidth = textureCoordsWidth;
            this.textureCoordsHeight = textureCoordsHeight;
            this.stepX = stepX;
            this.stepY = stepY;
            this.flipbook = flipbook;
            this.fps = fps;
            this.maxFrame = maxFrame;
            this.stretchToLifetime = stretchToLifetime;
            this.loop = loop;
        }

        public OpenExpression width() {
            return width;
        }

        public OpenExpression height() {
            return height;
        }

        public String facingCameraMode() {
            return facingCameraMode;
        }

        public OpenSize2i textureSize() {
            return textureSize;
        }

        public OpenExpression textureCoordsX() {
            return textureCoordsX;
        }

        public OpenExpression textureCoordsY() {
            return textureCoordsY;
        }

        public OpenExpression textureCoordsWidth() {
            return textureCoordsWidth;
        }

        public OpenExpression textureCoordsHeight() {
            return textureCoordsHeight;
        }

        public OpenExpression stepX() {
            return stepX;
        }

        public OpenExpression stepY() {
            return stepY;
        }

        public boolean flipbook() {
            return flipbook;
        }

        public int fps() {
            return fps;
        }

        public OpenExpression maxFrame() {
            return maxFrame;
        }

        public boolean isStretchToLifetime() {
            return stretchToLifetime;
        }

        public boolean isLoop() {
            return loop;
        }

        protected static class Builder extends BedrockComponent.Builder {

            private OpenExpression width;
            private OpenExpression height;

            private String facingCameraMode;

            private int textureWidth = 0;
            private int textureHeight = 0;

            private OpenExpression textureCoordsX;
            private OpenExpression textureCoordsY;
            private OpenExpression textureCoordsWidth;
            private OpenExpression textureCoordsHeight;
            private OpenExpression stepX;
            private OpenExpression stepY;

            private boolean flipbook;
            private int fps;
            private OpenExpression maxFrame;
            private boolean stretchToLifetime;
            private boolean loop;

            public void width(OpenExpression width) {
                this.width = width;
            }

            public void height(OpenExpression height) {
                this.height = height;
            }

            public void facingCameraMode(String facingCameraMode) {
                this.facingCameraMode = facingCameraMode;
            }

            public void textureWidth(int textureWidth) {
                this.textureWidth = textureWidth;
            }

            public void textureHeight(int textureHeight) {
                this.textureHeight = textureHeight;
            }

            public void textureCoordsX(OpenExpression textureCoordsX) {
                this.textureCoordsX = textureCoordsX;
            }

            public void textureCoordsY(OpenExpression textureCoordsY) {
                this.textureCoordsY = textureCoordsY;
            }


            public void textureCoordsWidth(OpenExpression textureCoordsWidth) {
                this.textureCoordsWidth = textureCoordsWidth;
            }

            public void textureCoordsHeight(OpenExpression textureCoordsHeight) {
                this.textureCoordsHeight = textureCoordsHeight;
            }

            public void stepX(OpenExpression uvStepX) {
                this.stepX = uvStepX;
            }

            public void stepY(OpenExpression uvStepY) {
                this.stepY = uvStepY;
            }


            public void flipbook(boolean flipbook) {
                this.flipbook = flipbook;
            }

            public void fps(int fps) {
                this.fps = fps;
            }

            public void maxFrame(OpenExpression maxFrame) {
                this.maxFrame = maxFrame;
            }

            public void stretchToLifetime(boolean stretchToLifetime) {
                this.stretchToLifetime = stretchToLifetime;
            }

            public void loop(boolean loop) {
                this.loop = loop;
            }

            @Override
            public BedrockComponent build() {
                return new ParticleBillboardAppearance(width, height, facingCameraMode, new OpenSize2i(textureWidth, textureHeight), textureCoordsX, textureCoordsY, textureCoordsWidth, textureCoordsHeight, stepX, stepY, flipbook, fps, maxFrame, stretchToLifetime, loop);
            }
        }
    }

    protected static class ParticleTintingAppearance extends BedrockComponent {

        private final List<OpenExpression> values;
        private final OpenExpression interpolation;
        private final Map<String, String> gradientValues;

        public ParticleTintingAppearance(List<OpenExpression> values, OpenExpression interpolation, Map<String, String> gradientValues) {
            this.values = values;
            this.interpolation = interpolation;
            this.gradientValues = gradientValues;
        }

        public List<OpenExpression> values() {
            return values;
        }

        public OpenExpression interpolation() {
            return interpolation;
        }

        public Map<String, String> gradientValues() {
            return gradientValues;
        }

        protected static class Builder extends BedrockComponent.Builder {

            private OpenExpression interpolation;
            private final List<OpenExpression> values = new ArrayList<>();
            private final Map<String, String> gradientValues = new LinkedHashMap<>();

            public void addColor(OpenExpression expression) {
                this.values.add(expression);
            }

            public void interpolation(OpenExpression interpolation) {
                this.interpolation = interpolation;
            }

            public void addColor(String progress, String value) {
                this.gradientValues.put(progress, value);
            }

            @Override
            public BedrockComponent build() {
                return new ParticleTintingAppearance(values, interpolation, gradientValues);
            }
        }
    }

    protected static abstract class Builder {

        public abstract BedrockComponent build();
    }

    protected static abstract class ShapeBuilder extends BedrockComponent.Builder {

        OpenExpression offsetX;
        OpenExpression offsetY;
        OpenExpression offsetZ;

        Object direction = "outwards";
        OpenExpression radius;
        boolean surfaceOnly;

        public void offsetX(OpenExpression offsetX) {
            this.offsetX = offsetX;
        }

        public void offsetY(OpenExpression offsetY) {
            this.offsetY = offsetY;
        }

        public void offsetZ(OpenExpression offsetZ) {
            this.offsetZ = offsetZ;
        }

        public void direction(Object direction) {
            this.direction = direction;
        }

        public void radius(OpenExpression radius) {
            this.radius = radius;
        }

        public void surfaceOnly(boolean surfaceOnly) {
            this.surfaceOnly = surfaceOnly;
        }
    }
}
