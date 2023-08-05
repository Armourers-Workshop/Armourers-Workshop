package moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder.panel;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.impl.KeyboardManagerImpl;
import com.apple.library.uikit.UIEvent;
import com.apple.library.uikit.UIView;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.builder.blockentity.AdvancedSkinBuilderBlockEntity;
import moe.plushie.armourers_workshop.builder.client.render.AdvancedSkinBuilderBlockEntityRenderer;
import moe.plushie.armourers_workshop.builder.entity.CameraEntity;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderTesselator;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.math.ClamppedVector3f;
import moe.plushie.armourers_workshop.utils.math.OpenAABB;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.utils.math.OpenNearPlane;
import moe.plushie.armourers_workshop.utils.math.OpenRay;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Consumer;

public class AdvancedCameraPanel extends UIView {

    private CGPoint startMousePos = CGPoint.ZERO;

    private final Vector3f origin = new Vector3f();

    private final Vector3f oldRotation = new Vector3f();
    private final Vector3f oldTranslate = new Vector3f();

    private final ClamppedVector3f lastScale = new ClamppedVector3f(1, 1, 1, 0.5f, 0.5f, 0.5f, 5.0f, 5.0f, 5.0f);
    private final ClamppedVector3f lastRotation = new ClamppedVector3f(0, 0, 0, -90, Float.NEGATIVE_INFINITY, 0, 90, Float.POSITIVE_INFINITY, 0);
    private final ClamppedVector3f lastTranslate = new ClamppedVector3f(0, 0, 0, -8, -8, -8, 8, 8, 8);

    private final Options options;
    private final AdvancedSkinBuilderBlockEntity blockEntity;
    private final CameraEntity cameraEntity = new CameraEntity();

    private Collection<Node> cachedTree;

    boolean moveMode = false;
    boolean rotationMode = false;

    public AdvancedCameraPanel(AdvancedSkinBuilderBlockEntity blockEntity) {
        super(CGRect.ZERO);
        this.options = Minecraft.getInstance().options;
        this.blockEntity = blockEntity;
    }

    public void connect() {
        cameraEntity.connect();
        reset();
    }

    public void disconnect() {
        reset();
        cameraEntity.disconnect();
    }

    public void reset() {
        origin.set(blockEntity.getRenderOrigin());
        lastRotation.set(0, 0, 0);
        lastTranslate.set(0, 0, 0);
        lastScale.set(1, 1, 1);
        applyCameraChanges();
    }

    @Override
    public void mouseDown(UIEvent event) {
        rotationMode = event.type() != UIEvent.Type.MOUSE_RIGHT_DOWN;
        if (KeyboardManagerImpl.hasSpaceDown()) {
            rotationMode = !rotationMode;
        }
        moveMode = !rotationMode;
        startMousePos = event.locationInWindow();
        // save the camera last state.
        oldRotation.set(lastRotation);
        oldTranslate.set(lastTranslate);
    }

    @Override
    public void mouseUp(UIEvent event) {
        // save the camera last state.
        oldRotation.set(lastRotation);
        oldTranslate.set(lastTranslate);
    }

    @Override
    public void mouseDragged(UIEvent event) {
        CGPoint mousePos = event.locationInWindow();
        if (moveMode) {
            float dx = mousePos.x - startMousePos.x;
            float dy = mousePos.y - startMousePos.y;
            move(new Vector3f(dx, dy, 0));
        }
        if (rotationMode) {
            rotation(mousePos);
        }
        applyCameraChanges();
    }

    @Override
    public void mouseWheel(UIEvent event) {
        if (KeyboardManagerImpl.hasControlDown()) {
            zoom(event.delta());
        } else {
            double delta = event.delta();
            if (delta < 0) {
                move(new Vector3f(0, 0, 0.95f));
            } else if (delta > 0) {
                move(new Vector3f(0, 0, -0.95f));
            }
            oldTranslate.set(lastTranslate);
        }
        applyCameraChanges();
    }

    @Override
    public void mouseMoved(UIEvent event) {
        raycast(event);
    }

    private void move(Vector3f delta) {
        CGRect window = bounds();
        OpenNearPlane plane = cameraEntity.getNearPlane();
        float near = options.getCameraNear();

        float deltaX = -delta.getX() / (window.width / 2f);
        float deltaY = delta.getY() / (window.height / 2f);
        float deltaZ = near + delta.getZ();

        Vector3f d1 = plane.at(0, 0, -near);
        Vector3f d2 = plane.at(deltaX, deltaY, deltaZ);

        float x = oldTranslate.getX() + d1.getX() + d2.getX();
        float y = oldTranslate.getY() + d1.getY() + d2.getY();
        float z = oldTranslate.getZ() + d1.getZ() + d2.getZ();

        lastTranslate.set(x, y, z);
    }

    private void rotation(CGPoint mousePos) {
        CGRect window = bounds();

        float dx = (mousePos.y - startMousePos.y) / window.height;
        float dy = (mousePos.x - startMousePos.x) / window.width;
        float rx = oldRotation.getX() + dx * 360;
        float ry = oldRotation.getY() + dy * 360;

        lastRotation.set(rx, ry, 0);
    }

