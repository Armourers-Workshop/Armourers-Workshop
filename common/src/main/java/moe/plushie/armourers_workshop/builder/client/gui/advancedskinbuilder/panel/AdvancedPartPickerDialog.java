package moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder.panel;

import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.core.client.gui.widget.ConfirmDialog;

import java.util.function.Function;

public class AdvancedPartPickerDialog extends ConfirmDialog {

    public static Function<UIView, AdvancedPartPickerDialog> SHH = v -> new AdvancedPartPickerDialog();


}
