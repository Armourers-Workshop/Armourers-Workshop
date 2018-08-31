package riskyken.armourers_workshop.client.guidebook;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import riskyken.armourers_workshop.common.blocks.ModBlocks;
import riskyken.armourers_workshop.common.items.ModItems;
import riskyken.armourers_workshop.common.lib.LibModInfo;

@SideOnly(Side.CLIENT)
public class GuideBook extends BookBase {

    private IBookChapter chapterContents;
    private IBookChapter chapterIntroduction;
    private IBookChapter chapterArmourer;
    private IBookChapter chapterEquipmentTemplates;
    private IBookChapter chapterPaintingTools;
    private IBookChapter chapterEquipmentWardrobe;
    private IBookChapter chapterRecipes;
    private IBookChapter chapterCredits;
    
    public GuideBook() {
        super(LibModInfo.ID.toLowerCase() + ":guideBook");
        registerChapters();
        createPages();
    }
    
    private void registerChapters() {
        //Add chapters
        chapterContents = new BookChapter(this, "contents", 0);
        chapterIntroduction = new BookChapter(this, "introduction", 2);
        chapterArmourer = new BookChapter(this, "armourer", 2);
        chapterEquipmentTemplates = new BookChapter(this, "equipmentTemplates", 1);
        chapterPaintingTools = new BookChapter(this, "paintingTools", 4);
        chapterEquipmentWardrobe = new  BookChapter(this, "equipmentWardrobe", 2);
        chapterRecipes = new  BookChapter(this, "recipes", 0);
        chapterCredits = new BookChapterCredits(this, "credits");
        
        //Add pages
        chapterContents.addPage(new BookPageContents(this));
        
        //Blocks
        chapterRecipes.addPage(new BookPageRecipe(this, ModBlocks.armourerBrain));
        chapterRecipes.addPage(new BookPageRecipe(this, ModBlocks.armourLibrary));
        chapterRecipes.addPage(new BookPageRecipe(this, ModBlocks.colourable));
        chapterRecipes.addPage(new BookPageRecipe(this, ModBlocks.colourableGlass));
        chapterRecipes.addPage(new BookPageRecipe(this, ModBlocks.colourableGlowing));
        chapterRecipes.addPage(new BookPageRecipe(this, ModBlocks.colourableGlassGlowing));
        chapterRecipes.addPage(new BookPageRecipe(this, ModBlocks.colourMixer));
        chapterRecipes.addPage(new BookPageRecipe(this, ModBlocks.mannequin));
        chapterRecipes.addPage(new BookPageRecipe(this, ModBlocks.skinningTable));
        chapterRecipes.addPage(new BookPageRecipe(this, ModBlocks.dyeTable));
        
        //Items
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.paintbrush));
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.paintRoller));
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.burnTool));
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.dodgeTool));
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.shadeNoiseTool));
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.colourNoiseTool));
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.colourPicker));
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.soap));
        //chapterRecipes.addPage(new BookPageRecipe(this, ModRegistry.getMinecraftItem(ModItems.wandOfStyle)));
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.hueTool));
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.guideBook));
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.mannequinTool));
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.dyeBottle));
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.armourersHammer));
        
        addChapter(chapterContents);
        addChapter(chapterIntroduction);
        addChapter(chapterArmourer);
        addChapter(chapterEquipmentTemplates);
        addChapter(chapterPaintingTools);
        addChapter(chapterEquipmentWardrobe);
        addChapter(chapterRecipes);
        addChapter(chapterCredits);
    }
    
    private void createPages() {
        for (int i = 0; i < getNumberOfChapters(); i++) {
            getChapterNumber(i).createPages();
        }
    }
}
