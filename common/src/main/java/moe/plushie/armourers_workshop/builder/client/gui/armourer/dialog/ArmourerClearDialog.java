package moe.plushie.armourers_workshop.builder.client.gui.armourer.dialog;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UICheckBox;
import com.apple.library.uikit.UIComboBox;
import com.apple.library.uikit.UIComboItem;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.client.gui.widget.ConfirmDialog;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;

@Environment(value = EnvType.CLIENT)
public class ArmourerClearDialog extends ConfirmDialog {

    final ArrayList<ISkinPartType> partTypes;
    private final UIComboBox partComboBox = new UIComboBox(new CGRect(0, 0, 80, 14));
    private final UICheckBox blockCheckBox = new UICheckBox(CGRect.ZERO);
    private final UICheckBox paintCheckBox = new UICheckBox(CGRect.ZERO);
    private final UICheckBox markersCheckBox = new UICheckBox(CGRect.ZERO);

    public ArmourerClearDialog(ArrayList<ISkinPartType> partTypes) {
        super();
        this.setFrame(new CGRect(0, 0, 240, 140));
        this.partTypes = partTypes;
        this.setup();
    }

    private void setup() {
        layoutIfNeeded();
        int width = bounds().width - 30;
        int left = confirmButton.frame().getX() + 1;
        int bottom = confirmButton.frame().getY() - 4;

        blockCheckBox.setFrame(new CGRect(left, bottom - 22, width, 9));
        blockCheckBox.setTitle(getText("clearBlocks"));
        blockCheckBox.setSelected(true);
        addSubview(blockCheckBox);

        paintCheckBox.setFrame(new CGRect(left, bottom - 11, width, 9));
        paintCheckBox.setTitle(getText("clearPaint"));
        paintCheckBox.setSelected(true);
        addSubview(paintCheckBox);

        markersCheckBox.setFrame(new CGRect(left, bottom - 33, width, 9));
        markersCheckBox.setTitle(getText("clearMarkers"));
        markersCheckBox.setSelected(true);
        addSubview(markersCheckBox);

        partComboBox.setFrame(new CGRect(left, 20, 80, 14));
        partComboBox.setSelectedIndex(0);
        partComboBox.reloadData(getItems(partTypes));
        addSubview(partComboBox);
    }

    public boolean isClearBlocks() {
        return blockCheckBox == null || blockCheckBox.isSelected();
    }

    public boolean isClearPaints() {
        return paintCheckBox == null || paintCheckBox.isSelected();
    }

    public boolean isClearMarkers() {
        return markersCheckBox == null || markersCheckBox.isSelected();
    }

    public ISkinPartType getSelectedPartType() {
        if (partTypes != null && partComboBox != null && partComboBox.selectedIndex() < partTypes.size()) {
            return partTypes.get(partComboBox.selectedIndex());
        }
        return SkinPartTypes.UNKNOWN;
    }

    private ArrayList<UIComboItem> getItems(ArrayList<ISkinPartType> partTypes) {
        ArrayList<UIComboItem> items = new ArrayList<>();
        for (ISkinPartType partType : partTypes) {
            NSString title;
            if (partType != SkinPartTypes.UNKNOWN) {
                title = new NSString(TranslateUtils.Name.of(partType));
            } else {
                title = new NSString("*");
            }
            items.add(new UIComboItem(title));
        }
        return items;
    }

    private NSString getText(String key) {
        return new NSString(TranslateUtils.title("inventory.armourers_workshop.armourer.dialog.clear" + "." + key));
    }
}
