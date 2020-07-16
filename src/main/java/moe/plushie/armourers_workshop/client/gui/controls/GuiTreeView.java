package moe.plushie.armourers_workshop.client.gui.controls;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTreeView extends GuiButtonExt {

    private final ArrayList<IGuiTreeViewItem> rootItems = new ArrayList<IGuiTreeViewItem>();

    public GuiTreeView(int xPos, int yPos, int width, int height) {
        super(-1, xPos, yPos, width, height, "");
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return super.mousePressed(mc, mouseX, mouseY);
    }

    @Override
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        super.mouseDragged(mc, mouseX, mouseY);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        super.mouseReleased(mouseX, mouseY);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial) {
        if (!visible) {
            return;
        }
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        int k = this.getHoverState(this.hovered);
        GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, this.x, this.y, 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
    }

    public ArrayList<IGuiTreeViewItem> getItems() {
        return rootItems;
    }
    
    public static interface IGuiTreeViewItem<T> {
        
        public String getName();
        
        public ArrayList<T> getSubItems();
    }

    public static class GuiTreeViewItem implements IGuiTreeViewItem<GuiTreeViewItem> {

        private final ArrayList<GuiTreeViewItem> subItems = new ArrayList<GuiTreeViewItem>();
        private String name;

        public GuiTreeViewItem(String name) {
            this.name = name;
        }
        
        @Override
        public String getName() {
            return name;
        }
        
        @Override
        public ArrayList<GuiTreeViewItem> getSubItems() {
            return subItems;
        }
    }
}
