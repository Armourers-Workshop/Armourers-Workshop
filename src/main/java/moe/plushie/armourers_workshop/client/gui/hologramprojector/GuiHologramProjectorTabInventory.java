package moe.plushie.armourers_workshop.client.gui.hologramprojector;

import moe.plushie.armourers_workshop.client.gui.controls.GuiTabPanel;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotHidable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiHologramProjectorTabInventory extends GuiTabPanel {

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
    
    public GuiHologramProjectorTabInventory(int tabId, GuiScreen parent) {
        super(tabId, parent, true);
    }
    
    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        
        GuiContainer guiCon = (GuiContainer) parent;
        // Move player inventory slots.
        for (int x = 0; x < 9; x++) {
            Slot slot = guiCon.inventorySlots.inventorySlots.get(x);
            if (slot instanceof SlotHidable) {
                ((SlotHidable)slot).setDisplayPosition(
                        (int) ((this.width / 2F) - (176F / 2F) + x * INV_SLOT_SIZE + INV_PLAYER_LEFT_PAD),
                        this.height + 1 - 1 - INV_SLOT_SIZE);
            }
        }
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                Slot slot = guiCon.inventorySlots.inventorySlots.get(x + y * 9 + 9);
                if (slot instanceof SlotHidable) {
                    ((SlotHidable)slot).setDisplayPosition(
                            (int) ((this.width / 2F) - (176F / 2F) + x * INV_SLOT_SIZE + 8),
                            this.height + 1 - 72 - 5 + y * INV_SLOT_SIZE);
                }
            }
        }
        
        Slot slot = guiCon.inventorySlots.inventorySlots.get(36);
        if (slot instanceof SlotHidable) {
            ((SlotHidable)slot).setDisplayPosition(
                    (int) ((this.width / 2F) - (16F / 2F)), 16);
        }
    }
    
    @Override
    public void tabChanged(int tabIndex) {
        GuiContainer guiCon = (GuiContainer) parent;
        for (int i = 0; i < guiCon.inventorySlots.inventorySlots.size(); i++) {
            Slot slot = guiCon.inventorySlots.inventorySlots.get(i);
            if (slot instanceof SlotHidable) {
                ((SlotHidable)slot).setVisible(tabIndex == getTabId());
            }
        }
    }
    
    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        int center = (int) (this.width / 2F);
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
    public void drawForegroundLayer(int mouseX, int mouseY, float partialTickTime) {
        this.fontRenderer.drawString(I18n.format("container.inventory", new Object[0]), width / 2 - INV_PLAYER_TEX_WIDTH / 2 + 8, height - 96 + 9, 4210752);
        super.drawForegroundLayer(mouseX, mouseY, partialTickTime);
    }
}
