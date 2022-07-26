package moe.plushie.armourers_workshop.library.client.gui.panels;

import com.google.common.util.concurrent.FutureCallback;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWExtendedButton;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWImageButton;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWLabel;
import moe.plushie.armourers_workshop.library.client.gui.GlobalSkinLibraryScreen;
import moe.plushie.armourers_workshop.library.data.global.GlobalSkinLibraryUtils;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskBetaJoin;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import moe.plushie.armourers_workshop.utils.math.Rectangle2i;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;

@Environment(value = EnvType.CLIENT)
public class JoinLibraryPanel extends AbstractLibraryPanel {

    private static final String URL_DISCORD = "https://discord.gg/5Z3KKvU";
    private static final String URL_WIKI_FAQ = "https://github.com/RiskyKen/Armourers-Workshop/wiki/FAQ";
    private static final String URL_VIDEO_UPDATE_JAVA = "https://youtu.be/xZfaXHulmKo";

    private final ArrayList<Component> pages = new ArrayList<>();

    private Rectangle2i frame;
    private AWLabel label;

    private AWImageButton buttonPrevious;
    private AWImageButton buttonNext;
    private AWExtendedButton buttonJoin;

    private int page = 0;
    private String joinFailMessage = null;
    private boolean joining = false;

    public JoinLibraryPanel() {
        super("inventory.armourers_workshop.skin-library-global.join", GlobalSkinLibraryScreen.Page.LIBRARY_JOIN::equals);
        this.remake();
    }

    @Override
    protected void init() {
        super.init();

        int recWidth = 318;
        int recHeight = 180;
        this.frame = new Rectangle2i(leftPos + (width - recWidth) / 2, topPos + (height - recHeight) / 2, recWidth, recHeight);
        this.label = addLabel(frame.getX() + 5, frame.getY() + 5, frame.getWidth() - 10, frame.getHeight() - 10, pages.get(page));
        this.label.setTextColor(0x333333);

        int buttonBottom = frame.getY() + frame.getHeight() - 16 - 5;
        this.buttonPrevious = addIconButton(frame.getX() + 5, buttonBottom, 208, 80, 16, 16, "button.previousPage", this::previous);
        this.buttonNext = addIconButton(frame.getX() + frame.getWidth() - 16 - 5, buttonBottom, 208, 96, 16, 16, "button.nextPage", this::next);
        this.buttonJoin = addTextButton(frame.getX() + (frame.getWidth() - 140) / 2, buttonBottom, 140, 16, "button.join", this::join);

        this.refresh();
    }

    @Override
    public void renderBackgroundLayer(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.fillGradient(matrixStack, leftPos, topPos, leftPos + width, topPos + height, 0xC0101010, 0xD0101010);
        RenderUtils.tile(matrixStack, frame.getX(), frame.getY(), 0, 0, frame.getWidth(), frame.getHeight(), 128, 128, 4, 4, 4, 4, RenderUtils.TEX_COMMON);
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

    private AWImageButton addIconButton(int x, int y, int u, int v, int width, int height, String key, Button.OnPress handler) {
        Component tooltip = getCommonDisplayText(key);
        AWImageButton button = new AWImageButton(x, y, width, height, u, v, RenderUtils.TEX_BUTTONS, handler, this::addHoveredButton, tooltip);
        addButton(button);
        return button;
    }

    private AWExtendedButton addTextButton(int x, int y, int width, int height, String key, Button.OnPress handler) {
        Component title = getDisplayText(key);
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

        String[] javaVersion = GlobalSkinLibraryUtils.getJavaVersion();
        boolean validJava = GlobalSkinLibraryUtils.isValidJavaVersion();

        if (!validJava) {
            Component urlWikiFaq = getURLText(URL_WIKI_FAQ);
            Component urlVideoUpdateJava = getURLText(URL_VIDEO_UPDATE_JAVA);
            String update = javaVersion.length > 2 ? javaVersion[2] : "0";
            this.pages.add(concat(getDisplayText("old_java", javaVersion[0], update, urlWikiFaq, urlVideoUpdateJava)));
            return;
        }

        this.pages.add(concat(getDisplayText("message_1.title"), "\n\n", getDisplayText("message_1.text")));
        this.pages.add(concat(getDisplayText("message_2.title"), "\n\n", getDisplayText("message_2.text")));
        this.pages.add(concat(getDisplayText("message_3.title"), "\n\n", getDisplayText("message_3.text")));
        this.pages.add(concat(getDisplayText("message_4.title"), "\n\n", getDisplayText("message_4.text")));
        this.pages.add(concat(getDisplayText("message_5.title"), "\n\n", getDisplayText("message_5.text"), getURLText(URL_DISCORD)));

        if (Strings.isNotBlank(joinFailMessage)) {
            TextComponent message = (TextComponent) pages.get(pages.size() - 1);
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

    private TextComponent concat(Object... keys) {
        TextComponent message = new TextComponent("");
        for (Object key : keys) {
            if (key instanceof String) {
                message.append((String) key);
            }
            if (key instanceof Component) {
                message.append((Component) key);
            }
        }
        return message;
    }
}
