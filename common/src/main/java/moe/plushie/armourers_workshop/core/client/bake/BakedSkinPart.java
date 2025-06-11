package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.armature.IJointTransform;
import moe.plushie.armourers_workshop.core.client.texture.EntityTextureLoader;
import moe.plushie.armourers_workshop.core.data.color.ColorDescriptor;
import moe.plushie.armourers_workshop.core.math.OpenVoxelShape;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTransform;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintScheme;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.OpenRandomSource;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class BakedSkinPart {

    private final int id = OpenRandomSource.nextInt(BakedSkinPart.class);

    private final SkinPart part;
    private final BakedGeometryQuads quads;
    private final SkinPartTransform transform;
    private final ColorDescriptor descriptor;
    private final ArrayList<BakedSkinPart> children = new ArrayList<>();

    private float renderPolygonOffset;
    private boolean shouldRender = true;
    private Function<IJointTransform, IJointTransform> jointTransformModifier;

    public BakedSkinPart(SkinPart part, SkinPartTransform transform, BakedGeometryQuads quads) {
        this.part = part;
        this.quads = quads;
        this.transform = transform;
        this.descriptor = quads.getColorInfo();
        this.renderPolygonOffset = getType().getRenderPolygonOffset();
    }

    public void addPart(BakedSkinPart part) {
        children.add(part);
    }

    public void removePart(BakedSkinPart part) {
        children.remove(part);
    }

    @Nullable
    public Object requirements(SkinPaintScheme scheme) {
        if (descriptor.isEmpty() || scheme.isEmpty()) {
            return null;
        }
        var needsEntityTexture = false;
        var requirements = new ArrayList<>();
        for (var paintType : descriptor.getPaintTypes()) {
            if (paintType.getDyeType() != null) {
                var resolvedColor = scheme.getResolvedColor(paintType);
                requirements.add(paintType.getId());
                requirements.add(resolvedColor);
                // we must know then texture info for the resolved color.
                if (resolvedColor != null) {
                    paintType = resolvedColor.getPaintType();
                }
            }
            if (paintType == SkinPaintTypes.TEXTURE) {
                needsEntityTexture = true;
            }
        }
        if (needsEntityTexture && EntityTextureLoader.getInstance().getTextureModel(scheme.getTexture()) != null) {
            requirements.add(SkinPaintTypes.TEXTURE.getId());
            requirements.add(scheme.getTexture());
        }
        return requirements;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        var name = part.getName();
        if (name == null) {
            name = part.getType().getName();
        }
        return name;
    }

    public SkinPart getPart() {
        return part;
    }

    public SkinPartType getType() {
        return part.getType();
    }

    public SkinPartTransform getTransform() {
        return transform;
    }

    public ColorDescriptor getColorInfo() {
        return quads.getColorInfo();
    }

    public OpenVoxelShape getRenderShape() {
        return quads.getShape();
    }

    public int getMarkerTotal() {
        return part.getMarkers().size();
    }

    public void setRenderPolygonOffset(float renderPolygonOffset) {
        this.renderPolygonOffset = renderPolygonOffset;
    }

    public float getRenderPolygonOffset() {
        return renderPolygonOffset;
    }

    public void setShouldRender(boolean shouldRender) {
        this.shouldRender = shouldRender;
    }

    public boolean isVisible() {
        return shouldRender;
    }

    public ArrayList<BakedSkinPart> getChildren() {
        return children;
    }

    public SkinProperties getProperties() {
        return part.getProperties();
    }

    public void setJointTransformModifier(Function<IJointTransform, IJointTransform> jointTransformModifier) {
        this.jointTransformModifier = jointTransformModifier;
    }

    public Function<IJointTransform, IJointTransform> getJointTransformModifier() {
        return jointTransformModifier;
    }

    public BakedGeometryQuads getQuads() {
        return quads;
    }
}
