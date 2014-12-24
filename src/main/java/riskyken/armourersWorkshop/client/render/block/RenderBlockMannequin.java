package riskyken.armourersWorkshop.client.render.block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.handler.PlayerSkinHandler;
import riskyken.armourersWorkshop.client.render.EquipmentModelRenderer;
import riskyken.armourersWorkshop.client.render.MannequinFakePlayer;
import riskyken.armourersWorkshop.client.render.PlayerSkinInfo;
import riskyken.armourersWorkshop.common.ApiRegistrar;
import riskyken.armourersWorkshop.common.SkinHelper;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockMannequin extends TileEntitySpecialRenderer {
    
    private RenderPlayer renderPlayer;
    private final Minecraft mc;
    
    public RenderBlockMannequin() {
        renderPlayer = (RenderPlayer) RenderManager.instance.entityRenderMap.get(EntityPlayer.class);
        mc = Minecraft.getMinecraft();
    }
    
    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tickTime) {
        TileEntityMannequin te = (TileEntityMannequin) tileEntity;
        MannequinFakePlayer fakePlayer = te.getFakePlayer();
        
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_NORMALIZE);
        float scale = 0.0625F;
        
        int rotaion = te.getRotation();
        
        GL11.glTranslated(x + 0.5D, y + 1.5D, z + 0.5D);
        
        GL11.glScalef(scale * 15, scale * 15, scale * 15);
        GL11.glTranslated(0, scale * -1.6F, 0);
        
        GL11.glScalef(-1, -1, 1);
        GL11.glRotatef(rotaion * 22.5F, 0, 1, 0);
        
        if (te.getIsDoll()) {
            float dollScale = 0.5F;
            GL11.glScalef(dollScale, dollScale, dollScale);
            GL11.glTranslatef(0, scale * 24, 0);
        }
        
        ResourceLocation resourcelocation = AbstractClientPlayer.locationStevePng;
        PlayerSkinInfo skinInfo = null;
        
        if (te.getGameProfile() != null) {
            skinInfo = PlayerSkinHandler.INSTANCE.getPlayersNakedData(te.getGameProfile().getId());
            resourcelocation = SkinHelper.getSkinResourceLocation(te.getGameProfile());
            if (te.getGameProfile() != null & te.getWorldObj() != null) {
                if (fakePlayer == null) {
                    fakePlayer = new MannequinFakePlayer(te.getWorldObj(),te.getGameProfile());
                    fakePlayer.posX = x;
                    fakePlayer.posY = y;
                    fakePlayer.posZ = z;
                    fakePlayer.prevPosX = x;
                    fakePlayer.prevPosY = y;
                    fakePlayer.prevPosZ = z;
                    
                    te.setFakePlayer(fakePlayer);
                }
            }
        }
        
        float f6 = 2.0F;
        if (fakePlayer != null) {
            renderPlayer.modelBipedMain.isChild = te.getBipedRotations().isChild;
            fakePlayer.rotationPitch = (float) Math.toDegrees(te.getBipedRotations().head.rotationX);
            fakePlayer.prevRotationPitch = (float) Math.toDegrees(te.getBipedRotations().head.rotationX);
            fakePlayer.rotationYawHead = (float) Math.toDegrees(te.getBipedRotations().head.rotationY);
            fakePlayer.prevRotationYawHead = (float) Math.toDegrees(te.getBipedRotations().head.rotationY);
            fakePlayer.onUpdate();
            
            fakePlayer.ticksExisted = Minecraft.getMinecraft().thePlayer.ticksExisted;
            
            if (te.getBipedRotations() != null) {
                te.getBipedRotations().applyRotationsToBiped(renderPlayer.modelBipedMain);
            }
            
            RenderPlayerEvent.Pre preEvent = new RenderPlayerEvent.Pre(fakePlayer, renderPlayer, tickTime);
            
            RenderPlayerEvent.Specials.Pre preEventSpecials = new RenderPlayerEvent.Specials.Pre(fakePlayer, renderPlayer, tickTime);

            if (renderPlayer.modelBipedMain.isChild) {
                GL11.glPushMatrix();
                GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
                GL11.glTranslatef(0.0F, 16.0F * scale, 0.0F);
            }
            GL11.glDisable(GL11.GL_CULL_FACE);
            MinecraftForge.EVENT_BUS.post(preEvent);
            MinecraftForge.EVENT_BUS.post(preEventSpecials);
            GL11.glEnable(GL11.GL_CULL_FACE);
            if (renderPlayer.modelBipedMain.isChild) {
                GL11.glPopMatrix();
            }
        }
        
        if (skinInfo != null && skinInfo.getNakedInfo().isNaked) {
            if (!skinInfo.bindNomalSkin()) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(resourcelocation);
            }
        } else {
            Minecraft.getMinecraft().getTextureManager().bindTexture(resourcelocation);
        }
        
        ApiRegistrar.INSTANCE.onRenderMannequin(tileEntity, te.getGameProfile());
        
        renderPlayer.modelBipedMain.bipedRightArm.setRotationPoint(-5.0F, 2.0F , 0.0F);
        renderPlayer.modelBipedMain.bipedLeftArm.setRotationPoint(5.0F, 2.0F , 0.0F);
        renderPlayer.modelBipedMain.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        renderPlayer.modelBipedMain.bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
        renderPlayer.modelBipedMain.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
        renderPlayer.modelBipedMain.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
        
        te.getBipedRotations().applyRotationsToBiped(renderPlayer.modelBipedMain);
        
        if (te.getBipedRotations().isChild) {
            GL11.glPushMatrix();
            GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
            GL11.glTranslatef(0.0F, 16.0F * scale, 0.0F);
            renderPlayer.modelBipedMain.bipedHead.render(scale);
            GL11.glDisable(GL11.GL_CULL_FACE);
            renderPlayer.modelBipedMain.bipedHeadwear.render(scale);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
            GL11.glTranslatef(0.0F, 24.0F * scale, 0.0F);
            renderPlayer.modelBipedMain.bipedBody.render(scale);
            renderPlayer.modelBipedMain.bipedRightArm.render(scale);
            renderPlayer.modelBipedMain.bipedLeftArm.render(scale);
            renderPlayer.modelBipedMain.bipedRightLeg.render(scale);
            renderPlayer.modelBipedMain.bipedLeftLeg.render(scale);
            GL11.glPopMatrix();
        } else {
            renderPlayer.modelBipedMain.bipedHead.render(scale);
            renderPlayer.modelBipedMain.bipedBody.render(scale);
            renderPlayer.modelBipedMain.bipedRightArm.render(scale);
            renderPlayer.modelBipedMain.bipedLeftArm.render(scale);
            renderPlayer.modelBipedMain.bipedRightLeg.render(scale);
            renderPlayer.modelBipedMain.bipedLeftLeg.render(scale);
            GL11.glDisable(GL11.GL_CULL_FACE);
            renderPlayer.modelBipedMain.bipedHeadwear.render(scale);
            GL11.glEnable(GL11.GL_CULL_FACE);
        }
        
        renderPlayer.modelBipedMain.bipedLeftLeg.rotateAngleZ = 0F;
        renderPlayer.modelBipedMain.bipedRightLeg.rotateAngleZ = 0F;
        renderPlayer.modelBipedMain.bipedHead.rotateAngleZ = 0F;
        renderPlayer.modelBipedMain.bipedHeadwear.rotateAngleZ = 0F;
        
        //renderPlayer.modelBipedMain.render(fakePlayer, 0, 0, 0, 0, 0, 0);
        //modelMannequin.render(te.getBipedRotations(), true, scale);
        
        if (fakePlayer != null) {
            RenderPlayerEvent.Post postEvent = new RenderPlayerEvent.Post(fakePlayer, renderPlayer, tickTime);
            RenderPlayerEvent.Specials.Post postEvenSpecialst = new RenderPlayerEvent.Specials.Post(fakePlayer, renderPlayer, tickTime);
            if (renderPlayer.modelBipedMain.isChild) {
                GL11.glPushMatrix();
                GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
                GL11.glTranslatef(0.0F, 16.0F * scale, 0.0F);
            }
            GL11.glDisable(GL11.GL_CULL_FACE);
            MinecraftForge.EVENT_BUS.post(postEvent);
            MinecraftForge.EVENT_BUS.post(postEvenSpecialst);
            GL11.glEnable(GL11.GL_CULL_FACE);
            if (renderPlayer.modelBipedMain.isChild) {
                GL11.glPopMatrix();
            }
        }

        EquipmentModelRenderer.INSTANCE.renderMannequinEquipment(((TileEntityMannequin)tileEntity), renderPlayer.modelBipedMain);
        GL11.glDisable(GL11.GL_NORMALIZE);
        GL11.glPopMatrix();
    }
}
