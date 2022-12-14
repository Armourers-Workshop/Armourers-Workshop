package moe.plushie.armourers_workshop.core.armature;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import moe.plushie.armourers_workshop.api.common.IResourceManager;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.init.platform.CommonNativeManager;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.utils.math.Vector4f;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

public class ModelLoader {
	public static final OpenMatrix4f CORRECTION = OpenMatrix4f.createScaleMatrix(1, 1, 1); //OpenMatrix4f.createRotatorDeg(-90.0F, Vec3f.X_AXIS);

    @Nullable
    public Armature parse(ResourceLocation location, ResourceManager resourceManager) {

        try {
            JsonObject root = loadJSON(location, resourceManager);
            return loadArmature(root);
        } catch (Exception e) {
            ModLog.info("Can't read " + location.toString() + " because " + e);
            return null;
        }
    }

    private Armature loadArmature(JsonObject root) {
        JsonObject obj = root.getAsJsonObject("armature");
        JsonObject hierarchy = obj.get("hierarchy").getAsJsonArray().get(0).getAsJsonObject();
        JsonArray nameAsVertexGroups = obj.getAsJsonArray("joints");
        HashMap<String, Joint> jointMap = new HashMap<>();
        Joint joint = loadJoint(hierarchy, nameAsVertexGroups, jointMap, true);
        joint.setInversedModelTransform(new OpenMatrix4f());
        return new Armature(jointMap.size(), joint, jointMap);
    }

    private Mesh loadMesh(JsonObject root) {
        JsonObject obj = root.getAsJsonObject("vertices");
        JsonObject positions = obj.getAsJsonObject("positions");
        JsonObject normals = obj.getAsJsonObject("normals");
        JsonObject uvs = obj.getAsJsonObject("uvs");
        JsonObject vdincies = obj.getAsJsonObject("vindices");
        JsonObject weights = obj.getAsJsonObject("weights");
        JsonObject drawingIndices = obj.getAsJsonObject("indices");
        JsonObject vcounts = obj.getAsJsonObject("vcounts");
        float[] positionArray = toFloatArray(positions.get("array").getAsJsonArray());

        for (int i = 0; i < positionArray.length / 3; i++) {
            int k = i * 3;
            Vector4f posVector = new Vector4f(positionArray[k], positionArray[k+1], positionArray[k+2], 1.0F);
            posVector.transform(CORRECTION);
            positionArray[k] = posVector.getX();
            positionArray[k+1] = posVector.getY();
            positionArray[k+2] = posVector.getZ();
        }

        float[] normalArray = toFloatArray(normals.get("array").getAsJsonArray());

        for (int i = 0; i < normalArray.length / 3; i++) {
            int k = i * 3;
            Vector4f normVector = new Vector4f(normalArray[k], normalArray[k+1], normalArray[k+2], 1.0F);
            normVector.transform(CORRECTION);
            normalArray[k] = normVector.getX();
            normalArray[k+1] = normVector.getY();
            normalArray[k+2] = normVector.getZ();
        }

        float[] uvArray = toFloatArray(uvs.get("array").getAsJsonArray());
        int[] animationIndexArray = toIntArray(vdincies.get("array").getAsJsonArray());
        float[] weightArray = toFloatArray(weights.get("array").getAsJsonArray());
        int[] drawingIndexArray = toIntArray(drawingIndices.get("array").getAsJsonArray());
        int[] vcountArray = toIntArray(vcounts.get("array").getAsJsonArray());

        return new Mesh(positionArray, normalArray, uvArray, animationIndexArray, weightArray, drawingIndexArray, vcountArray);
    }

    private Joint loadJoint(JsonObject object, JsonArray nameAsVertexGroups, HashMap<String, Joint> jointMap, boolean start) {
        OpenMatrix4f localMatrix = toMatrix4f(object.get("transform").getAsJsonArray());
        localMatrix.transpose();
        if (start) {
             localMatrix.multiplyFront(CORRECTION);
        }

        String name = object.get("name").getAsString();
        int index = -1;
        for (int i = 0; i < nameAsVertexGroups.size(); i++) {
            if (name.equals(nameAsVertexGroups.get(i).getAsString())) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new IllegalStateException("[ModelParsingError]: Joint name " + name + " not exist!");
        }

        Joint joint = new Joint(name, index, localMatrix);
        jointMap.put(name, joint);
        for (JsonElement children : object.get("children").getAsJsonArray()) {
            joint.addSubJoint(loadJoint(children.getAsJsonObject(), nameAsVertexGroups, jointMap, false));
        }
        return joint;
    }

    private JsonObject loadJSON(ResourceLocation location, ResourceManager resourceManager) throws Exception {
        IResourceManager resourceManager1 = CommonNativeManager.createResourceManager(resourceManager);
        JsonReader in = new JsonReader(new InputStreamReader(resourceManager1.readResource(location), StandardCharsets.UTF_8));
        in.setLenient(true);
        return Streams.parse(in).getAsJsonObject();
    }

    private OpenMatrix4f toMatrix4f(JsonArray array) {
        OpenMatrix4f matrix = new OpenMatrix4f();
        matrix.set(FloatBuffer.wrap(toFloatArray(array)));
        return matrix;
    }

    private int[] toIntArray(JsonArray array) {
        List<Integer> result = Lists.newArrayList();
        for (JsonElement je : array) {
            result.add(je.getAsInt());
        }

        return ArrayUtils.toPrimitive(result.toArray(new Integer[0]));
    }

    private float[] toFloatArray(JsonArray array) {
        List<Float> result = Lists.newArrayList();
        for (JsonElement je : array) {
            result.add(je.getAsFloat());
        }
        return ArrayUtils.toPrimitive(result.toArray(new Float[0]));
    }

//	@OnlyIn(Dist.CLIENT)
//	public ClientModel.RenderProperties getRenderProperties() {
//		JsonObject properties = this.rootJson.getAsJsonObject("render_properties");
//
//		if (properties != null) {
//			return ClientModel.RenderProperties.builder()
//					.transparency(properties.has("transparent") ? properties.get("transparent").getAsBoolean() : false)
//				.build();
//		} else {
//			return ClientModel.RenderProperties.builder().build();
//		}
//	}
//
//    public ResourceLocation getParent() {
//        return this.rootJson.has("parent") ? new ResourceLocation(this.rootJson.get("parent").getAsString()) : null;
//    }

//
//	public Armature getArmature() {
//		JsonObject obj = rootJson.getAsJsonObject("armature");
//		JsonObject hierarchy = obj.get("hierarchy").getAsJsonArray().get(0).getAsJsonObject();
//		JsonArray nameAsVertexGroups = obj.getAsJsonArray("joints");
//		Map<String, Joint> jointMap = Maps.newHashMap();
//		Joint joint = this.getJoint(hierarchy, nameAsVertexGroups, jointMap, true);
//		joint.setInversedModelTransform(new OpenMatrix4f());
//		return new Armature(jointMap.size(), joint, jointMap);
//	}

}
