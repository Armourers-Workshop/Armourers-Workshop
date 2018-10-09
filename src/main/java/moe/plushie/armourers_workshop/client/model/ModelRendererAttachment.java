package moe.plushie.armourers_workshop.client.model;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.skin.Point3D;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderer;
import moe.plushie.armourers_workshop.client.render.SkinPartRenderer;
import moe.plushie.armourers_workshop.common.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.common.data.PlayerPointer;
import moe.plushie.armourers_workshop.common.skin.PlayerWardrobe;
import moe.plushie.armourers_workshop.common.skin.ExPropsPlayerSkinData;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.common.skin.type.wings.SkinWings.MovementType;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import moe.plushie.armourers_workshop.proxies.ClientProxy.SkinRenderType;
import moe.plushie.armourers_workshop.utils.SkinUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * A ModelRenderer that is attached to each ModelRenderer on the
 * players ModelBiped as a sub part.
 * @author RiskyKen
 *
 */
@SideOnly(Side.CLIENT)
public class ModelRendererAttachment extends ModelRenderer {

    private final ISkinType skinType;
    private final ISkinPartType skinPart;
    private final Minecraft mc;
    private ModelBiped baseModel;
    
    public ModelRendererAttachment(ModelBiped modelBase, ISkinType skinType, ISkinPartType skinPart) {
        super(modelBase);
        this.baseModel = modelBase;
        mc = Minecraft.getMinecraft();
        this.skinType = skinType;
        this.skinPart = skinPart;
        addBox(0, 0, 0, 0, 0, 0);
    }
    
    @Override
    public void render(float scale) {
        if (ClientProxy.getSkinRenderType() != SkinRenderType.MODEL_ATTACHMENT) {
            return;
        }
        mc.profiler.startSection("armourers player render");
        SkinModelRenderer modelRenderer = SkinModelRenderer.INSTANCE;
        EntityPlayer player = modelRenderer.targetPlayer;
        if (player == null) {
            mc.profiler.endSection();
            return;
        }
        /*if (player instanceof MannequinFakePlayer) {
            mc.profiler.endSection();
            return;
        }*/
        double distance = Minecraft.getMinecraft().player.getDistance(
                player.posX,
                player.posY,
                player.posZ);
        if (distance > ConfigHandlerClient.maxSkinRenderDistance) {
            return;
        }
        
        PlayerWardrobe ewd = ClientProxy.equipmentWardrobeHandler.getEquipmentWardrobeData(new PlayerPointer(player));
        byte[] extraColours = null;
        if (ewd != null) {
            Color skinColour = new Color(ewd.skinColour);
            Color hairColour = new Color(ewd.hairColour);
            extraColours = new byte[] {
                    (byte)skinColour.getRed(), (byte)skinColour.getGreen(), (byte)skinColour.getBlue(),
                    (byte)hairColour.getRed(), (byte)hairColour.getGreen(), (byte)hairColour.getBlue()};
        }
        
        for (int skinIndex = 0; skinIndex < ExPropsPlayerSkinData.MAX_SLOTS_PER_SKIN_TYPE; skinIndex++) {
            Skin data = modelRenderer.getPlayerCustomArmour(player, skinType, skinIndex);
            if (data == null) {
                continue;
            }
            ISkinDye skinDye = modelRenderer.getPlayerDyeData(player, skinType, skinIndex);
            
            MovementType movmentType = MovementType.valueOf(SkinProperties.PROP_WINGS_MOVMENT_TYPE.getValue(data.getProperties()));
            
            int size = data.getParts().size();
            for (int i = 0; i < size; i++) {
                SkinPart partData = data.getParts().get(i);
                if (partData.getPartType() == skinPart) {
                    GL11.glPushMatrix();
                    
                    if (skinType == SkinTypeRegistry.skinLegs && skinPart.getRegistryName().equals("armourers:legs.skirt")) {
                        GL11.glTranslatef(0, 12 * scale, 0);
                        if (player.isSneaking()) {
                            GL11.glRotatef(-30, 1, 0, 0);
                            GL11.glTranslatef(0, -1.25F * scale, -2F * scale);
                            
                        }
                        if (player.isRiding()) {
                            GL11.glRotated(-70, 1F, 0F, 0F);
                        }
                    }
                    
                    
                    if (skinType == SkinTypeRegistry.skinWings) {
                        GL11.glTranslated(0, 0, scale * 2);
                        double angle = SkinUtils.getFlapAngleForWings(player, data);
                        Point3D point = new Point3D(0, 0, 0);
                        EnumFacing axis = EnumFacing.DOWN;
                        
                        if (partData.getMarkerCount() > 0) {
                            point = partData.getMarker(0);
                            axis = partData.getMarkerSide(0);
                        }
                        
                        GL11.glTranslated(scale * 0.5F, scale * 0.5F, scale * 0.5F);
                        GL11.glTranslated(scale * point.getX(), scale * point.getY(), scale * point.getZ());
                        
                        if (skinPart.getRegistryName().equals("armourers:wings.rightWing")) {
                            angle = -angle;
                        }
                        switch (axis) {
                        case UP:
                            GL11.glRotated(angle, 0, 1, 0);
                            break;
                        case DOWN:
                            GL11.glRotated(angle, 0, -1, 0);
                            break;
                        case SOUTH:
                            GL11.glRotated(angle, 0, 0, -1);
                            break;
                        case NORTH:
                            GL11.glRotated(angle, 0, 0, 1);
                            break;
                        case EAST:
                            GL11.glRotated(angle, 1, 0, 0);
                            break;
                        case WEST:
                            GL11.glRotated(angle, -1, 0, 0);
                            break;
                        }
                        
                        GL11.glTranslated(scale * -point.getX(), scale * -point.getY(), scale * -point.getZ());
                        GL11.glTranslated(scale * -0.5F, scale * -0.5F, scale * -0.5F);
                    }
                    
                    
                    
                    GL11.glEnable(GL11.GL_CULL_FACE);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    GL11.glEnable(GL11.GL_BLEND);
                    SkinPartRenderer.INSTANCE.renderPart(partData, scale, skinDye, extraColours, distance, true);
                    GlStateManager.resetColor();
                    GlStateManager.color(1, 1, 1, 1);
                    GL11.glDisable(GL11.GL_CULL_FACE);
                    GL11.glPopMatrix();
                    
                    break;
                }
            }
        }
        
        if (ClientProxy.useSafeTextureRender()) {
            if (player instanceof AbstractClientPlayer) {
                AbstractClientPlayer clientPlayer = (AbstractClientPlayer) player;
                Minecraft.getMinecraft().renderEngine.bindTexture(clientPlayer.getLocationSkin());
            }
        }
        mc.profiler.endSection();
    }
}
