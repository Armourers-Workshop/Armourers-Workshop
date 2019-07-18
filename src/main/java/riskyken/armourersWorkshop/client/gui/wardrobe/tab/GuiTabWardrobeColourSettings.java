package riskyken.armourersWorkshop.client.gui.wardrobe.tab;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.client.gui.GuiHelper;
import riskyken.armourersWorkshop.client.gui.controls.GuiIconButton;
import riskyken.armourersWorkshop.client.gui.controls.GuiTabPanel;
import riskyken.armourersWorkshop.client.gui.wardrobe.GuiWardrobe;
import riskyken.armourersWorkshop.client.lib.LibGuiResources;
import riskyken.armourersWorkshop.client.render.ModRenderHelper;
import riskyken.armourersWorkshop.common.SkinHelper;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientSkinWardrobeUpdate;
import riskyken.armourersWorkshop.common.painting.PaintType;
import riskyken.armourersWorkshop.common.painting.PaintingHelper;
import riskyken.armourersWorkshop.common.wardrobe.EquipmentWardrobeData;
import riskyken.armourersWorkshop.common.wardrobe.ExPropsPlayerSkinData;
import riskyken.armourersWorkshop.common.wardrobe.ExtraColours;
import riskyken.armourersWorkshop.common.wardrobe.ExtraColours.ExtraColourType;

@SideOnly(Side.CLIENT)
public class GuiTabWardrobeColourSettings extends GuiTabPanel {

    private static final ResourceLocation TEXTURE = new ResourceLocation(LibGuiResources.WARDROBE_2);

    EntityPlayer entityPlayer;
    ExPropsPlayerSkinData propsPlayerSkinData;
    EquipmentWardrobeData equipmentWardrobeData;

    private ExtraColourType selectingColourType = null;
    private Color selectingColour = null;

    private Color colourSkin;
    private Color colourHair;
    // private Color colourEye;
    // private Color colourMisc;

    private GuiButtonExt selectSkinButton;
    private GuiButtonExt autoSkinButton;
    private GuiButtonExt selectHairButton;
    private GuiButtonExt autoHairButton;

    private GuiIconButton buttonSkinSelect;
    private GuiIconButton buttonSkinAuto;

    private GuiIconButton buttonHairSelect;
    private GuiIconButton buttonHairAuto;

    // private GuiIconButton buttonEyeSelect;
    // private GuiIconButton buttonEyeAuto;

    // private GuiIconButton buttonMiscSelect;

    String guiName = "equipmentWardrobe";

