package riskyken.armourersWorkshop.common.addons;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import riskyken.armourersWorkshop.api.client.render.entity.ISkinnableEntityRenderer;
import riskyken.armourersWorkshop.api.common.skin.IEntityEquipment;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinPointer;
import riskyken.armourersWorkshop.api.common.skin.entity.ISkinnableEntity;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.handler.ModClientFMLEventHandler;
import riskyken.armourersWorkshop.client.model.skin.AbstractModelSkin;
import riskyken.armourersWorkshop.client.render.SkinModelRenderer;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.entity.EntitySkinHandler;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;

public class AddonCustomNPCS extends ModAddon {

    public AddonCustomNPCS() {
        super("customnpcs", "Custom NPCS");
    }
    
    @Override
    public void init() {
        EntitySkinHandler.INSTANCE.registerEntity(new SkinnableEntityCustomNPC());
    }
    
    public static class SkinnableEntityCustomNPC implements ISkinnableEntity {

        @Override
        public Class<? extends EntityLivingBase> getEntityClass() {
            try {
                return (Class<? extends EntityLivingBase>) Class.forName("noppes.npcs.entity.EntityCustomNpc");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @SideOnly(Side.CLIENT)
        @Override
        public  Class<? extends ISkinnableEntityRenderer> getRendererClass() {
            return SkinnableEntityCustomNPCRenderer.class;
        }

        @Override
        public boolean canUseWandOfStyle() {
            return true;
        }

        @Override
        public boolean canUseSkinsOnEntity() {
            return false;
        }

        @Override
        public void getValidSkinTypes(ArrayList<ISkinType> skinTypes)  {
            skinTypes.add(SkinTypeRegistry.skinHead);
            skinTypes.add(SkinTypeRegistry.skinChest);
            skinTypes.add(SkinTypeRegistry.skinLegs);
            skinTypes.add(SkinTypeRegistry.skinFeet);
            skinTypes.add(SkinTypeRegistry.skinWings);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public static class SkinnableEntityCustomNPCRenderer implements ISkinnableEntityRenderer {

        @Override
        public void render(EntityLivingBase entity, RendererLivingEntity renderer, double x, double y, double z, IEntityEquipment entityEquipment) {
            
            AbstractModelSkin model = SkinModelRenderer.INSTANCE.getModelForEquipmentType(SkinTypeRegistry.skinWings);
            
            ISkinPointer skinPointer = entityEquipment.getSkinPointer(SkinTypeRegistry.skinWings, 0);
            
            if (!entityEquipment.haveEquipment(SkinTypeRegistry.skinWings, 0)) {
                return;
            }
            Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer);
            if (skin == null) {
                return;
            }
            
            Object object = ReflectionHelper.getPrivateValue(RendererLivingEntity.class, renderer, "mainModel");
            float scale = 0.0625F;
            
            GL11.glPushMatrix();
            
            GL11.glTranslated(x, y, z);
            GL11.glScalef(1, -1, -1);
            GL11.glScalef(0.94F, 0.94F, 0.94F);
            
            GL11.glTranslated(0, -18 * scale + entity.height * scale, 0);
            entity.getEyeHeight();
            //renderTarget.prevPosX + (renderTarget.posX - renderTarget.prevPosX) * partialRenderTick;
            
            double rot = entity.prevRenderYawOffset + (entity.renderYawOffset - entity.prevRenderYawOffset) * ModClientFMLEventHandler.renderTickTime;
            
            GL11.glRotated(rot, 0, 1, 0);
            
            
            renderEquipmentType(entity, renderer, SkinTypeRegistry.skinHead, entityEquipment);
            renderEquipmentType(entity, renderer, SkinTypeRegistry.skinChest, entityEquipment);
            renderEquipmentType(entity, renderer, SkinTypeRegistry.skinLegs, entityEquipment);
            renderEquipmentType(entity, renderer, SkinTypeRegistry.skinFeet, entityEquipment);
            renderEquipmentType(entity, renderer, SkinTypeRegistry.skinWings, entityEquipment);

            GL11.glPopMatrix();
        }
        
        private void renderEquipmentType(EntityLivingBase entity, RendererLivingEntity renderer, ISkinType skinType, IEntityEquipment equipmentData) {
            
            if (!equipmentData.haveEquipment(skinType, 0)) {
                return;
            }
            ISkinPointer skinPointer = equipmentData.getSkinPointer(skinType, 0);
            Skin skin = ClientSkinCache.INSTANCE.getSkin(skinPointer);
            if (skin == null) {
                return;
            }
            
            Object object = ReflectionHelper.getPrivateValue(RendererLivingEntity.class, renderer, "mainModel");

            AbstractModelSkin model = SkinModelRenderer.INSTANCE.getModelForEquipmentType(skinType);
            if (object instanceof ModelBiped) {
                model.render(entity, (ModelBiped) object, skin, false, skinPointer.getSkinDye(), null, false, 0, false);
            } else {
                model.render(entity, null, skin, false, skinPointer.getSkinDye(), null, false, 0, false);
            }
        }
    }
}
