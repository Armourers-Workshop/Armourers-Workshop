package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIEdgeInsets;
import com.apple.library.uikit.UIEvent;
import com.apple.library.uikit.UIFont;
import com.apple.library.uikit.UIScreen;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.gui.element.SkinGuiElement;
import moe.plushie.armourers_workshop.core.data.ticket.TicketHolder;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.ModTextures;

import java.util.ArrayList;
import java.util.function.Consumer;

public abstract class SkinPreviewList<T> extends UIView {

    protected UIEdgeInsets contentInset = new UIEdgeInsets(0, 0, 0, 0);
    protected CGSize itemSize = new CGSize(48, 48);

    protected UIFont font = UIFont.systemFont();
    protected Consumer<T> itemSelector;
    protected ArrayList<T> entries = new ArrayList<>();
    protected TicketHolder tickets = new TicketHolder("SkinPreviewList");

    protected int minimumLineSpacing = 1;
    protected int minimumInteritemSpacing = 1;
    protected int backgroundColor = 0xC0222222;

    private int rowCount;
    private int colCount;
    private int totalCount;

    private int hoveredIndex = -1;

    private boolean showsName = true;

    public SkinPreviewList(CGRect frame) {
        super(frame);
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
        this.tickets.invalidate();
    }

    public ArrayList<T> entries() {
        return entries;
    }

    public void setEntries(ArrayList<T> entries) {
        this.tickets.invalidate();
        this.entries = new ArrayList<>(entries);
    }

    public void reloadData() {
        float boxW = innerWidth() + minimumInteritemSpacing;
        float boxH = innerHeight() + minimumLineSpacing;
        this.colCount = Math.max(1, (int) Math.floor(boxW / (itemSize.width + minimumInteritemSpacing)));
        this.rowCount = Math.max(1, (int) Math.floor(boxH / (itemSize.height + minimumLineSpacing)));
        this.totalCount = rowCount * colCount;
    }

    @Override
    public CGSize sizeThatFits(CGSize size) {
        float boxW = (size.width - contentInset.left - contentInset.right) + minimumInteritemSpacing;
        int colCount = Math.max(1, (int) Math.floor(boxW / (itemSize.width + minimumInteritemSpacing)));
        int rowCount = Math.max(1, (entries.size() + colCount - 1) / colCount);
        float width = (itemSize.width + minimumInteritemSpacing) * colCount - minimumInteritemSpacing;
        float height = (itemSize.height + minimumLineSpacing) * rowCount - minimumLineSpacing;
        return new CGSize(contentInset.left + width + contentInset.right, contentInset.top + height + contentInset.bottom);
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        super.render(point, context);
        var rect = bounds();
        var x = rect.x;
        var y = rect.y;
        var width = rect.width;
        var height = rect.height;
        if ((backgroundColor & 0xff000000) != 0) {
            context.fillRect(x, y, x + width, y + height, backgroundColor);
        }
        for (int i = 0; i < totalCount; ++i) {
            renderItem(i, false, context);
        }
        for (int i = 0; i < totalCount; ++i) {
            renderItem(i, true, context);
        }
    }

    public void renderItem(int index, boolean allowsHovered, CGGraphicsContext context) {
        if (index >= entries.size()) {
            return;
        }
        T entry = entries.get(index);
        int row = index / colCount;
        int col = index % colCount;
        float ix = (itemSize.width + minimumInteritemSpacing) * col;
        float iy = (itemSize.height + minimumLineSpacing) * row;
        float iw = itemSize.width;
        float ih = itemSize.height;
        boolean isHovered = index == hoveredIndex;//clipBox.contains(context.mouseX, context.mouseY);
        if (isHovered != allowsHovered) {
            return;
        }
        var contentScale = 1.0f;
        var clipBox = UIScreen.convertRectFromView(new CGRect(ix, iy, iw, ih), this);
        if (!context.boundingBoxOfClipPath().intersects(clipBox)) {
            return;
        }
        renderItemBackground(ix, iy, iw, ih, isHovered, entry, context);
        if (isHovered) {
            context.addClipPath(clipBox.insetBy(1, 1, 1, 1));
            contentScale = 1.5f;
        }
        renderItemContent(ix, iy, iw, ih, contentScale, entry, context);
        if (isHovered) {
            context.removeClipPath();
        }
    }

