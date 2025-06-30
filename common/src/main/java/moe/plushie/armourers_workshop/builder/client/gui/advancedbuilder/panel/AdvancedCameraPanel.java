package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.panel;

import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.impl.InputManagerImpl;
import com.apple.library.uikit.UIEvent;
import com.apple.library.uikit.UIView;
import moe.plushie.armourers_workshop.builder.blockentity.AdvancedBuilderBlockEntity;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document.DocumentEditor;
import moe.plushie.armourers_workshop.builder.data.ClamppedVector3f;
import moe.plushie.armourers_workshop.builder.entity.CameraEntity;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.math.OpenAxisAlignedBoundingBox;
import moe.plushie.armourers_workshop.core.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.core.math.OpenVector3f;
import moe.plushie.armourers_workshop.init.platform.EnvironmentManager;
import net.minecraft.client.Options;

import java.util.Collection;

public class AdvancedCameraPanel extends UIView {

    private CGPoint startMousePos = CGPoint.ZERO;

    private final OpenVector3f origin = new OpenVector3f();

    private final OpenVector3f oldRotation = new OpenVector3f();
    private final OpenVector3f oldTranslate = new OpenVector3f();

    private final OpenVector3f lastZoom = new ClamppedVector3f(1, 1, 1, 0.1f, 0.1f, 0.1f, 2.0f, 2.0f, 2.0f);
    private final OpenVector3f lastRotation = new ClamppedVector3f(0, 0, 0, -90, Float.NEGATIVE_INFINITY, 0, 90, Float.POSITIVE_INFINITY, 0);
    private final OpenVector3f lastTranslate = new OpenVector3f(0, 0, 0);


    private final Options options;
    private final AdvancedBuilderBlockEntity blockEntity;
    private final CameraEntity cameraEntity = new CameraEntity();

    private Collection<Node> cachedTree;

    boolean moveMode = false;
    boolean rotationMode = false;

    public AdvancedCameraPanel(DocumentEditor editor) {
        super(CGRect.ZERO);
        this.options = EnvironmentManager.getClient().options;
        this.blockEntity = editor.blockEntity();
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
        lastZoom.set(1, 1, 1);
        applyCameraChanges();
    }

    @Override
    public void mouseDown(UIEvent event) {
        rotationMode = event.type() != UIEvent.Type.MOUSE_RIGHT_DOWN;
        if (InputManagerImpl.hasSpaceDown()) {
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
            move(new OpenVector3f(dx, dy, 0));
        }
        if (rotationMode) {
            rotation(mousePos);
        }
        applyCameraChanges();
    }

    @Override
    public void mouseWheel(UIEvent event) {
        zoom(event.delta().y());
    }

    @Override
    public void mouseMoved(UIEvent event) {
//        raycast(event);
    }

    private void move(OpenVector3f delta) {
        var window = bounds();
        var plane = cameraEntity.getNearPlane();
        float near = options.getCameraNear();

        float deltaX = -delta.x() / (window.width / 2f);
        float deltaY = delta.y() / (window.height / 2f);
        float deltaZ = near + delta.z();

        var d1 = plane.at(0, 0, -near);
        var d2 = plane.at(deltaX, deltaY, deltaZ);

        float x = oldTranslate.x() + d1.x() + d2.x();
        float y = oldTranslate.y() + d1.y() + d2.y();
        float z = oldTranslate.z() + d1.z() + d2.z();

        lastTranslate.set(x, y, z);
    }

    private void rotation(CGPoint mousePos) {
        var window = bounds();

        float dx = (mousePos.y - startMousePos.y) / window.height;
        float dy = (mousePos.x - startMousePos.x) / window.width;
        float rx = oldRotation.x() + dx * 360;
        float ry = oldRotation.y() + dy * 360;

        lastRotation.set(rx, ry, 0);
    }

