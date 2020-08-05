package moe.plushie.armourers_workshop.common.skin.advanced;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.minecraft.util.math.Vec3d;

public class AdvancedPartNode {

    public int partIndex;
    public String name;
    public boolean isStatic = true;
    public boolean enabled = true;
    public float scale = 1F;
    public boolean mirror = false;
    public Vec3d pos = Vec3d.ZERO;
    public Vec3d rotationAngle = Vec3d.ZERO;
    public Vec3d rotationPos = Vec3d.ZERO;
    private final ArrayList<AdvancedPartNode> children = new ArrayList<AdvancedPartNode>();

    public Vec3d posOffset = Vec3d.ZERO;
    public Vec3d rotationAngleOffset = Vec3d.ZERO;
    public Vec3d rotationPosOffset = Vec3d.ZERO;

    public AdvancedPartNode(int partIndex, String name) {
        this.partIndex = partIndex;
        this.name = name;
    }

    public ArrayList<AdvancedPartNode> getChildren() {
        return children;
    }

    public void setRotationAngleOffset(double x, double y, double z) {
        this.rotationAngleOffset = new Vec3d(x, y, z);
    }

    @Override
    public String toString() {
        return "AdvancedPartNode [children=" + children + ", partIndex=" + partIndex + ", name=" + name + ", isStatic=" + isStatic + ", enabled=" + enabled + ", scale=" + scale + ", mirror=" + mirror + ", pos=" + pos + ", posOffset=" + posOffset + ", rotationAngle=" + rotationAngle
                + ", rotationAngleOffset=" + rotationAngleOffset + ", rotationPos=" + rotationPos + ", rotationPosOffset=" + rotationPosOffset + "]";
    }

    @Override
    public AdvancedPartNode clone() {
        AdvancedPartNode clone = new AdvancedPartNode(partIndex, name);
        for (AdvancedPartNode child : children) {
            clone.children.add(child.clone());
        }
        clone.isStatic = isStatic;
        clone.enabled = enabled;
        clone.scale = scale;
        clone.mirror = mirror;

        clone.pos = pos;
        clone.posOffset = posOffset;
        clone.rotationAngle = rotationAngle;
        clone.rotationAngleOffset = rotationAngleOffset;
        clone.rotationPos = rotationPos;
        clone.rotationPosOffset = rotationPosOffset;
        return clone;
    }

    public static class Serializer implements JsonSerializer<AdvancedPartNode>, JsonDeserializer<AdvancedPartNode> {

        private static final String TAG_CHILDREN = "children";
        private static final String TAG_PART_INDEX = "part_index";
        private static final String TAG_NAME = "name";
        private static final String TAG_IS_STATIC = "is_static";
        private static final String TAG_ENABLED = "enabled";
        private static final String TAG_SCALE = "scale";
        private static final String TAG_MIRROR = "mirror";
        private static final String TAG_POSITION = "pos";
        private static final String TAG_ROTATION = "rot";
        private static final String TAG_ROTATION_POSITION = "rot_pos";

        private static final String TAG_X = "x";
        private static final String TAG_Y = "y";
        private static final String TAG_Z = "z";

        @Override
        public AdvancedPartNode deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return deserialize(json);
        }

        @Override
        public JsonElement serialize(AdvancedPartNode src, Type typeOfSrc, JsonSerializationContext context) {
            return serialize(src);
        }

        public static AdvancedPartNode deserialize(JsonElement json) {
            JsonObject jsonObject = json.getAsJsonObject();
            int partIndex = jsonObject.get(TAG_PART_INDEX).getAsInt();
            String name = jsonObject.get(TAG_NAME).getAsString();
            AdvancedPartNode advancedPartNode = new AdvancedPartNode(partIndex, name);
            advancedPartNode.isStatic = jsonObject.get(TAG_IS_STATIC).getAsBoolean();
            advancedPartNode.enabled = jsonObject.get(TAG_ENABLED).getAsBoolean();
            advancedPartNode.scale = jsonObject.get(TAG_SCALE).getAsFloat();
            advancedPartNode.mirror = jsonObject.get(TAG_MIRROR).getAsBoolean();
            advancedPartNode.pos = deserializeVec3d(jsonObject.get(TAG_POSITION));
            advancedPartNode.rotationAngle = deserializeVec3d(jsonObject.get(TAG_ROTATION));
            advancedPartNode.rotationPos = deserializeVec3d(jsonObject.get(TAG_ROTATION_POSITION));
            JsonArray children = jsonObject.get(TAG_CHILDREN).getAsJsonArray();
            for (JsonElement childJson : children) {
                advancedPartNode.children.add(deserialize(childJson));
            }
            return advancedPartNode;
        }

        public static JsonElement serialize(AdvancedPartNode src) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(TAG_PART_INDEX, src.partIndex);
            jsonObject.addProperty(TAG_NAME, src.name);
            jsonObject.addProperty(TAG_IS_STATIC, src.isStatic);
            jsonObject.addProperty(TAG_ENABLED, src.enabled);
            jsonObject.addProperty(TAG_SCALE, src.scale);
            jsonObject.addProperty(TAG_MIRROR, src.mirror);
            jsonObject.add(TAG_POSITION, serializeVec3d(src.pos));
            jsonObject.add(TAG_ROTATION, serializeVec3d(src.rotationAngle));
            jsonObject.add(TAG_ROTATION_POSITION, serializeVec3d(src.rotationPos));
            JsonArray children = new JsonArray();
            for (AdvancedPartNode child : src.children) {
                children.add(serialize(child));
            }
            jsonObject.add(TAG_CHILDREN, children);
            return jsonObject;
        }

        public static Vec3d deserializeVec3d(JsonElement json) {
            JsonObject jsonObject = json.getAsJsonObject();
            double x = jsonObject.get(TAG_X).getAsDouble();
            double y = jsonObject.get(TAG_Y).getAsDouble();
            double z = jsonObject.get(TAG_Z).getAsDouble();
            return new Vec3d(x, y, z);
        }

        public static JsonElement serializeVec3d(Vec3d src) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(TAG_X, src.x);
            jsonObject.addProperty(TAG_Y, src.y);
            jsonObject.addProperty(TAG_Z, src.z);
            return jsonObject;
        }
    }
}
