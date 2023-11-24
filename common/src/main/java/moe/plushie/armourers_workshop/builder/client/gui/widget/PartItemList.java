package moe.plushie.armourers_workshop.builder.client.gui.widget;

import com.apple.library.coregraphics.CGRect;
import com.google.common.base.Objects;
import moe.plushie.armourers_workshop.core.client.gui.widget.SkinPreviewList;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PartItemList extends SkinPreviewList<SkinDescriptor> {

    private SkinDescriptor selectedItem;

    public PartItemList(CGRect frame) {
        super(frame);
    }

    public SkinDescriptor getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(SkinDescriptor selectedItem) {
        this.selectedItem = selectedItem;
    }

    @Override
    protected String getItemName(SkinDescriptor value) {
        return "";
    }

    @Override
    protected SkinDescriptor getItemDescriptor(SkinDescriptor value) {
        return value;
    }

    @Override
    protected int getItemBackgroundColor(SkinDescriptor entry, boolean isHovered) {
        if (Objects.equal(getSelectedItem(), entry)) {
            return 0xE0777711;
        }
        return super.getItemBackgroundColor(entry, isHovered);
    }
}
