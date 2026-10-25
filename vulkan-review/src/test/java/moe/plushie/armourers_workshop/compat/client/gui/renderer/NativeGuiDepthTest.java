package moe.plushie.armourers_workshop.compat.client.gui.renderer;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.*;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.resources.Identifier;
public class NativeGuiDepthTest {
    public static void main(String[] args) throws Exception {
        var builderClass=Class.forName("moe.plushie.armourers_workshop.compat.client.renderer.rendertype.AbstractRenderTypeBuilder$Provider$Builder");
        var constructor=builderClass.getDeclaredConstructor(VertexFormat.class,PrimitiveTopology.class,RenderPipeline.class);constructor.setAccessible(true);
        var pipeline=RenderPipeline.builder().withLocation(Identifier.fromNamespaceAndPath("armourers_workshop","test/reverse_depth"))
            .withVertexShader("core/gui").withFragmentShader("core/gui").withVertexBinding(0,DefaultVertexFormat.POSITION_COLOR)
            .withPrimitiveTopology(PrimitiveTopology.QUADS).withColorTargetState(ColorTargetState.DEFAULT)
            .withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL,true)).build();
        Object builder=constructor.newInstance(DefaultVertexFormat.POSITION_COLOR,PrimitiveTopology.QUADS,pipeline);
        var depth=builderClass.getDeclaredField("depthTest");depth.setAccessible(true);
        NativeGuiClipTest.check(depth.get(builder)==CompareOp.GREATER_THAN_OR_EQUAL,"reversed-Z background must pass depth cleared to zero; actual="+depth.get(builder));
        Object gui=constructor.newInstance(DefaultVertexFormat.POSITION_COLOR,PrimitiveTopology.QUADS,net.minecraft.client.renderer.RenderPipelines.GUI);
        var write=builderClass.getDeclaredField("writeDepth");write.setAccessible(true);
        var nativeDepth=net.minecraft.client.renderer.RenderPipelines.GUI.getDepthStencilState();
        var expected=nativeDepth==null ? CompareOp.ALWAYS_PASS : nativeDepth.depthTest();
        NativeGuiClipTest.check(depth.get(gui)==expected,"actual native GUI depth state preserved");
        NativeGuiClipTest.check((boolean)write.get(gui)==(nativeDepth!=null&&nativeDepth.writeDepth()),"actual GUI depth write preserved");
        System.out.println("PASS: native reversed depth and GUI depth state retained");
    }
}
