package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Environment(value = EnvType.CLIENT)
public class AWTabController<Target> extends Screen {

    private final boolean fullscreen;
    private ArrayList<Consumer<Tab>> listeners;
    private int x = 0;
    private int y = 0;
    private Tab selectedTab = null;

    private final ArrayList<Tab> tabs = new ArrayList<>();
    private final ArrayList<Tab> actives = new ArrayList<>();

    public AWTabController(boolean fullscreen) {
        super(TextComponent.EMPTY);
        this.fullscreen = fullscreen;
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
        if (this.listeners == null) {
            return;
        }
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
            if (tab.isHovered(x, y)) {
                return tab;
            }
        }
        return null;
    }

    public Collection<Tab> getActiveTabs() {
        return actives;
    }

    public Tab getSelectedTab() {
        return selectedTab;
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
        if (this.listeners != null) {
            this.listeners.forEach(listener -> listener.accept(selectedTab));
        }
    }

    public Tab getFirstActiveTab() {
        for (Tab tab : tabs) {
            if (tab.active) {
                return tab;
            }
        }
        return null;
    }

    public Screen getSelectedScreen() {
        if (selectedTab != null) {
            return selectedTab.screen;
        }
        return null;
    }

    public void init(int x, int y, int width, int height) {
        super.init(Minecraft.getInstance(), width, height);
        this.x = x;
        this.y = y;
        this.actives.clear();

        if (this.fullscreen) {
            this.initFullscreenWidgets(x, y, width, height);
        } else {
            this.initNormalWidgets(x, y, width, height);
        }

        this.actives.forEach(tab -> {
            if (tab.getScreen() instanceof AWTabPanel) {
                AWTabPanel panel = (AWTabPanel) tab.getScreen();
                panel.leftPos = x;
                panel.topPos = y;
            }
        });

        if (getSelectedScreen() != null) {
            getSelectedScreen().init(Minecraft.getInstance(), width, height);
            // auto attach
            if (!children.contains(getSelectedScreen())) {
                children.add(getSelectedScreen());
            }
        }
    }

    private void initNormalWidgets(int x, int y, int width, int height) {
        int ly = 5, ry = 5, spacing = -5;
        for (Tab tab : tabs) {
            if (!tab.active) {
                continue;
            }
            if (tab.alignment == 0 && ly + tab.height <= height) { // left
                tab.x = x + -tab.width + 5;
                tab.y = y + ly;
                tab.alignment1 = 0;
                ly += tab.height;
                ly += spacing;
                actives.add(tab);
                addWidget(tab);
                continue;
            }
            if (ry + tab.height <= height) { // right
                tab.x = x + width - 4;
                tab.y = y + ry;
                tab.alignment1 = 1;
                ry += tab.height;
                ry += spacing;
                actives.add(tab);
                addWidget(tab);
            }
        }
    }

    private void initFullscreenWidgets(int x, int y, int width, int height) {
        int ly = 0, ry = 0, spacing = -2;
        for (Tab tab : tabs) {
            if (!tab.active) {
                continue;
            }
            if (tab.alignment == 0 && ly + tab.height <= height) { // left
                tab.x = x - 4;
                tab.y = y + ly;
                tab.alignment1 = 1;
                ly += tab.height;
                ly += spacing;
                actives.add(tab);
                addWidget(tab);
                continue;
            }
            if (ry + tab.height <= height) { // right
                tab.x = x + width - tab.width + 5;
                tab.y = y + ry;
                tab.alignment1 = 0;
                ry += tab.height;
                ry += spacing;
                actives.add(tab);
                addWidget(tab);
            }
        }
        int mly = (height - (ly - spacing)) / 2, mry = (height - (ry - spacing)) / 2;
        for (Tab tab : actives) {
            if (tab.alignment1 == 1) {
                tab.y += mly;
            } else {
                tab.y += mry;
            }
        }
    }

    public void renderTooltip(PoseStack matrixStack, int mouseX, int mouseY) {
        for (Tab tab : actives) {
            if (tab.isHovered(mouseX, mouseY)) {
                renderTooltip(matrixStack, tab.screen.getTitle(), mouseX, mouseY);
            }
        }
        Screen screen = getSelectedScreen();
        if (screen instanceof AWTabPanel) {
            ((AWTabPanel) screen).renderTooltip(matrixStack, mouseX, mouseY);
        }
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        for (Tab tab : actives) {
            tab.renderButton(matrixStack, mouseX, mouseY);
        }
        Tab tab = getSelectedTab();
        if (tab != null) {
            tab.screen.render(matrixStack, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public boolean changeFocus(boolean p_231049_1_) {
        if (forwardToFocused(s -> s.changeFocus(p_231049_1_))) {
            return true;
        }
        return super.changeFocus(p_231049_1_);
    }

    @Override
    public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
        if (forwardToFocused(s -> s.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_))) {
            return true;
        }
        return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
    }

    public boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int k) {
        // click the tab.
        if (get(mouseX, mouseY) != null) {
            return false;
        }
        if (selectedTab != null && selectedTab.screen instanceof AWTabPanel) {
            AWTabPanel panel = (AWTabPanel) selectedTab.screen;
            return panel.hasClickedOutside(mouseX, mouseY, left + x, top + y, k);
        }
        return true;
    }

    private boolean forwardToFocused(Predicate<Screen> consumer) {
        if (selectedTab != null) {
            return consumer.test(selectedTab.screen);
        }
        return false;
    }

    public class Tab implements GuiEventListener {

        int x = 0;
        int y = 0;
        int width = 26;
        int height = 30;

        int iconU = 0;
        int iconV = 0;
        int iconWidth = 16;
        int iconHeight = 16;
        int alignment = 0;
        int alignment1 = 0;
        int animationFrames = 0;
        int animationSpeed = 0;

        Target target;
        boolean active = true;

        Screen screen;

        public Tab(Screen screen) {
            this.screen = screen;
        }

        public Tab setIcon(int u, int v) {
            this.iconU = u;
            this.iconV = v;
            return this;
        }

        public Tab setIconAnimation(int frames, int speed) {
            this.animationFrames = frames;
            this.animationSpeed = speed;
            return this;
        }

        public Tab setAlignment(int alignment) {
            this.alignment = alignment;
            return this;
        }

        public Tab setActive(boolean active) {
            this.active = active;
            return this;
        }

        public Target getTarget() {
            return this.target;
        }

        public Tab setTarget(Target target) {
            this.target = target;
            return this;
        }

        public Screen getScreen() {
            return screen;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isHovered(mouseX, mouseY)) {
                setSelectedTab(this);
                return true;
            }
            return false;
        }

        public boolean isHovered(double mouseX, double mouseY) {
            int top = 3, bottom = 3;
            return mouseX >= x && mouseX <= (x + width) && mouseY >= (y + top) && mouseY <= (y + height - bottom);
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return false; // reduce conflict with other button.
        }

        public void renderButton(PoseStack matrixStack, int mouseX, int mouseY) {
            int u = 0, v = height, ix = -1, iv = 0;
            if (alignment1 == 1) {
                u += width * 2;
                ix = 0;
            }
            if (isHovered(mouseX, mouseY)) {
                u += width;
                if (animationFrames > 0) {
                    int frame = (int) ((System.currentTimeMillis() / animationSpeed) % animationFrames);
                    iv += iconHeight * frame;
                }
            }
            if (getSelectedTab() == this) {
                v = 0;
            }
            int dx = (width - iconWidth) / 2 + ix;
            int dy = (height - iconHeight) / 2;
            RenderUtils.blit(matrixStack, x, y, u, v, width, height, RenderUtils.TEX_TABS);
            RenderUtils.blit(matrixStack, x + dx, y + dy, iconU, iconV + iv, iconWidth, iconHeight, RenderUtils.TEX_TAB_ICONS);
        }
    }
}