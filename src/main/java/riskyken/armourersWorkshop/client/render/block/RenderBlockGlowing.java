package riskyken.armourersWorkshop.client.render.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class RenderBlockGlowing implements ISimpleBlockRenderingHandler {

    public static int renderId = 0;
    
    public RenderBlockGlowing(int renderId) {
        this.renderId = renderId;
    }
    
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        renderItem(block, metadata, renderer);
    }
    
    private void renderItem(Block block, int metadata, RenderBlocks renderer) {
        Tessellator tessellator = Tessellator.instance;
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
        int light = block.getLightValue(world, x, y, z);
        if (light > 1) {
            Tessellator tessellator = Tessellator.instance;
            int l = block.colorMultiplier(world, x, y, z);
            float r = (float)(l >> 16 & 255) / 255.0F;
            float g = (float)(l >> 8 & 255) / 255.0F;
            float b = (float)(l & 255) / 255.0F;
            boolean rendered = false;
            tessellator.setColorOpaque_F(r, g, b);
            tessellator.setBrightness(15728880);
            
            if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y - 1, z, 0)) {
                renderer.renderFaceYNeg(block, x, y, z, block.getIcon(0, 0));
                rendered = true;
            }
            if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y + 1, z, 1)) {
                renderer.renderFaceYPos(block, x, y, z, block.getIcon(1, 0));
                rendered = true;
            }
            if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y, z - 1, 2)) {
                renderer.renderFaceZNeg(block, x, y, z, block.getIcon(2, 0));
                rendered = true;
            }
            if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y, z + 1, 3)) {
                renderer.renderFaceZPos(block, x, y, z, block.getIcon(3, 0));
                rendered = true;
            }
            if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x - 1, y, z, 4)) {
                renderer.renderFaceXNeg(block, x, y, z, block.getIcon(4, 0));
                rendered = true;
            }
            if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x + 1, y, z, 5)) {
                renderer.renderFaceXPos(block, x, y, z, block.getIcon(5, 0));
                rendered = true;
            }
            
            return rendered;
        } else {
            return renderer.renderStandardBlock(block, x, y, z);
        }
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return renderId;
    }
}
