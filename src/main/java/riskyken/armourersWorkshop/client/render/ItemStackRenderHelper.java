
package riskyken.armourersWorkshop.client.render;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import riskyken.armourersWorkshop.api.common.IPoint3D;
import riskyken.armourersWorkshop.api.common.IRectangle3D;
import riskyken.armourersWorkshop.api.common.skin.Rectangle3D;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.model.skin.IEquipmentModel;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

/**
 * Helps render item stacks.
 * 
 * @author RiskyKen
 *
 */

@SideOnly(Side.CLIENT)
public final class ItemStackRenderHelper {

    @Deprecated
    public static void renderItemAsArmourModel(ItemStack stack, boolean showSkinPaint) {
        if (SkinNBTHelper.stackHasSkinData(stack)) {
            SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
            renderItemModelFromSkinPointer(skinPointer, showSkinPaint, false);
        }
    }
    
    @Deprecated
    public static void renderItemModelFromSkinPointer(ISkinPointer skinPointer, boolean showSkinPaint, boolean doLodLoading) {
        renderItemModelFromSkin(ClientSkinCache.INSTANCE.getSkin(skinPointer), skinPointer, showSkinPaint, doLodLoading);
    }
    
    @Deprecated
    public static void renderItemModelFromSkin(Skin skin, ISkinPointer skinPointer, boolean showSkinPaint, boolean doLodLoading) {
        if (skin == null) {
            return;
        }
        //ModLogger.log("render skin");
        
        float blockScale = 16F;
        
        float mcScale = 1F / blockScale;
        float scale =  1;
        
        float offsetX = 0;
        float offsetY = 0;
        float offsetZ = 0;
        
        float scaleX = 1;
        float scaleY = 1;
        float scaleZ = 1;
        
        int width = 1;
        int height = 1;
        int depth = 1;
        
        Rectangle3D sb = skin.getSkinBounds();
        
        width = Math.max(width, sb.getWidth());
        height = Math.max(height, sb.getHeight());
        depth = Math.max(depth, sb.getDepth());
        
        scaleX = Math.min(scaleX, 1F / (float)width);
        scaleY = Math.min(scaleY, 1F / (float)height);
        scaleZ = Math.min(scaleZ, 1F / (float)depth);
        
        scale = Math.min(scale, scaleX);
        scale = Math.min(scale, scaleY);
        scale = Math.min(scale, scaleZ);
        
        offsetX = -sb.getX() - width / 2F;
        offsetY = -sb.getY() - height / 2F;
        offsetZ = -sb.getZ() - depth / 2F;
        
        GL11.glPushMatrix();
        
        GL11.glScalef(scale * blockScale, scale * blockScale, scale * blockScale);
        GL11.glTranslatef(offsetX * mcScale, 0, 0);
        GL11.glTranslatef(0, offsetY * mcScale, 0);
        GL11.glTranslatef(0, 0, offsetZ * mcScale);
        
        if (skin.getSkinType() == SkinTypeRegistry.skinWings) {
            GL11.glTranslated(-offsetX * mcScale, 0, 0);
        }
        
        renderSkinWithHelper(skin, skinPointer, showSkinPaint, doLodLoading);

        GL11.glPopMatrix();
    }
    
    public static void renderSkinWithHelper(Skin skin, ISkinPointer skinPointer, boolean showSkinPaint, boolean doLodLoading) {
        ISkinType skinType = skinPointer.getIdentifier().getSkinType();
        if (skinType == null) {
            skinType = skin.getSkinType();
        }
        
        IEquipmentModel targetModel = SkinModelRenderer.INSTANCE.getModelForEquipmentType(skinType);
        
        
        
        if (targetModel == null) {
            renderSkinWithoutHelper(skinPointer, doLodLoading);
            return;
        }
        
        targetModel.render(null, null, skin, showSkinPaint, skinPointer.getSkinDye(), null, true, 0, doLodLoading);
    }
    
    public static void renderSkinWithoutHelper(ISkinPointer skinPointer, boolean doLodLoading) {
        Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer);
        if (skin == null) {
            return;
        }
        float scale = 1F / 16F;
        for (int i = 0; i < skin.getParts().size(); i++) {
            GL11.glPushMatrix();
            SkinPart skinPart = skin.getParts().get(i);
            IPoint3D offset = skinPart.getPartType().getOffset();
            GL11.glTranslated(offset.getX() * scale, (offset.getY() + 1) * scale, offset.getZ() * scale);
            SkinPartRenderData renderData = new SkinPartRenderData(skinPart, 0.0625F, skinPointer.getSkinDye(), null, 0, true, true, true, null);
            SkinPartRenderer.INSTANCE.renderPart(renderData);
            GL11.glPopMatrix();
        }
        
    }
    
    public static void drawBounds(IRectangle3D rec, int r, int g, int b) {
        float scale = 1F / 16F;
        AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
                rec.getX() * scale, rec.getY() * scale, rec.getZ() * scale,
                (rec.getX() + rec.getWidth()) * scale, (rec.getY() + rec.getHeight()) * scale, (rec.getZ() + rec.getDepth()) * scale);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LIGHTING);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        //GL11.glColor4f((float)r / 255F, (float)g / 255F, (float)b / 255F, 1F);
        GL11.glLineWidth(1.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        //GL11.glDepthMask(false);
        GL11.glColor4f((float)r / 255F, (float)g / 255F, (float)b / 255F, 1);
        RenderGlobal.drawOutlinedBoundingBox(aabb, 0);
        GL11.glColor4f(1, 1, 1, 1);
        //GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1, 1, 1, 1);
    }
}
