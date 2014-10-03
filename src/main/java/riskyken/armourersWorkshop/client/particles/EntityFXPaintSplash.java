package riskyken.armourersWorkshop.client.particles;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityFXPaintSplash extends EntityFX {

    private static final ResourceLocation paintSplashTextures = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/particles/paintSplash.png");
    private static final ResourceLocation particleTextures = new ResourceLocation("textures/particle/particles.png");
    
    public EntityFXPaintSplash(World world, double x, double y, double z, int colour, ForgeDirection dir) {
        super(world, x + dir.offsetX * 0.5D, y + dir.offsetY * 0.5D, z + dir.offsetZ * 0.5D);
        this.particleScale = 0.2F + world.rand.nextFloat() * 0.4F;
        particleMaxAge = 10;
        
        Color c = new Color(colour);
        this.particleRed = (float)c.getRed() / 255;
        this.particleGreen = (float)c.getGreen() / 255;
        this.particleBlue = (float)c.getBlue() / 255;
        
        float xPos = world.rand.nextFloat() - 0.5F;
        float yPos = world.rand.nextFloat() - 0.5F;
        
        switch (dir) {
        case UP:
            this.setPosition(this.posX + xPos, this.posY, this.posZ + yPos);
            break;
        case DOWN:
            this.setPosition(this.posX + xPos, this.posY, this.posZ + yPos);
            break;
        case NORTH:
            this.setPosition(this.posX + xPos, this.posY + yPos, this.posZ);
            break;
        case SOUTH:
            this.setPosition(this.posX + xPos, this.posY + yPos, this.posZ);
            break;
        case EAST:
            this.setPosition(this.posX, this.posY + yPos, this.posZ + xPos);
            break;
        case WEST:
            this.setPosition(this.posX, this.posY + yPos, this.posZ + xPos);
            break;
        default:
            break;
        }
        
        motionX = dir.offsetX * 0.08;
        motionY = dir.offsetY * 0.08;
        motionZ = dir.offsetZ * 0.08;
        
        motionX += (world.rand.nextFloat() - 0.5F) * 0.06;
        motionY += (world.rand.nextFloat() - 0.5F) * 0.06;
        motionZ += (world.rand.nextFloat() - 0.5F) * 0.06;
        
        
        particleTextureIndexX = (Math.round(world.rand.nextFloat())) * 8;
        particleTextureIndexY = (Math.round(world.rand.nextFloat())) * 8;
        
        this.noClip = false;
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        motionY -= 0.02D;
        particleAlpha = 1 + -((float)particleAge / particleMaxAge);
    }
    
    
    @Override
    public void renderParticle(Tessellator tessellator, float p_70539_2_, float p_70539_3_, float p_70539_4_, float p_70539_5_, float p_70539_6_, float p_70539_7_) {
        tessellator.draw();
        Minecraft.getMinecraft().renderEngine.bindTexture(paintSplashTextures);
        
        //GL11.glPushMatrix();
        
        //GL11.glEnable(GL11.GL_BLEND);
        //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        tessellator.startDrawingQuads();
        
        
        
        
        
        float f6 = (particleTextureIndexX) / 8 * 0.5F;
        float f7 = f6 + 0.5F;
        float f8 = (particleTextureIndexY) / 8 * 0.5F;
        float f9 = f8 + 0.5F;;
        
        float f10 = 0.1F * this.particleScale;

        if (this.particleIcon != null)
        {
            f6 = this.particleIcon.getMinU();
            f7 = this.particleIcon.getMaxU();
            f8 = this.particleIcon.getMinV();
            f9 = this.particleIcon.getMaxV();
        }

        float f11 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)p_70539_2_ - interpPosX);
        float f12 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)p_70539_2_ - interpPosY);
        float f13 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)p_70539_2_ - interpPosZ);
        tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
        tessellator.addVertexWithUV((double)(f11 - p_70539_3_ * f10 - p_70539_6_ * f10), (double)(f12 - p_70539_4_ * f10), (double)(f13 - p_70539_5_ * f10 - p_70539_7_ * f10), (double)f7, (double)f9);
        tessellator.addVertexWithUV((double)(f11 - p_70539_3_ * f10 + p_70539_6_ * f10), (double)(f12 + p_70539_4_ * f10), (double)(f13 - p_70539_5_ * f10 + p_70539_7_ * f10), (double)f7, (double)f8);
        tessellator.addVertexWithUV((double)(f11 + p_70539_3_ * f10 + p_70539_6_ * f10), (double)(f12 + p_70539_4_ * f10), (double)(f13 + p_70539_5_ * f10 + p_70539_7_ * f10), (double)f6, (double)f8);
        tessellator.addVertexWithUV((double)(f11 + p_70539_3_ * f10 - p_70539_6_ * f10), (double)(f12 - p_70539_4_ * f10), (double)(f13 + p_70539_5_ * f10 - p_70539_7_ * f10), (double)f6, (double)f9);
        
        tessellator.draw();
        
        //GL11.glPopMatrix();
        
        //Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/particle/particles.png"));
        //ResourceLocation particleTextures;
        //particleTextures = ReflectionHelper.getPrivateValue(EffectRenderer.class, null, "particleTextures", "field_110737_b", "b");
        Minecraft.getMinecraft().renderEngine.bindTexture(particleTextures);
        tessellator.startDrawingQuads();
    }
    
    /*
    @Override
    public void renderParticle(Tessellator tessellator, float x, float y, float z, float par5, float par6, float par7) {
        tessellator.draw();
        Minecraft.getMinecraft().renderEngine.bindTexture(paintSplashTextures);
        
        //GL11.glPushMatrix();
        
        //GL11.glEnable(GL11.GL_BLEND);
        //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        
        tessellator.startDrawingQuads();
        
        
        
        
        
        float f6 = (float)this.particleTextureIndexX / 16.0F;
        float f7 = f6 + 0.0624375F;
        float f8 = (float)this.particleTextureIndexY / 16.0F;
        float f9 = f8 + 0.0624375F;
        float f10 = 0.1F * this.particleScale;

        if (this.particleIcon != null)
        {
            f6 = this.particleIcon.getMinU();
            f7 = this.particleIcon.getMaxU();
            f8 = this.particleIcon.getMinV();
            f9 = this.particleIcon.getMaxV();
        }

        float f11 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)x - interpPosX);
        float f12 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)x - interpPosY);
        float f13 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)x - interpPosZ);
        tessellator.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
        tessellator.addVertexWithUV((double)(f11 - p_70539_3_ * f10 - p_70539_6_ * f10), (double)(f12 - p_70539_4_ * f10), (double)(f13 - p_70539_5_ * f10 - p_70539_7_ * f10), (double)f7, (double)f9);
        tessellator.addVertexWithUV((double)(f11 - p_70539_3_ * f10 + p_70539_6_ * f10), (double)(f12 + p_70539_4_ * f10), (double)(f13 - p_70539_5_ * f10 + p_70539_7_ * f10), (double)f7, (double)f8);
        tessellator.addVertexWithUV((double)(f11 + p_70539_3_ * f10 + p_70539_6_ * f10), (double)(f12 + p_70539_4_ * f10), (double)(f13 + p_70539_5_ * f10 + p_70539_7_ * f10), (double)f6, (double)f8);
        tessellator.addVertexWithUV((double)(f11 + p_70539_3_ * f10 - p_70539_6_ * f10), (double)(f12 - p_70539_4_ * f10), (double)(f13 + p_70539_5_ * f10 - p_70539_7_ * f10), (double)f6, (double)f9);
        
        
        
        
        super.renderParticle(tessellator, x, y, z, par5, par6, par7);
        tessellator.draw();
        
        //GL11.glPopMatrix();
        
        //Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation("textures/particle/particles.png"));
        //ResourceLocation particleTextures;
        //particleTextures = ReflectionHelper.getPrivateValue(EffectRenderer.class, null, "particleTextures", "field_110737_b", "b");
        Minecraft.getMinecraft().renderEngine.bindTexture(particleTextures);
        tessellator.startDrawingQuads();
    }
    */
    @Override
    public int getFXLayer() {
        return 1;
    }
}
