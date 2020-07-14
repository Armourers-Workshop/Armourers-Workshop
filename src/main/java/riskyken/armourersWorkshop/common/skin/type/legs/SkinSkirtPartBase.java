package riskyken.armourersWorkshop.common.skin.type.legs;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.api.common.skin.Point3D;
import riskyken.armourersWorkshop.api.common.skin.Rectangle3D;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinProperties;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.model.armourer.ModelLegs;
import riskyken.armourersWorkshop.common.skin.type.AbstractSkinPartTypeBase;

public class SkinSkirtPartBase extends AbstractSkinPartTypeBase {

    public SkinSkirtPartBase(ISkinType baseType) {
        super(baseType);
        this.buildingSpace = new Rectangle3D(-10, -12, -10, 20, 15, 20);
        this.guideSpace = new Rectangle3D(-4, -12, -2, 8, 12, 4);
        // Offset -1 to match old skin system.
        this.offset = new Point3D(0, -1, 16);
    }

    @Override
    public String getPartName() {
        return "base";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderBuildingGuide(float scale, ISkinProperties skinProps, boolean showHelper) {
        GL11.glTranslated(0, this.buildingSpace.getY() * scale, 0);
        GL11.glTranslated(0, -this.guideSpace.getY() * scale, 0);
        GL11.glTranslated(2 * scale, 0, 0);
        ModelLegs.MODEL.renderLeftLeft(scale);
        GL11.glTranslated(-4 * scale, 0, 0);
        ModelLegs.MODEL.renderRightLeg(scale);
        GL11.glTranslated(2 * scale, 0, 0);
        GL11.glTranslated(0, this.guideSpace.getY() * scale, 0);
        GL11.glTranslated(0, -this.buildingSpace.getY() * scale, 0);
    }
}
