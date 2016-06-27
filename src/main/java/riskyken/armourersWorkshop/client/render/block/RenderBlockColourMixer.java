package riskyken.armourersWorkshop.client.render.block;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockColourMixer /*implements ISimpleBlockRenderingHandler*/ {
    /*
    public static int renderId = 0;
    
    public RenderBlockColourMixer() {
        renderId = RenderingRegistry.getNextAvailableRenderId();
    }
    
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        ClientProxy.renderPass = 0;
        renderItem(block, metadata, renderer, 0);
        ClientProxy.renderPass = 1;
        renderItem(block, metadata, renderer, 0);
    }

    private void renderItem(Block block, int metadata, RenderBlocks renderer, int pass) {
        
        Tessellator tessellator = Tessellator.instance;
        
        int rawColors = pass;
        float blockColorRed = (rawColors >> 16 & 0xff) / 255F;
        float blockColorGreen = (rawColors >> 8 & 0xff) / 255F;
        float blockColorBlue = (rawColors & 0xff) / 255F;
        
        GL11.glColor3f(1F, 1F, 1F);
        
        block.setBlockBoundsForItemRender();
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, metadata));
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, metadata));
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, metadata));
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, metadata));
        tessellator.draw();
        
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }
    
    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        boolean rendered = false;
        ClientProxy.renderPass = 0;
        if (renderer.renderStandardBlock(block, x, y, z)) {
            rendered = true;
        }
        ClientProxy.renderPass = 1;
        if (renderer.renderStandardBlock(block, x, y, z)) {
            rendered = true;
        }
        return rendered;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return renderId;
    }
    */
}
