package riskyken.armourersWorkshop.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.gui.controls.GuiBookButton;
import riskyken.armourersWorkshop.client.gui.controls.GuiBookTextButton;
import riskyken.armourersWorkshop.client.guidebook.GuideBook;
import riskyken.armourersWorkshop.client.guidebook.IBook;
import riskyken.armourersWorkshop.client.guidebook.IBookPage;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.utils.UtilColour;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiGuideBook extends GuiScreen {
    
    private static final ResourceLocation bookTexture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/guideBook.png");
    private static final ResourceLocation bookPageTexture = new ResourceLocation(LibModInfo.ID.toLowerCase(), "textures/gui/guideBookPage.png");
    
    private IBook book;
    
    private static int pageNumber = 1;
    
    private final int guiWidth;
    private final int guiHeight;
    private int guiLeft;
    private int guiTop;
    private ItemStack stack;
    
    private GuiBookButton backButton;
    private GuiBookButton forwardButton;
    
    @Override
    public void initGui() {
        super.initGui();
        guiLeft = width / 2 - guiWidth / 2;
        guiTop = height / 2 - guiHeight / 2;
        buttonList.clear();
        
        backButton = new GuiBookButton(0, this.guiLeft - 20, this.guiTop + 156, 3, 207, bookTexture);
        forwardButton = new GuiBookButton(1, this.guiLeft + 258, this.guiTop + 156, 3, 194, bookTexture);
        
        buttonList.add(backButton);
        buttonList.add(forwardButton);
        
        lastRenderTick = System.currentTimeMillis();
    }
    
    public GuiGuideBook(ItemStack stack) {
        this.stack = stack;
        book  = new GuideBook();
        guiWidth = 256;
        guiHeight = 180;
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) { startPageTurnRight(); }
        if (button.id == 1) { startPageTurnLeft(); }
        
        if (button.id > 1) {
            //setGoToChapter(button.id - 2);
        }
    }
    
    @Override
    protected void keyTyped(char key, int keyCode) {
        super.keyTyped(key, keyCode);
        if (keyCode == 1 || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.thePlayer.closeScreen();
        }
    }
    
    /** The amount the page has turned. 0.0 for none 1.0 for a full turn. */
    private float pageTurnAmount = 0F;
    PageState pageState = PageState.NONE;
    private float count;
    private long lastRenderTick;
    
    private void startPageTurnLeft() {
        pageState = PageState.TURN_LEFT;
        pageTurnAmount = 0F;
        nextPage();
    }
    
    private void startPageTurnRight() {
        pageState = PageState.TURN_RIGHT;
        pageTurnAmount = 0F;
        previousPage();
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partickTime) {
        GL11.glColor4f(1, 1, 1, 1);
        long tickTime = System.currentTimeMillis() - lastRenderTick;
        lastRenderTick = System.currentTimeMillis();
        
        //ModLogger.log(book.getNumberOfChapters());
        
        if (pageState != PageState.NONE) {
            float turnCenter = pageTurnAmount - 0.5F;
            if (turnCenter < 0) { turnCenter = -turnCenter; }
            turnCenter = -turnCenter + 0.5F;
            pageTurnAmount += ((0.006F) + (turnCenter * 0.08F) * (tickTime * 0.1F));
        }
        
        if (pageTurnAmount > 1F) {
            pageTurnAmount = 0F;
            pageState = PageState.NONE;
            count = 0;
        }
        
        if (pageState != PageState.NONE) {
            count = (pageState.movement * pageTurnAmount) * 180;
        }
        
        if (count > 90F) {
            count -= 180F;
        }
        if (count < -90F) {
            count += 180F;
        }
        
        mc.renderEngine.bindTexture(bookTexture);
        drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.guiWidth, this.guiHeight);
        
        for (int k = 0; k < this.buttonList.size(); ++k) {
            if (this.buttonList.get(k) instanceof GuiBookTextButton) {
                ((GuiButton)this.buttonList.get(k)).visible = pageNumber == 1;
                if (pageNumber == 3 & pageState == PageState.TURN_LEFT) {
                    ((GuiButton)this.buttonList.get(k)).visible = true;
                }
            }
        }
        backButton.visible = !book.isFirstPage(pageNumber);
        forwardButton.visible = !book.isLastPage(pageNumber + 1);
        
        for (int k = 0; k < this.buttonList.size(); ++k) {
            ((GuiButton)this.buttonList.get(k)).drawButton(this.mc, mouseX, mouseY);
        }
        
        if (pageState == PageState.NONE) {
            renderPageText(pageNumber, 17, 1);
            renderPageText(pageNumber + 1, 134, 2);
        }
        if (pageState == PageState.TURN_LEFT) {
            renderPageText(pageNumber - 2, 17, 1);
            renderPageText(pageNumber + 1, 134, 2);
        }
        if (pageState == PageState.TURN_RIGHT) {
            renderPageText(pageNumber, 17, 1);
            renderPageText(pageNumber + 3, 134, 2);
        }
        
        if (pageState != PageState.NONE) {
            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glTranslatef(this.guiLeft + 128, 0, 0);
            GL11.glRotatef(count, 0, 1, 0);
            GL11.glTranslatef(-(this.guiLeft + 128), 0, 0);
            GL11.glColor4f(1, 1, 1, 1);
            if (count >= 0) {
                mc.renderEngine.bindTexture(bookPageTexture);
                drawTexturedModalRect(this.guiLeft + 10, this.guiTop + 7, 10, 7, 118, 165);
                if (pageState == PageState.TURN_LEFT) {
                    renderPageText(pageNumber, 17, 1);
                }
                if (pageState == PageState.TURN_RIGHT) {
                    renderPageText(pageNumber + 2, 17, 1);
                }
            } else {
                GL11.glTranslatef(118, 0, 0);
                mc.renderEngine.bindTexture(bookPageTexture);
                drawTexturedModalRect(this.guiLeft + 10, this.guiTop + 7, 128, 7, 118, 165);
                GL11.glTranslatef(-118, 0, 0);
                if (pageState == PageState.TURN_LEFT) {
                    renderPageText(pageNumber - 1, 134, 2);
                }
                if (pageState == PageState.TURN_RIGHT) {
                    renderPageText(pageNumber + 1, 134, 2);
                }
            }
            
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glPopMatrix();
        }
    }
    
    private void renderPageText(int page, int left, int side) {
        //ModLogger.log(page);
        if (page < 1 | page > book.getTotalNumberOfPages()) {
            return;
        }
        
        IBookPage bookPage = book.getPageNumber(page);
        if (bookPage != null) {
            bookPage.renderPage(fontRendererObj, this.guiLeft + left, this.guiTop + 25);
        }
        
        String chapterTitle = book.getChapterFromPageNumber(page).getUnlocalizedName();
        chapterTitle = StatCollector.translateToLocal(chapterTitle + ".name");
        //String chapterTitle =  getLocalizedText(".chapter" + (getChapterFromPageNumber(page) + 1) + ".title");
        int pageCenter = 69;
        if (side == 2) {
            pageCenter = 186;
        }
        
        if (chapterTitle != null) {
            renderStringCenter(chapterTitle, this.guiLeft + left, this.guiTop + 12);
        }
        
        renderStringCenter( page + " - " + book.getTotalNumberOfPages(), this.guiLeft + left, this.guiTop + 160);
    }
    
    private void renderStringCenter(String text, int x, int y) {
        int xCenter = 104 / 2 - fontRendererObj.getStringWidth(text) / 2;
        fontRendererObj.drawString(text, x + xCenter, y, UtilColour.getMinecraftColor(7));
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    private void nextPage() {
        this.pageNumber += 2;
        if (pageNumber > book.getTotalNumberOfPages() + 1) {
            pageNumber = book.getTotalNumberOfPages();
        }
    }
    
    private void previousPage() {
        this.pageNumber -= 2;
        if (pageNumber < 1) {
            pageNumber = 1;
        }
    }
    
    private void goToPage(int pageNumber) {
        this.pageNumber = pageNumber;
    }
    
    private enum PageState {
        NONE(0),
        TURN_LEFT(-1),
        TURN_RIGHT(1);
        
        public final int movement;
        
        private PageState(int movement) {
            this.movement = movement;
        }
    }
}
