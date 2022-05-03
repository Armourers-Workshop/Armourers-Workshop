package moe.plushie.armourers_workshop.core.gui.wardrobe;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.core.gui.widget.AWAbstractContainerScreen;
import moe.plushie.armourers_workshop.init.common.ModConfig;
import moe.plushie.armourers_workshop.core.entity.MannequinEntity;
import moe.plushie.armourers_workshop.core.gui.widget.AWTabController;
import moe.plushie.armourers_workshop.core.render.entity.MannequinEntityRenderer;
import moe.plushie.armourers_workshop.init.common.ModContributors;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import moe.plushie.armourers_workshop.utils.slot.SkinSlotType;
import moe.plushie.armourers_workshop.core.capability.SkinWardrobe;
import moe.plushie.armourers_workshop.core.container.SkinWardrobeContainer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings({"unused", "NullableProblems"})
public class SkinWardrobeScreen extends AWAbstractContainerScreen<SkinWardrobeContainer> {

    private final Entity entity;
    private final PlayerEntity operator;
    private final SkinWardrobe wardrobe;
    private final AWTabController<SkinWardrobeContainer.Group> tabController = new AWTabController<>(false);
    private boolean enabledPlayerRotating = false;
    private float playerRotation = 45.0f;
    private int lastMouseX = 0;
    private int lastMouseY = 0;

    public SkinWardrobeScreen(SkinWardrobeContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);

        this.imageWidth = 278;
        this.imageHeight = 250;

        this.operator = inventory.player;
        this.operator.containerMenu = this.menu;

        this.entity = container.getEntity();
        this.wardrobe = container.getWardrobe();

