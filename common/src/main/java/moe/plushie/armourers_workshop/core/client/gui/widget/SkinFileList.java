package moe.plushie.armourers_workshop.core.client.gui.widget;

import com.apple.library.coregraphics.CGGraphicsContext;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSIndexPath;
import com.apple.library.foundation.NSString;
import com.apple.library.impl.TooltipRenderer;
import com.apple.library.uikit.UIControl;
import com.apple.library.uikit.UIFont;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UIImageView;
import com.apple.library.uikit.UIResponder;
import com.apple.library.uikit.UIScrollView;
import com.apple.library.uikit.UITableView;
import com.apple.library.uikit.UITableViewCell;
import com.apple.library.uikit.UITableViewDataSource;
import com.apple.library.uikit.UITableViewDelegate;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.api.library.ISkinLibrary;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.core.client.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.client.render.ExtendedItemRenderer;
import moe.plushie.armourers_workshop.core.data.color.ColorScheme;
import moe.plushie.armourers_workshop.core.data.ticket.Ticket;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModTextures;
import moe.plushie.armourers_workshop.init.platform.ItemTooltipManager;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

@Environment(EnvType.CLIENT)
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
        float height = size.height - tableView.frame().getHeight();
        if (offset.y != 0 && height > 0) {
            value = offset.y / height;
        }
        scrollIndicator.setValue(value);
    }

    private Entry findEntry(ISkinLibrary.Entry entry) {
        if (entry == null) {
            return null;
        }
        for (Entry cell : cells) {
            if (cell.entry.equals(entry)) {
                return cell;
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

        private final NSString title;
        private final ISkinLibrary.Entry entry;
        private final UIFont font = UIFont.systemFont(7);

        private final UIView iconView = new UIView(CGRect.ZERO);

        private SkinDescriptor descriptor = SkinDescriptor.EMPTY;

        public Entry(ISkinLibrary.Entry entry) {
            super(CGRect.ZERO);
            this.title = new NSString(entry.getName());
            this.entry = entry;
            if (!entry.isDirectory() && entry.getSkinIdentifier() != null) {
                this.descriptor = new SkinDescriptor(entry.getSkinIdentifier(), entry.getSkinType(), ColorScheme.EMPTY);
            }
            this.iconView.setFrame(new CGRect(0, 0, 16, 14));
            this.addSubview(iconView);
        }

        @Override
        public void render(CGPoint point, CGGraphicsContext context) {
            float left = 0;
            float top = 0;
            float width = bounds().getWidth();
            float height = bounds().getHeight();

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
            iconOffset = 16;
            if (backgroundColor != 0) {
                context.fillRect(left, top, left + width, top + height, backgroundColor);
            }
            context.drawText(title, left + iconOffset + 2, top + 3, textColor);
            renderIcon(context, left, top - 1, 16, 16);
        }

        public void renderIcon(CGGraphicsContext context, float x, float y, float width, float height) {
            if (entry.isDirectory()) {
                int u = entry.isPrivateDirectory() ? 32 : 16;
                context.drawResizableImage(ModTextures.LIST, x + (width - 12) / 2f, y + (height - 12) / 2f, 12, 12, u, 0, 16, 16, 256, 256);
                return;
            }
            var descriptor = getDescriptor();
            if (descriptor.isEmpty()) {
                int u = 48;
                context.drawResizableImage(ModTextures.LIST, x + (width - 12) / 2f, y + (height - 12) / 2f, 12, 12, u, 0, 16, 16, 256, 256);
                return;
            }
            var bakedSkin = SkinBakery.getInstance().loadSkin(descriptor, loadTicket);
            if (bakedSkin == null) {
                return;
            }
            var buffers = AbstractBufferSource.buffer();
            ExtendedItemRenderer.renderSkinInGUI(bakedSkin, x, y, 100, width, height - 1, 20, 45, 0, context.state().ctm(), buffers);
        }

        public void renderTooltip(CGRect rect, CGGraphicsContext context) {
            var window = window();
            if (window == null) {
                return;
            }
            var bakedSkin = SkinBakery.getInstance().loadSkin(getDescriptor(), loadTicket);
            if (bakedSkin == null) {
                return;
            }
            var bounds = window.bounds();
            var point = convertPointToView(CGPoint.ZERO, null);
            float size = 144;
            float dx = point.x - size - 5;
            float dy = MathUtils.clamp(context.state().mousePos().getY() - size / 2f, 0, bounds.height - size);
            context.drawTilableImage(ModTextures.GUI_PREVIEW, dx, dy, size, size, 0, 0, 62, 62, 4, 4, 4, 4);

            var poseStack = context.state().ctm();
            var tooltips = ObjectUtils.map(ItemTooltipManager.createSkinInfo(bakedSkin), NSString::new);
            context.drawMultilineText(tooltips, dx + 4, dy + 4, size - 8, 0xffffffff, true, font, 0);

            var buffers = AbstractBufferSource.buffer();
            ExtendedItemRenderer.renderSkinInGUI(bakedSkin, dx, dy, 100, size, size, 30, 45, 0, poseStack, buffers);
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
