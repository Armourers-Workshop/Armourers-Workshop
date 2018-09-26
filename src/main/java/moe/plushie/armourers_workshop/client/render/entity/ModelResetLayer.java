package moe.plushie.armourers_workshop.client.render.entity;

import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelResetLayer implements LayerRenderer<EntityPlayer> {

    private final RenderPlayer renderPlayer;
    
    public ModelResetLayer(RenderPlayer renderPlayer) {
        this.renderPlayer = renderPlayer;
    }
    
    @Override
    public void doRenderLayer(EntityPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        renderPlayer.getMainModel().bipedLeftArm.isHidden = false;
        renderPlayer.getMainModel().bipedRightArm.isHidden = false;
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
