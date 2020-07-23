package moe.plushie.armourers_workshop.client.gui.controls;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.ModGuiControl.IScreenSize;
import moe.plushie.armourers_workshop.common.data.type.Rectangle_I_2D;
import net.minecraft.client.Minecraft;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class ModGuiControl<CONTROL_TYPE, PARENT_TYPE extends IScreenSize> extends GuiButtonExt {
    
    private final PARENT_TYPE parent;
    private String hoverText;
    private String hoverTextDisable;
    
    private int hoverStateLast;
    private long hoverStartTime;
    
    public ModGuiControl(PARENT_TYPE parent, int id, int xPos, int yPos, int width, int height) {
        super(id, xPos, yPos, width, height, "");
        this.parent = parent;
    }
    
    public CONTROL_TYPE setHoverText(String hoverText) {
        this.hoverText = hoverText;
        return (CONTROL_TYPE) this;
    }
    
    public CONTROL_TYPE setHoverTexDisablet(String hoverTextDisable) {
        this.hoverTextDisable = hoverTextDisable;
        return (CONTROL_TYPE) this;
    }
    
    protected void updateHoverState(int mouseX, int mouseY) {
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        int hoverState = this.getHoverState(this.hovered);
        
        if (hoverState == 2 & hoverStateLast != hoverState) {
            hoverStartTime = System.currentTimeMillis();
        }
        hoverStateLast = hoverState;
    }
    
    protected int getHoverTime() {
        if (hoverStateLast == 2) {
            return (int) (System.currentTimeMillis() - hoverStartTime);
        }
        return 0;
    }
    
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial) {
        updateHoverState(mouseX, mouseY);
        super.drawButton(mc, mouseX, mouseY, partial);
    }
    
    public void drawRollover(Minecraft mc, int mouseX, int mouseY) {
        if (!this.visible) {
            return;
        }
        String CRLF = "\r\n";
        //CRLF = "\n";
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        int hoverState = this.getHoverState(this.hovered);
        if (hoverState == 0 & this.hovered) {
            if (!StringUtils.isNullOrEmpty(hoverTextDisable)) {
                ArrayList<String> textList = new ArrayList<String>();
                textList.add(hoverTextDisable);
                GuiHelper.drawHoveringText(textList, mouseX + parent.getSize().x, mouseY + parent.getSize().y, mc.fontRenderer, parent.getSize().width, parent.getSize().height, zLevel);
            }
        }
        if (hoverState == 2) {
            if (!StringUtils.isNullOrEmpty(hoverText)) {
                ArrayList<String> textList = new ArrayList<String>();
                String[] split = hoverText.split(CRLF);
                for (String line : split) {
                    textList.add(line);
                }
                GuiHelper.drawHoveringText(textList, mouseX + parent.getSize().x, mouseY + parent.getSize().y, mc.fontRenderer, parent.getSize().width, parent.getSize().height, zLevel);
            }
        }
    }
    
    public static interface IScreenSize {
        
        public Rectangle_I_2D getSize();
    }
}
