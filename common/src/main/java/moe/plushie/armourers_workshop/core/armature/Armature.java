package moe.plushie.armourers_workshop.core.armature;

import com.google.common.collect.ImmutableMap;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.utils.math.Rectangle3f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Armature {
	private final Map<Integer, Joint> jointById;
	private final Map<String, Joint> jointByName;
	private final Map<String, Integer> pathIndexMap;
	private final Joint jointHierarcy;
	private final int jointNumber;

	public Armature(int jointNumber, Joint rootJoint, Map<String, Joint> jointMap) {
		this.jointNumber = jointNumber;
		this.jointHierarcy = rootJoint;
		this.jointByName = jointMap;
		this.jointById = new HashMap<>();
		this.pathIndexMap = new HashMap<>();
		this.jointByName.values().forEach((joint) -> {
			this.jointById.put(joint.getId(), joint);
		});
	}

	public OpenMatrix4f[] getJointTransforms() {
		OpenMatrix4f[] jointMatrices = new OpenMatrix4f[this.jointNumber];
//		this.jointToTransformMatrixArray(this.jointHierarcy, jointMatrices);
		return jointMatrices;
	}

	public Joint searchJointById(int id) {
		return this.jointById.get(id);
	}

	public Joint searchJointByName(String name) {
		return this.jointByName.get(name);
	}

	public Collection<Joint> getJoints() {
		return this.jointByName.values();
	}

	public int searchPathIndex(String joint) {
		if (this.pathIndexMap.containsKey(joint)) {
			return this.pathIndexMap.get(joint);
		} else {
			String pathIndex = this.jointHierarcy.searchPath(new String(""), joint);
			int pathIndex2Int = 0;
			if (pathIndex == null) {
				throw new IllegalArgumentException("failed to get joint path index for " + joint);
			} else {
				pathIndex2Int = (pathIndex.length() == 0) ? -1 : Integer.parseInt(pathIndex);
				this.pathIndexMap.put(joint, pathIndex2Int);
			}
			return pathIndex2Int;
		}
	}

	public void initializeTransform() {
		this.jointHierarcy.initializeAnimationTransform();
	}

	public int getJointNumber() {
		return this.jointNumber;
	}

	public Joint getJointHierarcy() {
		return this.jointHierarcy;
	}

	public static final float Q = 0;
	// offset +24
	public static final ImmutableMap<String, TestBox> BOXES = ImmutableMap.<String, TestBox>builder()
			.put("Head", new TestBox(new Vector3f(0, 24, 0), new Rectangle3f(-4, -8, -4, 8, 8, 8)))

			.put("Chest", new TestBox(new Vector3f(0, 24, 0), new Rectangle3f(-4, 0, -2, 8, 6, 4)))
			.put("Torso", new TestBox(new Vector3f(0, 18, 0), new Rectangle3f(-4, 0, -2, 8, 6, 4)))

			.put("Skirt", new TestBox(new Vector3f(0, 12, 0), new Rectangle3f(-4, 0, -2, 8, 6, 4)).bind("Torso"))

			.put("Arm_L", new TestBox(new Vector3f(-5, 22, 0), new Rectangle3f(-1, -2, -2, 4, 6, 4)))
			.put("Arm_R", new TestBox(new Vector3f(5, 22, 0), new Rectangle3f(-3, -2, -2, 4, 6, 4)))

			.put("Hand_L", new TestBox(new Vector3f(-5, 18, 0), new Rectangle3f(-1, 0, -2, 4, 6, 4)))
			.put("Hand_R", new TestBox(new Vector3f(5, 18, 0), new Rectangle3f(-3, 0, -2, 4, 6, 4)))

			.put("Thigh_L", new TestBox(new Vector3f(-2, 12, 0), new Rectangle3f(-2, 0, -2, 4, 6, 4)))
			.put("Thigh_R", new TestBox(new Vector3f(2, 12, 0), new Rectangle3f(-2, 0, -2, 4, 6, 4)))

			.put("Leg_L", new TestBox(new Vector3f(-2, 6, 0), new Rectangle3f(-2, 0, -2, 4, 6, 4)))
			.put("Leg_R", new TestBox(new Vector3f(2, 6, 0), new Rectangle3f(-2, 0, -2, 4, 6, 4)))

			.put("Foot_L", new TestBox(new Vector3f(-2, 12, 0), new Rectangle3f(-2, 8, -2, 4, 4, 4)).bind("Leg_L"))
			.put("Foot_R", new TestBox(new Vector3f(2, 12, 0), new Rectangle3f(-2, 8, -2, 4, 4, 4)).bind("Leg_R"))

			.build();

	public static class TestBox {
		public Vector3f o;
		public Rectangle3f r;
		public String[] parents;
		public String binding;
		public TestBox(Vector3f o, Rectangle3f r, String... parents){
			this.o = o;
			this.r = r;
			this.parents = parents;
		}

		public TestBox bind(String n) {
			this.binding = n;
			return this;
		}
	}

	static AtomicInteger III = new AtomicInteger(0);

	public static void renderTest(HashMap<String, IPoseStack> map, MultiBufferSource buffers, IPoseStack poseStack, int packedLightIn, float partialTicks) {
		poseStack.pushPose();
		poseStack.scale(1 / 16f, 1 / 16f, 1 / 16f);
		III.set(0);
		map.forEach((key, value) -> {
			TestBox f = BOXES.get(key);
			if (f == null) {
				return;
			}
			poseStack.pushPose();
//			for (String p : f.parents) {
//				Matrix4f m42 = map.get(p);
//				if (m42 != null) {
//					m4.multiply(m42);
//				}
//			}
			poseStack.scale(16f, 16f, 16f);
			poseStack.multiply(value);
			poseStack.scale(1 / 16f, 1 / 16f, 1 / 16f);
			poseStack.scale(-1, -1, 1);

//			poseStack.translate(f.o.getX(), f.o.getY(), f.o.getZ());
			RenderSystem.drawBoundingBox(poseStack, f.r, ColorUtils.getPaletteColor(III.incrementAndGet()), buffers);
			RenderSystem.drawPoint(poseStack, Vector3f.ZERO, 4, 4, 4, buffers);
			poseStack.popPose();
		});
		poseStack.popPose();
	}
//	private void jointToTransformMatrixArray(Joint joint, OpenMatrix4f[] jointMatrices) {
//		OpenMatrix4f result = OpenMatrix4f.mul(joint.getAnimatedTransform(), joint.getInversedModelTransform(), null);
//		jointMatrices[joint.getId()] = result;
//
//		for (Joint childJoint : joint.getSubJoints()) {
//			this.jointToTransformMatrixArray(childJoint, jointMatrices);
//		}
//	}
}
