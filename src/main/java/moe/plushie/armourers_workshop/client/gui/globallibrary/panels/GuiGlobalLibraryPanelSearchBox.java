package moe.plushie.armourers_workshop.client.gui.globallibrary.panels;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.armourer.tab.GuiTabArmourerMain.DropDownItemSkin;
import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList;
import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList.DropDownListItem;
import moe.plushie.armourers_workshop.client.gui.controls.GuiDropDownList.IDropDownListCallback;
import moe.plushie.armourers_workshop.client.gui.controls.GuiLabeledTextField;
import moe.plushie.armourers_workshop.client.gui.controls.GuiPanel;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary.Screen;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiGlobalLibraryPanelSearchBox extends GuiPanel implements IDropDownListCallback {
    
    private GuiLabeledTextField searchTextbox;
    private GuiDropDownList dropDownList;
    public static ISkinType selectedSkinType;
    
    public GuiGlobalLibraryPanelSearchBox(GuiGlobalLibrary parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        String guiName = ((GuiGlobalLibrary)parent).getGuiName();
        buttonList.clear();
        searchTextbox = new GuiLabeledTextField(fontRenderer, x + 5, y + 5, width - 10 - 160, 12);
        searchTextbox.setEmptyLabel(GuiHelper.getLocalizedControlName(guiName, "searchBox.typeToSearch"));
        buttonList.add(new GuiButtonExt(0, x + width - 85, y + 3, 80, 16, GuiHelper.getLocalizedControlName(guiName, "searchBox.search")));
        
        SkinTypeRegistry str = SkinTypeRegistry.INSTANCE;
        dropDownList = new GuiDropDownList(1, x + width - 160, y + 4, 70, "", this);
        ArrayList<ISkinType> skinList = str.getRegisteredSkinTypes();
        dropDownList.addListItem("*");
        for (int i = 0; i < skinList.size(); i++) {
            ISkinType skinType = skinList.get(i);
            if (!skinType.isHidden()) {
                String skinLocalizedName = str.getLocalizedSkinTypeName(skinType);
                String skinRegistryName = skinType.getRegistryName();
                DropDownItemSkin item = new DropDownItemSkin(skinLocalizedName, skinRegistryName, skinType.enabled(), skinType);
                dropDownList.addListItem(item);
                if (skinType == selectedSkinType) {
                    dropDownList.setListSelectedIndex(i + 1);
                }
            }
        }
        buttonList.add(dropDownList);
    }
    
    @Override
    public void onDropDownListChanged(GuiDropDownList dropDownList) {
        DropDownListItem listItem = dropDownList.getListSelectedItem();
        selectedSkinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(listItem.tag);
    }
    
    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (!visible | !enabled) {
            return false;
        }
        boolean clicked = super.mouseClicked(mouseX, mouseY, button);
        if (!clicked) {
            clicked = searchTextbox.mouseClicked(mouseX, mouseY, button);
            if (button == 1) {
                if (searchTextbox.isFocused()) {
                    searchTextbox.setText("");
                }
                return true;
            }
        }
        return clicked;
    }
    
    @Override
    public boolean keyTyped(char c, int keycode) {
        if (!visible | !enabled) {
            return false;
        }
        boolean pressed = searchTextbox.textboxKeyTyped(c, keycode);
        if (keycode == 28) {
            doSearch();
        }
        return pressed;
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            doSearch();
        }
    }
    
    private void doSearch() {
        ((GuiGlobalLibrary)parent).panelSearchResults.clearResults();
        String search = searchTextbox.getText();
        ((GuiGlobalLibrary)parent).switchScreen(Screen.SEARCH);
        ((GuiGlobalLibrary)parent).panelSearchResults.doSearch(search, selectedSkinType);
    }
    
    @Override
    public void drawBackground(int mouseX, int mouseY, float partialTickTime) {
        if (!visible) {
            return;
        }
        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);
    }
    
    @Override
    public void draw(int mouseX, int mouseY, float partialTickTime) {
        super.draw(mouseX, mouseY, partialTickTime);
        searchTextbox.drawTextBox();
    }
    
    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        dropDownList.drawForeground(mc, mouseX, mouseY, partialTickTime);
    }
}
