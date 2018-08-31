package riskyken.armourers_workshop.common.skin.type.chest;

import java.awt.Point;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourers_workshop.api.common.IPoint3D;
import riskyken.armourers_workshop.api.common.IRectangle3D;
import riskyken.armourers_workshop.api.common.skin.Point3D;
import riskyken.armourers_workshop.api.common.skin.Rectangle3D;
import riskyken.armourers_workshop.api.common.skin.type.ISkinPartTypeTextured;
import riskyken.armourers_workshop.api.common.skin.type.ISkinType;
import riskyken.armourers_workshop.client.model.armourer.ModelChest;
import riskyken.armourers_workshop.common.skin.type.AbstractSkinPartTypeBase;

public class SkinChestPartLeftArm extends AbstractSkinPartTypeBase implements ISkinPartTypeTextured {
    
    public SkinChestPartLeftArm(ISkinType baseType) {
        super(baseType);
        this.buildingSpace = new Rectangle3D(-11, -16, -14, 14, 32, 28);
        this.guideSpace = new Rectangle3D(-3, -10, -2, 4, 12, 4);
        this.offset = new Point3D(10, -7, 0);
    }
    
    @Override
    public String getPartName() {
        return "leftArm";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderBuildingGuide(float scale, boolean showSkinOverlay, boolean showHelper) {
        GL11.glTranslated(0, this.buildingSpace.getY() * scale, 0);
        GL11.glTranslated(0, -this.guideSpace.getY() * scale, 0);
        ModelChest.MODEL.renderLeftArm(scale);
        GL11.glTranslated(0, this.guideSpace.getY() * scale, 0);
        GL11.glTranslated(0, -this.buildingSpace.getY() * scale, 0);
    }

    @Override
    public Point getTextureLocation() {
        return new Point(40, 16);
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
        return new Point3D(5, 2, 0);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IRectangle3D getItemRenderTextureBounds() {
        return new Rectangle3D(-8, 0, -2, 4, 12, 4);
    }
}
