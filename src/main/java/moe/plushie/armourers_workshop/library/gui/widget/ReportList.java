package moe.plushie.armourers_workshop.library.gui.widget;


import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ReportList extends Button {

//    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.CONTROL_SKIN_PANEL);

    private final ArrayList<GuiDetailListColumn> columns = new ArrayList<>();
    private final ArrayList<GuiDetailListItem> items = new ArrayList<>();

    protected int scrollAmount;
    protected int selectedIndex;

    protected int contentHeight = 0;

    protected FontRenderer font;
    protected IEventListener listener;

    public ReportList(int xPos, int yPos, int width, int height) {
        super(xPos, yPos, width, height, StringTextComponent.EMPTY, b -> {});
        this.font = Minecraft.getInstance().font;
    }

    public void setFrame(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.contentHeight = 0;
        this.items.forEach(item -> {
            item.layout(width - 2, 10);
            contentHeight += item.contentHeight + 1;
        });
        this.setScrollAmount(scrollAmount);
    }

    public void addColumn(String name, int width) {
        columns.add(new GuiDetailListColumn(name, width));
    }

    public GuiDetailListColumn getColumn(int index) {
        if (index >= 0 & index < columns.size()) {
            return columns.get(index);
        }
        return null;
    }

    public void removeColumn(int index) {
        columns.remove(index);
    }

    public void clearColumns() {
        columns.clear();
    }

    public void addItem(String... names) {
        GuiDetailListItem item = new GuiDetailListItem(names);
        item.layout(width - 2, 10);
        items.add(item);
        contentHeight += item.contentHeight + 1;
    }

    public GuiDetailListItem getItem(int index) {
        if (index >= 0 & index < items.size()) {
            return items.get(index);
        }
        return null;
    }

    public void removeItem(int index) {
        items.remove(index);
    }

    public void clearItems() {
        items.clear();
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float p_230431_4_) {
        if (!visible) {
            return;
        }
        this.isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
//        RenderUtils.bind(WIDGETS_LOCATION);
//        GuiUtils.drawContinuousTexturedBox(matrixStack, x, y, 0, 46, width, height, 200, 20, 2, 3, 2, 2, 0);
        RenderUtils.enableScissor(x, y, width, height);
        int dy = -scrollAmount;
        for (GuiDetailListItem item : items) {
            if (RenderUtils.inScissorRect(x + 1, y + 1 + dy, item.contentWidth, item.contentHeight)) {
                matrixStack.pushPose();
                matrixStack.translate(x + 1, y + 1 + dy, 0);
                item.render(matrixStack, mouseX, mouseY, p_230431_4_);
                matrixStack.popPose();
            }
            dy += item.contentHeight + 1;
        }
        RenderUtils.disableScissor();
    }

    public int getMaxScroll() {
        return Math.max(contentHeight - height, 0);
    }

    public int getScrollAmount() {
        return scrollAmount;
    }

    public void setScrollAmount(int scrollAmount) {
        int oldScrollAmount = this.scrollAmount;
        this.scrollAmount = MathHelper.clamp(scrollAmount, 0, this.getMaxScroll());
        if (this.listener != null && oldScrollAmount != this.scrollAmount) {
            this.listener.listDidScroll(this, this.scrollAmount);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int p_231044_5_) {
        if (!isMouseOver(mouseX, mouseY)) {
            return false;
        }
        int dy = -scrollAmount;
        for (int i = 0; i < items.size(); ++i) {
            GuiDetailListItem item = items.get(i);
            int y0 = y + 1 + dy;
            if (y0 <= mouseY && mouseY < (y0 + item.contentHeight + 1)) {
                if (listener != null) {
                    listener.listDidSelect(this, i);
                }
                return true;
            }
            dy += item.contentHeight + 1;
        }
        return false;
    }

    public boolean mouseScrolled(double p_231043_1_, double p_231043_3_, double p_231043_5_) {
        this.setScrollAmount(this.getScrollAmount() - (int) (p_231043_5_ * height / 4));
        return true;
    }

    public int getContentHeight() {
        return contentHeight;
    }

    public IEventListener getListener() {
        return listener;
    }

    public void setListener(IEventListener listener) {
        this.listener = listener;
    }

    public interface IEventListener {

        void listDidSelect(ReportList reportList, int index);

        void listDidScroll(ReportList reportList, int contentOffset);
    }

    public class GuiDetailListColumn {

        private final String name;
        private int width;

        public GuiDetailListColumn(String name, int width) {
            this.name = name;
            this.width = width;
        }

        public String getName() {
            return name;
        }

        public int getWidth(int listWidth) {
            return width;
        }
    }

    public class GuiDetailListItem {

        public String[] names;

        public HashMap<Integer, List<ITextProperties>> wrappedTextLines = new HashMap<>();

        public int contentWidth = 0;
        public int contentHeight = 0;

        public GuiDetailListItem(String[] names) {
            this.names = names;
        }

        public void layout(int itemWidth, int itemHeight) {
            wrappedTextLines.clear();
            int xOffset = 0;
            for (int i = 0; i < names.length; i++) {
                int columnWidth = 10;
                String name = names[i];
                GuiDetailListColumn column = getColumn(i);
                if (column != null) {
                    columnWidth = column.getWidth(itemWidth);
                    if (columnWidth == -1) {
                        columnWidth = itemWidth - 2 - xOffset;
                    }
                    if (Strings.isNotBlank(name)) {
                        List<ITextProperties> lines = font.getSplitter().splitLines(name, columnWidth, Style.EMPTY);
                        itemHeight = Math.max(itemHeight, lines.size() * 10);
                        wrappedTextLines.put(i, lines);
                    }
                }
                xOffset += columnWidth + 1;
            }
            this.contentWidth = itemWidth;
            this.contentHeight = itemHeight;
        }

        public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            int xOffset = 0;
            for (int i = 0; i < names.length; i++) {
                int columnWidth = 10;
                GuiDetailListColumn column = getColumn(i);
                if (column != null) {
                    columnWidth = column.getWidth(contentWidth);
                    if (columnWidth == -1) {
                        columnWidth = contentWidth - 2 - xOffset;
                    }
                    fill(matrixStack, xOffset, 0, xOffset + columnWidth, contentHeight, 0xCC808080);
                    List<ITextProperties> lines = wrappedTextLines.get(i);
                    if (lines != null) {
                        int dy = 0;
                        for (ITextProperties line : lines) {
                            font.draw(matrixStack, LanguageMap.getInstance().getVisualOrder(line), 1 + xOffset, 1 + dy, 0xFFFFFF);
                            dy += 10;
                        }
                    } else {
                        font.draw(matrixStack, names[i], 1 + xOffset, 1, 0xFFFFFF);
                    }
                    xOffset += columnWidth + 1;
                }
            }
        }
    }
}
