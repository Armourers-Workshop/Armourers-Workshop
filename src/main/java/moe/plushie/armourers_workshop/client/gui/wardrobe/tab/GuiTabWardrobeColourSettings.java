package moe.plushie.armourers_workshop.client.gui.wardrobe.tab;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.IExtraColours;
import moe.plushie.armourers_workshop.api.common.IExtraColours.ExtraColourType;
import moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability;
import moe.plushie.armourers_workshop.api.common.capability.IWardrobeCap;
import moe.plushie.armourers_workshop.client.gui.GuiHelper;
import moe.plushie.armourers_workshop.client.gui.controls.GuiIconButton;
import moe.plushie.armourers_workshop.client.gui.controls.GuiTabPanel;
import moe.plushie.armourers_workshop.client.gui.style.GuiResourceManager;
import moe.plushie.armourers_workshop.client.gui.style.GuiStyle;
import moe.plushie.armourers_workshop.client.gui.wardrobe.GuiWardrobe;
import moe.plushie.armourers_workshop.client.lib.LibGuiResources;
import moe.plushie.armourers_workshop.client.render.ModRenderHelper;
import moe.plushie.armourers_workshop.common.TextureHelper;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.painting.PaintTypeRegistry;
import moe.plushie.armourers_workshop.common.painting.PaintingHelper;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTabWardrobeColourSettings extends GuiTabPanel {

    private static final ResourceLocation TEXTURE_1 = new ResourceLocation(LibGuiResources.GUI_WARDROBE_1);
    private static final ResourceLocation TEXTURE_2 = new ResourceLocation(LibGuiResources.GUI_WARDROBE_2);
    private static final ResourceLocation GUI_JSON = new ResourceLocation(LibGuiResources.JSON_WARDROBE);
    private static final ResourceLocation TEXTURE_BUTTONS = new ResourceLocation(LibGuiResources.CONTROL_BUTTONS);

    private final GuiStyle guiStyle;
    private EntityPlayer entityPlayer;
    private IEntitySkinCapability skinCapability;
    private IWardrobeCap wardrobeCapability;

    private ExtraColourType selectingColourType = null;
    private Color selectingColour = null;

    private int colourPadding = 29;
    private ExtraColourType[] coloursLeft = new ExtraColourType[] { ExtraColourType.SKIN, ExtraColourType.HAIR, ExtraColourType.EYE };
    private ExtraColourType[] coloursRight = new ExtraColourType[] { ExtraColourType.MISC_1, ExtraColourType.MISC_2, ExtraColourType.MISC_3, ExtraColourType.MISC_4 };

    private Color[] colours;

    private GuiIconButton[] buttonsSelect;
    private GuiIconButton[] buttonsClear;
    private GuiIconButton[] buttonsAuto;

    String guiName = "wardrobe.tab.colour_settings";

    public GuiTabWardrobeColourSettings(int tabId, GuiScreen parent, EntityPlayer entityPlayer, IEntitySkinCapability skinCapability, IWardrobeCap wardrobeCapability) {
        super(tabId, parent, false);
        this.guiStyle = GuiResourceManager.getGuiJsonInfo(GUI_JSON);
        this.entityPlayer = entityPlayer;
        this.skinCapability = skinCapability;
        this.wardrobeCapability = wardrobeCapability;
        getColours();
    }

    @Override
    public void initGui(int xPos, int yPos, int width, int height) {
        super.initGui(xPos, yPos, width, height);

        buttonsSelect = new GuiIconButton[coloursLeft.length + coloursRight.length];
        buttonsClear = new GuiIconButton[coloursLeft.length + coloursRight.length];
        buttonsAuto = new GuiIconButton[3];

        for (int i = 0; i < buttonsSelect.length; i++) {
            if (i < coloursLeft.length) {
                buttonsSelect[i] = new GuiIconButton(parent, 0, 98, 35 + i * colourPadding, 16, 16, TEXTURE_BUTTONS);
            } else {
                buttonsSelect[i] = new GuiIconButton(parent, 0, 98 + 95, 35 + i * colourPadding - (coloursLeft.length * colourPadding), 16, 16, TEXTURE_BUTTONS);
            }
            buttonsSelect[i].setHoverText(GuiHelper.getLocalizedControlName(guiName, "button." + ExtraColourType.values()[i].toString().toLowerCase() + ".select"));
            buttonsSelect[i].setIconLocation(144, 192, 16, 16).setDrawButtonBackground(false);
            buttonList.add(buttonsSelect[i]);
        }

        for (int i = 0; i < buttonsClear.length; i++) {
            if (i < coloursLeft.length) {
                buttonsClear[i] = new GuiIconButton(parent, 0, 98 + 17, 35 + i * colourPadding, 16, 16, TEXTURE_BUTTONS);
            } else {
                buttonsClear[i] = new GuiIconButton(parent, 0, 98 + 95 + 17, 35 + i * colourPadding - (coloursLeft.length * colourPadding), 16, 16, TEXTURE_BUTTONS);
            }
            buttonsClear[i].setHoverText(GuiHelper.getLocalizedControlName(guiName, "button." + ExtraColourType.values()[i].toString().toLowerCase() + ".clear"));
            buttonsClear[i].setIconLocation(208, 160, 16, 16).setDrawButtonBackground(false);
            buttonList.add(buttonsClear[i]);
        }

        for (int i = 0; i < buttonsAuto.length; i++) {
            if (i < coloursLeft.length) {
                buttonsAuto[i] = new GuiIconButton(parent, 0, 98 + 17 * 2, 35 + i * colourPadding, 16, 16, TEXTURE_BUTTONS);
            } else {
                buttonsAuto[i] = new GuiIconButton(parent, 0, 98 + 95 + 17 * 2, 35 + i * colourPadding - (coloursLeft.length * colourPadding), 16, 16, TEXTURE_BUTTONS);
            }
            buttonsAuto[i].setHoverText(GuiHelper.getLocalizedControlName(guiName, "button." + ExtraColourType.values()[i].toString().toLowerCase() + ".auto"));
            buttonsAuto[i].setIconLocation(144, 208, 16, 16).setDrawButtonBackground(false);
            buttonList.add(buttonsAuto[i]);
        }
    }

    private void getColours() {
        IExtraColours extraColours = wardrobeCapability.getExtraColours();
        colours = new Color[ExtraColourType.values().length];
        for (int i = 0; i < colours.length; i++) {
            colours[i] = new Color(extraColours.getColour(ExtraColourType.values()[i]), true);
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (button == 0 & selectingColourType != null) {
            byte[] newColour = PaintingHelper.intToBytes(selectingColour.getRGB());
            newColour[3] = (byte) PaintTypeRegistry.PAINT_TYPE_NORMAL.getId();
            wardrobeCapability.getExtraColours().setColourBytes(selectingColourType, newColour);
            wardrobeCapability.sendUpdateToServer();
            selectingColourType = null;
            for (int i = 0; i < buttonsSelect.length; i++) {
                buttonsSelect[i].setPressed(false);
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        for (int i = 0; i < buttonsSelect.length; i++) {
            if (button == buttonsSelect[i]) {
                selectingColourType = ExtraColourType.values()[i];
                buttonsSelect[i].setPressed(true);
            }
        }

        for (int i = 0; i < buttonsAuto.length; i++) {
            if (button == buttonsAuto[i]) {
                int newColour = autoColour((AbstractClientPlayer) this.entityPlayer, ExtraColourType.values()[i]);
                wardrobeCapability.getExtraColours().setColour(ExtraColourType.values()[i], newColour);
                wardrobeCapability.sendUpdateToServer();
            }
        }

        for (int i = 0; i < buttonsClear.length; i++) {
            if (button == buttonsClear[i]) {
                wardrobeCapability.getExtraColours().setColour(ExtraColourType.values()[i], ExtraColours.COLOUR_NONE);
            }
        }
    }

    @Override
    public void drawBackgroundLayer(float partialTickTime, int mouseX, int mouseY) {
        GlStateManager.color(1F, 1F, 1F, 1F);

        // Colour display boxes.
        for (int i = 0; i < coloursLeft.length; i++) {
            drawColourDisplay(83, 36 + colourPadding * i, colours[i]);
        }
        for (int i = 0; i < coloursRight.length; i++) {
            drawColourDisplay(83 + 95, 36 + colourPadding * i, colours[i + coloursLeft.length]);
        }
        
        GlStateManager.color(1F, 1F, 1F, 1F);

        // Palette
        mc.renderEngine.bindTexture(TEXTURE_1);
        this.drawTexturedModalRect(this.x, this.y + 152, 0, 152, 256, 98);

        mc.renderEngine.bindTexture(TEXTURE_2);
        this.drawTexturedModalRect(this.x + 256, this.y + 152, 0, 152, 22, 98);
    }

    private void drawColourDisplay(int x, int y, Color colour) {
        float r = colour.getRed() / 255F;
        float g = colour.getGreen() / 255F;
        float b = colour.getBlue() / 255F;
        float t = colour.getAlpha() / 255F;
        drawColourDisplay(x, y, r, g, b, t);
    }

    private void drawColourDisplay(int x, int y, float r, float g, float b, float t) {
        if (t != 0) {
            this.drawTexturedModalRect(this.x + x, this.y + y, 242, 180, 14, 14);
            GlStateManager.color(r, g, b, 1F);
            this.drawTexturedModalRect(this.x + x + 1, this.y + y + 1, 243, 181, 12, 12);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            this.drawTexturedModalRect(this.x + x, this.y + y, 242, 180 - 14, 14, 14);
        }
    }

    @Override
    public void drawForegroundLayer(int mouseX, int mouseY, float partialTickTime) {
        super.drawForegroundLayer(mouseX, mouseY, partialTickTime);

        // String labelSkinOverride =
        // GuiHelper.getLocalizedControlName("equipmentWardrobe", "label.skinOverride");
        // this.fontRendererObj.drawString(labelSkinOverride + ":", 165, 18, 4210752);
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.resetColor();

        for (int i = 0; i < coloursLeft.length; i++) {
            fontRenderer.drawString(GuiHelper.getLocalizedControlName(guiName, "label." + coloursLeft[i].toString().toLowerCase()), 83, 26 + i * colourPadding, guiStyle.getColour("text"));
        }
        for (int i = 0; i < coloursRight.length; i++) {
            fontRenderer.drawString(GuiHelper.getLocalizedControlName(guiName, "label." + coloursRight[i].toString().toLowerCase()), 83 + 95, 26 + i * colourPadding, guiStyle.getColour("text"));
        }
        
        fontRenderer.drawString(GuiHelper.getLocalizedControlName(guiName, "label.palette"), 6, 152 + 5, guiStyle.getColour("text"));

        getColours();

        if (selectingColour != null & selectingColourType != null) {
            colours[selectingColourType.ordinal()] = selectingColour;
        }

        GL11.glPushMatrix();
        GL11.glTranslated(-x, -y, 0);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        ModRenderHelper.enableAlphaBlend();
        // Draw player preview.
        if (selectingColourType != null) {
            selectingColour = ((GuiWardrobe) parent).drawPlayerPreview(x, y, mouseX, mouseY, true);
        } else {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
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
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public int autoColour(AbstractClientPlayer player, ExtraColourType type) {
        BufferedImage playerTexture = TextureHelper.getBufferedImageSkin(player);
        if (playerTexture == null) {
            return ExtraColours.COLOUR_NONE;
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
