package moe.plushie.armourers_workshop.client.gui.controls;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiLabeledTextField extends GuiTextField {

    private final FontRenderer fontRenderer;
    private String emptyLabel = "";
    
    public GuiLabeledTextField(FontRenderer fontRenderer, int x, int y, int width, int height) {
        super(-1, fontRenderer, x, y, width, height);
        this.fontRenderer = fontRenderer;
    }
    
    public void setEmptyLabel(String emptyLabel) {
        this.emptyLabel = emptyLabel;
    }
    
    @Override
    public void drawTextBox() {
        super.drawTextBox();
        if (getVisible()) {
            if (this.getText().trim().isEmpty()) {
                String s = this.fontRenderer.trimStringToWidth(this.emptyLabel, this.getWidth());
                if (s.length() > 0 & !isFocused()) {
                    int lX = this.getEnableBackgroundDrawing() ? this.x + 4 : this.x;
                    int lY = this.getEnableBackgroundDrawing() ? this.y + (this.height - 8) / 2 : this.y;
                    this.fontRenderer.drawStringWithShadow(emptyLabel, lX, lY, 0xFF7F7F7F);
                }
            }
        }
    }
}
