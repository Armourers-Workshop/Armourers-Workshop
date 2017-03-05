package riskyken.armourersWorkshop.client.handler;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.items.ItemDebugTool.IDebug;
import riskyken.armourersWorkshop.common.items.ModItems;

@SideOnly(Side.CLIENT)
public class BlockHighlightRenderHandler {

    public BlockHighlightRenderHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onDrawBlockHighlightEvent(DrawBlockHighlightEvent event) {
        EntityPlayer player = event.player;
        World world = event.player.worldObj;
        MovingObjectPosition target = event.target;
        
        if (target != null && target.typeOfHit != MovingObjectType.BLOCK) {
            return;
        }
        
        int x = target.blockX;
        int y = target.blockY;
        int z = target.blockZ;
        
        Block block = world.getBlock(x, y, z);
        
        if (block == ModBlocks.mannequin) {
            drawMannequinBlockBounds(world, x, y, z, player, block, event.partialTicks);
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public void onRenderGameOverlayEvent(RenderGameOverlayEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player  = mc.thePlayer;
        World world = player.worldObj;
        if (player.getCurrentEquippedItem() == null || player.getCurrentEquippedItem().getItem() != ModItems.debugTool) {
            return;
        }
        
        if (event.type != ElementType.TEXT) {
            return;
        }
        
        MovingObjectPosition target = Minecraft.getMinecraft().objectMouseOver;
        
        if (target != null && target.typeOfHit != MovingObjectType.BLOCK) {
            return;
        }
        int x = target.blockX;
        int y = target.blockY;
        int z = target.blockZ;
        
        Block block = world.getBlock(x, y, z);
        
        FontRenderer fontRenderer = mc.fontRenderer;
        
        ArrayList<String> textLines = new ArrayList<String>();
        textLines.add("name: " + block.getLocalizedName());
        textLines.add("meta: " + world.getBlockMetadata(x, y, z));
        
        if (block instanceof IDebug) {
            IDebug debug = (IDebug) block;
            debug.getDebugHoverText(world, x, y, z, textLines);
        }
        int centerX = event.resolution.getScaledWidth() / 2;
        int centerY = event.resolution.getScaledHeight() / 2;
        
        int longestLine = 0;
        
        for (int i = 0; i < textLines.size(); i++) {
            int sWidth = fontRenderer.getStringWidth(textLines.get(i));
            longestLine = Math.max(longestLine, sWidth);
        }
        
        for (int i = 0; i < textLines.size(); i++) {
            fontRenderer.drawStringWithShadow(textLines.get(i), centerX - longestLine / 2, 5 + fontRenderer.FONT_HEIGHT * i, 0xFFFFFFFF);
        }
    }
    
    private void drawMannequinBlockBounds(World world, int x, int y, int z, EntityPlayer player, Block block, float partialTicks) {
        int meta = world.getBlockMetadata(x, y, z);
        
        double xOff = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double yOff = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double zOff = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
        
        double minX = x + block.getBlockBoundsMinX();
        double minY = y + block.getBlockBoundsMinY();
        double minZ = z + block.getBlockBoundsMinZ();
        double maxX = x + block.getBlockBoundsMaxX();
        double maxY = y + block.getBlockBoundsMaxY();
        double maxZ = z + block.getBlockBoundsMaxZ();
        
        if (meta == 0) {
            maxY += 1;
        }
        if (meta == 1) {
            minY -= 1;
        }
        float f1 = 0.002F;
        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
        
        aabb.offset(-xOff, -yOff, -zOff);
        
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
        GL11.glLineWidth(2.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(false);
        RenderGlobal.drawOutlinedBoundingBox(aabb.contract(f1, f1, f1), -1);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }
}
