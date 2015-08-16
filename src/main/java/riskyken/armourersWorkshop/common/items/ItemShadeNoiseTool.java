package riskyken.armourersWorkshop.common.items;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.api.common.painting.IPantableBlock;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.painting.tool.AbstractToolOption;
import riskyken.armourersWorkshop.common.painting.tool.IConfigurableTool;
import riskyken.armourersWorkshop.common.painting.tool.ToolOptions;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;
import riskyken.armourersWorkshop.common.undo.UndoManager;
import riskyken.armourersWorkshop.utils.TranslateUtils;
import riskyken.armourersWorkshop.utils.UtilColour;
import riskyken.armourersWorkshop.utils.UtilItems;
import riskyken.plushieWrapper.common.world.BlockLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemShadeNoiseTool extends AbstractModItem implements IConfigurableTool {

    public ItemShadeNoiseTool() {
        super(LibItemNames.SHADE_NOISE_TOOL);
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
                if ((Boolean) ToolOptions.FULL_BLOCK_MODE.readFromNBT(stack.getTagCompound())) {
                    for (int i = 0; i < 6; i++) {
                        usedOnBlockSide(stack, player, world, new BlockLocation(x, y, z), block, i);
                    }
                } else {
                    usedOnBlockSide(stack, player, world, new BlockLocation(x, y, z), block, side);
                }
                UndoManager.end(player);
            }
            return true;
        }
        
        if (block == ModBlocks.armourerBrain & player.isSneaking()) {
            if (!world.isRemote) {
                TileEntity te = world.getTileEntity(x, y, z);
                if (te != null && te instanceof TileEntityArmourerBrain) {
                    int intensity = UtilItems.getIntensityFromStack(stack, 16);
                    ((TileEntityArmourerBrain)te).addNoise(player, intensity);
                }
            }
            return true;
        }
        
        return false;
    }
    
    
    private void usedOnBlockSide(ItemStack stack, EntityPlayer player, World world, BlockLocation bl, Block block, int side) {
        int intensity = UtilItems.getIntensityFromStack(stack, 16);
        IPantableBlock worldColourable = (IPantableBlock) block;
        int oldColour = worldColourable.getColour(world, bl.x, bl.y, bl.z, side);
        int newColour = UtilColour.addShadeNoise(new Color(oldColour), intensity).getRGB();
        UndoManager.blockPainted(player, world, bl.x, bl.y, bl.z, oldColour, side);
        ((IPantableBlock) block).setColour(world, bl.x, bl.y, bl.z, newColour, side);
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
    }

    @Override
    public void getToolOptions(ArrayList<AbstractToolOption> toolOptionList) {
        toolOptionList.add(ToolOptions.FULL_BLOCK_MODE);
        toolOptionList.add(ToolOptions.INTENSITY);
    }
}
