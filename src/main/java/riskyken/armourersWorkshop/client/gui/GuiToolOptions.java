package riskyken.armourersWorkshop.client.gui;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.PacketHandler;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiToolOptionUpdate;
import riskyken.armourersWorkshop.common.painting.tool.AbstractToolOption;
import riskyken.armourersWorkshop.common.painting.tool.IConfigurableTool;

@SideOnly(Side.CLIENT)
public class GuiToolOptions extends GuiScreen {
    
    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/toolOptions.png");
    private static final int MARGIN_TOP = 16;
    private static final int MARGIN_LEFT = 6;
    private static final int CONTROL_PADDING = 6;
    
    private final int guiWidth;
    private int guiHeight;
    protected int guiLeft;
    protected int guiTop;
    protected ItemStack stack;
    private String guiName;
    private final ArrayList<AbstractToolOption> toolOptionsList;
    
    public GuiToolOptions(ItemStack stack) {
        this.stack = stack;
        toolOptionsList = new ArrayList<AbstractToolOption>();
        ((IConfigurableTool)stack.getItem()).getToolOptions(toolOptionsList);
        guiWidth = 175;
        guiHeight = 61;
        this.guiName = stack.getDisplayName();
    }
    
    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        
        guiLeft = width / 2 - guiWidth / 2;
        guiTop = height / 2 - guiHeight / 2;
        
        int controlHeight = guiTop + MARGIN_TOP;
        //TODO Change the GUI height to fit the controls.
        /*
        for (int i = 0; i < toolOptionsList.size(); i++) {
            controlHeight += toolOptionsList.get(i).getDisplayHeight() + CONTROL_PADDING;
        }
        */
        for (int i = 0; i < toolOptionsList.size(); i++) {
            GuiButton control = toolOptionsList.get(i).getGuiControl(i, guiLeft + MARGIN_LEFT, controlHeight, stack.getTagCompound());
            buttonList.add(control);
            controlHeight += toolOptionsList.get(i).getDisplayHeight() + CONTROL_PADDING;
        }
        
        //guiHeight = controlHeight;
    }
    
    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        GL11.glColor4f(1, 1, 1, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.guiWidth, this.guiHeight);
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
        renderGuiTitle(fontRendererObj, guiName);
    }
    
    @Override
    protected void keyTyped(char key, int keyCode) {
        super.keyTyped(key, keyCode);
        if (keyCode == 1 || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.thePlayer.closeScreen();
        }
    }
    
    private void renderGuiTitle(FontRenderer fontRenderer, String name) {
        int xPos = this.guiWidth / 2 - fontRenderer.getStringWidth(name) / 2;
        fontRenderer.drawString(name, this.guiLeft + xPos, this.guiTop + 6, 4210752);
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    @Override
    public void onGuiClosed() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToCompound(compound);
        PacketHandler.networkWrapper.sendToServer(new MessageClientGuiToolOptionUpdate(compound));
    }
    
    public void writeToCompound(NBTTagCompound compound) {
        for (int i = 0; i < toolOptionsList.size(); i++) {
            toolOptionsList.get(i).writeToNBT(compound, (GuiButton) buttonList.get(i));
        }
    }
}
