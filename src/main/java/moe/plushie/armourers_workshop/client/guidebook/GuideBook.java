package moe.plushie.armourers_workshop.client.guidebook;

import moe.plushie.armourers_workshop.common.blocks.ModBlocks;
import moe.plushie.armourers_workshop.common.items.ModItems;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
        chapterEquipmentWardrobe = new  BookChapter(this, "equipment-wardrobe", 2);
        chapterRecipes = new  BookChapter(this, "recipes", 0);
        chapterCredits = new BookChapterCredits(this, "credits");
        
        //Add pages
        chapterContents.addPage(new BookPageContents(this));
        
        //Blocks
        chapterRecipes.addPage(new BookPageRecipe(this, ModBlocks.armourer));
        chapterRecipes.addPage(new BookPageRecipe(this, ModBlocks.skinLibrary));
        chapterRecipes.addPage(new BookPageRecipe(this, ModBlocks.skinCube));
        chapterRecipes.addPage(new BookPageRecipe(this, ModBlocks.skinCubeGlass));
        chapterRecipes.addPage(new BookPageRecipe(this, ModBlocks.skinCubeGlowing));
        chapterRecipes.addPage(new BookPageRecipe(this, ModBlocks.skinCubeGlassGlowing));
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
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.wandOfStyle));
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
