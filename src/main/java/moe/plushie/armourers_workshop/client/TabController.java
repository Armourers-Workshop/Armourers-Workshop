package moe.plushie.armourers_workshop.client;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.utils.SkinCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TabController<Target> extends Screen {

    private static final ResourceLocation BUTTONS = SkinCore.resource("textures/gui/_controls/tabs.png");
    private static final ResourceLocation ICONS = SkinCore.resource("textures/gui/_controls/tab_icons.png");

    private ArrayList<Consumer<Tab>> listeners;

    private int x = 0;
    private int y = 0;

    private Tab selectedTab = null;

    private ArrayList<Tab> tabs = new ArrayList<>();
    private ArrayList<Tab> actives = new ArrayList<>();


    public TabController() {
        super(StringTextComponent.EMPTY);
    }

    public void clear() {
        tabs.clear();
        actives.clear();
        children.clear();
    }

    public void addListener(Consumer<Tab> listener) {
        if (this.listeners == null) {
            this.listeners = Lists.newArrayList();
        }
        this.listeners.add(listener);
    }

    public void removeListener(Consumer<Tab> listener) {
        this.listeners.remove(listener);
    }

    public Tab add(Screen screen) {
        Tab tab = new Tab(screen);
        tabs.add(tab);
        return tab;
    }

    public Tab get(int index) {
        if (index < tabs.size()) {
            return tabs.get(index);
        }
        return null;
    }

    public Tab get(double x, double y) {
        for (Tab tab : actives) {
            if (tab.isMouseOver(x, y)) {
                return tab;
            }
        }
        return null;
    }

    public void setSelectedTab(Tab selectedTab) {
        if (this.selectedTab != null) {
            this.children.remove(this.selectedTab.screen);
        }
        this.selectedTab = selectedTab;
        if (this.selectedTab != null) {
            this.selectedTab.screen.init(Minecraft.getInstance(), width, height);
            this.children.add(this.selectedTab.screen);
        }
        this.listeners.forEach(listener -> listener.accept(selectedTab));
    }

    public Tab getSelectedTab() {
        return selectedTab;
    }

    public Screen getSelectedScreen() {
        if (selectedTab != null) {
            return selectedTab.screen;
        }
        return null;
    }

    public ArrayList<Tab> getActiveTabs() {
        return actives;
    }



    public void init(int x, int y, int width, int height) {
        super.init(Minecraft.getInstance(), width, height);
        this.x = x;
        this.y = y;

        int ly = 5, ry = 5, spacing = -5;
        for (Tab tab : tabs) {
            if (!tab.visible) {
                continue;
            }
            if (tab.alignment == 0 && ly + tab.height <= height) { // left
                tab.x = x + -tab.width + 5;
                tab.y = y + ly;
                ly += tab.height;
                ly += spacing;
                actives.add(tab);
                addWidget(tab);
                continue;
            }
            if (ry + tab.height <= height) { // right
                tab.x = x + width - 4;
                tab.y = y + ry;
                tab.alignment = 1;
                ry += tab.height;
                ry += spacing;
                actives.add(tab);
                addWidget(tab);
            }
        }
    }

    @Override
    public void removed() {
        listeners.clear();
        super.removed();
    }

    public void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
        for (Tab tab : actives) {
            if (tab.isMouseOver(mouseX, mouseY)) {
                renderTooltip(matrixStack, tab.screen.getTitle(), mouseX, mouseY);
            }
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        for (Tab tab : actives) {
            tab.renderButton(matrixStack, mouseX, mouseY);
        }
        Tab tab = getSelectedTab();
        if (tab != null) {
            tab.screen.render(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

    public class Tab implements IGuiEventListener {

        int x = 0;
        int y = 0;
        int width = 26;
        int height = 30;

        int iconU = 0;
        int iconV = 0;
        int iconWidth = 16;
        int iconHeight = 16;
        int alignment = 0;
        int animationFrames = 0;
        int animationSpeed = 0;

        Target target;
        boolean visible = true;

        Screen screen;

        Tab(Screen screen) {
            this.screen = screen;
        }

        Tab setIcon(int u, int v) {
            this.iconU = u;
            this.iconV = v;
            return this;
        }

        Tab setIconAnimation(int frames, int speed) {
            this.animationFrames = frames;
            this.animationSpeed = speed;
            return this;
        }

        Tab setAlignment(int alignment) {
            this.alignment = alignment;
            return this;
        }

        Tab setVisible(boolean visible) {
            this.visible = visible;
            return this;
        }

        Target getTarget() {
            return this.target;
        }

        Tab setTarget(Target target) {
            this.target = target;
            return this;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isMouseOver(mouseX, mouseY)) {
                setSelectedTab(this);
                return true;
            }
            return false;
        }


        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            int top = 3, bottom = 3;
            return mouseX >= x && mouseX <= (x + width) && mouseY >= (y + top) && mouseY <= (y + height - bottom);
        }

        public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY) {
            int u = 0, v = height, ix = -1, iv = 0;
            if (alignment == 1) {
                u += width * 2;
                ix = 0;
            }
            if (isMouseOver(mouseX, mouseY)) {
                u += width;
                if (animationFrames > 0) {
                    int frame = (int) ((System.currentTimeMillis() / animationSpeed) % animationFrames);
                    iv += iconHeight * frame;
                }
            }
            if (getSelectedTab() == this) {
                v = 0;
            }
            Minecraft.getInstance().getTextureManager().bind(BUTTONS);
            blit(matrixStack, x, y, u, v, width, height);

            Minecraft.getInstance().getTextureManager().bind(ICONS);
            blit(matrixStack, x + (width - iconWidth) / 2 + ix, y + (height - iconHeight) / 2, iconU, iconV + iv, iconWidth, iconHeight);
        }
    }
}
