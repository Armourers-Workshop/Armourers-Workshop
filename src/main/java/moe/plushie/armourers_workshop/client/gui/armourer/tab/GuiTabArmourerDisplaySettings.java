package moe.plushie.armourers_workshop.client.gui.armourer.tab;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.armourer.GuiArmourer;
import moe.plushie.armourers_workshop.client.gui.controls.GuiCheckBox;
import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList;
import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList.IDropDownListCallback;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTabPanel;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.client.texture.PlayerTexture;
import moe.plushie.armourers_workshop.common.data.type.TextureType;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiButton;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiSetSkin;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityArmourer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTabArmourerDisplaySettings extends GuiTabPanel implements IDropDownListCallback {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.GUI_ARMOURER);

    private final TileEntityArmourer tileEntity;

    private GuiDropDownList textureTypeList;
    private GuiTextField textUserSkin;
    private GuiCheckBox checkShowGuides;
    private GuiCheckBox checkShowHelper;

    public GuiTabArmourerDisplaySettings(int tabId, GuiScreen parent) {
        super(tabId, parent, false);
        tileEntity = ((GuiArmourer) parent).tileEntity;
    }

    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        String guiName = tileEntity.getName();

        buttonList.clear();

        checkShowGuides = new GuiCheckBox(7, 10, 110, GuiHelper.getLocalizedControlName(guiName, "showGuide"), tileEntity.isShowGuides());
        checkShowHelper = new GuiCheckBox(6, 10, 125, GuiHelper.getLocalizedControlName(guiName, "showHelper"), tileEntity.isShowHelper());

        buttonList.add(new GuiButtonExt(8, 10, 90, 80, 16, GuiHelper.getLocalizedControlName(guiName, "set")));
        textUserSkin = new GuiTextField(-1, fontRenderer, x + 10, y + 70, 120, 16);
        textUserSkin.setMaxStringLength(300);
        textUserSkin.setText(tileEntity.getTexture().getTextureString());

        textureTypeList = new GuiDropDownList(0, 10, 30, 80, "", this);
        textureTypeList.addListItem(GuiHelper.getLocalizedControlName(tileEntity.getName(), "dropdown.user"), TextureType.USER.toString(), true);
        textureTypeList.addListItem(GuiHelper.getLocalizedControlName(tileEntity.getName(), "dropdown.url"), TextureType.URL.toString(), true);
        textureTypeList.setListSelectedIndex(0);
        if (tileEntity.getTexture().getTextureType() == TextureType.URL) {
            textureTypeList.setListSelectedIndex(1);
        }

        buttonList.add(checkShowGuides);
        buttonList.add(checkShowHelper);
        buttonList.add(textureTypeList);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        boolean clicked = super.mouseClicked(mouseX, mouseY, button);
        textUserSkin.mouseClicked(mouseX, mouseY, button);
        return clicked;
    }

    @Override
    public boolean keyTyped(char c, int keycode) {
        if (textUserSkin.textboxKeyTyped(c, keycode)) {
            return true;
        }
        return false;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 8) {
            String username = textUserSkin.getText().trim();
            PacketHandler.networkWrapper.sendToServer(new MessageClientGuiSetSkin(new PlayerTexture(username, TextureType.values()[textureTypeList.getListSelectedIndex() + 1])));
        } else {
            PacketHandler.networkWrapper.sendToServer(new MessageClientGuiButton((byte) button.id));
        }
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(this.x, this.y, 0, 0, this.width, this.height);

        drawTexturedModalRect(this.x + 7, this.y + 141, 7, 3, 162, 76);

        textUserSkin.drawTextBox();

        checkShowGuides.setIsChecked(tileEntity.isShowGuides());

        if (tileEntity.getSkinType() != null) {
            checkShowHelper.visible = tileEntity.getSkinType().showHelperCheckbox();
        } else {
            checkShowHelper.visible = false;
        }
    }

    @Override
    public void drawForegroundLayer(int mouseX, int mouseY, float partialTickTime) {
        String labelSkinType = GuiHelper.getLocalizedControlName(tileEntity.getName(), "label.skinType");
        String usernameLabel = GuiHelper.getLocalizedControlName(tileEntity.getName(), "label.username");
        String urlLabel = GuiHelper.getLocalizedControlName(tileEntity.getName(), "label.url");
        this.fontRenderer.drawString(labelSkinType, 10, 20, 4210752);
        if (textureTypeList.getListSelectedIndex() == 0) {
            this.fontRenderer.drawString(usernameLabel, 10, 60, 4210752);
        } else {
            this.fontRenderer.drawString(urlLabel, 10, 60, 4210752);
        }
        super.drawForegroundLayer(mouseX, mouseY, partialTickTime);
        textureTypeList.drawForeground(mc, mouseX - x, mouseY - y, partialTickTime);
    }

    @Override
    public void onDropDownListChanged(GuiDropDownList dropDownList) {
        textUserSkin.setText("");
    }
}
