package riskyken.armourersWorkshop.client.guidebook;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BookChapter implements IBookChapter {
    
    private static final int PAGE_WIDTH = 104;
    private static final int PAGE_HEIGHT = 130;
    
    private final IBook parentBook;
    private final String name;
    private final int numberOfParagraphs;
    private final ArrayList<IBookPage> pages;
    
    public BookChapter(IBook parentBook, String name, int numberOfParagraphs) {
        this.parentBook = parentBook;
        this.name = name;
        this.numberOfParagraphs = numberOfParagraphs;
        this.pages = new ArrayList<IBookPage>();
    }
    
    private String getParagraphText(int paragraphNumber) {
        return getLocalizedText(getUnlocalizedName() + ".paragraph" + paragraphNumber);
    }
    
    private String getLocalizedText(String unlocalizedText) {
        return I18n.format(unlocalizedText);
    }

    private ArrayList<IBookPage> createPagesForText(String text) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
        List<String> lines = fontRenderer.listFormattedStringToWidth(text, PAGE_WIDTH);
        ArrayList<IBookPage> pages = new ArrayList<IBookPage>();
        int linesPerPage = MathHelper.floor_double(PAGE_HEIGHT / fontRenderer.FONT_HEIGHT);
        
        ArrayList<String> pageLines = new ArrayList<String>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            //Stop an empty line at the top of a page.
            if (line.isEmpty() & pageLines.size() == 0) {
                continue;
            }
            pageLines.add(lines.get(i));
            if (pageLines.size() >= linesPerPage) {
                pages.add(new BookPage(parentBook, pageLines));
                pageLines = new ArrayList<String>();
            }
        }
        if (pageLines.size() > 0) {
            pages.add(new BookPage(parentBook, pageLines));
        }
        
        return pages;
    }
    
    @Override
    public void createPages() {
        String chapterText = "";
        for (int i = 0; i < this.numberOfParagraphs; i++) {
            chapterText += getParagraphText(i + 1);
            if (i != this.numberOfParagraphs) {
                chapterText += "\n\n";
            }
        }
        chapterText = chapterText.replaceAll("&n", "\n");
        chapterText = chapterText.replaceAll("&p", "\n\n");
        this.pages.addAll(createPagesForText(chapterText));
    }
    
    @Override
    public String getUnlocalizedName() {
        return parentBook.getUnlocalizedName() + "." + this.name;
    }
    
    @Override
    public int getNumberOfPages() {
        return this.pages.size();
    }
    
    @Override
    public IBookPage getPageNumber(int pageNumber) {
        return this.pages.get(pageNumber);
    }

    @Override
    public void addPage(IBookPage page) {
        this.pages.add(page);
    }
}
