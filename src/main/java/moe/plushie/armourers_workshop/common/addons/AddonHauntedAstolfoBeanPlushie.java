package moe.plushie.armourers_workshop.common.addons;

import java.util.ArrayList;

import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import moe.plushie.armourers_workshop.api.common.IExtraColours;
import moe.plushie.armourers_workshop.api.common.capability.IWardrobeCap;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderHelper;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.capability.wardrobe.WardrobeCap;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDye;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.entity.SkinnableEntityRegisty;
import moe.plushie.armourers_workshop.common.skin.entity.SkinnableEntity;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSkeletonHead;
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
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class AddonHauntedAstolfoBeanPlushie extends ModAddon {

    private static final String ENTITY_CLASS_NAME = "moriyashiine.hauntedastolfobeanplushie.entity.EntityHauntedAstolfoBeanPlushie";

    public AddonHauntedAstolfoBeanPlushie() {
        super("hauntedastolfobeanplushie", "Haunted Astolfo Bean Plushie");
    }

    @Override
    public void init() {
        ModLogger.log("Haunted Astolfo Bean Plushie mod detected! - Applying bulge to mannequins.");
        SkinnableEntityRegisty.INSTANCE.registerEntity(new SkinnableEntityCustomEntity());
    }

    public static class SkinnableEntityCustomEntity extends SkinnableEntity {

        @SideOnly(Side.CLIENT)
        @Override
        public void addRenderLayer(RenderManager renderManager) {
            Render<Entity> renderer = renderManager.getEntityClassRenderObject(getEntityClass());
            if (renderer != null && renderer instanceof RenderLivingBase) {
                SkinLayerRendererBeanPlushie rendererBeanPlushie = new SkinLayerRendererBeanPlushie(getEntityClass(), (RenderLivingBase) renderer);
                if (rendererBeanPlushie != null) {
                    ((RenderLivingBase) renderer).addLayer(rendererBeanPlushie);
                }
            } else {
                ModLogger.log(Level.WARN, "Failed to get renderer for " + ENTITY_CLASS_NAME);
            }
        }

        @Override
        public Class<? extends EntityLivingBase> getEntityClass() {
            try {
                return (Class<? extends EntityLivingBase>) Class.forName(ENTITY_CLASS_NAME);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void getValidSkinTypes(ArrayList<ISkinType> skinTypes) {
            skinTypes.add(SkinTypeRegistry.skinHead);
        }

        @Override
        public int getSlotsForSkinType(ISkinType skinType) {
            return 1;
        }

        @Override
        public boolean canUseWandOfStyle(EntityPlayer user) {
            return true;
        }
    }

    @SideOnly(Side.CLIENT)
    public static class SkinLayerRendererBeanPlushie implements LayerRenderer {

        private final Class<? extends EntityLivingBase> entityClass;
        private final RenderLivingBase renderLivingBase;
        private ModelRenderer head = null;
        private ModelRenderer headwear = null;

        public SkinLayerRendererBeanPlushie(Class<? extends EntityLivingBase> entityClass, RenderLivingBase renderLivingBase) {
            this.entityClass = entityClass;
            this.renderLivingBase = renderLivingBase;
            try {
                ModelHumanoidHead modelHead = (ModelHumanoidHead) renderLivingBase.getMainModel();
                head = ReflectionHelper.getPrivateValue(ModelSkeletonHead.class, modelHead, "field_82896_a", "skeletonHead");
                headwear = ReflectionHelper.getPrivateValue(ModelHumanoidHead.class, modelHead, "field_178717_b", "head");

            } catch (Exception e) {
                ModLogger.log(Level.WARN, "Failed to get model parts for " + ENTITY_CLASS_NAME);
                e.printStackTrace();
            }
            if (head != null & headwear != null) {
                MinecraftForge.EVENT_BUS.register(this);
            }
        }

        @SubscribeEvent(priority = EventPriority.LOW)
        public void onRenderLivingPre(RenderLivingEvent.Pre event) {
            if (!event.getEntity().getClass().equals(entityClass)) {
                return;
            }
            EntitySkinCapability skinCapability = (EntitySkinCapability) EntitySkinCapability.get(event.getEntity());
            if (skinCapability == null) {
                return;
            }

            if (skinCapability.hideHead) {
                head.isHidden = true;
            }
            if (skinCapability.hideHead | skinCapability.hideHeadOverlay) {
                headwear.isHidden = true;
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGH)
        public void onRenderLivingPost(RenderLivingEvent.Post event) {
            if (!event.getEntity().getClass().equals(entityClass)) {
                return;
            }
            head.isHidden = false;
            headwear.isHidden = false;
        }

        @Override
        public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
            EntitySkinCapability skinCapability = (EntitySkinCapability) EntitySkinCapability.get(entitylivingbaseIn);
            if (skinCapability == null) {
                return;
            }
            skinCapability.hideHead = false;

            double distance = Minecraft.getMinecraft().player.getDistance(entitylivingbaseIn.posX, entitylivingbaseIn.posY, entitylivingbaseIn.posZ);
            if (distance > ConfigHandlerClient.renderDistanceSkin) {
                return;
            }

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
                for (int skinIndex = 0; skinIndex < skinCapability.getSlotCountForSkinType(skinType); skinIndex++) {
                    ISkinDescriptor skinDescriptor = skinCapability.getSkinDescriptor(skinType, skinIndex);
                    if (skinDescriptor != null) {
                        Skin skin = ClientSkinCache.INSTANCE.getSkin(skinDescriptor);
                        if (skin == null) {
                            continue;
                        }

                        if (SkinProperties.PROP_MODEL_OVERRIDE_HEAD.getValue(skin.getProperties())) {
                            skinCapability.hideHead = true;
                        }
                        if (SkinProperties.PROP_MODEL_HIDE_OVERLAY_HEAD.getValue(skin.getProperties())) {
                            skinCapability.hideHeadOverlay = true;
                        }

                        SkinDye dye = new SkinDye(wardrobe.getDye());
                        for (int dyeIndex = 0; dyeIndex < 8; dyeIndex++) {
                            if (skinDescriptor.getSkinDye().haveDyeInSlot(dyeIndex)) {
                                dye.addDye(dyeIndex, skinDescriptor.getSkinDye().getDyeColour(dyeIndex));
                            }
                        }

                        GlStateManager.pushMatrix();
                        head.isHidden = true;
                        if (head != null) {
                            GL11.glRotated(Math.toDegrees(head.rotateAngleZ), 0, 0, 1);
                            GL11.glRotated(Math.toDegrees(head.rotateAngleY), 0, 1, 0);
                            GL11.glRotated(Math.toDegrees(head.rotateAngleX), 1, 0, 0);
                        }
                        SkinModelRenderHelper.INSTANCE.modelHelperDummy.render(null, skin, null, true, dye, null, true, 0, true);
                        GlStateManager.popMatrix();
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
