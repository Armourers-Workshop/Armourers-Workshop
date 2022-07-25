//package moe.plushie.armourers_workshop.core.render.layer;
//
//import com.mojang.blaze3d.matrix.PoseStack;
//import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.renderer.MultiBufferSource;
//import net.minecraft.client.renderer.entity.RenderLayerParent;
//import net.minecraft.client.renderer.entity.layers.VillagerLevelPendantLayer;
//import net.minecraft.client.renderer.entity.model.EntityModel;
//import net.minecraft.client.renderer.entity.model.IHeadToggle;
//import net.minecraft.client.resources.data.VillagerMetadataSection;
//import net.minecraft.entity.LivingEntity;
//import net.minecraft.entity.villager.IVillagerDataHolder;
//import net.minecraft.resources.IReloadableResourceManager;
//import net.minecraft.resources.IResourceManager;
//import net.minecraft.util.registry.DefaultedRegistry;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//
//import java.util.function.BiFunction;
//
//@SuppressWarnings("NullableProblems")
//@Environment(value = EnvType.CLIENT)
//public class DelegateVillagerLevelPendantLayer<T extends LivingEntity & IVillagerDataHolder, M extends EntityModel<T> & IHeadToggle> extends VillagerLevelPendantLayer<T, M> {
//
//    protected final VillagerLevelPendantLayer<T, M> pendantLayer;
//
//    public DelegateVillagerLevelPendantLayer(RenderLayerParent<T, M> renderer, VillagerLevelPendantLayer<T, M> pendantLayer, BiFunction<T, M, Boolean> test) {
//        super(renderer, (IReloadableResourceManager) Minecraft.getInstance().getResourceManager(), "");
//        this.pendantLayer = pendantLayer;
//    }
//
//    @Override
//    public void render(PoseStack matrixStack, MultiBufferSource buffers, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
//        pendantLayer.render(matrixStack, buffers, packedLightIn, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
//    }
//
//    @Override
//    public <K> VillagerMetadataSection.HatType getHatData(Object2ObjectMap<K, VillagerMetadataSection.HatType> p_215350_1_, String p_215350_2_, DefaultedRegistry<K> p_215350_3_, K p_215350_4_) {
//        return pendantLayer.getHatData(p_215350_1_, p_215350_2_, p_215350_3_, p_215350_4_);
//    }
//
//    @Override
//    public void onResourceManagerReload(IResourceManager resourceManager) {
//    }
//}
