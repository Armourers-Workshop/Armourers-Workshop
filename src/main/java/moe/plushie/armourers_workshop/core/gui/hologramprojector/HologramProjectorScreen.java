package moe.plushie.armourers_workshop.core.gui.hologramprojector;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.container.HologramProjectorContainer;
import moe.plushie.armourers_workshop.core.gui.widget.AWTabController;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings({"unused", "NullableProblems"})
public class HologramProjectorScreen extends ContainerScreen<HologramProjectorContainer> {

    private final AWTabController<Integer> tabController = new AWTabController<>(true);

    protected int inventoryX;
    protected int inventoryY;

    public HologramProjectorScreen(HologramProjectorContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);

        this.imageWidth = 176;
        this.imageHeight = 240;

        this.initTabs();
    }

    @Override
    protected void init() {
        topPos = 0;
        leftPos = 0;

        inventoryX = (width - 176) / 2;
        inventoryY = (height - 98);

        titleLabelX = (width - font.width(getTitle().getVisualOrderText())) / 2;
        titleLabelY = 5;

        menu.reload(inventoryX, inventoryY, width, height);
        tabController.init(0, 0, width, height);
        addWidget(tabController);
    }

    @Override
    public void removed() {
        tabController.removed();
        super.removed();
    }

    protected void initTabs() {
        tabController.clear();

        tabController.add(new HologramProjectorInventorySetting(menu))
                .setIcon(64, 0)
                .setIconAnimation(8, 150)
                .setTarget(1);

        tabController.add(new HologramProjectorOffsetSetting(menu))
                .setIcon(96, 0)
                .setIconAnimation(8, 150);

        tabController.add(new HologramProjectorAngleSetting(menu))
                .setIcon(176, 0)
                .setIconAnimation(8, 150);

        tabController.add(new HologramProjectorRotationOffsetSetting(menu))
                .setIcon(80, 0)
                .setIconAnimation(8, 150);

        tabController.add(new HologramProjectorRotationSpeedSetting(menu))
                .setIcon(160, 0)
                .setIconAnimation(4, 150);

        tabController.add(new HologramProjectorExtraSetting(menu))
                .setIcon(144, 0)
                .setIconAnimation(8, 150);

        tabController.addListener(this::switchTab);
        tabController.setSelectedTab(tabController.get(0)); // active the first tab
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        tabController.render(matrixStack, mouseX, mouseY, partialTicks);
        if (menu.shouldRenderPlayerInventory()) {
            RenderUtils.blit(matrixStack, inventoryX, inventoryY, 0, 0, 176, 98, RenderUtils.TEX_PLAYER_INVENTORY);
        }
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        font.draw(matrixStack, getTitle(), titleLabelX, titleLabelY, 0x404040);
        if (menu.shouldRenderPlayerInventory()) {
            font.draw(matrixStack, inventory.getDisplayName(), inventoryX + 8, inventoryY + 5, 0x404040);
        }
    }

    @Override
    protected void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
        tabController.renderTooltip(matrixStack, mouseX, mouseY);
        super.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderTooltip(matrixStack, mouseX, mouseY);
    }

//    @Override
//    public boolean mouseClicked(double mouseX, double mouseY, int button) {
//        ColourSettingPanel.ColorPicker colorPicker = getActivatedPicker();
//        if (colorPicker != null) {
//            colorPicker.end();
//            return false;
//        }
//        if (button == 1) {
//            enabledPlayerRotating = true;
//        }
//        return super.mouseClicked(mouseX, mouseY, button);
//    }
//
//    @Override
//    public boolean mouseReleased(double mouseX, double mouseY, int button) {
//        if (button == 1) {
//            enabledPlayerRotating = false;
//        }
//        return super.mouseReleased(mouseX, mouseY, button);
//    }

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

//    @Override
//    protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
//        if (super.hasClickedOutside(mouseX, mouseY, left, top, button)) {
//            return tabController.get(mouseX, mouseY) == null;
//        }
//        return false;
//    }

    private void switchTab(AWTabController<Integer>.Tab tab) {
        menu.setGroup(tab.getTarget() != null ? tab.getTarget() : 0);
    }
}
