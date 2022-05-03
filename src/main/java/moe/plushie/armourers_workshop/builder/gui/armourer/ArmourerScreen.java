package moe.plushie.armourers_workshop.builder.gui.armourer;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.builder.container.ArmourerContainer;
import moe.plushie.armourers_workshop.core.gui.widget.AWAbstractContainerScreen;
import moe.plushie.armourers_workshop.core.gui.widget.AWTabController;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ArmourerScreen extends AWAbstractContainerScreen<ArmourerContainer> {

    private final AWTabController<ArmourerContainer.Group> tabController = new AWTabController<>(false);

    public ArmourerScreen(ArmourerContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 224;

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
    }

    @Override
    public void removed() {
        tabController.removed();
        super.removed();
    }

    protected void initTabs() {
        tabController.clear();

        tabController.add(new ArmourerMainSetting(menu))
                .setTarget(ArmourerContainer.Group.MAIN)
                .setIcon(0, 0)
                .setIconAnimation(8, 150);

        tabController.add(new ArmourerDisplaySetting(menu))
                .setTarget(ArmourerContainer.Group.DISPLAY)
                .setIcon(16, 0)
                .setIconAnimation(8, 150);

        tabController.add(new ArmourerSkinSetting(menu))
                .setTarget(ArmourerContainer.Group.SKIN)
                .setIcon(32, 0)
                .setIconAnimation(8, 150);

        tabController.add(new ArmourerBlockSetting(menu))
                .setTarget(ArmourerContainer.Group.BLOCK)
                .setIcon(48, 0)
                .setIconAnimation(8, 150);

        tabController.addListener(this::switchTab);
        tabController.setSelectedTab(tabController.getFirstActiveTab()); // active the first tab
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        font.draw(matrixStack, getTitle(), titleLabelX, titleLabelY, 0x404040);
        if (menu.shouldRenderPlayerInventory()) {
            font.draw(matrixStack, inventory.getDisplayName(), inventoryLabelX, inventoryLabelY, 0x404040);
        }
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(matrixStack);
        RenderUtils.blit(matrixStack, leftPos, topPos, 0, 0, 176, 224, RenderUtils.TEX_ARMOURER);

        if (!menu.shouldRenderPlayerInventory()) {
            RenderUtils.blit(matrixStack, leftPos + 7, topPos + 141, 7, 3, 162, 76, RenderUtils.TEX_ARMOURER);
        }

        tabController.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
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
            return tabController.get(mouseX, mouseY) == null;
        }
        return false;
    }

    private void switchTab(AWTabController<ArmourerContainer.Group>.Tab tab) {
        if (tab.getScreen() instanceof ArmourerBaseSetting) {
            ((ArmourerBaseSetting) tab.getScreen()).reloadData();
        }
        menu.setGroup(tab.getTarget());
    }
}
