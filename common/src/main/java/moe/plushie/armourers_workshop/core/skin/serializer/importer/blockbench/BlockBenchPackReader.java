package moe.plushie.armourers_workshop.core.skin.serializer.importer.blockbench;

import moe.plushie.armourers_workshop.core.math.OpenSize2f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.serializer.importer.PackObject;
import moe.plushie.armourers_workshop.core.skin.serializer.importer.PackResourceSet;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOConsumer;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IODataObject;
import moe.plushie.armourers_workshop.core.skin.serializer.io.IOFunction;
import moe.plushie.armourers_workshop.core.utils.OpenDirection;
import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.function.Supplier;

/**
 * <a href="https://www.blockbench.net/wiki/docs/bbmodel">The .bbmodel format</a>
 * <a href="https://github.com/JannisX11/blockbench/blob/master/js/io/formats/bbmodel.js">bbmodel.js</a>
 */
public class BlockBenchPackReader {

    protected final String name;
    protected final PackResourceSet resourceSet;

    public BlockBenchPackReader(File file) throws IOException {
        this.name = file.getName();
        this.resourceSet = new PackResourceSet(file);
    }

    public BlockBenchPack readPack() throws IOException {
        var modelObject = PackObject.from(resourceSet.findResource("(.*)\\.bbmodel"));
        if (modelObject != null) {
            return parsePackObject(modelObject);
        }
        throw new IOException("error.bb.loadModel.noModel");
    }

    protected BlockBenchPack parsePackObject(PackObject object) throws IOException {
        var builder = new BlockBenchPack.Builder();

        // pack info
        object.at("name", it -> builder.name(it.stringValue()));
//        object.at("description", it -> builder.description(it.stringValue()));
//        object.at("author", it -> builder.author(it.collect(IDataPackObject::stringValue)));
        object.at("meta.format_version", it -> builder.version(it.stringValue()));
        object.at("meta.model_format", it -> builder.format(it.stringValue()));

        object.at("resolution", it -> builder.resolution(it.size2fValue()));
        object.at("display", it -> builder.setUseItemTransforms(true));

        object.each("elements", it -> builder.addElement(parseElementObject(it)));
        object.each("textures", it -> builder.addTexture(parseTextureObject(it)));
        object.each("animations", it -> builder.addAnimation(parseAnimationObject(it)));

        object.each("outliner", it -> builder.addOutliner(parseChildOutlinerObject(it)));

        object.each("display", (name, it) -> builder.addDisplay(name, parseTransformObject(it)));

        return builder.build();
    }

