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

public class ItemHueTool extends AbstractPaintingTool {
    
    @SideOnly(Side.CLIENT)
    private IIcon tipIcon;
    
    public ItemHueTool() {
        super(LibItemNames.HUE_TOOL);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        itemIcon = register.registerIcon(LibItemResources.HUE_TOOL);
        tipIcon = register.registerIcon(LibItemResources.HUE_TOOL_TIP);
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
        
        if (block instanceof IPantableBlock) {
            Color toolColour = new Color(getToolColour(stack));
            float[] toolhsb;
            toolhsb = Color.RGBtoHSB(toolColour.getRed(), toolColour.getGreen(), toolColour.getBlue(), null);
            
            IPantableBlock worldColourable = (IPantableBlock) block;
            int oldColour = worldColourable.getColour(world, x, y, z, side);
            float[] blockhsb;
            Color blockColour = new Color(oldColour);
            blockhsb = Color.RGBtoHSB(blockColour.getRed(), blockColour.getGreen(), blockColour.getBlue(), null);
            
            int newColour = Color.HSBtoRGB(toolhsb[0], toolhsb[1], blockhsb[2]);
            
            if (!world.isRemote) {
                UndoManager.playerPaintedBlock(player, world, x, y, z, oldColour, side);
                ((IPantableBlock)block).setColour(world, x, y, z, newColour, side);
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, LibSounds.PAINT, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
            } else {
                spawnPaintParticles(world, x, y, z, side, newColour);
            }
            return true;
        }
        
        return false;
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
