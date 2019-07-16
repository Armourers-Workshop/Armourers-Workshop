package riskyken.armourersWorkshop.client.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.client.gui.controls.GuiIconButton;
import riskyken.armourersWorkshop.client.gui.controls.GuiLabeledTextField;
import riskyken.armourersWorkshop.client.lib.LibGuiResources;
import riskyken.armourersWorkshop.common.inventory.ContainerOutfitMaker;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiButton;
import riskyken.armourersWorkshop.common.tileentities.TileEntityOutfitMaker;

@SideOnly(Side.CLIENT)
public class GuiOutfitMaker extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.OUTFIT_MAKER);

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

        textItemName = new GuiLabeledTextField(fontRendererObj, guiLeft + 8, guiTop + 18, 158, 16);
        textItemName.setMaxStringLength(40);
        textItemName.setText(tileEntity.getOutfitName());
        textItemName.setEmptyLabel(GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "skinName"));

        textFlavour = new GuiLabeledTextField(fontRendererObj, guiLeft + 8, guiTop + 38, 158, 16);
        textFlavour.setMaxStringLength(40);
        textFlavour.setText(tileEntity.getOutfitFlavour());
        textFlavour.setEmptyLabel(GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "skinFlavour"));

        iconButtonLoad = new GuiIconButton(this, 0, guiLeft + 6, guiTop + 120, 20, 20, GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "load"), TEXTURE).setIconLocation(176, 240, 16, 16);
        iconButtonSave = new GuiIconButton(this, 1, guiLeft + xSize - 20 - 6, guiTop + 120, 20, 20, GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "save"), TEXTURE).setIconLocation(176, 224, 16, 16);

        // buttonList.add(iconButtonLoad);
        buttonList.add(iconButtonSave);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        MessageClientGuiButton message = new MessageClientGuiButton((byte) button.id);
        PacketHandler.networkWrapper.sendToServer(message);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
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
    protected void keyTyped(char typedChar, int keyCode) {
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
            if (!sendTextName.equals(tileEntity.getOutfitName())) {
                tileEntity.setOutfitName(sendTextName);
                textChanged = true;
            }
            if (!sendTextFlavour.equals(tileEntity.getOutfitFlavour())) {
                tileEntity.setOutfitFlavour(sendTextFlavour);
                textChanged = true;
            }
            if (textChanged) {
                updateProperty(sendTextName, sendTextFlavour);
            }
        }
        if (!typed) {
            super.keyTyped(typedChar, keyCode);
        }
    }

    public void updateProperty(String sendTextName, String sendTextFlavour) {
        // MessageClientGuiUpdateTileProperties message = new MessageClientGuiUpdateTileProperties(property);
        // PacketHandler.networkWrapper.sendToServer(message);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        mc.renderEngine.bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        textItemName.drawTextBox();
        textFlavour.drawTextBox();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GuiHelper.renderLocalizedGuiName(this.fontRendererObj, this.xSize, tileEntity.getInventoryName());
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
        GL11.glPushMatrix();
        GL11.glTranslatef(-guiLeft, -guiTop, 0);
        iconButtonLoad.drawRollover(mc, mouseX, mouseY);
        iconButtonSave.drawRollover(mc, mouseX, mouseY);
        GL11.glPopMatrix();
    }
}
