package moe.plushie.armourers_workshop.core.render.bake;

import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.render.buffer.SkinRenderType;
import moe.plushie.armourers_workshop.core.skin.data.SkinPalette;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperties;
import moe.plushie.armourers_workshop.core.utils.ColorDescriptor;
import moe.plushie.armourers_workshop.core.utils.PaintColor;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.CustomVoxelShape;
import moe.plushie.armourers_workshop.core.AWCore;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.function.BiConsumer;

@OnlyIn(Dist.CLIENT)
public class BakedSkinPart {

    private final SkinPart part;
    private final PackedQuad quads;
    private final ColorDescriptor descriptor;

    private int id = 0;

    public BakedSkinPart(SkinPart part, PackedQuad quads) {
        this.part = part;
        this.quads = quads;
        this.descriptor = quads.getColorInfo();
    }

    public void forEach(BiConsumer<SkinRenderType, ArrayList<ColouredFace>> action) {
        quads.forEach(action);
    }

    public Object requirements(SkinPalette palette) {
        if (descriptor.isEmpty() || palette.isEmpty()) {
            return null;
        }
        boolean needsEntityTexture = false;
        ArrayList<Object> requirements = new ArrayList<>();
        for (ISkinPaintType paintType : descriptor.getPaintTypes()) {
            if (paintType.getDyeType() != null) {
                PaintColor resolvedColor = palette.getResolvedColor(paintType);
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
        if (needsEntityTexture && AWCore.bakery.getEntityTexture(palette.getTexture()) != null) {
            requirements.add(SkinPaintTypes.TEXTURE.getId());
            requirements.add(palette.getTexture());
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

    public ColorDescriptor getColorInfo() {
        return quads.getColorInfo();
    }

    public CustomVoxelShape getRenderShape() {
        return quads.getRenderShape();
    }

    public SkinProperties getProperties() {
        return part.getProperties();
    }

    public boolean isModelOverridden() {
        return part.getType().isModelOverridden(part.getProperties());
    }
}
