package moe.plushie.armourers_workshop.compatibility.forge.addon;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.client.gui.widget.ContainerMenuScreen;
import moe.plushie.armourers_workshop.core.client.gui.widget.SlotListView;
import moe.plushie.armourers_workshop.init.ModConstants;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;

@Available("[1.21, )")
@JeiPlugin
public class AbstractForgeJEIAddon implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ModConstants.key("gui-fix").toLocation();
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiScreenHandler(SlotListView.DelegateScreen.class, screen -> null);
        registration.addGuiScreenHandler(ContainerMenuScreen.class, screen -> {
            if (screen.shouldRenderExtendScreen()) {
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

        public GuiProperties(AbstractContainerScreen<?> containerScreen) {
            this.guiLeft = containerScreen.getGuiLeft();
            this.guiTop = containerScreen.getGuiTop();
            this.guiXSize = containerScreen.getXSize();
            this.guiYSize = containerScreen.getYSize();
            this.screenClass = containerScreen.getClass();
            this.screenWidth = containerScreen.width;
            this.screenHeight = containerScreen.height;
        }

        @Override
        public Class<? extends Screen> screenClass() {
            return screenClass;
        }

        @Override
        public int guiLeft() {
            return guiLeft;
        }

        @Override
        public int guiTop() {
            return guiTop;
        }

        @Override
        public int guiXSize() {
            return guiXSize;
        }

        @Override
        public int guiYSize() {
            return guiYSize;
        }

        @Override
        public int screenWidth() {
            return screenWidth;
        }

        @Override
        public int screenHeight() {
            return screenHeight;
        }
    }
}
