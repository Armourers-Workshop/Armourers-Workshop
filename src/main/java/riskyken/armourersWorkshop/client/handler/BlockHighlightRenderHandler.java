package riskyken.armourersWorkshop.client.handler;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.api.common.skin.Rectangle3D;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.client.skin.ClientSkinCache;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinnable;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

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
        
        if (event.currentItem != null && event.currentItem.getItem() == ModItems.equipmentSkin) {
            ISkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(event.currentItem);
            if (skinPointer != null && skinPointer.getSkinType() == SkinTypeRegistry.skinBlock) {
                drawSkinnableBlockHelper(world, x, y, z, player, event.partialTicks, skinPointer);
            }
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
    
    private void drawSkinnableBlockHelper(World world, int x, int y, int z, EntityPlayer player, float partialTicks, ISkinPointer skinPointer) {
        int meta = world.getBlockMetadata(x, y, z);
        
        Rectangle3D[][][] blockGrid;
        Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer, false);
        if (skin != null) {
            blockGrid = skin.getParts().get(0).getBlockGrid();
        } else {
            return;
        }
        
        double xOff = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double yOff = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double zOff = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
        
        float f1 = 0.002F;
        float scale = 0.0625F;
        
        for (int ix = 0; ix < 3; ix++) {
            for (int iy = 0; iy < 3; iy++) {
                for (int iz = 0; iz < 3; iz++) {
                    ForgeDirection dir = ForgeDirection.NORTH;
                    int rotation = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
                    dir = TileEntitySkinnable.getDirectionFromMeta(rotation);
                    float[] bounds = TileEntitySkinnable.getBlockBounds(skin, ix, iy, -iz + 2, dir);
                    if (bounds != null) {
                        double minX = bounds[0];
                        double minY = bounds[1];
                        double minZ = bounds[2];
                        double maxX = bounds[3];
                        double maxY = bounds[4];
                        double maxZ = bounds[5];
                        
                        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
                        aabb.offset(-xOff, -yOff, -zOff);
                        aabb.offset(x, y, z);
                        aabb.offset(ix - 1, 1D + iy, iz - 2);
                        GL11.glEnable(GL11.GL_BLEND);
                        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                        GL11.glColor4f(0.0F, 1.0F, 0.0F, 0.4F);
                        if (!world.isAirBlock(x + ix - 1, y + 1 + iy, z + iz - 2)) {
                            GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.4F);
                        }
                        GL11.glLineWidth(2.0F);
                        GL11.glDisable(GL11.GL_TEXTURE_2D);
                        GL11.glDepthMask(false);
                        RenderGlobal.drawOutlinedBoundingBox(aabb.contract(f1, f1, f1), -1);
                        GL11.glDepthMask(true);
                        GL11.glEnable(GL11.GL_TEXTURE_2D);
                        GL11.glDisable(GL11.GL_BLEND);
                    }
                }
            }
        }
    }
}
