package moe.plushie.armourers_workshop.client.gui.wardrobe.tab;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.GuiIconButton;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTabPanel;
import moe.plushie.armourers_workshop.client.gui.wardrobe.GuiWardrobe;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.common.init.entities.EntityMannequin;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiUpdateMannequin;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTabWardrobeManOffset extends GuiTabPanel {

    private static final ResourceLocation TEXTURE_BUTTONS = new ResourceLocation(LibGuiResources.CONTROL_BUTTONS);

    private final EntityMannequin entityMannequin;
    private final String guiName = "wardrobe.tab.man_offsets";

    private GuiIconButton[] iconButtonsX = new GuiIconButton[6];
    private GuiIconButton[] iconButtonsY = new GuiIconButton[6];
    private GuiIconButton[] iconButtonsZ = new GuiIconButton[6];

    public GuiTabWardrobeManOffset(int tabId, GuiScreen parent, EntityMannequin entityMannequin) {
        super(tabId, parent);
        this.entityMannequin = entityMannequin;
    }

    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);

        for (int i = 0; i < 3; i++) {
            iconButtonsX[i] = new GuiIconButton(parent, (i - 3), 81 + i * 20, 25, 16, 16, TEXTURE_BUTTONS);
            iconButtonsY[i] = new GuiIconButton(parent, (i - 3), 81 + i * 20, 25 + 1 * 20, 16, 16, TEXTURE_BUTTONS);
            iconButtonsZ[i] = new GuiIconButton(parent, (i - 3), 81 + i * 20, 25 + 2 * 20, 16, 16, TEXTURE_BUTTONS);

            iconButtonsX[i].setHoverText(GuiHelper.getLocalizedControlName(guiName, "button.sub." + -(i - 3)));
            iconButtonsY[i].setHoverText(GuiHelper.getLocalizedControlName(guiName, "button.sub." + -(i - 3)));
            iconButtonsZ[i].setHoverText(GuiHelper.getLocalizedControlName(guiName, "button.sub." + -(i - 3)));

            iconButtonsX[i].setDrawButtonBackground(false);
            iconButtonsY[i].setDrawButtonBackground(false);
            iconButtonsZ[i].setDrawButtonBackground(false);

            iconButtonsX[i].setIconLocation(208, 80, 16, 16);
            iconButtonsY[i].setIconLocation(208, 80, 16, 16);
            iconButtonsZ[i].setIconLocation(208, 80, 16, 16);
        }
        for (int i = 0; i < 3; i++) {
            iconButtonsX[i + 3] = new GuiIconButton(parent, i + 1, 160 + i * 20, 25, 16, 16, TEXTURE_BUTTONS);
            iconButtonsY[i + 3] = new GuiIconButton(parent, i + 1, 160 + i * 20, 25 + 1 * 20, 16, 16, TEXTURE_BUTTONS);
            iconButtonsZ[i + 3] = new GuiIconButton(parent, i + 1, 160 + i * 20, 25 + 2 * 20, 16, 16, TEXTURE_BUTTONS);

            iconButtonsX[i + 3].setHoverText(GuiHelper.getLocalizedControlName(guiName, "button.add." + (i + 1)));
            iconButtonsY[i + 3].setHoverText(GuiHelper.getLocalizedControlName(guiName, "button.add." + (i + 1)));
            iconButtonsZ[i + 3].setHoverText(GuiHelper.getLocalizedControlName(guiName, "button.add." + (i + 1)));

            iconButtonsX[i + 3].setDrawButtonBackground(false);
            iconButtonsY[i + 3].setDrawButtonBackground(false);
            iconButtonsZ[i + 3].setDrawButtonBackground(false);

            iconButtonsX[i + 3].setIconLocation(208, 96, 16, 16);
            iconButtonsY[i + 3].setIconLocation(208, 96, 16, 16);
            iconButtonsZ[i + 3].setIconLocation(208, 96, 16, 16);
        }

        for (int i = 0; i < 6; i++) {
            buttonList.add(iconButtonsX[i]);
            buttonList.add(iconButtonsY[i]);
            buttonList.add(iconButtonsZ[i]);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        MessageClientGuiUpdateMannequin message = new MessageClientGuiUpdateMannequin(entityMannequin);
        float scale = 0.0625F;
        
        float amount = 0;
        if (button.id == -1) {
            amount = -scale;
        }
        if (button.id == -2) {
            amount = -scale * 8;
        }
        if (button.id == -3) {
            amount = -1;
        }
        if (button.id == 1) {
            amount = scale;
        }
        if (button.id == 2) {
            amount = scale * 8;
        }
        if (button.id == 3) {
            amount = 1;
        }
        
        Vec3d offset = new Vec3d(0, 0, 0);
        for (int i = 0; i < 6; i++) {
            if (iconButtonsX[i] == button) {
                offset = offset.add(amount, 0, 0);
            }
            if (iconButtonsY[i] == button) {
                offset = offset.add(0, amount, 0);
            }
            if (iconButtonsZ[i] == button) {
                offset = offset.add(0, 0, amount);
            }
        }

        message.setOffset(offset);
        PacketHandler.networkWrapper.sendToServer(message);

        Vec3d pos = entityMannequin.getPositionVector();
        pos = pos.add(offset);
        entityMannequin.setPosition(pos.x, pos.y, pos.z);
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
    }

    @Override
    public void drawForegroundLayer(int mouseX, int mouseY, float partialTickTime) {
        super.drawForegroundLayer(mouseX, mouseY, partialTickTime);
        fontRenderer.drawString("X", 146, 29, 0x333333);
        fontRenderer.drawString("Y", 146, 29 + 20, 0x333333);
        fontRenderer.drawString("Z", 146, 29 + 40, 0x333333);
        // Draw entity preview.
        GL11.glPushMatrix();
        GL11.glTranslated(-x, -y, 0);
        ((GuiWardrobe) parent).drawPlayerPreview(x, y, mouseX, mouseY);
        GL11.glPopMatrix();

        for (int i = 0; i < buttonList.size(); i++) {
            GuiButton button = (GuiButton) buttonList.get(i);
            if (button instanceof GuiIconButton) {
                ((GuiIconButton) button).drawRollover(mc, mouseX - x, mouseY - y);
            }
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
