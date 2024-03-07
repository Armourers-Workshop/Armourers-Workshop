package moe.plushie.armourers_workshop.init.platform.forge.proxy;

import moe.plushie.armourers_workshop.builder.client.render.PaintingHighlightPlacementRenderer;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBufferSource;
import moe.plushie.armourers_workshop.compatibility.client.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.client.render.HighlightPlacementRenderer;
import moe.plushie.armourers_workshop.init.ModConfig;
import moe.plushie.armourers_workshop.init.ModItems;
import moe.plushie.armourers_workshop.init.client.ClientWardrobeHandler;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import manifold.ext.rt.api.auto;

public class ClientProxyImpl {

    public static void init() {
        EnvironmentExecutor.willInit(EnvironmentType.CLIENT);
        EnvironmentExecutor.willSetup(EnvironmentType.CLIENT);

        // listen the fml events.
        Registry.willClientSetupFO(event -> EnvironmentExecutor.didInit(EnvironmentType.CLIENT));
        Registry.willLoadCompleteFO(event -> event.enqueueWork(() -> EnvironmentExecutor.didSetup(EnvironmentType.CLIENT)));

        // listen the block highlight events.
        Registry.willRenderBlockHighlightFO((traceResult, camera, poseStackIn, buffersIn) -> {
            Player player = EnvironmentManager.getPlayer();
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
            auto poseStack = AbstractPoseStack.wrap(poseStackIn);
            auto buffers = AbstractBufferSource.wrap(buffersIn);
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

        Registry.willRenderLivingEntityFO(ClientWardrobeHandler::onRenderLivingEntityPre);
        Registry.didRenderLivingEntityFO(ClientWardrobeHandler::onRenderLivingEntityPost);

        Registry.willRenderSpecificHandFO((player, arm, light, poseStack, buffers, cancel) -> {
            if (!ModConfig.enableFirstPersonSkinRenderer()) {
                return;
            }
            auto transformType = AbstractItemTransformType.FIRST_PERSON_LEFT_HAND;
            if (arm == HumanoidArm.RIGHT) {
                transformType = AbstractItemTransformType.FIRST_PERSON_RIGHT_HAND;
            }
            ClientWardrobeHandler.onRenderSpecificHand(player, 0, light, transformType, poseStack, buffers, cancel);
        });
    }
}
