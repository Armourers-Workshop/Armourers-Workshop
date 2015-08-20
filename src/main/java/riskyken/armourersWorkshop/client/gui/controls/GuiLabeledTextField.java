package riskyken.armourersWorkshop.client.gui.controls;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

@SideOnly(Side.CLIENT)
public class GuiLabeledTextField extends GuiTextField {

    private final FontRenderer fontRenderer;
    private String emptyLabel = "";
    
    public GuiLabeledTextField(FontRenderer fontRenderer, int x, int y, int width, int height) {
        super(fontRenderer, x, y, width, height);
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
                    int lX = this.getEnableBackgroundDrawing() ? this.xPosition + 4 : this.xPosition;
                    int lY = this.getEnableBackgroundDrawing() ? this.yPosition + (this.height - 8) / 2 : this.yPosition;
                    this.fontRenderer.drawStringWithShadow(emptyLabel, lX, lY, 0xFF7F7F7F);
                }
            }
        }
    }
}
