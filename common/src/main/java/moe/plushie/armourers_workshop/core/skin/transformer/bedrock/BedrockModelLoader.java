package moe.plushie.armourers_workshop.core.skin.transformer.bedrock;

import moe.plushie.armourers_workshop.core.skin.transformer.SkinPackObject;
import moe.plushie.armourers_workshop.api.core.IResource;
import moe.plushie.armourers_workshop.api.data.IDataPackObject;
import moe.plushie.armourers_workshop.utils.math.Rectangle2f;
import moe.plushie.armourers_workshop.utils.math.Size2f;
import moe.plushie.armourers_workshop.utils.math.Vector2f;
import net.minecraft.core.Direction;

import java.io.IOException;

// https://learn.microsoft.com/en-us/minecraft/creator/reference/content/schemasreference/schemas/minecraftschema_geometry_1.12.0
public class BedrockModelLoader {

    public static BedrockModel readFromStream(IResource resource) throws IOException {
        // parse the json from the input stream.
        SkinPackObject object = SkinPackObject.from(resource);
        if (object == null) {
            throw new IOException("can't parse a json object from input stream");
        }
        BedrockModel.Builder builder = new BedrockModel.Builder();
        object.at("format_version", it -> builder.formatVersion(it.stringValue()));
        object.at("geometry.model", it -> builder.addGeometry(parseBedrockModelGeometryLegacy(it))); // available: < 1.12.0
        object.each("minecraft:geometry", it -> builder.addGeometry(parseBedrockModelGeometry(it))); // available: >= 1.12.0
        return builder.build();
    }

    private static BedrockModelGeometry parseBedrockModelGeometry(SkinPackObject object) throws IOException {
        BedrockModelGeometry.Builder builder = new BedrockModelGeometry.Builder();
        object.at("description.identifier", it -> builder.identifier(it.stringValue()));
        object.at("description.texture_width", it -> builder.textureWidth(it.intValue()));
        object.at("description.texture_height", it -> builder.textureHeight(it.intValue()));
        object.at("description.visible_bounds_width", it -> builder.visibleWidth(it.intValue()));
        object.at("description.visible_bounds_height", it -> builder.visibleHeight(it.intValue()));
        object.at("description.visible_bounds_offset", it -> builder.visibleOffset(it.vector3fValue()));
        object.each("bones", it -> builder.addBone(parseBedrockModelBone(it)));
        return builder.build();
    }

    private static BedrockModelGeometry parseBedrockModelGeometryLegacy(SkinPackObject object) throws IOException {
        BedrockModelGeometry.Builder builder = new BedrockModelGeometry.Builder();
        object.at("texturewidth", it -> builder.textureWidth(it.intValue()));
        object.at("textureheight", it -> builder.textureHeight(it.intValue()));
        object.at("visible_bounds_width", it -> builder.visibleWidth(it.intValue()));
        object.at("visible_bounds_height", it -> builder.visibleHeight(it.intValue()));
        object.at("visible_bounds_offset", it -> builder.visibleOffset(it.vector3fValue()));
        object.each("bones", it -> builder.addBone(parseBedrockModelBone(it)));
        return builder.build();
    }

    private static BedrockModelBone parseBedrockModelBone(SkinPackObject object) throws IOException {
        BedrockModelBone.Builder builder = new BedrockModelBone.Builder();
        object.at("name", it -> builder.name(it.stringValue()));
        object.at("parent", it -> builder.parent(it.stringValue()));
        object.at("pivot", it -> builder.pivot(it.vector3fValue()));
        object.at("rotation", it -> builder.rotation(it.vector3fValue()));
        object.each("cubes", it -> builder.addCube(parseBedrockModelCube(it)));
        object.each("locators", (key, it) -> builder.addLocator(key, it.vector3fValue()));
        return builder.build();
    }

    private static BedrockModelCube parseBedrockModelCube(SkinPackObject object) throws IOException {
        BedrockModelCube.Builder builder = new BedrockModelCube.Builder();
        object.at("pivot", it -> builder.pivot(it.vector3fValue()));
        object.at("rotation", it -> builder.rotation(it.vector3fValue()));
        object.at("origin", it -> builder.origin(it.vector3fValue()));
        object.at("size", it -> builder.size(it.size3fValue()));
        object.at("uv", it -> builder.uv(parseBedrockUV(it)));
        object.at("inflate", it -> builder.inflate(it.floatValue()));
        object.at("mirror", it -> builder.mirror(it.boolValue()));
        return builder.build();
    }

    private static BedrockModelUV parseBedrockUV(SkinPackObject object) throws IOException {
        // specifies the upper-left corner on the texture for the start of the texture mapping for this box.
        if (object.type() == IDataPackObject.Type.ARRAY) {
            return new BedrockModelUV(object.vector2fValue());
        }
        // This is an alternate per-face uv mapping which specifies each face of the cube.
        // Omitting a face will cause that face to not get drawn.
        BedrockModelUV texture = new BedrockModelUV();
        object.at("north", it -> parseBedrockUV(it, texture, Direction.NORTH));
        object.at("south", it -> parseBedrockUV(it, texture, Direction.SOUTH));
        object.at("east", it -> parseBedrockUV(it, texture, Direction.EAST));
        object.at("west", it -> parseBedrockUV(it, texture, Direction.WEST));
        object.at("up", it -> parseBedrockUV(it, texture, Direction.UP));
        object.at("down", it -> parseBedrockUV(it, texture, Direction.DOWN));
        return texture;
    }

    private static void parseBedrockUV(SkinPackObject object, BedrockModelUV texture, Direction direction) throws IOException {
        Rectangle2f rect = new Rectangle2f(0, 0, 0, 0);
        object.at("uv", it -> {
            Vector2f uv = it.vector2fValue();
            rect.setX(uv.getX());
            rect.setY(uv.getY());
        });
        object.at("uv_size", it -> {
            Size2f size = it.size2fValue();
            rect.setWidth(size.getWidth());
            rect.setHeight(size.getHeight());
        });
        texture.put(direction, rect);
    }
}
