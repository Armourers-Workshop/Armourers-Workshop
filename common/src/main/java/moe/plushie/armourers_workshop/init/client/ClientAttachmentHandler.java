package moe.plushie.armourers_workshop.init.client;

import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.api.core.math.IPoseStack;
import moe.plushie.armourers_workshop.compat.client.math.AbstractPoseStack;
import moe.plushie.armourers_workshop.core.client.other.EntityRenderData;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentPose;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentType;
import moe.plushie.armourers_workshop.core.skin.attachment.SkinAttachmentTypes;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
public class ClientAttachmentHandler {

    private static final OpenVector3f GUN_LEFT_WAIST_ORIGIN = new OpenVector3f(-4, 12, 0);
    private static final OpenVector3f GUN_RIGHT_WAIST_ORIGIN = new OpenVector3f(4, 12, 0);
    private static final OpenVector3f GUN_BACKPACK_ORIGIN = new OpenVector3f(0, 24, 2);

    public static void onRenderName(Entity entity, Component name, PoseStack poseStackIn, MultiBufferSource bufferSourceIn) {
        apply(entity, SkinAttachmentTypes.NAME, 0, poseStackIn, bufferSourceIn, (poseStack, attachmentPose, index) -> {
            // calculate the distance from target.
            var offset1 = OpenVector3f.ZERO.transforming(poseStack.last().pose());
            var offset2 = OpenVector3f.ZERO.transforming(attachmentPose.pose());

            float dx = offset2.x() - offset1.x();
            float dy = (offset2.y() + 0.5f) - offset1.y();
            float dz = offset2.z() - offset1.z();

            poseStack.translate(dx, dy, dz);
        });
    }

    public static void onRenderHand(Entity entity, ItemStack itemStack, OpenItemDisplayContext displayContext, PoseStack poseStackIn, MultiBufferSource bufferSourceIn, Consumer<ItemStack> handler) {
        var attachmentType = switch (displayContext) {
            case FIRST_PERSON_LEFT_HAND, THIRD_PERSON_LEFT_HAND -> SkinAttachmentTypes.LEFT_HAND;
            case FIRST_PERSON_RIGHT_HAND, THIRD_PERSON_RIGHT_HAND -> SkinAttachmentTypes.RIGHT_HAND;
            default -> SkinAttachmentTypes.UNKNOWN;
        };
        applyMultiple(entity, attachmentType, poseStackIn, bufferSourceIn, (poseStack, attachmentPose, index) -> {
            // ..
            if (poseStack != null && attachmentPose != null) {
                poseStack.last().set(attachmentPose);
                poseStack.rotate(OpenVector3f.XP.rotationDegrees(-90));
                poseStack.rotate(OpenVector3f.YP.rotationDegrees(180));
            }
            handler.accept(itemStack);
        });
    }

    public static void onRenderParrot(Entity entity, boolean isLeft, PoseStack poseStackIn, MultiBufferSource bufferSourceIn) {
        var attachmentType = SkinAttachmentTypes.RIGHT_SHOULDER;
        if (isLeft) {
            attachmentType = SkinAttachmentTypes.LEFT_SHOULDER;
        }
        apply(entity, attachmentType, 0, poseStackIn, bufferSourceIn, (poseStack, attachmentPose, index) -> {
            poseStack.last().set(attachmentPose);
            poseStack.translate(0, -1.5f, 0);
        });
    }

    public static void onRenderGun(Entity entity, ItemStack itemStack, OpenVector3f offset, PoseStack poseStackIn, MultiBufferSource bufferSourceIn) {
        var attachmentType = SkinAttachmentTypes.BACKPACK;
        var attachmentOrigin = GUN_BACKPACK_ORIGIN;

        float tx = offset.x();
        float ty = offset.y();
        float tz = offset.z();
        if (ty < 16) {
            if (tx < 0) {
                attachmentType = SkinAttachmentTypes.LEFT_WAIST;
                attachmentOrigin = GUN_LEFT_WAIST_ORIGIN;
            } else {
                attachmentType = SkinAttachmentTypes.RIGHT_WAIST;
                attachmentOrigin = GUN_RIGHT_WAIST_ORIGIN;
            }
        }

        float dx = (tx - attachmentOrigin.x()) / 16f;
        float dy = (ty - attachmentOrigin.y()) / 16f;
        float dz = (tz - attachmentOrigin.z()) / 16f;

        apply(entity, attachmentType, 0, poseStackIn, bufferSourceIn, (poseStack, attachmentPose, index) -> {
            poseStack.last().set(attachmentPose);
            poseStack.translate(-dx, -dy, dz);
        });
    }

    private static void apply(Entity entity, SkinAttachmentType attachmentType, int index, PoseStack poseStackIn, MultiBufferSource buffersIn, Applier applier) {
        var renderData = EntityRenderData.of(entity);
        if (renderData == null) {
            return;
        }
        var attachmentPose = renderData.getAttachmentPose(attachmentType, index);
        if (attachmentPose == null) {
            return; // pass, use vanilla behavior.
        }
        var poseStack = AbstractPoseStack.wrap(poseStackIn);
        applier.apply(poseStack, attachmentPose, index);
    }

    private static void applyMultiple(Entity entity, SkinAttachmentType attachmentType, PoseStack poseStackIn, MultiBufferSource buffersIn, Applier applier) {
        var renderData = EntityRenderData.of(entity);
        if (renderData == null) {
            applier.apply(null, null, 0);
            return;
        }
        var poseStack = AbstractPoseStack.wrap(poseStackIn);
        var attachmentPoses = renderData.attachmentManager().get(attachmentType);
        if (attachmentPoses != null) {
            attachmentPoses.forEach((index, attachmentPose) -> {
                poseStack.pushPose();
                applier.apply(poseStack, attachmentPose, index);
                poseStack.popPose();
            });
            if (attachmentPoses.containsKey(0)) {
                return; // all is rendering.
            }
        }
        var attachmentPose = renderData.getAttachmentPose(attachmentType, 0);
        if (attachmentPose == null) {
            applier.apply(null, null, 0);
            return;  // pass, use vanilla behavior.
        }
        applier.apply(poseStack, attachmentPose, 0);
    }

    private interface Applier {

        void apply(IPoseStack poseStack, SkinAttachmentPose pose, int index);
    }
}
