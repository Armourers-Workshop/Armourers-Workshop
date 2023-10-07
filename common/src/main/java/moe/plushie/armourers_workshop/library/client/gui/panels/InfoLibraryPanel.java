package moe.plushie.armourers_workshop.library.client.gui.panels;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSMutableString;
import com.apple.library.foundation.NSTextAlignment;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UILabelDelegate;
import moe.plushie.armourers_workshop.library.client.gui.GlobalSkinLibraryWindow;
import moe.plushie.armourers_workshop.library.data.GlobalSkinLibrary;
import moe.plushie.armourers_workshop.library.data.impl.ServerStatus;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class InfoLibraryPanel extends AbstractLibraryPanel implements UILabelDelegate {

    private static final String URL_DISCORD = "https://discord.gg/5Z3KKvU";
    private static final String URL_GITHUB = "https://github.com/Armourers-Workshop/Armourers-Workshop";
    private static final String URL_REDDIT = "https://www.reddit.com/r/ArmourersWorkshop/";
    private static final String URL_DONATION = "https://ko-fi.com/riskyken";

    private final UILabel label = new UILabel(CGRect.ZERO);

    private ServerStatus stats = null;
    private String failMessage = null;

    public InfoLibraryPanel() {
        super("inventory.armourers_workshop.skin-library-global.panel.info", GlobalSkinLibraryWindow.Page.LIBRARY_INFO::equals);
        this.setup();
    }

    private void setup() {
        label.setFrame(bounds().insetBy(5, 5, 5, 5));
        label.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        label.setTextVerticalAlignment(NSTextAlignment.Vertical.TOP);
        label.setNumberOfLines(0);
        label.setTextColor(UIColor.WHITE);
        label.setUserInteractionEnabled(true);
        label.setDelegate(this);
        addSubview(label);
    }

    @Override
    public void refresh() {
        super.refresh();
        stats = null;
        failMessage = null;
        reloadUI();
        GlobalSkinLibrary.getInstance().getServerStatus((results, exception) -> {
            if (exception != null) {
                failMessage = exception.getMessage();
            } else {
                stats = results;
            }
            reloadUI();
        });
    }

    public void reloadUI() {
        NSMutableString message = new NSMutableString("\n\n\n");

        if (stats != null) {
            message.append(getDisplayText("total_skins", stats.getTotalSkin()));
            message.append("\n\n");
            message.append(getDisplayText("download_count", stats.getDownloadsLastHour(), stats.getDownloadsLastDay(), stats.getDownloadsLastWeek()));
            message.append("\n\n");
        } else {
            if (failMessage != null) {
                message.append(getDisplayText("error_getting_stats"));
                message.append("\n\n");
                message.append(failMessage);
                message.append("\n\n");
            } else {
                message.append(getDisplayText("loading"));
                message.append("\n\n");
                message.append("\n\n");
            }
        }

        message.append("\n");
        message.append(getDisplayText("links"));
        message.append("\n\n");

        message.append(getDisplayText("link.discord"));
        message.append(" ");
        message.append(getURLText(URL_DISCORD));
        message.append("\n\n");

        message.append(getDisplayText("link.github"));
        message.append(" ");
        message.append(getURLText(URL_GITHUB));
        message.append("\n\n");

        message.append(getDisplayText("link.reddit"));
        message.append(" ");
        message.append(getURLText(URL_REDDIT));
        message.append("\n\n");

        message.append("\n");

        message.append(getDisplayText("link.donation"));
        message.append(" ");
        message.append(getURLText(URL_DONATION));
        message.append("\n\n");

        label.setText(message);
    }

    @Override
    public void labelWillClickAttributes(UILabel label, Map<String, ?> attributes) {
        if (router != null) {
            router.labelWillClickAttributes(label, attributes);
        }
    }
}
