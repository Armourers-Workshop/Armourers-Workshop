package riskyken.armourers_workshop.common.skin.type.legs;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourers_workshop.api.common.skin.Point3D;
import riskyken.armourers_workshop.api.common.skin.Rectangle3D;
import riskyken.armourers_workshop.api.common.skin.type.ISkinType;
import riskyken.armourers_workshop.client.model.armourer.ModelLegs;
import riskyken.armourers_workshop.common.skin.type.AbstractSkinPartTypeBase;

public class SkinLegsPartSkirt extends AbstractSkinPartTypeBase {

    public SkinLegsPartSkirt(ISkinType baseType) {
        super(baseType);
        this.buildingSpace = new Rectangle3D(-10, -12, -10, 20, 15, 20);
        this.guideSpace = new Rectangle3D(-4, -12, -2, 8, 12, 4);
      //Offset -1 to match old skin system.
        this.offset = new Point3D(0, -1, 20);
    }

    @Override
    public String getPartName() {
        return "skirt";
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void renderBuildingGuide(float scale, boolean showSkinOverlay, boolean showHelper) {
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
