package moe.plushie.armourers_workshop.client.render.entity;

import java.util.ArrayList;
import java.util.HashMap;

import moe.plushie.armourers_workshop.api.client.render.entity.ISkinnableEntityRenderer;
import moe.plushie.armourers_workshop.api.common.skin.entity.ISkinnableEntity;
import moe.plushie.armourers_workshop.client.render.ModRenderHelper;
import moe.plushie.armourers_workshop.common.skin.entity.EntitySkinHandler;
import moe.plushie.armourers_workshop.common.skin.entity.ExPropsEntityEquipmentData;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class EntitySkinRenderHandler {
    
    public static EntitySkinRenderHandler INSTANCE;
    
    public static void init() {
        INSTANCE = new EntitySkinRenderHandler();
    }
    
    private HashMap<Class<? extends EntityLivingBase>, ISkinnableEntityRenderer> entityRenderer;
    
    public EntitySkinRenderHandler() {
        MinecraftForge.EVENT_BUS.register(this);
        entityRenderer = new HashMap<Class<? extends EntityLivingBase>, ISkinnableEntityRenderer>();
    }
    
    public void initRenderer() {
        loadEntityRenderers();
    }
    
    private void loadEntityRenderers() {
        ModLogger.log("Adding layer renderers to entities");
        ArrayList<ISkinnableEntity> skinnableEntities = EntitySkinHandler.INSTANCE.getRegisteredEntities();
        for (int i = 0; i < skinnableEntities.size(); i++) {
            ISkinnableEntity skinnableEntity = skinnableEntities.get(i);
            ModLogger.log("Adding layer renderer to entity " + skinnableEntity.getEntityClass());
            skinnableEntity.addRenderLayer(Minecraft.getMinecraft().getRenderManager());
        }
    }
    
    private void registerRendererForEntity(Class<? extends EntityLivingBase> entity, Class<? extends ISkinnableEntityRenderer> renderClass) {
        try {
            entityRenderer.put(entity, renderClass.newInstance());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    @SubscribeEvent
    public void onRenderLivingEvent(RenderLivingEvent.Post event) {
        EntityLivingBase entity = event.getEntity();
        if (entity instanceof EntityPlayer) {
            return;
        }
        if (entityRenderer.containsKey(entity.getClass())) {
            ISkinnableEntityRenderer renderer = entityRenderer.get(entity.getClass());
            ExPropsEntityEquipmentData props = ExPropsEntityEquipmentData.getExtendedPropsForEntity(entity);
            if (props == null) {
                return;
            }
            Minecraft.getMinecraft().profiler.startSection("wandOfStyleRender");
            ModRenderHelper.enableAlphaBlend();
            //renderer.render(entity, event.getRenderer(), event.getX(), event.getY(), event.getZ(), props.getEquipmentData());
            ModRenderHelper.disableAlphaBlend();
            Minecraft.getMinecraft().profiler.endSection();
        }
    }
}