    public void renderItemContent(float x, float y, float width, float height, float contentScale, T entry, CGGraphicsContext context) {
        var bakedSkin = SkinBakery.getInstance().loadSkin(tickets.get(getItemDescriptor(entry)));
        if (bakedSkin == null) {
            int speed = 60;
            int frames = 18;

            int frame = (int) ((System.currentTimeMillis() / speed) % frames);
            int u = OpenMath.floori(frame / 9f);
            int v = frame - u * 9;
            context.drawResizableImage(ModTextures.SKIN_PANEL, x + 8, y + 8, width - 16, height - 16, u * 28, v * 28, 27, 27, 256, 256);
            return;
        }
        if (showsName) {
            var name = new NSString(getItemName(entry));
            var properties = name.split(font, width - 2);
            float iy = y + height - properties.size() * font.lineHeight() - 2;
            context.drawText(properties, x + 1, iy, 0xffeeeeee, false, font);
        }

        var texture = ArmourersWorkshop.getItemIcon(bakedSkin.type());
        if (texture != null) {
            context.drawResizableImage(texture, x + 1, y + 1, width / 4, height / 4, 0, 0, 16, 16, 16, 16);
        }

        var dx = x + width / 2;
        var dy = y + height / 2;
        var dw = width * contentScale;
        var dh = height * contentScale;
        context.draw(SkinGuiElement.blit(bakedSkin, dx - dw / 2, dy - dh / 2, 100, dw, dh, 20, 45, 0));
    }

    public void renderItemBackground(float x, float y, float width, float height, boolean isHovered, T entry, CGGraphicsContext context) {
        int backgroundColor = getItemBackgroundColor(entry, isHovered);
        int borderColor = getItemBorderColor(entry, isHovered);

        context.fillRect(x, y, x + width, y + height, backgroundColor);
        context.fillRect(x, y + 1, x + 1, y + height, borderColor);
        context.fillRect(x, y, x + width - 1, y + 1, borderColor);
        context.fillRect(x + 1, y + height - 1, x + width, y + height, borderColor);
        context.fillRect(x + width - 1, y, x + width, y + height - 1, borderColor);
    }

    public CGSize itemSize() {
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

    public Consumer<T> getItemSelector() {
        return itemSelector;
    }

    public void setItemSelector(Consumer<T> itemSelector) {
        this.itemSelector = itemSelector;
    }

    public int totalCount() {
        return this.totalCount;
    }


    protected abstract String getItemName(T value);

    protected abstract SkinDescriptor getItemDescriptor(T value);

    protected int getItemBackgroundColor(T entry, boolean isHovered) {
        if (isHovered) {
            return 0xC0777711;
        }
        return 0x22AAAAAA;
    }

    protected int getItemBorderColor(T entry, boolean isHovered) {
        if (isHovered) {
            return 0xCC888811;
        }
        return 0x22FFFFFF;
    }


    private float innerWidth() {
        return bounds().width - contentInset.left - contentInset.right;
    }

    private float innerHeight() {
        return bounds().height - contentInset.top - contentInset.bottom;
    }

    private void applyHovered(UIEvent event) {
        hoveredIndex = indexAtPoint(event.locationInView(this));
    }

    private int indexAtPoint(CGPoint point) {
        int col = (int) (point.x / (itemSize.width + minimumInteritemSpacing));
        int row = (int) (point.y / (itemSize.height + minimumLineSpacing));
        if (col < 0 || row < 0 || col >= colCount || row >= rowCount) {
            return -1;
        }
        return row * colCount + col;
    }
}
