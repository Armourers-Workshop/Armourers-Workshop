package riskyken.armourersWorkshop.client.model;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.api.common.skin.Point3D;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinDye;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.render.MannequinFakePlayer;
import riskyken.armourersWorkshop.client.render.SkinModelRenderer;
import riskyken.armourersWorkshop.client.render.SkinPartRenderer;
import riskyken.armourersWorkshop.common.config.ConfigHandlerClient;
import riskyken.armourersWorkshop.common.data.PlayerPointer;
import riskyken.armourersWorkshop.common.skin.EquipmentWardrobeData;
import riskyken.armourersWorkshop.common.skin.ExPropsPlayerSkinData;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPart;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.proxies.ClientProxy;
import riskyken.armourersWorkshop.proxies.ClientProxy.SkinRenderType;
import riskyken.armourersWorkshop.utils.SkinUtils;

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
        mc.mcProfiler.startSection("armourers player render");
        SkinModelRenderer modelRenderer = SkinModelRenderer.INSTANCE;
        EntityPlayer player = modelRenderer.targetPlayer;
        if (player == null) {
            mc.mcProfiler.endSection();
            return;
        }
        if (player instanceof MannequinFakePlayer) {
            mc.mcProfiler.endSection();
            return;
        }
        
        double distance = Minecraft.getMinecraft().thePlayer.getDistance(
                player.posX,
                player.posY,
                player.posZ);
        if (distance > ConfigHandlerClient.maxSkinRenderDistance) {
            return;
        }
        
        EquipmentWardrobeData ewd = ClientProxy.equipmentWardrobeHandler.getEquipmentWardrobeData(new PlayerPointer(player));
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
                        ForgeDirection axis = ForgeDirection.DOWN;
                        
                        if (partData.getMarkerCount() > 0) {
                            point = partData.getMarker(0);
                            axis = partData.getMarkerSide(0);
                        }
                        
                        GL11.glTranslated(scale * 0.5F, scale * 0.5F, scale * 0.5F);
                        GL11.glTranslated(scale * point.getX(), scale * point.getY(), scale * point.getZ());
                        if (skinPart.getRegistryName().equals("armourers:wings.leftWing")) {
                            switch (axis) {
                            case UP:
                                GL11.glRotated(angle, 0, 1, 0);
                                break;
                            case DOWN:
                                GL11.glRotated(angle, 0, 1, 0);
                                break;
                            case NORTH:
                                GL11.glRotated(-angle, 0, 0, 1);
                                break;
                            case EAST:
                                GL11.glRotated(angle, 1, 0, 0);
                                break;
                            case SOUTH:
                                GL11.glRotated(angle, 0, 0, 1);
                                break;
                            case WEST:
                                GL11.glRotated(-angle, 1, 0, 0);
                                break;
                            case UNKNOWN:
                                break;
                            }
                        } else {
                            angle = -angle;
                            switch (axis) {
                            case UP:
                                GL11.glRotated(angle, 0, 1, 0);
                                break;
                            case DOWN:
                                GL11.glRotated(angle, 0, 1, 0);
                                break;
                            case NORTH:
                                GL11.glRotated(-angle, 0, 0, 1);
                                break;
                            case EAST:
                                GL11.glRotated(angle, 1, 0, 0);
                                break;
                            case SOUTH:
                                GL11.glRotated(angle, 0, 0, 1);
                                break;
                            case WEST:
                                GL11.glRotated(-angle, 1, 0, 0);
                                break;
                            case UNKNOWN:
                                break;
                            }
                        }
                        GL11.glTranslated(scale * -point.getX(), scale * -point.getY(), scale * -point.getZ());
                        GL11.glTranslated(scale * -0.5F, scale * -0.5F, scale * -0.5F);
                    }
                    GL11.glEnable(GL11.GL_CULL_FACE);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    GL11.glEnable(GL11.GL_BLEND);
                    SkinPartRenderer.INSTANCE.renderPart(partData, scale, skinDye, extraColours, distance, true);
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
        mc.mcProfiler.endSection();
    }
}
