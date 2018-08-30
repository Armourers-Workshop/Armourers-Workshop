package riskyken.armourersWorkshop.client.guidebook;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import riskyken.armourersWorkshop.utils.UtilColour;
import riskyken.armourersWorkshop.utils.UtilColour.ColourFamily;

public class BookPageContents extends BookPageBase {

    public BookPageContents(IBook parentBook) {
        super(parentBook);
    }

    @Override
    public void renderPage(FontRenderer fontRenderer, int mouseX, int mouseY, boolean turning, int pageNumber) {
        drawPageTitleAndNumber(fontRenderer, pageNumber);
        for (int i = 0; i < parentBook.getNumberOfChapters(); i++) {
            IBookChapter chapter  = parentBook.getChapterNumber(i);
            String chapterTitle = chapter.getUnlocalizedName();
            chapterTitle = I18n.format(chapterTitle + ".name");
            fontRenderer.drawString(chapterTitle, PAGE_PADDING_LEFT,
                    PAGE_MARGIN_TOP  + fontRenderer.FONT_HEIGHT * 2 + i * 16, UtilColour.getMinecraftColor(7, ColourFamily.MINECRAFT));
        }
    }

    @Override
    public void renderRollover(FontRenderer fontRenderer, int mouseX, int mouseY) {
        // TODO Auto-generated method stub
        
    }
}
