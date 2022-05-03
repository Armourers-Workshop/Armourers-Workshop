package moe.plushie.armourers_workshop.library.gui.panels;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.gui.widget.AWLabel;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import moe.plushie.armourers_workshop.library.container.GlobalSkinLibraryContainer;
import moe.plushie.armourers_workshop.library.gui.GlobalSkinLibraryScreen;
import moe.plushie.armourers_workshop.library.gui.GlobalSkinLibraryScreen.Page;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

@SuppressWarnings({"unused", "NullableProblems"})
@OnlyIn(Dist.CLIENT)
public abstract class AbstractLibraryPanel extends Screen {

    public int leftPos = 0;
    public int topPos = 0;
    public int titleLabelX = 0;
    public int titleLabelY = 0;
    public boolean visible = false;
    public String baseKey;

    public final Predicate<Page> predicate;

    protected Button lastHoveredButton;
    protected GlobalSkinLibraryScreen.Router router;

    public AbstractLibraryPanel(String titleKey, Predicate<Page> predicate) {
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

    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.draw(matrixStack, this.getTitle(), (float) this.titleLabelX, (float) this.titleLabelY, 0xffffff);
    }

    public void renderBackgroundLayer(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

    }

    public void renderTooltipLayer(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (this.lastHoveredButton != null) {
            this.renderTooltip(matrixStack, lastHoveredButton.getMessage(), mouseX, mouseY);
            this.lastHoveredButton = null;
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        for (IGuiEventListener it : children) {
            if (it instanceof Widget) {
                Widget widget = (Widget) it;
                if (widget.visible) {
                    widget.render(matrixStack, mouseX, mouseY, partialTicks);
                }
            }
        }
        this.renderLabels(matrixStack, mouseX, mouseY);
    }

    public Optional<GlobalSkinLibraryContainer> getMenu() {
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

    protected AWLabel addLabel(int x, int y, int width, int height, ITextComponent message) {
        AWLabel label = new AWLabel(x, y, width, height, message);
        addWidget(label);
        return label;
    }

    @Override
    public void setFocused(@Nullable IGuiEventListener p_231035_1_) {
        if (getFocused() != p_231035_1_) {
            if (getFocused() instanceof Widget) {
                Widget widget = (Widget) getFocused();
                if (widget.isFocused()) {
                    widget.changeFocus(false);
                }
            }
        }
        super.setFocused(p_231035_1_);
    }

    protected ITextComponent getDisplayText(String key) {
        return TranslateUtils.title(baseKey + "." + key);
    }

    protected ITextComponent getDisplayText(String key, Object... objects) {
        return TranslateUtils.title(baseKey + "." + key, objects);
    }

    protected ITextComponent getCommonDisplayText(String key) {
        return TranslateUtils.title("inventory.armourers_workshop.common" + "." + key);
    }

    protected ITextComponent getURLText(String url) {
        Style style = Style.EMPTY.withColor(TextFormatting.BLUE).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        return new StringTextComponent(url).withStyle(style);
    }

    protected void addHoveredButton(Button button, MatrixStack matrixStack, int mouseX, int mouseY) {
        this.lastHoveredButton = button;
    }

    public GlobalSkinLibraryScreen.Router getRouter() {
        return router;
    }

    public void setRouter(GlobalSkinLibraryScreen.Router router) {
        this.router = router;
    }

}
