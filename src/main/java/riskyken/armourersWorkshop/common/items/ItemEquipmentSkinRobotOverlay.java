package riskyken.armourersWorkshop.common.items;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.client.handler.EquipmentRenderHandler;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;
import buildcraft.api.robots.IRobotOverlayItem;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Optional.Interface(iface = "buildcraft.api.robots.IRobotOverlayItem", modid = "BuildCraft|Core")
public class ItemEquipmentSkinRobotOverlay extends ItemEquipmentSkin implements IRobotOverlayItem {
    
    @Optional.Method(modid = "BuildCraft|Core")
    @Override
    public boolean isValidRobotOverlay(ItemStack stack) {
        if (!EquipmentNBTHelper.stackHasSkinData(stack)) {
            return false;
        }
        SkinPointer skinPointer = EquipmentNBTHelper.getSkinPointerFromStack(stack);
        if (skinPointer.getSkinType() != SkinTypeRegistry.skinHead) {
            return false;
        }
        return true;
    }
    
    @Optional.Method(modid = "BuildCraft|Core")
    @SideOnly(Side.CLIENT)
    @Override
    public void renderRobotOverlay(ItemStack stack, TextureManager textureManager) {
        GL11.glPushMatrix();
        GL11.glScalef(1.0125F, 1.0125F, 1.0125F);
        GL11.glTranslatef(0.0F, -0.25F, 0.0F);
        GL11.glRotatef(180F, 0F, 0F, 1F);
        GL11.glRotatef(-90F, 0F, 1F, 0F);
        EquipmentRenderHandler.INSTANCE.renderSkinWithHelper(stack);
        GL11.glPopMatrix();
    }
}
