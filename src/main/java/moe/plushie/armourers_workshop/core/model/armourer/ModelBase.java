package moe.plushie.armourers_workshop.core.model.armourer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelBase extends Model {

    public ModelBase() {
        super(RenderType::entityCutoutNoCull);
    }


//    @Override
//    public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
//
//    }

    @Override
    public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder vertexBuilder, int light, int p_225598_4_, float partialTicks, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
//        main.render(matrixStack, vertexBuilder, light, p_225598_4_, partialTicks, p_225598_6_, p_225598_7_, p_225598_8_);
//        leftArm.render(matrixStack, vertexBuilder, light, p_225598_4_, partialTicks, p_225598_6_, p_225598_7_, p_225598_8_);
//        rightArm.render(matrixStack, vertexBuilder, light, p_225598_4_, partialTicks, p_225598_6_, p_225598_7_, p_225598_8_);
    }
}
