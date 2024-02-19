package moe.plushie.armourers_workshop.builder.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGRect;
import com.google.common.base.Objects;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.core.client.gui.widget.SkinPreviewList;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.ModTextures;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public class PartItemList extends SkinPreviewList<PartItem> {

    private SkinDescriptor selectedItem;

    public PartItemList(CGRect frame) {
        super(frame);
    }

    @Override
    public void renderItemContent(float x, float y, float width, float height, boolean isHovered, PartItem entry, IBufferSource bufferSource, CGGraphicsContext context) {
        setTooltip(getItemTooltip(entry, isHovered));
        if (entry == PartItem.CLEAR) {
            context.drawResizableImage(ModTextures.SKIN_PANEL, x, y, width, height, 224, 0, 32, 32, 256, 256);
            return;
        }
        if (entry == PartItem.IMPORT) {
            context.drawResizableImage(ModTextures.SKIN_PANEL, x, y, width, height, 224, 32, 32, 32, 256, 256);
            return;
        }
        super.renderItemContent(x, y, width, height, isHovered, entry, bufferSource, context);
    }

    public SkinDescriptor getSelectedItem() {
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
        return value.getDescriptor();
    }

    @Override
    protected int getItemBackgroundColor(PartItem entry, boolean isHovered) {
        if (Objects.equal(getSelectedItem(), entry.getDescriptor()) && entry.hasSkin()) {
            return 0xE0777711;
        }
        return super.getItemBackgroundColor(entry, isHovered);
    }

    protected ItemStack getItemTooltip(PartItem entry, boolean isHovered) {
        if (isHovered && entry.hasItem()) {
            return entry.getItemStack();
        }
        return null;
    }
}
