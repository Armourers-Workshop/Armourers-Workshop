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
import riskyken.armourersWorkshop.client.render.MannequinFakePlayer;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentItemData;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentPartData;
import riskyken.armourersWorkshop.proxies.ClientProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelRendererAttachment extends ModelRenderer {

    private final EnumEquipmentType equipmentType;
    private final EnumEquipmentPart equipmentPart;
    private final Minecraft mc;
    public ModelRendererAttachment(ModelBase modelBase, EnumEquipmentType equipmentType, EnumEquipmentPart equipmentPart) {
        super(modelBase);
        mc = Minecraft.getMinecraft();
        this.equipmentType = equipmentType;
        this.equipmentPart = equipmentPart;
        addBox(0, 0, 0, 0, 0, 0);
    }
    
    @Override
    public void render(float scale) {
        if (ConfigHandler.compatibilityRender) {
            return;
        }
        mc.mcProfiler.startSection("armourers player render");
        EquipmentModelRenderer modelRenderer = EquipmentModelRenderer.INSTANCE;
        EntityPlayer player = modelRenderer.targetPlayer;
        if (player == null) {
            mc.mcProfiler.endSection();
            return;
        }
        if (player instanceof MannequinFakePlayer) {
            mc.mcProfiler.endSection();
            return;
        }
        if (!EquipmentRenderHelper.withinMaxRenderDistance(player.posX, player.posY, player.posZ)) {
            mc.mcProfiler.endSection();
            return;
        }
        CustomEquipmentItemData data = modelRenderer.getPlayerCustomArmour(player, equipmentType);
        if (data == null) {
            mc.mcProfiler.endSection();
            return;
        }
        
        data.onRender();
        int size = data.getParts().size();
        for (int i = 0; i < size; i++) {
            CustomEquipmentPartData partData = data.getParts().get(i);
            if (partData.getArmourPart() == equipmentPart) {
                GL11.glPushMatrix();
                if (equipmentType == EnumEquipmentType.SKIRT && equipmentPart == EnumEquipmentPart.SKIRT) {
                    GL11.glTranslatef(0, 12 * scale, 0);
                    if (player.isSneaking()) {
                        GL11.glTranslated(0, -3 * scale, 0);
                        GL11.glRotatef(-28.6478898F, 1F, 0F, 0F);
                    }
                }
                GL11.glEnable(GL11.GL_CULL_FACE);
                EquipmentPartRenderer.INSTANCE.renderPart(partData, scale);
                GL11.glDisable(GL11.GL_CULL_FACE);
                GL11.glPopMatrix();
                break;
            }
        }
        
        if (ClientProxy.shadersModLoaded) {
            if (player instanceof AbstractClientPlayer) {
                AbstractClientPlayer clientPlayer = (AbstractClientPlayer) player;
                Minecraft.getMinecraft().renderEngine.bindTexture(clientPlayer.getLocationSkin());
            }
        }
        mc.mcProfiler.endSection();
    }
}
