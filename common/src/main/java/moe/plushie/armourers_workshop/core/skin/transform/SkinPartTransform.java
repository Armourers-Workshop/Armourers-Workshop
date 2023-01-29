package moe.plushie.armourers_workshop.core.skin.transform;

import moe.plushie.armourers_workshop.api.action.ICanRotation;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinTransform;
import moe.plushie.armourers_workshop.core.skin.data.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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

    public void setup(float partialTicks, @Nullable Entity entity) {
        for (ISkinTransform transform : transforms) {
            if (transform instanceof SkinWingsTransform) {
                ((SkinWingsTransform) transform).setup(partialTicks, entity);
            }
        }
    }

    private ISkinTransform getWingsTransform(SkinPart part) {
        ISkinPartType partType = part.getType();
        if (!(partType instanceof ICanRotation)) {
            return null;
        }
        List<SkinMarker> markers = part.getMarkers();
        if (markers == null || markers.size() == 0) {
            return null;
        }
        return new SkinWingsTransform(partType, part.getProperties(), markers.get(0));
    }

    @Override
    public void pre(IPoseStack poseStack) {
        for (ISkinTransform transform : transforms) {
            transform.pre(poseStack);
        }
    }

    @Override
    public void post(IPoseStack poseStack) {
        for (ISkinTransform transform : transforms) {
            transform.post(poseStack);
        }
    }
}
