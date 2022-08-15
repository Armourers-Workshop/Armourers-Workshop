package moe.plushie.armourers_workshop.init.addon;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWAbstractContainerScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class JEIAddon implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ArmourersWorkshop.getResource("gui-fix");
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiScreenHandler(AWAbstractContainerScreen.class, screen -> {
            if (screen.shouldRenderPluginScreen()) {
                return new GuiProperties(screen);
            }
            return null;
        });
    }

    public static class GuiProperties implements IGuiProperties {
        private final Class<? extends Screen> screenClass;
        private final int guiLeft;
        private final int guiTop;
        private final int guiXSize;
        private final int guiYSize;
        private final int screenWidth;
        private final int screenHeight;

        public GuiProperties(AWAbstractContainerScreen<?> containerScreen) {
            this.guiLeft = containerScreen.getGuiLeft();
            this.guiTop = containerScreen.getGuiTop();
            this.guiXSize = containerScreen.getXSize();
            this.guiYSize = containerScreen.getYSize();
            this.screenClass = containerScreen.getClass();
            this.screenWidth = containerScreen.width;
            this.screenHeight = containerScreen.height;
        }

        @Override
        public Class<? extends Screen> getScreenClass() {
            return screenClass;
        }

        @Override
        public int getGuiLeft() {
            return guiLeft;
        }

        @Override
        public int getGuiTop() {
            return guiTop;
        }

        @Override
        public int getGuiXSize() {
            return guiXSize;
        }

        @Override
        public int getGuiYSize() {
            return guiYSize;
        }

        @Override
        public int getScreenWidth() {
            return screenWidth;
        }

        @Override
        public int getScreenHeight() {
            return screenHeight;
        }
    }
}
