package riskyken.armourersWorkshop.client.model.skin;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.client.skin.ClientSkinPaintCache;
import riskyken.armourersWorkshop.client.skin.SkinModelTexture;
import riskyken.armourersWorkshop.common.ApiRegistrar;
import riskyken.armourersWorkshop.common.painting.PaintingHelper;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;

@SideOnly(Side.CLIENT)
public class ModelSkinHead extends AbstractModelSkin {
    
    @Override
    public void render(Entity entity, Skin skin, boolean showSkinPaint, ISkinDye skinDye, byte[] extraColour, boolean itemRender) {
        if (skin == null) {
            return;
        }
        
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
        
        ApiRegistrar.INSTANCE.onRenderEquipment(entity, SkinTypeRegistry.skinHead);
        skin.onUsed();
        RenderHelper.enableGUIStandardItemLighting();
        
        if (skin.hasPaintData() & showSkinPaint) {
            if (extraColour == null) {
                extraColour = PaintingHelper.getLocalPlayerExtraColours();
            }
            SkinModelTexture st = ClientSkinPaintCache.INSTANCE.getTextureForSkin(skin, skinDye, extraColour);
            st.bindTexture();
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            bipedHead.render(SCALE);
            GL11.glPopAttrib();
        }
        
        if (skin.getParts().size() > 0) {
            ApiRegistrar.INSTANCE.onRenderEquipmentPart(entity, skin.getParts().get(0).getPartType());
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

            renderHead(skin.getParts().get(0), SCALE, skinDye, extraColour);
            
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
