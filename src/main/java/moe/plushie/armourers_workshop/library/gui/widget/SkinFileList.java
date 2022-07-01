package moe.plushie.armourers_workshop.library.gui.widget;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.item.SkinItemRenderer;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.common.AWCore;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import moe.plushie.armourers_workshop.utils.color.ColorScheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Size2i;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class SkinFileList extends Button {

    protected Insets contentInset = new Insets(0, 0, 0, 0);
    protected Size2i itemSize = new Size2i(48, 48);

    protected FontRenderer font;
    protected Consumer<Entry> itemSelector;
    protected ArrayList<Entry> entries = new ArrayList<>();

    protected int minimumLineSpacing = 1;
    protected int minimumInteritemSpacing = 1;
    protected int backgroundColor = 0xC0222222;

    private int rowCount;
    private int colCount;
    private int totalCount;

    private boolean showsName = true;

    public SkinFileList(int x, int y, int width, int height) {
        super(x, y, width, height, StringTextComponent.EMPTY, Objects::hash);
        this.font = Minecraft.getInstance().font;
        this.reloadData();
    }

    public ArrayList<Entry> getEntries() {
        return entries;
    }

    public void setEntries(ArrayList<Entry> entries) {
        this.entries = entries;
    }

    public void reloadData() {
        int boxW = getInnerWidth() + minimumInteritemSpacing;
        int boxH = getInnerHeight() + minimumLineSpacing;
        this.colCount = Math.max(1, (int) Math.floor(boxW / (float) (itemSize.width + minimumInteritemSpacing)));
        this.rowCount = Math.max(1, (int) Math.floor(boxH / (float) (itemSize.height + minimumLineSpacing)));
        this.totalCount = rowCount * colCount;
    }

    public void setFrame(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.setWidth(width);
        this.setHeight(height);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int p_231044_5_) {
        if (!isMouseOver(mouseX, mouseY)) {
            return false;
        }
        int col = (int) ((mouseX - x) / (itemSize.width + minimumInteritemSpacing));
        int row = (int) ((mouseY - y) / (itemSize.height + minimumLineSpacing));
        if (col < 0 || row < 0 || col >= colCount || row >= rowCount) {
            return false;
        }
        int index = row * colCount + col;
        if (index < entries.size()) {
            Entry entry = entries.get(index);
            if (itemSelector != null) {
                itemSelector.accept(entry);
            }
            return true;
        }
        return false;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float p_230431_4_) {
        if (!RenderUtils.inScissorRect(x, y, width, height)) {
            return;
        }
        if ((backgroundColor & 0xff000000) != 0) {
            fill(matrixStack, x, y, x + width, y + height, backgroundColor);
        }
        IRenderTypeBuffer.Impl buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        for (int i = 0; i < totalCount; ++i) {
            renderItem(matrixStack, mouseX, mouseY, p_230431_4_, i, false, buffers);
        }
        buffers.endBatch();
        for (int i = 0; i < totalCount; ++i) {
            renderItem(matrixStack, mouseX, mouseY, p_230431_4_, i, true, buffers);
        }
    }

    public void renderItem(MatrixStack matrixStack, int mouseX, int mouseY, float p_230431_4_, int index, boolean allowsHovered, IRenderTypeBuffer.Impl buffers) {
        if (index >= entries.size()) {
            return;
        }
        Entry entry = entries.get(index);
        int row = index / colCount;
        int col = index % colCount;
        int ix = x + (itemSize.width + minimumInteritemSpacing) * col;
        int iy = y + (itemSize.height + minimumLineSpacing) * row;
        int iw = itemSize.width;
        int ih = itemSize.height;
        boolean isHovered = mouseX >= ix & mouseY >= iy & mouseX < ix + iw & mouseY < iy + ih;
        if (isHovered != allowsHovered) {
            return;
        }
        if (!RenderUtils.inScissorRect(ix, iy, iw, ih)) {
            return;
        }
        renderItemBackground(matrixStack, ix, iy, iw, ih, isHovered, entry);
        renderItemContent(matrixStack, ix, iy, iw, ih, isHovered, entry, buffers);
        if (isHovered) {
            RenderUtils.enableScissor(ix + 1, iy + 1, iw - 2, ih - 2);
            buffers.endBatch();
            RenderUtils.disableScissor();
        }
    }

    public void renderItemContent(MatrixStack matrixStack, int x, int y, int width, int height, boolean isHovered, Entry entry, IRenderTypeBuffer buffers) {
        BakedSkin bakedSkin = BakedSkin.of(entry.descriptor);
        if (bakedSkin == null) {
            int speed = 60;
            int frames = 18;

            int frame = (int) ((System.currentTimeMillis() / speed) % frames);
            int u = MathHelper.floor(frame / 9f);
            int v = frame - u * 9;
            RenderUtils.resize(matrixStack, x + 8, y + 8, u * 28, v * 28, width - 16, height - 16, 27, 27, RenderUtils.TEX_SKIN_PANEL);
            return;
        }
        Skin skin = bakedSkin.getSkin();
        if (showsName) {
            String name = entry.name;
            List<ITextProperties> properties = font.getSplitter().splitLines(name, width - 2, Style.EMPTY);
            int iy = y + height - properties.size() * font.lineHeight - 2;
            RenderUtils.drawText(matrixStack, font, properties, x + 1, iy, width - 2, 0, false, 9, 0xffeeeeee);
        }

        ResourceLocation texture = AWCore.getItemIcon(skin.getType());
        if (texture != null) {
            RenderUtils.resize(matrixStack, x + 1, y + 1, 0, 0, width / 4, height / 4, 16, 16, 16, 16, texture);
        }

        int dx = x + width / 2, dy = y + height / 2, dw = width, dh = height;
        if (isHovered) {
            dw *= 1.5f;
            dh *= 1.5f;
        }

        SkinItemRenderer.renderSkin(bakedSkin, ColorScheme.EMPTY, ItemStack.EMPTY, dx - dw / 2, dy - dw / 2, 100, dw, dh, 160, 45, 0, matrixStack, buffers);
    }

    public void renderItemBackground(MatrixStack matrixStack, int x, int y, int width, int height, boolean isHovered, Entry entry) {
        int backgroundColour = 0x22AAAAAA;
        int borderColour = 0x22FFFFFF;

        if (isHovered) {
            backgroundColour = 0xC0777711;
            borderColour = 0xCC888811;
        }

        fill(matrixStack, x, y, x + width, y + height, backgroundColour);

        fill(matrixStack, x, y + 1, x + 1, y + height, borderColour);
        fill(matrixStack, x, y, x + width - 1, y + 1, borderColour);
        fill(matrixStack, x + 1, y + height - 1, x + width, y + height, borderColour);
        fill(matrixStack, x + width - 1, y, x + width, y + height - 1, borderColour);

        RenderSystem.enableAlphaTest();
    }


    @Override
    public boolean changeFocus(boolean p_231049_1_) {
        return false;
    }

    public Size2i getItemSize() {
        return itemSize;
    }

    public void setItemSize(Size2i itemSize) {
        this.itemSize = itemSize;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean showsName() {
        return showsName;
    }

    public void setShowsName(boolean showsName) {
        this.showsName = showsName;
    }

    public Consumer<Entry> getItemSelector() {
        return itemSelector;
    }

    public void setItemSelector(Consumer<Entry> itemSelector) {
        this.itemSelector = itemSelector;
    }

    public int getTotalCount() {
        return this.totalCount;
    }

    private int getInnerWidth() {
        return width - contentInset.left - contentInset.right;
    }

    private int getInnerHeight() {
        return height - contentInset.top - contentInset.bottom;
    }


    public static class Entry {
        public int id;
        public int userId = 0;
        public String name;
        public String description = "";
        public SkinDescriptor descriptor;

        public int downloads = 0;
        public float rating = 0;
        public int ratingCount = 0;

        public boolean showsDownloads = true;
        public boolean showsRating = false;
        public boolean showsGlobalId = true;

        public Entry(JsonObject object) {
            this.id = object.get("id").getAsInt();
            this.name = object.get("name").getAsString();
            this.descriptor = new SkinDescriptor("ks:" + id);
            if (object.has("description")) {
                this.description = object.get("description").getAsString();
            }
            this.showsDownloads = object.has("downloads");
            if (this.showsDownloads) {
                this.downloads = object.get("downloads").getAsInt();
            }
            if (object.has("rating")) {
                this.rating = object.get("rating").getAsFloat();
                this.showsRating = true;
            }
            if (object.has("rating_count")) {
                this.ratingCount = object.get("rating_count").getAsInt();
                this.showsRating = true;
            }
            if (object.has("user_id")) {
                this.userId = object.get("user_id").getAsInt();
            }
        }
    }
}
