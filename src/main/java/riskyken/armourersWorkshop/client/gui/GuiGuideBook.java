package riskyken.armourersWorkshop.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.gui.controls.GuiBookButton;
import riskyken.armourersWorkshop.client.gui.controls.GuiBookTextButton;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.utils.UtilColour;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiGuideBook extends GuiScreen {
    
    private static final ResourceLocation texture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/guideBook.png");
    
    /** Holds the number of pages in each chapter **/
    private final int[] chapters = {2, 2, 4, 2, 2, 4, 4};
    private final String bookName;
    
    private static int pageNumber = 1;
    
    private final int guiWidth;
    private final int guiHeight;
    private int guiLeft;
    private int guiTop;
    private ItemStack stack;
    
    @Override
    public void initGui() {
        super.initGui();
        guiLeft = width / 2 - guiWidth / 2;
        guiTop = height / 2 - guiHeight / 2;
        buttonList.clear();
        
        buttonList.add(new GuiBookButton(0, this.guiLeft + 26, this.guiTop + 156, 3, 207, texture));
        buttonList.add(new GuiBookButton(1, this.guiLeft + 212, this.guiTop + 156, 3, 194, texture));
        
        for (int i = 0; i < chapters.length; i++) {
            String chapterList =  getLocalizedText(".chapter" + (i + 1) + ".title");
            buttonList.add(new GuiBookTextButton(i + 2, this.guiLeft + 17, this.guiTop + 25 + 14 * i, fontRendererObj.getStringWidth(chapterList), chapterList));
        }
        
    }
    
    public GuiGuideBook(ItemStack stack) {
        this.stack = stack;
        guiWidth = 256;
        guiHeight = 180;
        this.bookName = "guideBook";
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) { previousPage(); }
        if (button.id == 1) { nextPage(); }
        
        if (button.id > 1) {
            setGoToChapter(button.id - 2);
        }
    }
    
    @Override
    protected void keyTyped(char key, int keyCode) {
        super.keyTyped(key, keyCode);
        if (keyCode == 1 || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.thePlayer.closeScreen();
        }
    }
    
    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        GL11.glColor4f(1, 1, 1, 1);
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.guiWidth, this.guiHeight);
        
        renderPageText(pageNumber, 17, 1);
        renderPageText(pageNumber + 1, 134, 2);
        
        
        for (int k = 0; k < this.buttonList.size(); ++k) {
            if (this.buttonList.get(k) instanceof GuiBookTextButton) {
                if (pageNumber == 1) {
                    ((GuiButton)this.buttonList.get(k)).drawButton(this.mc, p_73863_1_, p_73863_2_);
                }
            } else {
                if (((GuiButton)this.buttonList.get(k)).id == 0) {
                    if (!isFirstPage(pageNumber)) {
                        ((GuiButton)this.buttonList.get(k)).drawButton(this.mc, p_73863_1_, p_73863_2_);
                    }
                } else if (((GuiButton)this.buttonList.get(k)).id == 1) {
                    if (!isLastPage(pageNumber)) {
                        ((GuiButton)this.buttonList.get(k)).drawButton(this.mc, p_73863_1_, p_73863_2_);
                    }
                } else {
                    ((GuiButton)this.buttonList.get(k)).drawButton(this.mc, p_73863_1_, p_73863_2_);
                }
                
            }
            
        }

        for (int k = 0; k < this.labelList.size(); ++k) {
            if (this.buttonList.get(k) instanceof GuiBookTextButton) {
                if (pageNumber == 1) {
                    ((GuiLabel)this.labelList.get(k)).func_146159_a(this.mc, p_73863_1_, p_73863_2_);
                }
            } else {
                if (((GuiButton)this.buttonList.get(k)).id == 0) {
                    if (!isFirstPage(pageNumber)) {
                        ((GuiLabel)this.labelList.get(k)).func_146159_a(this.mc, p_73863_1_, p_73863_2_);
                    }
                } else if (((GuiButton)this.buttonList.get(k)).id == 1) {
                    if (!isLastPage(pageNumber)) {
                        ((GuiLabel)this.labelList.get(k)).func_146159_a(this.mc, p_73863_1_, p_73863_2_);
                    }
                } else {
                    ((GuiLabel)this.labelList.get(k)).func_146159_a(this.mc, p_73863_1_, p_73863_2_);
                }
            }
        }
    }
    
    private void renderPageText(int page, int left, int side) {
        String pageText =  getLocalizedText(".chapter" + (getChapterFromPageNumber(page) + 1) + ".page" + getChapterPageFromPageNumber(page) + ".text");
        String chapterTitle =  getLocalizedText(".chapter" + (getChapterFromPageNumber(page) + 1) + ".title");
        int pageCenter = 69;
        if (side == 2) {
            pageCenter = 186;
        }
        
        if (pageText != null) {
            fontRendererObj.drawSplitString(pageText, this.guiLeft + left, this.guiTop + 25, 104, UtilColour.getMinecraftColor(8));
        }
        if (chapterTitle != null) {
            renderStringCenter(chapterTitle, this.guiLeft + left, this.guiTop + 12);
        }
        
        renderStringCenter( page + " - " + getTotalPages(), this.guiLeft + left, this.guiTop + 160);
    }
    
    private void renderStringCenter(String text, int x, int y) {
        int xCenter = 104 / 2 - fontRendererObj.getStringWidth(text) / 2;
        fontRendererObj.drawString(text, x + xCenter, y, UtilColour.getMinecraftColor(7));
    }
    
    private String getLocalizedText(String item) {
        String unlocalized =  "book." + LibModInfo.ID.toLowerCase() + ":" + bookName + item;
        String localized = StatCollector.translateToLocal(unlocalized);
        if (!unlocalized.equals(localized)) {
            localized = localized.replaceAll("&n", "\n");
            localized = localized.replaceAll("&p", "\n\n");
            return localized;
        }
        return null;
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    private boolean isFirstPage(int pageNumber) {
        return pageNumber == 1;
    }
    
    private boolean isLastPage(int pageNumber) {
        return pageNumber >= getTotalPages() - 1;
    }
    
    private void nextPage() {
        this.pageNumber += 2;
        if (pageNumber >= getTotalPages() + 1) { pageNumber = getTotalPages() - 1; }
    }
    
    private void previousPage() {
        this.pageNumber -= 2;
        if (pageNumber < 1) { pageNumber = 1; }
    }
    
    private void setGoToChapter(int chapterNumber) {
        int pageCount = 0;
        for (int i = 0; i < chapters.length; i++) {
            if (chapterNumber == i) {
                this.pageNumber = pageCount + 1;
            }
            pageCount += getPagesInChapter(i);
        }
    }
    
    private void goToPage(int pageNumber) {
        this.pageNumber = pageNumber;
    }
    
    private int getChapterPageFromPageNumber(int pageNumber) {
        int pageCount = 0;
        for (int i = 0; i < getChapterFromPageNumber(pageNumber); i++) {
            if ((chapters[i] & 1) == 0 ) {
                pageCount += chapters[i];
            } else {
                pageCount += chapters[i] + 1;
            }
        }
        return pageNumber - pageCount;
    }
    
    private int getChapterFromPageNumber(int pageNumber) {
        int pageCount = 0;
        for (int i = 0; i < chapters.length; i++) {
            pageCount += getPagesInChapter(i);
            if (pageCount >= pageNumber) {
                return i;
            }
        }
        return 0;
    }
    
    private int getPagesInChapter(int chapterNumber) {
        if ((chapters[chapterNumber] & 1) == 0 ) {
            return chapters[chapterNumber];
        } else {
            return chapters[chapterNumber] + 1;
        }
    }
    
    private int getTotalPages() {
        int pageCount = 0;
        for (int i = 0; i < chapters.length; i++) {
            pageCount += getPagesInChapter(i);
        }
        return pageCount;
    }
}
