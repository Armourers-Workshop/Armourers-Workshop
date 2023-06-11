package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIEvent;
import com.apple.library.uikit.UIImage;
import moe.plushie.armourers_workshop.builder.data.palette.Palette;
import moe.plushie.armourers_workshop.init.ModTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public class PaletteBox extends UIControl {

    private int row = 4;
    private int column = 8;

    private int cellWidth = 0;
    private int cellHeight = 0;

    private int selectedIndex = 0;

    private Palette palette;
    private CGRect cachedBounds = CGRect.ZERO;
    private final UIImage backgroundImage = ModTextures.defaultButtonImage().imageAtIndex(State.DISABLED);

    public PaletteBox(CGRect frame) {
        super(frame);
    }

    @Override
    public void mouseDown(UIEvent event) {
        this.updateSelectedIndex(event.locationInView(this));
        super.mouseDown(event);
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        super.render(point, context);
        context.drawImage(backgroundImage, bounds());
        if (palette == null) {
            return;
        }
        for (int j = 0; j < row; ++j) {
            for (int i = 0; i < column; ++i) {
                int index = j * column + i;
                CGRect rect = new CGRect(i * cellWidth + 1, j * cellHeight + 1, cellWidth, cellHeight);
                context.fillRect(palette.getColor(index), rect);
            }
        }
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        CGRect rect = bounds();
        if (cachedBounds.equals(rect)) {
            return;
        }
        cellWidth = (rect.width - 2) / column;
        cellHeight = (rect.height - 2) / row;
        cachedBounds = rect;
    }

    public int row() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
        this.setNeedsLayout();
    }

    public int column() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
        this.setNeedsLayout();
    }

    public Palette getPalette() {
        return palette;
    }

    public void setPalette(Palette palette) {
        this.palette = palette;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    private void updateSelectedIndex(CGPoint point) {
        if (cellWidth == 0 || cellHeight == 0) {
            return;
        }
        CGRect rect = bounds();
        int i = (point.x - rect.x - 1) / cellWidth;
        int j = (point.y - rect.y - 1) / cellHeight;
        if (i < 0 || j < 0 || i >= column || j >= row) {
            return;
        }
        selectedIndex = j * column + i;
    }
}
