package riskyken.armourersWorkshop.client.gui.mannequin;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiDropDownList;
import riskyken.armourersWorkshop.client.gui.controls.GuiDropDownList.IDropDownListCallback;
import riskyken.armourersWorkshop.client.gui.controls.GuiTabPanel;
import riskyken.armourersWorkshop.common.data.Rectangle_I_2D;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin.TextureType;

public class GuiMannequinTabTexture extends GuiTabPanel implements IDropDownListCallback {
    
    private final TileEntityMannequin tileEntity;
    public GuiDropDownList textureTypeList;
    public GuiTextField nameTextbox;
    private GuiButtonExt setNameButton;
    
    public GuiMannequinTabTexture(int tabId, GuiScreen parent, TileEntityMannequin tileEntity) {
        super(tabId, parent, true);
        this.tileEntity = tileEntity;
    }

    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        textureTypeList = new GuiDropDownList(0, width / 2 - 110, 25, 50, "", this);
        textureTypeList.addListItem(GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "dropdown.user"), TextureType.USER.toString(), true);
        textureTypeList.addListItem(GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "dropdown.url"), TextureType.URL.toString(), true);
        textureTypeList.setListSelectedIndex(tileEntity.getTextureType().ordinal());
        nameTextbox = new GuiTextField(fontRenderer, width / 2 - 110 + 55, 25, 165, 14);
        nameTextbox.setMaxStringLength(200);
        if (tileEntity.getTextureType() == TextureType.USER) {
            if (tileEntity.getGameProfile() != null) {
                nameTextbox.setText(tileEntity.getGameProfile().getName());
            }
        } else {
            if (tileEntity.getImageUrl() != null) {
                nameTextbox.setText(tileEntity.getImageUrl());
            }
        }

        setNameButton = new GuiButtonExt(0, width / 2 + 60, 45, 50, 14, GuiHelper.getLocalizedControlName(tileEntity.getInventoryName(), "set"));
        
        buttonList.add(textureTypeList);
        buttonList.add(setNameButton);
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        Rectangle_I_2D rec = new Rectangle_I_2D(0, 0, 240, 68);
        rec.x = width / 2 - rec.width / 2;
        GuiUtils.drawContinuousTexturedBox(rec.x, rec.y, 0, 200, rec.width, rec.height, 38, 38, 4, zLevel);
    }
    
    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        if (button == 1) {
            nameTextbox.setText("");
        } else {
            nameTextbox.mouseClicked(mouseX, mouseY, button);
        }
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == setNameButton) {
            ((GuiMannequin)parent).tabOffset.sendData();
        }
    }
    
    @Override
    public boolean keyTyped(char c, int keycode) {
        return nameTextbox.textboxKeyTyped(c, keycode);
    }
    
    @Override
    public void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        nameTextbox.drawTextBox();
    }

    @Override
    public void onDropDownListChanged(GuiDropDownList dropDownList) {
        ((GuiMannequin)parent).tabOffset.sendData();
    }
}
