package riskyken.armourersWorkshop.common.skin.type;

import javax.vecmath.Point3i;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.skin.Rectangle3D;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.model.armourer.ModelHand;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SkinSwordPartBase extends AbstractSkinPartTypeBase {

    @SideOnly(Side.CLIENT)
    private static final ModelHand partModel = new ModelHand();
    
    private Rectangle3D buildingSpace;
    private Rectangle3D guideSpace;
    private Point3i offset;
    
    public SkinSwordPartBase(ISkinType baseType) {
        super(baseType);
        this.buildingSpace = new Rectangle3D(-10, -20, -18, 20, 62, 36);
        this.guideSpace = new Rectangle3D(-2, -2, 2, 4, 4, 8);
        //Offset -1 to match old skin system.
        this.offset = new Point3i(0, -1, 0);
    }
    
    @Override
    public String getPartName() {
        return "base";
    }

    @Override
    public Rectangle3D getBuildingSpace() {
        return this.buildingSpace;
    }

    @Override
    public Rectangle3D getGuideSpace() {
        return this.guideSpace;
    }

    @Override
    public Point3i getOffset() {
        return this.offset;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderBuildingGuide(float scale, boolean showSkinOverlay, boolean showHelper) {
        GL11.glTranslated(0, this.buildingSpace.y * scale, 0);
        GL11.glTranslated(0, -this.guideSpace.y * scale, 0);
        partModel.render(scale);
        GL11.glTranslated(0, this.guideSpace.y * scale, 0);
        GL11.glTranslated(0, -this.buildingSpace.y * scale, 0);
    }
}
