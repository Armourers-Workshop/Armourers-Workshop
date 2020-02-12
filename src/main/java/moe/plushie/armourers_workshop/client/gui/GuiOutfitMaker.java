package moe.plushie.armourers_workshop.client.gui;

import java.io.IOException;

import moe.plushie.armourers_workshop.client.gui.controls.GuiIconButton;
import moe.plushie.armourers_workshop.client.gui.controls.GuiLabeledTextField;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.common.inventory.ContainerOutfitMaker;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiButton;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiUpdateTileProperties;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityOutfitMaker;
import moe.plushie.armourers_workshop.common.tileentities.property.TileProperty;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiOutfitMaker extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.GUI_OUTFIT_MAKER);
    
    private final TileEntityOutfitMaker tileEntity;
    
    private GuiLabeledTextField textItemName;
    private GuiLabeledTextField textFlavour;
    private GuiIconButton iconButtonLoad;
    private GuiIconButton iconButtonSave;
    
    public GuiOutfitMaker(EntityPlayer entityPlayer, TileEntityOutfitMaker tileEntity) {
        super(new ContainerOutfitMaker(entityPlayer, tileEntity));
        this.tileEntity = tileEntity;
        xSize = 176;
        ySize = 240;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        
        textItemName = new GuiLabeledTextField(fontRenderer, getGuiLeft() + 8, getGuiTop() + 18, 158, 16);
        textItemName.setMaxStringLength(40);
        textItemName.setText(tileEntity.PROP_OUTFIT_NAME.get());
        textItemName.setEmptyLabel(GuiHelper.getLocalizedControlName(tileEntity.getName(), "skinName"));
        
        textFlavour = new GuiLabeledTextField(fontRenderer, getGuiLeft() + 8, getGuiTop() + 38, 158, 16);
        textFlavour.setMaxStringLength(40);
        textFlavour.setText(tileEntity.PROP_OUTFIT_FLAVOUR.get());
        textFlavour.setEmptyLabel(GuiHelper.getLocalizedControlName(tileEntity.getName(), "skinFlavour"));
        
        iconButtonLoad = new GuiIconButton(this, 0, getGuiLeft() + 6, getGuiTop() + 120, 20, 20, GuiHelper.getLocalizedControlName(tileEntity.getName(), "load"), TEXTURE).setIconLocation(176, 240, 16, 16);
        iconButtonSave = new GuiIconButton(this, 1, getGuiLeft() + getXSize() - 20 - 6, getGuiTop() + 120, 20, 20, GuiHelper.getLocalizedControlName(tileEntity.getName(), "save"), TEXTURE).setIconLocation(176, 224, 16, 16);
        
        //buttonList.add(iconButtonLoad);
        buttonList.add(iconButtonSave);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        MessageClientGuiButton message = new MessageClientGuiButton((byte) button.id);
        PacketHandler.networkWrapper.sendToServer(message);
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        textItemName.mouseClicked(mouseX, mouseY, mouseButton);
        textFlavour.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 1) {
            if (textItemName.isFocused()) {
                textItemName.setText("");
            }
            if (textFlavour.isFocused()) {
                textFlavour.setText("");
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        boolean typed = false;
        if (!typed) {
            typed = textItemName.textboxKeyTyped(typedChar, keyCode);
        }
        if (!typed) {
            typed = textFlavour.textboxKeyTyped(typedChar, keyCode);
        }
        if (typed) {
            String sendTextName = textItemName.getText().trim();
            String sendTextFlavour = textFlavour.getText().trim();
            boolean textChanged = false;
            if (!sendTextName.equals(tileEntity.PROP_OUTFIT_NAME.get())) {
                tileEntity.PROP_OUTFIT_NAME.set(sendTextName);
                textChanged = true;
            }
            if (!sendTextFlavour.equals(tileEntity.PROP_OUTFIT_FLAVOUR.get())) {
                tileEntity.PROP_OUTFIT_FLAVOUR.set(sendTextFlavour);
                textChanged = true;
            }
            if (textChanged) {
                updateProperty(tileEntity.PROP_OUTFIT_NAME);
                updateProperty(tileEntity.PROP_OUTFIT_FLAVOUR);
            }
        }
        if (!typed) {
            super.keyTyped(typedChar, keyCode);
        }
    }
    
    public void updateProperty(TileProperty<?>... property) {
        MessageClientGuiUpdateTileProperties message = new MessageClientGuiUpdateTileProperties(property);
        PacketHandler.networkWrapper.sendToServer(message);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1F, 1F, 1F, 1F);
        mc.renderEngine.bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        textItemName.drawTextBox();
        textFlavour.drawTextBox();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiHelper.renderLocalizedGuiName(this.fontRenderer, this.xSize, tileEntity.getName());
        this.fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
        GlStateManager.pushMatrix();
        GlStateManager.translate(-guiLeft, -guiTop, 0);
        iconButtonLoad.drawRollover(mc, mouseX, mouseY);
        iconButtonSave.drawRollover(mc, mouseX, mouseY);
        GlStateManager.popMatrix();
    }
}
