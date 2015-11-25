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
import riskyken.armourersWorkshop.common.painting.PaintType;
import riskyken.armourersWorkshop.common.painting.tool.AbstractToolOption;
import riskyken.armourersWorkshop.common.painting.tool.IConfigurableTool;
import riskyken.armourersWorkshop.common.painting.tool.ToolOptions;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;
import riskyken.armourersWorkshop.common.undo.UndoManager;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.TranslateUtils;
import riskyken.plushieWrapper.common.world.BlockLocation;

public class ItemPaintbrush extends AbstractPaintingTool implements IConfigurableTool {
    
    public ItemPaintbrush() {
        super(LibItemNames.PAINTBRUSH);
    }

    @SideOnly(Side.CLIENT)
    private IIcon tipIcon;
    @SideOnly(Side.CLIENT)
    private IIcon faceModeIcon;
    @SideOnly(Side.CLIENT)
    private IIcon faceModeTipIcon;
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(LibItemResources.PAINTBRUSH);
        tipIcon = register.registerIcon(LibItemResources.PAINTBRUSH_TIP);
        faceModeIcon = register.registerIcon(LibItemResources.PAINTBRUSH_FACE_MODE);
        faceModeTipIcon = register.registerIcon(LibItemResources.PAINTBRUSH_FACE_MODE_TIP);
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
                    PaintType paintType = ((IPantable)te).getPaintType(0);
                    setToolColour(stack, colour);
                    setToolPaintType(stack, paintType);
                }
            }
            return true;
        }
        
        if (block instanceof IPantableBlock) {
            int newColour = getToolColour(stack);
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
                if ((Boolean) ToolOptions.FULL_BLOCK_MODE.readFromNBT(stack.getTagCompound())) {
                    world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, LibSounds.PAINT, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F); 
                } else {
                    world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, LibSounds.PAINT, 1.0F, world.rand.nextFloat() * 0.1F + 1.5F);
                }
            } else {
                spawnPaintParticles(world, x, y, z, side, newColour);
            }
            return true;
        }
        
        if (block == ModBlocks.armourerBrain & player.isSneaking()) {
            if (!world.isRemote) {
                TileEntity te = world.getTileEntity(x, y, z);
                if (te != null && te instanceof TileEntityArmourerBrain) {
                    ((TileEntityArmourerBrain)te).toolUsedOnArmourer(this, world, stack, player);
                }
            }
            ModLogger.log("armourer");
            return true;
        }
        
        return false;
    }
    
    @Override
    public void usedOnBlockSide(ItemStack stack, EntityPlayer player, World world, BlockLocation bl, Block block, int side) {
        int colour = getToolColour(stack);
        PaintType paintType = getToolPaintType(stack);
        IPantableBlock worldColourable = (IPantableBlock) block;
        int oldColour = worldColourable.getColour(world, bl.x, bl.y, bl.z, side);
        byte oldPaintType = (byte) worldColourable.getPaintType(world, bl.x, bl.y, bl.z, side).getKey();
        UndoManager.blockPainted(player, world, bl.x, bl.y, bl.z, oldColour, oldPaintType, side);
        ((IPantableBlock)block).setColour(world, bl.x, bl.y, bl.z, colour, side);
        ((IPantableBlock)block).setPaintType(world, bl.x, bl.y, bl.z, paintType, side);
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
        PaintType paintType = getToolPaintType(stack);
        String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
        String colourText = TranslateUtils.translate("item.armourersworkshop:rollover.colour", c.getRGB());
        String hexText = TranslateUtils.translate("item.armourersworkshop:rollover.hex", hex);
        String paintText = TranslateUtils.translate("item.armourersworkshop:rollover.paintType", paintType.toString());
        
        list.add(colourText);
        list.add(hexText);
        list.add(paintText);
        list.add(TranslateUtils.translate("item.armourersworkshop:rollover.openSettings"));
    }
    
    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        if ((Boolean) ToolOptions.FULL_BLOCK_MODE.readFromNBT(stack.getTagCompound())) {
            if (pass == 0) {
                return itemIcon;
            }
            return tipIcon;
        } else {
            if (pass == 0) {
                return faceModeIcon;
            }
            return faceModeTipIcon;
        }
    }
    
    @Override
    public void getToolOptions(ArrayList<AbstractToolOption> toolOptionList) {
        toolOptionList.add(ToolOptions.FULL_BLOCK_MODE);
    }
}
