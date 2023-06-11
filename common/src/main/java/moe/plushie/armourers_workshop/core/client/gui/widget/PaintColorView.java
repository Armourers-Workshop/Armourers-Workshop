package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.api.skin.ISkinPaintType;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.utils.TickUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public class PaintColorView extends UIView {

    private UIColor color = UIColor.WHITE;
    private ISkinPaintType paintType = SkinPaintTypes.NORMAL;

    public PaintColorView(CGRect frame) {
        super(frame);
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        super.render(point, context);
        int cu = (int) paintType.getU();
        int cv = (int) paintType.getV();
        int dv = (cv + (int)TickUtils.getPaintTextureOffset()) % 256;

        if (paintType != SkinPaintTypes.RAINBOW) {
            context.setBlendColor(color);
        }

        CGRect rect = bounds();
        context.drawImage(ModTextures.CUBE, 0, 0, cu, dv, rect.width, rect.height, 1, 1, 256, 256);
        if (paintType != SkinPaintTypes.RAINBOW) {
            context.setBlendColor(UIColor.WHITE);
        }
    }

    public PaintColor paintColor() {
        return PaintColor.of(color.getRGB(), paintType);
    }

    public ISkinPaintType paintType() {
        return paintType;
    }

    public void setPaintType(ISkinPaintType paintType) {
        this.paintType = paintType;
    }

    public UIColor color() {
        return color;
    }

    public void setColor(UIColor color) {
        this.color = color;
    }
}
