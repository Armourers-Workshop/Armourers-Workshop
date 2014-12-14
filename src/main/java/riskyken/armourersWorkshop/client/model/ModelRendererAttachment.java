package riskyken.armourersWorkshop.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentPart;
import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;
import riskyken.armourersWorkshop.client.render.EquipmentModelRenderer;
import riskyken.armourersWorkshop.client.render.EquipmentPartRenderer;
import riskyken.armourersWorkshop.client.render.EquipmentRenderHelper;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentItemData;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentPartData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelRendererAttachment extends ModelRenderer {

    private final EnumEquipmentType equipmentType;
    private final EnumEquipmentPart equipmentPart;
    
    public ModelRendererAttachment(ModelBase modelBase, EnumEquipmentType equipmentType, EnumEquipmentPart equipmentPart) {
        super(modelBase);
        this.equipmentType = equipmentType;
        this.equipmentPart = equipmentPart;
        addBox(0, 0, 0, 0, 0, 0);
    }
    
    @Override
    public void render(float scale) {
        EquipmentModelRenderer modelRenderer = EquipmentModelRenderer.INSTANCE;
        EntityPlayer player = modelRenderer.targetPlayer;
        if (player == null) {
            return;
        }
        if (!EquipmentRenderHelper.withinMaxRenderDistance(player.posX, player.posY, player.posZ)) {
            return;
        }
        CustomEquipmentItemData data = modelRenderer.getPlayerCustomArmour(player, equipmentType);
        if (data == null) {
            return;
        }
        for (int i = 0; i < data.getParts().size(); i++) {
            CustomEquipmentPartData partData = data.getParts().get(i);
            if (partData.getArmourPart() == equipmentPart) {
                GL11.glPushMatrix();
                if (equipmentType == EnumEquipmentType.SKIRT && equipmentPart == EnumEquipmentPart.SKIRT) {
                    GL11.glTranslatef(0, 12 * scale, 0);
                }
                EquipmentPartRenderer.INSTANCE.renderPart(partData, scale);
                GL11.glPopMatrix();
                break;
            }
        }
        if (player instanceof AbstractClientPlayer) {
            AbstractClientPlayer clientPlayer = (AbstractClientPlayer) player;
            Minecraft.getMinecraft().renderEngine.bindTexture(clientPlayer.getLocationSkin());
        }
    }
}