    protected BlockBenchElement parseElementObject(PackObject object) throws IOException {
        return (switch (object.get("type").stringValue()) {
            case "cube" -> parseElementObject(BlockBenchCube.Builder::new, builder -> {
                object.at("from", it -> builder.from(it.vector3fValue()));
                object.at("to", it -> builder.to(it.vector3fValue()));

                object.at("origin", it -> builder.origin(it.vector3fValue()));
                object.at("rotation", it -> builder.rotation(it.vector3fValue()));

                object.at("inflate", it -> builder.inflate(it.floatValue()));

                object.at("allow_mirror_modeling", it -> builder.allowMirrorModeling(it.boolValue()));
                object.at("render_order", it -> builder.renderOrder(it.stringValue()));
                object.at("box_uv", it -> builder.boxUV(it.boolValue()));
                object.at("mirror_uv", it -> builder.mirrorUV(it.boolValue()));
                object.at("uv_offset", it -> builder.uvOffset(it.vector2fValue()));

                object.at("faces", c1 -> {
                    c1.at("north", it -> builder.addFace(OpenDirection.NORTH, parseFaceObject(it)));
                    c1.at("south", it -> builder.addFace(OpenDirection.SOUTH, parseFaceObject(it)));
                    c1.at("east", it -> builder.addFace(OpenDirection.EAST, parseFaceObject(it)));
                    c1.at("west", it -> builder.addFace(OpenDirection.WEST, parseFaceObject(it)));
                    c1.at("up", it -> builder.addFace(OpenDirection.UP, parseFaceObject(it)));
                    c1.at("down", it -> builder.addFace(OpenDirection.DOWN, parseFaceObject(it)));
                });
            });
            case "mesh" -> parseElementObject(BlockBenchMesh.Builder::new, builder -> {
                object.at("origin", it -> builder.origin(it.vector3fValue()));
                //object.at("origin", it -> builder.origin(it.vector3fValue()));
                object.at("rotation", it -> builder.rotation(it.vector3fValue()));

                object.at("allow_mirror_modeling", it -> builder.allowMirrorModeling(it.boolValue()));
                object.at("render_order", it -> builder.renderOrder(it.stringValue()));
                object.at("box_uv", it -> builder.boxUV(it.boolValue()));
                object.at("mirror_uv", it -> builder.mirrorUV(it.boolValue()));
                object.at("uv_offset", it -> builder.uvOffset(it.vector2fValue()));

                object.each("vertices", (key, value) -> {
                    builder.addVertex(key, value.vector3fValue());
                });

                object.each("faces", (key, value) -> {
                    var builder2 = new BlockBenchMeshFace.Builder();
                    value.each("uv", (key2, value2) -> builder2.addUV(key2, value2.vector2fValue()));
                    value.each("vertices", it2 -> builder2.addVertex(it2.stringValue()));
                    value.at("texture", it2 -> {
                        if (!it2.isNull()) {
                            builder2.texture(it2.intValue());
                        }
                    });
                    builder.addFace(key, builder2.build());
                });
            });
            case "locator" -> parseElementObject(BlockBenchLocator.Builder::new, builder -> {
                object.at("position", it -> builder.position(it.vector3fValue()));
                object.at("rotation", it -> builder.rotation(it.vector3fValue()));
            });
            case "null_object" -> parseElementObject(BlockBenchNull.Builder::new, builder -> {
                object.at("position", it -> builder.position(it.vector3fValue()));
            });
            default -> parseElementObject(BlockBenchElement.Builder::new, null);
        }).apply(object);
    }

    protected <T extends BlockBenchElement.Builder> IOFunction<PackObject, BlockBenchElement> parseElementObject(Supplier<T> supplier, @Nullable IOConsumer<T> consumer) {
        return object -> {
            var builder = supplier.get();

            object.at("uuid", it -> builder.uuid(it.stringValue()));
            object.at("name", it -> builder.name(it.stringValue()));
            object.at("type", it -> builder.type(it.stringValue()));

            object.at("export", it -> builder.export(it.boolValue()));

            // ignore_inherited_scale
            // visibility
            // locked

            if (consumer != null) {
                consumer.accept(builder);
            }

            return builder.build();
        };
    }

    protected BlockBenchOutliner parseOutlinerObject(PackObject object) throws IOException {
        var builder = new BlockBenchOutliner.Builder();

        object.at("uuid", it -> builder.uuid(it.stringValue()));
        object.at("name", it -> builder.name(it.stringValue()));

        object.at("origin", it -> builder.origin(it.vector3fValue()));
        object.at("rotation", it -> builder.rotation(it.vector3fValue()));

        object.at("export", it -> builder.export(it.boolValue()));

        object.each("children", it -> builder.addChild(parseChildOutlinerObject(it)));

        return builder.build();
    }

    protected Object parseChildOutlinerObject(PackObject object) throws IOException {
        if (object.type() == IODataObject.Type.STRING) {
            return object.stringValue();
        }
        return parseOutlinerObject(object);
    }

    protected BlockBenchDisplay parseTransformObject(PackObject object) throws IOException {
        var builder = new BlockBenchDisplay.Builder();
        object.at("translation", it -> builder.translation(it.vector3fValue()));
        object.at("rotation", it -> builder.rotation(it.vector3fValue()));
        object.at("scale", it -> builder.scale(it.vector3fValue()));
        return builder.build();
    }

