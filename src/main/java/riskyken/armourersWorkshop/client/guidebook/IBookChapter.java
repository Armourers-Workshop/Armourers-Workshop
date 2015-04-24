package riskyken.armourersWorkshop.client.guidebook;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IBookChapter {

    public void createPages();
    
    public String getUnlocalizedName();
    
    public int getNumberOfPages();
    
    public IBookPage getPageNumber(int pageNumber);
    
    public void addPage(IBookPage page);
}
