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
        this.descriptor = quads.colorInfo();
        this.renderPolygonOffset = type().renderPolygonOffset();
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
        for (var paintType : descriptor.paintTypes()) {
            if (paintType.dyeType() != null) {
                var resolvedColor = scheme.getResolvedColor(paintType);
                requirements.add(paintType.id());
                requirements.add(resolvedColor);
                // we must know then texture info for the resolved color.
                if (resolvedColor != null) {
                    paintType = resolvedColor.paintType();
                }
            }
            if (paintType == SkinPaintTypes.TEXTURE) {
                needsEntityTexture = true;
            }
        }
        if (needsEntityTexture && EntityTextureLoader.getInstance().getTextureModel(scheme.texture()) != null) {
            requirements.add(SkinPaintTypes.TEXTURE.id());
            requirements.add(scheme.texture());
        }
        return requirements;
    }

    public int id() {
        return this.id;
    }

    public String name() {
        var name = part.name();
        if (name == null) {
            name = part.type().name();
        }
        return name;
    }

    public SkinPart part() {
        return part;
    }

    public SkinPartType type() {
        return part.type();
    }

    public SkinPartTransform transform() {
        return transform;
    }

    public ColorDescriptor colorInfo() {
        return quads.colorInfo();
    }

    public OpenVoxelShape renderShape() {
        return quads.shape();
    }

    public int markerTotal() {
        return part.markers().size();
    }

    public void setRenderPolygonOffset(float renderPolygonOffset) {
        this.renderPolygonOffset = renderPolygonOffset;
    }

    public float renderPolygonOffset() {
        return renderPolygonOffset;
    }

    public void setShouldRender(boolean shouldRender) {
        this.shouldRender = shouldRender;
    }

    public boolean isVisible() {
        return shouldRender;
    }

    public ArrayList<BakedSkinPart> children() {
        return children;
    }

    public SkinProperties properties() {
        return part.properties();
    }

    public void setJointTransformModifier(Function<IJointTransform, IJointTransform> jointTransformModifier) {
        this.jointTransformModifier = jointTransformModifier;
    }

    public Function<IJointTransform, IJointTransform> jointTransformModifier() {
        return jointTransformModifier;
    }

    public BakedGeometryQuads quads() {
        return quads;
    }
}
