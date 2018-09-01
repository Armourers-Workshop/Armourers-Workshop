package moe.plushie.armourers_workshop.client.guidebook;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IBookChapter {

    public void createPages();
    
    public String getUnlocalizedName();
    
    public int getNumberOfPages();
    
    public IBookPage getPageNumber(int pageNumber);
    
    public void addPage(IBookPage page);
}
