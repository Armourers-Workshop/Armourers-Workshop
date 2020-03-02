package moe.plushie.armourers_workshop.common.skin.type.horse;

import moe.plushie.armourers_workshop.api.common.skin.Point3D;
import moe.plushie.armourers_workshop.api.common.skin.Rectangle3D;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinProperties;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.skin.type.AbstractSkinPartTypeBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SkinHorsePartBase extends AbstractSkinPartTypeBase {

    public SkinHorsePartBase(ISkinType baseType) {
        super(baseType);
        this.buildingSpace = new Rectangle3D(-32, -32, -32, 64, 64, 64);
        this.guideSpace = new Rectangle3D(-5, -8, -19, 10, 10, 24);
        this.offset = new Point3D(0, 0, 0);
    }

    @Override
    public String getPartName() {
        return "base";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderBuildingGuide(float scale, ISkinProperties skinProps, boolean showHelper) {
    }
}
