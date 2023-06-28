package moe.plushie.armourers_workshop.library.client.gui.panels;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSMutableString;
import com.apple.library.foundation.NSString;
import com.apple.library.foundation.NSTextAlignment;
import com.apple.library.uikit.UIButton;
import com.apple.library.uikit.UIColor;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UILabel;
import com.apple.library.uikit.UILabelDelegate;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.library.client.gui.GlobalSkinLibraryWindow;
import moe.plushie.armourers_workshop.library.data.GlobalSkinLibrary;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class JoinLibraryPanel extends AbstractLibraryPanel implements UILabelDelegate {

    private static final String URL_DISCORD = "https://discord.gg/5Z3KKvU";
    private static final String URL_WIKI_FAQ = "https://github.com/RiskyKen/Armourers-Workshop/wiki/FAQ";
    private static final String URL_VIDEO_UPDATE_JAVA = "https://youtu.be/xZfaXHulmKo";

    private final ArrayList<NSString> pages = new ArrayList<>();

    private final UILabel label = new UILabel(CGRect.ZERO);

    private final UIButton buttonPrevious = new UIButton(CGRect.ZERO);
    private final UIButton buttonNext = new UIButton(CGRect.ZERO);
    private final UIButton buttonJoin = new UIButton(CGRect.ZERO);

    private int page = 0;
    private String joinFailMessage = null;
    private boolean joining = false;

    public JoinLibraryPanel() {
        super("inventory.armourers_workshop.skin-library-global.join", GlobalSkinLibraryWindow.Page.LIBRARY_JOIN::equals);
        this.remake();
        this.setup(bounds());
    }

    private void setup(CGRect rect) {
        int recWidth = 318;
        int recHeight = 180;

        UIView contentView = new UIView(new CGRect((rect.width - recWidth) / 2, (rect.height - recHeight) / 2, recWidth, recHeight));
        contentView.setContents(ModTextures.defaultWindowImage());
        contentView.setAutoresizingMask(AutoresizingMask.flexibleTopMargin | AutoresizingMask.flexibleBottomMargin | AutoresizingMask.flexibleLeftMargin | AutoresizingMask.flexibleRightMargin);
        addSubview(contentView);

        label.setFrame(contentView.bounds().insetBy(5, 5, 5, 5));
        label.setNumberOfLines(0);
        label.setTextVerticalAlignment(NSTextAlignment.Vertical.TOP);
        label.setTextColor(new UIColor(0x333333));
        label.setDelegate(this);
        contentView.addSubview(label);

        CGRect frame = label.frame();
        int buttonBottom = frame.getMaxY() - 16;

        buttonPrevious.setFrame(new CGRect(frame.getMinX(), buttonBottom, 16, 16));
        buttonPrevious.setTooltip(getCommonDisplayText("button.previousPage"));
        buttonPrevious.setImage(ModTextures.iconImage(208, 80, 16, 16, ModTextures.BUTTONS), UIControl.State.ALL);
        buttonPrevious.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, JoinLibraryPanel::previous);
        contentView.addSubview(buttonPrevious);

        buttonNext.setFrame(new CGRect(frame.getMaxX() - 16, buttonBottom, 16, 16));
        buttonNext.setTooltip(getCommonDisplayText("button.nextPage"));
        buttonNext.setImage(ModTextures.iconImage(208, 96, 16, 16, ModTextures.BUTTONS), UIControl.State.ALL);
        buttonNext.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, JoinLibraryPanel::next);
        contentView.addSubview(buttonNext);

        buttonJoin.setFrame(new CGRect(frame.getMidX() - 70, buttonBottom, 140, 16));
        buttonJoin.setTitle(getDisplayText("button.join"), UIControl.State.NORMAL);
        buttonJoin.setTitleColor(UIColor.WHITE, UIControl.State.NORMAL);
        buttonJoin.setBackgroundImage(ModTextures.defaultButtonImage(), UIControl.State.ALL);
        buttonJoin.addTarget(this, UIControl.Event.MOUSE_LEFT_DOWN, JoinLibraryPanel::join);
        contentView.addSubview(buttonJoin);
    }

    @Override
    public void refresh() {
        super.refresh();
        page = 0;
        remake();
        refreshStatus();
    }

    @Override
    public void labelWillClickAttributes(UILabel label, Map<String, ?> attributes) {
        if (router != null) {
            router.labelWillClickAttributes(label, attributes);
        }
    }

    private void previous(UIControl button) {
        setPage(page - 1);
    }

    private void next(UIControl button) {
        setPage(page + 1);
    }

    private void join(UIControl button) {
        joining = true;
        joinFailMessage = null;
        GlobalSkinLibrary.getInstance().join((result, exception) -> {
            if (exception != null) {
                onJoinedFailed(exception.toString());
            } else {
                onJoined();
            }
        });
        refreshStatus();
    }

    private void onJoined() {
        joining = false;
        joinFailMessage = "";
        router.showNewHome();
    }

    private void onJoinedFailed(String message) {
        joining = false;
        joinFailMessage = message;
        remake();
        refreshStatus();
    }

    private void refreshStatus() {
        boolean canJoin = pages.size() != 1;
        boolean isLast = page == (pages.size() - 1);
        label.setText(pages.get(page));
        buttonPrevious.setEnabled(page != 0);
        buttonNext.setEnabled(!isLast);
        buttonNext.setHidden(!canJoin);
        buttonPrevious.setHidden(!canJoin);
        buttonJoin.setHidden(!(canJoin && isLast));
        buttonJoin.setEnabled(!joining);
    }

    private void remake() {
        pages.clear();

        GlobalSkinLibrary library = GlobalSkinLibrary.getInstance();
        String[] javaVersion = library.getJavaVersion();
        boolean validJava = library.isValidJavaVersion();

        if (!validJava) {
            NSString urlWikiFaq = getURLText(URL_WIKI_FAQ);
            NSString urlVideoUpdateJava = getURLText(URL_VIDEO_UPDATE_JAVA);
            String update = javaVersion.length > 2 ? javaVersion[2] : "0";
            pages.add(concat(getDisplayText("old_java", javaVersion[0], update, urlWikiFaq, urlVideoUpdateJava)));
            return;
        }

        pages.add(concat(getDisplayText("message_1.title"), "\n\n", getDisplayText("message_1.text")));
        pages.add(concat(getDisplayText("message_2.title"), "\n\n", getDisplayText("message_2.text")));
        pages.add(concat(getDisplayText("message_3.title"), "\n\n", getDisplayText("message_3.text")));
        pages.add(concat(getDisplayText("message_4.title"), "\n\n", getDisplayText("message_4.text")));
        pages.add(concat(getDisplayText("message_5.title"), "\n\n", getDisplayText("message_5.text"), getURLText(URL_DISCORD)));

        if (Strings.isNotBlank(joinFailMessage)) {
            NSMutableString message = (NSMutableString) pages.get(pages.size() - 1);
            message.append("\n\n");
            message.append("§cError: " + joinFailMessage + "§r");
            message.append("\n\n");
        }

        if (label != null) {
            label.setText(pages.get(page));
        }
    }

    private void setPage(int page) {
        if (page < 0 || page >= pages.size()) {
            return;
        }
        this.page = page;
        this.refreshStatus();
    }

    private NSMutableString concat(Object... keys) {
        NSMutableString message = new NSMutableString("");
        for (Object key : keys) {
            if (key instanceof String) {
                message.append((String) key);
            }
            if (key instanceof NSString) {
                message.append((NSString) key);
            }
        }
        return message;
    }
}
