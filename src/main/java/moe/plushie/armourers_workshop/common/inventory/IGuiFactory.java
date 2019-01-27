package moe.plushie.armourers_workshop.common.inventory;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IGuiFactory {
    
    public Container getServerGuiElement(EntityPlayer player, World world, BlockPos pos);
    
    @SideOnly(Side.CLIENT)
    public GuiScreen getClientGuiElement(EntityPlayer player, World world, BlockPos pos);
}
