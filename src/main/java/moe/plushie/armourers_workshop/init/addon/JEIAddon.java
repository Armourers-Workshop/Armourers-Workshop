package moe.plushie.armourers_workshop.init.addon;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import moe.plushie.armourers_workshop.builder.gui.ColorMixerScreen;
import moe.plushie.armourers_workshop.builder.gui.armourer.ArmourerScreen;
import moe.plushie.armourers_workshop.core.gui.DyeTableScreen;
import moe.plushie.armourers_workshop.core.gui.hologramprojector.HologramProjectorScreen;
import moe.plushie.armourers_workshop.core.gui.wardrobe.SkinWardrobeScreen;
import moe.plushie.armourers_workshop.init.common.AWCore;
import moe.plushie.armourers_workshop.library.gui.GlobalSkinLibraryScreen;
import moe.plushie.armourers_workshop.library.gui.SkinLibraryScreen;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class JEIAddon implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return AWCore.resource("gui-fix");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        // core
        registration.addGuiScreenHandler(SkinWardrobeScreen.class, s -> null);
        registration.addGuiScreenHandler(HologramProjectorScreen.class, s -> null);
        registration.addGuiScreenHandler(DyeTableScreen.class, s -> null);
        // library
        registration.addGuiScreenHandler(SkinLibraryScreen.class, s -> null);
        registration.addGuiScreenHandler(GlobalSkinLibraryScreen.class, s -> null);
        // builder
        registration.addGuiScreenHandler(ColorMixerScreen.class, s -> null);
        registration.addGuiScreenHandler(ArmourerScreen.class, s -> null);
    }
}
