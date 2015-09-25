
package riskyken.armourersWorkshop.client.render;

import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.model.equipmet.IEquipmentModel;
import riskyken.armourersWorkshop.client.skin.ClientSkinCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Helps render item stacks.
 * 
 * @author RiskyKen
 *
 */

@SideOnly(Side.CLIENT)
public final class ItemStackRenderHelper {

    public static void renderItemAsArmourModel(ItemStack stack, boolean showSkinPaint) {
        if (EquipmentNBTHelper.stackHasSkinData(stack)) {
            SkinPointer skinPointer = EquipmentNBTHelper.getSkinPointerFromStack(stack);
            renderItemModelFromSkinPointer(skinPointer, showSkinPaint);
        }
    }
    
    
    public static void renderItemModelFromSkinPointer(SkinPointer skinPointer, boolean showSkinPaint) {
        int skinId = skinPointer.getSkinId();
        ISkinType skinType = skinPointer.getSkinType();
        
        IEquipmentModel targetModel = EquipmentModelRenderer.INSTANCE.getModelForEquipmentType(skinType);
        if (targetModel == null) {
            renderSkinWithoutHelper(skinPointer);
            return;
        }
        
        Skin data = ClientSkinCache.INSTANCE.getEquipmentItemData(skinId);
        if (data == null) {
            return;
        }
        
        if (skinType == SkinTypeRegistry.skinHead) {
            GL11.glTranslatef(0F, 0.2F, 0F);
            targetModel.render(null, null, data, showSkinPaint);
        } else if (skinType == SkinTypeRegistry.skinChest) {
            GL11.glTranslatef(0F, -0.35F, 0F);
            targetModel.render(null, null, data, showSkinPaint);
        } else if (skinType == SkinTypeRegistry.skinLegs) {
            GL11.glTranslatef(0F, -1.2F, 0F);
            targetModel.render(null, null, data, showSkinPaint);
        } else if (skinType == SkinTypeRegistry.skinSkirt) {
            GL11.glTranslatef(0F, -1.0F, 0F);
            targetModel.render(null, null, data, showSkinPaint);
        } else if (skinType == SkinTypeRegistry.skinFeet) {
            GL11.glTranslatef(0F, -1.2F, 0F);
            targetModel.render(null, null, data, showSkinPaint);
        } else if (skinType == SkinTypeRegistry.skinSword) {
            targetModel.render(null, null, data, showSkinPaint);
        } else if (skinType == SkinTypeRegistry.skinBow) {
            targetModel.render(null, null, data, showSkinPaint);
        }
    }
    
    public static void renderSkinWithoutHelper(SkinPointer skinPointer) {
        Skin skin = ClientSkinCache.INSTANCE.getEquipmentItemData(skinPointer.skinId);
        if (skin == null) {
            return;
        }
        skin.onUsed();
        for (int i = 0; i < skin.getParts().size(); i++) {
            SkinPart skinPart = skin.getParts().get(i);
            EquipmentPartRenderer.INSTANCE.renderPart(skinPart, 0.0625F, skinPointer.getSkinDye());
        }
    }
}
