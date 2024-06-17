package moe.plushie.armourers_workshop.builder.client.gui.armourer.dialog;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UICheckBox;
import com.apple.library.uikit.UIComboBox;
import com.apple.library.uikit.UIComboItem;
import com.apple.library.uikit.UILabel;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.core.client.gui.widget.ConfirmDialog;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.ArrayList;

@Environment(EnvType.CLIENT)
public class ArmourerCopyDialog extends ConfirmDialog {

    final ArrayList<ISkinPartType> partTypes;

    private final UICheckBox mirrorCheckBox = new UICheckBox(CGRect.ZERO);
    private final UICheckBox paintCheckBox = new UICheckBox(CGRect.ZERO);

    private final UIComboBox sourcePartComboBox = new UIComboBox(new CGRect(0, 0, 100, 14));
    private final UIComboBox destinationPartComboBox = new UIComboBox(new CGRect(0, 0, 100, 14));

    public ArmourerCopyDialog(ArrayList<ISkinPartType> partTypes) {
        super();
        this.setFrame(new CGRect(0, 0, 240, 140));
        this.partTypes = partTypes;
        this.setup();
    }

    private void setup() {
        layoutIfNeeded();
        var width = bounds().width - 30;
        var left = confirmButton.frame().getX() + 1;
        var right = cancelButton.frame().getMaxX() - 1;
        var bottom = confirmButton.frame().getY() - 4;

        mirrorCheckBox.setFrame(new CGRect(left, bottom - 22, width, 9));
        mirrorCheckBox.setTitle(NSString.localizedString("armourer.dialog.copy.mirror"));
        mirrorCheckBox.setSelected(false);
        addSubview(mirrorCheckBox);

        paintCheckBox.setFrame(new CGRect(left, bottom - 11, width, 9));
        paintCheckBox.setTitle(NSString.localizedString("armourer.dialog.copy.copyPaint"));
        paintCheckBox.setSelected(false);
        addSubview(paintCheckBox);

        sourcePartComboBox.setFrame(new CGRect(left, 35, 100, 14));
        sourcePartComboBox.setSelectedIndex(0);
        sourcePartComboBox.reloadData(getItems(partTypes));
        addSubview(sourcePartComboBox);

        destinationPartComboBox.setFrame(new CGRect(right - 100, 35, 100, 14));
        destinationPartComboBox.setSelectedIndex(0);
        destinationPartComboBox.reloadData(getItems(partTypes));
        addSubview(destinationPartComboBox);

        UILabel label1 = new UILabel(new CGRect(sourcePartComboBox.frame().getX(), sourcePartComboBox.frame().getY() - 10, 100, 9));
        UILabel label2 = new UILabel(new CGRect(destinationPartComboBox.frame().getX(), destinationPartComboBox.frame().getY() - 10, 100, 9));
        label1.setText(NSString.localizedString("armourer.dialog.copy.srcPart"));
        label2.setText(NSString.localizedString("armourer.dialog.copy.desPart"));
        addSubview(label1);
        addSubview(label2);
    }

    public ISkinPartType getSourcePartType() {
        if (partTypes != null && sourcePartComboBox != null && sourcePartComboBox.selectedIndex() < partTypes.size()) {
            return partTypes.get(sourcePartComboBox.selectedIndex());
        }
        return SkinPartTypes.UNKNOWN;
    }

    public ISkinPartType getDestinationPartType() {
        if (partTypes != null && destinationPartComboBox != null && destinationPartComboBox.selectedIndex() < partTypes.size()) {
            return partTypes.get(destinationPartComboBox.selectedIndex());
        }
        return SkinPartTypes.UNKNOWN;
    }

    public boolean isMirror() {
        return mirrorCheckBox == null || mirrorCheckBox.isSelected();
    }

    public boolean isCopyPaintData() {
        return paintCheckBox == null || paintCheckBox.isSelected();
    }

    private ArrayList<UIComboItem> getItems(ArrayList<ISkinPartType> partTypes) {
        var items = new ArrayList<UIComboItem>();
        for (var partType : partTypes) {
            var title = new NSString(TranslateUtils.Name.of(partType));
            items.add(new UIComboItem(title));
        }
        return items;
    }
}
