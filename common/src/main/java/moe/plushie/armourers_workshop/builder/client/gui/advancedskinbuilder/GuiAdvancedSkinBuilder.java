//package moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder;
//
//import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
//import moe.plushie.armourers_workshop.api.common.skin.data.ISkinIdentifier;
//import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
//import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
//import moe.plushie.armourers_workshop.client.gui.advanced_skin_builder.panel.GuiAdvancedSkinBuilderPanelSettings;
//import moe.plushie.armourers_workshop.client.gui.armourer.tab.GuiTabArmourerMain.DropDownItemSkin;
//import moe.plushie.armourers_workshop.client.gui.controls.*;
//import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList.DropDownListItem;
//import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList.IDropDownListCallback;
//import moe.plushie.armourers_workshop.client.gui.controls.GuiTreeView.GuiTreeViewItem;
//import moe.plushie.armourers_workshop.client.gui.controls.GuiTreeView.IGuiTreeViewCallback;
//import moe.plushie.armourers_workshop.client.gui.controls.GuiTreeView.IGuiTreeViewItem;
//import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
//import moe.plushie.armourers_workshop.client.render.AdvancedPartRenderer;
//import moe.plushie.armourers_workshop.client.render.ModRenderHelper;
//import moe.plushie.armourers_workshop.client.render.SkinRenderData;
//import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
//import moe.plushie.armourers_workshop.common.addons.ModAddonManager;
//import moe.plushie.armourers_workshop.common.inventory.ContainerAdvancedSkinBuilder;
//import moe.plushie.armourers_workshop.common.inventory.slot.SlotHidable;
//import moe.plushie.armourers_workshop.common.lib.LibBlockNames;
//import moe.plushie.armourers_workshop.common.network.PacketHandler;
//import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiButton;
//import moe.plushie.armourers_workshop.common.skin.advanced.AdvancedPartNode;
//import moe.plushie.armourers_workshop.common.skin.advanced.IAdvancedPartParent;
//import moe.plushie.armourers_workshop.common.skin.data.Skin;
//import moe.plushie.armourers_workshop.common.skin.data.SkinIdentifier;
//import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
//import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
//import moe.plushie.armourers_workshop.common.tileentities.TileEntityAdvancedSkinBuilder;
//import moe.plushie.armourers_workshop.utils.ArrayUtils;
//import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
//import net.minecraft.client.gui.GuiButton;
//import net.minecraft.client.gui.ScaledResolution;
//import net.minecraft.client.model.ModelPlayer;
//import net.minecraft.client.renderer.GlStateManager;
//import net.minecraft.client.renderer.RenderHelper;
//import net.minecraft.client.resources.DefaultPlayerSkin;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.inventory.Slot;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.ResourceLocation;
//import net.minecraftforge.fml.relauncher.Side;
//import net.minecraftforge.fml.relauncher.SideOnly;
//import org.lwjgl.opengl.GL11;
//
//import java.awt.*;
//import java.io.IOException;
//import java.util.ArrayList;
//
//@SideOnly(Side.CLIENT)
//public class GuiAdvancedSkinBuilder extends ModGuiContainer<ContainerAdvancedSkinBuilder> implements IDropDownListCallback, IGuiTreeViewCallback, IAdvancedPartParent {
//
//    public static final int PADDING = 2;
//
//    private static final ResourceLocation TEXTURE_BUTTONS = new ResourceLocation(LibGuiResources.CONTROL_BUTTONS);
//    private static final int INVENTORY_HEIGHT = 76;
//    private static final int INVENTORY_WIDTH = 162;
//
//    private final TileEntityAdvancedSkinBuilder tileEntity;
//
//    public GuiAdvancedSkinBuilderPanelSettings panelSettings;
//
//    private ISkinType skinType = null;
//
//    private Rectangle recTreeView;
//    private Rectangle recNoteProp;
//    private Rectangle recSetting;
//    private Rectangle recSkinPreview;
//    private Rectangle[] recLayout;
//
//    // Tree View
//    private GuiTreeView treeView;
//    private GuiIconButton buttonAdd;
//    private GuiIconButton buttonRemove;
//    private GuiIconButton buttonSave;
//
//    // Node Options
//    private GuiDropDownList dropDownNoteSettings;
//
//    // Preview
//    private GuiDropDownList dropDownSkinType;
//
//    private ArrayList<ISkinIdentifier> skinIdentifiers = new ArrayList<ISkinIdentifier>();
//
//    public GuiAdvancedSkinBuilder(EntityPlayer player, TileEntityAdvancedSkinBuilder tileEntity) {
//        super(new ContainerAdvancedSkinBuilder(player.inventory, tileEntity));
//        this.tileEntity = tileEntity;
//    }
//
//    @Override
//    public void initGui() {
//        ScaledResolution reso = new ScaledResolution(mc);
//        this.xSize = reso.getScaledWidth();
//        this.ySize = reso.getScaledHeight();
//        super.initGui();
//
//        recTreeView = new Rectangle(PADDING, PADDING, 120, height - 90 - PADDING * 3);
//        recNoteProp = new Rectangle(PADDING, height - 90 - PADDING, recTreeView.width, 90);
//        recSetting = new Rectangle(recNoteProp.width + PADDING * 2, recNoteProp.y, width - recNoteProp.width - PADDING * 3, recNoteProp.height);
//        recSkinPreview = new Rectangle(recTreeView.width + PADDING * 2, PADDING, width - recTreeView.width - PADDING * 3, height - recNoteProp.height - PADDING * 3);
//        recLayout = new Rectangle[] { recTreeView, recNoteProp, recSetting, recSkinPreview };
//
//        buttonList.clear();
//
//        // Tree View
//        treeView = new GuiTreeView(recTreeView.x + PADDING, recTreeView.y + PADDING, recTreeView.width - PADDING * 2, recTreeView.height - 20 - PADDING);
//        treeView.setCallback(this);
//        buttonList.add(treeView);
//
//        buttonAdd = new GuiIconButton(this, 0, recTreeView.x + PADDING, recTreeView.y + recTreeView.height - PADDING - 16, 16, 16, TEXTURE_BUTTONS);
//        buttonAdd.setIconLocation(208, 176, 16, 16).setDrawButtonBackground(false).setHoverText("Add Node");
//        buttonList.add(buttonAdd);
//
//        buttonRemove = new GuiIconButton(this, 0, recTreeView.x + PADDING * 2 + 16, recTreeView.y + recTreeView.height - PADDING - 16, 16, 16, TEXTURE_BUTTONS);
//        buttonRemove.setIconLocation(208, 160, 16, 16).setDrawButtonBackground(false).setHoverText("Remove Node");
//        buttonList.add(buttonRemove);
//
//        buttonSave = new GuiIconButton(this, 0, recTreeView.x + PADDING * 3 + 32, recTreeView.y + recTreeView.height - PADDING - 16, 16, 16, TEXTURE_BUTTONS);
//        buttonSave.setIconLocation(208, 64, 16, 16).setDrawButtonBackground(false).setHoverText("Save");
//        buttonList.add(buttonSave);
//
//        // Node Options
//        dropDownNoteSettings = new GuiDropDownList(0, recNoteProp.x + PADDING, recNoteProp.y + PADDING + 10, recNoteProp.width - PADDING * 2, "", this);
//        updateSettingsDropDown();
//        dropDownNoteSettings.setMaxDisplayCount(5);
//        buttonList.add(dropDownNoteSettings);
//
//        // Settings
//        panelSettings = new GuiAdvancedSkinBuilderPanelSettings(this, recSetting.x, recSetting.y, recSetting.width, recSetting.height);
//        panelList.add(panelSettings);
//
//
//        movePlayerInventorySlots(width - INVENTORY_WIDTH - PADDING, height - INVENTORY_HEIGHT - PADDING);
//        setPlayerSlotVisible(false);
//        // setPlayerSlotVisible(true);
//
//        // Preview
//        SkinTypeRegistry str = SkinTypeRegistry.INSTANCE;
//        dropDownSkinType = new GuiDropDownList(0, recSkinPreview.x + recSkinPreview.width - 80 - PADDING, recSkinPreview.y + PADDING, 80, "", this);
//        ArrayList<ISkinType> skinList = str.getRegisteredSkinTypes();
//        int skinCount = 0;
//        for (int i = 0; i < skinList.size(); i++) {
//            ISkinType skinType = skinList.get(i);
//            if (!skinType.isHidden() & skinType != SkinTypeRegistry.skinOutfit) {
//                String skinLocalizedName = str.getLocalizedSkinTypeName(skinType);
//                String skinRegistryName = skinType.getRegistryName();
//                DropDownItemSkin item = new DropDownItemSkin(skinLocalizedName, skinRegistryName, skinType.enabled(), skinType);
//                dropDownSkinType.addListItem(item);
//                // if (skinType == tileEntity.getSkinType()) {
//                // dropDownSkinType.setListSelectedIndex(skinCount);
//                // }
//                skinCount++;
//            }
//        }
//
//        for (int i = 0; i < panelList.size(); i++) {
//            panelList.get(i).initGui();
//        }
//
//        updateActiveSkinType();
//        buttonList.add(dropDownSkinType);
//
//        updateSetting();
//    }
//
//    private void updateSettingsDropDown() {
//        dropDownNoteSettings.clearList();
//        GuiTreeView.IGuiTreeViewItem treeViewItem = treeView.getSelectedItem();
//        if (treeViewItem != null && treeViewItem instanceof GuiTreeViewPartNote) {
//            GuiTreeViewPartNote treeNote = (GuiTreeViewPartNote) treeViewItem;
//            for (PartNodeSetting setting : PartNodeSetting.values()) {
//                boolean enabled = true;
//                if (treeNote.isLocked()) {
//                    enabled = setting.onRoot;
//                }
//                dropDownNoteSettings.addListItem(setting.name(), setting.name(), enabled);
//            }
//        }
//    }
//
//    private void movePlayerInventorySlots(int xPos, int yPos) {
//        int slotSize = 18;
//
//        int neiBump = 18;
//        if (ModAddonManager.addonNEI.isVisible()) {
//            neiBump = 18;
//        } else {
//            neiBump = 0;
//        }
//        int playerInvY = yPos;
//        int hotBarY = playerInvY + 58;
//        for (int x = 0; x < 9; x++) {
//            Slot slot = inventorySlots.inventorySlots.get(x);
//            slot.xPos = xPos + x * 18;
//            slot.yPos = yPos - neiBump + 58;
//        }
//        for (int y = 0; y < 3; y++) {
//            for (int x = 0; x < 9; x++) {
//                Slot slot = inventorySlots.inventorySlots.get(x + y * 9 + 9);
//                slot.xPos = xPos + x * 18;
//                slot.yPos = yPos + y * slotSize - neiBump;
//            }
//        }
//    }
//
//    private ArrayList<ISkinIdentifier> readSkinsFromInv() {
//        ArrayList<ISkinIdentifier> identifiers = new ArrayList<ISkinIdentifier>();
//        int i1 = getContainer().getPlayerInvStartIndex();
//        int i2 = getContainer().getPlayerInvEndIndex();
//        for (int i = i1; i < i2; i++) {
//            Slot slot = inventorySlots.inventorySlots.get(i);
//            ItemStack stack = slot.getStack();
//            ISkinDescriptor descriptor = SkinNBTHelper.getSkinDescriptorFromStack(stack);
//            if (descriptor != null) {
//                if (descriptor.getIdentifier().getSkinType() == SkinTypeRegistry.skinPart) {
//                    identifiers.add(new SkinIdentifier(descriptor.getIdentifier()));
//                }
//            }
//        }
//        return identifiers;
//    }
//
//    public ArrayList<ISkinIdentifier> readSkinsFromTree() {
//        ArrayList<ISkinIdentifier> identifiers = new ArrayList<ISkinIdentifier>();
//        ArrayList<IGuiTreeViewItem> treeViewItems = treeView.getFullItemList();
//        for (IGuiTreeViewItem treeViewItem : treeViewItems) {
//            GuiTreeViewPartNote treeViewPartNote = (GuiTreeViewPartNote) treeViewItem;
//            if (treeViewPartNote.getSkinIdentifier() != null) {
//                if (!identifiers.contains(treeViewPartNote.getSkinIdentifier())) {
//                    identifiers.add(treeViewPartNote.getSkinIdentifier());
//                }
//            }
//        }
//        return identifiers;
//    }
//
//    public ArrayList<ISkinIdentifier> getListOfSkins() {
//        ArrayList<ISkinIdentifier> identifiers = new ArrayList<ISkinIdentifier>();
//        identifiers.addAll(readSkinsFromInv());
//        identifiers.addAll(readSkinsFromTree());
//        return ArrayUtils.removeDuplicates(identifiers);
//    }
//
//    private void updateSkinList() {
//        skinIdentifiers.clear();
//        skinIdentifiers.addAll(readSkinsFromTree());
//    }
//
//    private void setPlayerSlotVisible(boolean visible) {
//        for (int i = getContainer().getPlayerInvStartIndex(); i < getContainer().getPlayerInvEndIndex(); i++) {
//            Slot slot = getContainer().getSlot(i);
//            if (slot instanceof SlotHidable) {
//                ((SlotHidable) slot).setVisible(visible);
//            }
//        }
//    }
//
//    private void updateActiveSkinType() {
//        skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(dropDownSkinType.getListSelectedItem().tag);
//        treeView.getItems().clear();
//        if (skinType != null) {
//            for (int i = 0; i < skinType.getSkinParts().size(); i++) {
//                ISkinPartType partType = skinType.getSkinParts().get(i);
//                String partName = SkinTypeRegistry.INSTANCE.getLocalizedSkinPartTypeName(partType);
//                GuiTreeViewPartNote treeViewPartNote = new GuiTreeViewPartNote("root - " + partName);
//                treeViewPartNote.setLocked(true);
//                treeViewPartNote.setColour(0xFFCCCCFF);
//                treeViewPartNote.getAdvancedPartNode().partIndex = -1;
//                treeView.addItem(treeViewPartNote);
//            }
//            GuiTreeViewPartNote treeViewPartFree = new GuiTreeViewPartNote("root - Float");
//            treeViewPartFree.setLocked(true);
//            treeViewPartFree.setColour(0xFFCCCCFF);
//            treeViewPartFree.getAdvancedPartNode().enabled = false;
//            treeView.addItem(treeViewPartFree);
//
//            GuiTreeViewPartNote treeViewPartStatic = new GuiTreeViewPartNote("root - Static");
//            treeViewPartStatic.setLocked(true);
//            treeViewPartStatic.setColour(0xFFCCCCFF);
//            treeViewPartStatic.getAdvancedPartNode().enabled = false;
//            treeView.addItem(treeViewPartStatic);
//        }
//    }
//
//    private void updateSetting() {
//        PartNodeSetting nodeSetting = getActiveSetting();
//        GuiTreeViewPartNote treeViewPartNote = null;
//        GuiTreeView.IGuiTreeViewItem treeViewItem = treeView.getSelectedItem();
//        if (treeViewItem != null && treeViewItem instanceof GuiTreeViewPartNote) {
//            treeViewPartNote = (GuiTreeViewPartNote) treeViewItem;
//        }
//        panelSettings.updateSetting(nodeSetting, treeViewPartNote);
//    }
//
//    public void applySetting() {
//        PartNodeSetting nodeSetting = getActiveSetting();
//        GuiTreeViewPartNote treeViewPartNote = null;
//        GuiTreeView.IGuiTreeViewItem treeViewItem = treeView.getSelectedItem();
//        if (treeViewItem != null && treeViewItem instanceof GuiTreeViewPartNote) {
//            treeViewPartNote = (GuiTreeViewPartNote) treeViewItem;
//        }
//        panelSettings.applySetting(nodeSetting, treeViewPartNote);
//    }
//
//    public AdvancedPartNode convertTreeToAdvancedPartNode() {
//        AdvancedPartNode partNode = new AdvancedPartNode(-1, "main root");
//        convertTreeToAdvancedPartNode(partNode, treeView.getItems());
//        // ModLogger.log(partNode);
//        return partNode;
//    }
//
//    public ISkinType getSkinType() {
//        return skinType;
//    }
//
//    private AdvancedPartNode convertTreeToAdvancedPartNode(AdvancedPartNode advancedPartNode, ArrayList<GuiTreeView.IGuiTreeViewItem> treeViewItems) {
//        for (GuiTreeView.IGuiTreeViewItem treeViewItem : treeViewItems) {
//            GuiTreeViewPartNote partNote = (GuiTreeViewPartNote) treeViewItem;
//            AdvancedPartNode node = partNote.getAdvancedPartNode().clone();
//            node.partIndex = -1;
//            if (partNote.getSkinIdentifier() != null) {
//                for (int i = 0; i < skinIdentifiers.size(); i++) {
//                    if (skinIdentifiers.get(i).equals(partNote.getSkinIdentifier())) {
//                        node.partIndex = i;
//                        break;
//                    }
//                }
//            }
//            advancedPartNode.getChildren().add(node);
//            convertTreeToAdvancedPartNode(node, treeViewItem.getSubItems());
//        }
//        return advancedPartNode;
//    }
//
//    @Override
//    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//        // this.drawDefaultBackground();
//        super.drawScreen(mouseX, mouseY, partialTicks);
//        this.renderHoveredToolTip(mouseX, mouseY);
//    }
//
//    @Override
//    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
//        for (Rectangle rec : recLayout) {
//            drawRect(rec.x, rec.y, rec.x + rec.width, rec.y + rec.height, 0x44808080);
//        }
//
//        fontRenderer.drawString("Tree View:", recTreeView.x + PADDING, recTreeView.y + PADDING, 0xCCFFFFFF);
//        fontRenderer.drawString("Node Options:", recNoteProp.x + PADDING, recNoteProp.y + PADDING, 0xCCFFFFFF);
//
//        fontRenderer.drawString("Preview:", recSkinPreview.x + PADDING, recSkinPreview.y + PADDING, 0xCCFFFFFF);
//
//        GlStateManager.disableDepth();
//        GlStateManager.pushAttrib();
//        for (GuiPanel panel : panelList) {
//            panel.drawBackground(mouseX, mouseY, partialTicks);
//        }
//        GlStateManager.popAttrib();
//        GlStateManager.pushAttrib();
//        for (GuiPanel panel : panelList) {
//            panel.draw(mouseX, mouseY, partialTicks);
//        }
//        GlStateManager.popAttrib();
//        GlStateManager.pushAttrib();
//        for (GuiPanel panel : panelList) {
//            panel.drawForeground(mouseX, mouseY, partialTicks);
//        }
//        GlStateManager.popAttrib();
//        GlStateManager.enableDepth();
//    }
//
//    @Override
//    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
//        dropDownSkinType.drawForeground(mc, mouseX, mouseY, 0);
//        dropDownNoteSettings.drawForeground(mc, mouseX, mouseY, 0);
//
//        GlStateManager.pushMatrix();
//        GlStateManager.pushAttrib();
//        GlStateManager.translate(recSkinPreview.x + recSkinPreview.width / 2, recSkinPreview.y + recSkinPreview.height / 2, 500);
//
//        RenderHelper.enableGUIStandardItemLighting();
//        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//        GlStateManager.disableNormalize();
//        GlStateManager.disableColorMaterial();
//        GlStateManager.enableNormalize();
//        GlStateManager.enableColorMaterial();
//        ModRenderHelper.enableAlphaBlend();
//        GlStateManager.enableDepth();
//
//        float scale = 50;
//        GlStateManager.scale((-scale), scale, scale);
//
//        GL11.glRotatef(-40, 1.0F, 0.0F, 0.0F);
//        float rotation = (float) ((double) System.currentTimeMillis() / 10 % 360);
//        GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
//
//        mc.renderEngine.bindTexture(DefaultPlayerSkin.getDefaultSkinLegacy());
//        GlStateManager.pushMatrix();
//
//        // GlStateManager.scale(16F / 1F, 1F, 1F);
//        ModelPlayer modelPlayer = new ModelPlayer(1, false);
//        if (skinType == SkinTypeRegistry.skinHead) {
//            modelPlayer.bipedHead.render(1F / 16F);
//        }
//        if (skinType == SkinTypeRegistry.skinChest) {
//            modelPlayer.bipedBody.render(1F / 16F);
//            GlStateManager.translate(2F * (1F / 16F), 0, 0);
//            modelPlayer.bipedLeftArm.render(1F / 16F);
//            GlStateManager.translate(-2F * (1F / 16F), 0, 0);
//
//            GlStateManager.translate(-2F * (1F / 16F), 0, 0);
//            modelPlayer.bipedRightArm.render(1F / 16F);
//            GlStateManager.translate(2F * (1F / 16F), 0, 0);
//        }
//        if (skinType == SkinTypeRegistry.skinLegs) {
//            modelPlayer.bipedLeftLeg.render(1F / 16F);
//            modelPlayer.bipedRightLeg.render(1F / 16F);
//        }
//        if (skinType == SkinTypeRegistry.skinFeet) {
//            modelPlayer.bipedLeftLeg.render(1F / 16F);
//            modelPlayer.bipedRightLeg.render(1F / 16F);
//        }
//        // ArmourerRenderHelper.renderBuildingGrid(skinType, 1F / 16F, true, new
//        // SkinProperties(), false);
//        GlStateManager.popMatrix();
//
//        SkinRenderData renderData = new SkinRenderData(1F / 16F, null, null, 0, true, true, true, null);
//        updateSkinList();
//        AdvancedPartRenderer.renderAdvancedSkin(this, renderData, null, null, convertTreeToAdvancedPartNode());
//
//        ModRenderHelper.enableAlphaBlend();
//
//        GlStateManager.disablePolygonOffset();
//        GlStateManager.disableDepth();
//
//        // ModRenderHelper.disableAlphaBlend();
//        GlStateManager.disableNormalize();
//        GlStateManager.disableColorMaterial();
//
//        RenderHelper.disableStandardItemLighting();
//        GlStateManager.resetColor();
//
//        GlStateManager.popAttrib();
//        GlStateManager.popMatrix();
//    }
//
//    @Override
//    protected void actionPerformed(GuiButton button) throws IOException {
//        if (button == buttonAdd) {
//            treeView.addItem(new GuiTreeViewPartNote("New Node"), treeView.getSelectedIndex());
//        }
//        if (button == buttonRemove) {
//            treeView.removeSelectedItem();
//        }
//        if (button == buttonSave) {
//            MessageClientGuiButton message = new MessageClientGuiButton((byte) 0);
//            PacketHandler.networkWrapper.sendToServer(message);
//        }
//    }
//
//    @Override
//    public String getName() {
//        return LibBlockNames.ADVANCED_SKIN_BUILDER;
//    }
//
//    @Override
//    public void onDropDownListChanged(GuiDropDownList dropDownList) {
//        if (dropDownList == dropDownSkinType) {
//            updateActiveSkinType();
//        }
//        if (dropDownList == dropDownNoteSettings) {
//            updateSetting();
//        }
//    }
//
//    private PartNodeSetting getActiveSetting() {
//        DropDownListItem listItem = dropDownNoteSettings.getListSelectedItem();
//        if (listItem != null) {
//            return PartNodeSetting.valueOf(listItem.tag);
//        }
//        return null;
//    }
//
//    @Override
//    public void onSelectionChange(GuiTreeView guiTreeView, IGuiTreeViewItem selectedItem) {
//        updateSettingsDropDown();
//        updateSetting();
//    }
//
//    @Override
//    public SkinPart getAdvancedPart(int index) {
//        if (index >= 0 & index < skinIdentifiers.size()) {
//            Skin skin = null;
//            skin = ClientSkinCache.INSTANCE.getSkin(skinIdentifiers.get(index));
//            if (skin != null) {
//                return skin.getParts().get(0);
//            }
//        }
//        return null;
//    }
//
//    @Override
//    public AdvancedPartNode getAdvancedPartNode(int index) {
//        return null;
//    }
//
//    public static class GuiTreeViewPartNote extends GuiTreeViewItem {
//
//        private AdvancedPartNode advancedPartNode;
//        private ISkinIdentifier skinIdentifier;
//
//        public GuiTreeViewPartNote(String name) {
//            super(name);
//            advancedPartNode = new AdvancedPartNode(0, name);
//        }
//
//        @Override
//        public void setName(String name) {
//            super.setName(name);
//            advancedPartNode.name = name;
//        }
//
//        public AdvancedPartNode getAdvancedPartNode() {
//            return advancedPartNode;
//        }
//
//        public ISkinIdentifier getSkinIdentifier() {
//            return skinIdentifier;
//        }
//
//        public void setSkinIdentifier(ISkinIdentifier skinIdentifier) {
//            this.skinIdentifier = skinIdentifier;
//        }
//    }
//
//    public static enum PartNodeSetting {
//
//        NAME(false), SKIN(true), ENABLED(true), SCALE(false), MIRROR(true), POSITION(false), ROTATION(false), ROTATION_POSITION(false);
//
//        private boolean onRoot;
//
//        private PartNodeSetting(boolean onRoot) {
//            this.onRoot = onRoot;
//        }
//    }
//}
