package moe.plushie.armourers_workshop.client.render.tileentities;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.painting.IPaintType;
import moe.plushie.armourers_workshop.api.common.skin.cubes.ICubeColour;
import moe.plushie.armourers_workshop.client.render.IRenderBuffer;
import moe.plushie.armourers_workshop.client.render.ModRenderHelper;
import moe.plushie.armourers_workshop.client.render.RenderBridge;
import moe.plushie.armourers_workshop.common.init.items.ModItems;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.painting.IBlockPainter;
import moe.plushie.armourers_workshop.common.painting.PaintTypeRegistry;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityColourable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockColourable extends TileEntitySpecialRenderer<TileEntityColourable> {

    public static final ResourceLocation MARKERS = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/tile-entities/markers.png");
    public static float markerAlpha = 0F;
    private static long lastWorldTimeUpdate;

    private final IRenderBuffer renderer;

    public RenderBlockColourable() {
        renderer = RenderBridge.INSTANCE;
    }

    @Override
    public void render(TileEntityColourable tileEntity, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        updateAlpha(tileEntity);
        if (!(markerAlpha > 0)) {
            return;
        }

        ICubeColour cubeColour = tileEntity.getColour();
        GlStateManager.pushAttrib();
        // ModRenderHelper.disableLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        ModRenderHelper.enableAlphaBlend();
        GlStateManager.color(1F, 1F, 1F, markerAlpha);
        bindTexture(MARKERS);
        renderer.startDrawingQuads(DefaultVertexFormats.POSITION_TEX);
        for (int i = 0; i < 6; i++) {
            EnumFacing dir = EnumFacing.byIndex(i);
            int paintType = cubeColour.getPaintType(i) & 0xFF;
            if (paintType != 255) {
                IPaintType pt = PaintTypeRegistry.getInstance().getPaintTypeFromIndex(paintType);
                renderFaceWithMarker(renderer, x, y, z, dir, pt.getMarkerIndex());
            }
        }
        renderer.draw();
        GlStateManager.color(1F, 1F, 1F, 1F);
        ModRenderHelper.disableAlphaBlend();
        ModRenderHelper.enableLighting();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popAttrib();
    }

    public static void updateAlpha(TileEntity tileEntity) {
        if (lastWorldTimeUpdate != tileEntity.getWorld().getTotalWorldTime()) {
            lastWorldTimeUpdate = tileEntity.getWorld().getTotalWorldTime();
            if (isPlayerHoldingPaintingTool()) {
                markerAlpha += 0.25F;
                if (markerAlpha > 1F) {
                    markerAlpha = 1F;
                }
            } else {
                markerAlpha -= 0.25F;
                if (markerAlpha < 0F) {
                    markerAlpha = 0F;
                }
            }
        }
    }

    public static void renderFaceWithMarker(IRenderBuffer renderer, double x, double y, double z, EnumFacing face, int marker) {
        Tessellator tess = Tessellator.getInstance();
        float tileScale = 0.125F;
        float ySrc = (float) Math.floor((double) marker / 8F);
        float xSrc = marker - (ySrc * 8);
        float xStart = tileScale * xSrc;
        float yStart = tileScale * ySrc;
        float xEnd = xStart + tileScale * 1;
        float yEnd = yStart + tileScale * 1;
        float offset = 0.001F;

        switch (face) {
        case DOWN:
            tess.getBuffer().pos(x, y - offset, z).tex(xStart, yEnd).endVertex();
            renderer.addVertexWithUV(x + 1F, y - offset, z, xEnd, yEnd);
            renderer.endVertex();
            renderer.addVertexWithUV(x + 1F, y - offset, z + 1F, xEnd, yStart);
            renderer.endVertex();
            renderer.addVertexWithUV(x, y - offset, z + 1F, xStart, yStart);
            renderer.endVertex();
            break;
        case UP:
            renderer.addVertexWithUV(x, y + 1F + offset, z + 1F, xStart, yEnd);
            renderer.endVertex();
            renderer.addVertexWithUV(x + 1F, y + 1F + offset, z + 1F, xEnd, yEnd);
            renderer.endVertex();
            renderer.addVertexWithUV(x + 1F, y + 1F + offset, z, xEnd, yStart);
            renderer.endVertex();
            renderer.addVertexWithUV(x, y + 1F + offset, z, xStart, yStart);
            renderer.endVertex();
            break;
        case NORTH:
            renderer.addVertexWithUV(x + 1F, y, z - offset, xStart, yEnd);
            renderer.endVertex();
            renderer.addVertexWithUV(x, y, z - offset, xEnd, yEnd);
            renderer.endVertex();
            renderer.addVertexWithUV(x, y + 1F, z - offset, xEnd, yStart);
            renderer.endVertex();
            renderer.addVertexWithUV(x + 1F, y + 1F, z - offset, xStart, yStart);
            renderer.endVertex();
            break;
        case SOUTH:
            renderer.addVertexWithUV(x, y, z + 1F + offset, xStart, yEnd);
            renderer.endVertex();
            renderer.addVertexWithUV(x + 1F, y, z + 1F + offset, xEnd, yEnd);
            renderer.endVertex();
            renderer.addVertexWithUV(x + 1F, y + 1F, z + 1F + offset, xEnd, yStart);
            renderer.endVertex();
            renderer.addVertexWithUV(x, y + 1F, z + 1F + offset, xStart, yStart);
            renderer.endVertex();
            break;
        case WEST:
            renderer.addVertexWithUV(x - offset, y, z, xStart, yEnd);
            renderer.endVertex();
            renderer.addVertexWithUV(x - offset, y, z + 1F, xEnd, yEnd);
            renderer.endVertex();
            renderer.addVertexWithUV(x - offset, y + 1F, z + 1F, xEnd, yStart);
            renderer.endVertex();
            renderer.addVertexWithUV(x - offset, y + 1F, z, xStart, yStart);
            renderer.endVertex();
            break;
        case EAST:
            renderer.addVertexWithUV(x + 1 + offset, y, z + 1F, xStart, yEnd);
            renderer.endVertex();
            renderer.addVertexWithUV(x + 1 + offset, y, z, xEnd, yEnd);
            renderer.endVertex();
            renderer.addVertexWithUV(x + 1 + offset, y + 1F, z, xEnd, yStart);
            renderer.endVertex();
            renderer.addVertexWithUV(x + 1 + offset, y + 1F, z + 1F, xStart, yStart);
            renderer.endVertex();
            break;
        }
    }

    private static boolean isPlayerHoldingPaintingTool() {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        ItemStack stack = player.getHeldItemMainhand();
        if (stack != null) {
            Item item = stack.getItem();
            if (item instanceof IBlockPainter) {
                return true;
            } else if (item == ModItems.COLOUR_PICKER) {
                return true;
            } else if (item == ModItems.BLOCK_MARKER) {
                return true;
            } else if (item == ModItems.SOAP) {
                return true;
            }
        }
        return false;
    }
}
