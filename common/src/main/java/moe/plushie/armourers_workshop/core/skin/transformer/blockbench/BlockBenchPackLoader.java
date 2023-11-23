package moe.plushie.armourers_workshop.core.skin.transformer.blockbench;

import moe.plushie.armourers_workshop.core.skin.transformer.SkinPackObject;
import moe.plushie.armourers_workshop.core.skin.transformer.bedrock.BedrockTransform;
import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import net.minecraft.core.Direction;

import java.io.IOException;

public class BlockBenchPackLoader {

    public static BlockBenchPack load(BlockBenchReader reader) throws IOException {
        SkinPackObject modelObject = SkinPackObject.from(reader.findResource("(.*)\\.bbmodel"));
        if (modelObject == null) {
            throw translatableException("error.bb.loadModel.noModel");
        }

        return parsePackObject(modelObject, reader);
    }

    private static BlockBenchPack parsePackObject(SkinPackObject object, BlockBenchReader reader) throws IOException {
        BlockBenchPack.Builder builder = new BlockBenchPack.Builder();

        // pack info
        object.at("name", it -> builder.name(it.stringValue()));
//        object.at("description", it -> builder.description(it.stringValue()));
//        object.at("author", it -> builder.author(it.collect(IDataPackObject::stringValue)));
        object.at("meta.format_version", it -> builder.version(it.stringValue()));
        object.at("meta.model_format", it -> builder.format(it.stringValue()));

        object.at("resolution", it -> builder.resolution(it.size2fValue()));

        object.each("elements", it -> builder.addElement(parseElementObject(it)));
        object.each("textures", it -> builder.addTexture(parseTextureObject(it)));
        object.each("outliner", it -> builder.addOutliner(parseChildOutlinerObject(it)));

        object.each("display", (name, it) -> builder.addDisplay(name, parseTransformObject(it)));

        return builder.build();
    }

    private static BlockBenchElement parseElementObject(SkinPackObject object) throws IOException {
        BlockBenchElement.Builder builder = new BlockBenchElement.Builder();
        object.at("uuid", it -> builder.uuid(it.stringValue()));
        object.at("name", it -> builder.name(it.stringValue()));
        object.at("type", it -> builder.type(it.stringValue()));

        object.at("from", it -> builder.from(it.vector3fValue()));
        object.at("to", it -> builder.to(it.vector3fValue()));

        object.at("origin", it -> builder.origin(it.vector3fValue()));
        object.at("rotation", it -> builder.rotation(it.vector3fValue()));
        object.at("inflate", it -> builder.inflate(it.floatValue()));

        object.at("allow_mirror_modeling", it -> builder.allowMirrorModeling(it.boolValue()));
        object.at("box_uv", it -> builder.boxUV(it.boolValue()));
        object.at("uv_offset", it -> builder.uvOffset(it.vector2fValue()));
        object.at("export", it -> builder.export(it.boolValue()));

        object.at("faces", c1 -> {
            c1.at("north", it -> builder.addFace(Direction.NORTH, parseFaceObject(it)));
            c1.at("south", it -> builder.addFace(Direction.SOUTH, parseFaceObject(it)));
            c1.at("east", it -> builder.addFace(Direction.EAST, parseFaceObject(it)));
            c1.at("west", it -> builder.addFace(Direction.WEST, parseFaceObject(it)));
            c1.at("up", it -> builder.addFace(Direction.UP, parseFaceObject(it)));
            c1.at("down", it -> builder.addFace(Direction.DOWN, parseFaceObject(it)));
        });

        return builder.build();
    }

    public static BlockBenchOutliner parseOutlinerObject(SkinPackObject object) throws IOException {
        BlockBenchOutliner.Builder builder = new BlockBenchOutliner.Builder();

        object.at("uuid", it -> builder.uuid(it.stringValue()));
        object.at("name", it -> builder.name(it.stringValue()));

        object.at("origin", it -> builder.origin(it.vector3fValue()));
        object.at("rotation", it -> builder.rotation(it.vector3fValue()));

        object.at("export", it -> builder.export(it.boolValue()));

        object.each("children", it -> builder.addChild(parseChildOutlinerObject(it)));

        return builder.build();
    }

    public static Object parseChildOutlinerObject(SkinPackObject object) throws IOException {
        if (object.type() == IDataPackObject.Type.STRING) {
            return object.stringValue();
        }
        return parseOutlinerObject(object);
    }

    private static BedrockTransform parseTransformObject(SkinPackObject object) throws IOException {
        BedrockTransform.Builder builder = new BedrockTransform.Builder();
        object.at("translation", it -> builder.translation(it.vector3fValue()));
        object.at("rotation", it -> builder.rotation(it.vector3fValue()));
        object.at("scale", it -> builder.scale(it.vector3fValue()));
        return builder.build();
    }

    private static BlockBenchTexture parseTextureObject(SkinPackObject object) throws IOException {
        BlockBenchTexture.Builder builder = new BlockBenchTexture.Builder();
        object.at("name", it -> builder.name(it.stringValue()));
        object.at("uuid", it -> builder.uuid(it.stringValue()));
        object.at("source", it -> builder.source(it.stringValue()));
        object.at("render_mode", it -> builder.renderMode(it.stringValue()));
        object.at("frame_time", it -> builder.frameTime(it.intValue()));
        object.at("frame_order_type", it -> builder.frameOrderType(it.stringValue()));
        object.at("frame_order", it -> builder.frameOrder(it.stringValue()));
        object.at("frame_interpolate", it -> builder.frameInterpolate(it.boolValue()));
        return builder.build();
    }

    private static BlockBenchFace parseFaceObject(SkinPackObject object) throws IOException {
        BlockBenchFace.Builder builder = new BlockBenchFace.Builder();
        object.at("uv", it -> builder.uv(it.rectangle2fValue()));
        object.at("texture", it -> {
            if (!it.isNull()) {
                builder.texture(it.intValue());
            }
        });
        return builder.build();
    }

    private static IOException translatableException(String message) {
        return new IOException(message);
    }
}
