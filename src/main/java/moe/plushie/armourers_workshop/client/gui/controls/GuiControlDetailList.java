package moe.plushie.armourers_workshop.client.gui.controls;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiControlDetailList extends GuiButtonExt {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.CONTROL_SKIN_PANEL);

    private final ArrayList<GuiControlDetailList.IGuiDetailListColumn> columns = new ArrayList<GuiControlDetailList.IGuiDetailListColumn>();
    private final ArrayList<GuiControlDetailList.IGuiDetailListItem> items = new ArrayList<GuiControlDetailList.IGuiDetailListItem>();

    protected int scrollAmount;
    protected int selectedIndex;

    public GuiControlDetailList(int xPos, int yPos, int width, int height) {
        super(-1, xPos, yPos, width, height, "");
    }

    public void setPosAndSize(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void addColumn(GuiControlDetailList.IGuiDetailListColumn column) {
        columns.add(column);
    }

    public void addColumn(String name, int width) {
        columns.add(new GuiDetailListColumn(name, width));
    }

    public GuiControlDetailList.IGuiDetailListColumn getColumn(int index) {
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

    public void addItem(GuiControlDetailList.IGuiDetailListItem item) {
        items.add(item);
    }

    public void addItem(String... names) {
        items.add(new GuiDetailListItem(names));
    }

    public GuiControlDetailList.IGuiDetailListItem getItem(int index) {
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
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!visible) {
            return;
        }
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        int k = this.getHoverState(this.hovered);
        GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, this.x, this.y, 0, 46, this.width, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
        for (int i = 0; i < items.size(); i++) {
            items.get(i).draw(mc, x + 1, y + 1 + 10 * i, mouseX, mouseY, partialTicks, this);
        }
    }

    @Override
    public void playPressSound(SoundHandler soundHandlerIn) {
    }

    public interface IGuiDetailListColumn {

        public String getName();

        public int getWidth(int listWidth);
    }

    public interface IGuiDetailListItem {

        public void draw(Minecraft mc, int x, int y, int mouseX, int mouseY, float partialTicks, GuiControlDetailList parent);
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

    public class GuiDetailListItem implements IGuiDetailListItem {

        public String[] names;

        public GuiDetailListItem(String[] names) {
            this.names = names;
        }

        @Override
        public void draw(Minecraft mc, int x, int y, int mouseX, int mouseY, float partialTicks, GuiControlDetailList parent) {
            int xOffset = 0;
            for (int i = 0; i < names.length; i++) {
                int columnWidth = 10;
                GuiControlDetailList.IGuiDetailListColumn column = parent.getColumn(i);
                if (column != null) {
                    columnWidth = column.getWidth(parent.width);
                    if (columnWidth == -1) {
                        columnWidth = parent.width - 2 - xOffset;
                    }
                    drawRect(x + xOffset, y, x + xOffset + columnWidth, y + 9, 0xCC808080);
                    mc.fontRenderer.drawString(names[i], x + 1 + xOffset, y + 1, 0xFFFFFF);
                    xOffset += columnWidth + 1;
                }
            }
        }
    }
}