        this.initTabs();
    }

    @Override
    protected void init() {
        super.init();

        titleLabelX = imageWidth / 2 - font.width(getTitle().getVisualOrderText()) / 2;
        titleLabelY = 8;
        inventoryLabelX = 51 + 8;
        inventoryLabelY = 152 + 5;

        tabController.init(leftPos, topPos, 278, 151);
        addWidget(tabController);
    }

    @Override
    public void removed() {
        tabController.removed();
        super.removed();
    }

    protected void initTabs() {
        boolean isPlayer = entity instanceof PlayerEntity;
        boolean isMannequin = entity instanceof MannequinEntity;

        tabController.clear();

        tabController.add(new SkinWardrobeInventorySetting(menu))
                .setIcon(192, 0)
                .setTarget(SkinWardrobeContainer.Group.SKINS)
                .setActive(!isPlayer || ModConfig.Common.showWardrobeSkins || operator.isCreative());

        if (wardrobe.getUnlockedSize(SkinSlotType.OUTFIT) != 0) {
            tabController.add(new SkinWardrobeOutfitSetting(menu))
                    .setIcon(0, 128)
                    .setTarget(SkinWardrobeContainer.Group.OUTFITS)
                    .setActive(!isPlayer || ModConfig.Common.showWardrobeOutfits || operator.isCreative());
        }

        tabController.add(new SkinWardrobeDisplaySetting(menu))
                .setIcon(208, 0)
                .setActive(!isPlayer || ModConfig.Common.showWardrobeDisplaySettings || operator.isCreative());

        tabController.add(new SkinWardrobeColourSetting(menu))
                .setIcon(224, 0)
                .setTarget(SkinWardrobeContainer.Group.COLORS)
                .setActive(!isPlayer || ModConfig.Common.showWardrobeColourSettings || operator.isCreative());

        tabController.add(new SkinWardrobeDyeSetting(menu))
                .setIcon(240, 0)
                .setTarget(SkinWardrobeContainer.Group.DYES)
                .setActive(!isPlayer || ModConfig.Common.showWardrobeDyeSetting || operator.isCreative());

        if (isPlayer && ModContributors.getCurrentContributor() != null) {
            tabController.add(new SkinWardrobeContributorSetting(menu))
                    .setIcon(32, 128)
                    .setActive(ModConfig.Common.showWardrobeContributorSetting || operator.isCreative());
        }

        if (isMannequin) {
            tabController.add(new SkinWardrobeRotationSetting(menu))
                    .setIcon(80, 0)
                    .setIconAnimation(8, 150)
                    .setAlignment(1);

            tabController.add(new SkinWardrobeTextureSetting(menu))
                    .setIcon(128, 0)
                    .setIconAnimation(8, 150)
                    .setAlignment(1);

            tabController.add(new SkinWardrobeExtraSetting(menu))
                    .setIcon(144, 0)
                    .setIconAnimation(8, 150)
                    .setAlignment(1);

            tabController.add(new SkinWardrobeLocationSetting(menu))
                    .setIcon(96, 0)
                    .setIconAnimation(8, 150)
                    .setAlignment(1);
        }

        tabController.addListener(this::switchTab);
        tabController.setSelectedTab(tabController.getFirstActiveTab()); // active the first tab
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderUtils.blit(matrixStack, leftPos, topPos, 0, 0, 256, 151, RenderUtils.TEX_WARDROBE_1);
        RenderUtils.blit(matrixStack, leftPos + 256, topPos, 0, 0, 22, 151, RenderUtils.TEX_WARDROBE_2);

        if (menu.shouldRenderPlayerInventory()) {
            RenderUtils.blit(matrixStack, leftPos + 51, topPos + 152, 0, 0, 176, 98, RenderUtils.TEX_PLAYER_INVENTORY);
        }

        tabController.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        font.draw(matrixStack, getTitle(), titleLabelX, titleLabelY, 0x404040);
        if (menu.shouldRenderPlayerInventory()) {
            font.draw(matrixStack, inventory.getDisplayName(), inventoryLabelX, inventoryLabelY, 0x404040);
        }
    }

    @Override
    protected void renderTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
        tabController.renderTooltip(matrixStack, mouseX, mouseY);
        super.renderTooltip(matrixStack, mouseX, mouseY);
    }

    public void renderPlayer(MatrixStack matrixStack, int mouseX, int mouseY, int x, int y, int width, int height) {
        boolean isFocus = (x <= mouseX && mouseX <= x + width) && (y <= mouseY && mouseY <= y + height);
        if (!isFocus) {
            RenderUtils.enableScissor(x, y, width, height);
            RenderSystem.translatef(0, 0, 100);
        } else {
            RenderSystem.translatef(0, 0, 300);
        }
        RenderSystem.pushMatrix();
        RenderSystem.translatef(x + (float) width / 2, y + height - 8, 50);
        RenderSystem.rotatef(-20, 1, 0, 0);
        RenderSystem.rotatef(playerRotation, 0, 1, 0);
        RenderSystem.translatef(0, 0, -50);
        MannequinEntityRenderer.enableLimitScale = true;

        if (entity instanceof LivingEntity) {
            InventoryScreen.renderEntityInInventory(0, 0, 45, 0, 0, (LivingEntity) entity);
        }

        MannequinEntityRenderer.enableLimitScale = false;
        RenderSystem.popMatrix();

        if (!isFocus) {
            RenderUtils.disableScissor();
        }
    }

    @Override
    public void renderContentLayer(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.renderSuperLayer(matrixStack, mouseX, mouseY, partialTicks);

        if (enabledPlayerRotating) {
            playerRotation = (playerRotation + (mouseX - lastMouseX) + 360) % 360;
        }
        lastMouseX = mouseX;
        lastMouseY = mouseY;

        renderPlayer(matrixStack, mouseX, mouseY, leftPos + 8, topPos + 27, 71, 111);
        renderTooltip(matrixStack, mouseX, mouseY);

        SkinWardrobeColourSetting.ColorPicker colorPicker = getActivatedPicker();
        if (colorPicker != null) {
            colorPicker.update(mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        SkinWardrobeColourSetting.ColorPicker colorPicker = getActivatedPicker();
        if (colorPicker != null) {
            colorPicker.end();
            return false;
        }
        if (button == 1) {
            enabledPlayerRotating = true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 1) {
            enabledPlayerRotating = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
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

    private void switchTab(AWTabController<SkinWardrobeContainer.Group>.Tab tab) {
        menu.setGroup(tab.getTarget());
    }

    private int getExtendedHeight() {
        AWTabController<SkinWardrobeContainer.Group>.Tab tab = tabController.getSelectedTab();
        if (tab != null && tab.getTarget() != null) {
            return tab.getTarget().getExtendedHeight();
        }
        return 0;
    }

    @Nullable
    private SkinWardrobeColourSetting.ColorPicker getActivatedPicker() {
        Screen screen = tabController.getSelectedScreen();
        if (screen instanceof SkinWardrobeColourSetting) {
            return ((SkinWardrobeColourSetting) screen).getActivatedPicker();
        }
        return null;
    }
}