    private void zoom(double delta) {
        float scale = lastZoom.x();
        if (delta < 0) {
            scale /= 0.99f;
        } else if (delta > 0) {
            scale *= 0.99f;
        }
        lastZoom.set(scale, scale, scale);
        applyCameraChanges();
    }

//    private void raycast(UIEvent event) {
//        CGRect window = bounds();
//        CGPoint mousePos = event.locationInWindow();
//
//        float deltaX = ((mousePos.x - window.width / 2f)) / (window.width / 2f);
//        float deltaY = -((mousePos.y - window.height / 2f)) / (window.height / 2f);
//        float deltaZ = options.getCameraNear();
//
//        OpenNearPlane plane = cameraEntity.getNearPlane();
//
//        Vector3f d1 = plane.at(0, 0, -deltaZ);
//        Vector3f d2 = plane.at(deltaX, deltaY, deltaZ);
//
//        Vector3f location = lastTranslate.adding(origin);
//        Vector3f hit = location.adding(d1).adding(d2);
//        Vector3f origin = location.adding(d1);
//        Vector3f direction = hit.subtracting(origin).normalizing();
//
//        OpenRay ray = new OpenRay(origin, direction);
//        ArrayList<Result> results = new ArrayList<>();
//        buildPickTree().forEach(node -> node.raycast(ray, results::add));
//        if (results.isEmpty()) {
//            AdvancedSkinBuilderBlockEntityRenderer.setResult(Collections.emptyList());
//        } else {
//            results.sort(Comparator.comparing(it -> it.distance));
//            AdvancedSkinBuilderBlockEntityRenderer.setResult(Collections.singleton(results.get(0).part));
//        }
//
////        AdvancedSkinBuilderBlockEntityRenderer.setOutput(0, origin);
////        AdvancedSkinBuilderBlockEntityRenderer.setOutput(1, origin.adding(direction.scaling(50)));
////        ModLog.debug("{}/{}/({} {}) ", mousePos, window, deltaX, deltaY);
//    }

//    public Collection<Node> buildPickTree() {
//        if (cachedTree != null) {
//            return cachedTree;
//        }
//        AdvancedSkinBuilderBlockEntity entity = blockEntity;
//        SkinRenderTesselator tesselator = SkinRenderTesselator.create(entity.descriptor, Tickets.TEST);
//        if (tesselator == null) {
//            return Collections.emptyList();
//        }
//        ArrayList<Node> allNodes = new ArrayList<>();
//        PoseStack poseStack = new PoseStack();
//
//        Vector3f pos = entity.getRenderOrigin();
//        Vector3f scale = entity.carmeScale;
//
//        poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
//        poseStack.scale(scale.getX(), scale.getY(), scale.getZ());
//        poseStack.scale(-MathUtils.SCALE, -MathUtils.SCALE, MathUtils.SCALE);
//
//        tesselator.setLightmap(0xf000f0);
//        tesselator.setPartialTicks(0);
//        tesselator.setBufferProvider(skin -> (bakedPart, bakedSkin, scheme, shouldRender, context) -> {
//            if (shouldRender) {
//                OpenMatrix4f mat = new OpenMatrix4f(context.pose().lastPose());
//                mat.invert();
//                allNodes.add(new Node(bakedPart, mat));
//            }
//            return 0;
//        });
//
//        tesselator.draw(poseStack, null);
//
//        // build tree.
//
//        cachedTree = allNodes;
//        return allNodes;
//    }

    public void applyCameraChanges() {

        float tx = lastTranslate.x();
        float ty = lastTranslate.y();
        float tz = lastTranslate.z();

        float rx = lastRotation.x();
        float ry = lastRotation.y();
        float rz = lastRotation.z();

        blockEntity.carmeOffset.set(tx, ty, tz);
        blockEntity.carmeRot.set(rx, ry, rz);

        cameraEntity.setZoom(lastZoom.z());
        cameraEntity.setXRot(rx);
        cameraEntity.setYRot(ry);
        cameraEntity.setPos(origin.x() + tx, origin.y() + ty, origin.z() + tz);
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
        final OpenAxisAlignedBoundingBox box;

        Node(BakedSkinPart part, OpenMatrix4f invMat) {
            this.part = part;
            this.invMat = invMat;
            this.box = part.renderShape().aabb();
        }

//        public void raycast(OpenRay ray, Consumer<Result> recorder) {
//            var ray1 = ray.transforming(invMat);
//            if (!box.intersects(ray1)) {
//                return;
//            }
//            Result[] result = {null};
//            part.getQuads().forEach(ray1, face -> {
//                // TODO: Support Transform @SAGESSE
//                //var transform = face.getTransform();
//                var shape = face.getShape();
//                var distance = ray1.origin.distanceToSquared(shape.getMinX(), shape.getMinY(), shape.getMinZ());
//                if (result[0] == null) {
//                    result[0] = new Result(part, distance);
//                } else {
//                    result[0].distance = Math.min(result[0].distance, distance);
//                }
//            });
//            if (result[0] != null) {
//                recorder.accept(result[0]);
//            }
//        }
    }
}

