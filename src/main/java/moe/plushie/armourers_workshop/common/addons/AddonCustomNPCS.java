package moe.plushie.armourers_workshop.common.addons;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.IExtraColours;
import moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability;
import moe.plushie.armourers_workshop.api.common.capability.IWardrobeCap;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderHelper;
import moe.plushie.armourers_workshop.client.render.SkinRenderData;
import moe.plushie.armourers_workshop.client.render.entity.SkinLayerRendererHeldItem;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.capability.wardrobe.WardrobeCap;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDye;
import moe.plushie.armourers_workshop.common.skin.entity.SkinnableEntity;
import moe.plushie.armourers_workshop.common.skin.entity.SkinnableEntityRegisty;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
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
            SkinnableEntityRegisty.INSTANCE.registerEntity(new SkinnableEntityCustomNPC());
        }
    }

    @Override
    public void postInit() {
        if (setIsModLoaded()) {
            npcAPI = getApi();
        }
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
                Method m = ReflectionHelper.findMethod(npcAPI.getClass(), "getIEntity", null, new Class[] { Entity.class });
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
     * @param part     0:Head, 1:Body, 2:ArmLeft, 3:ArmRight, 4:LegLeft, 5:LegRight
     * @return
     */
    public static float[] getModelScale(Object iDisplay, int part) {
        if (npcAPI != null) {
            try {
                Method m = ReflectionHelper.findMethod(iDisplay.getClass(), "getModelScale", null, new Class[] { int.class });
                return (float[]) m.invoke(iDisplay, new Object[] { part });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    private static Class<? extends EntityLivingBase>  getCNPCEntityClass() {
        try {
            return (Class<? extends EntityLivingBase>) Class.forName(CLASS_NAME_ENTITY_CNPC);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
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
                LayerRenderer<? extends EntityLivingBase> layerRendererNPC = new SkinLayerRendererCustomNPC((RenderLivingBase) renderer);
                if (layerRendererNPC != null) {
                    ((RenderLivingBase<?>) renderer).addLayer(layerRendererNPC);
                }

                try {
                    Object object = ReflectionHelper.getPrivateValue(RenderLivingBase.class, (RenderLivingBase) renderer, "field_177097_h", "layerRenderers");
                    if (object != null) {
                        List<LayerRenderer<?>> layerRenderers = (List<LayerRenderer<?>>) object;
                        // Looking for held item layer.
                        for (int i = 0; i < layerRenderers.size(); i++) {
                            LayerRenderer<?> layerRenderer = layerRenderers.get(i);
                            if (layerRenderer.getClass().getName().contains("LayerHeldItem")) {
                                // Replacing held item layer.
                                ModLogger.log("Removing held item layer from " + renderer);
                                layerRenderers.remove(i);
                                ModLogger.log("Adding skinned held item layer to " + renderer);
                                layerRenderers.add(new SkinLayerRendererHeldItem((RenderLivingBase) renderer, layerRenderer));
                                break;
                            }
                        }
                    } else {
                        ModLogger.log(Level.WARN, "Failed to get 'layerRenderers' on " + renderer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        @Override
        public Class<? extends EntityLivingBase> getEntityClass() {
            return getCNPCEntityClass();
        }

        @Override
        public void getValidSkinTypes(ArrayList<ISkinType> skinTypes) {
            skinTypes.add(SkinTypeRegistry.skinOutfit);
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
                return 10;
            }
            if (skinType == SkinTypeRegistry.skinOutfit) {
                return 10;
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
            MinecraftForge.EVENT_BUS.register(this);
        }

        @Override
        public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            IEntitySkinCapability skinCapability = EntitySkinCapability.get(entitylivingbaseIn);
            if (skinCapability == null) {
                return;
            }
            double distance = Minecraft.getMinecraft().player.getDistance(entitylivingbaseIn.posX, entitylivingbaseIn.posY, entitylivingbaseIn.posZ);
            if (distance > ConfigHandlerClient.renderDistanceSkin) {
                return;
            }
            // Object iEntity = AddonCustomNPCS.getiEntity(entitylivingbaseIn);
            // Object display = AddonCustomNPCS.getDisplay(entitylivingbaseIn);
            // ModLogger.log(display);

            ISkinType[] skinTypes = skinCapability.getValidSkinTypes();
            SkinModelRenderHelper modelRenderer = SkinModelRenderHelper.INSTANCE;
            IExtraColours extraColours = ExtraColours.EMPTY_COLOUR;
            IWardrobeCap wardrobe = WardrobeCap.get(entitylivingbaseIn);
            if (wardrobe != null) {
                extraColours = wardrobe.getExtraColours();
            }
            GlStateManager.enableRescaleNormal();
            for (int i = 0; i < skinTypes.length; i++) {
                ISkinType skinType = skinTypes[i];
                if (skinType.getVanillaArmourSlotId() != -1 | skinType == SkinTypeRegistry.skinWings | skinType == SkinTypeRegistry.skinOutfit) {
                    for (int skinIndex = 0; skinIndex < skinCapability.getSlotCountForSkinType(skinType); skinIndex++) {
                        ISkinDescriptor skinDescriptor = skinCapability.getSkinDescriptor(skinType, skinIndex);
                        if (skinDescriptor != null) {
                            Skin skin = ClientSkinCache.INSTANCE.getSkin(skinDescriptor);
                            if (skin == null) {
                                continue;
                            }
                            SkinDye dye = new SkinDye(wardrobe.getDye());
                            for (int dyeIndex = 0; dyeIndex < 8; dyeIndex++) {
                                if (skinDescriptor.getSkinDye().haveDyeInSlot(dyeIndex)) {
                                    dye.addDye(dyeIndex, skinDescriptor.getSkinDye().getDyeColour(dyeIndex));
                                }
                            }
                            ResourceLocation texture = DefaultPlayerSkin.getDefaultSkinLegacy();
                            modelRenderer.renderEquipmentPart(skin, new SkinRenderData(0.0625F, dye, extraColours, distance, true, true, false, texture), entitylivingbaseIn, (ModelBiped) renderLivingBase.getMainModel());
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
        
        @SubscribeEvent(priority = EventPriority.LOW)
        public void onRenderLivingPre(RenderLivingEvent.Pre<EntityLivingBase> event) {
            if (event.getEntity().getClass() != getCNPCEntityClass()) {
                return;
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public void onRenderLivingPost(RenderLivingEvent.Post<EntityLivingBase> event) {
            if (event.getEntity().getClass() != getCNPCEntityClass()) {
                return;
            }
        }
    }
}
