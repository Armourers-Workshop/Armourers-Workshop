package moe.plushie.armourers_workshop.client.gui;

import moe.plushie.armourers_workshop.client.guidebook.GuideBook;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiGuideBook extends GuiBookBase {
    
    private ItemStack stack;
    
    public GuiGuideBook(ItemStack stack) {
        super(new GuideBook(), 256, 180);
        this.stack = stack;
    }
}
