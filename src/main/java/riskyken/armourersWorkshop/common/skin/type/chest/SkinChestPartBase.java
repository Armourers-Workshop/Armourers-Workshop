package riskyken.armourersWorkshop.common.skin.type.chest;

import java.awt.Point;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.api.common.IPoint3D;
import riskyken.armourersWorkshop.api.common.IRectangle3D;
import riskyken.armourersWorkshop.api.common.skin.Point3D;
import riskyken.armourersWorkshop.api.common.skin.Rectangle3D;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartTypeTextured;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.model.armourer.ModelChest;
import riskyken.armourersWorkshop.common.skin.type.AbstractSkinPartTypeBase;

public class SkinChestPartBase extends AbstractSkinPartTypeBase implements ISkinPartTypeTextured {
    
    public SkinChestPartBase(ISkinType baseType) {
        super(baseType);
        this.buildingSpace = new Rectangle3D(-6, -24, -30, 12, 38, 60);
        this.guideSpace = new Rectangle3D(-4, -12, -2, 8, 12, 4);
        this.offset = new Point3D(0, -1, 0);
    }
    
    @Override
    public String getPartName() {
        return "base";
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void renderBuildingGuide(float scale, boolean showSkinOverlay, boolean showHelper) {
        GL11.glTranslated(0, this.buildingSpace.getY() * scale, 0);
        GL11.glTranslated(0, -this.guideSpace.getY() * scale, 0);
        ModelChest.MODEL.renderChest(scale);
        GL11.glTranslated(0, this.guideSpace.getY() * scale, 0);
        GL11.glTranslated(0, -this.buildingSpace.getY() * scale, 0);
    }

    @Override
    public Point getTextureLocation() {
        return new Point(16, 16);
    }

    @Override
    public boolean isTextureMirrored() {
        return false;
    }

    @Override
    public IPoint3D getTextureModelSize() {
        return new Point3D(8, 12, 4);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IPoint3D getItemRenderOffset() {
        return new Point3D(0, 0, 0);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IRectangle3D getItemRenderTextureBounds() {
        return new Rectangle3D(-4, 0, -2, 8, 12, 4);
    }
}
