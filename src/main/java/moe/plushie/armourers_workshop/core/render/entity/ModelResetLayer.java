package moe.plushie.armourers_workshop.core.render.entity;//package moe.plushie.armourers_workshop.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelResetLayer<T extends Entity, M extends EntityModel<T>> extends LayerRenderer<T, M> {

    private final PlayerRenderer renderPlayer;

    public ModelResetLayer(PlayerRenderer renderPlayer) {
        super(null);
        this.renderPlayer = renderPlayer;
    }

//    @Override
//    public void doRenderLayer(EntityPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
//        renderPlayer.getMainModel().bipedLeftArm.isHidden = false;
//        renderPlayer.getMainModel().bipedRightArm.isHidden = false;
//    }


    @Override
    public void render(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, T p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
        renderPlayer.getModel().leftArm.visible = false;
        renderPlayer.getModel().rightArm.visible = false;
    }
//
//
//    @Override
//    public boolean shouldCombineTextures() {
//        return false;
//    }
}
