package moe.plushie.armourers_workshop.library.gui.panels;

import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.gui.widget.AWComboBox;
import moe.plushie.armourers_workshop.core.gui.widget.AWSkinTypeComboBox;
import moe.plushie.armourers_workshop.core.gui.widget.AWTextField;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import moe.plushie.armourers_workshop.init.common.AWCore;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskSkinSearch.SearchColumnType;
import moe.plushie.armourers_workshop.library.data.global.task.GlobalTaskSkinSearch.SearchOrderType;
import moe.plushie.armourers_workshop.library.gui.GlobalSkinLibraryScreen.Page;
import moe.plushie.armourers_workshop.library.gui.SkinLibraryScreen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public class SearchBoxLibraryPanel extends AbstractLibraryPanel {

    private final SearchOrderType[] orderTypes = {SearchOrderType.DESC, SearchOrderType.ASC};
    private final SearchColumnType[] columnTypes = {SearchColumnType.DATE_CREATED, SearchColumnType.DATE_CREATED, SearchColumnType.NAME, SearchColumnType.NAME, SearchColumnType.DOWNLOADS, SearchColumnType.DOWNLOADS, SearchColumnType.RATING, SearchColumnType.RATING};

    private AWTextField searchText;
    private AWComboBox sortList;
    private AWSkinTypeComboBox skinTypeList;
    private ExtendedButton searchButton;

    private String keyword = "";
    private ISkinType skinType = SkinTypes.UNKNOWN;
    private SearchOrderType orderType = SearchOrderType.DESC;
    private SearchColumnType columnType = SearchColumnType.DATE_CREATED;

    public SearchBoxLibraryPanel() {
        super("inventory.armourers_workshop.skin-library-global.searchBox", Page::hasSearch);
    }

    @Override
    protected void init() {
        super.init();

        this.searchText = addTextField(leftPos + 5, topPos + 4, width - 10 - 180 - 70 - 5, 14, "typeToSearch");
        this.searchText.setValue(keyword);

        this.sortList = addSortList(leftPos + width - 180 - 70 - 5, topPos + 3, 90, 16);
        this.skinTypeList = addSkinTypeList(leftPos + width - 160, topPos + 3, 70, 16);

        this.searchButton = new ExtendedButton(leftPos + width - 84, topPos + 3, 80, 16, getDisplayText("search"), this::search);
        this.addButton(searchButton);
    }

    public void reloadData(String keyword, ISkinType skinType, SearchColumnType columnType, SearchOrderType orderType) {
        this.keyword = keyword;
        this.skinType = skinType;
        this.orderType = orderType;
        this.columnType = columnType;
        if (this.searchText == null) {
            return;
        }
        this.searchText.setValue(keyword);
        this.sortList.setSelectedIndex(getSortIndex(columnType, orderType));
        this.skinTypeList.setSelectedSkin(skinType);
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
    }

    @Override
    public void renderBackgroundLayer(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.fillGradient(matrixStack, leftPos, topPos, leftPos + width, topPos + height, 0xC0101010, 0xD0101010);
    }

    private AWTextField addTextField(int x, int y, int width, int height, String key) {
        AWTextField textField = new AWTextField(font, x, y, width, height, getDisplayText(key));
        textField.setMaxLength(255);
        textField.setReturnHandler(this::search);
        addWidget(textField);
        return textField;
    }

    private AWComboBox addSortList(int x, int y, int width, int height) {
        int selectedIndex = 0;
        ArrayList<SearchColumnType> columnTypes1 = new ArrayList<>();
        ArrayList<AWComboBox.ComboItem> items = new ArrayList<>();
        for (SearchColumnType columnType : columnTypes) {
            SearchOrderType orderType = orderTypes[columnTypes1.size() % 2];
            StringTextComponent title = new StringTextComponent("");
            if (orderType == SearchOrderType.DESC) {
                title.append("\u2191 "); // up
            } else {
                title.append("\u2193 "); // down
            }
            title.append(TranslateUtils.title("skin_search_column." + AWCore.getModId() + "." + columnType.toString().toLowerCase()));
            AWComboBox.ComboItem item = new AWComboBox.ComboItem(title);
            if (columnType == this.columnType && orderType == this.orderType) {
                selectedIndex = items.size();
            }
            items.add(item);
            columnTypes1.add(columnType);
        }
        AWComboBox comboBox = new AWComboBox(x, y, width, height, items, selectedIndex, button -> {
            int newValue = ((AWComboBox) button).getSelectedIndex();
            this.orderType = orderTypes[newValue % 2];
            this.columnType = columnTypes1.get(newValue);
            this.search(button);
        });
        addButton(comboBox);
        return comboBox;
    }

    private AWSkinTypeComboBox addSkinTypeList(int x, int y, int width, int height) {
        AWSkinTypeComboBox comboBox = new AWSkinTypeComboBox(x, y, width, height, SkinTypes.values(), this.skinType, newValue -> {
            this.skinType = newValue;
            this.search(null);
        });
        addButton(comboBox);
        return comboBox;
    }

    private int getSortIndex(SearchColumnType columnType, SearchOrderType orderType) {
        for (int i = 0; i < columnTypes.length; ++i) {
            if (columnType == columnTypes[i] && orderType == orderTypes[i % 2]) {
                return i;
            }
        }
        return 0;
    }

    private void search(Object button) {
        keyword = searchText.getValue();
        router.showSkinList(keyword, skinType, columnType, orderType);
    }
}
