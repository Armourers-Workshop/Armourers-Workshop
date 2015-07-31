package riskyken.armourersWorkshop.common.items;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import riskyken.armourersWorkshop.api.common.painting.IPantableBlock;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.blocks.BlockBoundingBox;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.minecraftWrapper.common.entity.EntityPlayerPointer;
import riskyken.minecraftWrapper.common.item.ItemStackPointer;
import riskyken.minecraftWrapper.common.world.BlockLocation;
import riskyken.minecraftWrapper.common.world.WorldPointer;

public class ItemSoap extends AbstractModItemNew {

    public ItemSoap() {
        super(LibItemNames.SOAP);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(ArrayList<String> iconList) {
        iconList.add(LibItemResources.SOAP);
    }
    
    @Override
    public int getColorFromItemStack(ItemStackPointer stack, int pass) {
        return 0xFFFF7FD2;
    }
    
    @Override
    public boolean onItemUse(ItemStackPointer stack, EntityPlayerPointer player, WorldPointer world,
            BlockLocation bl, int side, float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(bl);
        if (block instanceof IPantableBlock) {
            IPantableBlock paintableBlock = (IPantableBlock) block;
            //DOTO This may make block sides transparent.
        }
        if (block == ModBlocks.boundingBox) {
            BlockBoundingBox bb = (BlockBoundingBox) block;
            if (!world.isRemote()) {
                //bb.setColour(world.getMinecraftWorld(), bl.x, bl.y, bl.z, 0x00FFFFFF, side);
                //world.playSoundEffect(bl.x + 0.5D, bl.y + 0.5D, bl.z + 0.5D, LibSounds.PAINT, 1.0F, world.rand().nextFloat() * 0.1F + 0.9F);
            }
            return true;
        }
        return false;
    }
}
