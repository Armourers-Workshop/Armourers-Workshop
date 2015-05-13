package riskyken.armourersWorkshop.client.guidebook;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.ReflectionHelper;

public class BookPageRecipe implements IBookPage {
    
    private Item item;
    private static RenderItem itemRender = new RenderItem();
    private List<IRecipe> validRecipes;
    private static final int TEXT_COLOUR = 0xFF000000;
    
    public BookPageRecipe(Block block) {
        this(Item.getItemFromBlock(block));
    }
    
    public BookPageRecipe(Item item) {
        this.item = item;
        validRecipes = new ArrayList<IRecipe>();
        List<IRecipe> recipeList = CraftingManager.getInstance().getRecipeList();
        for (int i = 0; i < recipeList.size(); i++) {
            IRecipe recipe = recipeList.get(i);
            if (recipe.getRecipeOutput() != null && recipe.getRecipeOutput().getItem() == item) {
                if (recipe instanceof ShapedRecipes | recipe instanceof ShapelessRecipes |
                        recipe instanceof ShapedOreRecipe | recipe instanceof ShapelessOreRecipe) {
                    validRecipes.add(recipe);
                }
            }
        }
    }

    @Override
    public void renderPage(FontRenderer fontRenderer, int x, int y) {
        ItemStack result = new ItemStack(item);
        int nameWidth = fontRenderer.getStringWidth(result.getDisplayName());
        fontRenderer.drawSplitString(result.getDisplayName(), x, y, BookPage.PAGE_WIDTH, TEXT_COLOUR);
        //fontRenderer.drawString(result.getDisplayName(), x + BookPage.PAGE_WIDTH / 2 - nameWidth / 2, y, TEXT_COLOUR);
        Minecraft mc = Minecraft.getMinecraft();
        
        if (validRecipes.size() > 0) {
            RenderHelper.enableGUIStandardItemLighting();
            //ModRenderHelper.disableLighting();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            
            IRecipe recipe = validRecipes.get(0);
            if (recipe instanceof ShapedRecipes) {
                ShapedRecipes shapedRecipe = (ShapedRecipes) recipe;
                for (int ix = 0; ix < shapedRecipe.recipeWidth; ix++) {
                    for (int iy = 0; iy < shapedRecipe.recipeHeight; iy++) {
                        itemRender.renderItemAndEffectIntoGUI(fontRenderer, mc.getTextureManager(),
                                shapedRecipe.recipeItems[0], x, y + 10);
                    }
                }
            }
            
            if (recipe instanceof ShapedOreRecipe) {
                ShapedOreRecipe shapedOreRecipe = (ShapedOreRecipe) recipe;
                Object[] input = shapedOreRecipe.getInput();
                int width = (Integer) ReflectionHelper.getPrivateValue(ShapedOreRecipe.class, shapedOreRecipe, "width");
                int height = (Integer) ReflectionHelper.getPrivateValue(ShapedOreRecipe.class, shapedOreRecipe, "height");
                
                for (int ix = 0; ix < width; ix++) {
                    for (int iy = 0; iy < height; iy++) {
                        Object inputObj = input[ix + iy * width];
                        if (inputObj != null && (inputObj instanceof ItemStack | inputObj instanceof ArrayList<?>)) {
                            ItemStack stack;
                            if (inputObj instanceof ArrayList) {
                                ArrayList<ItemStack> list = (ArrayList) inputObj;
                                stack = (ItemStack) list.get(0);
                            } else {
                                stack = (ItemStack) inputObj;
                            }
                            
                            itemRender.renderItemAndEffectIntoGUI(fontRenderer, mc.getTextureManager(),
                                    stack, x + 28 + ix * 16, y + 20 + iy * 16 + 10);
                            itemRender.renderItemOverlayIntoGUI(fontRenderer, mc.getTextureManager(),
                                    stack, x + 28 + ix * 16, y + 20 + iy * 16 + 10);
                        }
                    }
                }
            }
            
            if (recipe instanceof ShapelessRecipes) {
                ShapelessRecipes shapelessRecipes = (ShapelessRecipes) recipe;
            }
            if (recipe instanceof ShapelessOreRecipe) {
                ShapelessOreRecipe shapelessOreRecipe = (ShapelessOreRecipe) recipe;
                int ix = 0;
                int iy = 0;
                for (int i = 0; i < shapelessOreRecipe.getRecipeSize(); i++) {
                    Object inputObj = shapelessOreRecipe.getInput().get(i);
                    if (inputObj != null && (inputObj instanceof ItemStack | inputObj instanceof ArrayList<?>)) {
                        ItemStack stack;
                        if (inputObj instanceof ArrayList) {
                            ArrayList<ItemStack> list = (ArrayList) inputObj;
                            stack = (ItemStack) list.get(0);
                        } else {
                            stack = (ItemStack) inputObj;
                        }
                        itemRender.renderItemAndEffectIntoGUI(fontRenderer, mc.getTextureManager(),
                                stack, x + 28 + ix * 16, y + 20 + iy * 16 + 10);
                        itemRender.renderItemOverlayIntoGUI(fontRenderer, mc.getTextureManager(),
                                stack, x + 28 + ix * 16, y + 20 + iy * 16 + 10);
                    }
                    ix++;
                    if (ix > 2) {
                        ix = 0;
                        iy++;
                    }
                }
            }
            
            itemRender.renderItemAndEffectIntoGUI(fontRenderer, mc.getTextureManager(),
                    recipe.getRecipeOutput(), x + 44, y + 100);
        }
    }
}
