package riskyken.armourersWorkshop.common.items.paintingtool;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.api.common.painting.IPantableBlock;
import riskyken.armourersWorkshop.api.common.skin.cubes.ICubeColour;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.blocks.BlockColourable;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.items.AbstractModItem;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.lib.LibSounds;
import riskyken.armourersWorkshop.common.painting.IBlockPainter;
import riskyken.armourersWorkshop.common.painting.tool.AbstractToolOption;
import riskyken.armourersWorkshop.common.painting.tool.IConfigurableTool;
import riskyken.armourersWorkshop.common.painting.tool.ToolOptions;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;
import riskyken.armourersWorkshop.common.undo.UndoManager;
import riskyken.armourersWorkshop.utils.TranslateUtils;
import riskyken.armourersWorkshop.utils.UtilBlocks;
import riskyken.armourersWorkshop.utils.UtilItems;
import riskyken.plushieWrapper.common.world.BlockLocation;

public class ItemBlendingTool extends AbstractModItem implements IConfigurableTool, IBlockPainter {

    public ItemBlendingTool() {
        super(LibItemNames.BLENDING_TOOL);
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(LibItemResources.BLEND_TOOL);
    }
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
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
        int side = target.sideHit;
        
        Block block = world.getBlock(x, y, z);
        ItemStack stack = player.getCurrentEquippedItem();
        
        if (stack == null || stack.getItem() != this) {
            return;
        }
        if (!(block instanceof BlockColourable)) {
            return;
        }
        
        int radius = (Integer) ToolOptions.RADIUS.readFromNBT(stack.getTagCompound());
        ArrayList<BlockLocation> blocks = UtilBlocks.findTouchingBlockFaces(world, x, y, z, side, radius);
        
        double xOff = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialTicks;
        double yOff = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.partialTicks;
        double zOff = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialTicks;
        float f1 = 0.002F;
        
        for (int i = 0; i < blocks.size(); i++) {
            BlockLocation blockLoc = blocks.get(i);
            AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(blockLoc.x, blockLoc.y, blockLoc.z, blockLoc.x + 1, blockLoc.y + 1, blockLoc.z + 1);
            aabb.offset(-xOff, -yOff, -zOff);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.2F);
            GL11.glLineWidth(2.0F);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDepthMask(false);
            RenderGlobal.drawOutlinedBoundingBox(aabb.expand(f1, f1, f1), 0x4400FFFF);
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
        }
    }
    
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
            int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(x, y, z);

        if (block instanceof IPantableBlock) {
            if (!world.isRemote) {
                UndoManager.begin(player);
                usedOnBlockSide(stack, player, world, new BlockLocation(x, y, z), block, side);
                UndoManager.end(player);
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, LibSounds.BURN, 1.0F, 1.0F);
            }
            return true;
        }
        
        if (block == ModBlocks.armourerBrain & player.isSneaking()) {
            if (!world.isRemote) {
                TileEntity te = world.getTileEntity(x, y, z);
                if (te != null && te instanceof TileEntityArmourer) {
                    ((TileEntityArmourer)te).toolUsedOnArmourer(this, world, stack, player);
                }
            }
            return true;
        }
        
        return false;
    }

    @Override
    public void usedOnBlockSide(ItemStack stack, EntityPlayer player, World world, BlockLocation bl, Block block, int side) {
        int intensity = UtilItems.getIntensityFromStack(stack, 16);
        int radius = (Integer) ToolOptions.RADIUS.readFromNBT(stack.getTagCompound());
        ArrayList<BlockLocation> blocks = UtilBlocks.findTouchingBlockFaces(world, bl.x, bl.y, bl.z, side, radius);
        
        int r = 0;
        int g = 0;
        int b = 0;
        
        for (int i = 0; i < blocks.size(); i++) {
            BlockLocation loc = blocks.get(i);
            Block tarBlock = world.getBlock(loc.x, loc.y, loc.z);
            if (tarBlock instanceof IPantableBlock) {
                IPantableBlock pBlock = (IPantableBlock) tarBlock;
                ICubeColour c = pBlock.getColour(world, loc.x, loc.y, loc.z);
                r += c.getRed(side) & 0xFF;
                g += c.getGreen(side) & 0xFF;
                b += c.getBlue(side) & 0xFF;
            }
        }
        
        r = r / blocks.size();
        g = g / blocks.size();
        b = b / blocks.size();
        
        IPantableBlock worldColourable = (IPantableBlock) block;
        int oldColour = worldColourable.getColour(world, bl.x, bl.y, bl.z, side);
        byte oldPaintType = (byte) worldColourable.getPaintType(world, bl.x, bl.y, bl.z, side).getKey();
        UndoManager.blockPainted(player, world, bl.x, bl.y, bl.z, oldColour, oldPaintType, side);
        
        ((IPantableBlock)block).setColour(world, bl.x, bl.y, bl.z, new Color(r, g, b).getRGB(), side);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        super.addInformation(stack, player, list, p_77624_4_);
        int intensity = UtilItems.getIntensityFromStack(stack, 16);
        int radius = (Integer) ToolOptions.RADIUS.readFromNBT(stack.getTagCompound());
        
        list.add(TranslateUtils.translate("item.armourersworkshop:rollover.intensity", intensity));
        list.add(TranslateUtils.translate("item.armourersworkshop:rollover.radius", radius * 2 - 1 , radius * 2 - 1, 1));
        list.add(TranslateUtils.translate("item.armourersworkshop:rollover.openSettings"));
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote & player.isSneaking()) {
            player.openGui(ArmourersWorkshop.instance, LibGuiIds.TOOL_OPTIONS, world, 0, 0, 0);
        }
        return stack;
    }
    
    @Override
    public void getToolOptions(ArrayList<AbstractToolOption> toolOptionList) {
        toolOptionList.add(ToolOptions.INTENSITY);
        toolOptionList.add(ToolOptions.RADIUS_SAMPLE);
        toolOptionList.add(ToolOptions.RADIUS_EFFECT);
    }
}
