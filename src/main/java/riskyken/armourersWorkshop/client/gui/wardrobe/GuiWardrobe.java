package riskyken.armourersWorkshop.client.gui.wardrobe;

import java.awt.Color;
import java.nio.FloatBuffer;

import org.apache.logging.log4j.Level;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiTab;
import riskyken.armourersWorkshop.client.gui.controls.GuiTabPanel;
import riskyken.armourersWorkshop.client.gui.controls.GuiTabbed;
import riskyken.armourersWorkshop.client.gui.wardrobe.tab.GuiTabWardrobeColourSettings;
import riskyken.armourersWorkshop.client.gui.wardrobe.tab.GuiTabWardrobeDisplaySettings;
import riskyken.armourersWorkshop.client.gui.wardrobe.tab.GuiTabWardrobeOutfits;
import riskyken.armourersWorkshop.client.gui.wardrobe.tab.GuiTabWardrobeSkins;
import riskyken.armourersWorkshop.client.lib.LibGuiResources;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.common.inventory.ContainerSkinWardrobe;
import riskyken.armourersWorkshop.common.inventory.slot.SlotHidable;
import riskyken.armourersWorkshop.common.wardrobe.EquipmentWardrobeData;
import riskyken.armourersWorkshop.common.wardrobe.ExPropsPlayerSkinData;
import riskyken.armourersWorkshop.proxies.ClientProxy;
import riskyken.armourersWorkshop.utils.ModLogger;