    private void zoom(double delta) {
        float scale = lastScale.getX();
        if (delta < 0) {
            scale *= 0.95f;
        } else if (delta > 0) {
            scale /= 0.95f;
        }
        lastScale.set(scale, scale, scale);
        cachedTree = null;
    }

    private void raycast(UIEvent event) {
        CGRect window = bounds();
        CGPoint mousePos = event.locationInWindow();

        float deltaX = ((mousePos.x - window.width / 2f)) / (window.width / 2f);
        float deltaY = -((mousePos.y - window.height / 2f)) / (window.height / 2f);
        float deltaZ = options.getCameraNear();

        OpenNearPlane plane = cameraEntity.getNearPlane();

        Vector3f d1 = plane.at(0, 0, -deltaZ);
        Vector3f d2 = plane.at(deltaX, deltaY, deltaZ);

        Vector3f location = lastTranslate.adding(origin);
        Vector3f hit = location.adding(d1).adding(d2);
        Vector3f origin = location.adding(d1);
        Vector3f direction = hit.subtracting(origin).normalizing();

        OpenRay ray = new OpenRay(origin, direction);
        ArrayList<Result> results = new ArrayList<>();
        buildPickTree().forEach(node -> node.raycast(ray, results::add));
        if (results.isEmpty()) {
            AdvancedSkinBuilderBlockEntityRenderer.setResult(Collections.emptyList());
        } else {
            results.sort(Comparator.comparing(it -> it.distance));
            AdvancedSkinBuilderBlockEntityRenderer.setResult(Collections.singleton(results.get(0).part));
        }

//        AdvancedSkinBuilderBlockEntityRenderer.setOutput(0, origin);
//        AdvancedSkinBuilderBlockEntityRenderer.setOutput(1, origin.adding(direction.scaling(50)));
//        ModLog.debug("{}/{}/({} {}) ", mousePos, window, deltaX, deltaY);
    }

    public Collection<Node> buildPickTree() {
        if (cachedTree != null) {
            return cachedTree;
        }
        AdvancedSkinBuilderBlockEntity entity = blockEntity;
        SkinRenderTesselator tesselator = SkinRenderTesselator.create(entity.descriptor, Tickets.TEST);
        if (tesselator == null) {
            return Collections.emptyList();
        }
        ArrayList<Node> allNodes = new ArrayList<>();
        PoseStack poseStack = new PoseStack();

        Vector3f pos = entity.getRenderOrigin();
        Vector3f scale = entity.carmeScale;

        poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
        poseStack.scale(scale.getX(), scale.getY(), scale.getZ());
        poseStack.scale(-MathUtils.SCALE, -MathUtils.SCALE, MathUtils.SCALE);

        tesselator.setLightmap(0xf000f0);
        tesselator.setPartialTicks(0);
        tesselator.setBuffer(skin -> (bakedPart, bakedSkin, scheme, shouldRender, context) -> {
            if (shouldRender) {
                OpenMatrix4f mat = new OpenMatrix4f(context.pose().lastPose());
                mat.invert();
                allNodes.add(new Node(bakedPart, mat));
            }
            return 0;
        });

        tesselator.draw(poseStack, null);

        // build tree.

        cachedTree = allNodes;
        return allNodes;
    }

    public void applyCameraChanges() {

        float tx = lastTranslate.getX();
        float ty = lastTranslate.getY();
        float tz = lastTranslate.getZ();

        float rx = lastRotation.getX();
        float ry = lastRotation.getY();
        float rz = lastRotation.getZ();

        float sx = lastScale.getX();
        float sy = lastScale.getY();
        float sz = lastScale.getZ();

        blockEntity.carmeOffset.set(tx, ty, tz);
        blockEntity.carmeRot.set(rx, ry, rz);
        blockEntity.carmeScale.set(sx, sy, sz);

        cameraEntity.setXRot(rx);
        cameraEntity.setYRot(ry);
        cameraEntity.setPos(origin.getX() + tx, origin.getY() + ty, origin.getZ() + tz);
        cameraEntity.setOldPosAndRot();
    }


    public static class Result {

        final BakedSkinPart part;
        float distance;

        Result(BakedSkinPart part, float distance) {
            this.part = part;
            this.distance = distance;
        }
    }

    public static class Node {

        final OpenMatrix4f invMat;
        final BakedSkinPart part;
        final OpenAABB box;

        Node(BakedSkinPart part, OpenMatrix4f invMat) {
            this.part = part;
            this.invMat = invMat;
            this.box = part.getRenderShape().aabb();
        }

        public void raycast(OpenRay ray, Consumer<Result> recorder) {
            OpenRay ray1 = ray.transforming(invMat);
            if (!box.intersects(ray1)) {
                return;
            }
            Result[] result = {null};
            part.forEach(ray1, face -> {
                float distance = ray1.origin.distanceToSquared(new Vector3f(face.x, face.y, face.z));
                if (result[0] == null) {
                    result[0] = new Result(part, distance);
                } else {
                    result[0].distance = Math.min(result[0].distance, distance);
                }
            });
            if (result[0] != null) {
                recorder.accept(result[0]);
            }
        }
    }
}

