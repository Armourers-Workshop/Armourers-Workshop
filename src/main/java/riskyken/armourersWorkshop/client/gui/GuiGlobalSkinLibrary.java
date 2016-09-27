package riskyken.armourersWorkshop.client.gui;

import cpw.mods.fml.client.config.GuiButtonExt;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import riskyken.armourersWorkshop.common.tileentities.TileEntityGlobalSkinLibrary;
import riskyken.armourersWorkshop.utils.ModLogger;
import riskyken.armourersWorkshop.utils.UtilColour;
import riskyken.armourersWorkshop.utils.UtilColour.ColourFamily;

public class GuiGlobalSkinLibrary extends GuiScreen {

    private final TileEntityGlobalSkinLibrary tileEntity;
    
    public GuiGlobalSkinLibrary(TileEntityGlobalSkinLibrary tileEntity) {
        this.tileEntity = tileEntity;
    }
    
    @Override
    public void initGui() {
        buttonList.clear();
        buttonList.add(new GuiButtonExt(0, 5, 5, 100, 20, "Cookies"));
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            ModLogger.log("Cookies!");
        }
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTickTime) {
        this.drawRect(0, 0, this.width, this.height, 0xCC000000);
        super.drawScreen(mouseX, mouseY, partialTickTime);
        drawTextCentered(tileEntity.getBlockType().getLocalizedName(), this.width / 2, 2, UtilColour.getMinecraftColor(0, ColourFamily.MINECRAFT));
        drawTextCentered("WARNING - This block is unfinished.", this.width / 2, 12, 0xFF0000);
        drawTextCentered("!!! Do not use !!!", this.width / 2, 22, 0xFF0000);
    }
    
    private void drawTextCentered(String text, int x, int y, int colour) {
        int stringWidth = fontRendererObj.getStringWidth(text);
        fontRendererObj.drawString(text, x - (stringWidth / 2), y, colour);
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
