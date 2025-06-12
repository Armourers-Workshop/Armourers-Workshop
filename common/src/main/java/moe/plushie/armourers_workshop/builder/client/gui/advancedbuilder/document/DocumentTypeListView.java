package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSIndexPath;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIComboItem;
import com.apple.library.uikit.UIImage;
import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.builder.client.gui.widget.NewComboBox;
import moe.plushie.armourers_workshop.builder.client.gui.widget.NewComboItem;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocumentType;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocumentTypes;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;

import java.util.ArrayList;

public class DocumentTypeListView extends NewComboBox {

    private final ArrayList<AdvancedTypeSection> allSections = new ArrayList<>();

    public DocumentTypeListView(CGRect frame) {
        super(frame);
        SkinDocumentTypes.forEach((category, items) -> {
            var section = new AdvancedTypeSection(category);
            items.forEach(section::add);
            allSections.add(section);
        });
    }


    public void reloadData() {
        super.reloadData(allSections);
    }

    public SkinDocumentType selectedType() {
        var indexPath = super.selectedIndex();
        var section = allSections.get(indexPath.section);
        var item = section.get(indexPath.row);
        if (item != null) {
            return item.category;
        }
        return null;
    }

    public void setSelectedType(SkinDocumentType type) {
        super.setSelectedIndex(findCategoryIndexPath(type));
    }

    private NSIndexPath findCategoryIndexPath(SkinDocumentType category) {
        for (int j = 0; j < allSections.size(); ++j) {
            var section = allSections[j];
            for (int i = 0; i < section.size(); ++i) {
                var item = section.get(i);
                if (item.category == category) {
                    return new NSIndexPath(i, j);
                }
            }
        }
        return new NSIndexPath(0, 0);
    }


    public static class AdvancedTypeSection extends NewComboItem {

        public AdvancedTypeSection(String category) {
            super(NSString.localizedTableString("documentType", "category." + category));
        }

        public AdvancedTypeItem add(SkinDocumentType category) {
            var skinType = category.skinType();
            var rl = ArmourersWorkshop.getItemIcon(skinType);
            var icon = UIImage.of(rl).resize(12, 12, 16, 16).limit(16, 16).build();
            var item = new AdvancedTypeItem(icon, category);
            add(item);
            return item;
        }

        @Override
        public AdvancedTypeItem get(int index) {
            return (AdvancedTypeItem) super.get(index);
        }
    }

    public static class AdvancedTypeItem extends UIComboItem {

        private final SkinDocumentType category;

        public AdvancedTypeItem(UIImage icon, SkinDocumentType category) {
            super(icon, new NSString(TranslateUtils.Name.of(category.skinType())));
            this.category = category;
        }

        public SkinType getSkinType() {
            return category.skinType();
        }
    }
}
