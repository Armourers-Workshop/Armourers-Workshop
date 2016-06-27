package riskyken.armourersWorkshop.client.render.item;

public class RenderItemSwordSkin /*implements IItemRenderer*/ {/*

    private final RenderItem renderItem;
    private final Minecraft mc;
    
    public RenderItemSwordSkin() {
        renderItem = (RenderItem) RenderManager.instance.entityRenderMap.get(EntityItem.class);
        mc = Minecraft.getMinecraft();
    }
    
    @Override
    public boolean handleRenderType(ItemStack stack, ItemRenderType type) {
        IItemRenderer render = Addons.getItemRenderer(stack, type);
        if (canRenderModel(stack)) {
            if (type == ItemRenderType.INVENTORY) {
                if (render != null) {
                    return render.handleRenderType(stack, type);
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } else {
            if (render != null) {
                return render.handleRenderType(stack, type);
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack stack, ItemRendererHelper helper) {
        IItemRenderer render = Addons.getItemRenderer(stack, type);
        if (canRenderModel(stack)) {
            if (type == ItemRenderType.INVENTORY) {
                if (render != null) {
                    return render.shouldUseRenderHelper(type, stack, helper);
                } else {
                    return false;
                }
            } else {
                return type == ItemRenderType.ENTITY;
            }
        } else {
            if (render != null) {
                return render.shouldUseRenderHelper(type, stack, helper);
            } else {
                return false;
            }
        }
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        if (canRenderModel(stack) & type != ItemRenderType.INVENTORY) {
            if (type != ItemRenderType.ENTITY) {
                GL11.glPopMatrix();
                GL11.glRotatef(-135, 0, 1, 0);
                GL11.glRotatef(-10, 0, 0, 1);
            }

            GL11.glPushMatrix();
            
            GL11.glScalef(1F, -1F, 1F);
            GL11.glScalef(1.6F, 1.6F, 1.6F);

            boolean isBlocking = false;
            
            if (data.length >= 2) {
                if (data[1] instanceof AbstractClientPlayer & data[0] instanceof RenderBlocks) {
                    RenderBlocks renderBlocks = (RenderBlocks) data[0];
                    AbstractClientPlayer player = (AbstractClientPlayer) data[1];
                    isBlocking = player.isBlocking();
                }
            }
            
            float scale = 0.0625F;
            
            switch (type) {
            case EQUIPPED:
                
                GL11.glTranslatef(2F * scale, -1F * scale, 0F * scale);
                if (isBlocking) {
                    GL11.glTranslatef(-0F * scale, 2F * scale, 1F * scale);
                }
                GL11.glRotatef(90F, 0F, 1F, 0F);
                break;
            case ENTITY:
                GL11.glScalef(-1F, 1F, 1F);
                GL11.glTranslatef(0F, -10F * scale, 0F);
                break;
            case EQUIPPED_FIRST_PERSON:
                GL11.glScalef(-1F, 1F, 1F);
                GL11.glRotatef(-90F, 0F, 1F, 0F);
                break;
            default:
                break;
            }
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GL11.glEnable(GL11.GL_CULL_FACE);
            ModRenderHelper.enableAlphaBlend();
            Addons.onWeaponRender(type, EventState.PRE);
            
            SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
            Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer);
            if (skin != null) {
                ItemStackRenderHelper.renderSkinWithHelper(skin, skinPointer, false);
            }
            Addons.onWeaponRender(type, EventState.POST);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
            
            if (type != ItemRenderType.ENTITY) {
                GL11.glPushMatrix();
            }

        } else {
            IItemRenderer render = Addons.getItemRenderer(stack, type);
            if (render != null) {
                render.renderItem(type, stack, data);
            } else {
                renderNomalIcon(stack);
            }
        }
    }
    
    private boolean canRenderModel(ItemStack stack) {
        if (SkinNBTHelper.stackHasSkinData(stack)) {
            SkinPointer skinData = SkinNBTHelper.getSkinPointerFromStack(stack);
            if (ClientSkinCache.INSTANCE.isSkinInCache(skinData)) {
                return true;
            } else {
                ClientSkinCache.INSTANCE.requestSkinFromServer(skinData);
                return false;
            }
        } else {
            return false;
        }
    }
    
    private void renderNomalIcon(ItemStack stack) {
        IIcon icon = stack.getItem().getIcon(stack, 0);
        renderItem.renderIcon(0, 0, icon, icon.getIconWidth(), icon.getIconHeight());
        icon = stack.getItem().getIcon(stack, 1);
        renderItem.renderIcon(0, 0, icon, icon.getIconWidth(), icon.getIconHeight());
    }*/
}
