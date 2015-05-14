package riskyken.armourersWorkshop.client.guidebook;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.MathHelper;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BookPageRecipe implements IBookPage {
    
    private static final int TEXT_COLOUR = 0xFF000000;
    private static RenderItem itemRender = new RenderItem();
    
    private Item item;
    private List<IRecipe> validRecipes;
    
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
            renderRecipe(mc ,fontRenderer, validRecipes.get(0), x, y);
        }
    }
    
    private void renderRecipe(Minecraft mc, FontRenderer fontRenderer, IRecipe recipe, int x, int y) {
        RenderHelper.enableGUIStandardItemLighting();
        //ModRenderHelper.disableLighting();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        
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
                        
                        if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
                            ArrayList<ItemStack> subItems = new ArrayList<ItemStack>();
                            stack.getItem().getSubItems(stack.getItem(), null, subItems);
                            
                            int item = MathHelper.floor_double((double)(System.currentTimeMillis() + (ix + iy) * 1000) / 1000 % subItems.size());
                            stack = subItems.get(item);
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
    
    public void drawTexturedModalRect(int p_73729_1_, int p_73729_2_, int p_73729_3_, int p_73729_4_, int p_73729_5_, int p_73729_6_) {
        float zLevel = 1.0F;
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(p_73729_1_ + 0), (double)(p_73729_2_ + p_73729_6_), (double)zLevel, (double)((float)(p_73729_3_ + 0) * f), (double)((float)(p_73729_4_ + p_73729_6_) * f1));
        tessellator.addVertexWithUV((double)(p_73729_1_ + p_73729_5_), (double)(p_73729_2_ + p_73729_6_), (double)zLevel, (double)((float)(p_73729_3_ + p_73729_5_) * f), (double)((float)(p_73729_4_ + p_73729_6_) * f1));
        tessellator.addVertexWithUV((double)(p_73729_1_ + p_73729_5_), (double)(p_73729_2_ + 0), (double)zLevel, (double)((float)(p_73729_3_ + p_73729_5_) * f), (double)((float)(p_73729_4_ + 0) * f1));
        tessellator.addVertexWithUV((double)(p_73729_1_ + 0), (double)(p_73729_2_ + 0), (double)zLevel, (double)((float)(p_73729_3_ + 0) * f), (double)((float)(p_73729_4_ + 0) * f1));
        tessellator.draw();
    }
}
