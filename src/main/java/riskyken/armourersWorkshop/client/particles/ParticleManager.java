package riskyken.armourersWorkshop.client.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleManager {

	public static final ParticleManager INSTANCE = new ParticleManager();
	
	public void spawnParticle(World world, Particle particle) {
		spawnParticle(world, particle, false);
	}
	
	public void spawnParticle(World world, Particle particle, boolean must) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc != null && mc.getRenderViewEntity() != null && mc.effectRenderer != null) {

			if (!must) {
				int particleSetting = mc.gameSettings.particleSetting;
				
				if (particleSetting == 2 || (particleSetting == 1 && world.rand.nextInt(3) == 0)) {
					return;
				}
				/*
				double distanceX = mc.getRenderViewEntity().posX - particle.posX;
				double distanceY = mc.getRenderViewEntity().posY - particle.posY;
				double distanceZ = mc.getRenderViewEntity().posZ - particle.posZ;
				int maxDistance = 16;
				
				if (distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ > maxDistance * maxDistance) {
					return;
				}
				*/
			}
			
			if (particle != null) {
				Minecraft.getMinecraft().effectRenderer.addEffect(particle);
			}
		}
	}
	
	
	
}
