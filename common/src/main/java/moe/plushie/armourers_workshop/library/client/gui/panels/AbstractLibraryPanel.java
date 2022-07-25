package moe.plushie.armourers_workshop.library.client.gui.panels;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWLabel;
import moe.plushie.armourers_workshop.library.client.gui.GlobalSkinLibraryScreen;
import moe.plushie.armourers_workshop.library.menu.GlobalSkinLibraryMenu;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;

@SuppressWarnings({"unused"})
@Environment(value = EnvType.CLIENT)
public abstract class AbstractLibraryPanel extends Screen {

    public final Predicate<GlobalSkinLibraryScreen.Page> predicate;
    public int leftPos = 0;
    public int topPos = 0;
    public int titleLabelX = 0;
    public int titleLabelY = 0;
    public boolean visible = false;
    public String baseKey;
    protected Button lastHoveredButton;
    protected GlobalSkinLibraryScreen.Router router;

    public AbstractLibraryPanel(String titleKey, Predicate<GlobalSkinLibraryScreen.Page> predicate) {
        super(TranslateUtils.title(titleKey));
        this.baseKey = titleKey;
        this.predicate = predicate;
    }

    public void init(Minecraft minecraft, int x, int y, int width, int height) {
        this.leftPos = x;
        this.topPos = y;
        this.init(minecraft, width, height);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = leftPos + 5;
        this.titleLabelY = topPos + 5;
    }

    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.getTitle(), (float) this.titleLabelX, (float) this.titleLabelY, 0xffffff);
    }

    public void renderBackgroundLayer(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    }

    public void renderTooltipLayer(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (this.lastHoveredButton != null) {
            this.renderTooltip(matrixStack, lastHoveredButton.getMessage(), mouseX, mouseY);
            this.lastHoveredButton = null;
        }
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderLabels(matrixStack, mouseX, mouseY);
    }

    public Optional<GlobalSkinLibraryMenu> getMenu() {
        Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof GlobalSkinLibraryScreen) {
            return Optional.of(((GlobalSkinLibraryScreen) screen).getMenu());
        }
        return Optional.empty();
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    protected AWLabel addLabel(int x, int y, int width, int height, Component message) {
        AWLabel label = new AWLabel(x, y, width, height, message);
        addButton(label);
        return label;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener p_231035_1_) {
        if (getFocused() != p_231035_1_) {
            if (getFocused() instanceof AbstractWidget) {
                AbstractWidget widget = (AbstractWidget) getFocused();
                if (widget.isFocused()) {
                    widget.changeFocus(false);
                }
            }
        }
        super.setFocused(p_231035_1_);
    }

    protected Component getDisplayText(String key) {
        return TranslateUtils.title(baseKey + "." + key);
    }

    protected Component getDisplayText(String key, Object... objects) {
        return TranslateUtils.title(baseKey + "." + key, objects);
    }

    protected Component getCommonDisplayText(String key) {
        return TranslateUtils.title("inventory.armourers_workshop.common" + "." + key);
    }

    protected Component getURLText(String url) {
        Style style = Style.EMPTY.withColor(ChatFormatting.BLUE).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        return new TextComponent(url).withStyle(style);
    }

    protected void addHoveredButton(Button button, PoseStack matrixStack, int mouseX, int mouseY) {
        this.lastHoveredButton = button;
    }

    public GlobalSkinLibraryScreen.Router getRouter() {
        return router;
    }

    public void setRouter(GlobalSkinLibraryScreen.Router router) {
        this.router = router;
    }

}
