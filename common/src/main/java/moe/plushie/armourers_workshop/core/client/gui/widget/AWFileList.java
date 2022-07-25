package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.library.ISkinLibrary;
import moe.plushie.armourers_workshop.core.client.render.ExtendedItemRenderer;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Environment(value = EnvType.CLIENT)
public class AWFileList extends Button implements ContainerEventHandler {

    protected final ArrayList<GuiEventListener> children = new ArrayList<>();
    protected final EntryList<Entry> list;

    protected GuiEventListener focused;
    protected GuiEventListener selectedItem;

    protected boolean dragging = false;

    public AWFileList(int x, int y, int width, int height, int rowHeight, Component p_i232255_5_, Button.OnPress changeHandler) {
        super(x, y, width, height, p_i232255_5_, changeHandler);
        this.list = new EntryList<>(x, y, width, height, rowHeight);
        this.children.add(list);
    }

    public void reloadData(Collection<ISkinLibrary.Entry> entries) {
        ArrayList<AWFileList.Entry> allEntries = new ArrayList<>();
        entries.forEach(entry -> allEntries.add(new Entry(entry)));
        list.replaceEntries(allEntries);
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float p_230431_4_) {
        list.render(matrixStack, mouseX, mouseY, p_230431_4_);
    }

    @Override
    public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
        return ContainerEventHandler.super.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_);
    }

    @Override
    public boolean mouseReleased(double p_231048_1_, double p_231048_3_, int p_231048_5_) {
        return ContainerEventHandler.super.mouseReleased(p_231048_1_, p_231048_3_, p_231048_5_);
    }

    @Override
    public boolean mouseDragged(double p_231045_1_, double p_231045_3_, int p_231045_5_, double p_231045_6_, double p_231045_8_) {
        return ContainerEventHandler.super.mouseDragged(p_231045_1_, p_231045_3_, p_231045_5_, p_231045_6_, p_231045_8_);
    }

    @Override
    public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
        return ContainerEventHandler.super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
    }

    @Override
    public boolean keyReleased(int p_223281_1_, int p_223281_2_, int p_223281_3_) {
        return ContainerEventHandler.super.keyReleased(p_223281_1_, p_223281_2_, p_223281_3_);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return children;
    }

    @Override
    public boolean isDragging() {
        return dragging;
    }

    @Override
    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    @Nullable
    @Override
    public GuiEventListener getFocused() {
        return focused;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        this.focused = focused;
    }

    @Override
    public boolean changeFocus(boolean p_231049_1_) {
        return ContainerEventHandler.super.changeFocus(p_231049_1_);
    }

    public Button getThis() {
        return this;
    }

    public SkinDescriptor getHoveredSkin(double mouseX, double mouseY) {
        if (mouseX > x + 16) {
            return SkinDescriptor.EMPTY;
        }
        Entry entry = list.getEntry(mouseX, mouseY);
        if (entry != null) {
            return entry.getDescriptor();
        }
        return SkinDescriptor.EMPTY;
    }

    public ISkinLibrary.Entry getItem(int index) {
        List<Entry> entries = list.children();
        if (index >= 0 && index < entries.size()) {
            return entries.get(index).entry;
        }
        return null;
    }

    protected void setSelectedItemWithEntry(GuiEventListener gui) {
        this.selectedItem = gui;
        this.onPress();
    }

    @Nullable
    public ISkinLibrary.Entry getSelectedItem() {
        if (selectedItem instanceof Entry) {
            return ((Entry) selectedItem).entry;
        }
        return null;
    }

    public void setSelectedItem(ISkinLibrary.Entry entry) {
        this.setSelectedItemWithEntry(null);
    }

    public class Entry extends ContainerObjectSelectionList.Entry<Entry> {

        final Font font;
        final Component title;
        final ArrayList<GuiEventListener> children = new ArrayList<>();

        SkinDescriptor descriptor = SkinDescriptor.EMPTY;
        ISkinLibrary.Entry entry;

        public Entry(ISkinLibrary.Entry entry) {
            this.title = new TextComponent(entry.getName());
            this.font = Minecraft.getInstance().font;
            this.entry = entry;
            if (!entry.isDirectory()) {
                String identifier = entry.getNamespace() + ":" + entry.getPath();
                descriptor = new SkinDescriptor(identifier, entry.getSkinType(), ColorScheme.EMPTY);
            }
        }

        public void renderIcon(PoseStack matrixStack, int x, int y, int width, int height) {
            if (entry.isDirectory()) {
                int u = entry.isPrivateDirectory() ? 32 : 16;
                RenderUtils.blit(matrixStack, x + (width - 12) / 2, y + (height - 12) / 2 - 1, u, 0, 12, 12, RenderUtils.TEX_LIST);
                return;
            }
            if (!getDescriptor().isEmpty()) {
                MultiBufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
                ExtendedItemRenderer.renderSkin(getDescriptor(), ItemStack.EMPTY, x, y, 100, width, height - 1, 160, 45, 0, matrixStack, buffers);
            }
        }

        @Override
        public void render(PoseStack matrixStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float p_230432_10_) {
            int textColor = 0xffaaaaaa;
            int backgroundColor = 0;
            int iconOffset = 0;

            if (isMouseOver) {
                textColor = 0xff000000;
                backgroundColor = 0xffcccccc;
            }
            if (entry.isDirectory()) {
                textColor = 0xff88ff88;
            }
            if (entry.isPrivateDirectory()) {
                textColor = 0xff8888ff;
            }
            if (selectedItem == this) {
                textColor = 0xff000000;
                backgroundColor = 0xffffff88;
            }
            if (entry.isDirectory() || !getDescriptor().isEmpty()) {
                iconOffset = 16;
            }
            if (backgroundColor != 0) {
                fill(matrixStack, left, top, left + width - 3, top + height + 4, backgroundColor);
            }
            font.draw(matrixStack, title, left + iconOffset + 2, top + 3, textColor);
            renderIcon(matrixStack, left, top - 1, 16, 16);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int p_231044_5_) {
            return true;
        }

        @Override
        public java.util.List<? extends GuiEventListener> children() {
            return children;
        }

        public SkinDescriptor getDescriptor() {
            if (ModConfig.Common.allowLibraryPreviews) {
                return descriptor;
            }
            return SkinDescriptor.EMPTY;
        }

    }

    public class EntryList<T extends ContainerObjectSelectionList.Entry<T>> extends ContainerObjectSelectionList<T> {

        public EntryList(int x0, int y0, int width, int height, int itemHeight) {
            super(Minecraft.getInstance(), width, height, y0, y0 + height, itemHeight);
            this.x0 = x0;
            this.x1 = x0 + width;
            this.y0 = y0;
            this.y1 = y0 + height;
            this.setRenderBackground(false);
            this.setRenderTopAndBottom(false);
            this.setRenderSelection(false);
        }

        @Override
        public void render(PoseStack matrixStack, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
            RenderUtils.tile(matrixStack, x0, y0, 0, 0, width, height, 11, 11, 1, 1, 1, 1, RenderUtils.TEX_LIST);
            RenderUtils.enableScissor(x0 + 1, y0 + 1, width - 2, height - 2);
            MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
            super.render(matrixStack, p_230430_2_, p_230430_3_, p_230430_4_);
            buffers.endBatch();
            RenderUtils.disableScissor();
        }

        @Override
        public void setSelected(@Nullable T entry) {
            super.setSelected(entry);
            setSelectedItemWithEntry(entry);
        }

        @Override
        public void setFocused(@Nullable GuiEventListener entry) {
            super.setFocused(entry);
            if (entry instanceof ContainerObjectSelectionList.Entry) {
                setSelected((T) entry);
            } else {
                setSelected(null);
            }
        }

        @Override
        public int getRowWidth() {
            return width;
        }

        @Override
        protected int getScrollbarPosition() {
            return x1 - 6;
        }

        @Override
        public void replaceEntries(Collection<T> children) {
            super.replaceEntries(children);
            this.setScrollAmount(getScrollAmount());
        }

        public T getEntry(double mouseX, double mouseY) {
            return getEntryAtPosition(mouseX, mouseY);
        }
    }
}
