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
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskSkinSearch.SearchColumnType;
import moe.plushie.armourers_workshop.common.library.global.task.GlobalTaskSkinSearch.SearchOrderType;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiGlobalLibraryPanelSearchBox extends GuiPanel implements IDropDownListCallback {

    private static final String ARROW_UP = "\u2191";
    private static final String ARROW_DOWN = "\u2193";

    private GuiLabeledTextField searchTextbox;
    private GuiDropDownList dropDownSkinType;
    private GuiDropDownList dropDownSort;

    public static ISkinType selectedSkinType;
    public static SearchColumnType searchColumnType = SearchColumnType.DATE_CREATED;
    public static SearchOrderType searchOrderType = SearchOrderType.DESC;

    public GuiGlobalLibraryPanelSearchBox(GuiGlobalLibrary parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }

    @Override
    public void initGui() {
        super.initGui();
        String guiName = ((GuiGlobalLibrary) parent).getGuiName();
        buttonList.clear();
        searchTextbox = new GuiLabeledTextField(fontRenderer, x + 5, y + 5, width - 10 - 180 - 70 - 5, 12);
        searchTextbox.setEmptyLabel(GuiHelper.getLocalizedControlName(guiName, "searchBox.typeToSearch"));
        buttonList.add(new GuiButtonExt(0, x + width - 85, y + 3, 80, 16, GuiHelper.getLocalizedControlName(guiName, "searchBox.search")));

        SkinTypeRegistry str = SkinTypeRegistry.INSTANCE;
        dropDownSkinType = new GuiDropDownList(1, x + width - 160, y + 4, 70, "", this);
        ArrayList<ISkinType> skinList = str.getRegisteredSkinTypes();
        dropDownSkinType.addListItem("*", "*", true);
        for (int i = 0; i < skinList.size(); i++) {
            ISkinType skinType = skinList.get(i);
            if (!skinType.isHidden()) {
                String skinLocalizedName = str.getLocalizedSkinTypeName(skinType);
                String skinRegistryName = skinType.getRegistryName();
                DropDownItemSkin item = new DropDownItemSkin(skinLocalizedName, skinRegistryName, skinType.enabled(), skinType);
                dropDownSkinType.addListItem(item);
                if (skinType == selectedSkinType) {
                    dropDownSkinType.setListSelectedIndex(i + 1);
                }
            }
        }
        buttonList.add(dropDownSkinType);

        dropDownSort = new GuiDropDownList(-1, x + width - 180 - 70 - 5, y + 4, 90, "", this);
        SearchColumnType[] columnTypes = { SearchColumnType.DATE_CREATED, SearchColumnType.NAME, SearchColumnType.DOWNLOADS, SearchColumnType.RATING };
        for (int i = 0; i < columnTypes.length; i++) {
            SearchColumnType columnType = columnTypes[i];
            dropDownSort.addListItem(ARROW_UP + " " + I18n.format(columnType.getLangKey()), columnType.toString(), true);
            dropDownSort.addListItem(ARROW_DOWN + " " + I18n.format(columnType.getLangKey()), columnType.toString(), true);
            if (columnType == searchColumnType) {
                dropDownSort.setListSelectedIndex(i * 2);
                if (searchOrderType == SearchOrderType.ASC) {
                    dropDownSort.setListSelectedIndex(i * 2 + 1);
                }
            }
        }
        buttonList.add(dropDownSort);
    }
    
    public void updateDropDowns(SearchColumnType searchOrderColumn, SearchOrderType searchOrder) {
        this.searchColumnType = searchOrderColumn;
        this.searchOrderType = searchOrder;
        dropDownSort.clearList();
        SearchColumnType[] columnTypes = { SearchColumnType.DATE_CREATED, SearchColumnType.NAME, SearchColumnType.DOWNLOADS, SearchColumnType.RATING };
        for (int i = 0; i < columnTypes.length; i++) {
            SearchColumnType columnType = columnTypes[i];
            dropDownSort.addListItem(ARROW_UP + " " + I18n.format(columnType.getLangKey()), columnType.toString(), true);
            dropDownSort.addListItem(ARROW_DOWN + " " + I18n.format(columnType.getLangKey()), columnType.toString(), true);
            if (columnType == searchColumnType) {
                dropDownSort.setListSelectedIndex(i * 2);
                if (searchOrderType == SearchOrderType.ASC) {
                    dropDownSort.setListSelectedIndex(i * 2 + 1);
                }
            }
        }
    }

    @Override
    public void onDropDownListChanged(GuiDropDownList dropDownList) {
        if (dropDownList == dropDownSkinType) {
            DropDownListItem listItem = dropDownList.getListSelectedItem();
            if (listItem.tag.equals("*")) {
                selectedSkinType = null;
            } else {
                selectedSkinType = SkinTypeRegistry.INSTANCE.getSkinTypeFromRegistryName(listItem.tag);
            }
        }
        if (dropDownList == dropDownSort) {
            DropDownListItem listItem = dropDownList.getListSelectedItem();
            searchColumnType = SearchColumnType.valueOf(listItem.tag);
            if (listItem.displayText.startsWith(ARROW_UP)) {
                searchOrderType = SearchOrderType.DESC;
            }
            if (listItem.displayText.startsWith(ARROW_DOWN)) {
                searchOrderType = SearchOrderType.ASC;
            }
        }
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
        ((GuiGlobalLibrary) parent).panelSearchResults.clearResults();
        String search = searchTextbox.getText();
        ((GuiGlobalLibrary) parent).switchScreen(Screen.SEARCH);
        ((GuiGlobalLibrary) parent).panelSearchResults.doSearch(search, selectedSkinType, searchColumnType, searchOrderType);
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
        if (!visible) {
            return;
        }
        super.draw(mouseX, mouseY, partialTickTime);
        searchTextbox.drawTextBox();
    }

    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        dropDownSkinType.drawForeground(mc, mouseX, mouseY, partialTickTime);
        dropDownSort.drawForeground(mc, mouseX, mouseY, partialTickTime);
    }
}
