package riskyken.armourersWorkshop.common.items;

import java.awt.Color;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.api.common.painting.IPantable;
import riskyken.armourersWorkshop.api.common.painting.IPantableBlock;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.lib.LibSounds;
import riskyken.armourersWorkshop.common.undo.UndoManager;
import riskyken.armourersWorkshop.utils.TranslateUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPaintRoller extends AbstractPaintingTool {
    
    public ItemPaintRoller() {
        super(LibItemNames.PAINT_ROLLER);
    }
    
    @SideOnly(Side.CLIENT)
    private IIcon tipIcon;
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(LibItemResources.PAINT_ROLLER);
        tipIcon = register.registerIcon(LibItemResources.PAINT_ROLLER_TIP);
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
            world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, LibSounds.PAINT, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
            for (int i = -1; i < 2; i++ ) {
                for (int j = -1; j < 2; j++ ) {
                    switch (side) {
                        case 0:
                            paintBlock(world, player, stack, x + j, y, z + i, side);
                            break;
                        case 1:
                            paintBlock(world, player, stack, x + j , y, z + i, side);
                            break;
                        case 2:
                            paintBlock(world, player, stack, x + i, y  + j, z, side);
                            break;
                        case 3:
                            paintBlock(world, player, stack, x + i, y + j, z, side);
                            break;
                        case 4:
                            paintBlock(world, player, stack, x, y + i, z + j, side);
                            break;
                        case 5:
                            paintBlock(world, player, stack, x, y + i, z + j, side);
                            break;
                    }
                }
            }
            return true;
        }
        
        return false;
    }
    
    private void paintBlock(World world, EntityPlayer player, ItemStack stack, int x, int y, int z, int side) {
        Block block = world.getBlock(x, y, z);
        if (block instanceof IPantableBlock) {
            int newColour = getToolColour(stack);
            if (!world.isRemote) {
                IPantableBlock worldColourable = (IPantableBlock) block;
                int oldColour = worldColourable.getColour(world, x, y, z, side);
                UndoManager.playerPaintedBlock(player, world, x, y, z, oldColour, side);
                ((IPantableBlock)block).setColour(world, x, y, z, newColour, side);
            } else {
                spawnPaintParticles(world, x, y, z, side, newColour);
            }
        }
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
}
