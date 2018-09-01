package moe.plushie.armourers_workshop.client.gui;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

public class ModGuiFactory implements IModGuiFactory {

    @Override
    public void initialize(Minecraft minecraftInstance) {

    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    @Override
    public boolean hasConfigGui() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        // TODO Auto-generated method stub
        return null;
    }
}
