package riskyken.armourersWorkshop.client.guidebook;

import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuideBook extends BookBase {

    private BookChapter chapterContents;
    private BookChapter chapterIntroduction;
    private BookChapter chapterArmourer;
    private BookChapter chapterEquipmentTemplates;
    private BookChapter chapterPaintingTools;
    private BookChapter chapterEquipmentWardrobe;
    private BookChapter chapterRecipes;
    
    public GuideBook() {
        super(LibModInfo.ID.toLowerCase() + ":guideBook");
        registerChapters();
        createPages();
    }
    
    private void registerChapters() {
        chapterContents = new BookChapter(this, "contents", 1);
        chapterIntroduction = new BookChapter(this, "introduction", 2);
        chapterArmourer = new BookChapter(this, "armourer", 2);
        chapterEquipmentTemplates = new BookChapter(this, "equipmentTemplates", 1);
        chapterPaintingTools = new BookChapter(this, "paintingTools", 4);
        chapterEquipmentWardrobe = new  BookChapter(this, "equipmentWardrobe", 2);
        chapterRecipes = new  BookChapter(this, "recipes", 0);
        
        //Blocks
        chapterRecipes.addPage(new BookPageRecipe(ModBlocks.armourerBrain));
        chapterRecipes.addPage(new BookPageRecipe(ModBlocks.armourLibrary));
        chapterRecipes.addPage(new BookPageRecipe(ModBlocks.colourable));
        chapterRecipes.addPage(new BookPageRecipe(ModBlocks.colourableGlass));
        chapterRecipes.addPage(new BookPageRecipe(ModBlocks.colourableGlowing));
        chapterRecipes.addPage(new BookPageRecipe(ModBlocks.colourableGlassGlowing));
        chapterRecipes.addPage(new BookPageRecipe(ModBlocks.colourMixer));
        chapterRecipes.addPage(new BookPageRecipe(ModBlocks.mannequin));
        chapterRecipes.addPage(new BookPageRecipe(ModBlocks.skinningTable));
        
        //Items
        chapterRecipes.addPage(new BookPageRecipe(ModItems.paintbrush));
        chapterRecipes.addPage(new BookPageRecipe(ModItems.paintRoller));
        chapterRecipes.addPage(new BookPageRecipe(ModItems.burnTool));
        chapterRecipes.addPage(new BookPageRecipe(ModItems.dodgeTool));
        chapterRecipes.addPage(new BookPageRecipe(ModItems.shadeNoiseTool));
        chapterRecipes.addPage(new BookPageRecipe(ModItems.colourNoiseTool));
        chapterRecipes.addPage(new BookPageRecipe(ModItems.colourPicker));
        chapterRecipes.addPage(new BookPageRecipe(ModItems.soap));
        chapterRecipes.addPage(new BookPageRecipe(ModItems.wandOfStyle));
        
        addChapter(chapterContents);
        addChapter(chapterIntroduction);
        addChapter(chapterArmourer);
        addChapter(chapterEquipmentTemplates);
        addChapter(chapterPaintingTools);
        addChapter(chapterEquipmentWardrobe);
        addChapter(chapterRecipes);
    }
    
    private void createPages() {
        for (int i = 0; i < getNumberOfChapters(); i++) {
            getChapterNumber(i).createPages();
        }
    }
}
