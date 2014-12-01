package riskyken.armourersWorkshop.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.model.ModelMannequin;
import riskyken.armourersWorkshop.common.SkinHelper;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;

import com.mojang.authlib.GameProfile;

public class RenderItemMannequin implements IItemRenderer {
    
    private static final String TAG_OWNER = "owner";
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
        
        ResourceLocation skin = AbstractClientPlayer.locationStevePng;
        
        if (item.hasTagCompound()) {
            NBTTagCompound compound = item.getTagCompound();
            GameProfile gameProfile = null;
            if (compound.hasKey(TAG_OWNER, 10)) {
                gameProfile = NBTUtil.func_152459_a(compound.getCompoundTag(TAG_OWNER));
                skin = SkinHelper.getSkinResourceLocation(gameProfile);
            }
        }
        
        if (item.getItem() == Item.getItemFromBlock(ModBlocks.doll)) {
            float dollScale = 0.5F;
            GL11.glScalef(dollScale, dollScale, dollScale);
        }
        
        float scale = 0.0625F;
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        //OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        Minecraft.getMinecraft().renderEngine.bindTexture(skin);
        modelMannequin.render(null, 0, 0, 0, headPitch, headTilt, scale, true);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

}
