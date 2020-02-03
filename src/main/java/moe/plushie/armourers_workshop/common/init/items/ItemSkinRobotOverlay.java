package moe.plushie.armourers_workshop.common.init.items;

/*@Optional.Interface(iface = "buildcraft.api.robots.IRobotOverlayItem", modid = "BuildCraft|Core")*/
public class ItemSkinRobotOverlay extends ItemSkin /*implements IRobotOverlayItem*/ {
    /*
    @Optional.Method(modid = "BuildCraft|Core")
    @Override
    public boolean isValidRobotOverlay(ItemStack stack) {
        if (!SkinNBTHelper.stackHasSkinData(stack)) {
            return false;
        }
        SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
        if (skinPointer.getIdentifier().getSkinType() != SkinTypeRegistry.skinHead) {
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
    */
}
