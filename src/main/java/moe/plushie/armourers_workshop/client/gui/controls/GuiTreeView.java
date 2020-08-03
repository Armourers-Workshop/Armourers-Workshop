package moe.plushie.armourers_workshop.client.gui.controls;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTreeView extends GuiButtonExt {

    private final ArrayList<IGuiTreeViewItem> rootItems = new ArrayList<IGuiTreeViewItem>();
    private int selectedIndex = -1;
    private IGuiTreeViewCallback callback;

    private long lastClickTime;
    private int lastClickIndex = -1;

    public GuiTreeView(int xPos, int yPos, int width, int height) {
        super(-1, xPos, yPos, width, height, "");
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        int index = getIndexAsPos(mouseX, mouseY);
        if (index >= 0 & index < getTotalItems(rootItems)) {
            selectedIndex = index;
        } else {
            selectedIndex = -1;
        }
        sendCallback();
        if (lastClickTime + 500L > System.currentTimeMillis()) {
            if (lastClickIndex == selectedIndex) {
                IGuiTreeViewItem item = getSelectedItem();
                if (item != null) {
                    item.setExpanded(!item.isExpanded());
                    resetIndexes();
                }
            }
            lastClickTime = 0;
        } else {
            lastClickTime = System.currentTimeMillis();
            lastClickIndex = selectedIndex;
        }
        super.mouseReleased(mouseX, mouseY);
    }
    
    public void setCallback(IGuiTreeViewCallback callback) {
        this.callback = callback;
    }

    private void sendCallback() {
        if (callback != null) {
            callback.onSelectionChange(this, getSelectedItem());
        }
    }

    public int getSelectedIndex() {
        if (selectedIndex >= 0 & selectedIndex < getTotalItems(rootItems)) {
            return selectedIndex;
        }
        return -1;
    }

    public GuiTreeView.IGuiTreeViewItem getSelectedItem() {
        return getItemFromIndex(getSelectedIndex());
    }

    public GuiTreeView.IGuiTreeViewItem getItemFromIndex(int index) {
        ArrayList<IGuiTreeViewItem> fullList = getFullItemList();
        if (index >= 0 & index < fullList.size()) {
            return fullList.get(index);
        }
        return null;
    }

    public ArrayList<IGuiTreeViewItem> getFullItemList() {
        ArrayList<IGuiTreeViewItem> buildList = new ArrayList<GuiTreeView.IGuiTreeViewItem>();
        getFullItemList(rootItems, buildList);
        return buildList;
    }

    private void getFullItemList(ArrayList<IGuiTreeViewItem> items, ArrayList<IGuiTreeViewItem> buildList) {
        for (IGuiTreeViewItem item : items) {
            buildList.add(item);
            if (item.isExpanded()) {
                getFullItemList(item.getSubItems(), buildList);
            }
        }
    }
    
    public void removeSelectedItem() {
        removeItem(getSelectedIndex());
    }

    public void removeItem(int index) {
        removeItem(rootItems, index);
        resetIndexes();
        sendCallback();
    }

    private void removeItem(ArrayList<IGuiTreeViewItem> items, int index) {
        for (int i = 0; i < items.size(); i++) {
            IGuiTreeViewItem item = items.get(i);
            if (item.getIndex() == index) {
                if (!item.isLocked()) {
                    items.remove(i);
                }
                return;
            }
            if (item.isExpanded()) {
                removeItem(item.getSubItems(), index);
            }
        }
    }

    public void addItem(IGuiTreeViewItem item, int index) {
        IGuiTreeViewItem parentItem = getSelectedItem();
        if (parentItem != null) {
            parentItem.getSubItems().add(item);
            resetIndexes();
            sendCallback();
        }
    }

    public void addItem(IGuiTreeViewItem item) {
        rootItems.add(item);
        resetIndexes();
        sendCallback();
    }

    private int getIndexAsPos(int mouseX, int mouseY) {
        return (mouseY - x - 3) / 10;
    }

    private int getTotalItems(ArrayList<IGuiTreeViewItem> items) {
        int count = 0;
        for (IGuiTreeViewItem item : items) {
            count += getTotalItems(item.getSubItems());
            count++;
        }
        return count;
    }

    private void resetIndexes() {
        resetIndexes(new Integer(0), rootItems);
    }

    private int resetIndexes(int count, ArrayList<IGuiTreeViewItem> items) {
        int total = 0;
        for (IGuiTreeViewItem item : items) {
            item.setIndex(total + count);
            total++;
            if (item.isExpanded()) {
                int subCount = resetIndexes(total + count, item.getSubItems());
                total += subCount;
            }
        }
        return total;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial) {
        if (!visible) {
            return;
        }
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        int k = this.getHoverState(this.hovered);
        GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, this.x, this.y, 0, 46, this.width, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
        drawListItems(mc, x + 3, y + 3, rootItems);

        if (selectedIndex >= 0 & selectedIndex < getTotalItems(rootItems)) {
            drawRect(x + 2, y + 2 + selectedIndex * 10, x + width - 2, y + 2 + 10 * (selectedIndex + 1), 0x44FFFF00);
        }

        int index = getIndexAsPos(mouseX, mouseY);
        if (hovered & index >= 0 & index < getTotalItems(rootItems)) {
            drawRect(x + 2, y + 2 + index * 10, x + width - 2, y + 2 + 10 * (index + 1), 0x44FFFFFF);
        }
    }

    private int drawListItems(Minecraft mc, int x, int y, ArrayList<IGuiTreeViewItem> items) {
        int offset = 0;
        for (int i = 0; i < items.size(); i++) {
            IGuiTreeViewItem item = items.get(i);
            String name = item.getIndex() + ":" + item.getName();
            name = item.getName();
            if (item.isExpanded()) {
                mc.fontRenderer.drawString("+" + name, x, y + offset * 10, item.getColour());
                if (!item.getSubItems().isEmpty()) {
                    offset += drawListItems(mc, x + 10, y + 10 + offset * 10, item.getSubItems());
                }
            } else {
                mc.fontRenderer.drawString("-" + name, x, y + offset * 10, item.getColour());
            }
            offset += 1;
        }
        return offset;
    }

    @Override
    public void playPressSound(SoundHandler soundHandlerIn) {
    }

    public ArrayList<IGuiTreeViewItem> getItems() {
        return rootItems;
    }

    public static interface IGuiTreeViewCallback {

        public void onSelectionChange(GuiTreeView guiTreeView, IGuiTreeViewItem selectedItem);
    }

    public static interface IGuiTreeViewItem<T> {

        public ArrayList<GuiTreeView.IGuiTreeViewItem> getSubItems();

        public void setIndex(int index);

        public int getIndex();

        public String getName();

        public void setLocked(boolean locked);

        public boolean isLocked();

        public void setExpanded(boolean expanded);

        public boolean isExpanded();

        public void setColour(int colour);

        public int getColour();
    }

    public static class GuiTreeViewItem implements IGuiTreeViewItem<GuiTreeViewItem> {

        private ArrayList<GuiTreeView.IGuiTreeViewItem> subItems = new ArrayList<GuiTreeView.IGuiTreeViewItem>();
        private int index;
        private String name;
        private boolean locked = false;
        private boolean expanded = true;
        private int colour = 0xFFFFFFFF;

        public GuiTreeViewItem(String name) {
            this.name = name;
        }
        
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public void setIndex(int index) {
            this.index = index;
        }

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public ArrayList<GuiTreeView.IGuiTreeViewItem> getSubItems() {
            return subItems;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setLocked(boolean locked) {
            this.locked = locked;
        }

        @Override
        public boolean isLocked() {
            return locked;
        }

        @Override
        public void setExpanded(boolean expanded) {
            this.expanded = expanded;
        }

        @Override
        public boolean isExpanded() {
            return expanded;
        }

        @Override
        public void setColour(int colour) {
            this.colour = colour;
        }

        @Override
        public int getColour() {
            return colour;
        }
    }
}
