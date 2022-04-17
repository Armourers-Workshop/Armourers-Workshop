package moe.plushie.armourers_workshop.library.gui.widget;


import com.mojang.blaze3d.matrix.MatrixStack;
import moe.plushie.armourers_workshop.core.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public class ReportList extends Button {

//    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.CONTROL_SKIN_PANEL);

    private final ArrayList<IGuiDetailListColumn> columns = new ArrayList<IGuiDetailListColumn>();
    private final ArrayList<IRenderable> items = new ArrayList<>();

    protected int scrollAmount;
    protected int selectedIndex;

    protected FontRenderer font;

    public ReportList(int xPos, int yPos, int width, int height) {
        super(xPos, yPos, width, height, StringTextComponent.EMPTY, b -> {});
        this.font = Minecraft.getInstance().font;
    }

    public void setFrame(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void addColumn(IGuiDetailListColumn column) {
        columns.add(column);
    }

    public void addColumn(String name, int width) {
        columns.add(new GuiDetailListColumn(name, width));
    }

    public IGuiDetailListColumn getColumn(int index) {
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

    public void addItem(IRenderable item) {
        items.add(item);
    }

    public void addItem(String... names) {
        items.add(new GuiDetailListItem(names));
    }

    public IRenderable getItem(int index) {
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
        RenderUtils.bind(WIDGETS_LOCATION);
        GuiUtils.drawContinuousTexturedBox(matrixStack, x, y, 0, 46, width, height, 200, 20, 2, 3, 2, 2, 0);
        int dy = 0;
        for (IRenderable item : items) {
            matrixStack.pushPose();
            matrixStack.translate(x + 1, y + 1 + dy, 0);
            item.render(matrixStack, mouseX, mouseY, p_230431_4_);
            matrixStack.popPose();
            dy += 10;
        }
    }

    public interface IGuiDetailListColumn {

        String getName();

        int getWidth(int listWidth);
    }

    public class GuiDetailListColumn implements IGuiDetailListColumn {

        private final String name;
        private int width;

        public GuiDetailListColumn(String name, int width) {
            this.name = name;
            this.width = width;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getWidth(int listWidth) {
            return width;
        }
    }

    public class GuiDetailListItem implements IRenderable {

        public String[] names;

        public GuiDetailListItem(String[] names) {
            this.names = names;
        }

        @Override
        public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            int xOffset = 0;
            for (int i = 0; i < names.length; i++) {
                int columnWidth = 10;
                IGuiDetailListColumn column = getColumn(i);
                if (column != null) {
                    columnWidth = column.getWidth(width);
                    if (columnWidth == -1) {
                        columnWidth = width - 2 - xOffset;
                    }
                    fill(matrixStack, xOffset, 0, xOffset + columnWidth, y + 9, 0xCC808080);
                    font.draw(matrixStack, names[i], 1 + xOffset, 1, 0xFFFFFF);
                    xOffset += columnWidth + 1;
                }
            }
        }
    }
}
