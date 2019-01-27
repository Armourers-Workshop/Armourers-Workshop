package moe.plushie.armourers_workshop.common.tileentities;

import moe.plushie.armourers_workshop.client.gui.GuiOutfitMaker;
import moe.plushie.armourers_workshop.common.inventory.ContainerOutfitMaker;
import moe.plushie.armourers_workshop.common.inventory.IGuiFactory;
import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityOutfitMaker extends AbstractTileEntityInventory implements IGuiFactory {

    public static final int OUTFIT_SKINS = 5;
    public static final int OUTFIT_ROWS = 4;
    private static final int INVENTORY_SIZE = OUTFIT_SKINS * OUTFIT_ROWS + 2;
    
    public TileEntityOutfitMaker() {
        super(INVENTORY_SIZE);
    }
    
    @Override
    public String getName() {
        return LibBlockNames.OUTFIT_MAKER;
    }

    @Override
    public Container getServerGuiElement(EntityPlayer player, World world, BlockPos pos) {
        return new ContainerOutfitMaker(player, this);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getClientGuiElement(EntityPlayer player, World world, BlockPos pos) {
        return new GuiOutfitMaker(player, this);
    }
}
