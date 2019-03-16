package moe.plushie.armourers_workshop.client.particles;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = LibModInfo.ID, value = { Side.CLIENT })
@SideOnly(Side.CLIENT)
public class ModParticleManager {

    private static final ResourceLocation PARTICLES_TEXTURES = new ResourceLocation(LibModInfo.ID, "textures/particles/particles.png");
    private static final ArrayList<Particle> PARTICLES = new ArrayList<Particle>();
    private static Minecraft mc = Minecraft.getMinecraft();
    
    public static void spawnParticle(ParticlePaintSplash particle) {
        PARTICLES.add(particle);
    }
    
    @SubscribeEvent
    public static void render(RenderWorldLastEvent event) {
        Minecraft.getMinecraft().renderEngine.bindTexture(PARTICLES_TEXTURES);
        Entity entity = mc.getRenderViewEntity();
        float partialTicks = event.getPartialTicks();
        
        float f = ActiveRenderInfo.getRotationX();
        float f1 = ActiveRenderInfo.getRotationZ();
        float f2 = ActiveRenderInfo.getRotationYZ();
        float f3 = ActiveRenderInfo.getRotationXY();
        float f4 = ActiveRenderInfo.getRotationXZ();
        Particle.interpPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        Particle.interpPosY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        Particle.interpPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
        Particle.cameraViewDir = entity.getLook(partialTicks);
        
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder builder = tess.getBuffer();
        
        if (PARTICLES.isEmpty()) {
            return;
        }
        
        GlStateManager.pushAttrib();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.alphaFunc(516, 0.003921569F);
        
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        for (int i = 0; i < PARTICLES.size(); i++) {
            Particle particle = PARTICLES.get(i);
            if (particle.isAlive()) {
                particle.renderParticle(builder, entity, partialTicks, f, f4, f1, f2, f3);
                particle.onUpdate();
            }
        }
        tess.draw();
        
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.popAttrib();
    }
    
    @SubscribeEvent
    public static void update(ClientTickEvent event) {
        if (event.phase != Phase.START) {
            return;
        }
        if (PARTICLES.isEmpty()) {
            return;
        }
        for (int i = 0; i < PARTICLES.size(); i++) {
            Particle particle = PARTICLES.get(i);
            if (!particle.isAlive()) {
                PARTICLES.remove(i);
                i--;
            }
        }
    }
}
