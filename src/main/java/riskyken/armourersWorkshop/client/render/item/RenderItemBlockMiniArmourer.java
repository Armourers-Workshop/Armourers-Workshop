package riskyken.armourersWorkshop.client.render.item;

public class RenderItemBlockMiniArmourer /*implements IItemRenderer*/ {/*

    private static final ModelBlockArmourer modelArmourer = new ModelBlockArmourer();
    
    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        float scale = 0.0625F;
        GL11.glPushMatrix();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        if (type == ItemRenderType.INVENTORY) {
            GL11.glTranslatef(0F, -0.2F, 0F);
        }
        modelArmourer.render(null, 0, scale);
        GL11.glPopMatrix();
    }*/
}
