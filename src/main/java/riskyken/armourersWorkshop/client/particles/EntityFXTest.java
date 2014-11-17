package riskyken.armourersWorkshop.client.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.common.lib.LibModInfo;

public class EntityFXTest extends EntityFX {
    
    private static final ResourceLocation paintSplashTextures = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/particles/paintSplash.png");
    private static final ResourceLocation particleTextures = new ResourceLocation("textures/particle/particles.png");
    
    public EntityFXTest(World world, double x, double y, double z) {
        super(world, x, y, z);
        particleMaxAge = 5;
        this.particleScale = 0.5F;
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        motionY = 0.02D;
    }
    
    @Override
    public void renderParticle(Tessellator tessellator, float p_70539_2_, float p_70539_3_, float p_70539_4_, float p_70539_5_, float p_70539_6_, float p_70539_7_) {
        tessellator.draw();
        Minecraft.getMinecraft().renderEngine.bindTexture(paintSplashTextures);
        
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

        Minecraft.getMinecraft().renderEngine.bindTexture(particleTextures);
        tessellator.startDrawingQuads();
    }
}
