package moe.plushie.armourers_workshop.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import moe.plushie.armourers_workshop.client.setting.*;
import moe.plushie.armourers_workshop.core.config.SkinConfig;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import moe.plushie.armourers_workshop.core.utils.SkinCore;
import moe.plushie.armourers_workshop.core.wardrobe.SkinWardrobeContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkinWardrobeScreen extends ContainerScreen<SkinWardrobeContainer> {

    private final LivingEntity entity;
    private final PlayerEntity operator;

    private final TabController<SkinWardrobeContainer.Group> tabController = new TabController<>();

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
            if (tab.screen instanceof BaseSettingPanel) {
                BaseSettingPanel panel = (BaseSettingPanel) tab.screen;
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
                .setTarget(SkinWardrobeContainer.Group.SKINS)
                .setVisible(!isPlayer || SkinConfig.showWardrobeSkins || operator.isCreative());

        // exists outfit slot
        tabController.add(new OutfitSettingPanel(menu))
                .setIcon(0, 128)
                .setTarget(SkinWardrobeContainer.Group.OUTFITS)
                .setVisible(!isPlayer || SkinConfig.showWardrobeOutfits || operator.isCreative());

        tabController.add(new DisplaySettingPanel(entity))
                .setIcon(208, 0)
                .setVisible(isPlayer && (SkinConfig.showWardrobeDisplaySettings || operator.isCreative()));

        tabController.add(new ColourSettingPanel(menu.getWardrobe()))
                .setIcon(224, 0)
                .setVisible(!isPlayer || SkinConfig.showWardrobeColourSettings || operator.isCreative());

        tabController.add(new DyeSettingPanel(menu))
                .setIcon(240, 0)
                .setTarget(SkinWardrobeContainer.Group.DYES)
                .setVisible(!isPlayer || SkinConfig.showWardrobeDyeSetting || operator.isCreative());

//        tabController.add(new ContributorSettingPanel(entity))
//                .setTarget(6)
//                .setIcon(32, 128)
//                .setVisible(!isPlayer || SkinConfig.showWardrobeColourSettings || operator.isCreative());

        tabController.add(new RotationSettingPanel(entity))
                .setIcon(80, 0)
                .setIconAnimation(8, 150)
                .setAlignment(1)
                .setVisible(isMannequin);

        tabController.add(new TextureSettingPanel(entity))
                .setIcon(128, 0)
                .setIconAnimation(8, 150)
                .setAlignment(1)
                .setVisible(isMannequin);

        tabController.add(new ExtraSettingPanel(entity))
                .setIcon(144, 0)
                .setIconAnimation(8, 150)
                .setAlignment(1)
                .setVisible(isMannequin);

        tabController.add(new LocationSettingPanel(entity))
                .setIcon(96, 0)
                .setIconAnimation(8, 150)
                .setVisible(isMannequin);

        tabController.addListener(tab -> getMenu().setGroup(tab.getTarget()));
        tabController.setSelectedTab(tabController.get(0)); // active the first tab
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        bindTexture(SkinCore.TEX_WARDROBE_1);
        blit(matrixStack, leftPos, topPos, 0, 0, 256, 151);

        bindTexture(SkinCore.TEX_WARDROBE_2);
        blit(matrixStack, leftPos + 256, topPos, 0, 0, 22, 151);

        if (menu.isNeedsPlayerInventory()) {
            bindTexture(SkinCore.TEX_PLAYER_INVENTORY);
            blit(matrixStack, leftPos + 51, topPos + 152, 0, 0, 176, 98);
        }

        tabController.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        font.draw(matrixStack, getTitle(), titleLabelX, titleLabelY, 0x404040);
        if (menu.isNeedsPlayerInventory()) {
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

        InventoryScreen.renderEntityInInventory(0, 0, 45, 0, 0, entity);

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
            colorPicker.pick(mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        ColourSettingPanel.ColorPicker colorPicker = getActivatedPicker();
        if (colorPicker != null) {
            colorPicker.endPick();
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

    private void bindTexture(ResourceLocation location) {
        Minecraft.getInstance().getTextureManager().bind(location);
    }
}