@SideOnly(Side.CLIENT)
public class GuiWardrobe extends GuiTabbed {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.WARDROBE);
    private static final ResourceLocation TEXTURE_TAB = new ResourceLocation(LibGuiResources.WARDROBE_TABS);
    
    private final GuiTabWardrobeSkins tabSkins;
    private final GuiTabWardrobeOutfits tabOutfits;
    private final GuiTabWardrobeDisplaySettings tabDisplaySettings;
    private final GuiTabWardrobeColourSettings tabColourSettings;

    ExPropsPlayerSkinData customEquipmentData;
    EquipmentWardrobeData equipmentWardrobeData;
    EntityPlayer player;
    
    private boolean rotatingPlayer = false;
    private float playerRotation = 45F;

    private int lastMouseX;
    private int lastMouseY;
    
    String guiName = "equipmentWardrobe";
    
    public GuiWardrobe(InventoryPlayer inventory, ExPropsPlayerSkinData customEquipmentData) {
        super(new ContainerSkinWardrobe(inventory, customEquipmentData), false, TEXTURE_TAB);
        
        // Tab size 21
        this.xSize = 236;
        this.ySize = 240;
        
        this.player = inventory.player;
        this.customEquipmentData = customEquipmentData;
        PlayerPointer playerPointer = new PlayerPointer(player);
        equipmentWardrobeData = ClientProxy.equipmentWardrobeHandler.getEquipmentWardrobeData(playerPointer);
        
        if (equipmentWardrobeData == null) {
            equipmentWardrobeData = new EquipmentWardrobeData();
            ModLogger.log(Level.ERROR,"Unable to get skin info for player: " + this.player.getDisplayName());
        }
        
        tabSkins = new GuiTabWardrobeSkins(0, this);
        tabOutfits = new GuiTabWardrobeOutfits(1, this);
        tabDisplaySettings = new GuiTabWardrobeDisplaySettings(2, this, player, customEquipmentData, equipmentWardrobeData);
        tabColourSettings = new GuiTabWardrobeColourSettings(3, this, player, customEquipmentData, equipmentWardrobeData);
        
        tabList.add(tabSkins);
        tabList.add(tabOutfits);
        tabList.add(tabDisplaySettings);
        tabList.add(tabColourSettings);
        
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(guiName, "tab.skins")).setIconLocation(52, 0).setTabTextureSize(26, 30).setPadding(0, 4, 3, 3));
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(guiName, "tab.outfits")).setIconLocation(52 + 16 * 4, 0).setTabTextureSize(26, 30).setPadding(0, 4, 3, 3));
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(guiName, "tab.displaySettings")).setIconLocation(52 + 16, 0).setTabTextureSize(26, 30).setPadding(0, 4, 3, 3));
        tabController.addTab(new GuiTab(GuiHelper.getLocalizedControlName(guiName, "tab.colourSettings")).setIconLocation(52 + 32, 0).setTabTextureSize(26, 30).setPadding(0, 4, 3, 3));
        
        tabController.setActiveTabIndex(activeTab);
        
        tabChanged();
    }
    
    public ContainerSkinWardrobe getContainer() {
        return (ContainerSkinWardrobe) inventorySlots;
}
    
    private void setSlotVisibilitySkins(boolean visible) {
        setSlotVisibility(getContainer().getIndexSkinsStart(), getContainer().getIndexSkinsEnd(), visible);
    }
    
    private void setSlotVisibilityOutfits(boolean visible) {
        setSlotVisibility(getContainer().getIndexOutfitStart(), getContainer().getIndexOutfitEnd(), visible);
    }
    
    private void setSlotVisibility(int start, int end, boolean visible) {
        for (int i = start; i < end; i++) {
            Object slot = inventorySlots.inventorySlots.get(i);
            if (slot != null && slot instanceof SlotHidable) {
                ((SlotHidable)slot).setVisible(visible);
            }
        }
    }
    
    @Override
    protected void tabChanged() {
        super.tabChanged();
        setSlotVisibilitySkins(activeTab == tabSkins.getTabId());
        setSlotVisibilityOutfits(activeTab == tabOutfits.getTabId());
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        // Title label.
        GuiHelper.renderLocalizedGuiName(fontRendererObj, this.xSize, "equipmentWardrobe");
        for (int i = 0; i < tabList.size(); i++) {
            GuiTabPanel tab = tabList.get(i);
            if (tab.getTabId() == activeTab) {
                tab.drawForegroundLayer(mouseX, mouseY);
            }
        }
        // Player inventory label.
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 36, this.ySize - 96 + 2, 4210752);
        GL11.glPushMatrix();
        GL11.glTranslatef(-guiLeft, -guiTop, 0F);
        tabController.drawHoverText(mc, mouseX, mouseY);
        GL11.glPopMatrix();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        mc.renderEngine.bindTexture(TEXTURE);
        for (int i = 0; i < tabList.size(); i++) {
            GuiTabPanel tab = tabList.get(i);
            if (tab.getTabId() == activeTab) {
                tab.drawBackgroundLayer(partialTickTime, mouseX, mouseY);
            }
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
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        if (button == 1) {
            rotatingPlayer = true;
        }
        super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int button) {
        super.mouseMovedOrUp(mouseX, mouseY, button);
        if (button == 1) {
            rotatingPlayer = false;
        }
    }
    
    public void drawPlayerPreview(int x, int y, int mouseX, int mouseY) {
        drawPlayerPreview(x, y, mouseX, mouseY, false);
    }
    
    public Color drawPlayerPreview(int x, int y, int mouseX, int mouseY, boolean selectingColour) {
        Color colour = new Color(255, 255, 255);
        // 3D player preview
        float boxX = x + 36.5F;
        float boxY = y + 100F;
        float lookX = boxX - mouseX;
        float lookY = boxY - 50 - mouseY;
        
        
        /*
        drawGradientRect(
                x + 8,
                y + 18,
                x + 8 + 57,
                y + 18 + 92,
                0xFFFFFFFF, 0xFFFFFFFF);
        */
        boolean overPlayerBox = false;
        if (mouseX >= x + 8 & mouseX < x + 8 + 57) {
            if (mouseY >= y + 18 & mouseY < y + 18 + 92) {
                overPlayerBox = true;
            }
        }
        
        if (!overPlayerBox) {
            ModRenderHelper.enableScissorScaled(x + 8, y + 18, 57, 92);
        }
        
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glTranslatef(boxX, boxY, 50);
        GL11.glRotatef(-20, 1, 0, 0);
        GL11.glRotatef(playerRotation, 0, 1, 0);
        GL11.glTranslatef(0, 0, -50);
        if (selectingColour) {
            renderEntityWithoutLighting(0, 0, 35, 0, 0, this.mc.thePlayer);
            colour = getColourAtPos(Mouse.getX(), Mouse.getY());
        }
        GuiInventory.func_147046_a(0, 0, 35, 0, 0, this.mc.thePlayer);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
        
        if (!overPlayerBox) {
            ModRenderHelper.disableScissor();
        }
        
        
        return colour;
    }
    
    private void renderEntityWithoutLighting(int xPos, int yPos, int scale, float yaw, float pitch, EntityLivingBase entity) {
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) xPos, (float) yPos, 50.0F);
        GL11.glScalef((float) (-scale), (float) scale, (float) scale);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        float f2 = entity.renderYawOffset;
        float f3 = entity.rotationYaw;
        float f4 = entity.rotationPitch;
        float f5 = entity.prevRotationYawHead;
        float f6 = entity.rotationYawHead;
        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.disableStandardItemLighting();
        ModRenderHelper.disableLighting();
        GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-((float) Math.atan((double) (pitch / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        entity.renderYawOffset = (float) Math.atan((double) (yaw / 40.0F)) * 20.0F;
        entity.rotationYaw = (float) Math.atan((double) (yaw / 40.0F)) * 40.0F;
        entity.rotationPitch = -((float) Math.atan((double) (pitch / 40.0F))) * 20.0F;
        entity.rotationYawHead = entity.rotationYaw;
        entity.prevRotationYawHead = entity.rotationYaw;
        GL11.glTranslatef(0.0F, entity.yOffset, 0.0F);
        RenderManager.instance.playerViewY = 180.0F;
        RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        entity.renderYawOffset = f2;
        entity.rotationYaw = f3;
        entity.rotationPitch = f4;
        entity.prevRotationYawHead = f5;
        entity.rotationYawHead = f6;
        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
    
    private Color getColourAtPos(int x, int y) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(3);
        GL11.glReadPixels(x, y, 1, 1, GL11.GL_RGB, GL11.GL_FLOAT, buffer);
        int r = Math.round(buffer.get() * 255);
        int g = Math.round(buffer.get() * 255);
        int b = Math.round(buffer.get() * 255);
        return new Color(r, g, b);
    }
}
