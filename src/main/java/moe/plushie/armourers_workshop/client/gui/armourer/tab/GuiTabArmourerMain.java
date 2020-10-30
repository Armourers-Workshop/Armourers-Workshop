package moe.plushie.armourers_workshop.client.gui.armourer.tab;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.armourer.GuiArmourer;
import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList;
import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList.DropDownListItem;
import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList.IDropDownListCallback;
import moe.plushie.armourers_workshop.client.gui.controls.GuiHelp;
import moe.plushie.armourers_workshop.client.gui.controls.GuiIconButton;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTabPanel;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiButton;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiSetArmourerSkinProps;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiSetArmourerSkinType;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientLoadArmour;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityArmourer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTabArmourerMain extends GuiTabPanel<GuiArmourer> implements IDropDownListCallback {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.GUI_ARMOURER);
    private static final ResourceLocation TEXTURE_BUTTONS = new ResourceLocation(LibGuiResources.CONTROL_BUTTONS);

    private final TileEntityArmourer tileEntity;

    private GuiDropDownList dropDownSkinType;
    private GuiTextField textItemName;
    private GuiTextField textFlavour;
    private boolean resetting;

    public GuiTabArmourerMain(int tabId, GuiArmourer parent) {
        super(tabId, parent, false);
        tileEntity = parent.tileEntity;
    }

    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        String guiName = tileEntity.getName();

        buttonList.clear();

        SkinTypeRegistry str = SkinTypeRegistry.INSTANCE;
        dropDownSkinType = new GuiDropDownList(0, 7, 21, 50, "", this);
        ArrayList<ISkinType> skinList = str.getRegisteredSkinTypes();
        int skinCount = 0;
        for (int i = 0; i < skinList.size(); i++) {
            ISkinType skinType = skinList.get(i);
            if (!skinType.isHidden() & skinType != SkinTypeRegistry.skinOutfit) {
                String skinLocalizedName = str.getLocalizedSkinTypeName(skinType);
                String skinRegistryName = skinType.getRegistryName();
                DropDownItemSkin item = new DropDownItemSkin(skinLocalizedName, skinRegistryName, skinType.enabled(), skinType);
                dropDownSkinType.addListItem(item);
                if (skinType == tileEntity.getSkinType()) {
                    dropDownSkinType.setListSelectedIndex(skinCount);
                }
                skinCount++;
            }
        }
        buttonList.add(dropDownSkinType);

        // TODO Make button icons for save/load buttons.
        GuiIconButton buttonSave = new GuiIconButton(parent, 13, 88, 16, 16, 16, GuiHelper.getLocalizedControlName(guiName, "save"), TEXTURE_BUTTONS);
        GuiIconButton buttonLoad = new GuiIconButton(parent, 14, 88, 16 + 13, 16, 16, GuiHelper.getLocalizedControlName(guiName, "load"), TEXTURE_BUTTONS);
        // buttonList.add(buttonSave);
        // buttonList.add(buttonLoad);

        buttonList.add(new GuiButtonExt(13, 88, 16, 50, 12, GuiHelper.getLocalizedControlName(guiName, "save")));
        buttonList.add(new GuiButtonExt(14, 88, 16 + 13, 50, 12, GuiHelper.getLocalizedControlName(guiName, "load")));

        textItemName = new GuiTextField(-1, fontRenderer, x + 8, y + 58, 158, 16);
        textItemName.setMaxStringLength(40);
        textItemName.setText(SkinProperties.PROP_ALL_CUSTOM_NAME.getValue(tileEntity.getSkinProps()));

        textFlavour = new GuiTextField(-1, fontRenderer, x + 8, y + 90, 158, 16);
        textFlavour.setMaxStringLength(40);
        textFlavour.setText(SkinProperties.PROP_ALL_FLAVOUR_TEXT.getValue(tileEntity.getSkinProps()));

        buttonList.add(new GuiHelp(parent, 0, 6, 12, GuiHelper.getLocalizedControlName(guiName, "main.help.skinType")));
        buttonList.add(new GuiHelp(parent, 0, 81, 18, GuiHelper.getLocalizedControlName(guiName, "main.help.save")));
        buttonList.add(new GuiHelp(parent, 0, 81, 30, GuiHelper.getLocalizedControlName(guiName, "main.help.load")));
        buttonList.add(new GuiHelp(parent, 0, 6, 48, GuiHelper.getLocalizedControlName(guiName, "main.help.itemName")));
        buttonList.add(new GuiHelp(parent, 0, 6, 80, GuiHelper.getLocalizedControlName(guiName, "main.help.itemFlavour")));
    }

    public static class DropDownItemSkin extends DropDownListItem {

        private final ISkinType skinType;

        public DropDownItemSkin(String displayText, String tag, boolean enabled, ISkinType skinType) {
            super(displayText, tag, enabled);
            this.skinType = skinType;
        }

        @Override
        public void drawItem(Minecraft mc, GuiDropDownList parent, int x, int y, int mouseX, int mouseY, float partial, boolean topItem) {
            int textWidth = parent.width - 8;
            int textHeight = 8;
            int textColour = 16777215;
            if (topItem) {
                mc.fontRenderer.drawString(displayText, x, y, textColour);
            } else {
                // textWidth -= 7;
                if (!enabled) {
                    textColour = 0xFFCC0000;
                } else {
                    if (isMouseOver(parent, x, y, mouseX, mouseY) & !topItem) {
                        if (enabled) {
                            textColour = 16777120;
                            drawRect(x, y, x + textWidth, y + textHeight, 0x44CCCCCC);
                        }
                    }
                }
                mc.renderEngine.bindTexture(skinType.getIcon());
                GlStateManager.color(1, 1, 1);
                Gui.drawScaledCustomSizeModalRect(x - 2, y, 0, 0, 16, 16, 8, 8, 16, 16);
                mc.fontRenderer.drawString(displayText, x + 7, y, textColour);
            }
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        boolean clicked = super.mouseClicked(mouseX, mouseY, button);
        if (!clicked) {
            textItemName.mouseClicked(mouseX, mouseY, button);
            textFlavour.mouseClicked(mouseX, mouseY, button);
            if (button == 1) {
                if (textItemName.isFocused()) {
                    textItemName.setText("");
                }
                if (textFlavour.isFocused()) {
                    textFlavour.setText("");
                }
            }
        }
        return clicked;
    }

    int fidgCount = 0;
    String[] fidgMessage = new String[] { "STOP!", "STOP ALREADY", "I SAID STOP!" };

    @Override
    public boolean keyTyped(char c, int keycode) {
        boolean typed = super.keyTyped(c, keycode);
        if (!typed) {
            typed = textItemName.textboxKeyTyped(c, keycode);
        }
        if (!typed) {
            typed = textFlavour.textboxKeyTyped(c, keycode);
        }
        if (typed) {
            SkinProperties skinProps = tileEntity.getSkinProps();
            String sendTextName = textItemName.getText().trim();
            String sendTextFlavour = textFlavour.getText().trim();
            if (fidgCount < 3) {
                if (sendTextName.equalsIgnoreCase("fidget spinner")) {
                    sendTextName = fidgMessage[fidgCount];
                    fidgCount++;
                }
            }

            boolean textChanged = false;
            if (!sendTextName.equals(SkinProperties.PROP_ALL_CUSTOM_NAME.getValue(skinProps))) {
                textChanged = true;
            }
            if (!sendTextFlavour.equals(SkinProperties.PROP_ALL_FLAVOUR_TEXT.getValue(skinProps))) {
                textChanged = true;
            }
            if (textChanged) {
                SkinProperties.PROP_ALL_CUSTOM_NAME.setValue(skinProps, sendTextName);
                SkinProperties.PROP_ALL_FLAVOUR_TEXT.setValue(skinProps, sendTextFlavour);
                PacketHandler.networkWrapper.sendToServer(new MessageClientGuiSetArmourerSkinProps(skinProps));
            }
        }
        return typed;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
        case 13:
            PacketHandler.networkWrapper.sendToServer(new MessageClientLoadArmour(textItemName.getText().trim(), ""));
            break;
        default:
            if (button.id == 14) {
                // Load
                // loadedArmourItem = true;
            }
            if (button.id > 0) {
                PacketHandler.networkWrapper.sendToServer(new MessageClientGuiButton((byte) button.id));
            }
            break;
        }
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        GL11.glColor4f(1, 1, 1, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(this.x, this.y, 0, 0, this.width, this.height);
        // input slot
        drawTexturedModalRect(this.x + 63, this.y + 20, 238, 0, 18, 18);
        // output slot
        drawTexturedModalRect(this.x + 142, this.y + 16, 230, 18, 26, 26);

        this.fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), this.x + 8, this.y + this.height - 96 + 2, 4210752);
        textItemName.drawTextBox();
        textFlavour.drawTextBox();
    }

    public void resetValues(SkinProperties skinProperties) {
        resetting = true;
        String newNameText = SkinProperties.PROP_ALL_CUSTOM_NAME.getValue(skinProperties);
        if (!newNameText.equals("")) {
            int cur = textItemName.getCursorPosition();
            textItemName.setText(newNameText);
            textItemName.setCursorPosition(cur);
        } else {
            textItemName.setText(newNameText);
        }
        String newFlavourText = SkinProperties.PROP_ALL_FLAVOUR_TEXT.getValue(skinProperties);
        if (!newFlavourText.equals("")) {
            int cur = textFlavour.getCursorPosition();
            textFlavour.setText(newFlavourText);
            textFlavour.setCursorPosition(cur);
        } else {
            textFlavour.setText(newFlavourText);
        }
        resetting = false;
    }

    @Override
    public void drawForegroundLayer(int mouseX, int mouseY, float partialTickTime) {
        super.drawForegroundLayer(mouseX, mouseY, partialTickTime);

        String itemNameLabel = GuiHelper.getLocalizedControlName(tileEntity.getName(), "label.itemName");

        String labelFlavour = GuiHelper.getLocalizedControlName(tileEntity.getName(), "label.flavour");
        String versionLabel = LibModInfo.RELEASE_TYPE.toString() + ": " + LibModInfo.MOD_VERSION;

        this.fontRenderer.drawString(itemNameLabel, 14, 48, 4210752);
        this.fontRenderer.drawString(labelFlavour, 14, 80, 4210752);

        int versionWidth = fontRenderer.getStringWidth(versionLabel);
        this.fontRenderer.drawString(versionLabel, this.width - versionWidth - 7, this.height - 96 + 2, 4210752);
        GlStateManager.color(1, 1, 1, 1);
        dropDownSkinType.drawForeground(mc, mouseX - x, mouseY - y, partialTickTime);

        GL11.glPushMatrix();
        GL11.glTranslatef(-x, -y, 0F);
        for (int i = 0; i < buttonList.size(); i++) {
            GuiButton button = buttonList.get(i);
            if (button instanceof GuiHelp) {
                ((GuiHelp) button).drawRollover(mc, mouseX - x, mouseY - y);
            }
        }
        GL11.glPopMatrix();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void onDropDownListChanged(GuiDropDownList dropDownList) {
        DropDownListItem listItem = dropDownList.getListSelectedItem();
        ISkinType skinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(listItem.tag);
        parent.skinTypeUpdate(skinType);
        PacketHandler.networkWrapper.sendToServer(new MessageClientGuiSetArmourerSkinType(skinType));
    }
}
