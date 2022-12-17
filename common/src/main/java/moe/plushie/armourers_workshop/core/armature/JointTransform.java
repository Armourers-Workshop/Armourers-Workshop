package moe.plushie.armourers_workshop.core.armature;

import com.google.common.collect.Maps;
import com.mojang.math.Quaternion;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

import java.util.Map;

public class JointTransform {
	public static final String ANIMATION_TRANSFROM = "animation_transform";
	public static final String JOINT_LOCAL_TRANSFORM = "joint_local_transform";
	public static final String PARENT = "parent";
	public static final String RESULT1 = "front_result";
	public static final String RESULT2 = "overwrite_rotation";

	@FunctionalInterface
	public interface MatrixOperation {
		OpenMatrix4f mul(OpenMatrix4f left, OpenMatrix4f right, OpenMatrix4f dest);
	}

	public static class TransformEntry {
		public final MatrixOperation multiplyFunction;
		public final JointTransform transform;

		public TransformEntry(MatrixOperation multiplyFunction, JointTransform transform) {
			this.multiplyFunction = multiplyFunction;
			this.transform = transform;
		}
	}

	private Map<String, TransformEntry> entries = Maps.newHashMap();
	private Vector3f translation;
	private Vector3f scale;
	private Quaternion rotation;

	public JointTransform(Vector3f translation, Quaternion rotation, Vector3f scale) {
		this.translation = translation;
		this.rotation = rotation;
		this.scale = scale;
	}

	public Vector3f translation() {
		return this.translation;
	}

	public Quaternion rotation() {
		return this.rotation;
	}

	public Vector3f scale() {
		return this.scale;
	}

	public JointTransform copy() {
		return JointTransform.empty().copyFrom(this);
	}

	public JointTransform copyFrom(JointTransform jt) {
        Vector3f newV = jt.translation();
		Quaternion newQ = jt.rotation();
        Vector3f newS = jt.scale;
		this.translation.set(newV.getX(), newV.getY(), newV.getZ());
		this.rotation.set(newQ.i(), newQ.j(), newQ.k(), newQ.r());
		this.scale.set(newS.getX(), newS.getY(), newS.getZ());

		for (Map.Entry<String, TransformEntry> entry : jt.entries.entrySet()) {
			this.entries.put(entry.getKey(), entry.getValue());
		}

		return this;
	}

	public void jointLocal(JointTransform transform, MatrixOperation multiplyFunction) {
		this.entries.put(JOINT_LOCAL_TRANSFORM, new TransformEntry(multiplyFunction, transform));
	}

	public void parent(JointTransform transform, MatrixOperation multiplyFunction) {
		this.entries.put(PARENT, new TransformEntry(multiplyFunction, transform));
	}

	public void frontResult(JointTransform transform, MatrixOperation multiplyFunction) {
		this.entries.put(RESULT1, new TransformEntry(multiplyFunction, transform));
	}

//	public void overwriteRotation(JointTransform transform) {
//		this.entries.put(RESULT2, new TransformEntry(OpenMatrix4f::mul, transform));
//	}
//
//	public OpenMatrix4f getAnimationBindedMatrix(Joint joint, OpenMatrix4f parentTransform) {
//		OpenMatrix4f.AnimationTransformEntry animationTransformEntry = new OpenMatrix4f.AnimationTransformEntry();
//
//		for (Map.Entry<String, TransformEntry> entry : this.entries.entrySet()) {
//			animationTransformEntry.put(entry.getKey(), entry.getValue().transform.toMatrix(), entry.getValue().multiplyFunction);
//		}
//
//		animationTransformEntry.put(ANIMATION_TRANSFROM, this.toMatrix(), OpenMatrix4f::mul);
//		animationTransformEntry.put(JOINT_LOCAL_TRANSFORM, joint.getLocalTrasnform());
//		animationTransformEntry.put(PARENT, parentTransform);
//		animationTransformEntry.put(ANIMATION_TRANSFROM, joint.getAnimatedTransform());
//
//		return animationTransformEntry.getResult();
//	}
//
//	public OpenMatrix4f toMatrix() {
//		OpenMatrix4f matrix = new OpenMatrix4f().translate(this.translation).mulBack(OpenMatrix4f.fromQuaternion(this.rotation)).scale(this.scale);
//		return matrix;
//	}

	@Override
	public String toString() {
		return String.format("translation:%s, rotation:%s, %d entries ", this.translation, this.rotation, this.entries.size());
	}

//	private static JointTransform interpolateSimple(JointTransform prev, JointTransform next, float progression) {
//		return new JointTransform(MathUtils.lerpVector(prev.translation, next.translation, progression),
//				MathUtils.lerpQuaternion(prev.rotation, next.rotation, progression),
//				MathUtils.lerpVector(prev.scale, next.scale, progression));
//	}
//
//	public static JointTransform interpolate(JointTransform prev, JointTransform next, float progression) {
//		if (prev == null || next == null) {
//			return JointTransform.empty();
//		}
//
//		progression = MathUtils.clamp(progression, 0.0F, 1.0F);
//		JointTransform interpolated = interpolateSimple(prev, next, progression);
//
//		for (Map.Entry<String, TransformEntry> entry : prev.entries.entrySet()) {
//			JointTransform transform = next.entries.containsKey(entry.getKey()) ? next.entries.get(entry.getKey()).transform : JointTransform.empty();
//			interpolated.entries.put(entry.getKey(), new TransformEntry(entry.getValue().multiplyFunction, interpolateSimple(entry.getValue().transform, transform, progression)));
//		}
//
//		for (Map.Entry<String, TransformEntry> entry : next.entries.entrySet()) {
//			if (!interpolated.entries.containsKey(entry.getKey())) {
//				interpolated.entries.put(entry.getKey(), new TransformEntry(entry.getValue().multiplyFunction, interpolateSimple(JointTransform.empty(), entry.getValue().transform, progression)));
//			}
//		}
//
//		return interpolated;
//	}
//
//	public static JointTransform fromMatrixNoScale(OpenMatrix4f matrix) {
//		return new JointTransform(matrix.toTranslationVector(), matrix.toQuaternion(), new Vec3f(1.0F, 1.0F, 1.0F));
//	}
//
//	public static JointTransform getTranslation(Vector3f vec) {
//		return JointTransform.translationRotation(vec, new Quaternion(0.0F, 0.0F, 0.0F, 1.0F));
//	}
//
//	public static JointTransform getRotation(Quaternion quat) {
//		return JointTransform.translationRotation(new Vector3f(0.0F, 0.0F, 0.0F), quat);
//	}
//
//	public static JointTransform getScale(Vector3f vec) {
//		return new JointTransform(new Vector3f(1.0F, 1.0F, 1.0F), new Quaternion(0.0F, 0.0F, 0.0F, 1.0F), vec);
//	}
//
//	public static JointTransform fromMatrix(OpenMatrix4f matrix) {
//		return new JointTransform(matrix.toTranslationVector(), matrix.toQuaternion(), matrix.toScaleVector());
//	}

	public static JointTransform translationRotation(Vector3f vec, Quaternion quat) {
		return new JointTransform(vec, quat, new Vector3f(1.0F, 1.0F, 1.0F));
	}

	public static JointTransform empty() {
		return new JointTransform(new Vector3f(0.0F, 0.0F, 0.0F), new Quaternion(0.0F, 0.0F, 0.0F, 1.0F), new Vector3f(1.0F, 1.0F, 1.0F));
	}
}
