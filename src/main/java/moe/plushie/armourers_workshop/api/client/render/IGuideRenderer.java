package moe.plushie.armourers_workshop.api.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IGuideRenderer {

    void render(MatrixStack matrixStack, IGuideProvider provider, int light, int overlay, IRenderTypeBuffer buffers);

}
