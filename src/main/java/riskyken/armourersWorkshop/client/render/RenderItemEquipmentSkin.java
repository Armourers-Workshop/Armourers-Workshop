package riskyken.armourersWorkshop.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.common.lib.LibCommonTags;
import riskyken.armourersWorkshop.proxies.ClientProxy;

public class RenderItemEquipmentSkin implements IItemRenderer {

    private final RenderItem renderItem;
    private final Minecraft mc;

    public RenderItemEquipmentSkin() {
        renderItem = (RenderItem) RenderManager.instance.entityRenderMap.get(EntityItem.class);
        mc = Minecraft.getMinecraft();
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,ItemRendererHelper helper) {
        //if (type != ItemRenderType.INVENTORY) { return false; }
        if (item.hasTagCompound() && item.getTagCompound().hasKey(LibCommonTags.TAG_ARMOUR_DATA)) {
            return true;
        }
        return false;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        if (item.hasTagCompound() && item.getTagCompound().hasKey(LibCommonTags.TAG_ARMOUR_DATA)) {

            GL11.glScalef(-1F, -1F, 1F);
            float scale = 1.2F;
            GL11.glScalef(scale, scale, scale);
            GL11.glRotatef(180, 0, 1, 0);
            switch (type) {
            case EQUIPPED:
                GL11.glRotatef(180, 0, 1, 0);
                GL11.glTranslatef(-0.5F, -0.8F, 0.5F);
                break;
            case ENTITY:
                GL11.glTranslatef(0F, -0.3F, 0F);
                break;
            case EQUIPPED_FIRST_PERSON:
                GL11.glScalef(0.8F, 0.8F, 0.8F);
                GL11.glRotatef(90, 0, 1, 0);
                GL11.glTranslatef(0.3F, -0.9F, 0.3F);
                break;
            default:
                break;
            }
            ClientProxy.renderItemAsArmourModel(item);
            
        } else {
            IIcon icon = item.getItem().getIcon(item, 0);
            renderItem.renderIcon(0, 0, icon, icon.getIconWidth(), icon.getIconHeight());
        }
        GL11.glPopMatrix();
        
    }
}
