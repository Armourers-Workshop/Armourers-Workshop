package riskyken.armourersWorkshop.client.model;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.customarmor.ArmourerType;
import riskyken.armourersWorkshop.common.customarmor.data.CustomArmourBlockData;
import riskyken.armourersWorkshop.common.customarmor.data.CustomArmourItemData;
import riskyken.armourersWorkshop.common.customarmor.data.CustomArmourPartData;
import riskyken.armourersWorkshop.proxies.ClientProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCustomArmourHead extends ModelCustomArmour {
    
    @Override
    public void render(Entity entity, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale) {
        CustomArmourItemData armourData = ClientProxy.getPlayerCustomArmour(entity, ArmourerType.HEAD);
        if (armourData == null) { return; }
        EntityPlayer player = (EntityPlayer) entity;
        
        ArrayList<CustomArmourPartData> parts = armourData.getParts();
        
        ArrayList<CustomArmourBlockData> armourBlockData = armourData.getParts().get(0).getArmourData();
        
        this.isSneak = player.isSneaking();
        this.isRiding = player.isRiding();
        this.heldItemRight = 0;
        if (player.getHeldItem() != null) {
            this.heldItemRight = 1;
        }
        
        bindArmourTexture();
        
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glRotatef(p_78088_5_, 0, 1, 0);
        GL11.glRotatef(p_78088_6_, 1, 0, 0);
        
        if (isSneak) {
            GL11.glTranslated(0, 1 * scale, 0);
        }
        
        renderHead(armourData.getParts().get(0), scale);
        
        GL11.glPopMatrix();
        GL11.glColor3f(1F, 1F, 1F);
    }
    
    private void renderHead(CustomArmourPartData part, float scale) {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        renderPart(part.getArmourData(), scale);
        GL11.glPopMatrix();
    }
}
