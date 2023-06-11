package moe.plushie.armourers_workshop.library.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.uikit.UIEvent;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.render.ExtendedItemRenderer;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.ticket.Ticket;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.library.data.impl.ServerSkin;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Environment(value = EnvType.CLIENT)
public class SkinItemList extends UIView {

    protected Insets contentInset = new Insets(0, 0, 0, 0);
    protected CGSize itemSize = new CGSize(48, 48);

    protected Font font;
    protected Consumer<ServerSkin> itemSelector;
    protected ArrayList<ServerSkin> entries = new ArrayList<>();
    protected Ticket loadTicket = Ticket.list();

    protected int minimumLineSpacing = 1;
    protected int minimumInteritemSpacing = 1;
    protected int backgroundColor = 0xC0222222;

    private int rowCount;
    private int colCount;
    private int totalCount;

    private int hoveredIndex = -1;

    private boolean showsName = true;

    public SkinItemList(CGRect frame) {
        super(frame);
        this.font = Minecraft.getInstance().font;
        this.reloadData();
    }

    @Override
    public void mouseDown(UIEvent event) {
        super.mouseDown(event);
        if (itemSelector == null) {
            return;
        }
        int index = indexAtPoint(event.locationInView(this));
        if (index >= 0 && index < entries.size()) {
            itemSelector.accept(entries.get(index));
        }
    }

    @Override
    public void mouseEntered(UIEvent event) {
        super.mouseEntered(event);
        applyHovered(event);
    }

    @Override
    public void mouseMoved(UIEvent event) {
        super.mouseMoved(event);
        applyHovered(event);
    }

    @Override
    public void mouseExited(UIEvent event) {
        super.mouseExited(event);
        hoveredIndex = -1;
    }

    @Override
    public void removeFromSuperview() {
        super.removeFromSuperview();
        this.loadTicket.invalidate();
    }

    public ArrayList<ServerSkin> getEntries() {
        return entries;
    }

    public void setEntries(ArrayList<ServerSkin> entries) {
        this.loadTicket.invalidate();
        this.entries = new ArrayList<>(entries);
    }

    public void reloadData() {
        int boxW = getInnerWidth() + minimumInteritemSpacing;
        int boxH = getInnerHeight() + minimumLineSpacing;
        this.colCount = Math.max(1, (int) Math.floor(boxW / (float) (itemSize.width + minimumInteritemSpacing)));
        this.rowCount = Math.max(1, (int) Math.floor(boxH / (float) (itemSize.height + minimumLineSpacing)));
        this.totalCount = rowCount * colCount;
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        super.render(point, context);
        CGRect rect = bounds();
        int x = rect.x;
        int y = rect.y;
        int width = rect.width;
        int height = rect.height;
        if ((backgroundColor & 0xff000000) != 0) {
            context.fillRect(x, y, x + width, y + height, backgroundColor);
        }
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        for (int i = 0; i < totalCount; ++i) {
            renderItem(context, i, false, buffers);
        }
        buffers.endBatch();
        for (int i = 0; i < totalCount; ++i) {
            renderItem(context, i, true, buffers);
        }
    }

    public void renderItem(CGGraphicsContext context, int index, boolean allowsHovered, MultiBufferSource.BufferSource buffers) {
        if (index >= entries.size()) {
            return;
        }
        ServerSkin entry = entries.get(index);
        int row = index / colCount;
        int col = index % colCount;
        int ix = (itemSize.width + minimumInteritemSpacing) * col;
        int iy = (itemSize.height + minimumLineSpacing) * row;
        int iw = itemSize.width;
        int ih = itemSize.height;
        boolean isHovered = index == hoveredIndex;//clipBox.contains(context.mouseX, context.mouseY);
        if (isHovered != allowsHovered) {
            return;
        }
        CGRect clipBox = convertRectToView(new CGRect(ix, iy, iw, ih), null);
        if (!RenderSystem.inScissorRect(clipBox)) {
            return;
        }
        renderItemBackground(ix, iy, iw, ih, isHovered, entry, context);
        renderItemContent(ix, iy, iw, ih, isHovered, entry, buffers, context);
        if (isHovered) {
            context.addClipRect(clipBox.insetBy(1, 1, 1, 1));
            buffers.endBatch();
            context.removeClipRect();
        }
    }

