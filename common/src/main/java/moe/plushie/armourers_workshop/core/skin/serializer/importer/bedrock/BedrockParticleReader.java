package moe.plushie.armourers_workshop.core.skin.serializer.importer.bedrock;

import moe.plushie.armourers_workshop.core.skin.serializer.importer.PackObject;
import moe.plushie.armourers_workshop.core.skin.serializer.importer.PackResourceSet;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOConsumer;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOFunction;
import moe.plushie.armourers_workshop.core.utils.Collections;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Supplier;

/**
 * <a href="https://snowstorm.app">Minecraft Bedrock Particle Generator</a>
 * <a href="https://github.com/JannisX11/snowstorm">Minecraft Bedrock Particle Generator Source Code</a>
 * <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/particlesreference/?view=minecraft-bedrock-stable">Minecraft Bedrock Particle JSON Documentation</a>
 */
public class BedrockParticleReader {

    protected final String name;
    protected final PackResourceSet resourceSet;

    public BedrockParticleReader(File file) throws IOException {
        this.name = file.getName();
        this.resourceSet = new PackResourceSet(file);
    }

    public BedrockParticle readPack() throws IOException {
        var modelObject = PackObject.from(resourceSet.firstResource());
        if (modelObject != null) {
            return parseParticleObject(modelObject);
        }
        throw new IOException("error.bb.loadParticle.noModel");
    }

    protected BedrockParticle parseParticleObject(PackObject object) throws IOException {
        var builder = new BedrockParticle.Builder();

        object.at("format_version", it -> builder.format(it.stringValue()));
        object.at("particle_effect", it -> {
            it.at("description.identifier", it2 -> builder.name(it2.stringValue()));
            it.at("description.basic_render_parameters", it1 -> {
                it1.at("material", it2 -> builder.material(it2.stringValue()));
                it1.at("texture", it2 -> builder.texture(it2.stringValue()));
            });
            it.each("curves", (key, value) -> builder.addCurve(key, parseCurveObject(value)));
            it.each("components", (key, value) -> builder.addComponent(key, parseComponentObject(key, value)));
            it.each("events", (key, value) -> builder.addEvent(key, parseEventObject(value)));
        });

        return builder.build();
    }

    protected BedrockCurve parseCurveObject(PackObject object) throws IOException {
        var builder = new BedrockCurve.Builder();

        object.at("type", it -> builder.type(it.stringValue()));
        object.at("input", it -> builder.input(it.expression()));
        object.at("horizontal_range", it -> builder.range(it.expression()));
        object.at("nodes", it -> {
            var parameters = new ArrayList<Float>();
            switch (it.type()) {
                case ARRAY -> it.allValues().forEach(it2 -> parameters.add(it2.floatValue()));
                case DICTIONARY -> it.entrySet().forEach(it2 -> {
                    parameters.add(Float.parseFloat(it2.getKey())); // time
                    parameters.add(it2.getValue().get("value").floatValue()); // value
                    parameters.add(it2.getValue().get("slope").floatValue()); // slope
                });
            }
            builder.parameters(parameters);
        });

        return builder.build();
    }

