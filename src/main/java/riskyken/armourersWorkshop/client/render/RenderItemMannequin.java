package riskyken.armourersWorkshop.client.render;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.model.ModelMannequin;

public class RenderItemMannequin implements IItemRenderer {

    private final ModelMannequin modelMannequin;
    
    public RenderItemMannequin(ModelMannequin modelMannequin) {
        this.modelMannequin = modelMannequin;
    }
    
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
        GL11.glPushMatrix();
        GL11.glScalef(-1, -1, 1);
        GL11.glTranslatef(0, -0.5F, 0);
        
        float headPitch = 0F;
        float headTilt = 0F;
        
        switch (type) {
        case EQUIPPED_FIRST_PERSON:
            GL11.glTranslatef(-0.6F, -0.5F, 0.6F);
            GL11.glRotatef(-60, 0, 1, 0);
            headPitch = -40F;
            headTilt = -10F;
            break;
        case ENTITY:
            GL11.glScalef(1.4F, 1.4F, 1.4F);
            GL11.glTranslatef(0, -0.8F, 0);
            break;
        case EQUIPPED:
            GL11.glScalef(1.2F, 1.2F, 1.2F);
            GL11.glTranslatef(-0.6F, -0.5F, 0.6F);
            GL11.glRotatef(-60, 0, 1, 0);
            break;
        case INVENTORY:
            GL11.glTranslatef(0, 0.1F, 0);
            GL11.glScalef(0.9F, 0.9F, 0.9F);
            GL11.glRotatef(180, 0, 1, 0);
            break;
        default:
            break;
        }
        
        float scale = 0.0625F;
        GL11.glColor3f(1F, 1F, 1F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        modelMannequin.render(null, 0, 0, 0, headPitch, headTilt, scale);
        GL11.glPopMatrix();
    }

}
