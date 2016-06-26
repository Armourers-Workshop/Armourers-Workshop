package riskyken.armourersWorkshop.client.particles;

import java.awt.Color;

import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.utils.UtilRender;
import riskyken.plushieWrapper.client.IRenderBuffer;
import riskyken.plushieWrapper.client.RenderBridge;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityFXPaintSplash extends EntityFX {
    
    private static final ResourceLocation paintSplashTextures = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/particles/paintSplash.png");
    private static final ResourceLocation particleTextures = ReflectionHelper.getPrivateValue(EffectRenderer.class, null, "particleTextures", "field_110737_b", "b");
    
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
        
        this.motionX = dir.offsetX * 0.08;
        this.motionY = dir.offsetY * 0.08;
        this.motionZ = dir.offsetZ * 0.08;
        
        this.motionX += (world.rand.nextFloat() - 0.5F) * 0.06;
        this.motionY += (world.rand.nextFloat() - 0.5F) * 0.06;
        this.motionZ += (world.rand.nextFloat() - 0.5F) * 0.06;
        
        this.particleTextureIndexX = (Math.round(world.rand.nextFloat())) * 8;
        this.particleTextureIndexY = (Math.round(world.rand.nextFloat())) * 8;
        
        this.noClip = false;
    }
    
    @Override
    public void onUpdate() {
        super.onUpdate();
        this.motionY -= 0.02D;
        this.particleAlpha = 1 + -((float)this.particleAge / this.particleMaxAge);
    }
    
    @Override
    public void renderParticle(Tessellator tessellator, float p_70539_2_, float p_70539_3_, float p_70539_4_, float p_70539_5_, float p_70539_6_, float p_70539_7_) {
        IRenderBuffer renderBuffer = RenderBridge.INSTANCE;
        renderBuffer.draw();
        
        UtilRender.bindTexture(paintSplashTextures);
        
        renderBuffer.startDrawingQuads();
        renderBuffer.setBrightness(getBrightnessForRender(0));
        
        float f6 = (particleTextureIndexX) / 8 * 0.5F;
        float f7 = f6 + 0.5F;
        float f8 = (particleTextureIndexY) / 8 * 0.5F;
        float f9 = f8 + 0.5F;;
        
        float f10 = 0.1F * this.particleScale;
        
        if (this.particleIcon != null) {
            f6 = this.particleIcon.getMinU();
            f7 = this.particleIcon.getMaxU();
            f8 = this.particleIcon.getMinV();
            f9 = this.particleIcon.getMaxV();
        }
        
        float f11 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)p_70539_2_ - interpPosX);
        float f12 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)p_70539_2_ - interpPosY);
        float f13 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)p_70539_2_ - interpPosZ);
        renderBuffer.setColourRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);
        renderBuffer.addVertexWithUV((double)(f11 - p_70539_3_ * f10 - p_70539_6_ * f10), (double)(f12 - p_70539_4_ * f10), (double)(f13 - p_70539_5_ * f10 - p_70539_7_ * f10), (double)f7, (double)f9);
        renderBuffer.addVertexWithUV((double)(f11 - p_70539_3_ * f10 + p_70539_6_ * f10), (double)(f12 + p_70539_4_ * f10), (double)(f13 - p_70539_5_ * f10 + p_70539_7_ * f10), (double)f7, (double)f8);
        renderBuffer.addVertexWithUV((double)(f11 + p_70539_3_ * f10 + p_70539_6_ * f10), (double)(f12 + p_70539_4_ * f10), (double)(f13 + p_70539_5_ * f10 + p_70539_7_ * f10), (double)f6, (double)f8);
        renderBuffer.addVertexWithUV((double)(f11 + p_70539_3_ * f10 - p_70539_6_ * f10), (double)(f12 - p_70539_4_ * f10), (double)(f13 + p_70539_5_ * f10 - p_70539_7_ * f10), (double)f6, (double)f9);
        
        renderBuffer.draw();
        UtilRender.bindTexture(particleTextures);
        renderBuffer.startDrawingQuads();
    }
}
