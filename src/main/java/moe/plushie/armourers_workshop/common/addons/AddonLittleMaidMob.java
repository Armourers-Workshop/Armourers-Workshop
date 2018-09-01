package moe.plushie.armourers_workshop.common.addons;

public class AddonLittleMaidMob extends ModAddon {

    public AddonLittleMaidMob() {
        super("lmmx", "Little Maid Mob");
    }
    
    @Override
    public boolean isModLoaded() {
        return false;
        //return super.isModLoaded();
    }
    
    @Override
    public void init() {
        if (isModLoaded()) {
            //EntitySkinHandler.INSTANCE.registerEntity(new SkinnableEntityLittleMaidMob());
        }
    }
    /*
    public static class SkinnableEntityLittleMaidMob implements ISkinnableEntity {

        @Override
        public Class<? extends EntityLivingBase> getEntityClass() {
            try {
                return (Class<? extends EntityLivingBase>) Class.forName("littleMaidMobX.LMM_EntityLittleMaid");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @SideOnly(Side.CLIENT)
        @Override
        public  Class<? extends ISkinnableEntityRenderer> getRendererClass() {
            return SkinnableEntityLittleMaidMobRenderer.class;
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
    public static class SkinnableEntityLittleMaidMobRenderer implements ISkinnableEntityRenderer {

        @Override
        public void render(EntityLivingBase entity, RendererLivingEntity renderer, double x, double y, double z, IEntityEquipment entityEquipment) {
            float scale = 0.0625F;
            
            GL11.glPushMatrix();
            
            GL11.glTranslated(x, y, z);
            GL11.glScalef(1, -1, -1);
            GL11.glScalef(0.94F, 0.94F, 0.94F);
            
            GL11.glTranslated(0, -24 * scale, 0);
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
            
            Object object = null;
            try {
                object = ReflectionHelper.getPrivateValue(RendererLivingEntity.class, renderer, "field_77045_g", "mainModel");
            } catch (UnableToAccessFieldException e) {
                e.printStackTrace();
            }

            AbstractModelSkin model = SkinModelRenderer.INSTANCE.getModelForEquipmentType(skinType);
            if (object != null && object instanceof ModelBiped) {
                model.render(entity, (ModelBiped) object, skin, false, skinPointer.getSkinDye(), null, false, 0, false);
            } else {
                model.render(entity, null, skin, false, skinPointer.getSkinDye(), null, false, 0, false);
            }
        }
    }*/
}
