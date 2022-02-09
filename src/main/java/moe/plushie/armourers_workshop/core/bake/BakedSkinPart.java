package moe.plushie.armourers_workshop.core.bake;

import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.render.SkinRenderType;
import moe.plushie.armourers_workshop.core.utils.CustomVoxelShape;
import moe.plushie.armourers_workshop.core.skin.data.SkinPart;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.function.BiConsumer;

@OnlyIn(Dist.CLIENT)
public class BakedSkinPart {

    private final SkinPart part;
    private final PackedQuad quads;

    private int id = 0;

    public BakedSkinPart(SkinPart part, PackedQuad quads) {
        this.part = part;
        this.quads = quads;
    }

    public void forEach(BiConsumer<SkinRenderType, ArrayList<ColouredFace>> action) {
        quads.forEach(action);
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

    public PackedColorInfo getColorInfo() {
        return quads.getColorInfo();
    }

    public CustomVoxelShape getRenderShape() {
        return quads.getRenderShape();
    }

    public SkinProperties getProperties() { return part.getProperties(); }

    public boolean isModelOverridden() {
        return part.getType().isModelOverridden(part.getProperties());
    }
}
