package moe.plushie.armourers_workshop.core.data.transform;

import moe.plushie.armourers_workshop.api.action.ICanRotation;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinTransform;
import moe.plushie.armourers_workshop.core.skin.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class SkinPartTransform implements ISkinTransform {

    private final ArrayList<ISkinTransform> transforms = new ArrayList<>();

    public SkinPartTransform(SkinPart part, ISkinTransform quadsTransform) {
        if (quadsTransform != null) {
            transforms.add(quadsTransform);
        }
        ISkinTransform wingsTransform = getWingsTransform(part);
        if (wingsTransform != null) {
            transforms.add(wingsTransform);
        }
        ISkinTransform partTransform = part.getTransform();
        if (partTransform != null) {
            transforms.add(partTransform);
        }
    }

    public void forEach(Consumer<ISkinTransform> consumer) {
        transforms.forEach(consumer);
    }

    private ISkinTransform getWingsTransform(SkinPart part) {
        ISkinPartType partType = part.getType();
        if (!(partType instanceof ICanRotation)) {
            return null;
        }
        Collection<SkinMarker> markers = part.getMarkers();
        if (markers == null || markers.size() == 0) {
            return null;
        }
        return new SkinWingsTransform(partType, part.getProperties(), markers.iterator().next());
    }

    @Override
    public void apply(IPoseStack poseStack) {
        for (ISkinTransform transform : transforms) {
            transform.apply(poseStack);
        }
    }

    public void addTransform(ISkinTransform transform) {
        transforms.add(transform);
    }

    public void insertTransform(ISkinTransform transform, int index) {
        transforms.add(index, transform);
    }

    public void removeTransform(ISkinTransform transform) {
        transforms.remove(transform);
    }
}
