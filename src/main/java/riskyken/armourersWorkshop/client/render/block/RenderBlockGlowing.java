package riskyken.armourersWorkshop.client.render.block;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import riskyken.armourersWorkshop.api.common.painting.IPantableBlock;
import riskyken.armourersWorkshop.api.common.skin.cubes.ICubeColour;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.plushieWrapper.client.IRenderBuffer;
import riskyken.plushieWrapper.client.RenderBridge;

@SideOnly(Side.CLIENT)
public class RenderBlockGlowing implements ISimpleBlockRenderingHandler {

    public static int renderId = 0;
    
    public RenderBlockGlowing() {
        renderId = RenderingRegistry.getNextAvailableRenderId();
    }
    
    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        renderItem(block, metadata, renderer);
        boolean glowing = block.getLightValue() > 0;
        if (glowing) {
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GL11.glScalef(1.1F, 1.1F, 1.1F);
            GL11.glColor4f(1F, 1F, 1F, 0.6F);
            GL11.glEnable(GL11.GL_BLEND);
            ModRenderHelper.enableAlphaBlend();
            GL11.glDisable(GL11.GL_LIGHTING);
            renderItem(block, metadata, renderer);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }
    
    private void renderItem(Block block, int metadata, RenderBlocks renderer) {
        Tessellator tessellator = Tessellator.instance;
        block.setBlockBoundsForItemRender();
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(255, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(255, metadata));
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(255, metadata));
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(255, metadata));
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(255, metadata));
        tessellator.draw();
        
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(255, metadata));
        tessellator.draw();
        
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        
        if (block instanceof IPantableBlock) {
            int light = block.getLightValue(world, x, y, z);
            Tessellator tessellator = Tessellator.instance;
            
            ICubeColour colour = ((IPantableBlock)block).getColour(world, x, y, z);
            
            boolean rendered = false;
            
            if (light > 1) {
                rendered = renderFaces(world, x, y, z, colour, block, renderer);
            } else {
                rendered = renderFacesWithLighting(world, x, y, z, colour, block, renderer);
            }
            
            return rendered;
        } else {
            return renderer.renderStandardBlock(block, x, y, z);
        }
    }
    
    private boolean renderFaces(IBlockAccess world, int x, int y, int z, ICubeColour colour, Block block, RenderBlocks renderer) {
        boolean rendered = false;
        int meta = world.getBlockMetadata(x, y, z);
        
        IRenderBuffer renderBuffer = RenderBridge.INSTANCE;
        renderBuffer.setBrightness(0xF000F0);
        double faceOffset = 0.02;
        
        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y - 1, z, 0)) {
            int pt = colour.getPaintType(0) & 0xFF;
            renderBuffer.setColorOpaque_B(colour.getRed(0), colour.getGreen(0), colour.getBlue(0));
            renderer.renderFaceYNeg(block, x, y, z, block.getIcon(pt, 0));
            if (meta == 1) {
                renderBuffer.setColorOpaque_B((byte)255, (byte)255, (byte)255);
                renderer.renderFaceYNeg(block, x, y - faceOffset, z, block.getIcon(0, meta));
            }
            rendered = true;
        }
        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y + 1, z, 1)) {
            int pt = colour.getPaintType(1) & 0xFF;
            renderBuffer.setColorOpaque_B(colour.getRed(1), colour.getGreen(1), colour.getBlue(1));
            renderer.renderFaceYPos(block, x, y, z, block.getIcon(pt, 0));
            if (meta == 2) {
                renderBuffer.setColorOpaque_B((byte)255, (byte)255, (byte)255);
                renderer.renderFaceYPos(block, x, y + faceOffset, z, block.getIcon(1, meta));
            }
            rendered = true;
        }
        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y, z - 1, 2)) {
            int pt = colour.getPaintType(2) & 0xFF;
            renderBuffer.setColorOpaque_B(colour.getRed(2), colour.getGreen(2), colour.getBlue(2));
            renderer.renderFaceZNeg(block, x, y, z, block.getIcon(pt, 0));
            if (meta == 3) {
                renderBuffer.setColorOpaque_B((byte)255, (byte)255, (byte)255);
                renderer.renderFaceZNeg(block, x, y, z - faceOffset, block.getIcon(2, meta));
            }
            rendered = true;
        }
        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y, z + 1, 3)) {
            int pt = colour.getPaintType(3) & 0xFF;
            renderBuffer.setColorOpaque_B(colour.getRed(3), colour.getGreen(3), colour.getBlue(3));
            renderer.renderFaceZPos(block, x, y, z, block.getIcon(pt, 0));
            if (meta == 4) {
                renderBuffer.setColorOpaque_B((byte)255, (byte)255, (byte)255);
                renderer.renderFaceZPos(block, x, y, z + faceOffset, block.getIcon(3, meta));
            }
            rendered = true;
        }
        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x - 1, y, z, 4)) {
            int pt = colour.getPaintType(4) & 0xFF;
            renderBuffer.setColorOpaque_B(colour.getRed(4), colour.getGreen(4), colour.getBlue(4));
            renderer.renderFaceXNeg(block, x, y, z, block.getIcon(pt, 0));
            if (meta == 5) {
                renderBuffer.setColorOpaque_B((byte)255, (byte)255, (byte)255);
                renderer.renderFaceXNeg(block, x - faceOffset, y, z, block.getIcon(4, meta));
            }
            rendered = true;
        }
        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x + 1, y, z, 5)) {
            int pt = colour.getPaintType(5) & 0xFF;
            renderBuffer.setColorOpaque_B(colour.getRed(5), colour.getGreen(5), colour.getBlue(5));
            renderer.renderFaceXPos(block, x, y, z, block.getIcon(pt, 0));
            if (meta == 6) {
                renderBuffer.setColorOpaque_B((byte)255, (byte)255, (byte)255);
                renderer.renderFaceXPos(block, x + faceOffset, y, z, block.getIcon(5, meta));
            }
            rendered = true;
        }
        return rendered;
    }
    
    private boolean renderFacesWithLighting(IBlockAccess world, int x, int y, int z, ICubeColour colour, Block block, RenderBlocks renderer) {
        boolean rendered = false;
        int meta = world.getBlockMetadata(x, y, z);
        
        IRenderBuffer renderBuffer = RenderBridge.INSTANCE;
        double faceOffset = 0.02;
        
        float yNegLight = 0.5F;
        float yPosLight = 1.0F;
        float zLight = 0.8F;
        float xLight = 0.6F;
        
        float yNegR = yNegLight * ((colour.getRed(0) & 0xFF) / 255F);
        float yNegG = yNegLight * ((colour.getGreen(0) & 0xFF) / 255F);
        float yNegB = yNegLight * ((colour.getBlue(0) & 0xFF) / 255F);
        
        float yPosR = yPosLight * ((colour.getRed(1) & 0xFF) / 255F);
        float yPosG = yPosLight * ((colour.getGreen(1) & 0xFF) / 255F);
        float yPosB = yPosLight * ((colour.getBlue(1) & 0xFF) / 255F);
        
        float zNegR = zLight * ((colour.getRed(2) & 0xFF) / 255F);
        float zNegG = zLight * ((colour.getGreen(2) & 0xFF) / 255F);
        float zNegB = zLight * ((colour.getBlue(2) & 0xFF) / 255F);
        
        float zPosR = zLight * ((colour.getRed(3) & 0xFF) / 255F);
        float zPosG = zLight * ((colour.getGreen(3) & 0xFF) / 255F);
        float zPosB = zLight * ((colour.getBlue(3) & 0xFF) / 255F);
        
        float xNegR = xLight * ((colour.getRed(4) & 0xFF) / 255F);
        float xNegG = xLight * ((colour.getGreen(4) & 0xFF) / 255F);
        float xNegB = xLight * ((colour.getBlue(4) & 0xFF) / 255F);
        
        float xPosR = xLight * ((colour.getRed(5) & 0xFF) / 255F);
        float xPosG = xLight * ((colour.getGreen(5) & 0xFF) / 255F);
        float xPosB = xLight * ((colour.getBlue(5) & 0xFF) / 255F);
        
        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y - 1, z, 0)) {
            int pt = colour.getPaintType(0) & 0xFF;
            renderBuffer.setBrightness(block.getMixedBrightnessForBlock(world, x, y - 1, z));
            renderBuffer.setColorOpaque_F(yNegR, yNegG, yNegB);
            renderer.renderFaceYNeg(block, x, y, z, block.getIcon(pt, 0));
            if (meta == 1) {
                renderBuffer.setColorOpaque_B((byte)255, (byte)255, (byte)255);
                renderer.renderFaceYNeg(block, x, y - faceOffset, z, block.getIcon(0, meta));
            }
            rendered = true;
        }
        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y + 1, z, 1)) {
            int pt = colour.getPaintType(1) & 0xFF;
            renderBuffer.setBrightness(block.getMixedBrightnessForBlock(world, x, y + 1, z));
            renderBuffer.setColorOpaque_F(yPosR, yPosG, yPosB);
            renderer.renderFaceYPos(block, x, y, z, block.getIcon(pt, 0));
            if (meta == 2) {
                renderBuffer.setColorOpaque_B((byte)255, (byte)255, (byte)255);
                renderer.renderFaceYPos(block, x, y + faceOffset, z, block.getIcon(1, meta));
            }
            rendered = true;
        }
        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y, z - 1, 2)) {
            int pt = colour.getPaintType(2) & 0xFF;
            renderBuffer.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z - 1));
            renderBuffer.setColorOpaque_F(zNegR, zNegG, zNegB);
            renderer.renderFaceZNeg(block, x, y, z, block.getIcon(pt, 0));
            if (meta == 3) {
                renderBuffer.setColorOpaque_B((byte)255, (byte)255, (byte)255);
                renderer.renderFaceZNeg(block, x, y, z - faceOffset, block.getIcon(2, meta));
            }
            rendered = true;
        }
        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x, y, z + 1, 3)) {
            int pt = colour.getPaintType(3) & 0xFF;
            renderBuffer.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z + 1));
            renderBuffer.setColorOpaque_F(zPosR, zPosG, zPosB);
            renderer.renderFaceZPos(block, x, y, z, block.getIcon(pt, 0));
            if (meta == 4) {
                renderBuffer.setColorOpaque_B((byte)255, (byte)255, (byte)255);
                renderer.renderFaceZPos(block, x, y, z + faceOffset, block.getIcon(3, meta));
            }
            rendered = true;
        }
        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x - 1, y, z, 4)) {
            int pt = colour.getPaintType(4) & 0xFF;
            renderBuffer.setBrightness(block.getMixedBrightnessForBlock(world, x - 1, y, z));
            renderBuffer.setColorOpaque_F(xNegR, xNegG, xNegB);
            renderer.renderFaceXNeg(block, x, y, z, block.getIcon(pt, 0));
            if (meta == 5) {
                renderBuffer.setColorOpaque_B((byte)255, (byte)255, (byte)255);
                renderer.renderFaceXNeg(block, x - faceOffset, y, z, block.getIcon(4, meta));
            }
            rendered = true;
        }
        if (renderer.renderAllFaces || block.shouldSideBeRendered(world, x + 1, y, z, 5)) {
            int pt = colour.getPaintType(5) & 0xFF;
            renderBuffer.setBrightness(block.getMixedBrightnessForBlock(world, x + 1, y, z));
            renderBuffer.setColorOpaque_F(xPosR, xPosG, xPosB);
            renderer.renderFaceXPos(block, x, y, z, block.getIcon(pt, 0));
            if (meta == 6) {
                renderBuffer.setColorOpaque_B((byte)255, (byte)255, (byte)255);
                renderer.renderFaceXPos(block, x + faceOffset, y, z, block.getIcon(5, meta));
            }
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
}
