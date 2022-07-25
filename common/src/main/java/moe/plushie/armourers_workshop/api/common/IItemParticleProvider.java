package moe.plushie.armourers_workshop.api.common;

import net.minecraft.world.item.context.UseOnContext;

public interface IItemParticleProvider {

    void playParticle(UseOnContext context);
}
