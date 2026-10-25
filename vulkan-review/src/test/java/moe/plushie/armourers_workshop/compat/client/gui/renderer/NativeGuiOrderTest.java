package moe.plushie.armourers_workshop.compat.client.gui.renderer;

import com.apple.library.coregraphics.CGGraphicsElement;
import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.*;
import com.mojang.blaze3d.vertex.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import moe.plushie.armourers_workshop.api.client.*;
import moe.plushie.armourers_workshop.compat.client.math.AbstractPoseStack;
import moe.plushie.armourers_workshop.compat.client.renderer.vertex.AbstractBufferSource;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.feature.*;
import net.minecraft.client.renderer.feature.phase.FeatureRenderPhase;
import net.minecraft.client.renderer.rendertype.*;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.loading.FMLLoader;
import org.joml.Matrix4f;
import sun.misc.Unsafe;

/** Actual AW batching + native phase execution, with only GPU feature drawing replaced by a recorder. */
public class NativeGuiOrderTest {
    private static final Unsafe UNSAFE;
    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            UNSAFE = (Unsafe) field.get(null);
        } catch (ReflectiveOperationException e) { throw new ExceptionInInitializerError(e); }
    }

    public static void main(String[] args) throws Exception {
        // SharedConstants needs only the loader's production flag here, not a running game.
        Field currentField = FMLLoader.class.getDeclaredField("current");
        currentField.setAccessible(true);
        @SuppressWarnings("unchecked")
        AtomicReference<FMLLoader> current = (AtomicReference<FMLLoader>) currentField.get(null);
        FMLLoader loader = (FMLLoader) UNSAFE.allocateInstance(FMLLoader.class);
        set(loader, "production", true);
        if (!current.compareAndSet(null, loader)) throw new AssertionError("Standalone test must not replace a running loader");
        try {
            testPainterBarrier();
            System.out.println("PASS: actual AW flush executes background before solid skin, translucent skin and foreground; immediate entity boundary preserved");
        } finally { current.compareAndSet(loader, null); }
    }

    private static void testPainterBarrier() throws Exception {
        SubmitNodeStorage storage = new SubmitNodeStorage();
        List<String> draws = new ArrayList<>();
        int[] executions = {0};
        Runnable execute = () -> {
            executions[0]++;
            executeNativePhases(storage, draws);
        };
        var pose = new AbstractPoseStack(new PoseStack());
        var dispatcher = new AbstractComposeGuiGraphicsRenderer.OffscreenDispatcherImpl(pose, AbstractBufferSource.wrap(storage), storage, execute);
        IRenderType background = type("background", true);
        IRenderType solid = type("solid-skin", false);
        IRenderType translucent = type("translucent-skin", true);
        IRenderType foreground = type("foreground", true);
        try (var scope = NativeGuiClip.begin(new Matrix4f())) {
            dispatcher.submit(quad(background));
            dispatcher.flush();
            check(draws.equals(List.of("background")), "background must execute, not just enqueue, before an immediate entity: " + draws);
            // The installed entity helper renders its own storage immediately at this boundary.
            draws.add("immediate-entity");
            dispatcher.submit(quad(solid));
            dispatcher.flush();
            dispatcher.submit(quad(translucent));
            dispatcher.flush();
            dispatcher.submit(quad(foreground));
            dispatcher.flush();
            dispatcher.flush();
        }
        check(draws.equals(List.of("background", "immediate-entity", "solid-skin", "translucent-skin", "foreground")), "painter order: " + draws);
        check(executions[0] == 4, "empty flush must not start another native frame");
        check(storage.getSubmitsPerOrder().values().stream().allMatch(c -> c.allPhases().stream().allMatch(FeatureRenderPhase::isEmpty)), "PiP parent must not draw segments again");
    }

    private static CGGraphicsElement quad(IRenderType type) {
        return (pose, source) -> emit(source.getBuffer(type));
    }

    private static void emit(IVertexConsumer out) {
        for (float[] xy : new float[][]{{0,0}, {10,0}, {10,10}, {0,10}})
            out.vertex(xy[0], xy[1], 0).color(255, 255, 255, 239).endVertex();
    }

    private static IRenderType type(String name, boolean blend) throws Exception {
        var pipeline = RenderPipeline.builder().withLocation(Identifier.fromNamespaceAndPath("armourers_workshop", name))
            .withVertexShader("core/gui").withFragmentShader("core/gui")
            .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR).withPrimitiveTopology(PrimitiveTopology.QUADS)
            .withColorTargetState(new ColorTargetState(blend ? Optional.of(BlendFunction.TRANSLUCENT) : Optional.empty(), GpuFormat.RGBA8_UNORM, 15)).build();
        var constructor = RenderType.class.getDeclaredConstructor(String.class, RenderSetup.class);
        constructor.setAccessible(true);
        RenderType type = constructor.newInstance(name, RenderSetup.builder(pipeline).createRenderSetup());
        return (IRenderType) Proxy.newProxyInstance(IRenderType.class.getClassLoader(), new Class[]{IRenderType.class},
            (p, method, args) -> method.getName().equals("get") ? type : null);
    }

    private static void executeNativePhases(SubmitNodeStorage storage, List<String> draws) {
        try {
            FeatureRenderDispatcher dispatcher = (FeatureRenderDispatcher) UNSAFE.allocateInstance(FeatureRenderDispatcher.class);
            FeatureRendererMap renderers = new FeatureRendererMap();
            renderers.put(CustomFeatureRenderer.TYPE, new FeatureRenderer<CustomFeatureRenderer.Submit>() {
                public void prepareGroup(FeatureFrameContext c, List<CustomFeatureRenderer.Submit> submits, boolean ordered) { }
                public void executeGroup(FeatureFrameContext c, int group, List<CustomFeatureRenderer.Submit> submits, boolean ordered) {
                    for (var submit : submits) {
                        var output = new NativeGuiClipTest.Output();
                        submit.customGeometryRenderer().render(submit.pose(), output);
                        check(output.vertices.size() == 4, "actual deferred vertices replay once");
                        draws.add(submit.renderType().pipeline().getLocation().getPath());
                    }
                }
            });
            set(dispatcher, "featureRenderers", renderers);
            FeatureRenderDispatcher.PreparedFrame frame = dispatcher.new PreparedFrame();
            set(frame, "context", new FeatureFrameContext(null, null, null, null, null, null, null, null));
            set(frame, "submitNodeStorage", storage);
            Class<?> grouperClass = Class.forName("net.minecraft.client.renderer.feature.FeatureRenderDispatcher$PhaseSubmitGrouper");
            Constructor<?> grouper = grouperClass.getDeclaredConstructors()[0];
            grouper.setAccessible(true);
            storage.drainPhases(phase -> {
                try { phase.sortInto((FeatureRenderPhase.Output) grouper.newInstance(frame, phase)); }
                catch (ReflectiveOperationException e) { throw new RuntimeException(e); }
            });
            frame.executeSolid();
            frame.executeTranslucent();
        } catch (ReflectiveOperationException e) { throw new RuntimeException(e); }
    }

    private static void set(Object target, String name, Object value) throws ReflectiveOperationException {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static void check(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
}
