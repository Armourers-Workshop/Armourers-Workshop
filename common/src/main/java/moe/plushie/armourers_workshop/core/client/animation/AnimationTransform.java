package moe.plushie.armourers_workshop.core.client.animation;

import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.IVector3f;
import moe.plushie.armourers_workshop.api.skin.ISkinTransform;
import moe.plushie.armourers_workshop.core.data.transform.SkinTransform;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.utils.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

import java.util.ArrayList;
import java.util.function.Function;

public class AnimationTransform implements ISkinTransform {

    private final Vector3f pivot;
    private final Vector3f offset;

    private final Vector3f translate = new Vector3f();
    private final Vector3f rotation = new Vector3f();
    private final Vector3f scale = new Vector3f(1, 1, 1);

    private final SkinTransform parent;
    private final ArrayList<AnimationController.Snapshot> snapshots = new ArrayList<>();

    private int dirty = 0x00;
    private int changes = 0x00;

    public AnimationTransform(SkinTransform parent) {
        this.parent = parent;
        this.pivot = parent.getPivot();
        this.offset = parent.getOffset();
        this.clear(0x01, offset.equals(Vector3f.ZERO));
        this.clear(0x02, pivot.equals(Vector3f.ZERO));
    }

    public void link(AnimationController.Snapshot snapshot) {
        snapshots.add(snapshot);
    }

    @Override
    public void apply(IPoseStack poseStack) {
        // not any changes, so only call parent.
        int flags = resolve();
        if ((flags & 0xF0) == 0) {
            parent.apply(poseStack);
            return;
        }
        // the translate have changes?
        if ((flags & 0x10) != 0) {
            poseStack.translate(translate.getX(), translate.getY(), translate.getZ());
        }
        // the rotation have changes?
        if ((flags & 0x20) != 0) {
            if ((flags & 0x02) != 0) {
                poseStack.translate(pivot.getX(), pivot.getY(), pivot.getZ());
            }
            poseStack.rotate(OpenQuaternionf.fromZYX(rotation, true));
            // poseStack.rotate(Vector3f.ZP.rotationDegrees(rotation.getZ()));
            // poseStack.rotate(Vector3f.YP.rotationDegrees(rotation.getY()));
            // poseStack.rotate(Vector3f.XP.rotationDegrees(rotation.getX()));
            if ((flags & 0x02) != 0) {
                poseStack.translate(-pivot.getX(), -pivot.getY(), -pivot.getZ());
            }
        }
        // the scale have changes?
        if ((flags & 0x40) != 0) {
            if ((flags & 0x02) != 0) {
                poseStack.translate(pivot.getX(), pivot.getY(), pivot.getZ());
            }
            if ((flags & 0x04) != 0) {
                poseStack.multiply(OpenMatrix4f.createScaleMatrix(scale.getX(), scale.getY(), scale.getZ()));
            } else {
                poseStack.scale(scale.getX(), scale.getY(), scale.getZ());
            }
            if ((flags & 0x02) != 0) {
                poseStack.translate(-pivot.getX(), -pivot.getY(), -pivot.getZ());
            }
        }
        // the offset have changes?
        if ((flags & 0x80) != 0) {
            poseStack.translate(offset.getX(), offset.getY(), offset.getZ());
        }
    }

    public void mark(int newFlags) {
        dirty |= newFlags;
    }

    private int resolve() {
        // when dirty flags is clear, not need to recalculate.
        if (dirty == 0x00) {
            return changes;
        }
        // the translate have changes?
        if ((dirty & 0x10) != 0) {
            translate.set(combine(parent.getTranslate(), AnimationController.Snapshot::getTranslate, 0));
            clear(0x10, translate.equals(Vector3f.ZERO));
        }
        // the rotation have changes?
        if ((dirty & 0x20) != 0) {
            rotation.set(combine(parent.getRotation(), AnimationController.Snapshot::getRotation, 1));
            clear(0x20, rotation.equals(Vector3f.ZERO));
        }
        // the scale have changes?
        if ((dirty & 0x40) != 0) {
            scale.set(combine(parent.getScale(), AnimationController.Snapshot::getScale, 2));
            clear(0x04, Math.abs(scale.getX()) == Math.abs(scale.getY()) && Math.abs(scale.getY()) == Math.abs(scale.getZ()));
            clear(0x40, scale.equals(Vector3f.ONE));
        }
        dirty = 0x00;
        return changes;
    }

    private Vector3f combine(IVector3f parent, Function<AnimationController.Snapshot, IVector3f> getter, int mode) {
        float x = parent.getX();
        float y = parent.getY();
        float z = parent.getZ();
        for (var snapshot : snapshots) {
            var value = getter.apply(snapshot);
            // TODO: add override mode support?
            if (mode == 2) {
                x *= value.getX();
                y *= value.getY();
                z *= value.getZ();
            } else {
                x += value.getX();
                y += value.getY();
                z += value.getZ();
            }
        }
        if (mode == 1) {
            x = x % 360;
            y = y % 360;
            z = z % 360;
        }
        return new Vector3f(x, y, z);
    }

    private void clear(int flags, boolean value) {
        if (value) {
            changes &= ~flags;
        } else {
            changes |= flags;
        }
    }
}

