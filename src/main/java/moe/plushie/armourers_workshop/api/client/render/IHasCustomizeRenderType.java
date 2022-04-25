package moe.plushie.armourers_workshop.api.client.render;

import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IHasCustomizeRenderType {

    @OnlyIn(Dist.CLIENT)
    RenderType getItemRenderType(boolean flags);
}
