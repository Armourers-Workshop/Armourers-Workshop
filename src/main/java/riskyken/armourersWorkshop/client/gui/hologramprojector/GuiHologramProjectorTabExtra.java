package riskyken.armourersWorkshop.client.gui.hologramprojector;

import cpw.mods.fml.client.config.GuiUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiCheckBox;
import riskyken.armourersWorkshop.client.gui.controls.GuiTabPanel;
import riskyken.armourersWorkshop.common.data.Rectangle_I_2D;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiHologramProjector;
import riskyken.armourersWorkshop.common.tileentities.TileEntityHologramProjector;

public class GuiHologramProjectorTabExtra extends GuiTabPanel {

    private final String inventoryName;
    private final TileEntityHologramProjector tileEntity;
    
    private GuiCheckBox checkGlowing;
    
    public GuiHologramProjectorTabExtra(int tabId, GuiScreen parent, String inventoryName, TileEntityHologramProjector tileEntity) {
        super(tabId, parent, true);
        this.inventoryName = inventoryName;
        this.tileEntity = tileEntity;
    }
    
    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        checkGlowing = new GuiCheckBox(-1, (int)((width / 2F) - (200 / 2F)) + 10, 30, GuiHelper.getLocalizedControlName(inventoryName, "glowing"), tileEntity.isGlowing());
        buttonList.add(checkGlowing);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == checkGlowing) {
            MessageClientGuiHologramProjector message = new MessageClientGuiHologramProjector();
            message.setGlowing(checkGlowing.isChecked());
            PacketHandler.networkWrapper.sendToServer(message);
        }
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        Rectangle_I_2D rec = new Rectangle_I_2D(0, 0, 200, 52);
        rec.x = width / 2 - rec.width / 2;
        GuiUtils.drawContinuousTexturedBox(rec.x, rec.y, 0, 138, rec.width, rec.height, 38, 38, 4, zLevel);
    }
}
