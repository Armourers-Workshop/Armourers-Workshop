package moe.plushie.armourers_workshop.client.guidebook;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BookPageRecipe extends BookPageBase {
    
    private static final int TEXT_COLOUR = 0xFF000000;
    private static RenderItem renderItem;
    
    private Item item;
    private List<IRecipe> validRecipes;
    
    public BookPageRecipe(IBook parentBook, Block block) {
        this(parentBook, Item.getItemFromBlock(block));
        renderItem = Minecraft.getMinecraft().getRenderItem();
    }
    
    public BookPageRecipe(IBook parentBook, Item item) {
        super(parentBook);
        this.item = item;
        validRecipes = new ArrayList<IRecipe>();
        /*
        IRecipe recipe = CraftingManager.getRecipe(item.getRegistryName());
        if (recipe != null) {
            validRecipes.add(recipe);
        }
        */
        /*
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
        */
    }
    
    @Override
    public void renderPage(FontRenderer fontRenderer, int mouseX, int mouseY, boolean turning, int pageNumber) {
        //GL11.glEnable(GL11.GL_BLEND);
        drawPageTitleAndNumber(fontRenderer, pageNumber);
        ItemStack result = new ItemStack(item);
        
        List<String> lines = fontRenderer.listFormattedStringToWidth(result.getDisplayName(), PAGE_TEXTURE_WIDTH);
        for (int i = 0; i < lines.size(); i++) {
            renderStringCenter(fontRenderer, lines.get(i), PAGE_MARGIN_TOP + PAGE_PADDING_TOP + fontRenderer.FONT_HEIGHT * 2 + i * fontRenderer.FONT_HEIGHT);
        }
        
        
        
        Minecraft mc = Minecraft.getMinecraft();
        
        //mc.renderEngine.bindTexture(bookPageTexture);
        GlStateManager.color(1F, 1F, 1F, 1F);
        //drawTexturedModalRect(0, 0, 0, 0, PAGE_TEXTURE_WIDTH, PAGE_TEXTURE_HEIGHT);
        //drawPageTitleAndNumber(fontRenderer, pageNumber);
        if (validRecipes.size() > 0) {
            renderRecipe(mc ,fontRenderer, validRecipes.get(0), 0, lines.size() * fontRenderer.FONT_HEIGHT);
        }
        
    }
    
    private void renderRecipe(Minecraft mc, FontRenderer fontRenderer, IRecipe recipe, int x, int y) {
        GlStateManager.pushAttrib();
        RenderHelper.enableGUIStandardItemLighting();
        //ModRenderHelper.disableLighting();
        
        //GL11.glEnable(GL11.GL_BLEND);
        //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        //GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        //GL11.glEnable(GL11.GL_DEPTH_TEST);
        
        if (recipe instanceof ShapedRecipes) {
            ShapedRecipes shapedRecipe = (ShapedRecipes) recipe;
            for (int ix = 0; ix < shapedRecipe.recipeWidth; ix++) {
                for (int iy = 0; iy < shapedRecipe.recipeHeight; iy++) {
                    //renderItem.renderItemAndEffectIntoGUI(shapedRecipe.getIngredients().get(0), x, y);
                    /*
                    itemRender.renderItemAndEffectIntoGUI(fontRenderer, mc.getTextureManager(),
                            shapedRecipe.recipeItems[0], x, y + 10);
                    */
                }
            }
        }
        /*
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
                                stack, x + 32 + ix * 18, y + 30 + iy * 18 + 10);
                        itemRender.renderItemOverlayIntoGUI(fontRenderer, mc.getTextureManager(),
                                stack, x + 32 + ix * 18, y + 30 + iy * 18 + 10);
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
                            stack, x + 32 + ix * 18, y + 30 + iy * 18 + 10);
                    itemRender.renderItemOverlayIntoGUI(fontRenderer, mc.getTextureManager(),
                            stack, x + 32 + ix * 18, y + 30 + iy * 18 + 10);
                }
                ix++;
                if (ix > 2) {
                    ix = 0;
                    iy++;
                }
            }
        }
        
        itemRender.renderItemAndEffectIntoGUI(fontRenderer, mc.getTextureManager(),
                recipe.getRecipeOutput(), x + 50, y + 100);
        GL11.glDisable(GL11.GL_LIGHTING);*/
        GlStateManager.popAttrib();
    }
    
    public void drawTexturedModalRect(int p_73729_1_, int p_73729_2_, int p_73729_3_, int p_73729_4_, int p_73729_5_, int p_73729_6_) {
        /*
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
        */
    }

    @Override
    public void renderRollover(FontRenderer fontRenderer, int mouseX, int mouseY) {
        // TODO Auto-generated method stub
        
    }
}
