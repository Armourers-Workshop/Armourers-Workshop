package moe.plushie.armourers_workshop.library.client.gui.panels;

import com.google.common.util.concurrent.FutureCallback;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWLabel;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.library.client.gui.GlobalSkinLibraryScreen;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;

@SuppressWarnings("NullableProblems")
@Environment(value = EnvType.CLIENT)
public class InfoLibraryPanel extends AbstractLibraryPanel {

    private static final String URL_DISCORD = "https://discord.gg/5Z3KKvU";
    private static final String URL_GITHUB = "https://github.com/RiskyKen/Armourers-Workshop";
    private static final String URL_REDDIT = "https://www.reddit.com/r/ArmourersWorkshop/";
    private static final String URL_DONATION = "https://ko-fi.com/riskyken";

    private TextComponent message = new TextComponent("");
    private GlobalTaskInfo.TaskData stats = null;
    private String failMessage = null;
    private AWLabel label;

    public InfoLibraryPanel() {
        super("inventory.armourers_workshop.skin-library-global.panel.info", GlobalSkinLibraryScreen.Page.LIBRARY_INFO::equals);
    }

    @Override
    protected void init() {
        super.init();
        this.label = addLabel(leftPos + 5, topPos + 5, width - 10, height - 10, message);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            this.updateInfo();
        }
    }

    public void updateInfo() {
        stats = null;
        failMessage = null;
        new GlobalTaskInfo().createTaskAndRun(new FutureCallback<GlobalTaskInfo.TaskData>() {

            @Override
            public void onSuccess(GlobalTaskInfo.TaskData result) {
                Minecraft.getInstance().execute(() -> {
                    stats = result;
                    reloadUI();
                });
            }

            @Override
            public void onFailure(Throwable t) {
                Minecraft.getInstance().execute(() -> {
                    failMessage = t.getMessage();
                    reloadUI();
                });
            }
        });
        this.reloadUI();
    }

    public void reloadUI() {
        message = new TextComponent("\n\n\n");

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

        label.setMessage(message);
    }

    @Override
    public boolean handleComponentClicked(@Nullable Style style) {
        if (style == null) {
            return false;
        }
        ClickEvent clickevent = style.getClickEvent();
        if (clickevent == null) {
            return false;
        }
        if (clickevent.getAction() == ClickEvent.Action.OPEN_URL) {
            try {
                URI uri = new URI(clickevent.getValue());
                String s = uri.getScheme();
                if (s == null) {
                    throw new URISyntaxException(clickevent.getValue(), "Missing protocol");
                }
                Util.getPlatform().openUri(uri);
                return true;
            } catch (URISyntaxException urisyntaxexception) {
                ModLog.error("Can't open url for {}", clickevent, urisyntaxexception);
            }
        }
        return false;
    }

    @Override
    public void renderBackgroundLayer(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.fillGradient(matrixStack, leftPos, topPos, leftPos + width, topPos + height, 0xC0101010, 0xD0101010);
    }
}