    public GuiTabWardrobeColourSettings(int tabId, GuiScreen parent, EntityPlayer entityPlayer, ExPropsPlayerSkinData propsPlayerSkinData, EquipmentWardrobeData equipmentWardrobeData) {
        super(tabId, parent, false);
        this.entityPlayer = entityPlayer;
        this.propsPlayerSkinData = propsPlayerSkinData;
        this.equipmentWardrobeData = equipmentWardrobeData;
        getColours();
    }

    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);
        buttonSkinSelect = new GuiIconButton(parent, 0, 83 + 18, 38 - 2, 18, 18, GuiHelper.getLocalizedControlName(guiName, "selectSkin"), TEXTURE).setIconLocation(238, 0, 18, 18).setHorizontal(false);
        buttonSkinAuto = new GuiIconButton(parent, 0, 83 + 40, 38 - 2, 18, 18, GuiHelper.getLocalizedControlName(guiName, "autoSkin"), TEXTURE).setIconLocation(238, 76, 18, 18).setHorizontal(false);

        buttonHairSelect = new GuiIconButton(parent, 0, 159 + 18, 38 - 2, 18, 18, GuiHelper.getLocalizedControlName(guiName, "selectHair"), TEXTURE).setIconLocation(238, 0, 18, 18).setHorizontal(false);
        buttonHairAuto = new GuiIconButton(parent, 0, 159 + 40, 38 - 2, 18, 18, GuiHelper.getLocalizedControlName(guiName, "autoHair"), TEXTURE).setIconLocation(238, 76, 18, 18).setHorizontal(false);

        // buttonEyeSelect = new GuiIconButton(parent, 0, 83 + 18, 70 - 2, 18, 18, GuiHelper.getLocalizedControlName(guiName, "selectEye"), TEXTURE).setIconLocation(238, 0, 18, 18).setHorizontal(false);
        // buttonEyeAuto = new GuiIconButton(parent, 0, 83 + 40, 70 - 2, 18, 18, GuiHelper.getLocalizedControlName(guiName, "autoEye"), TEXTURE).setIconLocation(238, 76, 18, 18).setHorizontal(false);

        // buttonMiscSelect = new GuiIconButton(parent, 0, 159 + 18, 70 - 2, 18, 18, GuiHelper.getLocalizedControlName(guiName, "selectAcc"), TEXTURE).setIconLocation(238, 0, 18, 18).setHorizontal(false);

        buttonList.add(buttonSkinSelect);
        buttonList.add(buttonSkinAuto);

        buttonList.add(buttonHairSelect);
        buttonList.add(buttonHairAuto);

        // buttonList.add(buttonEyeSelect);
        // buttonList.add(buttonEyeAuto);

        // buttonList.add(buttonMiscSelect);
    }

    private void getColours() {
        ExtraColours extraColours = propsPlayerSkinData.getEquipmentWardrobeData().getExtraColours();
        this.colourSkin = new Color(extraColours.getColour(ExtraColourType.SKIN));
        this.colourHair = new Color(extraColours.getColour(ExtraColourType.HAIR));
        // this.colourEye = new Color(extraColours.getColour(ExtraColourType.EYE));
        // this.colourMisc = new Color(extraColours.getColour(ExtraColourType.MISC));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (button == 0 & selectingColourType != null) {
            EquipmentWardrobeData ewd = new EquipmentWardrobeData(this.equipmentWardrobeData);
            byte[] newColour = PaintingHelper.intToBytes(selectingColour.getRGB());
            newColour[3] = (byte) PaintType.NORMAL.getKey();
            ewd.getExtraColours().setColourBytes(selectingColourType, newColour);
            PacketHandler.networkWrapper.sendToServer(new MessageClientSkinWardrobeUpdate(ewd));
            selectingColourType = null;
            buttonSkinSelect.setPressed(false);
            buttonHairSelect.setPressed(false);
            // buttonEyeSelect.setPressed(false);
            // buttonMiscSelect.setPressed(false);
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button == buttonSkinSelect) {
            selectingColourType = ExtraColourType.SKIN;
            buttonSkinSelect.setPressed(true);
        }
        if (button == buttonHairSelect) {
            selectingColourType = ExtraColourType.HAIR;
            buttonHairSelect.setPressed(true);
        }
        // if (button == buttonEyeSelect) {
        // selectingColourType = ExtraColourType.EYE;
        // buttonEyeSelect.setPressed(true);
        // }
        // if (button == buttonMiscSelect) {
        // selectingColourType = ExtraColourType.MISC;
        // buttonMiscSelect.setPressed(true);
        // }

        if (button == buttonSkinAuto) {
            EquipmentWardrobeData ewd = new EquipmentWardrobeData(this.equipmentWardrobeData);
            int newSkinColour = autoColour((AbstractClientPlayer) this.entityPlayer, ExtraColourType.SKIN);
            ewd.getExtraColours().setColour(ExtraColourType.SKIN, newSkinColour);
            PacketHandler.networkWrapper.sendToServer(new MessageClientSkinWardrobeUpdate(ewd));
        }

        if (button == buttonHairAuto) {
            EquipmentWardrobeData ewd = new EquipmentWardrobeData(this.equipmentWardrobeData);
            int newHairColour = autoColour((AbstractClientPlayer) this.entityPlayer, ExtraColourType.HAIR);
            ewd.getExtraColours().setColour(ExtraColourType.HAIR, newHairColour);
            PacketHandler.networkWrapper.sendToServer(new MessageClientSkinWardrobeUpdate(ewd));
        }

        // if (button == buttonEyeAuto) {
        // EquipmentWardrobeData ewd = new EquipmentWardrobeData(this.equipmentWardrobeData);
        // int newEyeColour = autoColour((AbstractClientPlayer) this.entityPlayer, ExtraColourType.EYE);
        // ewd.getExtraColours().setColour(ExtraColourType.EYE, newEyeColour);
        // PacketHandler.networkWrapper.sendToServer(new MessageClientSkinWardrobeUpdate(ewd));
        // }
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        // Top half of GUI. (active tab)
        // this.drawTexturedModalRect(this.x, this.y, 0, 0, 236, 151);

        // Bottom half of GUI. (player inventory)
        // this.drawTexturedModalRect(this.x + 29, this.y + 151, 29, 151, 178, 89);

        // Skin colour display
        drawColourDisplay(83, 38, colourSkin);

        // Hair colour display
        drawColourDisplay(159, 38, colourHair);

        // Eye colour display
        // drawColourDisplay(83, 70, colourEye);

        // Acc colour display
        // drawColourDisplay(159, 70, colourMisc);

        // Palette
        mc.renderEngine.bindTexture(TEXTURE);
        this.drawTexturedModalRect(this.x + 83, this.y + 88, 22, 0, 128, 56);
    }

    private void drawColourDisplay(int x, int y, Color colour) {
        float r = (float) colour.getRed() / 255F;
        float g = (float) colour.getGreen() / 255F;
        float b = (float) colour.getBlue() / 255F;
        drawColourDisplay(x, y, r, g, b);
    }

    private void drawColourDisplay(int x, int y, float r, float g, float b) {
        this.drawTexturedModalRect(this.x + x, this.y + y, 242, 180, 14, 14);
        GL11.glColor4f(r, g, b, 1F);
        this.drawTexturedModalRect(this.x + x + 1, this.y + y + 1, 243, 181, 12, 12);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);

        // String labelSkinOverride = GuiHelper.getLocalizedControlName("equipmentWardrobe", "label.skinOverride");
        // this.fontRendererObj.drawString(labelSkinOverride + ":", 165, 18, 4210752);

        fontRenderer.drawString(GuiHelper.getLocalizedControlName(guiName, "label.skinColour") + ":", 83, 26, 4210752);
        fontRenderer.drawString(GuiHelper.getLocalizedControlName(guiName, "label.hairColour") + ":", 159, 26, 4210752);

        //fontRenderer.drawString(GuiHelper.getLocalizedControlName(guiName, "label.eyeColour") + ":", 83, 58, 4210752);
        //fontRenderer.drawString(GuiHelper.getLocalizedControlName(guiName, "label.miscColour") + ":", 159, 58, 4210752);

        getColours();

        if (selectingColourType == ExtraColourType.SKIN & selectingColour != null) {
            colourSkin = selectingColour;
        }
        if (selectingColourType == ExtraColourType.HAIR & selectingColour != null) {
            colourHair = selectingColour;
        }
        // if (selectingColourType == ExtraColourType.EYE & selectingColour != null) {
        // colourEye = selectingColour;
        // }
        // if (selectingColourType == ExtraColourType.MISC & selectingColour != null) {
        // colourMisc = selectingColour;
        // }

        GL11.glPushMatrix();
        GL11.glTranslated(-x, -y, 0);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        ModRenderHelper.enableAlphaBlend();
        // Draw player preview.
        if (selectingColourType != null) {
            selectingColour = ((GuiWardrobe) parent).drawPlayerPreview(x, y, mouseX, mouseY, true);
        } else {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            ModRenderHelper.enableAlphaBlend();
            ((GuiWardrobe) parent).drawPlayerPreview(x, y, mouseX, mouseY, false);
        }
        GL11.glPopMatrix();
        for (int i = 0; i < buttonList.size(); i++) {
            GuiButton button = (GuiButton) buttonList.get(i);
            if (button instanceof GuiIconButton) {
                ((GuiIconButton) button).drawRollover(mc, mouseX - x, mouseY - y);
            }
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public int autoColour(AbstractClientPlayer player, ExtraColourType type) {
        BufferedImage playerTexture = SkinHelper.getBufferedImageSkin(player);
        if (playerTexture == null) {
            return ExtraColours.COLOUR_HAIR_DEFAULT.getRGB();
        }

        int r = 0, g = 0, b = 0;

        if (type == ExtraColourType.SKIN) {
            for (int ix = 0; ix < 2; ix++) {
                for (int iy = 0; iy < 1; iy++) {
                    Color c = new Color(playerTexture.getRGB(ix + 11, iy + 13));
                    r += c.getRed();
                    g += c.getGreen();
                    b += c.getBlue();
                }
            }
            r = r / 2;
            g = g / 2;
            b = b / 2;
        }
        if (type == ExtraColourType.HAIR) {
            for (int ix = 0; ix < 2; ix++) {
                for (int iy = 0; iy < 1; iy++) {
                    Color c = new Color(playerTexture.getRGB(ix + 11, iy + 3));
                    r += c.getRed();
                    g += c.getGreen();
                    b += c.getBlue();
                }
            }
            r = r / 2;
            g = g / 2;
            b = b / 2;
        }
        if (type == ExtraColourType.EYE) {
            Color c1 = new Color(playerTexture.getRGB(10, 13));
            Color c2 = new Color(playerTexture.getRGB(13, 13));

            r += c1.getRed();
            g += c1.getGreen();
            b += c1.getBlue();

            r += c2.getRed();
            g += c2.getGreen();
            b += c2.getBlue();

            r = r / 2;
            g = g / 2;
            b = b / 2;
        }
        return new Color(r, g, b).getRGB();
    }
}
