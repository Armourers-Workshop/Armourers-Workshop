package moe.plushie.armourers_workshop.client.guidebook;

import java.util.ArrayList;

public class BookChapterBase implements IBookChapter {
    
    protected static final int PAGE_WIDTH = 104;
    protected static final int PAGE_HEIGHT = 130;
    
    protected final IBook parentBook;
    protected final String name;
    protected final ArrayList<IBookPage> pages;
    
    public BookChapterBase(IBook parentBook, String name) {
        this.parentBook = parentBook;
        this.name = name;
        this.pages = new ArrayList<IBookPage>();
    }
    
    @Override
    public void createPages() {
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
