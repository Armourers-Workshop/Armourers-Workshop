package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIEvent;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.utils.MathUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public class InventoryBox extends UIControl {

    protected NSString message;

    protected CGPoint offset = new CGPoint(0, 0);
    protected CGSize itemSize = new CGSize(10, 10);

    private CGPoint mouseOffset = CGPoint.ZERO;

    public InventoryBox(CGRect frame) {
        super(frame);
    }

    @Override
    public void mouseDown(UIEvent event) {
        super.mouseDown(event);
        offset = convertPointFromView(event.locationInWindow(), null);
        sendEvent(Event.VALUE_CHANGED);
    }

    @Override
    public void mouseMoved(UIEvent event) {
        super.mouseMoved(event);
        mouseOffset = convertPointFromView(event.locationInWindow(), null);
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        super.render(point, context);
        // TODO: Refactoring
        int u = 176;
        int v = 0;
        int width = bounds().width;
        int height = bounds().height;
        int hoverWidth = MathUtils.clamp(mouseOffset.x, 0, width);
        int hoverHeight = MathUtils.clamp(mouseOffset.y, 0, height);
        for (int iy = 0; iy < height; iy += itemSize.height) {
            for (int ix = 0; ix < width; ix += itemSize.width) {
                int iu = u;
                if (ix <= offset.x && iy <= offset.y) {
                    iu += itemSize.width;
                }
                int iv = v;
                if (ix <= hoverWidth && iy <= hoverHeight && isHighlighted()) {
                    iv += itemSize.height;
                }
                context.drawImage(ModTextures.ARMOURER, ix, iy, iu, iv, itemSize.width, itemSize.height, 256, 256);
            }
        }
    }

    public CGPoint getOffset() {
        return offset;
    }

    public void setOffset(CGPoint offset) {
        this.offset = offset;
    }

    public NSString message() {
        return message;
    }

    public void setMessage(NSString message) {
        this.message = message;
    }

    public CGSize getItemSize() {
        return itemSize;
    }

    public void setItemSize(CGSize itemSize) {
        this.itemSize = itemSize;
    }
}
