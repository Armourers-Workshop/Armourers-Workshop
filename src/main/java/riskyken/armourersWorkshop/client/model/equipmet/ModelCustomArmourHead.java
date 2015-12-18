package riskyken.armourersWorkshop.client.model.equipmet;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.common.ApiRegistrar;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;

@SideOnly(Side.CLIENT)
public class ModelCustomArmourHead extends AbstractModelCustomEquipment {
    
    @Override
    public void render(Entity entity, Skin armourData, float limb1, float limb2, float limb3, float headY, float headX) {
        setRotationAngles(limb1, limb2, limb3, headY, headX, SCALE, entity);
        render(entity, armourData, false, null, null);
    }
    
    @Override
    public void render(Entity entity, ModelBiped modelBiped, Skin armourData, boolean showSkinPaint, ISkinDye skinDye, byte[] extraColour) {
        setRotationFromModelBiped(modelBiped);
        render(entity, armourData, showSkinPaint, skinDye, extraColour);
    }
    
    @Override
    public void render(Entity entity, Skin armourData, boolean showSkinPaint, ISkinDye skinDye, byte[] extraColour) {
        if (armourData == null) { return; }
        
        
        if (entity != null && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            this.isSneak = player.isSneaking();
            this.isRiding = player.isRiding();
            this.heldItemRight = 0;
            if (player.getHeldItem() != null) {
                this.heldItemRight = 1;
            }
        }
        
        ApiRegistrar.INSTANCE.onRenderEquipment(entity, SkinTypeRegistry.skinHead);
        armourData.onUsed();
        
        if (armourData.hasPaintData() & showSkinPaint) {
            armourData.blindPaintTexture();
            GL11.glDisable(GL11.GL_CULL_FACE);
            bipedHead.render(SCALE);
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
        
        if (armourData.getParts().size() > 0) {
            ApiRegistrar.INSTANCE.onRenderEquipmentPart(entity, armourData.getParts().get(0).getPartType());
            GL11.glPushMatrix();
            if (isChild) {
                float f6 = 2.0F;
                GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
                GL11.glTranslatef(0.0F, 16.0F * SCALE, 0.0F);
            }
            
            GL11.glColor3f(1F, 1F, 1F);
            GL11.glRotated(Math.toDegrees(bipedHead.rotateAngleZ), 0, 0, 1);
            GL11.glRotated(Math.toDegrees(bipedHead.rotateAngleY), 0, 1, 0);
            GL11.glRotated(Math.toDegrees(bipedHead.rotateAngleX), 1, 0, 0);
            
            if (isSneak) {
                GL11.glTranslated(0, 1 * SCALE, 0);
            }

            renderHead(armourData.getParts().get(0), SCALE, skinDye, extraColour);
            
            GL11.glPopMatrix();
        }

        GL11.glColor3f(1F, 1F, 1F);
    }
    
    private void renderHead(SkinPart part, float scale, ISkinDye skinDye, byte[] extraColours) {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        renderPart(part, scale, skinDye, extraColours);
        GL11.glPopMatrix();
    }
}
