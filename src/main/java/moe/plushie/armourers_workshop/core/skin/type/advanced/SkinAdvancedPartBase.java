package moe.plushie.armourers_workshop.core.skin.type.advanced;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinProperties;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.type.AbstractSkinPartType;
import moe.plushie.armourers_workshop.core.skin.type.Point3D;
import moe.plushie.armourers_workshop.core.skin.type.Rectangle3D;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SkinAdvancedPartBase extends AbstractSkinPartType {

    public SkinAdvancedPartBase() {
        super();
        this.buildingSpace = new Rectangle3D(-32, -32, -32, 64, 64, 64);
        this.guideSpace = new Rectangle3D(0, 0, 0, 0, 0, 0);
        this.offset = new Point3D(0, 0, 0);
    }

    @Override
    public void renderBuildingGuide(float scale, ISkinProperties skinProps, boolean showHelper) {
    }
    
    @Override
    public int getMinimumMarkersNeeded() {
        return 0;
    }
    
    @Override
    public int getMaximumMarkersNeeded() {
        return 0;
    }
}
