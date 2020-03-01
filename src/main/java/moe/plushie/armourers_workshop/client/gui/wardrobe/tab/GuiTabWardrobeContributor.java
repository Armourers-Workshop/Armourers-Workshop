package moe.plushie.armourers_workshop.client.gui.wardrobe.tab;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.GuiCheckBox;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTabPanel;
import moe.plushie.armourers_workshop.client.gui.wardrobe.GuiWardrobe;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTabWardrobeContributor extends GuiTabPanel {
    
    private static final ResourceLocation GUI_JSON = new ResourceLocation(LibGuiResources.JSON_WARDROBE);
    public static boolean testMode = false;
    
    private GuiCheckBox checkBoxTest;
    
    public GuiTabWardrobeContributor(int tabId, GuiScreen parent) {
        super(tabId, parent, false);
    }
    
    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        checkBoxTest = new GuiCheckBox(0, 85, 130, "Magic circle test?", testMode);
        
        buttonList.add(checkBoxTest);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == checkBoxTest) {
            testMode = checkBoxTest.isChecked();
        }
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
    }
    
    @Override
    public void drawForegroundLayer(int mouseX, int mouseY, float partialTickTime) {
        super.drawForegroundLayer(mouseX, mouseY, partialTickTime);
        
        String thanks = GuiHelper.getLocalizedControlName("wardrobe.tab.contributor", "label.contributor");
        thanks += "\n\n\nOptions coming here soon!";
        
        fontRenderer.drawSplitString(thanks, 85, 26, 185, 0x404040);
        
        // Draw player preview.
        GL11.glPushMatrix();
        GL11.glTranslated(-x, -y, 0);
        ((GuiWardrobe)parent).drawPlayerPreview(x, y, mouseX, mouseY);
        GL11.glPopMatrix();
    }
}
