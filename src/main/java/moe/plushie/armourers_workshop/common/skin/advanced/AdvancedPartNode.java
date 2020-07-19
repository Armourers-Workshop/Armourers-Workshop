package moe.plushie.armourers_workshop.common.skin.advanced;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.minecraft.util.math.Vec3d;

public class AdvancedPartNode {

    private final ArrayList<AdvancedPartNode> children = new ArrayList<AdvancedPartNode>();
    public int partIndex;
    public String name;
    public boolean isStatic = true;
    public boolean enabled = true;
    public float scale = 1F;
    public boolean mirror = false;

    public Vec3d pos = Vec3d.ZERO;
    public Vec3d posOffset = Vec3d.ZERO;

    public Vec3d rotationAngle = Vec3d.ZERO;
    public Vec3d rotationAngleOffset = Vec3d.ZERO;

    public Vec3d rotationPos = Vec3d.ZERO;
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

        private static final String TAG_FILENAME = "filename";
        private static final String TAG_LAST_ACCESS = "lastAccess";

        @Override
        public AdvancedPartNode deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public JsonElement serialize(AdvancedPartNode src, Type typeOfSrc, JsonSerializationContext context) {
            // TODO Auto-generated method stub
            return null;
        }
    }
}
