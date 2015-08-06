package riskyken.armourersWorkshop.common.items;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.ArmourersWorkshop;
import riskyken.armourersWorkshop.api.common.painting.IPantable;
import riskyken.armourersWorkshop.api.common.painting.IPantableBlock;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.lib.LibGuiIds;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.lib.LibSounds;
import riskyken.armourersWorkshop.common.painting.tool.AbstractToolOption;
import riskyken.armourersWorkshop.common.painting.tool.IConfigurableTool;
import riskyken.armourersWorkshop.common.painting.tool.ToolOptions;
import riskyken.armourersWorkshop.common.undo.UndoManager;
import riskyken.armourersWorkshop.utils.TranslateUtils;
import riskyken.plushieWrapper.common.world.BlockLocation;

public class ItemPaintbrush extends AbstractPaintingTool implements IConfigurableTool {
    
    public ItemPaintbrush() {
        super(LibItemNames.PAINTBRUSH);
    }

    @SideOnly(Side.CLIENT)
    private IIcon tipIcon;
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(LibItemResources.PAINTBRUSH);
        tipIcon = register.registerIcon(LibItemResources.PAINTBRUSH_TIP);
    }
    
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
            int side, float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(x, y, z);
        
        if (player.isSneaking() & block == ModBlocks.colourMixer) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te != null && te instanceof IPantable) {
                if (!world.isRemote) {
                    int colour = ((IPantable)te).getColour(0);
                    setToolColour(stack, colour);
                }
            }
            return true;
        }
        
        if (!getToolHasColour(stack)) {
            return false;
        }
        
        if (block instanceof IPantableBlock) {
            int newColour = getToolColour(stack);
            if (!world.isRemote) {
                if ((Boolean) ToolOptions.FULL_BLOCK_MODE.readFromNBT(stack.getTagCompound())) {
                    for (int i = 0; i < 6; i++) {
                        usedOnBlockSide(stack, player, world, new BlockLocation(x, y, z), block, i, newColour);
                    }
                } else {
                    usedOnBlockSide(stack, player, world, new BlockLocation(x, y, z), block, side, newColour);
                }
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, LibSounds.PAINT, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
            } else {
                spawnPaintParticles(world, x, y, z, side, newColour);
            }
            return true;
        }
        return false;
    }
    
    private void usedOnBlockSide(ItemStack stack, EntityPlayer player, World world, BlockLocation bl, Block block, int side, int colour) {
        IPantableBlock worldColourable = (IPantableBlock) block;
        int oldColour = worldColourable.getColour(world, bl.x, bl.y, bl.z, side);
        UndoManager.playerPaintedBlock(player, world, bl.x, bl.y, bl.z, oldColour, side);
        ((IPantableBlock)block).setColour(world, bl.x, bl.y, bl.z, colour, side);
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote & player.isSneaking()) {
            player.openGui(ArmourersWorkshop.instance, LibGuiIds.TOOL_OPTIONS, world, 0, 0, 0);
        }
        return stack;
    }
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        super.addInformation(stack, player, list, p_77624_4_);
        Color c = new Color(getToolColour(stack));
        String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
        String colourText = TranslateUtils.translate("item.armourersworkshop:rollover.colour", c.getRGB());
        String hexText = TranslateUtils.translate("item.armourersworkshop:rollover.hex", hex);
        list.add(colourText);
        list.add(hexText);
    }
    
    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        if (pass == 0) {
            return itemIcon;
        }
        return tipIcon;
    }
    
    @Override
    public void getToolOptions(ArrayList<AbstractToolOption> toolOptionList) {
        toolOptionList.add(ToolOptions.FULL_BLOCK_MODE);
    }
}
