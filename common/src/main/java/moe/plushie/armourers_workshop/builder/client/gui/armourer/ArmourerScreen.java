package moe.plushie.armourers_workshop.builder.client.gui.armourer;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.builder.blockentity.ArmourerBlockEntity;
import moe.plushie.armourers_workshop.builder.menu.ArmourerMenu;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWAbstractContainerScreen;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWTabController;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

@Environment(value = EnvType.CLIENT)
public class ArmourerScreen extends AWAbstractContainerScreen<ArmourerMenu> {

    private final ArmourerBlockEntity tileEntity;
    private final AWTabController<ArmourerMenu.Group> tabController = new AWTabController<>(false);

    private int lastVersion = 0;

    public ArmourerScreen(ArmourerMenu container, Inventory inventory, Component title) {
        super(container, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 224;
        this.tileEntity = container.getTileEntity();

        this.initTabs();
    }

    @Override
    protected void init() {
        super.init();

        titleLabelX = imageWidth / 2 - font.width(getTitle().getVisualOrderText()) / 2;
        titleLabelY = 6;
        inventoryLabelX = 8;
        inventoryLabelY = imageHeight - 96;

        tabController.init(leftPos, topPos, 176, 224);
        addWidget(tabController);

        lastVersion = tileEntity.getVersion();
    }

    @Override
    public void removed() {
        tabController.removed();
        super.removed();
    }

    @Override
    public void tick() {
        super.tick();
        int lastVersion = tileEntity.getVersion();
        if (this.lastVersion != lastVersion) {
            tabController.getActiveTabs().forEach(tab -> {
                Screen screen = tab.getScreen();
                if (screen instanceof ArmourerBaseSetting) {
                    ((ArmourerBaseSetting) screen).reloadData();
                }
            });
            this.lastVersion = lastVersion;
        }
    }

    protected void initTabs() {
        tabController.clear();

        tabController.add(new ArmourerMainSetting(menu))
                .setTarget(ArmourerMenu.Group.MAIN)
                .setIcon(0, 0)
                .setIconAnimation(8, 150);

        tabController.add(new ArmourerDisplaySetting(menu))
                .setTarget(ArmourerMenu.Group.DISPLAY)
                .setIcon(16, 0)
                .setIconAnimation(8, 150);

        tabController.add(new ArmourerSkinSetting(menu))
                .setTarget(ArmourerMenu.Group.SKIN)
                .setIcon(32, 0)
                .setIconAnimation(8, 150);

        tabController.add(new ArmourerBlockSetting(menu, this))
                .setTarget(ArmourerMenu.Group.BLOCK)
                .setIcon(48, 0)
                .setIconAnimation(8, 150);

        tabController.addListener(this::switchTab);
        tabController.setSelectedTab(tabController.getFirstActiveTab()); // active the first tab
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        font.draw(matrixStack, getTitle(), titleLabelX, titleLabelY, 0x404040);
        if (menu.shouldRenderInventory()) {
            font.draw(matrixStack, inventory.getDisplayName(), inventoryLabelX, inventoryLabelY, 0x404040);
        }
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(matrixStack);
        RenderUtils.blit(matrixStack, leftPos, topPos, 0, 0, 176, 224, RenderUtils.TEX_ARMOURER);

        if (!menu.shouldRenderInventory()) {
            RenderUtils.blit(matrixStack, leftPos + 7, topPos + 141, 7, 3, 162, 76, RenderUtils.TEX_ARMOURER);
        }

        tabController.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderTooltip(PoseStack matrixStack, int mouseX, int mouseY) {
        tabController.renderTooltip(matrixStack, mouseX, mouseY);
        super.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double p_231045_6_, double p_231045_8_) {
        if (tabController.mouseDragged(mouseX, mouseY, button, p_231045_6_, p_231045_8_)) {
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, p_231045_6_, p_231045_8_);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (tabController.isDragging()) {
            return true;
        }
        return super.isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean changeFocus(boolean p_231049_1_) {
        return tabController.changeFocus(p_231049_1_);
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
        if (super.hasClickedOutside(mouseX, mouseY, left, top, button)) {
            if (!inventory.getCarried().isEmpty()) {
                return true;
            }
            return tabController.get(mouseX, mouseY) == null;
        }
        return false;
    }

    private void switchTab(AWTabController<ArmourerMenu.Group>.Tab tab) {
        if (tab.getScreen() instanceof ArmourerBaseSetting) {
            ((ArmourerBaseSetting) tab.getScreen()).reloadData();
        }
        menu.setGroup(tab.getTarget());
    }
}
