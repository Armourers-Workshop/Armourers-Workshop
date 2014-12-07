package riskyken.armourersWorkshop.client.handler;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
        
        if (block != ModBlocks.mannequin) {
            return;
        }
        
        int meta = world.getBlockMetadata(x, y, z);
        
        double xOff = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialTicks;
        double yOff = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.partialTicks;
        double zOff = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialTicks;
        
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
        
        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
        
        aabb.offset(-xOff, -yOff, -zOff);
        
        GL11.glEnable(GL11.GL_BLEND);
        //OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
        GL11.glLineWidth(2.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        RenderGlobal.drawOutlinedBoundingBox(aabb, -1);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        
        event.setCanceled(true);
    }
}
