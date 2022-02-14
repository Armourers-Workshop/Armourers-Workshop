package moe.plushie.armourers_workshop.core.bake;

import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.render.SkinRenderType;
import moe.plushie.armourers_workshop.core.skin.data.Palette;
import moe.plushie.armourers_workshop.core.skin.data.SkinPart;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.CustomVoxelShape;
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

    public Object requirements(Palette palette) {
        if (descriptor.isEmpty() || palette.isEmpty()) {
            return null;
        }
        ArrayList<Object> requirements = new ArrayList<>();
        for (ISkinPaintType paintType : descriptor.getPaintTypes()) {
            if (paintType == SkinPaintTypes.TEXTURE && palette.isTextureReady()) {
                requirements.add(paintType.getId());
                requirements.add(palette.getTexture());
            } else if (paintType.getDyeType() != null) {
                requirements.add(paintType.getId());
                requirements.add(palette.getResolvedColor(paintType));
            }
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
