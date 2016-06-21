package riskyken.armourersWorkshop.client.guidebook;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.items.ModItems;
import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.plushieWrapper.common.registry.ModRegistry;

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
        chapterContents = new BookChapter(this, "contents", 0);
        chapterIntroduction = new BookChapter(this, "introduction", 2);
        chapterArmourer = new BookChapter(this, "armourer", 2);
        chapterEquipmentTemplates = new BookChapter(this, "equipmentTemplates", 1);
        chapterPaintingTools = new BookChapter(this, "paintingTools", 4);
        chapterEquipmentWardrobe = new  BookChapter(this, "equipmentWardrobe", 2);
        chapterRecipes = new  BookChapter(this, "recipes", 0);
        
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
        chapterRecipes.addPage(new BookPageRecipe(this, ModRegistry.getMinecraftItem(ModItems.soap)));
        //chapterRecipes.addPage(new BookPageRecipe(this, ModRegistry.getMinecraftItem(ModItems.wandOfStyle)));
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.hueTool));
        chapterRecipes.addPage(new BookPageRecipe(this, ModRegistry.getMinecraftItem(ModItems.guideBook)));
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
    }
    
    private void createPages() {
        for (int i = 0; i < getNumberOfChapters(); i++) {
            getChapterNumber(i).createPages();
        }
    }
}
