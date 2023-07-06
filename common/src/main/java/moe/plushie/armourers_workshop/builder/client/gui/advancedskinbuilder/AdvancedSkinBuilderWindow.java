package moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder;

import com.apple.library.coregraphics.CGAffineTransform;
import com.apple.library.coregraphics.CGPoint;
import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.foundation.NSString;
import com.apple.library.impl.KeyboardManagerImpl;
import com.apple.library.uikit.UIEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.builder.blockentity.AdvancedSkinBuilderBlockEntity;
import moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder.panel.SidebarView;
import moe.plushie.armourers_workshop.builder.client.render.AdvancedSkinBuilderBlockEntityRenderer;
import moe.plushie.armourers_workshop.builder.entity.CameraEntity;
import moe.plushie.armourers_workshop.builder.menu.AdvancedSkinBuilderMenu;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.gui.widget.MenuWindow;
import moe.plushie.armourers_workshop.core.client.gui.widget.TreeNode;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderTesselator;
import moe.plushie.armourers_workshop.core.data.ticket.Tickets;
import moe.plushie.armourers_workshop.init.ModLog;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.math.OpenAABB;
import moe.plushie.armourers_workshop.utils.math.OpenMatrix4f;
import moe.plushie.armourers_workshop.utils.math.OpenNearPlane;
import moe.plushie.armourers_workshop.utils.math.OpenRay;
import moe.plushie.armourers_workshop.utils.math.Vector3f;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class AdvancedSkinBuilderWindow extends MenuWindow<AdvancedSkinBuilderMenu> {

    public final AdvancedSkinBuilderBlockEntity blockEntity;
//    private final NSWindow window = new NSWindow(new Rectangle2i(0, 0, 320, 240));

//    public static AbstractMenuWindowProvider<AdvancedSkinBuilderMenu, AdvancedSkinBuilderWindow> PROVIDER;
//    public static AdvancedSkinBuilderWindow create(AdvancedSkinBuilderMenu container, Inventory inventory, NSString title) {
//        if (PROVIDER != null) {
//            return PROVIDER.create(container, inventory, title);
//        }
//        return new AdvancedSkinBuilderWindow(container, inventory, title);
//    }

    public AdvancedSkinBuilderWindow(AdvancedSkinBuilderMenu container, Inventory inventory, NSString title) {
        super(container, inventory, title);
        this.blockEntity = container.getBlockEntity(AdvancedSkinBuilderBlockEntity.class);
        this.inventoryView.setHidden(true);

        SidebarView sidebarView = new SidebarView(new CGRect(bounds().width - 180, 0, 180, bounds().height));
        sidebarView.setAutoresizingMask(AutoresizingMask.flexibleLeftMargin | AutoresizingMask.flexibleHeight);
        sidebarView.setTransform(CGAffineTransform.scale(0.5f, 0.5f));
        addSubview(sidebarView);

        sidebarView.reloadData();


//        AdvancedSkinCanvasView canvasView = new AdvancedSkinCanvasView(bounds());
//        canvasView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
//        addSubview(canvasView);

//        TreeNode rootNode = new TreeNode(new NSString("Root"));
//        TreeView treeView =  new TreeView(rootNode, new CGRect(2, 2, 120, bounds().getHeight() - 94));
//        treeView.setAutoresizingMask(AutoresizingMask.flexibleRightMargin | AutoresizingMask.flexibleHeight);
//        addSubview(treeView);
//
//        addToNode(rootNode, "Root", 10);
//        addToNode(rootNode.nodeAtIndex(0), "First", 10);
//        addToNode(rootNode.nodeAtIndex(1), "Second", 10);
//        addToNode(rootNode.nodeAtIndex(3), "Thriii", 10);
//        addToNode(rootNode.nodeAtIndex(0).nodeAtIndex(0), "Children", 10);

        // enter t-p
        // move carme

        this.minecraft = Minecraft.getInstance();
        this.cameraEntity = new CameraEntity(minecraft.player);

        Vector3f pos = blockEntity.getRenderOrigin();
        lastCameraRot = new Vector3f();
        lastCameraPos = new Vector3f(pos);
        applyCameraChanges();
    }

    private void addToNode(TreeNode node, String prefix, int count) {
        for (int i = 0; i < count; ++i) {
            node.add(new TreeNode(new NSString(prefix + " - " + i)));
        }
    }

    public Minecraft minecraft;
    public CameraEntity cameraEntity;

    @Override
    public void init() {
        super.init();
        cameraEntity.connect();
    }

    @Override
    public void deinit() {
        super.deinit();
        cameraEntity.disconnect();
    }

    @Override
    public void screenWillResize(CGSize size) {
        setFrame(new CGRect(0, 0, size.width, size.height));
    }


    public CGPoint startMousePos = CGPoint.ZERO;

    public Vector3f oldCameraRot = new Vector3f();
    public Vector3f oldCameraPos = new Vector3f();

    public Vector3f lastCameraRot = new Vector3f();
    public Vector3f lastCameraPos = new Vector3f();

    public boolean moveMode = false;
    public boolean rotationMode = false;

    @Override
    public void mouseDown(UIEvent event) {
        super.mouseDown(event);
        rotationMode = event.type() != UIEvent.Type.MOUSE_RIGHT_DOWN;
        if (KeyboardManagerImpl.hasSpaceDown()) {
            rotationMode = !rotationMode;
        }
        moveMode = !rotationMode;
        startMousePos = event.locationInWindow();
        oldCameraRot = lastCameraRot.copy();
        oldCameraPos = lastCameraPos.copy();
        // .
//        raycast(event);
    }

    @Override
    public void mouseDragged(UIEvent event) {
        super.mouseDragged(event);
        CGRect window = bounds();
        CGPoint mousePos = event.locationInWindow();
        if (rotationMode) {
            float dx = (float) (mousePos.y - startMousePos.y) / (float) window.height;
            float dy = (float) (mousePos.x - startMousePos.x) / (float) window.width;
            float rx = oldCameraRot.getX() + dx * 360;
            float ry = oldCameraRot.getY() + dy * 360;
            lastCameraRot.set(rx, ry, 0);
            ModLog.debug("{}/{} => {}", mousePos, window, lastCameraRot);
        }
        if (moveMode) {
            float x = oldCameraPos.getX();
            float y = oldCameraPos.getY();
            float z = oldCameraPos.getZ();

            float deltaX = -((mousePos.x - startMousePos.x)) / (window.width / 2f);
            float deltaY = ((mousePos.y - startMousePos.y)) / (window.height / 2f);
            float deltaZ = minecraft.options.getCameraNear();

            OpenNearPlane plane = cameraEntity.getNearPlane();

            Vector3f d1 = plane.at(0, 0, -deltaZ);
            Vector3f d2 = plane.at(deltaX, deltaY, deltaZ);

            float finalX = x + d1.getX() + d2.getX();
            float finalY = y + d1.getY() + d2.getY();
            float finalZ = z + d1.getZ() + d2.getZ();

            lastCameraPos.set(finalX, finalY, finalZ);
            ModLog.debug("{}/{}/({} {}) ", mousePos, window, deltaX, deltaY);
        }
        applyCameraChanges();
    }

    @Override
    public void mouseWheel(UIEvent event) {
        super.mouseWheel(event);
        double delta = event.delta();
        if (delta < 0) {
            blockEntity.scale *= 0.95f;
        } else if (delta > 0) {
            blockEntity.scale /= 0.95f;
        }
        cachedTree = null;

        ModLog.debug("{}", blockEntity.scale);
    }

    @Override
    public void mouseMoved(UIEvent event) {
        super.mouseMoved(event);
        raycast(event);
    }

    @Override
    public boolean shouldRenderBackground() {
        return false;
    }

    private void raycast(UIEvent event) {
        CGRect window = bounds();
        CGPoint mousePos = event.locationInWindow();

        float deltaX = ((mousePos.x - window.width / 2f)) / (window.width / 2f);
        float deltaY = -((mousePos.y - window.height / 2f)) / (window.height / 2f);
        float deltaZ = minecraft.options.getCameraNear();

        OpenNearPlane plane = cameraEntity.getNearPlane();

        Vector3f d1 = plane.at(0, 0, -deltaZ);
        Vector3f d2 = plane.at(deltaX, deltaY, deltaZ);

        Vector3f hit = lastCameraPos.adding(d1).adding(d2);
        Vector3f origin = lastCameraPos.adding(d1);
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

        ModLog.debug("{}/{}/({} {}) ", mousePos, window, deltaX, deltaY);
    }

    private Collection<Node> cachedTree;

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

        poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
        poseStack.scale(entity.scale, entity.scale, entity.scale);
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
        cameraEntity.setXRot(lastCameraRot.getX());
        cameraEntity.setYRot(lastCameraRot.getY());
        cameraEntity.setPos(lastCameraPos.getX(), lastCameraPos.getY(), lastCameraPos.getZ());
        cameraEntity.setOldPosAndRot();
        // .
        blockEntity.carmeOffset = lastCameraPos;
        blockEntity.carmeRot = lastCameraRot;
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
