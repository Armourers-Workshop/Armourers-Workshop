package moe.plushie.armourers_workshop.core.client.bake;

import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.data.color.ColorDescriptor;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.transform.SkinPartTransform;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.texture.PlayerTextureLoader;
import moe.plushie.armourers_workshop.utils.math.OpenRay;
import moe.plushie.armourers_workshop.utils.math.OpenVoxelShape;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class BakedSkinPart {

    private final SkinPart part;
    private final BakedCubeQuads quads;
    private final SkinPartTransform transform;
    private final ColorDescriptor descriptor;
    private final ArrayList<BakedSkinPart> children = new ArrayList<>();

    private int id = 0;
    private float renderPolygonOffset;

    public BakedSkinPart(SkinPart part, BakedCubeQuads quads) {
        this.part = part;
        this.quads = quads;
        this.transform = new SkinPartTransform(part, quads.getTransform());
        this.descriptor = quads.getColorInfo();
        this.renderPolygonOffset = getType().getRenderPolygonOffset();
    }

    public void addPart(BakedSkinPart part) {
        children.add(part);
    }

    public void removePart(BakedSkinPart part) {
        children.remove(part);
    }

    public void forEach(BiConsumer<RenderType, ArrayList<BakedCubeFace>> action) {
        quads.forEach(action);
    }

    public void forEach(OpenRay ray, Consumer<BakedCubeFace> recorder) {
        quads.forEach(ray, recorder);
    }

    @Nullable
    public Object requirements(ColorScheme scheme) {
        if (descriptor.isEmpty() || scheme.isEmpty()) {
            return null;
        }
        boolean needsEntityTexture = false;
        ArrayList<Object> requirements = new ArrayList<>();
        for (ISkinPaintType paintType : descriptor.getPaintTypes()) {
            if (paintType.getDyeType() != null) {
                IPaintColor resolvedColor = scheme.getResolvedColor(paintType);
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
        if (needsEntityTexture && PlayerTextureLoader.getInstance().getTextureModel(scheme.getTexture()) != null) {
            requirements.add(SkinPaintTypes.TEXTURE.getId());
            requirements.add(scheme.getTexture());
        }
        return requirements;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SkinPart getPart() {
        return part;
    }

    public ISkinPartType getType() {
        return part.getType();
    }

    public SkinPartTransform getTransform() {
        return transform;
    }

    public ColorDescriptor getColorInfo() {
        return quads.getColorInfo();
    }

    public OpenVoxelShape getRenderShape() {
        return quads.getRenderShape();
    }

    public int getFaceTotal() {
        return quads.getFaceTotal();
    }

    public void setRenderPolygonOffset(float renderPolygonOffset) {
        this.renderPolygonOffset = renderPolygonOffset;
    }

    public float getRenderPolygonOffset() {
        return renderPolygonOffset;
    }

    public ArrayList<BakedSkinPart> getChildren() {
        return children;
    }

    public SkinProperties getProperties() {
        return part.getProperties();
    }

    public BakedCubeQuads getQuads() {
        return quads;
    }
}
