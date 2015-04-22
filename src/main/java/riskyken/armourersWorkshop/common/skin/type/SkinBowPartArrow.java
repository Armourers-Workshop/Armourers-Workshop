package riskyken.armourersWorkshop.common.skin.type;

import javax.vecmath.Point3i;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.model.armourer.ModelHand;
import riskyken.armourersWorkshop.common.Rectangle3D;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SkinBowPartArrow extends AbstractSkinPartTypeBase {

    @SideOnly(Side.CLIENT)
    private static final ModelHand partModel = new ModelHand();
    
    private Rectangle3D buildingSpace;
    private Rectangle3D guideSpace;
    private Point3i offset;
    
    public SkinBowPartArrow(ISkinType baseType) {
        super(baseType);
        this.buildingSpace = new Rectangle3D(-4, -4, -14, 8, 8, 28);
        this.guideSpace = new Rectangle3D(0, 0, 0, 1, 1, 1);
        this.offset = new Point3i(25, 0, 0);
    }

    @Override
    public String getPartName() {
        return "arrow";
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
        //partModel.render(scale);
        GL11.glTranslated(0, this.guideSpace.y * scale, 0);
        GL11.glTranslated(0, -this.buildingSpace.y * scale, 0);
    }
}
