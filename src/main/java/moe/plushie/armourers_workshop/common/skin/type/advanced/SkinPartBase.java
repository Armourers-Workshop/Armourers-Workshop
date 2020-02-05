package moe.plushie.armourers_workshop.common.skin.type.advanced;

import moe.plushie.armourers_workshop.api.common.skin.Point3D;
import moe.plushie.armourers_workshop.api.common.skin.Rectangle3D;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.AbstractSkinPartTypeBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SkinPartBase extends AbstractSkinPartTypeBase {

    public SkinPartBase(ISkinType baseType) {
        super(baseType);
        this.buildingSpace = new Rectangle3D(-32, -32, -32, 64, 64, 64);
        this.guideSpace = new Rectangle3D(0, 0, 0, 0, 0, 0);
        this.offset = new Point3D(0, 0, 0);
    }

    @Override
    public String getPartName() {
        return "part";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderBuildingGuide(float scale, SkinProperties skinProps, boolean showHelper) {
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
