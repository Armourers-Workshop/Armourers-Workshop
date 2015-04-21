package riskyken.armourersWorkshop.client.render.npc;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.common.equipment.skin.IEquipmentSkinType;
import riskyken.armourersWorkshop.client.render.ItemStackRenderHelper;
import riskyken.armourersWorkshop.common.equipment.EntityEquipmentData;
import riskyken.armourersWorkshop.common.equipment.npc.ExPropsEntityEquipmentData;
import riskyken.armourersWorkshop.common.equipment.skin.SkinTypeRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class NpcEquipmentRenderHandler {
    
    public static NpcEquipmentRenderHandler INSTANCE;
    
    public static void init() {
        INSTANCE = new NpcEquipmentRenderHandler();
    }
    
    public NpcEquipmentRenderHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onRenderLivingEvent(RenderLivingEvent.Post event) {
        EntityLivingBase entity = event.entity;
        if (entity instanceof EntityPlayer) {
            return;
        }
        ExPropsEntityEquipmentData props = ExPropsEntityEquipmentData.getExtendedPropsForEntity(entity);
        if (props == null) {
            return;
        }
        
        GL11.glPushMatrix();
        float scale = 0.0625F;
        //float yawOffset = entity.prevRenderYawOffset + (entity.renderYawOffset - entity.prevRenderYawOffset) * partialRenderTick;
        
        GL11.glTranslated(event.x, event.y, event.z);
        GL11.glScalef(1, -1, -1);
        
        //GL11.glTranslated(0, -entity.getYOffset(), 0);
        GL11.glTranslated(0, -entity.height + 1.5F * scale, 0);
        
        GL11.glRotatef(entity.renderYawOffset, 0, 1, 0);
        GL11.glRotatef(entity.rotationYawHead - entity.renderYawOffset, 0, 1, 0);
        GL11.glRotatef(entity.rotationPitch, 1, 0, 0);
        
        EntityEquipmentData equipmentData = props.getEquipmentData();
        renderEquipmentType(SkinTypeRegistry.skinHead, equipmentData);
        //renderEquipmentType(SkinTypeRegistry.skinChest, equipmentData);
        //renderEquipmentType(SkinTypeRegistry.skinLegs, equipmentData);
        //renderEquipmentType(SkinTypeRegistry.skinSkirt, equipmentData);
        //renderEquipmentType(SkinTypeRegistry.skinFeet, equipmentData);
        GL11.glPopMatrix();
    }
    
    private void renderEquipmentType(IEquipmentSkinType skinType, EntityEquipmentData equipmentData) {
        if (equipmentData.haveEquipment(skinType)) {
            int id = equipmentData.getEquipmentId(skinType);
            ItemStackRenderHelper.renderItemModelFromId(id, skinType);
        }
    }
}
