package riskyken.armourersWorkshop.client.model;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.customarmor.ArmourerType;
import riskyken.armourersWorkshop.common.customarmor.data.CustomArmourItemData;
import riskyken.armourersWorkshop.common.customarmor.data.CustomArmourPartData;
import riskyken.armourersWorkshop.proxies.ClientProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCustomArmourSkirt extends ModelCustomArmour {
    
    @Override
    public void render(Entity entity, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
        setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entity);
        CustomArmourItemData armourData = ClientProxy.getPlayerCustomArmour(entity, ArmourerType.SKIRT);
        if (armourData == null) { return; }
        ArrayList<CustomArmourPartData> parts = armourData.getParts();
        EntityPlayer player = (EntityPlayer) entity;
        
        this.isSneak = player.isSneaking();
        this.isRiding = player.isRiding();
        this.heldItemRight = 0;
        if (player.getHeldItem() != null) {
            this.heldItemRight = 1;
        }
        
        bindArmourTexture();
        
        for (int i = 0; i < parts.size(); i++) {
            CustomArmourPartData part = parts.get(i);
            switch (part.getArmourPart()) {
            case SKIRT:
                renderSkirt(part, scale);
                break;
            default:
                break;
            }
        }
        
        GL11.glColor3f(1F, 1F, 1F);
    }
    
    private void renderSkirt(CustomArmourPartData part, float scale) {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
      
        GL11.glTranslated(0, 11 * scale, 0);
        
        if (isSneak) {
            GL11.glTranslated(0, -3 * scale, 4 * scale);
        }
        
        renderPart(part.getArmourData(), scale);
        GL11.glPopMatrix();
    }
}
