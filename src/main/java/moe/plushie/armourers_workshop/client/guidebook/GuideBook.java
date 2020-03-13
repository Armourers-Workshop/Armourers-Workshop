package moe.plushie.armourers_workshop.client.guidebook;

import moe.plushie.armourers_workshop.common.init.blocks.ModBlocks;
import moe.plushie.armourers_workshop.common.init.items.ModItems;
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
        chapterRecipes.addPage(new BookPageRecipe(this, ModBlocks.ARMOURER));
        chapterRecipes.addPage(new BookPageRecipe(this, ModBlocks.SKIN_LIBRARY));
        chapterRecipes.addPage(new BookPageRecipe(this, ModBlocks.SKIN_CUBE));
        chapterRecipes.addPage(new BookPageRecipe(this, ModBlocks.SKIN_CUBE_GLASS));
        chapterRecipes.addPage(new BookPageRecipe(this, ModBlocks.SKIN_CUBE_GLOWING));
        chapterRecipes.addPage(new BookPageRecipe(this, ModBlocks.SKIN_CUBE_GLASS_GLOWING));
        chapterRecipes.addPage(new BookPageRecipe(this, ModBlocks.COLOUR_MIXER));
        // chapterRecipes.addPage(new BookPageRecipe(this, ModBlocks.MANNEQUIN));
        chapterRecipes.addPage(new BookPageRecipe(this, ModBlocks.SKINNING_TABLE));
        chapterRecipes.addPage(new BookPageRecipe(this, ModBlocks.DYE_TABLE));
        
        //Items
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.PAINT_BRUSH));
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.PAINT_ROLLER));
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.BURN_TOOL));
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.DODGE_TOOL));
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.SHADE_NOISE_TOOL));
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.COLOUR_NOISE_TOOL));
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.COLOUR_PICKER));
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.SOAP));
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.WAND_OF_STYLE));
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.HUE_TOOL));
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.GUIDE_BOOK));
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.MANNEQUIN_TOOL));
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.DYE_BOTTLE));
        chapterRecipes.addPage(new BookPageRecipe(this, ModItems.ARMOURERS_HAMMER));
        
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
