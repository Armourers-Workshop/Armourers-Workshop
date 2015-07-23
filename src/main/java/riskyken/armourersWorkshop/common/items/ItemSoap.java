package riskyken.armourersWorkshop.common.items;

import java.awt.Point;
import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.lib.LibSounds;
import riskyken.armourersWorkshop.common.skin.SkinTextureHelper;
import riskyken.armourersWorkshop.common.tileentities.TileEntityArmourerBrain;
import riskyken.armourersWorkshop.common.tileentities.TileEntityBoundingBox;
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
        if (block == ModBlocks.boundingBox) {
            TileEntity te = world.getTileEntity(bl);
            if (te != null && te instanceof TileEntityBoundingBox && !world.isRemote()) {
                TileEntityArmourerBrain parent = ((TileEntityBoundingBox)te).getParent();
                if (parent != null) {
                    ISkinType skinType = parent.getSkinType();
                    Point texturePoint = SkinTextureHelper.getTextureLocationFromWorldBlock((TileEntityBoundingBox)te, side);
                    parent.updatePaintData(texturePoint.x, texturePoint.y, 0x00FFFFFF);
                }
                world.playSoundEffect(bl.x + 0.5D, bl.y + 0.5D, bl.z + 0.5D, LibSounds.PAINT, 1.0F, world.rand().nextFloat() * 0.1F + 0.9F);
            }
            return true;
        }
        return false;
    }
}
