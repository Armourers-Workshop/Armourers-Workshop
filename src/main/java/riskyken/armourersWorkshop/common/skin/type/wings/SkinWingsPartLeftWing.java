package riskyken.armourersWorkshop.common.skin.type.wings;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.api.common.skin.Point3D;
import riskyken.armourersWorkshop.api.common.skin.Rectangle3D;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.model.armourer.ModelChest;
import riskyken.armourersWorkshop.common.skin.type.AbstractSkinPartTypeBase;

public class SkinWingsPartLeftWing extends AbstractSkinPartTypeBase {

    public SkinWingsPartLeftWing(ISkinType baseType) {
        super(baseType);
        this.buildingSpace = new Rectangle3D(-32, -24, -20, 32, 48, 48);
        this.guideSpace = new Rectangle3D(-4, -12, -4, 8, 12, 4);
        this.offset = new Point3D(0, -1, 0);
    }

    @Override
    public String getPartName() {
        return "leftWing";
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void renderBuildingGuide(float scale, boolean showSkinOverlay, boolean showHelper) {
        GL11.glTranslated(0, this.buildingSpace.getY() * scale, 0);
        
        GL11.glTranslated(0, -this.guideSpace.getY() * scale, 0);
        GL11.glTranslated(0, 0, -2 * scale);
        
        ModelChest.MODEL.renderChest(scale);
        
        GL11.glTranslated(0, 0, 2 * scale);
        GL11.glTranslated(0, this.guideSpace.getY() * scale, 0);
        
        GL11.glTranslated(0, -this.buildingSpace.getY() * scale, 0);
    }
    
    @Override
    public int getMaximumMarkersNeeded() {
        return 1;
    }
    
    @Override
    public int getMinimumMarkersNeeded() {
        return 1;
    }
}
