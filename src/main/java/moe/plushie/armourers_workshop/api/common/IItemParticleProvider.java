package moe.plushie.armourers_workshop.api.common;

import net.minecraft.item.ItemUseContext;

public interface IItemParticleProvider {

    void playParticle(ItemUseContext context);
}
