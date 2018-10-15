package moe.plushie.armourers_workshop.common.addons;

import java.lang.reflect.Method;
import java.util.ArrayList;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderer;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.entityskin.IEntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.capability.wardrobe.IWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.WardrobeCap;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDye;
import moe.plushie.armourers_workshop.common.skin.entity.EntitySkinHandler;
import moe.plushie.armourers_workshop.common.skin.entity.SkinnableEntity;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AddonCustomNPCS extends ModAddon {

    private static final String CLASS_NAME_ENTITY_CNPC = "noppes.npcs.entity.EntityCustomNpc";
    private static final String CLASS_NAME_NPC_API = "noppes.npcs.api.NpcAPI";
    
    private static Object npcAPI = null;
    
    public AddonCustomNPCS() {
        super("customnpcs", "CustomNPC");
    }
    
    @Override
    public void init() {
        if (isModLoaded()) {
            EntitySkinHandler.INSTANCE.registerEntity(new SkinnableEntityCustomNPC());
        }
    }
    
    @Override
    public void postInit() {
        npcAPI = getApi();
    }
    
    private static Object getApi() {
        try {
            Class c = Class.forName(CLASS_NAME_NPC_API);
            Method m = ReflectionHelper.findMethod(c, "Instance", null, new Class[] {});
            return m.invoke(null, new Object[] {});  
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static Object getiEntity(Entity entity) {
        if (npcAPI != null) {
            try {
                Method m = ReflectionHelper.findMethod(npcAPI.getClass(), "getIEntity", null, new Class[] {Entity.class});
                return m.invoke(npcAPI, entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    public static Object getDisplay(Object iEntity) {
        if (npcAPI != null) {
            try {
                Method m = ReflectionHelper.findMethod(iEntity.getClass(), "getDisplay", null, new Class[] {});
                return m.invoke(iEntity, new Object[] {});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    /**
     * 
     * @param iDisplay
     * @param part 0:Head, 1:Body, 2:ArmLeft, 3:ArmRight, 4:LegLeft, 5:LegRight
     * @return
     */
    public static float[] getModelScale(Object iDisplay, int part) {
        if (npcAPI != null) {
            try {
                Method m = ReflectionHelper.findMethod(iDisplay.getClass(), "getModelScale", null, new Class[] {int.class});
                return (float[]) m.invoke(iDisplay, new Object[] {part});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    public static class SkinnableEntityCustomNPC extends SkinnableEntity {
        
        private boolean addedRender = false;
        
        public SkinnableEntityCustomNPC() {
            if (!ArmourersWorkshop.isDedicated()) {
                MinecraftForge.EVENT_BUS.register(this);
            }
        }
        
        @SideOnly(Side.CLIENT)
        @SubscribeEvent
        public void onRenderLiving(RenderLivingEvent.Pre event) {
            if (!addedRender) {
                if (event.getEntity().getClass().isAssignableFrom(getEntityClass())) {
                    addRenderLayer(Minecraft.getMinecraft().getRenderManager());
                    addedRender = true;
                }

            }
        }
        
        @SideOnly(Side.CLIENT)
        @Override
        public void addRenderLayer(RenderManager renderManager) {
            Render<Entity> renderer = renderManager.getEntityClassRenderObject(getEntityClass());
            if (renderer != null && renderer instanceof RenderLivingBase) {
                LayerRenderer<? extends EntityLivingBase> layerRenderer = new SkinLayerRendererCustomNPC((RenderLivingBase) renderer);
                if (layerRenderer != null) {
                    ((RenderLivingBase<?>) renderer).addLayer(layerRenderer);
                }
            }
            ModLogger.log(renderer);
        }
        
        @Override
        public Class<? extends EntityLivingBase> getEntityClass() {
            try {
                return (Class<? extends EntityLivingBase>) Class.forName(CLASS_NAME_ENTITY_CNPC);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void getValidSkinTypes(ArrayList<ISkinType> skinTypes) {
            skinTypes.add(SkinTypeRegistry.skinHead);
            skinTypes.add(SkinTypeRegistry.skinChest);
            skinTypes.add(SkinTypeRegistry.skinLegs);
            skinTypes.add(SkinTypeRegistry.skinFeet);
            skinTypes.add(SkinTypeRegistry.skinWings);
            
            skinTypes.add(SkinTypeRegistry.skinSword);
            skinTypes.add(SkinTypeRegistry.skinShield);
            skinTypes.add(SkinTypeRegistry.skinBow);
        }

        @Override
        public int getSlotsForSkinType(ISkinType skinType) {
            if (skinType.getVanillaArmourSlotId() != -1 | skinType == SkinTypeRegistry.skinWings) {
                return 4;
            }
            return 1;
        }
        
        @Override
        public boolean canUseWandOfStyle(EntityPlayer user) {
            return true;
        }
        
    }
    
    
    @SideOnly(Side.CLIENT)
    public static class SkinLayerRendererCustomNPC implements LayerRenderer {

        private final RenderLivingBase renderLivingBase;
        
        public SkinLayerRendererCustomNPC(RenderLivingBase renderLivingBase) {
            this.renderLivingBase = renderLivingBase;
        }
        
        @Override
        public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            IEntitySkinCapability skinCapability = EntitySkinCapability.get(entitylivingbaseIn);
            if (skinCapability == null) {
                return;
            }
            double distance = Minecraft.getMinecraft().player.getDistance(
                    entitylivingbaseIn.posX,
                    entitylivingbaseIn.posY,
                    entitylivingbaseIn.posZ);
            if (distance > ConfigHandlerClient.skinMaxRenderDistance) {
                return;
            }
            //Object iEntity = AddonCustomNPCS.getiEntity(entitylivingbaseIn);
            //Object display = AddonCustomNPCS.getDisplay(entitylivingbaseIn);
            //ModLogger.log(display);
            
            ISkinType[] skinTypes = skinCapability.getValidSkinTypes();
            SkinModelRenderer modelRenderer = SkinModelRenderer.INSTANCE;
            ExtraColours extraColours = ExtraColours.EMPTY_COLOUR;
            IWardrobeCap wardrobe = WardrobeCap.get(entitylivingbaseIn);
            if (wardrobe != null) {
                extraColours = wardrobe.getExtraColours();
            }
            GlStateManager.enableRescaleNormal();
            for (int i = 0; i < skinTypes.length; i++) {
                ISkinType skinType = skinTypes[i];
                if (skinType.getVanillaArmourSlotId() != -1 | skinType == SkinTypeRegistry.skinWings) {
                    for (int skinIndex = 0; skinIndex < skinCapability.getSlotCountForSkinType(skinType); skinIndex++) {
                        ISkinDescriptor skinDescriptor = skinCapability.getSkinDescriptor(skinType, skinIndex);
                        if (skinDescriptor != null) {
                            Skin skin = ClientSkinCache .INSTANCE.getSkin(skinDescriptor);
                            if (skin == null) {
                                continue;
                            }
                            SkinDye dye = new SkinDye(wardrobe.getDye());
                            for (int dyeIndex = 0; dyeIndex < 8; dyeIndex++) {
                                if (skinDescriptor.getSkinDye().haveDyeInSlot(dyeIndex)) {
                                    dye.addDye(dyeIndex, skinDescriptor.getSkinDye().getDyeColour(dyeIndex));
                                }
                            }
                            modelRenderer.renderEquipmentPart(entitylivingbaseIn, (ModelBiped) renderLivingBase.getMainModel(), skin, dye, extraColours, 0, true);
                        }
                    }
                }
            }
            GlStateManager.disableRescaleNormal();
        }
        
        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }
}
