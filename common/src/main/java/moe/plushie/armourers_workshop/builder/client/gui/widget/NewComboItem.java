package moe.plushie.armourers_workshop.builder.client.gui.widget;

import com.apple.library.foundation.NSString;
import com.apple.library.uikit.UIComboItem;

import java.util.ArrayList;

public class NewComboItem {

    private final NSString name;
    private final ArrayList<UIComboItem> items = new ArrayList<>();

    public NewComboItem(NSString name) {
        this.name = name;
    }

    public void add(UIComboItem item) {
        items.add(item);
    }

    public void remove(UIComboItem item) {
        items.remove(item);
    }

    public UIComboItem get(int index) {
        return items.get(index);
    }

    public NSString getTitle() {
        return name;
    }

    public int size() {
        return items.size();
    }

}
