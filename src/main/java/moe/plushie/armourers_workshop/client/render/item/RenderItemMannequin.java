package moe.plushie.armourers_workshop.client.render.item;

import moe.plushie.armourers_workshop.client.texture.PlayerTexture;
import moe.plushie.armourers_workshop.common.data.type.BipedRotations;
import moe.plushie.armourers_workshop.common.init.entities.EntityMannequin.TextureData;
import moe.plushie.armourers_workshop.common.init.items.ItemMannequin;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;

public class RenderItemMannequin extends TileEntityItemStackRenderer {
    
    private final ModelPlayer modelPlayerSmall = new ModelPlayer(0F, true);
    private final ModelPlayer modelPlayerNormal = new ModelPlayer(0F, false);

    @Override
    public void renderByItem(ItemStack itemStackIn, float partialTicks) {
        TextureData textureData = ItemMannequin.getTextureData(itemStackIn);
        PlayerTexture playerTexture = ClientProxy.playerTextureDownloader.getPlayerTexture(textureData);
        Minecraft.getMinecraft().renderEngine.bindTexture(playerTexture.getResourceLocation());
        
        ModelPlayer targetModel = modelPlayerNormal;
        if (playerTexture.isSlimModel()) {
            targetModel = modelPlayerSmall;
        }
        
        float scale = 0.0625F;
        GlStateManager.pushMatrix();
        GlStateManager.scale(-1, -1, 1);
        /*
        float angle = (((Minecraft.getMinecraft().world.getTotalWorldTime() * 8) % 360) + 0);
        GlStateManager.rotate(angle, 0, 1, 0);
        
        //GlStateManager.translate(0, 0, -7F * scale);
        
        GlStateManager.translate(0F * scale, 0, 0);
        */
        
        targetModel.setRotationAngles(0F, 0F, 0F, 0F, 0F, 0F, Minecraft.getMinecraft().player);
        
        BipedRotations bipedRotations = new BipedRotations();
        
        bipedRotations.applyRotationsToBiped(targetModel);
        
        targetModel.bipedHead.render(scale);
        targetModel.bipedHeadwear.render(scale);
        
        targetModel.bipedBody.render(scale);
        targetModel.bipedBodyWear.render(scale);
        
        targetModel.bipedLeftArm.render(scale);
        targetModel.bipedLeftArmwear.render(scale);
        
        targetModel.bipedRightArm.render(scale);
        targetModel.bipedRightArmwear.render(scale);
        
        
        targetModel.bipedLeftLeg.render(scale);
        targetModel.bipedLeftLegwear.render(scale);
        
        targetModel.bipedRightLeg.render(scale);
        targetModel.bipedRightLegwear.render(scale);
        GlStateManager.popMatrix();
    }
    /*
    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        GL11.glScalef(-1, -1, 1);
        GL11.glTranslatef(0, -0.5F, 0);
        
        float headPitch = 0F;
        float headTilt = 0F;
        float limbWobble = 0F;
        
        switch (type) {
        case EQUIPPED_FIRST_PERSON:
            GL11.glTranslatef(-0.6F, -0.5F, 0.6F);
            GL11.glRotatef(-60, 0, 1, 0);
            
            headPitch = -40F;
            headTilt = -10F;
            
            if (data.length >= 2) {
                if (data[1] instanceof AbstractClientPlayer & data[0] instanceof RenderBlocks) {
                    RenderBlocks renderBlocks = (RenderBlocks) data[0];
                    AbstractClientPlayer player = (AbstractClientPlayer) data[1];
                    World world = player.worldObj;
                    float partialTickTime = ModClientFMLEventHandler.renderTickTime;
                    
                    float pitchTime = (world.getTotalWorldTime() % 10L) + partialTickTime;
                    float tiltTime = (world.getTotalWorldTime() % 8L) + partialTickTime;
                    float limbTime = (world.getTotalWorldTime() % 6L) + partialTickTime;
                    
                    pitchTime  = (pitchTime / 5) - 1;
                    tiltTime = (tiltTime / 4) - 1;
                    limbTime = (limbTime / 3) - 1;
                    
                    float lastDistance = player.distanceWalkedModified - player.prevDistanceWalkedModified;
                    
                    headTilt += Math.sin(tiltTime * Math.PI) * 80F * lastDistance;
                    headPitch += Math.sin(pitchTime * Math.PI) * 40F * lastDistance;
                    limbWobble += Math.sin(limbTime * Math.PI) * lastDistance / 10F;
                }
            }
            break;
        case ENTITY:
            GL11.glScalef(1.4F, 1.4F, 1.4F);
            GL11.glTranslatef(0, -0.8F, 0);
            break;
        case EQUIPPED:
            GL11.glScalef(1.2F, 1.2F, 1.2F);
            GL11.glTranslatef(-0.6F, -0.5F, 0.6F);
            GL11.glRotatef(-60, 0, 1, 0);
            break;
        case INVENTORY:
            GL11.glTranslatef(0, 0.1F, 0);
            GL11.glScalef(0.9F, 0.9F, 0.9F);
            GL11.glRotatef(180, 0, 1, 0);
            break;
        default:
            break;
        }
        
        PlayerTexture playerTexture = MannequinTextureHelper.getMannequinTexture(item);
        Minecraft.getMinecraft().renderEngine.bindTexture(playerTexture.getResourceLocation());
        
        if (item.getItem() == Item.getItemFromBlock(ModBlocks.doll)) {
            float dollScale = 0.5F;
            GL11.glScalef(dollScale, dollScale, dollScale);
        }
        
        float scale = 0.0625F;
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        if (playerTexture.isSlimModel()) {
            modelAlex.render(null, 0, limbWobble, 0, headPitch, headTilt, scale, true);
        } else {
            modelSteve.render(null, 0, limbWobble, 0, headPitch, headTilt, scale, true);
        }
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }*/

}
