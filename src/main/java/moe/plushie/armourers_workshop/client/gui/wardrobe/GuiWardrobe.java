package moe.plushie.armourers_workshop.client.gui.wardrobe;

import java.awt.Color;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.capability.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.api.common.capability.IWardrobeCap;
import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTab;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTabPanel;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTabbed;
import moe.plushie.armourers_workshop.client.gui.style.GuiResourceManager;
import moe.plushie.armourers_workshop.client.gui.style.GuiStyle;
import moe.plushie.armourers_workshop.client.gui.wardrobe.tab.GuiTabWardrobeColourSettings;
import moe.plushie.armourers_workshop.client.gui.wardrobe.tab.GuiTabWardrobeContributor;
import moe.plushie.armourers_workshop.client.gui.wardrobe.tab.GuiTabWardrobeDisplaySettings;
import moe.plushie.armourers_workshop.client.gui.wardrobe.tab.GuiTabWardrobeDyes;
import moe.plushie.armourers_workshop.client.gui.wardrobe.tab.GuiTabWardrobeManExtras;
import moe.plushie.armourers_workshop.client.gui.wardrobe.tab.GuiTabWardrobeManOffset;
import moe.plushie.armourers_workshop.client.gui.wardrobe.tab.GuiTabWardrobeManRotations;
import moe.plushie.armourers_workshop.client.gui.wardrobe.tab.GuiTabWardrobeManTextureData;
import moe.plushie.armourers_workshop.client.gui.wardrobe.tab.GuiTabWardrobeOutfits;
import moe.plushie.armourers_workshop.client.gui.wardrobe.tab.GuiTabWardrobeSkins;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.client.render.ModRenderHelper;
import moe.plushie.armourers_workshop.common.Contributors;
import moe.plushie.armourers_workshop.common.Contributors.Contributor;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.init.entities.EntityMannequin;
import moe.plushie.armourers_workshop.common.inventory.ContainerSkinWardrobe;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotHidable;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiWardrobe extends GuiTabbed<ContainerSkinWardrobe> {

    private static final ResourceLocation TEXTURE_1 = new ResourceLocation(LibGuiResources.GUI_WARDROBE_1);
    private static final ResourceLocation TEXTURE_2 = new ResourceLocation(LibGuiResources.GUI_WARDROBE_2);
    private static final ResourceLocation GUI_JSON = new ResourceLocation(LibGuiResources.JSON_WARDROBE);

    private static final String GUI_NAME = "wardrobe";
    private final GuiStyle guiStyle;

    private static int activeTab;

    private final GuiTabWardrobeSkins tabSkins;
    private final GuiTabWardrobeOutfits tabOutfits;
    private final GuiTabWardrobeDisplaySettings tabDisplaySetting;
    private final GuiTabWardrobeColourSettings tabColourSettings;
    private final GuiTabWardrobeDyes tabDyes;
    private final GuiTabWardrobeContributor tabContributor;
    
    private final GuiTabWardrobeManTextureData tabManTextureData;
    private final GuiTabWardrobeManRotations tabManRotations;
    private final GuiTabWardrobeManExtras tabManExtras;
    private final GuiTabWardrobeManOffset tabManOffset;

    private EntitySkinCapability skinCapability;
    private EntityPlayer player;

    private boolean rotatingPlayer = false;
    private float playerRotation = 45F;
    private final boolean isPlayer;
    private final boolean isMannequin;

    private int lastMouseX;
    private int lastMouseY;

    public GuiWardrobe(InventoryPlayer inventory, EntitySkinCapability skinCapability, IWardrobeCap wardrobeCapability) {
        super(new ContainerSkinWardrobe(inventory, skinCapability, wardrobeCapability), false, TEXTURE_TAB_ICONS);

        this.guiStyle = GuiResourceManager.getGuiJsonInfo(GUI_JSON);

        // Tab size 21
        this.xSize = 278;
        this.ySize = 250;

        this.player = inventory.player;
        this.skinCapability = skinCapability;
        isPlayer = wardrobeCapability instanceof IPlayerWardrobeCap;
        isMannequin = skinCapability.getEntity() instanceof EntityMannequin;
        boolean isCreative = player.capabilities.isCreativeMode;
        tabController.setTabsPerSide(5);
        
        tabSkins = new GuiTabWardrobeSkins(tabList.size(), this);
        tabList.add(tabSkins);
        GuiTab tabListItemSkins = new GuiTab(tabController, GuiHelper.getLocalizedControlName(GUI_NAME, "tab.skins"));
        tabListItemSkins.setIconLocation(192, 0);
        tabListItemSkins.setTabTextureSize(26, 30);
        tabListItemSkins.setPadding(0, 4, 3, 3);
        tabListItemSkins.setVisable(!isPlayer | (isPlayer & (ConfigHandler.wardrobeTabSkins | isCreative)));
        tabController.addTab(tabListItemSkins);
        
        tabOutfits = new GuiTabWardrobeOutfits(tabList.size(), this, player, skinCapability, wardrobeCapability);
        if (skinCapability.getSlotCountForSkinType(SkinTypeRegistry.skinOutfit)  > 0) {
            tabList.add(tabOutfits);
            GuiTab tabListItemOutfits = new GuiTab(tabController, GuiHelper.getLocalizedControlName(GUI_NAME, "tab.outfits"));
            tabListItemOutfits.setIconLocation(0, 128);
            tabListItemOutfits.setTabTextureSize(26, 30);
            tabListItemOutfits.setPadding(0, 4, 3, 3);
            tabListItemOutfits.setVisable(!isPlayer | (isPlayer & (ConfigHandler.wardrobeTabOutfits | isCreative)));
            tabController.addTab(tabListItemOutfits);
        }
        
        if (isPlayer) {
            tabDisplaySetting = new GuiTabWardrobeDisplaySettings(tabList.size(), this, player, skinCapability, (IPlayerWardrobeCap) wardrobeCapability);
            tabList.add(tabDisplaySetting);
            tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalizedControlName(GUI_NAME, "tab.displaySettings"))
                    .setIconLocation(208, 0)
                    .setTabTextureSize(26, 30)
                    .setPadding(0, 4, 3, 3)
                    .setVisable(!isPlayer | (isPlayer & (ConfigHandler.wardrobeTabDisplaySettings | isCreative))));
        } else {
            tabDisplaySetting = null;
        }
        
        tabColourSettings = new GuiTabWardrobeColourSettings(tabList.size(), this, player, skinCapability, wardrobeCapability);
        tabList.add(tabColourSettings);
        tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalizedControlName(GUI_NAME, "tab.colourSettings"))
                .setIconLocation(224, 0)
                .setTabTextureSize(26, 30)
                .setPadding(0, 4, 3, 3)
                .setVisable(!isPlayer | (isPlayer & (ConfigHandler.wardrobeTabColourSettings | isCreative))));
        
        tabDyes = new GuiTabWardrobeDyes(tabList.size(), this, player, skinCapability, wardrobeCapability);
        tabList.add(tabDyes);
        tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalizedControlName(GUI_NAME, "tab.dyes"))
                .setIconLocation(240, 0)
                .setTabTextureSize(26, 30)
                .setPadding(0, 4, 3, 3)
                .setVisable(!isPlayer | (isPlayer & (ConfigHandler.wardrobeTabDyes | isCreative))));
        
        Contributor contributor = Contributors.INSTANCE.getContributor(player.getGameProfile());
        tabContributor = new GuiTabWardrobeContributor(tabList.size(), this);
        tabList.add(tabContributor);
        tabController.addTab(new GuiTab(tabController, GuiHelper.getLocalizedControlName(GUI_NAME, "tab.contributor"))
                .setIconLocation(32, 128)
                .setTabTextureSize(26, 30)
                .setPadding(0, 4, 3, 3)
                .setVisable(isPlayer & contributor != null));

        if (isMannequin) {
            EntityMannequin entityMannequin = (EntityMannequin) skinCapability.getEntity();

            tabController.setTabsPerSide(4);
            
            tabManRotations = new GuiTabWardrobeManRotations(tabList.size(), this, entityMannequin);
            tabList.add(tabManRotations);
            GuiTab tabListItemManRotations = new GuiTab(tabController, GuiHelper.getLocalizedControlName(GUI_NAME, "tab.man_rotations"));
            tabListItemManRotations.setIconLocation(80, 0);
            tabListItemManRotations.setAnimation(8, 150);
            tabListItemManRotations.setVisable(isMannequin);
            tabListItemManRotations.setPadding(0, 0, 3, 3);
            tabController.addTab(tabListItemManRotations);

            tabManTextureData = new GuiTabWardrobeManTextureData(tabList.size(), this, entityMannequin);
            tabList.add(tabManTextureData);
            GuiTab tabListItemManTextureData = new GuiTab(tabController, GuiHelper.getLocalizedControlName(GUI_NAME, "tab.man_texture"));
            tabListItemManTextureData.setIconLocation(128, 0);
            tabListItemManTextureData.setAnimation(8, 150);
            tabListItemManTextureData.setVisable(isMannequin);
            tabListItemManTextureData.setPadding(0, 0, 3, 3);
            tabController.addTab(tabListItemManTextureData);

            tabManExtras = new GuiTabWardrobeManExtras(tabList.size(), this, entityMannequin);
            tabList.add(tabManExtras);
            GuiTab tabListItemManExtras = new GuiTab(tabController, GuiHelper.getLocalizedControlName(GUI_NAME, "tab.man_extras"));
            tabListItemManExtras.setIconLocation(144, 0);
            tabListItemManExtras.setAnimation(8, 150);
            tabListItemManExtras.setVisable(isMannequin);
            tabListItemManExtras.setPadding(0, 0, 3, 3);
            tabController.addTab(tabListItemManExtras);

            tabManOffset = new GuiTabWardrobeManOffset(tabList.size(), this, entityMannequin);
            tabList.add(tabManOffset);
            GuiTab tabListItemManOffset = new GuiTab(tabController, GuiHelper.getLocalizedControlName(GUI_NAME, "tab.man_offsets"));
            tabListItemManOffset.setIconLocation(96, 0);
            tabListItemManOffset.setAnimation(8, 150);
            tabListItemManOffset.setVisable(isMannequin);
            tabListItemManOffset.setPadding(0, 0, 3, 3);
            tabController.addTab(tabListItemManOffset);
        } else {
            tabManRotations = null;
            tabManTextureData = null;
            tabManExtras = null;
            tabManOffset = null;
        }

        tabController.setActiveTabIndex(getActiveTab());

        tabChanged();
    }
    
    @Override
    public void initGui() {
        super.initGui();
        tabController.initGui(getGuiLeft() - 17, guiTop, xSize + 42, ySize);
    }
    
    @Override
    public void updateScreen() {
        super.updateScreen();
        for (int i = 0; i < tabList.size(); i++) {
            GuiTabPanel tab = tabList.get(i);
            if (tab.getTabId() == getActiveTab()) {
                tab.update();
            }
        }
    }

    @Override
    protected int getActiveTab() {
        return activeTab;
    }

    @Override
    protected void setActiveTab(int value) {
        activeTab = value;
    }

    @Override
    public ContainerSkinWardrobe getContainer() {
        return (ContainerSkinWardrobe) inventorySlots;
    }

    private void setSlotVisibilityPlayerInv(boolean visible) {
        setSlotVisibility(getContainer().getPlayerInvStartIndex(), getContainer().getPlayerInvEndIndex(), visible);
    }
    
    private void setSlotVisibilitySkins(boolean visible) {
        setSlotVisibility(getContainer().getIndexSkinsStart(), getContainer().getIndexSkinsEnd(), visible);
    }
    
    private void setSlotVisibilityMannequinHands(boolean visible) {
        setSlotVisibility(getContainer().getIndexMannequinHandsStart(), getContainer().getIndexMannequinHandsEnd(), visible);
    }

    private void setSlotVisibilityDyes(boolean visible) {
        setSlotVisibility(getContainer().getIndexDyeStart(), getContainer().getIndexDyeEnd(), visible);
    }

    private void setSlotVisibilityOutfits(boolean visible) {
        setSlotVisibility(getContainer().getIndexOutfitStart(), getContainer().getIndexOutfitEnd(), visible);
    }

    private void setSlotVisibility(int start, int end, boolean visible) {
        for (int i = start; i < end; i++) {
            Object slot = inventorySlots.inventorySlots.get(i);
            if (slot != null && slot instanceof SlotHidable) {
                ((SlotHidable) slot).setVisible(visible);
            }
        }
    }
    
    private boolean tabNeedsPlayerSlots(int tabId) {
        return tabId == tabSkins.getTabId() | tabId == tabOutfits.getTabId() | tabId == tabDyes.getTabId();
    }

    @Override
    protected void tabChanged() {
        super.tabChanged();
        setSlotVisibilityPlayerInv(tabNeedsPlayerSlots(getActiveTab()));
        setSlotVisibilitySkins(getActiveTab() == tabSkins.getTabId());
        setSlotVisibilityMannequinHands(getActiveTab() == tabSkins.getTabId());
        setSlotVisibilityDyes(getActiveTab() == tabDyes.getTabId());
        setSlotVisibilityOutfits(getActiveTab() == tabOutfits.getTabId());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.enableBlend();
        mc.renderEngine.bindTexture(TEXTURE_1);
        this.drawTexturedModalRect(getGuiLeft(), getGuiTop(), 0, 0, 256, 151);
        mc.renderEngine.bindTexture(TEXTURE_2);
        this.drawTexturedModalRect(getGuiLeft() + 256, getGuiTop(), 0, 0, 22, 151);
        
        for (int i = 0; i < tabList.size(); i++) {
            GuiTabPanel tab = tabList.get(i);
            if (tab.getTabId() == getActiveTab()) {
                tab.drawBackgroundLayer(partialTickTime, mouseX, mouseY);
            }
        }
        
        if (tabNeedsPlayerSlots(getActiveTab())) {
            GuiHelper.renderPlayerInvTexture(getGuiLeft() + 51, getGuiTop() + 152);
        }
        
        if (rotatingPlayer) {
            playerRotation += mouseX - lastMouseX;
            if (playerRotation < 0F) {
                playerRotation += 360F;
            }
            if (playerRotation > 360F) {
                playerRotation -= 360F;
            }
        }
        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        // Title label.
        GuiHelper.renderLocalizedGuiName(fontRenderer, this.xSize, GUI_NAME, guiStyle.getColour("text"));
        for (int i = 0; i < tabList.size(); i++) {
            GuiTabPanel tab = tabList.get(i);
            if (tab.getTabId() == getActiveTab()) {
                tab.drawForegroundLayer(mouseX, mouseY, 0);
            }
        }
        if (tabNeedsPlayerSlots(getActiveTab())) {
            GuiHelper.renderPlayerInvlabel(51, 152, fontRenderer, guiStyle.getColour("text"));
        }
        GL11.glPushMatrix();
        GL11.glTranslatef(-guiLeft, -guiTop, 0F);
        tabController.drawHoverText(mc, mouseX, mouseY);
        GL11.glPopMatrix();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        if (button == 1) {
            rotatingPlayer = true;
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (state == 1) {
            rotatingPlayer = false;
        }
    }

    public void drawPlayerPreview(int x, int y, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawPlayerPreview(x, y, mouseX, mouseY, false);
    }

    public Color drawPlayerPreview(int x, int y, int mouseX, int mouseY, boolean selectingColour) {
        Color colour = new Color(255, 255, 255);
        // 3D player preview
        float boxX = x + 42.5F;
        float boxY = y + 125F;
        float lookX = boxX - mouseX;
        float lookY = boxY - 50 - mouseY;

        /*
         * drawGradientRect( x + 8, y + 27, x + 8 + 71, y + 27 + 111, 0x88FFFFFF,
         * 0x88FFFFDD);
         */
        boolean overPlayerBox = false;
        if (mouseX >= x + 8 & mouseX < x + 8 + 71) {
            if (mouseY >= y + 27 & mouseY < y + 27 + 111) {
                overPlayerBox = true;
            }
        }



        // RenderHelper.enableStandardItemLighting();

        if (!overPlayerBox) {
            ModRenderHelper.enableScissor(x + 8, y + 27, 71, 111, true);
        }
        
        if (skinCapability.getEntity() instanceof EntityLivingBase) {

            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();

            GL11.glTranslatef(boxX, boxY, 50);
            GL11.glRotatef(-20, 1, 0, 0);
            GL11.glRotatef(playerRotation, 0, 1, 0);
            GL11.glTranslatef(0, 0, -50);
            if (selectingColour) {
                renderEntityWithoutLighting(0, 0, 45, 0, 0, (EntityLivingBase) skinCapability.getEntity());
                colour = getColourAtPos(Mouse.getX(), Mouse.getY());
            }
            GuiInventory.drawEntityOnScreen(0, 0, 45, 0, 0, (EntityLivingBase) skinCapability.getEntity());

            GlStateManager.popAttrib();
            GlStateManager.popMatrix();

            GlStateManager.enableBlend();


        } else {
            GlStateManager.pushAttrib();
            GlStateManager.enableColorMaterial();
            GlStateManager.pushMatrix();
            GL11.glTranslatef(boxX, boxY, 50);
            GL11.glRotatef(-20, 1, 0, 0);
            GL11.glRotatef(playerRotation, 0, 1, 0);
            if (isMannequin) {
                EntityMannequin mannequin = (EntityMannequin) skinCapability.getEntity();
                float rot = mannequin.getRotation();
                GL11.glRotatef(rot, 0, 1, 0);
                float manScale = mannequin.getScale();
                GL11.glScalef(1F / manScale, 1F / manScale, 1F / manScale);
            }
            GL11.glTranslatef(0, 0, -50);
            
            GlStateManager.translate(0, 0, 50.0F);
            GlStateManager.scale((-45), 45, 45);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
            rendermanager.setPlayerViewY(180.0F);
            rendermanager.setRenderShadow(false);
            rendermanager.renderEntity(skinCapability.getEntity(), 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
            rendermanager.setRenderShadow(true);
            GlStateManager.popMatrix();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.disableTexture2D();
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.popAttrib();
            
            GlStateManager.enableBlend();
            if (selectingColour) {
                colour = getColourAtPos(Mouse.getX(), Mouse.getY());
            }
        }
        if (!overPlayerBox) {
            ModRenderHelper.disableScissor();
        }

        return colour;
    }

    private void renderEntityWithoutLighting(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(posX, posY, 50.0F);
        GlStateManager.scale((-scale), scale, scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float) Math.atan(mouseY / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);
        ent.renderYawOffset = (float) Math.atan(mouseX / 40.0F) * 20.0F;
        ent.rotationYaw = (float) Math.atan(mouseX / 40.0F) * 40.0F;
        ent.rotationPitch = -((float) Math.atan(mouseY / 40.0F)) * 20.0F;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        rendermanager.setRenderShadow(true);
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    private Color getColourAtPos(int x, int y) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
        GL11.glReadPixels(x, y, 1, 1, GL11.GL_RGB, GL11.GL_FLOAT, buffer);
        GL11.glFinish();
        int r = Math.round(buffer.get() * 255);
        int g = Math.round(buffer.get() * 255);
        int b = Math.round(buffer.get() * 255);
        return new Color(r, g, b);
    }

    @Override
    public String getName() {
        return GUI_NAME;
    }
}
