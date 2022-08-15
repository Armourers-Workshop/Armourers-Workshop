package moe.plushie.armourers_workshop.core.client.gui.hologramprojector;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.core.blockentity.HologramProjectorBlockEntity;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWAbstractContainerScreen;
import moe.plushie.armourers_workshop.core.client.gui.widget.AWTabController;
import moe.plushie.armourers_workshop.core.menu.HologramProjectorMenu;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

@SuppressWarnings({"unused"})
@Environment(value = EnvType.CLIENT)
public class HologramProjectorScreen extends AWAbstractContainerScreen<HologramProjectorMenu> {

    private final AWTabController<Integer> tabController = new AWTabController<>(true);
    private final HologramProjectorBlockEntity tileEntity;

    protected int inventoryX;
    protected int inventoryY;

    public HologramProjectorScreen(HologramProjectorMenu container, Inventory inventory, Component title) {
        super(container, inventory, title);

        this.imageWidth = 176;
        this.imageHeight = 240;
        this.tileEntity = container.getTileEntity();

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

        tabController.add(new HologramProjectorInventorySetting(tileEntity))
                .setIcon(64, 0)
                .setIconAnimation(8, 150)
                .setTarget(1);

        tabController.add(new HologramProjectorOffsetSetting(tileEntity))
                .setIcon(96, 0)
                .setIconAnimation(8, 150);

        tabController.add(new HologramProjectorAngleSetting(tileEntity))
                .setIcon(176, 0)
                .setIconAnimation(8, 150);

        tabController.add(new HologramProjectorRotationOffsetSetting(tileEntity))
                .setIcon(80, 0)
                .setIconAnimation(8, 150);

        tabController.add(new HologramProjectorRotationSpeedSetting(tileEntity))
                .setIcon(160, 0)
                .setIconAnimation(4, 150);

        tabController.add(new HologramProjectorExtraSetting(tileEntity))
                .setIcon(144, 0)
                .setIconAnimation(8, 150);

        tabController.addListener(this::switchTab);
        tabController.setSelectedTab(tabController.getFirstActiveTab()); // active the first tab
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        tabController.render(matrixStack, mouseX, mouseY, partialTicks);
        if (menu.shouldRenderInventory()) {
            RenderUtils.blit(matrixStack, inventoryX, inventoryY, 0, 0, 176, 98, RenderUtils.TEX_PLAYER_INVENTORY);
        }
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        font.draw(matrixStack, getTitle(), titleLabelX, titleLabelY, 0x404040);
        if (menu.shouldRenderInventory()) {
            font.draw(matrixStack, inventory.getDisplayName(), inventoryX + 8, inventoryY + 5, 0x404040);
        }
    }

    @Override
    protected void renderTooltip(PoseStack matrixStack, int mouseX, int mouseY) {
        tabController.renderTooltip(matrixStack, mouseX, mouseY);
        super.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
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
    protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int k) {
        if (!menu.shouldRenderInventory()) {
            return true;
        }
        // click the inventory?
        int invLeft = left + inventoryX;
        int invTop = top + inventoryY;
        if (mouseX >= invLeft && mouseY >= invTop && mouseX <= (invLeft + 176) && mouseY <= (invTop + 98)) {
            return false;
        }
        return tabController.hasClickedOutside(mouseX, mouseY, left, top, k);
    }

    private void switchTab(AWTabController<Integer>.Tab tab) {
        menu.setGroup(tab.getTarget() != null ? tab.getTarget() : 0);
    }
}
