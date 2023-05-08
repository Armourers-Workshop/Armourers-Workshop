package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSIndexPath;
import com.apple.library.impl.TooltipRenderer;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UIImageView;
import com.apple.library.uikit.UIResponder;
import com.apple.library.uikit.UIScrollView;
import com.apple.library.uikit.UITableView;
import com.apple.library.uikit.UITableViewCell;
import com.apple.library.uikit.UITableViewDataSource;
import com.apple.library.uikit.UITableViewDelegate;
import com.apple.library.uikit.UIView;
import com.apple.library.uikit.UIWindow;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.library.ISkinLibrary;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.render.ExtendedItemRenderer;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.ticket.Ticket;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.ItemTooltipManager;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

@Environment(value = EnvType.CLIENT)
public class SkinFileList extends UIControl implements UITableViewDataSource, UITableViewDelegate {

    private final EntryList tableView = new EntryList(CGRect.ZERO);
    private final EntryListIndicator scrollIndicator = new EntryListIndicator(new CGRect(0, 0, 10, 100));

    private final Ticket loadTicket = Ticket.list();
    private final ArrayList<Entry> cells = new ArrayList<>();

    private Entry selectedItem;

    public SkinFileList(CGRect frame) {
        super(frame);
        this.setup();
    }

    private void setup() {
        CGRect bounds = bounds();

        UIImageView bg1 = new UIImageView(new CGRect(0, 0, bounds.width - 10, bounds.height));
        bg1.setImage(UIImage.of(ModTextures.LIST).fixed(11, 11).clip(1, 1, 1, 1).build());
        bg1.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        addSubview(bg1);

        tableView.setRowHeight(14);
        tableView.setShowsVerticalScrollIndicator(false);
        tableView.setDelegate(this);
        tableView.setDataSource(this);
        tableView.setFrame(bg1.frame().insetBy(1, 1, 1, 1));
        tableView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        addSubview(tableView);

        scrollIndicator.forwardingResponder = tableView;
        scrollIndicator.setFrame(new CGRect(bounds.width - 10, 0, 10, bounds.height));
        scrollIndicator.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin | AutoresizingMask.flexibleHeight);
        scrollIndicator.addTarget(this, Event.VALUE_CHANGED, SkinFileList::updateContentOffsetIfNeeded);
        addSubview(scrollIndicator);
    }

    public void reloadData(Collection<ISkinLibrary.Entry> entries) {
        loadTicket.invalidate();
        cells.clear();
        entries.forEach(entry -> cells.add(new Entry(entry)));
        tableView.reloadData();
        // Make sure cells in the visible rect.
        tableView.setContentOffset(tableView.contentOffset());
    }

    @Override
    public void removeFromSuperview() {
        super.removeFromSuperview();
        this.loadTicket.invalidate();
    }

    @Override
    public int tableViewNumberOfRowsInSection(UITableView tableView, int section) {
        return cells.size();
    }

    @Override
    public UITableViewCell tableViewCellForRow(UITableView tableView, NSIndexPath indexPath) {
        return cells.get(indexPath.row);
    }

    @Override
    public void tableViewDidSelectRow(UITableView tableView, NSIndexPath indexPath) {
        tableView.deselectRow(indexPath, false);
        selectedItem = getEntry(indexPath.row);
        sendEvent(Event.VALUE_CHANGED);
    }

    @Override
    public void scrollViewDidScroll(UIScrollView scrollView) {
        updateProgressIfNeeded();
    }

    @Override
    protected boolean shouldPassHighlighted() {
        return false;
    }

    @Nullable
    public ISkinLibrary.Entry getSelectedItem() {
        if (selectedItem != null) {
            return selectedItem.entry;
        }
        return null;
    }

    public void setSelectedItem(ISkinLibrary.Entry entry) {
        selectedItem = findEntry(entry);
        sendEvent(Event.VALUE_CHANGED);
    }

    public CGPoint contentOffset() {
        return tableView.contentOffset();
    }

    public void setContentOffset(CGPoint contentOffset) {
        tableView.setContentOffset(contentOffset);
    }

    private void updateContentOffsetIfNeeded(UIControl sender) {
        CGPoint offset = tableView.contentOffset();
        CGSize size = tableView.contentSize();
        int value = (int) ((size.height - tableView.frame().getHeight()) * scrollIndicator.value());
        if (offset.y != value) {
            tableView.setContentOffset(new CGPoint(offset.x, value));
        }
    }

    private void updateProgressIfNeeded() {
        CGPoint offset = tableView.contentOffset();
        CGSize size = tableView.contentSize();
        float value = 0;
        int height = size.height - tableView.frame().getHeight();
        if (offset.y != 0 && height > 0) {
            value = offset.y / (float) height;
        }
        scrollIndicator.setValue(value);
    }

    private Entry findEntry(ISkinLibrary.Entry entry) {
        if (entry == null) {
            return null;
        }
        for (Entry entry1 : cells) {
            if (entry1.entry.equals(entry)) {
                return entry1;
            }
        }
        return null;
    }

    public ISkinLibrary.Entry getItem(int index) {
        Entry entry = getEntry(index);
        if (entry != null) {
            return entry.entry;
        }
        return null;
    }

    private Entry getEntry(int index) {
        if (index >= 0 && index < cells.size()) {
            return cells.get(index);
        }
        return null;
    }

    public class Entry extends UITableViewCell {

        private final Font font;
        private final Component title;
        private final ISkinLibrary.Entry entry;

        private final UIView iconView = new UIView(CGRect.ZERO);

        private SkinDescriptor descriptor = SkinDescriptor.EMPTY;

        public Entry(ISkinLibrary.Entry entry) {
            super(CGRect.ZERO);
            this.title = Component.literal(entry.getName());
            this.font = Minecraft.getInstance().font;
            this.entry = entry;
            if (!entry.isDirectory()) {
                this.descriptor = new SkinDescriptor(entry.getSkinIdentifier(), entry.getSkinType(), ColorScheme.EMPTY);
            }
            this.iconView.setFrame(new CGRect(0, 0, 16, 14));
            this.addSubview(iconView);
        }

        @Override
        public void render(CGPoint point, CGGraphicsContext context) {
            int left = 0;
            int top = 0;
            int width = bounds().getWidth();
            int height = bounds().getHeight();

            int textColor = 0xffaaaaaa;
            int backgroundColor = 0;
            int iconOffset = 0;

            if (isHighlighted()) {
                textColor = 0xff000000;
                backgroundColor = 0xffcccccc;
            }
            if (entry.isDirectory()) {
                textColor = 0xff88ff88;
            }
            if (entry.isPrivateDirectory()) {
                textColor = 0xff8888ff;
            }
            if (selectedItem == this) {
                textColor = 0xff000000;
                backgroundColor = 0xffffff88;
            }
            if (entry.isDirectory() || !getDescriptor().isEmpty()) {
                iconOffset = 16;
            }
            if (backgroundColor != 0) {
                context.fillRect(left, top, left + width, top + height, backgroundColor);
            }
            context.drawText(title.getVisualOrderText(), left + iconOffset + 2, top + 3, textColor);
            renderIcon(context.poseStack, left, top - 1, 16, 16);
        }

        public void renderIcon(PoseStack poseStack, int x, int y, int width, int height) {
            if (entry.isDirectory()) {
                int u = entry.isPrivateDirectory() ? 32 : 16;
                RenderSystem.blit(poseStack, x + (width - 12) / 2, y + (height - 12) / 2 - 1, u, 0, 12, 12, ModTextures.LIST);
                return;
            }
            BakedSkin bakedSkin = SkinBakery.getInstance().loadSkin(getDescriptor(), loadTicket);
            if (bakedSkin == null) {
                return;
            }
            MultiBufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
            ExtendedItemRenderer.renderSkinInBox(bakedSkin, ColorScheme.EMPTY, ItemStack.EMPTY, x, y, 100, width, height - 1, 20, 45, 0, poseStack, buffers);
        }

        public void renderTooltip(CGRect rect, CGGraphicsContext context) {
            UIWindow window = window();
            if (window == null) {
                return;
            }
            BakedSkin bakedSkin = SkinBakery.getInstance().loadSkin(getDescriptor(), loadTicket);
            if (bakedSkin == null) {
                return;
            }
            CGRect bounds = window.bounds();
            CGPoint point = convertPointToView(CGPoint.ZERO, null);
            int size = 144;
            int mouseY = context.mouseY;
            int dz = 0;
            int dx = point.x - size - 5;
            int dy = MathUtils.clamp(mouseY - size / 2, 0, bounds.height - size);
            Font font = context.font.font();
            PoseStack poseStack = context.poseStack;
            ArrayList<FormattedText> tooltips = new ArrayList<>(ItemTooltipManager.createSkinInfo(bakedSkin));
            RenderSystem.drawContinuousTexturedBox(poseStack, ModTextures.GUI_PREVIEW, dx, dy, 0, 0, size, size, 62, 62, 4, dz);
            RenderSystem.drawShadowText(poseStack, tooltips, dx + 4, dy + 4, size - 8, dz, font, 7, 0xffffff);
            MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
            ExtendedItemRenderer.renderSkinInBox(bakedSkin, ColorScheme.EMPTY, ItemStack.EMPTY, dx, dy, dz + 100, size, size, 30, 45, 0, 0, 0xf000f0, poseStack, buffers);
            buffers.endBatch();
        }

        @Override
        public void didMoveToWindow() {
            super.didMoveToWindow();
            if (!descriptor.isEmpty() && window() != null) {
                iconView.setTooltip((TooltipRenderer) this::renderTooltip);
            } else {
                iconView.setTooltip(null);
            }
        }

        public SkinDescriptor getDescriptor() {
            if (ModConfig.Common.allowLibraryPreviews) {
                return descriptor;
            }
            return SkinDescriptor.EMPTY;
        }
    }

    public static class EntryList extends UITableView {

        public EntryList(CGRect frame) {
            super(frame);
        }

        @Override
        public void layerDidDraw(CGGraphicsContext context) {
            super.layerDidDraw(context);
            // the table view will enable clip, so we need to submit all the data immediately.
            Minecraft.getInstance().renderBuffers().bufferSource().endBatch();
        }
    }

    public static class EntryListIndicator extends ScrollIndicator {

        public UIView forwardingResponder;

        public EntryListIndicator(CGRect frame) {
            super(frame);
        }

        @Override
        public UIResponder nextResponder() {
            // we need forwarding chain responder to the target,
            // to ensure that all events are handled correctly.
            if (forwardingResponder != null) {
                return forwardingResponder;
            }
            return super.nextResponder();
        }
    }
}
