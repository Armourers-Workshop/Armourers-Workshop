package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.core.client.texture.TextureAnimationController;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintColor;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintType;
import moe.plushie.armourers_workshop.core.skin.texture.SkinPaintTypes;
import moe.plushie.armourers_workshop.core.utils.TickUtils;
import moe.plushie.armourers_workshop.init.ModTextures;

public class PaintColorView extends UIView {

    private UIColor color = UIColor.WHITE;
    private SkinPaintType paintType = SkinPaintTypes.NORMAL;

    public PaintColorView(CGRect frame) {
        super(frame);
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        super.render(point, context);
        var texture = paintType.texturePos();
        var textureMatrix = TextureAnimationController.DEFAULT.getTextureMatrix(TickUtils.animationTicks());
        var textureOffset = OpenVector3f.ZERO.transforming(textureMatrix);
        var cu = texture.u();
        var cv = texture.v();
        var dv = (int) (cv + textureOffset.y() * 256) % 256;

        if (paintType != SkinPaintTypes.RAINBOW) {
            context.setBlendColor(color);
        }

        var rect = bounds();
        context.drawResizableImage(ModTextures.CUBE, 0, 0, rect.width, rect.height, cu, dv, 1, 1, 256, 256);
        if (paintType != SkinPaintTypes.RAINBOW) {
            context.setBlendColor(UIColor.WHITE);
        }
    }

    public SkinPaintColor paintColor() {
        return SkinPaintColor.of(color.value(), paintType);
    }

    public void setPaintColor(SkinPaintColor color) {
        setColor(UIColor.of(color.argb()));
        setPaintType(color.paintType());
    }

    public SkinPaintType paintType() {
        return paintType;
    }

    public void setPaintType(SkinPaintType paintType) {
        this.paintType = paintType;
    }

    public UIColor color() {
        return color;
    }

    public void setColor(UIColor color) {
        this.color = color;
    }
}
