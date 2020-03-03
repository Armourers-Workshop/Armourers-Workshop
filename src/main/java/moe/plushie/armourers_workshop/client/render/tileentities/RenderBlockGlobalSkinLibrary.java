package moe.plushie.armourers_workshop.client.render.tileentities;

import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.client.model.block.ModelBlockGlobalSkinLibrary;
import moe.plushie.armourers_workshop.common.init.blocks.BlockGlobalSkinLibrary;
import moe.plushie.armourers_workshop.common.init.blocks.ModBlocks;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityGlobalSkinLibrary;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;

public class RenderBlockGlobalSkinLibrary extends TileEntitySpecialRenderer<TileEntityGlobalSkinLibrary> {

    private static final ModelBlockGlobalSkinLibrary GLOBE_MODEL = new ModelBlockGlobalSkinLibrary();
    private static final float SCALE = 0.0625F;
    
    @Override
    public void render(TileEntityGlobalSkinLibrary te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 1.5D, z + 0.5D);
        GlStateManager.scale(-1, -1, 1);
        float shrink = 0.4F;
        if (ConfigHandlerClient.useClassicBlockModels) {
            shrink = 1;
        } else {
            float xPos = 2.5F;
            float yPos = 4.6F;
            IBlockState state = te.getWorld().getBlockState(te.getPos());
            if (state.getBlock() == ModBlocks.GLOBAL_SKIN_LIBRARY) {
                EnumFacing facing = state.getValue(BlockGlobalSkinLibrary.STATE_FACING);
                GlStateManager.translate(
                        (xPos * SCALE * facing.getZOffset()) + (yPos * SCALE * -facing.getXOffset()),
                        4 * SCALE,
                        (xPos * SCALE * facing.getXOffset() + (yPos * SCALE * facing.getZOffset()))
                        );
            }
        }
        GlStateManager.scale(shrink, shrink, shrink);
        GLOBE_MODEL.render(te, partialTicks, SCALE);
        GlStateManager.popMatrix();
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
    }
}
