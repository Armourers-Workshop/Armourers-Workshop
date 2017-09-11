package riskyken.armourersWorkshop.client.gui.globallibrary.panels;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.FutureTask;

import com.google.gson.JsonArray;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiLabeledTextField;
import riskyken.armourersWorkshop.client.gui.controls.GuiPanel;
import riskyken.armourersWorkshop.client.gui.globallibrary.GuiGlobalLibrary;
import riskyken.armourersWorkshop.client.gui.globallibrary.GuiGlobalLibrary.Screen;
import riskyken.armourersWorkshop.common.library.global.DownloadUtils.DownloadJsonCallable;

@SideOnly(Side.CLIENT)
public class GuiGlobalLibraryPanelSearchBox extends GuiPanel {
    
    private static final String SEARCH_URL = "http://plushie.moe/armourers_workshop/skin-search.php";
    
    private GuiLabeledTextField searchTextbox;
    
    public GuiGlobalLibraryPanelSearchBox(GuiGlobalLibrary parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        String guiName = ((GuiGlobalLibrary)parent).getGuiName();
        buttonList.clear();
        searchTextbox = new GuiLabeledTextField(fontRenderer, x + 5, y + 5, width - 10 - 85, 12);
        searchTextbox.setEmptyLabel(GuiHelper.getLocalizedControlName(guiName, "searchBox.typeToSearch"));
        buttonList.add(new GuiButtonExt(0, x + width - 85, y + 3, 80, 16, GuiHelper.getLocalizedControlName(guiName, "searchBox.search")));
    }
    
    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (!visible | !enabled) {
            return;
        }
        super.mouseClicked(mouseX, mouseY, button);
        searchTextbox.mouseClicked(mouseX, mouseY, button);
        if (button == 1) {
            if (searchTextbox.isFocused()) {
                searchTextbox.setText("");
            }
        }
    }
    
    @Override
    public boolean keyTyped(char c, int keycode) {
        if (!visible | !enabled) {
            return false;
        }
        boolean pressed = searchTextbox.textboxKeyTyped(c, keycode);
        if (keycode == 28) {
            doSearch();
        }
        return pressed;
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        doSearch();
    }
    
    private void doSearch() {
        try {
            String searchUrl = SEARCH_URL + "?search=" + URLEncoder.encode(searchTextbox.getText(), "UTF-8");
            FutureTask<JsonArray> futureTask = new FutureTask<JsonArray>(new DownloadJsonCallable(searchUrl));
            ((GuiGlobalLibrary)parent).panelSearchResults.setDownloadSearchResultsTask(futureTask);
            ((GuiGlobalLibrary)parent).jsonDownloadExecutor.execute(futureTask);
            ((GuiGlobalLibrary)parent).switchScreen(Screen.SEARCH);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void draw(int mouseX, int mouseY, float partialTickTime) {
        if (!visible) {
            return;
        }
        drawGradientRect(this.x, this.y, this.x + this.width, this.y + height, 0xC0101010, 0xD0101010);
        super.draw(mouseX, mouseY, partialTickTime);
        searchTextbox.drawTextBox();
    }
}
