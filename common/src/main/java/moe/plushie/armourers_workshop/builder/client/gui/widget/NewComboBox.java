package moe.plushie.armourers_workshop.builder.client.gui.widget;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.uikit.UIControl;

import java.util.function.Function;

//
//  /---------------------------\
//  | [v] General - Outfit      |
//  +---------------------------|
//  | General                   |
//  |   [v] Head                |
//  |   [v] Chest               |
//  |   [v] Leg                 |
//  |   [v] Feet                |
//  |   [v] Wings               |
//  |   [v] Outfit              |
//  | Horse                     |
//  |   [v] Head                |
//  |   [v] Chest               |
//  |   [v] Leg                 |
//  |   [v] Feet                |
//  | Ironman                   |
//  |   [v] Head                |
//  |   [v] Chest               |
//  |   [v] Leg                 |
//  |   [v] Feet                |
//  |                           |
//  \---------------------------/
//
//

public class NewComboBox extends UIControl {

    public static Function<CGRect, NewComboBox> CR = NewComboBox::new;;

    public NewComboBox(CGRect frame) {
        super(frame);
    }
}
