package riskyken.armourersWorkshop.client.gui.skinlibrary;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.client.gui.AbstractGuiDialog;
import riskyken.armourersWorkshop.common.lib.LibModInfo;

@SideOnly(Side.CLIENT)
public class GuiDialogNewFolder extends AbstractGuiDialog {

    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/toolOptions.png");
    
    private GuiButtonExt buttonClose;
    
    public GuiDialogNewFolder(GuiScreen parent, IDialogCallback callback, int width, int height) {
        super(parent, callback, width, height);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        buttonClose = new GuiButtonExt(-1, x + 10, y + 10, 80, 20, "Close");
        buttonList.add(buttonClose);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonClose) {
            returnDialogResult(DialogResult.CANCEL);
        }
    }
    
    @Override
    public void drawBackground(int mouseX, int mouseY, float partialTickTime) {
        //super.drawBackground(mouseX, mouseY, partialTickTime);
        int textureWidth = 176;
        int textureHeight = 62;
        int borderSize = 4;
        mc.renderEngine.bindTexture(texture);
        GuiUtils.drawContinuousTexturedBox(x, y, 0, 0, width, height, textureWidth, textureHeight, borderSize, zLevel);
    }
    
    @Override
    public void drawForeground(int mouseX, int mouseY, float partialTickTime) {
        super.drawForeground(mouseX, mouseY, partialTickTime);
    }
}
