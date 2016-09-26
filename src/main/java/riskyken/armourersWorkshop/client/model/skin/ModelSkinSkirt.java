package riskyken.armourersWorkshop.client.model.skin;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.common.ApiRegistrar;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;

@SideOnly(Side.CLIENT)
public class ModelSkinSkirt extends AbstractModelSkin {
    
    @Override
    public void render(Entity entity, Skin armourData, boolean showSkinPaint, ISkinDye skinDye, byte[] extraColour, boolean itemRender, double distance) {
        if (armourData == null) { return; }
        
        ArrayList<SkinPart> parts = armourData.getParts();
        
        if (entity != null && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            this.isSneak = player.isSneaking();
            this.isRiding = player.isRiding();
            this.leftArmPose = ArmPose.EMPTY;
            this.rightArmPose = ArmPose.EMPTY;
            /*
            if (player.getHeldItem() != null) {
                this.heldItemRight = 1;
            }
            */
        }
        
        ApiRegistrar.INSTANCE.onRenderEquipment(entity, SkinTypeRegistry.skinSkirt);
        armourData.onUsed();
        
        for (int i = 0; i < parts.size(); i++) {
            SkinPart part = parts.get(i);
            
            GL11.glPushMatrix();
            if (isChild) {
                float f6 = 2.0F;
                GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
                GL11.glTranslatef(0.0F, 24.0F * SCALE, 0.0F);
            }
            
            ApiRegistrar.INSTANCE.onRenderEquipmentPart(entity, part.getPartType());
            
            if (part.getPartType().getPartName().equals("base")) {
                renderSkirt(part, SCALE, skinDye, extraColour, distance);
            }
            
            GL11.glPopMatrix();
        }
        
        GL11.glColor3f(1F, 1F, 1F);
    }
    
    private void renderSkirt(SkinPart part, float scale, ISkinDye skinDye, byte[] extraColour, double distance) {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        
        GL11.glTranslated(0, 12 * scale, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
        if (isSneak) {
            GL11.glTranslated(0, -3 * scale, 4 * scale);
        }
        
        renderPart(part, scale, skinDye, extraColour, distance);
        GL11.glPopMatrix();
    }
}
