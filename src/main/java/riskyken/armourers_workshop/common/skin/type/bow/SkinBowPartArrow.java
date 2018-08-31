package riskyken.armourers_workshop.common.skin.type.bow;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourers_workshop.api.common.skin.Point3D;
import riskyken.armourers_workshop.api.common.skin.Rectangle3D;
import riskyken.armourers_workshop.api.common.skin.type.ISkinType;
import riskyken.armourers_workshop.client.model.armourer.ModelArrow;
import riskyken.armourers_workshop.common.skin.type.AbstractSkinPartTypeBase;

public class SkinBowPartArrow extends AbstractSkinPartTypeBase {

    public SkinBowPartArrow(ISkinType baseType) {
        super(baseType);
        this.buildingSpace = new Rectangle3D(-5, -5, -2, 11, 11, 16);
        this.guideSpace = new Rectangle3D(0, 0, 0, 0, 0, 0);
        this.offset = new Point3D(0, 0, 21);
    }

    @Override
    public String getPartName() {
        return "arrow";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderBuildingGuide(float scale, boolean showSkinOverlay, boolean showHelper) {
        GL11.glTranslated(0, this.buildingSpace.getY() * scale, 0);
        GL11.glTranslated(0, -this.guideSpace.getY() * scale, 0);
        if (showHelper) {
            ModelArrow.MODEL.render(scale, true);
        }
        GL11.glTranslated(0, this.guideSpace.getY() * scale, 0);
        GL11.glTranslated(0, -this.buildingSpace.getY() * scale, 0);
    }
}
