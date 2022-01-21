package moe.plushie.armourers_workshop.core.render.other;

import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.model.bake.ColouredFace;
import moe.plushie.armourers_workshop.core.model.bake.PackedColorInfo;
import moe.plushie.armourers_workshop.core.render.SkinRenderType;
import moe.plushie.armourers_workshop.core.skin.data.SkinPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.function.BiConsumer;

@OnlyIn(Dist.CLIENT)
public class BakedSkinPart {

    private final SkinPart part;
    private boolean usesDyeChannel;
    private boolean usesTextureChannel;

    public BakedSkinPart(SkinPart part) {
        this.part = part;
    }

    public void forEach(BiConsumer<SkinRenderType, ArrayList<ColouredFace>> action) {
        part.getQuads().forEach(action);
    }


    public Object getDye() {
        return null;
    }

    public int getId() {
        return part.getId();
    }

    public SkinPart getPart() {
        return part;
    }

    public ISkinPartType getType() {
        return part.getType();
    }

    public PackedColorInfo getColorInfo() {
        return part.getQuads().colorInfo;
    }


    @OnlyIn(Dist.CLIENT)
    public SkinRenderShape getRenderShape() {
        return part.getRenderShape();
    }

}
