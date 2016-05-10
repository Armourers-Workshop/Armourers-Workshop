
package riskyken.armourersWorkshop.client.render;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.IPoint3D;
import riskyken.armourersWorkshop.api.common.skin.Rectangle3D;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.model.skin.IEquipmentModel;
import riskyken.armourersWorkshop.client.skin.ClientSkinCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;

/**
 * Helps render item stacks.
 * 
 * @author RiskyKen
 *
 */

@SideOnly(Side.CLIENT)
public final class ItemStackRenderHelper {

    public static void renderItemAsArmourModel(ItemStack stack, boolean showSkinPaint) {
        if (SkinNBTHelper.stackHasSkinData(stack)) {
            SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
            renderItemModelFromSkinPointer(skinPointer, showSkinPaint);
        }
    }
    
    
    public static void renderItemModelFromSkinPointer(ISkinPointer skinPointer, boolean showSkinPaint) {
        Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer);
        if (skin == null) {
            return;
        }
        
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
        
        scaleX = Math.min(scaleX, 1F / width);
        scaleY = Math.min(scaleY, 1F / height);
        scaleZ = Math.min(scaleZ, 1F / depth);
        
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
        
        renderSkinWithHelper(skin, skinPointer, showSkinPaint);

        GL11.glPopMatrix();
    }
    
    public static void renderSkinWithHelper(Skin skin, ISkinPointer skinPointer, boolean showSkinPaint) {
        ISkinType skinType = skinPointer.getSkinType();
        
        IEquipmentModel targetModel = SkinModelRenderer.INSTANCE.getModelForEquipmentType(skinType);
        
        
        
        if (targetModel == null) {
            renderSkinWithoutHelper(skinPointer);
            return;
        }
        
        targetModel.render(null, null, skin, showSkinPaint, skinPointer.getSkinDye(), null, true);
    }
    
    public static void renderSkinWithoutHelper(ISkinPointer skinPointer) {
        Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer);
        if (skin == null) {
            return;
        }
        skin.onUsed();
        float scale = 1F / 16F;
        for (int i = 0; i < skin.getParts().size(); i++) {
            GL11.glPushMatrix();
            SkinPart skinPart = skin.getParts().get(i);
            IPoint3D offset = skinPart.getPartType().getOffset();
            GL11.glTranslated(offset.getX() * scale, (offset.getY() + 1) * scale, offset.getZ() * scale);
            SkinPartRenderer.INSTANCE.renderPart(skinPart, 0.0625F, skinPointer.getSkinDye(), null);
            GL11.glPopMatrix();
        }
        
    }
}
