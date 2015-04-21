package riskyken.armourersWorkshop.common.equipment.skin;

import javax.vecmath.Point3i;

import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.equipment.skin.IEquipmentSkinPart;
import riskyken.armourersWorkshop.api.common.equipment.skin.IEquipmentSkinType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class EquipmentSkinTypeBase implements IEquipmentSkinType {

    private int id = -1;
    @SideOnly(Side.CLIENT)
    protected IIcon icon = null;
    
    @SideOnly(Side.CLIENT)
    @Override
    public void renderBuildingGuide(float scale, boolean showSkinOverlay, boolean showHelper) {
        for (int i = 0; i < this.getSkinParts().size(); i++) {
            IEquipmentSkinPart skinPart = this.getSkinParts().get(i);
            Point3i partOffset = skinPart.getOffset();
            GL11.glTranslated(partOffset.x * scale, partOffset.y * scale, partOffset.z * scale);
            skinPart.renderBuildingGuide(scale, showSkinOverlay, showHelper);
            GL11.glTranslated(-partOffset.x * scale, -partOffset.y * scale, -partOffset.z * scale);
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void renderBuildingGrid(float scale) {
        for (int i = 0; i < this.getSkinParts().size(); i++) {
            IEquipmentSkinPart skinPart = this.getSkinParts().get(i);
            Point3i partOffset = skinPart.getOffset();
            GL11.glTranslated(partOffset.x * scale, partOffset.y * scale, partOffset.z * scale);
            skinPart.renderBuildingGrid(scale);
            GL11.glTranslated(-partOffset.x * scale, -partOffset.y * scale, -partOffset.z * scale);
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon() {
        return this.icon;
    }
    
    @Override
    public boolean showSkinOverlayCheckbox() {
        return false;
    }
    
    @Override
    public int getVanillaArmourSlotId() {
        return -1;
    }
    
    @Override
    public int getId() {
        return this.id;
    }
    
    @Override
    public void setId(int id) {
        this.id = id;
    }
}
