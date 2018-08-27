package riskyken.armourersWorkshop.common.items.paintingtool;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.api.common.painting.IPantableBlock;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.blocks.BlockLocation;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.items.AbstractModItem;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.lib.LibSounds;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientToolPaintBlock;
import riskyken.armourersWorkshop.common.painting.IBlockPainter;
import riskyken.armourersWorkshop.common.painting.PaintType;
import riskyken.armourersWorkshop.common.painting.tool.AbstractToolOption;
import riskyken.armourersWorkshop.common.painting.tool.IConfigurableTool;
import riskyken.armourersWorkshop.common.painting.tool.ToolOptions;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourer;
import riskyken.armourersWorkshop.common.undo.UndoManager;
import riskyken.armourersWorkshop.utils.TranslateUtils;
import riskyken.armourersWorkshop.utils.UtilColour;
import riskyken.armourersWorkshop.utils.UtilItems;

public class ItemShadeNoiseTool extends AbstractModItem implements IConfigurableTool, IBlockPainter {

    public ItemShadeNoiseTool() {
        super(LibItemNames.SHADE_NOISE_TOOL);
        setSortPriority(16);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(LibItemResources.SHADE_NOISE_TOOL);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
            int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(x, y, z);

        if (block instanceof IPantableBlock) {
            if (!world.isRemote) {
                UndoManager.begin(player);
            }
            
            if ((Boolean) ToolOptions.FULL_BLOCK_MODE.readFromNBT(stack.getTagCompound())) {
                for (int i = 0; i < 6; i++) {
                    usedOnBlockSide(stack, player, world, new BlockLocation(x, y, z), block, i);
                }
            } else {
                usedOnBlockSide(stack, player, world, new BlockLocation(x, y, z), block, side);
            }
            if (!world.isRemote) {
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
    
    @SuppressWarnings("deprecation")
    @Override
    public void usedOnBlockSide(ItemStack stack, EntityPlayer player, World world, BlockLocation bl, Block block, int side) {
        int intensity = UtilItems.getIntensityFromStack(stack, 16);
        IPantableBlock worldColourable = (IPantableBlock) block;
        if (worldColourable.isRemoteOnly(world, bl.x, bl.y, bl.z, side) & world.isRemote) {
            byte[] rgbt = new byte[4];
            int oldColour = worldColourable.getColour(world, bl.x, bl.y, bl.z, side);
            PaintType oldPaintType = worldColourable.getPaintType(world, bl.x, bl.y, bl.z, side);
            Color c = UtilColour.addShadeNoise(new Color(oldColour), intensity);
            rgbt[0] = (byte)c.getRed();
            rgbt[1] = (byte)c.getGreen();
            rgbt[2] = (byte)c.getBlue();
            rgbt[3] = (byte)oldPaintType.getKey();
            if (block == ModBlocks.boundingBox && oldPaintType == PaintType.NONE) {
                rgbt[3] = (byte)PaintType.NORMAL.getKey();
            }
            MessageClientToolPaintBlock message = new MessageClientToolPaintBlock(bl.x, bl.y, bl.z, (byte)side, rgbt);
            PacketHandler.networkWrapper.sendToServer(message);
        } else if(!worldColourable.isRemoteOnly(world, bl.x, bl.y, bl.z, side) & !world.isRemote) {
            int oldColour = worldColourable.getColour(world, bl.x, bl.y, bl.z, side);
            byte oldPaintType = (byte) worldColourable.getPaintType(world, bl.x, bl.y, bl.z, side).getKey();
            int newColour = UtilColour.addShadeNoise(new Color(oldColour), intensity).getRGB();
            UndoManager.blockPainted(player, world, bl.x, bl.y, bl.z, oldColour, oldPaintType, side);
            ((IPantableBlock) block).setColour(world, bl.x, bl.y, bl.z, newColour, side);
        }
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote & player.isSneaking()) {
            player.openGui(ArmourersWorkshop.instance, LibGuiIds.TOOL_OPTIONS, world, 0, 0, 0);
        }
        return stack;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        super.addInformation(stack, player, list, p_77624_4_);
        int intensity = UtilItems.getIntensityFromStack(stack, 16);
        String rollover = TranslateUtils.translate("item.armourersworkshop:rollover.intensity", intensity);
        list.add(rollover);
        list.add(TranslateUtils.translate("item.armourersworkshop:rollover.openSettings"));
    }

    @Override
    public void getToolOptions(ArrayList<AbstractToolOption> toolOptionList) {
        toolOptionList.add(ToolOptions.FULL_BLOCK_MODE);
        toolOptionList.add(ToolOptions.INTENSITY);
    }
}
