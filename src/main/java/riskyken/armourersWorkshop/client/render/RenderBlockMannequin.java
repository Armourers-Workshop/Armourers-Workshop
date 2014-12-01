package riskyken.armourersWorkshop.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.model.ModelMannequin;
import riskyken.armourersWorkshop.common.ApiRegistrar;
import riskyken.armourersWorkshop.common.SkinHelper;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockMannequin extends TileEntitySpecialRenderer {
    
    private final ModelMannequin modelMannequin;
    private RenderPlayer renderPlayer = new RenderPlayer();
    
    public RenderBlockMannequin(ModelMannequin modelMannequin) {
        this.modelMannequin = modelMannequin;
    }
    
    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tickTime) {
        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
        
        TileEntityMannequin te = (TileEntityMannequin) tileEntity;
        MannequinFakePlayer fakePlayer = te.getFakePlayer();
        
        GL11.glPushMatrix();
        float scale = 0.0625F;
        
        int rotaion = te.getRotation();
        
        GL11.glTranslated(x + 0.5D, y + 1.5D, z + 0.5D);
        
        GL11.glScalef(scale * 15, scale * 15, scale * 15);
        GL11.glTranslated(0, scale * -1.6F, 0);
        
        GL11.glScalef(-1, -1, 1);
        GL11.glRotatef(rotaion * 22.5F, 0, 1, 0);
        
        ResourceLocation resourcelocation = AbstractClientPlayer.locationStevePng;
        PlayerSkinInfo skinInfo = null;
        
        if (te.getGameProfile() != null) {
            skinInfo = EquipmentPlayerRenderCache.INSTANCE.getPlayersNakedData(te.getGameProfile().getId());
            resourcelocation = SkinHelper.getSkinResourceLocation(te.getGameProfile());
            if (te.getGameProfile() != null & te.getWorldObj() != null) {
                if (fakePlayer == null) {
                    fakePlayer = new MannequinFakePlayer(te.getWorldObj(),te.getGameProfile());
                    te.setFakePlayer(fakePlayer);
                }
            }
        }
        
        if (skinInfo != null && skinInfo.getNakedInfo().isNaked) {
            if (!skinInfo.bindNomalSkin()) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(resourcelocation);
            }
        } else {
            Minecraft.getMinecraft().getTextureManager().bindTexture(resourcelocation);
        }
        
        modelMannequin.isChild = te.getBipedRotations().isChild;
        
        float f6 = 2.0F;
        if (fakePlayer != null) {
            renderPlayer.modelBipedMain.isChild = modelMannequin.isChild;
            fakePlayer.rotationPitch = (float) Math.toDegrees(te.getBipedRotations().head.rotationX);
            fakePlayer.rotationYawHead = (float) Math.toDegrees(te.getBipedRotations().head.rotationY);
            fakePlayer.prevRotationYawHead = (float) Math.toDegrees(te.getBipedRotations().head.rotationY);
            if (te.getBipedRotations() != null) {
                te.getBipedRotations().applyRotationsToBiped(renderPlayer.modelBipedMain);
            }
            
            RenderPlayerEvent.Specials.Pre preEvent = new RenderPlayerEvent.Specials.Pre(fakePlayer, renderPlayer, 1);

            if (renderPlayer.modelBipedMain.isChild) {
                GL11.glPushMatrix();
                GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
                GL11.glTranslatef(0.0F, 16.0F * scale, 0.0F);
            }
            GL11.glDisable(GL11.GL_CULL_FACE);
            MinecraftForge.EVENT_BUS.post(preEvent);
            GL11.glEnable(GL11.GL_CULL_FACE);
            if (renderPlayer.modelBipedMain.isChild) {
                GL11.glPopMatrix();
            }
        }
        
        ApiRegistrar.INSTANCE.onRenderMannequin(tileEntity, te.getGameProfile());
        
        modelMannequin.render(te.getBipedRotations(), true, scale);
        
        if (fakePlayer != null) {
            RenderPlayerEvent.Specials.Post postEvent = new RenderPlayerEvent.Specials.Post(fakePlayer, renderPlayer, 1);
            if (renderPlayer.modelBipedMain.isChild) {
                GL11.glPushMatrix();
                GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
                GL11.glTranslatef(0.0F, 16.0F * scale, 0.0F);
            }
            GL11.glDisable(GL11.GL_CULL_FACE);
            MinecraftForge.EVENT_BUS.post(postEvent);
            GL11.glEnable(GL11.GL_CULL_FACE);
            if (renderPlayer.modelBipedMain.isChild) {
                GL11.glPopMatrix();
            }
        }
        
        if (player.getDistance(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord) < 40) {
            if (tileEntity instanceof TileEntityMannequin) {
                EquipmentPlayerRenderCache.INSTANCE.renderMannequinEquipment(((TileEntityMannequin)tileEntity), modelMannequin);
            }
        }
        
        GL11.glPopMatrix();
    }
}
