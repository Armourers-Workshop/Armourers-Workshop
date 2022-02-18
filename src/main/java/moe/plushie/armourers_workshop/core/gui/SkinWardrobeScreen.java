package moe.plushie.armourers_workshop.core.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.core.AWConfig;
import moe.plushie.armourers_workshop.core.gui.wardrobe.*;
import moe.plushie.armourers_workshop.core.gui.widget.TabController;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeContainer;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeGroup;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class SkinWardrobeScreen extends ContainerScreen<SkinWardrobeContainer> {

    private final Entity entity;
    private final PlayerEntity operator;

    private final TabController<SkinWardrobeGroup> tabController = new TabController<>();

    public SkinWardrobeScreen(SkinWardrobeContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title);

        this.imageWidth = 278;
        this.imageHeight = 250;

        this.operator = inventory.player;
        this.operator.containerMenu = this.menu;

        this.entity = container.getEntity();

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

        tabController.getActiveTabs().forEach(tab -> {
            if (tab.getScreen() instanceof BaseSettingPanel) {
                BaseSettingPanel panel = (BaseSettingPanel) tab.getScreen();
                panel.leftPos = leftPos;
                panel.topPos = topPos;
            }
        });
    }

    @Override
    public void removed() {
        tabController.removed();
        super.removed();
    }

    protected void initTabs() {
        boolean isPlayer = entity instanceof PlayerEntity;
        boolean isMannequin = true;// entity instanceof MannequinEntity;

        tabController.clear();

        tabController.add(new SkinSettingPanel(menu))
                .setIcon(192, 0)
                .setTarget(SkinWardrobeGroup.SKINS)
                .setVisible(!isPlayer || AWConfig.showWardrobeSkins || operator.isCreative());

        // exists outfit slot
        tabController.add(new OutfitSettingPanel(menu))
                .setIcon(0, 128)
                .setTarget(SkinWardrobeGroup.OUTFITS)
                .setVisible(!isPlayer || AWConfig.showWardrobeOutfits || operator.isCreative());

        tabController.add(new DisplaySettingPanel(menu))
                .setIcon(208, 0)
                .setVisible(isPlayer && (AWConfig.showWardrobeDisplaySettings || operator.isCreative()));

        tabController.add(new ColourSettingPanel(menu))
                .setIcon(224, 0)
                .setVisible(!isPlayer || AWConfig.showWardrobeColourSettings || operator.isCreative());

        tabController.add(new DyeSettingPanel(menu))
                .setIcon(240, 0)
                .setTarget(SkinWardrobeGroup.DYES)
                .setVisible(!isPlayer || AWConfig.showWardrobeDyeSetting || operator.isCreative());

//        tabController.add(new ContributorSettingPanel(menu))
//                .setTarget(6)
//                .setIcon(32, 128)
//                .setVisible(!isPlayer || SkinConfig.showWardrobeColourSettings || operator.isCreative());

        tabController.add(new RotationSettingPanel(menu))
                .setIcon(80, 0)
                .setIconAnimation(8, 150)
                .setAlignment(1)
                .setVisible(isMannequin);

        tabController.add(new TextureSettingPanel(menu))
                .setIcon(128, 0)
                .setIconAnimation(8, 150)
                .setAlignment(1)
                .setVisible(isMannequin);

        tabController.add(new ExtraSettingPanel(menu))
                .setIcon(144, 0)
                .setIconAnimation(8, 150)
                .setAlignment(1)
                .setVisible(isMannequin);

        tabController.add(new LocationSettingPanel(menu))
                .setIcon(96, 0)
                .setIconAnimation(8, 150)
                .setVisible(isMannequin);

        tabController.addListener(tab -> getMenu().setGroup(tab.getTarget()));
        tabController.setSelectedTab(tabController.get(0)); // active the first tab
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

    protected void renderPlayer(MatrixStack matrixStack, int mouseX, int mouseY, int x, int y, int width, int height) {
        boolean isFocus = (x <= mouseX && mouseX <= x + width) && (y <= mouseY && mouseY <= y + height);

        if (!isFocus) {
            RenderUtils.enableScissor(x, y, width, height);
        } else {
            RenderSystem.translatef(0, 0, 300);
        }

        RenderSystem.pushMatrix();
        RenderSystem.translatef(x + (float) width / 2, y + height - 6, 50);
        RenderSystem.rotatef(-30, 0, 1, 0);
        RenderSystem.rotatef(45, 0, 1, 0);
        RenderSystem.translatef(0, 0, -50);

        if (entity instanceof LivingEntity) {
            InventoryScreen.renderEntityInInventory(0, 0, 45, 0, 0, (LivingEntity) entity);
        }

        RenderSystem.popMatrix();

        if (!isFocus) {
            RenderUtils.disableScissor();
        } else {
            RenderSystem.translatef(0, 0, -300);
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        renderPlayer(matrixStack, mouseX, mouseY, leftPos + 8, topPos + 27, 71, 111);
        renderTooltip(matrixStack, mouseX, mouseY);

        ColourSettingPanel.ColorPicker colorPicker = getActivatedPicker();
        if (colorPicker != null) {
            colorPicker.update(mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        ColourSettingPanel.ColorPicker colorPicker = getActivatedPicker();
        if (colorPicker != null) {
            colorPicker.end();
            return false;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
        if (super.hasClickedOutside(mouseX, mouseY, left, top, button)) {
            return tabController.get(mouseX, mouseY) == null;
        }
        return false;
    }

    private ColourSettingPanel.ColorPicker getActivatedPicker() {
        Screen screen = tabController.getSelectedScreen();
        if (screen instanceof ColourSettingPanel) {
            return ((ColourSettingPanel) screen).getActivatedPicker();
        }
        return null;
    }
}