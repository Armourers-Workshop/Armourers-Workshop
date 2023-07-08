package moe.plushie.armourers_workshop.init.platform.forge.proxy;

import moe.plushie.armourers_workshop.builder.client.render.PaintingHighlightPlacementRenderer;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.core.client.render.HighlightPlacementRenderer;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.forge.NotificationCenterImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

import manifold.ext.rt.api.auto;

@OnlyIn(Dist.CLIENT)
public class ClientProxyImpl {

    public static void init() {
        EnvironmentExecutor.willInit(EnvironmentType.CLIENT);
        EnvironmentExecutor.willSetup(EnvironmentType.CLIENT);

        // listen the fml events.
        NotificationCenterImpl.observer(FMLClientSetupEvent.class, event -> EnvironmentExecutor.didInit(EnvironmentType.CLIENT));
        NotificationCenterImpl.observer(FMLLoadCompleteEvent.class, event -> event.enqueueWork(() -> EnvironmentExecutor.didSetup(EnvironmentType.CLIENT)));

        // listen the block highlight events.
        Registry.willRenderBlockHighlightFO((traceResult, camera, poseStack, buffers) -> {
            Player player = Minecraft.getInstance().player;
            if (player == null) {
                return;
            }
            // hidden hit box at inside
            // if (event.getTarget().isInside()) {
            //     BlockState state = player.level.getBlockState(event.getTarget().getBlockPos());
            //     if (state.is(ModBlocks.BOUNDING_BOX)) {
            //         event.setCanceled(true);
            //         return;
            //     }
            // }
            ItemStack itemStack = player.getMainHandItem();
            if (ModConfig.Client.enableEntityPlacementHighlight && itemStack.is(ModItems.MANNEQUIN.get())) {
                HighlightPlacementRenderer.renderEntity(player, traceResult, camera, poseStack, buffers);
            }
            if (ModConfig.Client.enableBlockPlacementHighlight && itemStack.is(ModItems.SKIN.get())) {
                HighlightPlacementRenderer.renderBlock(itemStack, player, traceResult, camera, poseStack, buffers);
            }
            if (ModConfig.Client.enablePaintToolPlacementHighlight && itemStack.is(ModItems.BLENDING_TOOL.get())) {
                PaintingHighlightPlacementRenderer.renderPaintTool(itemStack, player, traceResult, camera, poseStack, buffers);
            }
        });

        Registry.willRenderLivingEntityFO(ClientWardrobeHandler::onRenderLivingPre);
        Registry.didRenderLivingEntityFO(ClientWardrobeHandler::onRenderLivingPost);

        NotificationCenterImpl.observer(RenderArmEvent.class, event -> {
            if (!ModConfig.enableFirstPersonSkinRenderer()) {
                return;
            }
            int light = event.getPackedLight();
            auto player = Minecraft.getInstance().player;
            auto poseStack = event.getPoseStack();
            auto buffers = event.getMultiBufferSource();
            auto transformType = AbstractItemTransformType.FIRST_PERSON_LEFT_HAND;
            if (event.getArm() == HumanoidArm.RIGHT) {
                transformType = AbstractItemTransformType.FIRST_PERSON_RIGHT_HAND;
            }
            ClientWardrobeHandler.onRenderSpecificHand(player, 0, light, transformType, poseStack, buffers, () -> {
                event.setCanceled(true);
            });
        });
    }
}
