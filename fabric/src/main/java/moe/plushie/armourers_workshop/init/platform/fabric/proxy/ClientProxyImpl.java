package moe.plushie.armourers_workshop.init.platform.fabric.proxy;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.client.IBufferSource;
import moe.plushie.armourers_workshop.api.common.IEntityHandler;
import moe.plushie.armourers_workshop.api.math.IPoseStack;
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
import moe.plushie.armourers_workshop.init.platform.fabric.config.FabricConfig;
import moe.plushie.armourers_workshop.init.platform.fabric.config.FabricConfigTracker;
import moe.plushie.armourers_workshop.init.platform.fabric.event.ClientStartupEvents;
import moe.plushie.armourers_workshop.init.platform.fabric.event.RenderSpecificArmEvents;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockGatherCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

@Environment(EnvType.CLIENT)
public class ClientProxyImpl implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EnvironmentExecutor.willInit(EnvironmentType.CLIENT);

        WorldRenderEvents.BLOCK_OUTLINE.register(this::onDrawBlockHighlightEvent);

        ClientPickBlockGatherCallback.EVENT.register(this::onPickItem);

        RenderSpecificArmEvents.MAIN_HAND.register(this::onRenderSpecificFirstPersonHand);
        RenderSpecificArmEvents.OFF_HAND.register(this::onRenderSpecificFirstPersonHand);

        // load all configs
        FabricConfigTracker.INSTANCE.loadConfigs(FabricConfig.Type.CLIENT, FabricLoader.getInstance().getConfigDir());

        ClientStartupEvents.CLIENT_WILL_START.register(minecraft -> {
            // we will call `ResourceManager.registerReloadListener` in willSetup phase.
            EnvironmentExecutor.willSetup(EnvironmentType.CLIENT);
        });

        ClientStartupEvents.CLIENT_STARTED.register(minecraft -> {
            EnvironmentExecutor.didInit(EnvironmentType.CLIENT);
            EnvironmentExecutor.didSetup(EnvironmentType.CLIENT);
        });
    }

    public ItemStack onPickItem(Player player, HitResult result) {
        EntityHitResult result1 = ObjectUtils.safeCast(result, EntityHitResult.class);
        if (result1 != null) {
            IEntityHandler handler = ObjectUtils.safeCast(result1.getEntity(), IEntityHandler.class);
            if (handler != null) {
                return handler.getCustomPickResult(result);
            }
        }
        return ItemStack.EMPTY;
    }

    public boolean onDrawBlockHighlightEvent(WorldRenderContext context, WorldRenderContext.BlockOutlineContext blockOutlineContext) {
        Minecraft minecraft = Minecraft.getInstance();
        BlockHitResult target = ObjectUtils.safeCast(minecraft.hitResult, BlockHitResult.class);
        Player player = minecraft.player;
        if (player == null || target == null) {
            return true;
        }
        // hidden hit box at inside
        // if (event.getTarget().isInside()) {
        //     BlockState state = player.level.getBlockState(event.getTarget().getBlockPos());
        //     if (state.is(ModBlocks.BOUNDING_BOX)) {
        //         event.setCanceled(true);
        //         return;
        //     }
        // }
        IPoseStack poseStack = AbstractPoseStack.wrap(context.matrixStack());
        IBufferSource bufferSource = AbstractBufferSource.wrap(context.consumers());
        ItemStack itemStack = player.getMainHandItem();
        if (ModConfig.Client.enableEntityPlacementHighlight && itemStack.is(ModItems.MANNEQUIN.get())) {
            HighlightPlacementRenderer.renderEntity(player, target, context.camera(), poseStack, bufferSource);
        }
        if (ModConfig.Client.enableBlockPlacementHighlight && itemStack.is(ModItems.SKIN.get())) {
            HighlightPlacementRenderer.renderBlock(itemStack, player, target, context.camera(), poseStack, bufferSource);
        }
        if (ModConfig.Client.enablePaintToolPlacementHighlight && itemStack.is(ModItems.BLENDING_TOOL.get())) {
            PaintingHighlightPlacementRenderer.renderPaintTool(itemStack, player, target, context.camera(), poseStack, bufferSource);
        }
        return true;
    }

    public boolean onRenderSpecificFirstPersonHand(PoseStack poseStack, MultiBufferSource buffers, int light, Player player, InteractionHand hand) {
        if (!ModConfig.enableFirstPersonSkinRenderer()) {
            return true;
        }
        AbstractItemTransformType transformType = AbstractItemTransformType.FIRST_PERSON_LEFT_HAND;
        if (hand == InteractionHand.MAIN_HAND) {
            transformType = AbstractItemTransformType.FIRST_PERSON_RIGHT_HAND;
        }
        boolean[] flags = {false};
        ClientWardrobeHandler.onRenderSpecificHand(player, 0, light, transformType, poseStack, buffers, () -> {
            flags[0] = true;
        });
        return !flags[0];
    }
}
