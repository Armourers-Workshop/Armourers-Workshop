package moe.plushie.armourers_workshop.client.gui.globallibrary.panels;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.GuiPanel;
import moe.plushie.armourers_workshop.client.gui.globallibrary.GuiGlobalLibrary;
import moe.plushie.armourers_workshop.common.library.global.PlushieUser;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiGlobalLibraryPanelProfile extends GuiPanel {

    private final String guiName;
    private PlushieUser user = null;

    public GuiGlobalLibraryPanelProfile(GuiScreen parent) {
        super(parent, 0, 0, 1, 1);
        guiName = ((GuiGlobalLibrary) parent).getGuiName() + ".panel.profile";
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
    }

    @Override
    public GuiPanel setVisible(boolean visible) {
        if (visible & !this.visible) {
            updateProfileData();
        }
        return super.setVisible(visible);
    }

    @Override
    public void draw(int mouseX, int mouseY, float partialTickTime) {
        if (!visible) {
            return;
        }
        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);
        String username = "MissingNo";
        if (user != null) {
            username = user.getUsername();
        }
        fontRenderer.drawString(GuiHelper.getLocalizedControlName(guiName, "name", username), x + 5, y + 5, 0xFFFFFF);
        
        String profileItems = "";
        profileItems += "Bio (info, links)\n";
        profileItems += "Skins\n";
        profileItems += "Badges\n";
        profileItems += "Lists\n";
        profileItems += "Following\n";
        profileItems += "Followers\n";
        profileItems += "Friends\n";
        fontRenderer.drawSplitString(profileItems, x + 5, y + 20, width - 10, 0xFFFFFF);
        super.draw(mouseX, mouseY, partialTickTime);

    }

    public void setProfileTarget(PlushieUser user) {
        this.user = user;
        if (visible) {
            updateProfileData();
        }
    }

    private void updateProfileData() {

    }
}
