package riskyken.armourersWorkshop.client.guidebook;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IBook {
    
    public String getUnlocalizedName();
    
    public int getNumberOfChapters();
    
    public void addChapter(IBookChapter chapter);
    
    public IBookChapter getChapterNumber(int chapterNumber);
    
    public IBookChapter getChapterFromPageNumber(int pageNumber);
    
    public int getChapterIndexFromPageNumber(int pageNumber);
    
    public int getTotalNumberOfPages();
    
    public IBookPage getPageNumber(int pageNumber);
    
    public boolean isFirstPage(int pageNumber);
    
    public boolean isLastPage(int pageNumber);
}
