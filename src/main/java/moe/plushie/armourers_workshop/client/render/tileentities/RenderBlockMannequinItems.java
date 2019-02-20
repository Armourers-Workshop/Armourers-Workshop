package moe.plushie.armourers_workshop.client.render.tileentities;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockMannequinItems {
    /*
    private RenderPlayer renderPlayer;
    private float scale = 0.0625F;
    
    public RenderBlockMannequinItems() {
        renderPlayer = (RenderPlayer) RenderManager.instance.entityRenderMap.get(EntityPlayer.class);
    }
    
    public void renderHeadStack(MannequinFakePlayer fakePlayer, ItemStack stack, ModelBiped targetBiped, RenderManager rm, byte[] extraColours, double distance) {
        Item targetItem = stack.getItem();
        
        if (SkinNBTHelper.stackHasSkinData(stack)) {
            SkinModelRenderer.INSTANCE.renderEquipmentPartFromStack(stack, targetBiped, extraColours, distance, true);
            return;
        }
        
        if (targetItem instanceof ItemBlock) {
            float blockScale = 0.5F;
            GL11.glRotated(Math.toDegrees(targetBiped.bipedHead.rotateAngleZ), 0, 0, 1);
            GL11.glRotated(Math.toDegrees(targetBiped.bipedHead.rotateAngleY), 0, 1, 0);
            GL11.glRotated(Math.toDegrees(targetBiped.bipedHead.rotateAngleX), 1, 0, 0);
            GL11.glTranslatef(0, -4 * scale, 0);
            
            GL11.glScalef(-blockScale, -blockScale, blockScale);
            GL11.glRotatef(90F, 0F, 1F, 0F);
            
            rm.itemRenderer.renderItem(fakePlayer, stack, stack.getItemDamage());
        } else {
            if (targetItem instanceof ItemArmor) {
                int passes = targetItem.getRenderPasses(stack.getItemDamage());
                for (int i = 0; i < passes; i++) {
                    ModelBiped armourBiped = ForgeHooksClient.getArmorModel(fakePlayer, stack, 0, renderPlayer.modelArmorChestplate);
                    if (i == 0) {
                        bindTexture(RenderBiped.getArmorResource(fakePlayer, stack, 0, null));
                    } else {
                        bindTexture(RenderBiped.getArmorResource(fakePlayer, stack, 0, "overlay"));
                    }
                    
                    Color c = new Color(targetItem.getColorFromItemStack(stack, i));
                    GL11.glColor3f((float)c.getRed() / 255, (float)c.getGreen() / 255, (float)c.getBlue() / 255);
                    armourBiped.isChild = false;
                    
                    if (armourBiped == renderPlayer.modelArmorChestplate) {
                        setRotations(targetBiped.bipedHead, armourBiped.bipedHead);
                        armourBiped.bipedHead.showModel = true;
                        armourBiped.bipedHead.render(scale);
                        resetRotations(targetBiped.bipedHead);
                    } else {
                        try {
                            GL11.glRotated(Math.toDegrees(targetBiped.bipedHead.rotateAngleZ), 0, 0, 1);
                            GL11.glRotated(Math.toDegrees(targetBiped.bipedHead.rotateAngleY), 0, 1, 0);
                            GL11.glRotated(Math.toDegrees(targetBiped.bipedHead.rotateAngleX), 1, 0, 0);
                            armourBiped.render(null, 0, 0, 0, 0, 0, scale);
                        } catch (Exception e) {
                            //ModLogger.log(e);
                        }
                    }
                }
            }
        }
    }

    public void renderChestStack(MannequinFakePlayer fakePlayer, ItemStack stack, ModelMannequin targetBiped, RenderManager rm, byte[] extraColours, double distance) {
        Item targetItem = stack.getItem();
        if (SkinNBTHelper.stackHasSkinData(stack)) {
            SkinModelRenderer.INSTANCE.renderEquipmentPartFromStack(stack, targetBiped, extraColours, distance, true);
            return;
        }
        if (targetItem instanceof ItemArmor) {
            int passes = targetItem.getRenderPasses(stack.getItemDamage());
            for (int i = 0; i < passes; i++) {
                ModelBiped armourBiped = ForgeHooksClient.getArmorModel(fakePlayer, stack, 1, renderPlayer.modelArmorChestplate);
                if (i == 0) {
                    bindTexture(RenderBiped.getArmorResource(fakePlayer, stack, 1, null));
                } else {
                    bindTexture(RenderBiped.getArmorResource(fakePlayer, stack, 1, "overlay"));
                }
                
                Color c = new Color(targetItem.getColorFromItemStack(stack, i));
                GL11.glColor3f((float)c.getRed() / 255, (float)c.getGreen() / 255, (float)c.getBlue() / 255);
                armourBiped.isChild = false;
                if (armourBiped == renderPlayer.modelArmorChestplate) {
                    setRotations(targetBiped.bipedBody, armourBiped.bipedBody);
                    setRotations(targetBiped.bipedLeftArm, armourBiped.bipedLeftArm);
                    setRotations(targetBiped.bipedRightArm, armourBiped.bipedRightArm);
                    
                    armourBiped.bipedBody.showModel = true;
                    armourBiped.bipedLeftArm.showModel = true;
                    armourBiped.bipedRightArm.showModel = true;
                    
                    armourBiped.bipedBody.render(scale);
                    armourBiped.bipedLeftArm.render(scale);
                    armourBiped.bipedRightArm.render(scale);
                    
                    resetRotations(targetBiped.bipedBody);
                    resetRotations(targetBiped.bipedLeftArm);
                    resetRotations(targetBiped.bipedRightArm);
                    
                    
                    armourBiped = ForgeHooksClient.getArmorModel(fakePlayer, stack, 1, renderPlayer.modelArmor);
                    setRotations(targetBiped.bipedBody, armourBiped.bipedBody);
                    armourBiped.bipedBody.showModel = true;
                    armourBiped.bipedBody.render(scale);
                    resetRotations(targetBiped.bipedBody);
                } else {
                    try {
                        armourBiped.render(null, 0, 0, 0, 0, 0, scale);
                    } catch (Exception e) {
                        //ModLogger.log(e);
                    }
                }
            }
        }
    }
    public void renderLegsStack(MannequinFakePlayer fakePlayer, ItemStack stack, ModelBiped targetBiped, RenderManager rm, byte[] extraColours, double distance) {
        Item targetItem = stack.getItem();
        if (SkinNBTHelper.stackHasSkinData(stack)) {
            SkinModelRenderer.INSTANCE.renderEquipmentPartFromStack(stack, targetBiped, extraColours, distance, true);
            return;
        }
        if (targetItem instanceof ItemArmor) {
            int passes = targetItem.getRenderPasses(stack.getItemDamage());
            for (int i = 0; i < passes; i++) {
                ModelBiped armourBiped = ForgeHooksClient.getArmorModel(fakePlayer, stack, 2, renderPlayer.modelArmor);
                if (i == 0) {
                    bindTexture(RenderBiped.getArmorResource(fakePlayer, stack, 2, null));
                } else {
                    bindTexture(RenderBiped.getArmorResource(fakePlayer, stack, 2, "overlay"));
                }
                
                Color c = new Color(targetItem.getColorFromItemStack(stack, i));
                GL11.glColor3f((float)c.getRed() / 255, (float)c.getGreen() / 255, (float)c.getBlue() / 255);
                armourBiped.isChild = false;
                if (armourBiped == renderPlayer.modelArmor) {
                    setRotations(targetBiped.bipedLeftLeg, armourBiped.bipedLeftLeg);
                    setRotations(targetBiped.bipedRightLeg, armourBiped.bipedRightLeg);
                    armourBiped.bipedBody.showModel = true;
                    armourBiped.bipedLeftLeg.showModel = true;
                    armourBiped.bipedRightLeg.showModel = true;
                    armourBiped.bipedBody.render(scale);
                    armourBiped.bipedLeftLeg.render(scale);
                    armourBiped.bipedRightLeg.render(scale);
                    resetRotations(armourBiped.bipedLeftLeg);
                    resetRotations(armourBiped.bipedRightLeg);
                } else {
                    try {
                        armourBiped.render(fakePlayer, 0, 0, 0, 0, 0, scale);
                    } catch (Exception e) {
                        //ModLogger.log(e);
                    }
                }
            }
        }
    }
    
    public void renderFeetStack(MannequinFakePlayer fakePlayer, ItemStack stack, ModelBiped targetBiped, RenderManager rm, byte[] extraColours, double distance) {
        Item targetItem = stack.getItem();
        if (SkinNBTHelper.stackHasSkinData(stack)) {
            SkinModelRenderer.INSTANCE.renderEquipmentPartFromStack(stack, targetBiped, extraColours, distance, true);
            return;
        }
        if (targetItem instanceof ItemArmor) {
            int passes = targetItem.getRenderPasses(stack.getItemDamage());
            for (int i = 0; i < passes; i++) {
                ModelBiped armourBiped = ForgeHooksClient.getArmorModel(fakePlayer, stack, 3, renderPlayer.modelArmorChestplate);
                if (i == 0) {
                    bindTexture(RenderBiped.getArmorResource(fakePlayer, stack, 3, null));
                } else {
                    bindTexture(RenderBiped.getArmorResource(fakePlayer, stack, 3, "overlay"));
                }
                
                Color c = new Color(targetItem.getColorFromItemStack(stack, i));
                GL11.glColor3f((float)c.getRed() / 255, (float)c.getGreen() / 255, (float)c.getBlue() / 255);
                armourBiped.isChild = false;
                if (armourBiped == renderPlayer.modelArmorChestplate) {
                    setRotations(targetBiped.bipedLeftLeg, armourBiped.bipedLeftLeg);
                    setRotations(targetBiped.bipedRightLeg, armourBiped.bipedRightLeg);
                    armourBiped.bipedLeftLeg.showModel = true;
                    armourBiped.bipedRightLeg.showModel = true;
                    armourBiped.bipedLeftLeg.render(scale);
                    armourBiped.bipedRightLeg.render(scale);
                    resetRotations(armourBiped.bipedLeftLeg);
                    resetRotations(armourBiped.bipedRightLeg);
                } else {
                    try {
                        armourBiped.render(fakePlayer, 0, 0, 0, 0, 0, scale);
                    } catch (Exception e) {
                        //ModLogger.log(e);
                    }
                }
            }
        }
    }
    
    public void renderRightArmStack(MannequinFakePlayer fakePlayer, ItemStack stack, ModelBiped targetBiped, RenderManager rm, byte[] extraColours, double distance) {
        Item targetItem = stack.getItem();
        float blockScale = 0.5F;
        float itemScale = 1 - (float)1 / 3;
        Tessellator tessellator = Tessellator.instance;
        
        //Movement
        GL11.glTranslatef(-5F * scale, 0F, 0F);
        GL11.glTranslatef(0F, 2F * scale, 0F);
        
        GL11.glRotated(Math.toDegrees(targetBiped.bipedRightArm.rotateAngleZ), 0F, 0F, 1F);
        GL11.glRotated(Math.toDegrees(targetBiped.bipedRightArm.rotateAngleY), 0F, 1F, 0F);
        GL11.glRotated(Math.toDegrees(targetBiped.bipedRightArm.rotateAngleX), 1F, 0F, 0F);
        
        GL11.glTranslatef(-2F * scale, 0F, 0F);
        GL11.glTranslatef(0F, 10F * scale, 0F);
        
        if (SkinNBTHelper.stackHasSkinData(stack)) {
            SkinPointer sp = SkinNBTHelper.getSkinPointerFromStack(stack);
            if (sp.getIdentifier().getSkinType() == SkinTypeRegistry.skinSword | sp.getIdentifier().getSkinType() == SkinTypeRegistry.skinBow) {
                GL11.glRotatef(90, 1, 0, 0);
                GL11.glTranslated(1 * scale, 0 * scale, 2 * scale);
                SkinModelRenderer.INSTANCE.renderEquipmentPartFromStack(stack, null, extraColours, distance, true);
                return;
            }
        }
        
        GL11.glRotatef(-90, 0, 1, 0);
        GL11.glRotatef(45, 0, 0, 1);
        
        GL11.glScalef(itemScale, itemScale, itemScale);
        GL11.glRotatef(-335.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
        
        if (targetItem instanceof ItemBlock) {
            GL11.glTranslatef(-3 * scale, 4 * scale, 2 * scale);
            GL11.glScalef(-blockScale, -blockScale, blockScale);
            GL11.glRotatef(30F, 0F, 1F, 0F);
            GL11.glRotatef(130F, 1F, 0F, 0F);
        } else {
            if (!(targetItem instanceof ItemSword)) {
                //GL11.glRotatef(90F, 0F, 1F, 0F);
                GL11.glScalef(0.75F, 0.75F, 0.75F);
            }
        }
        
        if (targetItem.requiresMultipleRenderPasses()) {
            int passes = targetItem.getRenderPasses(stack.getItemDamage());
            for (int i = 0 ; i < passes; i++) {
                int c = targetItem.getColorFromItemStack(stack, i);
                float r = (float)(c >> 16 & 255) / 255.0F;
                float g = (float)(c >> 8 & 255) / 255.0F;
                float b = (float)(c & 255) / 255.0F;
                GL11.glColor4f(r, g, b, 1F);
                rm.itemRenderer.renderItem(fakePlayer, stack, i, ItemRenderType.EQUIPPED);
            }
            GL11.glColor4f(1F, 1F, 1F, 1F);
        } else {
            int c = targetItem.getColorFromItemStack(stack, 0);
            float r = (float)(c >> 16 & 255) / 255.0F;
            float g = (float)(c >> 8 & 255) / 255.0F;
            float b = (float)(c & 255) / 255.0F;
            GL11.glColor4f(r, g, b, 1F);
            rm.itemRenderer.renderItem(fakePlayer, stack, 0, ItemRenderType.EQUIPPED);
            GL11.glColor4f(1F, 1F, 1F, 1F);
        }
    }
    
    public void renderLeftArmStack(MannequinFakePlayer fakePlayer, ItemStack stack, ModelBiped targetBiped, RenderManager rm, byte[] extraColours, double distance) {
        Item targetItem = stack.getItem();
        float blockScale = 0.5F;
        float itemScale = 1 - (float)1 / 3;
        

        //Movement
        GL11.glTranslatef(5F * scale, 0F, 0F);
        GL11.glTranslatef(0F, 2F * scale, 0F);
        
        GL11.glRotated(Math.toDegrees(targetBiped.bipedLeftArm.rotateAngleZ), 0F, 0F, 1F);
        GL11.glRotated(Math.toDegrees(targetBiped.bipedLeftArm.rotateAngleY), 0F, 1F, 0F);
        GL11.glRotated(Math.toDegrees(targetBiped.bipedLeftArm.rotateAngleX), 1F, 0F, 0F);
        
        GL11.glTranslatef(1F * scale, 0F, 0F);
        GL11.glTranslatef(0F, 10F * scale, 0F);
        
        if (SkinNBTHelper.stackHasSkinData(stack)) {
            SkinPointer sp = SkinNBTHelper.getSkinPointerFromStack(stack);
            if (sp.getIdentifier().getSkinType() == SkinTypeRegistry.skinSword | sp.getIdentifier().getSkinType() == SkinTypeRegistry.skinBow) {
                GL11.glRotatef(90, 1, 0, 0);
                GL11.glTranslated(0 * scale, 0 * scale, 2 * scale);
                GL11.glScalef(-1, 1, 1);
                GL11.glCullFace(GL11.GL_FRONT);
                SkinModelRenderer.INSTANCE.renderEquipmentPartFromStack(stack, null, extraColours, distance, true);
                GL11.glCullFace(GL11.GL_BACK);
                return;
            }
        }
        
        GL11.glRotatef(-90, 0, 1, 0);
        GL11.glRotatef(45, 0, 0, 1);
        
        GL11.glScalef(itemScale, itemScale, itemScale);
        GL11.glRotatef(-335.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
        
        if (targetItem instanceof ItemBlock) {
            GL11.glTranslatef(-2 * scale, 4 * scale, 2 * scale);
            GL11.glScalef(-blockScale, -blockScale, blockScale);
            GL11.glRotatef(50F, 0F, 1F, 0F);
            GL11.glRotatef(130F, 1F, 0F, 0F);
        } else {
            if (!(targetItem instanceof ItemSword)) {
                //GL11.glRotatef(-45F, 1F, 0F, 1F);
                GL11.glScalef(0.75F, 0.75F, 0.75F);
            }
        }
        
        if (targetItem.requiresMultipleRenderPasses()) {
            int passes = targetItem.getRenderPasses(stack.getItemDamage());
            for (int i = 0 ; i < passes; i++) {
                int c = targetItem.getColorFromItemStack(stack, i);
                float r = (float)(c >> 16 & 255) / 255.0F;
                float g = (float)(c >> 8 & 255) / 255.0F;
                float b = (float)(c & 255) / 255.0F;
                GL11.glColor4f(r, g, b, 1F);
                rm.itemRenderer.renderItem(fakePlayer, stack, i, ItemRenderType.EQUIPPED);
            }
            GL11.glColor4f(1F, 1F, 1F, 1F);
        } else {
            int c = targetItem.getColorFromItemStack(stack, 0);
            float r = (float)(c >> 16 & 255) / 255.0F;
            float g = (float)(c >> 8 & 255) / 255.0F;
            float b = (float)(c & 255) / 255.0F;
            GL11.glColor4f(r, g, b, 1F);
            rm.itemRenderer.renderItem(fakePlayer, stack, 0, ItemRenderType.EQUIPPED);
            GL11.glColor4f(1F, 1F, 1F, 1F);
        }
    }
    
    public void renderWingsStack(MannequinFakePlayer fakePlayer, ItemStack stack, ModelBiped targetBiped, RenderManager rm, byte[] extraColours, double distance) {
        Item targetItem = stack.getItem();
        if (SkinNBTHelper.stackHasSkinData(stack)) {
            SkinPointer sp = SkinNBTHelper.getSkinPointerFromStack(stack);
            if (sp.getIdentifier().getSkinType() == SkinTypeRegistry.skinWings) {
                SkinModelRenderer.INSTANCE.renderEquipmentPartFromStack(fakePlayer, stack, null, extraColours, distance, true);
                return;
            }
        }
    }
    
    private void bindTexture(ResourceLocation resourceLocation) {
        UtilRender.bindTexture(resourceLocation);
    }
    
    private void setRotations(ModelRenderer des, ModelRenderer src) {
        des.rotateAngleX = src.rotateAngleX;
        des.rotateAngleY = src.rotateAngleY;
        des.rotateAngleZ = src.rotateAngleZ;
    }
    
    private void resetRotations(ModelRenderer des) {
        des.rotateAngleX = 0F;
        des.rotateAngleY = 0F;
        des.rotateAngleZ = 0F;
    }*/
}
