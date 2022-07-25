package moe.plushie.armourers_workshop.init.addon;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.builder.client.gui.ColorMixerScreen;
import moe.plushie.armourers_workshop.builder.client.gui.armourer.ArmourerScreen;
import moe.plushie.armourers_workshop.core.client.gui.DyeTableScreen;
import moe.plushie.armourers_workshop.core.client.gui.hologramprojector.HologramProjectorScreen;
import moe.plushie.armourers_workshop.core.client.gui.wardrobe.SkinWardrobeScreen;
import moe.plushie.armourers_workshop.library.client.gui.GlobalSkinLibraryScreen;
import moe.plushie.armourers_workshop.library.client.gui.SkinLibraryScreen;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class JEIAddon implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ArmourersWorkshop.getResource("gui-fix");
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
