package riskyken.armourersWorkshop.client.guidebook;

import riskyken.armourersWorkshop.common.lib.LibModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuideBook extends BookBase {

    public GuideBook() {
        super(LibModInfo.ID.toLowerCase() + ":guideBook");
        registerChapters();
        createPages();
    }
    
    private void registerChapters() {
        addChapter(new BookChapter(this, "contents", 1));
        addChapter(new BookChapter(this, "introduction", 2));
        addChapter(new BookChapter(this, "armourer", 2));
        addChapter(new BookChapter(this, "equipmentTemplates", 1));
        addChapter(new BookChapter(this, "paintingTools", 4));
        addChapter(new BookChapter(this, "equipmentWardrobe", 2));
    }
    
    private void createPages() {
        for (int i = 0; i < getNumberOfChapters(); i++) {
            getChapterNumber(i).createPages();
        }
    }
}
