package moe.plushie.armourers_workshop.client.guidebook;

import java.util.ArrayList;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class BookBase implements IBook {
    
    private String name;
    private ArrayList<IBookChapter> chapters;
    
    public BookBase(String name) {
        this.name = name;
        chapters = new ArrayList<IBookChapter>();
    }
    
    @Override
    public String getUnlocalizedName() {
        return "book." + this.name;
    }
    
    @Override
    public int getNumberOfChapters() {
        return this.chapters.size();
    }
    
    @Override
    public void addChapter(IBookChapter chapter) {
        this.chapters.add(chapter);
    }
    
    @Override
    public IBookChapter getChapterNumber(int chapterNumber) {
        return this.chapters.get(chapterNumber);
    }
    
    @Override
    public IBookChapter getChapterFromPageNumber(int pageNumber) {
        return this.chapters.get(getChapterIndexFromPageNumber(pageNumber));
    }
    
    @Override
    public int getChapterIndexFromPageNumber(int pageNumber) {
        int pageCount = pageNumber;
        for (int i = 0; i < chapters.size(); i++) {
            IBookChapter chapter = chapters.get(i);
            int pagesInChapter = chapter.getNumberOfPages();
            if (pageCount <= pagesInChapter) {
                return i;
            }
            pageCount -= pagesInChapter;
        }
        return -1;
    }
    
    @Override
    public int getTotalNumberOfPages() {
        int count = 0;
        for (int i = 0; i < chapters.size(); i++) {
            count += chapters.get(i).getNumberOfPages();
        }
        return count;
    }
    
    @Override
    public IBookPage getPageNumber(int pageNumber) {
        int pageCount = pageNumber;
        for (int i = 0; i < chapters.size(); i++) {
            IBookChapter chapter = chapters.get(i);
            int pagesInChapter = chapter.getNumberOfPages();
            if (pageCount <= pagesInChapter) {
                return chapter.getPageNumber(pageCount - 1);
            }
            pageCount -= pagesInChapter;
        }
        return null;
    }
    
    @Override
    public boolean isFirstPage(int pageNumber) {
        return pageNumber <= 1;
    }
    
    @Override
    public boolean isLastPage(int pageNumber) {
        return pageNumber >= getTotalNumberOfPages();
    }
}