    protected BlockBenchTexture parseTextureObject(PackObject object) throws IOException {
        var builder = new BlockBenchTexture.Builder();
        object.at("name", it -> builder.name(it.stringValue()));
        object.at("uuid", it -> builder.uuid(it.stringValue()));
        object.at("source", it -> builder.source(it.stringValue()));
        object.at("render_mode", it -> builder.renderMode(it.stringValue()));
        object.at("frame_time", it -> builder.frameTime(it.intValue()));
        object.at("frame_order_type", it -> builder.frameOrderType(it.stringValue()));
        object.at("frame_order", it -> builder.frameOrder(it.stringValue()));
        object.at("frame_interpolate", it -> builder.frameInterpolate(it.boolValue()));
        object.at("width", width -> object.at("height", height -> builder.imageSize(new OpenSize2f(width.floatValue(), height.floatValue()))));
        object.at("uv_width", width -> object.at("uv_height", height -> builder.textureSize(new OpenSize2f(width.floatValue(), height.floatValue()))));
        return builder.build();
    }

    protected BlockBenchAnimation parseAnimationObject(PackObject object) throws IOException {
        var builder = new BlockBenchAnimation.Builder();
        object.at("name", it -> builder.name(it.stringValue()));
        object.at("uuid", it -> builder.uuid(it.stringValue()));
        object.at("loop", it -> builder.loop(it.stringValue()));
        object.at("length", it -> builder.duration(it.floatValue()));
        object.each("animators", (key, it) -> builder.addAnimator(parseAnimatorObject(key, it)));
        // "override"
        // "snapping"
        // "selected"
        // "anim_time_update"
        // "blend_weight"
        // "start_delay"
        // "loop_delay"
        return builder.build();
    }

    protected BlockBenchAnimator parseAnimatorObject(String uuid, PackObject object) throws IOException {
        var builder = new BlockBenchAnimator.Builder(uuid);
        object.at("name", it -> builder.name(it.stringValue()));
        object.at("uuid", it -> builder.uuid(it.stringValue()));
        object.at("type", it -> builder.type(it.stringValue()));
        object.each("keyframes", fo -> {
            var fb = new BlockBenchKeyframe.Builder();
            fo.at("uuid", it -> fb.uuid(it.stringValue()));
            fo.at("channel", it -> fb.name(it.stringValue()));
            fo.at("time", it -> fb.time(it.floatValue()));
            fo.at("interpolation", it -> {
                fb.interpolation(it.stringValue());
                if (it.stringValue().equals("bezier")) {
                    var values = new ArrayList<OpenVector3f>();
                    var parameters = new ArrayList<Float>();
                    //fo.get("bezier_linked");
                    values.add(fo.get("bezier_left_time").vector3fValue());
                    values.add(fo.get("bezier_left_value").vector3fValue());
                    values.add(fo.get("bezier_right_time").vector3fValue());
                    values.add(fo.get("bezier_right_value").vector3fValue());
                    for (var parameter : values) {
                        parameters.add(parameter.x());
                        parameters.add(parameter.y());
                        parameters.add(parameter.z());
                    }
                    fb.parameters(parameters);
                }
            });
            fo.each("data_points", it -> {
                var point = new LinkedHashMap<String, OpenPrimitive>();
                for (var entry : it.entrySet()) {
                    var key = entry.getKey();
                    var value = entry.getValue();
                    switch (value.type()) {
                        case NUMBER -> point.put(key, OpenPrimitive.of(value.floatValue()));
                        case STRING -> point.put(key, OpenPrimitive.of(value.stringValue()));
                        default -> throw new IOException("a unknown point type of " + value);
                    }
                }
                fb.point(point);
            });
            builder.addFrame(fb.build());
        });
        return builder.build();
    }


    protected BlockBenchCubeFace parseFaceObject(PackObject object) throws IOException {
        var builder = new BlockBenchCubeFace.Builder();
        object.at("rotation", it -> builder.rotation(it.intValue()));
        object.at("uv", it -> builder.uv(it.rectangle2fValue()));
        object.at("texture", it -> {
            if (!it.isNull()) {
                builder.texture(it.intValue());
            }
        });
        return builder.build();
    }

    public String getName() {
        return name;
    }
}
