package riskyken.armourers_workshop.common.items;

import riskyken.armourers_workshop.common.lib.LibItemNames;

public class ItemSoap extends AbstractModItem {

    public ItemSoap() {
        super(LibItemNames.SOAP);
    }
    /*
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
    }*/
}
