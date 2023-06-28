package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.init.ModTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PlayerInventoryView extends UIView {

    private Style style = Style.NORMAL;

    private final UIImage compactImage = UIImage.of(ModTextures.COMMON).uv(0, 180).build();
    private final UIImage backgroundImage = UIImage.of(ModTextures.PLAYER_INVENTORY).build();

    private final UILabel nameLabel = new UILabel(new CGRect(8, 5, 100, 9));

    public PlayerInventoryView(CGRect frame) {
        super(frame);
        this.nameLabel.setFrame(new CGRect(8, 5, frame.width - 16, 9));
        this.nameLabel.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleBottomMargin);
        this.addSubview(nameLabel);
    }

    @Override
    public void render(CGPoint point, CGGraphicsContext context) {
        switch (style) {
            case NORMAL: {
                context.drawImage(backgroundImage, bounds());
                break;
            }
            case COMPACT: {
                context.drawImage(compactImage, bounds().insetBy(15, 7, 7, 7));
                break;
            }
        }
        super.render(point, context);
    }

    public NSString name() {
        return nameLabel.text();
    }

    public void setName(NSString name) {
        this.nameLabel.setText(name);
    }

    public Style style() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public enum Style {
        NORMAL, COMPACT, NONE
    }
}
