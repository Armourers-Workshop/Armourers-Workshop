package moe.plushie.armourers_workshop.client.gui.controls;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDropDownList extends GuiButtonExt {

    private List<DropDownListItem> listItems;
    private int selectedIndex;
    private int hoverIndex;
    private boolean isMouseDownOver;
    private boolean isDroppedDown;
    private int dropButtonX;
    private int dropButtonY;
    private int dropButtonWidth;
    private int dropButtonHeight;
    private IDropDownListCallback callback;
    
    public GuiDropDownList(int id, int xPos, int yPos, int width, String displayString, IDropDownListCallback callback) {
        super(id, xPos, yPos, width, 14, displayString);
        this.callback = callback;
        this.listItems = new ArrayList<DropDownListItem>();
        this.selectedIndex = 0;
        this.hoverIndex = -1;
        this.isMouseDownOver = false;
        this.isDroppedDown = false;
        this.dropButtonHeight = this.height;
        this.dropButtonWidth = 14;
        this.dropButtonY = this.y;
        this.dropButtonX = this.x + this.width - this.dropButtonWidth;
    }
    
    public boolean getIsDroppedDown() {
        return this.isDroppedDown;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            mouseCheck();
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int k = this.getHoverState(this.hovered);
            GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, this.x, this.y, 0, 46, this.width, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
            
            drawDropDownButton(mc, mouseX, mouseY);
            
            if (getListSelectedItem() != null) {
                getListSelectedItem().drawItem(mc, this, this.x + 3, this.y + 3, mouseX, mouseY, partial, true);
            }
            //mc.fontRenderer.drawString(this.displayString, this.x + 3, this.y + 3, 16777215);
        }
    }
    
    public void drawForeground(Minecraft mc, int mouseX, int mouseY, float partial) {
        this.hoverIndex = -1;
        if (this.isDroppedDown) {
            int listSize = listItems.size();
            GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, this.x, this.y + this.height + 1, 0, 46, this.width, 10 * listSize + 4, 200, 20, 2, 3, 2, 2, this.zLevel);
            for (int i = 0; i < listSize; i++) {
                DropDownListItem listItem = listItems.get(i);
                int textX = this.x + 4;
                int textY = this.y + this.height + 4 + (i * 10);
                if (listItem.isMouseOver(this, textX, textY, mouseX, mouseY) & listItem.enabled) {
                    hoverIndex = i;
                }
                listItem.drawItem(mc, this, textX, textY, mouseX, mouseY, partial, false);
            } 
        }
    }
    
    private boolean mouseDown = false;
    
    private void mouseCheck() {
        //Fix for TMI, it stops the mouse released event in container GUI's.
        if (Loader.isModLoaded("TooManyItems")) {
            if (Mouse.isCreated()) {
                if (Mouse.isButtonDown(0)) {
                    mouseDown = true;
                } else {
                    if (mouseDown) {
                        mouseDown = false;
                        Minecraft mc = Minecraft.getMinecraft();
                        ScaledResolution reso = new ScaledResolution(mc);
                        double scaleWidth = (double)mc.displayWidth / reso.getScaledWidth_double();
                        double scaleHeight = (double)mc.displayHeight / reso.getScaledHeight_double();
                        int mouseX = (int) (Mouse.getX() / scaleWidth);
                        int mouseY = (int) (-(Mouse.getY() - mc.displayHeight) / scaleHeight);
                        mouseReleased(mouseX, mouseY);
                    }
                }
            }
        }
    }
    
    private boolean mouseOverDropDownButton(int mouseX, int mouseY) {
        return mouseX >= this.dropButtonX && mouseY >= this.dropButtonY && mouseX < this.dropButtonX + this.dropButtonWidth && mouseY < this.dropButtonY + this.dropButtonHeight;
    }
    
    private void drawDropDownButton(Minecraft mc, int mouseX, int mouseY) {
        int k = this.getHoverState(mouseOverDropDownButton(mouseX, mouseY));
        GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, this.dropButtonX, this.dropButtonY, 0, 46 + k * 20, this.dropButtonWidth, this.dropButtonHeight, 200, 20, 2, 3, 2, 2, this.zLevel);
        
        String dropDownArrow = "v";
        if (isDroppedDown) {
            dropDownArrow = "^";
        }
        int arrowWidth = mc.fontRenderer.getStringWidth(dropDownArrow);
        mc.fontRenderer.drawString(dropDownArrow, this.dropButtonX + (this.dropButtonWidth / 2) - (arrowWidth / 2), this.dropButtonY + 3, 16777215);
    }
    
    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        int oldHeight = this.height;
        if (this.isDroppedDown) {
            this.height += 10 * listItems.size() + 4;
        }
        if (super.mousePressed(mc, mouseX, mouseY)) {
            if (this.isDroppedDown) {
                this.height = oldHeight;
            }
            this.isMouseDownOver = mouseOverDropDownButton(mouseX, mouseY);
            return true;
        } else {
            if (this.isDroppedDown) {
                this.height = oldHeight;
                this.isDroppedDown = false;
            }
            return false;
        }
    }
    
    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        if (this.isMouseDownOver && mouseOverDropDownButton(mouseX, mouseY)) {
            this.isDroppedDown = !this.isDroppedDown;
            this.isMouseDownOver = false;
        }
        if (hoverIndex != -1) {
            setListSelectedIndex(hoverIndex);
            this.isDroppedDown = false;
            if (this.callback != null) {
                this.callback.onDropDownListChanged(this);
            }
        }
    }
    
    public void clearList() {
        listItems.clear();
    }
    
    public void addListItem(DropDownListItem listItem) {
        listItems.add(listItem);
    }
    
    public void addListItem(String displayText) {
        addListItem(displayText, "", true);
    }
    
    public void addListItem(String displayText, String tag, boolean enabled) {
        listItems.add(new DropDownListItem(displayText, tag, enabled));
    }
    
    public DropDownListItem getListIndex(int index) {
        return this.listItems.get(index);
    }
    
    public int getListSelectedIndex() {
        return selectedIndex;
    }
    
    public DropDownListItem getListSelectedItem() {
        return this.listItems.get(this.selectedIndex);
    }
    
    public void setListSelectedIndex(int index) {
        this.selectedIndex = index;
        this.displayString = listItems.get(this.selectedIndex).displayText;
    }
    
    public int getListSize() {
        return this.listItems.size();
    }
    
    public interface IDropDownListCallback {
        public void onDropDownListChanged(GuiDropDownList dropDownList);
    }
    
    public static class DropDownListItem {
        public String displayText;
        public String tag;
        public boolean enabled;
        
        public DropDownListItem(String displayText, String tag, boolean enabled) {
            this.displayText = displayText;
            this.tag = tag;
            this.enabled = enabled;
        }
        
        public boolean isMouseOver(GuiDropDownList parent, int x, int y, int mouseX, int mouseY) {
            int textWidth = parent.width - 8;
            int textHeight = 8;
            if (mouseX >= x && mouseY >= y && mouseX < x + textWidth && mouseY < y + textHeight) {
                return true;
            }
            return false;
        }

        public void drawItem(Minecraft mc, GuiDropDownList parent, int x, int y, int mouseX, int mouseY, float partial, boolean topItem) {
            int textWidth = parent.width - 8;
            int textHeight = 8;
            int textColour = 16777215;
            if (!enabled) {
                textColour = 0xFFCC0000;
            } else {
                if (isMouseOver(parent, x, y, mouseX, mouseY) & !topItem) {
                    if (enabled) {
                        textColour = 16777120;
                        drawRect(x, y, x + textWidth, y + textHeight, 0x44CCCCCC);
                    }
                }
            }
            mc.fontRenderer.drawString(displayText, x, y, textColour);
        }
    }
}
