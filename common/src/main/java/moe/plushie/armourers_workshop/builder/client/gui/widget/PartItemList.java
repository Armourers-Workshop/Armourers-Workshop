package moe.plushie.armourers_workshop.builder.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGRect;
import moe.plushie.armourers_workshop.core.client.gui.widget.SkinPreviewList;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.init.ModTextures;
import net.minecraft.world.item.ItemStack;

public class PartItemList extends SkinPreviewList<PartItem> {

    private SkinDescriptor selectedItem;

    public PartItemList(CGRect frame) {
        super(frame);
    }

    @Override
    public void renderItemContent(float x, float y, float width, float height, float contentScale, PartItem entry, CGGraphicsContext context) {
        setTooltip(getItemTooltip(entry, contentScale > 1.0f));
        if (entry == PartItem.CLEAR) {
            context.drawResizableImage(ModTextures.SKIN_PANEL, x, y, width, height, 224, 0, 32, 32, 256, 256);
            return;
        }
        if (entry == PartItem.IMPORT) {
            context.drawResizableImage(ModTextures.SKIN_PANEL, x, y, width, height, 224, 32, 32, 32, 256, 256);
            return;
        }
        super.renderItemContent(x, y, width, height, contentScale, entry, context);
    }

    public SkinDescriptor selectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(SkinDescriptor selectedItem) {
        this.selectedItem = selectedItem;
    }

    @Override
    protected String getItemName(PartItem value) {
        return "";
    }

    @Override
    protected SkinDescriptor getItemDescriptor(PartItem value) {
        return value.descriptor();
    }

    @Override
    protected int getItemBackgroundColor(PartItem entry, boolean isHovered) {
        if (Objects.equals(selectedItem(), entry.descriptor()) && entry.hasSkin()) {
            return 0xe0777711;
        }
        return super.getItemBackgroundColor(entry, isHovered);
    }

    protected ItemStack getItemTooltip(PartItem entry, boolean isHovered) {
        if (isHovered && entry.hasItem()) {
            return entry.itemStack();
        }
        return null;
    }
}
