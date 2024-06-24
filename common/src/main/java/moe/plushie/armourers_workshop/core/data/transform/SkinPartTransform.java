package moe.plushie.armourers_workshop.core.data.transform;

import moe.plushie.armourers_workshop.api.action.ICanRotation;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.math.ITransformf;
import moe.plushie.armourers_workshop.api.skin.ISkinTransform;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;

import java.util.ArrayList;
import java.util.List;

public class SkinPartTransform implements ISkinTransform {

    public static final SkinPartTransform IDENTITY = new SkinPartTransform();
    private final ArrayList<ISkinTransform> children = new ArrayList<>();

    public SkinPartTransform() {
    }

    public SkinPartTransform(SkinPart part, ISkinTransform quadsTransform) {
        if (quadsTransform != null) {
            children.add(quadsTransform);
        }
        var wingsTransform = getWingsTransform(part);
        if (wingsTransform != null) {
            children.add(wingsTransform);
        }
        var partTransform = part.getTransform();
        if (partTransform != null) {
            children.add(partTransform);
        }
    }

    private ISkinTransform getWingsTransform(SkinPart part) {
        var partType = part.getType();
        if (!(partType instanceof ICanRotation)) {
            return null;
        }
        var markers = part.getMarkers();
        if (markers == null || markers.isEmpty()) {
            return null;
        }
        return new SkinWingsTransform(partType, part.getProperties(), markers.iterator().next());
    }

    @Override
    public void apply(IPoseStack poseStack) {
        for (var transform : children) {
            transform.apply(poseStack);
        }
    }

    public void addChild(ISkinTransform transform) {
        children.add(transform);
    }

    public void insertChild(ISkinTransform transform, int index) {
        children.add(index, transform);
    }

    public void replaceChild(ISkinTransform oldTransform, ISkinTransform newTransform) {
        int index = children.indexOf(oldTransform);
        if (index != -1) {
            children.set(index, newTransform);
        }
    }

    public void removeChild(ISkinTransform transform) {
        children.remove(transform);
    }

    public List<ISkinTransform> getChildren() {
        return children;
    }

    public boolean isIdentity() {
        for (var transform : children) {
            if (transform instanceof ITransformf transform1 && transform1.isIdentity()) {
                continue;
            }
            return false;
        }
        return true;
    }
}