    public void renderItemContent(int x, int y, int width, int height, boolean isHovered, ServerSkin entry, MultiBufferSource buffers, CGGraphicsContext context) {
        BakedSkin bakedSkin = SkinBakery.getInstance().loadSkin(entry.getDescriptor(), loadTicket);
        if (bakedSkin == null) {
            int speed = 60;
            int frames = 18;

            int frame = (int) ((System.currentTimeMillis() / speed) % frames);
            int u = MathUtils.floor(frame / 9f);
            int v = frame - u * 9;
            context.drawImage(ModTextures.SKIN_PANEL, x + 8, y + 8, u * 28, v * 28, width - 16, height - 16, 27, 27, 256, 256);
            return;
        }
        Skin skin = bakedSkin.getSkin();
        if (showsName) {
            String name = entry.getName();
            List<FormattedText> properties = font.getSplitter().splitLines(name, width - 2, Style.EMPTY);
            int iy = y + height - properties.size() * font.lineHeight - 2;
            RenderSystem.drawText(context.state().ctm(), font, properties, x + 1, iy, width - 2, 0, false, 9, 0xffeeeeee);
        }

        ResourceLocation texture = ArmourersWorkshop.getItemIcon(skin.getType());
        if (texture != null) {
            context.drawImage(texture, x + 1, y + 1, 0, 0, width / 4, height / 4, 16, 16, 16, 16);
        }

        int dx = x + width / 2, dy = y + height / 2, dw = width, dh = height;
        if (isHovered) {
            dw *= 1.5f;
            dh *= 1.5f;
        }

        ExtendedItemRenderer.renderSkinInBox(bakedSkin, ColorScheme.EMPTY, ItemStack.EMPTY, dx - dw / 2, dy - dw / 2, 100, dw, dh, 20, 45, 0, 0, 0xf000f0, context.state().ctm(), buffers);
    }

    public void renderItemBackground(int x, int y, int width, int height, boolean isHovered, ServerSkin entry, CGGraphicsContext context) {
        int backgroundColor = 0x22AAAAAA;
        int borderColor = 0x22FFFFFF;

        if (isHovered) {
            backgroundColor = 0xC0777711;
            borderColor = 0xCC888811;
        }

        context.fillRect(x, y, x + width, y + height, backgroundColor);
        context.fillRect(x, y + 1, x + 1, y + height, borderColor);
        context.fillRect(x, y, x + width - 1, y + 1, borderColor);
        context.fillRect(x + 1, y + height - 1, x + width, y + height, borderColor);
        context.fillRect(x + width - 1, y, x + width, y + height - 1, borderColor);

        RenderSystem.enableAlphaTest();
    }


    public CGSize getItemSize() {
        return itemSize;
    }

    public void setItemSize(CGSize itemSize) {
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

    public Consumer<ServerSkin> getItemSelector() {
        return itemSelector;
    }

    public void setItemSelector(Consumer<ServerSkin> itemSelector) {
        this.itemSelector = itemSelector;
    }

    public int getTotalCount() {
        return this.totalCount;
    }

    private int getInnerWidth() {
        return bounds().width - contentInset.left - contentInset.right;
    }

    private int getInnerHeight() {
        return bounds().height - contentInset.top - contentInset.bottom;
    }

    private void applyHovered(UIEvent event) {
        hoveredIndex = indexAtPoint(event.locationInView(this));
    }

    private int indexAtPoint(CGPoint point) {
        int col = point.x / (itemSize.width + minimumInteritemSpacing);
        int row = point.y / (itemSize.height + minimumLineSpacing);
        if (col < 0 || row < 0 || col >= colCount || row >= rowCount) {
            return -1;
        }
        return row * colCount + col;
    }
}
