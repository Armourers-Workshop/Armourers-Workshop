package riskyken.armourersWorkshop.client.gui.mannequin;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiTabPanel;
import riskyken.armourersWorkshop.common.data.Rectangle_I_2D;
import riskyken.armourersWorkshop.common.tileentities.TileEntityMannequin;

public class GuiMannequinTabName extends GuiTabPanel {
    
    private final TileEntityMannequin tileEntity;
    public GuiTextField nameTextbox;
    private GuiButtonExt setNameButton;
    
    public GuiMannequinTabName(int tabId, GuiScreen parent, TileEntityMannequin tileEntity) {
        super(tabId, parent);
        this.tileEntity = tileEntity;
    }

    @Override
    public void initGui() {
        super.initGui();
        nameTextbox = new GuiTextField(0 ,fontRenderer, width / 2 - 78, 25, 100, 14);
        if (tileEntity.getGameProfile() != null) {
            nameTextbox.setText(tileEntity.getGameProfile().getName());
        }
        setNameButton = new GuiButtonExt(0, width / 2 + 28, 25, 50, 14, GuiHelper.getLocalizedControlName(tileEntity.getName(), "set"));
        
        buttonList.add(setNameButton);
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        Rectangle_I_2D rec = new Rectangle_I_2D(0, 0, 176, 48);
        rec.x = width / 2 - rec.width / 2;
        GuiUtils.drawContinuousTexturedBox(rec.x, rec.y, 0, 200, rec.width, rec.height, 38, 38, 4, zLevel);
    }
    
    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        if (button == 1) {
            nameTextbox.setText("");
        } else {
            nameTextbox.mouseClicked(mouseX, mouseY, button);
        }
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == setNameButton) {
            ((GuiMannequin)parent).tabOffset.sendData();
        }
    }
    
    @Override
    public boolean keyTyped(char c, int keycode) {
        return nameTextbox.textboxKeyTyped(c, keycode);
    }
    
    @Override
    public void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        nameTextbox.drawTextBox();
    }
}
