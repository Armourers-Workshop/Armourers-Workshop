package riskyken.armourersWorkshop.common.items;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.api.common.painting.IPantableBlock;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.blocks.BlockBoundingBox;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.lib.LibSounds;
import riskyken.armourersWorkshop.common.painting.PaintType;

public class ItemSoap extends AbstractModItem {

    public ItemSoap() {
        super(LibItemNames.SOAP);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon(LibItemResources.SOAP);
    }
    
    @Override
    public int getColorFromItemStack(ItemStack itemStack, int pass) {
        return 0xFFFF7FD2;
    }
    
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(x, y, z);
        if (block instanceof IPantableBlock) {
            IPantableBlock paintableBlock = (IPantableBlock) block;
            //DOTO This may make block sides transparent.
        }
        if (block == ModBlocks.boundingBox) {
            BlockBoundingBox bb = (BlockBoundingBox) block;
            if (!world.isRemote) {
                bb.setColour(world, x, y, z, 0x00FFFFFF, side);
                bb.setPaintType(world, x, y, z, PaintType.NONE, side);
                world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, LibSounds.PAINT, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
            }
            return true;
        }
        return false;
    }
}
