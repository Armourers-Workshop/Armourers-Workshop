package riskyken.armourersWorkshop.client.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.equipment.skin.ISkinPart;
import riskyken.armourersWorkshop.api.common.equipment.skin.ISkinType;
import riskyken.armourersWorkshop.client.render.EquipmentModelRenderer;
import riskyken.armourersWorkshop.client.render.EquipmentPartRenderer;
import riskyken.armourersWorkshop.client.render.EquipmentRenderHelper;
import riskyken.armourersWorkshop.client.render.MannequinFakePlayer;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentItemData;
import riskyken.armourersWorkshop.common.equipment.data.CustomEquipmentPartData;
import riskyken.armourersWorkshop.common.equipment.skin.SkinTypeRegistry;
import riskyken.armourersWorkshop.proxies.ClientProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelRendererAttachment extends ModelRenderer {

    private final ISkinType skinType;
    private final ISkinPart skinPart;
    private final Minecraft mc;
    private ModelBiped baseModel;
    
    public ModelRendererAttachment(ModelBiped modelBase, ISkinType skinType, ISkinPart skinPart) {
        super(modelBase);
        this.baseModel = modelBase;
        mc = Minecraft.getMinecraft();
        this.skinType = skinType;
        this.skinPart = skinPart;
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
        CustomEquipmentItemData data = modelRenderer.getPlayerCustomArmour(player, skinType);
        if (data == null) {
            mc.mcProfiler.endSection();
            return;
        }
        
        data.onRender();
        int size = data.getParts().size();
        for (int i = 0; i < size; i++) {
            CustomEquipmentPartData partData = data.getParts().get(i);
            if (partData.getSkinPart() == skinPart) {
                GL11.glPushMatrix();
                if (skinType == SkinTypeRegistry.skinSkirt && skinPart.getRegistryName().equals("armourers:skirt.base")) {
                    GL11.glRotated(Math.toDegrees(-baseModel.bipedLeftLeg.rotateAngleX), 1F, 0F, 0F);
                    GL11.glTranslatef(-2 * scale, 0, 0);
                    if (player.isSneaking()) {
                    }
                }
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glEnable(GL11.GL_BLEND);
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
