package riskyken.armourersWorkshop.client.gui.mannequin;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import riskyken.armourersWorkshop.client.gui.controls.GuiTabPanel;
import riskyken.armourersWorkshop.common.inventory.slot.SlotHidable;

@SideOnly(Side.CLIENT)
public class GuiMannequinTabInventory extends GuiTabPanel {

    private static final int INV_SLOT_SIZE = 18;
    
    private static final int INV_PLAYER_TEX_WIDTH = 176;
    private static final int INV_PLAYER_TEX_HEIGHT = 98;
    private static final int INV_PLAYER_TEX_U = 0;
    private static final int INV_PLAYER_TEX_V = 0;
    private static final int INV_PLAYER_TOP_PAD = 15;
    private static final int INV_PLAYER_LEFT_PAD = 8;
    
    private static final int INV_MAN_TEX_WIDTH = 176;
    private static final int INV_MAN_TEX_HEIGHT = 40;
    private static final int INV_MAN_TEX_U = 0;
    private static final int INV_MAN_TEX_V = 98;
    private static final int INV_MAN_TOP_PAD = 15;
    private static final int INV_MAN_LEFT_PAD = 26;
    
    public GuiMannequinTabInventory(int tabId, GuiScreen parent) {
        super(tabId, parent, true);
    }
    
    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        
        GuiContainer guiCon = (GuiContainer) parent;
        //Move player inventory slots.
        for (int x = 0; x < 9; x++) {
            Slot slot = (Slot) guiCon.inventorySlots.inventorySlots.get(7 + x);
            if (slot instanceof SlotHidable) {
                ((SlotHidable)slot).setDisplayPosition(
                        (int) ((this.width / 2F) - (176F / 2F) + x * INV_SLOT_SIZE + INV_PLAYER_LEFT_PAD),
                        this.height + 1 - 1 - INV_SLOT_SIZE);
            }
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                Slot slot = (Slot) guiCon.inventorySlots.inventorySlots.get(7 + x + y * 9 + 9);
                if (slot instanceof SlotHidable) {
                    ((SlotHidable)slot).setDisplayPosition(
                            (int) ((this.width / 2F) - (176F / 2F) + x * INV_SLOT_SIZE + 8),
                            this.height + 1 - 72 - 5 + y * INV_SLOT_SIZE);
                }
            }
        }
        
        //Move mannequin inventory slots.
        for (int i = 0; i < 7; i++) {
            Slot slot = (Slot) guiCon.inventorySlots.inventorySlots.get(i);
            if (slot instanceof SlotHidable) {
                ((SlotHidable)slot).setDisplayPosition(this.width / 2 - INV_MAN_TEX_WIDTH / 2 + INV_MAN_LEFT_PAD + i * 18, 16);
            }
        }
    }
    
    @Override
    public void tabChanged(int tabIndex) {
        GuiContainer guiCon = (GuiContainer) parent;
        for (int i = 0; i < guiCon.inventorySlots.inventorySlots.size(); i++) {
            Slot slot = (Slot) guiCon.inventorySlots.inventorySlots.get(i);
            if (slot instanceof SlotHidable) {
                ((SlotHidable)slot).setVisible(tabIndex == getTabId());
            }
        }
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        int center = (int) ((float)this.width / 2F);
        drawTexturedModalRect(
                center - INV_PLAYER_TEX_WIDTH / 2,
                height - INV_PLAYER_TEX_HEIGHT + 6,
                INV_PLAYER_TEX_U, INV_PLAYER_TEX_V,
                INV_PLAYER_TEX_WIDTH, INV_PLAYER_TEX_HEIGHT);
        
        drawTexturedModalRect(
                center - INV_MAN_TEX_WIDTH / 2, 0,
                INV_MAN_TEX_U, INV_MAN_TEX_V,
                INV_MAN_TEX_WIDTH, INV_MAN_TEX_HEIGHT);
    }
    
    @Override
    public void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        //this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
    }
}
