package riskyken.armourers_workshop.client.particles;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleManager {

	public static final ParticleManager INSTANCE = new ParticleManager();
	/*
	public void spawnParticle(World world, EntityFX particle) {
		spawnParticle(world, particle, false);
	}
	
	public void spawnParticle(World world, EntityFX particle, boolean must) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc != null && mc.renderViewEntity != null && mc.effectRenderer != null) {

			if (!must) {
				int particleSetting = mc.gameSettings.particleSetting;
				
				if (particleSetting == 2 || (particleSetting == 1 && world.rand.nextInt(3) == 0)) {
					return;
				}
				
				double distanceX = mc.renderViewEntity.posX - particle.posX;
				double distanceY = mc.renderViewEntity.posY - particle.posY;
				double distanceZ = mc.renderViewEntity.posZ - particle.posZ;
				int maxDistance = 16;
				
				if (distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ > maxDistance * maxDistance) {
					return;
				}
			}
			
			if (particle != null) {
				Minecraft.getMinecraft().effectRenderer.addEffect(particle);
			}
		}
	}
	*/
}
