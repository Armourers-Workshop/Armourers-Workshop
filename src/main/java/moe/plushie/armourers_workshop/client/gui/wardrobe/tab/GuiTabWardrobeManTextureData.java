package moe.plushie.armourers_workshop.client.gui.wardrobe.tab;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.mojang.authlib.GameProfile;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList;
import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList.IDropDownListCallback;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTabPanel;
import moe.plushie.armourers_workshop.client.gui.wardrobe.GuiWardrobe;
import moe.plushie.armourers_workshop.common.data.type.TextureType;
import moe.plushie.armourers_workshop.common.init.entities.EntityMannequin;
import moe.plushie.armourers_workshop.common.init.entities.EntityMannequin.TextureData;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiUpdateMannequin;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTabWardrobeManTextureData extends GuiTabPanel implements IDropDownListCallback {

    private final EntityMannequin entityMannequin;
    private TextureData textureData;

    private GuiDropDownList textureTypeList;
    private GuiTextField nameTextbox;
    private GuiButtonExt setNameButton;

    private final String guiName = "wardrobe.tab.man_texture";

    public GuiTabWardrobeManTextureData(int tabId, GuiScreen parent, EntityMannequin entityMannequin) {
        super(tabId, parent);
        this.entityMannequin = entityMannequin;
    }

    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        textureData = entityMannequin.getTextureData();

        textureTypeList = new GuiDropDownList(0, 81, 25, 80, "", this);
        textureTypeList.addListItem(GuiHelper.getLocalizedControlName(guiName, "dropdown.user"), TextureType.USER.toString(), true);
        textureTypeList.addListItem(GuiHelper.getLocalizedControlName(guiName, "dropdown.url"), TextureType.URL.toString(), true);
        textureTypeList.setListSelectedIndex(0);
        if (textureData.getTextureType() == TextureType.URL) {
            textureTypeList.setListSelectedIndex(1);
        }

        nameTextbox = new GuiTextField(-1, fontRenderer, x + 81, y + 70, 165, 14);
        nameTextbox.setMaxStringLength(300);

        setupForTextureData(textureData);

        setNameButton = new GuiButtonExt(0, 81, 90, 100, 16, GuiHelper.getLocalizedControlName(guiName, "set"));

        buttonList.add(textureTypeList);
        buttonList.add(setNameButton);
    }

    private void setupForTextureData(TextureData textureData) {
        switch (textureData.getTextureType()) {
        case NONE:
            //nameTextbox.setEnabled(false);
            nameTextbox.setText("");
            break;
        case USER:
            nameTextbox.setEnabled(true);
            if (textureData.getProfile() != null) {
                nameTextbox.setText(textureData.getProfile().getName());
            }
            break;
        case URL:
            nameTextbox.setEnabled(true);
            if (!StringUtils.isNullOrEmpty(textureData.getUrl())) {
                nameTextbox.setText(textureData.getUrl());
            }
            break;
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        boolean clicked = super.mouseClicked(mouseX, mouseY, button);
        if (clicked) {
            nameTextbox.setFocused(false);
        }
        if (!clicked) {
            clicked = nameTextbox.mouseClicked(mouseX, mouseY, button);
        }
        if (!clicked) {
            if (button == 1 & nameTextbox.isFocused()) {
                nameTextbox.setText("");
            }
        }
        return clicked;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == setNameButton) {
            sendNewTextureData();
        }
    }

    @Override
    public boolean keyTyped(char c, int keycode) {
        boolean typed = nameTextbox.textboxKeyTyped(c, keycode);
        if (!typed & nameTextbox.isFocused()) {
            if (Keyboard.KEY_RETURN == keycode) {
                sendNewTextureData();
                nameTextbox.setFocused(false);
            }
        }
        return typed;
    }

    @Override
    public void update() {
        nameTextbox.updateCursorCounter();
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        nameTextbox.drawTextBox();
    }

    @Override
    public void drawForegroundLayer(int mouseX, int mouseY, float partialTickTime) {
        super.drawForegroundLayer(mouseX, mouseY, partialTickTime);
        //nameTextbox.drawTextBox();
        textureTypeList.drawForeground(mc, mouseX - x, mouseY - y, partialTickTime);
        
        // Draw entity preview.
        GL11.glPushMatrix();
        GL11.glTranslated(-x, -y, 0);
        
        ((GuiWardrobe) parent).drawPlayerPreview(x, y, mouseX, mouseY);
        GL11.glPopMatrix();
    }

    @Override
    public void onDropDownListChanged(GuiDropDownList dropDownList) {
        // sendNewTextureData();
    }

    private TextureType getSelectedTextureType() {
        return TextureType.valueOf(textureTypeList.getListSelectedItem().tag);
    }

    private void sendNewTextureData() {
        TextureType textureType = getSelectedTextureType();
        String textureString = nameTextbox.getText();
        if (StringUtils.isNullOrEmpty(textureString)) {
            textureType = TextureType.NONE;
        }
        TextureData textureData = null;
        switch (textureType) {
        case NONE:
            textureData = new TextureData();
            break;
        case USER:
            textureData = new TextureData(new GameProfile(null, textureString));
            break;
        case URL:
            textureData = new TextureData(textureString);
            break;
        }
        entityMannequin.setTextureData(textureData, false);
        MessageClientGuiUpdateMannequin message = new MessageClientGuiUpdateMannequin(entityMannequin);
        message.setTextureData(textureData);
        PacketHandler.networkWrapper.sendToServer(message);
    }
}