    protected BedrockComponent parseComponentObject(String name, PackObject object) throws IOException {
        return (switch (name) {
            // ..
            case "minecraft:emitter_initialization" -> parse(BedrockComponent.EmitterInitialization.Builder::new, builder -> {
                object.at("creation_expression", it -> builder.creation(it.expression()));
                object.at("per_update_expression", it -> builder.update(it.expression()));
            });
            case "minecraft:emitter_local_space" -> parse(BedrockComponent.EmitterLocalSpace.Builder::new, builder -> {
                object.at("position", it -> builder.position(it.boolValue()));
                object.at("rotation", it -> builder.rotation(it.boolValue()));
                object.at("velocity", it -> builder.velocity(it.boolValue()));
            });
            case "minecraft:emitter_rate_steady" -> parse(BedrockComponent.EmitterSteadyRate.Builder::new, builder -> {
                object.at("spawn_rate", it -> builder.spawnRate(it.expression()));
                object.at("max_particles", it -> builder.maxParticles(it.expression()));
            });
            case "minecraft:emitter_rate_instant" -> parse(BedrockComponent.EmitterInstantRate.Builder::new, builder -> {
                object.at("num_particles", it -> builder.particles(it.expression()));
            });
            case "minecraft:emitter_rate_manual" -> parse(BedrockComponent.EmitterManualRate.Builder::new, builder -> {
                object.at("max_particles", it -> builder.maxParticles(it.expression()));
            });
            case "minecraft:emitter_lifetime_events" -> parse(BedrockComponent.EmitterEventLifetime.Builder::new, builder -> {
                object.at("creation_event", it -> builder.creation(it.collect(IODataObject::stringValue)));
                object.at("expiration_event", it -> builder.expiration(it.collect(IODataObject::stringValue)));
                object.each("timeline", (key, value) -> builder.timeline(key, value.collect(IODataObject::stringValue)));
                object.each("travel_distance_events", (key, value) -> builder.travelDistance(key, value.collect(IODataObject::stringValue)));
                object.each("looping_travel_distance_events", it -> builder.travelDistanceLoop(it.get("distance").floatValue(), it.get("effects").collect(IODataObject::stringValue)));
            });
            case "minecraft:emitter_lifetime_looping" -> parse(BedrockComponent.EmitterLoopingLifetime.Builder::new, builder -> {
                object.at("active_time", it -> builder.activeTime(it.expression()));
                object.at("sleep_time", it -> builder.sleepTime(it.expression()));
            });
            case "minecraft:emitter_lifetime_once" -> parse(BedrockComponent.EmitterOnceLifetime.Builder::new, builder -> {
                object.at("active_time", it -> builder.activeTime(it.expression()));
            });
            case "minecraft:emitter_lifetime_expression" -> parse(BedrockComponent.EmitterExpressionLifetime.Builder::new, builder -> {
                object.at("activation_expression", it -> builder.activation(it.expression()));
                object.at("expiration_expression", it -> builder.expiration(it.expression()));
            });
            case "minecraft:emitter_shape_point" -> parse(BedrockComponent.EmitterPointShape.Builder::new, builder -> {
                object.at("offset", it -> {
                    builder.offsetX(it.at(0).expression());
                    builder.offsetY(it.at(1).expression());
                    builder.offsetZ(it.at(2).expression());
                });
            });
            case "minecraft:emitter_shape_entity_aabb" -> parse(BedrockComponent.EmitterEntityShape.Builder::new, builder -> {
                object.at("direction", it -> builder.direction(parseShapeDirection(it)));
                object.at("surface_only", it -> builder.surfaceOnly(it.boolValue()));
            });
            case "minecraft:emitter_shape_sphere" -> parse(BedrockComponent.EmitterSphereShape.Builder::new, builder -> {
                object.at("radius", it -> builder.radius(it.expression()));
                object.at("offset", it -> {
                    builder.offsetX(it.at(0).expression());
                    builder.offsetY(it.at(1).expression());
                    builder.offsetZ(it.at(2).expression());
                });
                object.at("direction", it -> builder.direction(parseShapeDirection(it)));
                object.at("surface_only", it -> builder.surfaceOnly(it.boolValue()));
            });
            case "minecraft:emitter_shape_box" -> parse(BedrockComponent.EmitterBoxShape.Builder::new, builder -> {
                object.at("half_dimensions", it -> {
                    builder.width(it.at(0).expression());
                    builder.height(it.at(1).expression());
                    builder.depth(it.at(2).expression());
                });
                object.at("offset", it -> {
                    builder.offsetX(it.at(0).expression());
                    builder.offsetY(it.at(1).expression());
                    builder.offsetZ(it.at(2).expression());
                });
                object.at("direction", it -> builder.direction(parseShapeDirection(it)));
                object.at("surface_only", it -> builder.surfaceOnly(it.boolValue()));
            });
            case "minecraft:emitter_shape_disc" -> parse(BedrockComponent.EmitterDiscShape.Builder::new, builder -> {
                object.at("plane_normal", it -> {
                    builder.planeNormalX(it.at(0).expression());
                    builder.planeNormalY(it.at(1).expression());
                    builder.planeNormalZ(it.at(2).expression());
                });
                object.at("offset", it -> {
                    builder.offsetX(it.at(0).expression());
                    builder.offsetY(it.at(1).expression());
                    builder.offsetZ(it.at(2).expression());
                });
                object.at("direction", it -> builder.direction(parseShapeDirection(it)));
                object.at("radius", it -> builder.radius(it.expression()));
                object.at("surface_only", it -> builder.surfaceOnly(it.boolValue()));
            });
            case "minecraft:emitter_shape_custom" -> parse(BedrockComponent.EmitterCustomShape.Builder::new, builder -> {
                object.at("offset", it -> {
                    builder.offsetX(it.at(0).expression());
                    builder.offsetY(it.at(1).expression());
                    builder.offsetZ(it.at(2).expression());
                });
                object.at("direction", it -> builder.direction(parseShapeDirection(it)));
            });
            // ..
            case "minecraft:particle_initialization" -> parse(BedrockComponent.ParticleInitialization.Builder::new, builder -> {
                object.at("per_update_expression", it -> builder.update(it.expression()));
                object.at("per_render_expression", it -> builder.render(it.expression()));
            });

            case "minecraft:particle_initial_speed" -> parse(BedrockComponent.ParticleInitialSpeed.Builder::new, builder -> {
                builder.speed(object.expression());
            });
            case "minecraft:particle_initial_spin" -> parse(BedrockComponent.ParticleInitialSpin.Builder::new, builder -> {
                object.at("rotation", it -> builder.rotation(it.expression()));
                object.at("rotation_rate", it -> builder.rotationRate(it.expression()));
            });

            case "minecraft:particle_lifetime_events" -> parse(BedrockComponent.ParticleEventLifetime.Builder::new, builder -> {
                object.at("creation_event", it -> builder.creation(it.collect(IODataObject::stringValue)));
                object.at("expiration_event", it -> builder.expiration(it.collect(IODataObject::stringValue)));
                object.each("timeline", (key, value) -> builder.timeline(key, value.collect(IODataObject::stringValue)));
            });

            case "minecraft:particle_lifetime_expression" -> parse(BedrockComponent.ParticleExpressLifetime.Builder::new, builder -> {
                object.at("max_lifetime", it -> builder.maxAge(it.expression()));
                object.at("expiration_expression", it -> builder.expiration(it.expression()));
            });

            case "minecraft:particle_kill_plane" -> parse(BedrockComponent.ParticleKillInPlaneLifetime.Builder::new, builder -> {
                object.allValues().forEach(it -> builder.add(it.floatValue())); // 4 elements of a plane equation
            });

            case "minecraft:particle_expire_if_in_blocks" -> parse(BedrockComponent.ParticleKillInBlocksLifetime.Builder::new, builder -> {
                object.allValues().forEach(it -> builder.add(it.stringValue())); // block id
            });
            case "minecraft:particle_expire_if_not_in_blocks" -> parse(BedrockComponent.ParticleOnlyInBlocksLifetime.Builder::new, builder -> {
                object.allValues().forEach(it -> builder.add(it.stringValue())); // block id
            });

            case "minecraft:particle_motion_collision" -> parse(BedrockComponent.ParticleCollisionMotion.Builder::new, builder -> {
                object.at("enabled", it -> builder.enabled(it.expression()));
                object.at("collision_drag", it -> builder.collisionDrag(it.floatValue()));
                object.at("collision_radius", it -> builder.collisionRadius(it.floatValue()));
                object.at("coefficient_of_restitution", it -> builder.coefficientOfRestitution(it.floatValue()));
                object.at("expire_on_contact", it -> builder.expireOnContact(it.boolValue()));
                object.each("events", it -> builder.event(it.get("min_speed").floatValue(), it.get("event").stringValue()));
            });

            case "minecraft:particle_motion_dynamic" -> parse(BedrockComponent.ParticleDynamicMotion.Builder::new, builder -> {
                object.at("linear_acceleration", it -> {
                    builder.linearAccelerationX(it.at(0).expression());
                    builder.linearAccelerationY(it.at(1).expression());
                    builder.linearAccelerationZ(it.at(2).expression());
                });
                object.at("linear_drag_coefficient", it -> builder.linearDragCoefficient(it.expression()));
                object.at("rotation_acceleration", it -> builder.rotationAcceleration(it.expression()));
                object.at("rotation_drag_coefficient", it -> builder.rotationDragCoefficient(it.expression()));
            });

            case "minecraft:particle_motion_parametric" -> parse(BedrockComponent.ParticleParametricMotion.Builder::new, builder -> {
                object.at("relative_position", it -> {
                    builder.relativePositionX(it.at(0).expression());
                    builder.relativePositionY(it.at(1).expression());
                    builder.relativePositionZ(it.at(2).expression());
                });
                object.at("direction", it -> {
                    builder.directionX(it.at(0).expression());
                    builder.directionY(it.at(1).expression());
                    builder.directionZ(it.at(2).expression());
                });
            });

            case "minecraft:particle_appearance_billboard" -> parse(BedrockComponent.ParticleBillboardAppearance.Builder::new, builder -> {
                object.at("size", it -> {
                    builder.width(it.at(0).expression());
                    builder.height(it.at(1).expression());
                });

                object.at("facing_camera_mode", it -> builder.facingCameraMode(it.stringValue()));
                object.at("direction", it -> {
                    //    it.at("mode", it2 -> it2.stringValue()); // derive_from_velocity/custom
                    //    it.at("min_speed_threshold", it2 -> it2.floatValue());
                    //    it.at("custom_direction", it2 -> it2.allValues()); // x expr, y expr, z expr
                });

                object.at("uv", it -> {

                    it.at("texture_width", it2 -> builder.textureWidth(it2.intValue()));
                    it.at("texture_height", it2 -> builder.textureHeight(it2.intValue()));

                    // static
                    it.at("uv", it2 -> {
                        builder.textureCoordsX(it2.at(0).expression());
                        builder.textureCoordsY(it2.at(1).expression());
                    });
                    it.at("uv_size", it2 -> {
                        builder.textureCoordsWidth(it2.at(0).expression());
                        builder.textureCoordsHeight(it2.at(1).expression());
                    });

                    // animated
                    it.at("flipbook", it1 -> builder.flipbook(true));
                    it.at("flipbook", it1 -> {
                        it1.at("base_UV", it2 -> {
                            builder.textureCoordsX(it2.at(0).expression());
                            builder.textureCoordsY(it2.at(1).expression());
                        });
                        it1.at("size_UV", it2 -> {
                            builder.textureCoordsWidth(it2.at(0).expression());
                            builder.textureCoordsHeight(it2.at(1).expression());
                        });
                        it1.at("step_UV", it2 -> {
                            builder.stepX(it2.at(0).expression());
                            builder.stepY(it2.at(1).expression());
                        });
                        it1.at("frames_per_second", it2 -> builder.fps(it2.intValue()));
                        it1.at("max_frame", it2 -> builder.maxFrame(it2.expression()));
                        it1.at("stretch_to_lifetime", it2 -> builder.stretchToLifetime(it2.boolValue()));
                        it1.at("loop", it2 -> builder.loop(it2.boolValue()));
                    });
                });
            });
            case "minecraft:particle_appearance_tinting" -> parse(BedrockComponent.ParticleTintingAppearance.Builder::new, builder -> {
                object.at("color", it -> {
                    switch (it.type()) {
                        case ARRAY -> {
                            builder.addColor(it.at(0).expression()); // R
                            builder.addColor(it.at(1).expression()); // G
                            builder.addColor(it.at(2).expression()); // B
                            builder.addColor(it.at(3).expression()); // A
                        }
                        case DICTIONARY -> {
                            it.at("interpolant", it2 -> builder.interpolation(it2.expression()));
                            it.each("gradient", (key, value) -> builder.addColor(key, value.stringValue())); // 0xAARRGGBB
                            // gradient is array, i / (float) colors.size() - 1
                        }
                    }
                });
            });
            case "minecraft:particle_appearance_lighting" -> parse(BedrockComponent.ParticleLightingAppearance.Builder::new, builder -> {
                // empty
            });
            default -> throw new IOException("can't");
        }).apply(object);
    }

    protected BedrockEvent parseEventObject(PackObject object) throws IOException {
        var builder = new BedrockEvent.Builder();
        object.at("expression", it -> builder.expression(it.expression()));
        object.at("sound_effect", it -> builder.sound(it.get("event_name").stringValue()));
        object.at("particle_effect", it -> builder.particle(it.get("effect").stringValue(), it.get("type").stringValue(), it.get("pre_effect_expression").expression()));
        return builder.build();
    }

    // outwards/inwards or  ["a","b","c"]
    protected Object parseShapeDirection(PackObject object) {
        if (object.type() == IODataObject.Type.ARRAY) {
            var x = object.at(0).expression();
            var y = object.at(1).expression();
            var z = object.at(2).expression();
            return Collections.newList(x, y, z);
        }
        return object.stringValue();
    }


    private <T extends BedrockComponent.Builder> IOFunction<PackObject, BedrockComponent> parse(Supplier<T> supplier, @Nullable IOConsumer<T> consumer) {
        return object -> {
            var builder = supplier.get();
            if (consumer != null) {
                consumer.accept(builder);
            }
            return builder.build();
        };
    }
}
