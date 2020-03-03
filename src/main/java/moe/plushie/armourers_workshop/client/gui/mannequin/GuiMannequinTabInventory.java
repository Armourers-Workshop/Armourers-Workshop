package moe.plushie.armourers_workshop.client.gui.mannequin;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTabPanel;
import moe.plushie.armourers_workshop.common.inventory.slot.SlotHidable;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityMannequin;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMannequinTabInventory extends GuiTabPanel<GuiMannequin> {

    private static final int INV_SLOT_SIZE = 18;
    
    private static final int INV_PLAYER_TEX_WIDTH = 176;
    private static final int INV_PLAYER_TEX_HEIGHT = 98;
    private static final int INV_PLAYER_TEX_U = 0;
    private static final int INV_PLAYER_TEX_V = 0;
    private static final int INV_PLAYER_TOP_PAD = 15;
    private static final int INV_PLAYER_LEFT_PAD = 8;
    
    private static final int INV_MAN_TEX_WIDTH = 38;
    private static final int INV_MAN_TEX_HEIGHT = 38;
    private static final int INV_MAN_TAR_WIDTH = 176;
    private static final int INV_MAN_TAR_HEIGHT = 40;
    private static final int INV_MAN_TEX_U = 0;
    private static final int INV_MAN_TEX_V = 200;
    private static final int INV_MAN_TOP_PAD = 16;
    private static final int INV_MAN_LEFT_PAD = 26;
    
    private final TileEntityMannequin tileEntity;
    
    public GuiMannequinTabInventory(int tabId, GuiMannequin parent, TileEntityMannequin tileEntity) {
        super(tabId, parent, true);
        this.tileEntity = tileEntity;
    }
    
    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        
        GuiContainer guiCon = parent;
        //Move player inventory slots.
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
        
        
        //Move mannequin inventory slots.
        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 7; i++) {
                Slot slot = guiCon.inventorySlots.inventorySlots.get(36 + i + j * 7);
                if (slot instanceof SlotHidable) {
                    ((SlotHidable)slot).setDisplayPosition(
                            this.width / 2 - INV_MAN_TAR_WIDTH / 2 + INV_MAN_LEFT_PAD + i * INV_SLOT_SIZE,
                            INV_MAN_TOP_PAD + j * 18);
                }
            }
        }
    }
    
    @Override
    public void tabChanged(int tabIndex) {
        GuiContainer guiCon = parent;
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
        
        GuiUtils.drawContinuousTexturedBox(
                center - INV_MAN_TAR_WIDTH / 2, 0,
                INV_MAN_TEX_U, INV_MAN_TEX_V,
                INV_MAN_TAR_WIDTH, INV_MAN_TAR_HEIGHT + 18 * 4,
                INV_MAN_TEX_WIDTH, INV_MAN_TEX_HEIGHT,
                4, zLevel);
        for (int i = 0; i < 5; i++) {
            drawTexturedModalRect(
                    center - INV_PLAYER_TEX_WIDTH / 2 + INV_MAN_LEFT_PAD - 1, INV_MAN_TOP_PAD - 1 + i * INV_SLOT_SIZE,
                    25, 113,
                    128, INV_SLOT_SIZE);
        }
        String unfinishedText = GuiHelper.getLocalizedControlName(tileEntity.getName(), "unfinished");
        
        fontRenderer.drawSplitString(unfinishedText, center - INV_PLAYER_TEX_WIDTH / 2 + 5, 114, INV_PLAYER_TEX_WIDTH, 0x222222);
        fontRenderer.drawSplitString(unfinishedText, center - INV_PLAYER_TEX_WIDTH / 2 + 5 + 1, 114 + 1, INV_PLAYER_TEX_WIDTH, 0xFF3333);
        
        //fontRenderer.drawSplitString(str, mouseX, mouseY, wrapWidth, textColor);
    }
    
    @Override
    public void drawForegroundLayer(int mouseX, int mouseY, float partialTickTime) {
        super.drawForegroundLayer(mouseX, mouseY, partialTickTime);
    }
}
