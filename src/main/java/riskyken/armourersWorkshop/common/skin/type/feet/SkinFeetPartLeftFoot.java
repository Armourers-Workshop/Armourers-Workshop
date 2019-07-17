package riskyken.armourersWorkshop.common.skin.type.feet;

import java.awt.Point;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.api.common.IPoint3D;
import riskyken.armourersWorkshop.api.common.IRectangle3D;
import riskyken.armourersWorkshop.api.common.skin.Point3D;
import riskyken.armourersWorkshop.api.common.skin.Rectangle3D;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartTypeTextured;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.model.armourer.ModelLegs;
import riskyken.armourersWorkshop.common.skin.type.AbstractSkinPartTypeBase;

public class SkinFeetPartLeftFoot extends AbstractSkinPartTypeBase implements ISkinPartTypeTextured  {
    
    public SkinFeetPartLeftFoot(ISkinType baseType) {
        super(baseType);
        this.buildingSpace = new Rectangle3D(-8, -13, -8, 11, 5, 16);
        this.guideSpace = new Rectangle3D(-2, -12, -2, 4, 12, 4);
        this.offset = new Point3D(6, 0, 0);
    }
    
    @Override
    public String getPartName() {
        return "leftFoot";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderBuildingGuide(float scale, boolean showSkinOverlay, boolean showHelper) {
        GL11.glTranslated(0, this.buildingSpace.getY() * scale, 0);
        GL11.glTranslated(0, -this.guideSpace.getY() * scale, 0);
        ModelLegs.MODEL.renderLeftLeft(scale);
        GL11.glTranslated(0, this.guideSpace.getY() * scale, 0);
        GL11.glTranslated(0, -this.buildingSpace.getY() * scale, 0);
    }

    @Override
    public Point getTextureLocation() {
        return new Point(0, 16);
    }

    @Override
    public boolean isTextureMirrored() {
        return true;
    }

    @Override
    public IPoint3D getTextureModelSize() {
        return new Point3D(4, 12, 4);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IPoint3D getItemRenderOffset() {
        return new Point3D(2, 12, 0);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IRectangle3D getItemRenderTextureBounds() {
        return new Rectangle3D(0, 12, -2, 4, 12, 4);
    }
}
