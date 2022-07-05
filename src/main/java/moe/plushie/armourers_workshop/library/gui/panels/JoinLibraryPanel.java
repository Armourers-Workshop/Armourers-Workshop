package moe.plushie.armourers_workshop.library.gui.panels;

import com.google.common.util.concurrent.FutureCallback;
import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.gui.widget.AWExtendedButton;
import moe.plushie.armourers_workshop.core.gui.widget.AWImageButton;
import moe.plushie.armourers_workshop.core.gui.widget.AWLabel;
import moe.plushie.armourers_workshop.library.data.global.GlobalSkinLibraryUtils;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskBetaJoin;
import moe.plushie.armourers_workshop.library.gui.GlobalSkinLibraryScreen.Page;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.util.Strings;

import java.awt.*;
import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public class JoinLibraryPanel extends AbstractLibraryPanel {

    private static final String URL_DISCORD = "https://discord.gg/5Z3KKvU";
    private static final String URL_WIKI_FAQ = "https://github.com/RiskyKen/Armourers-Workshop/wiki/FAQ";
    private static final String URL_VIDEO_UPDATE_JAVA = "https://youtu.be/xZfaXHulmKo";

    private final ArrayList<ITextComponent> pages = new ArrayList<>();

    private Rectangle frame;
    private AWLabel label;

    private AWImageButton buttonPrevious;
    private AWImageButton buttonNext;
    private AWExtendedButton buttonJoin;

    private int page = 0;
    private String joinFailMessage = null;
    private boolean joining = false;

    public JoinLibraryPanel() {
        super("inventory.armourers_workshop.skin-library-global.join", Page.LIBRARY_JOIN::equals);
        this.remake();
    }

    @Override
    protected void init() {
        super.init();

        int recWidth = 318;
        int recHeight = 180;
        this.frame = new Rectangle(leftPos + (width - recWidth) / 2, topPos + (height - recHeight) / 2, recWidth, recHeight);
        this.label = addLabel(frame.x + 5, frame.y + 5, frame.width - 10, frame.height - 10, pages.get(page));
        this.label.setTextColor(0x333333);

        int buttonBottom = frame.y + frame.height - 16 - 5;
        this.buttonPrevious = addIconButton(frame.x + 5, buttonBottom, 208, 80, 16, 16, "button.previousPage", this::previous);
        this.buttonNext = addIconButton(frame.x + frame.width - 16 - 5, buttonBottom, 208, 96, 16, 16, "button.nextPage", this::next);
        this.buttonJoin = addTextButton(frame.x + (frame.width - 140) / 2, buttonBottom, 140, 16, "button.join", this::join);

        this.refresh();
    }

    @Override
    public void renderBackgroundLayer(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.fillGradient(matrixStack, leftPos, topPos, leftPos + width, topPos + height, 0xC0101010, 0xD0101010);
        RenderUtils.tile(matrixStack, frame.x, frame.y, 0, 0, frame.width, frame.height, 128, 128, 4, 4, 4, 4, RenderUtils.TEX_COMMON);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            this.page = 0;
            this.remake();
            this.refresh();
        }
    }

    private AWImageButton addIconButton(int x, int y, int u, int v, int width, int height, String key, Button.IPressable handler) {
        ITextComponent tooltip = getCommonDisplayText(key);
        AWImageButton button = new AWImageButton(x, y, width, height, u, v, RenderUtils.TEX_BUTTONS, handler, this::addHoveredButton, tooltip);
        addButton(button);
        return button;
    }

    private AWExtendedButton addTextButton(int x, int y, int width, int height, String key, Button.IPressable handler) {
        ITextComponent title = getDisplayText(key);
        AWExtendedButton button = new AWExtendedButton(x, y, width, height, title, handler);
        addButton(button);
        return button;
    }

    private void previous(Button button) {
        setPage(page - 1);
    }

    private void next(Button button) {
        setPage(page + 1);
    }

    private void join(Button button) {
        joining = true;
        joinFailMessage = null;
        new GlobalTaskBetaJoin().createTaskAndRun(new FutureCallback<GlobalTaskBetaJoin.BetaJoinResult>() {
            @Override
            public void onSuccess(GlobalTaskBetaJoin.BetaJoinResult result) {
                switch (result.getJoinResult()) {
                    case JOINED:
                        onJoined();
                        break;
                    case ALREADY_JOINED:
                    case MINECRAFT_AUTH_FAIL:
                    case JOIN_FAILED:
                        onJoinedFailed(result.getMessage());
                        break;
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Minecraft.getInstance().execute(() -> onJoinedFailed(t.toString()));
            }
        });
        this.refresh();
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
        refresh();
    }

    private void refresh() {
        boolean canJoin = pages.size() != 1;
        boolean isLast = page == (pages.size() - 1);
        this.label.setMessage(pages.get(page));
        this.buttonPrevious.setEnabled(page != 0);
        this.buttonNext.setEnabled(!isLast);
        this.buttonNext.visible = canJoin;
        this.buttonPrevious.visible = canJoin;
        this.buttonJoin.visible = canJoin && isLast;
        this.buttonJoin.active = !joining;
    }

    private void remake() {
        this.pages.clear();

        int[] javaVersion = GlobalSkinLibraryUtils.getJavaVersion();
        boolean validJava = GlobalSkinLibraryUtils.isValidJavaVersion(javaVersion);

        if (!validJava) {
            ITextComponent urlWikiFaq = getURLText(URL_WIKI_FAQ);
            ITextComponent urlVideoUpdateJava = getURLText(URL_VIDEO_UPDATE_JAVA);
            this.pages.add(concat(getDisplayText("old_java", javaVersion[0], javaVersion[1], urlWikiFaq, urlVideoUpdateJava)));
            return;
        }

        this.pages.add(concat(getDisplayText("message_1.title"), "\n\n", getDisplayText("message_1.text")));
        this.pages.add(concat(getDisplayText("message_2.title"), "\n\n", getDisplayText("message_2.text")));
        this.pages.add(concat(getDisplayText("message_3.title"), "\n\n", getDisplayText("message_3.text")));
        this.pages.add(concat(getDisplayText("message_4.title"), "\n\n", getDisplayText("message_4.text")));
        this.pages.add(concat(getDisplayText("message_5.title"), "\n\n", getDisplayText("message_5.text"), getURLText(URL_DISCORD)));

        if (Strings.isNotBlank(joinFailMessage)) {
            StringTextComponent message = (StringTextComponent) pages.get(pages.size() - 1);
            message.append("\n\n");
            message.append("§cError: " + joinFailMessage + "§r");
            message.append("\n\n");
        }

        if (this.label != null) {
            this.label.setMessage(pages.get(page));
        }
    }
    private void setPage(int page) {
        if (page < 0 || page >= pages.size()) {
            return;
        }
        this.page = page;
        this.refresh();
    }

    private StringTextComponent concat(Object... keys) {
        StringTextComponent message = new StringTextComponent("");
        for (Object key : keys) {
            if (key instanceof String) {
                message.append((String) key);
            }
            if (key instanceof ITextComponent) {
                message.append((ITextComponent) key);
            }
        }
        return message;
    }
}
