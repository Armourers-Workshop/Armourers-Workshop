package moe.plushie.armourers_workshop.compat.client.gui.renderer;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.*;
import com.mojang.blaze3d.vertex.*;
import java.lang.reflect.*;
import java.util.*;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.compat.client.renderer.vertex.AbstractBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.*;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;

public class NativeGuiSubmissionTest {
    public static void main(String[] args) throws Exception {
        var pipeline=RenderPipeline.builder().withLocation(Identifier.fromNamespaceAndPath("armourers_workshop","test/gui"))
            .withVertexShader("core/gui").withFragmentShader("core/gui")
            .withVertexBinding(0,DefaultVertexFormat.POSITION_COLOR).withPrimitiveTopology(PrimitiveTopology.QUADS)
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT)).build();
        var ctor=RenderType.class.getDeclaredConstructor(String.class,RenderSetup.class);ctor.setAccessible(true);
        RenderType type=ctor.newInstance("gui-test",RenderSetup.builder(pipeline).createRenderSetup());
        IRenderType rt=(IRenderType)Proxy.newProxyInstance(IRenderType.class.getClassLoader(),new Class[]{IRenderType.class},(p,m,a)->m.getName().equals("get")?type:null);
        List<Object> callbacks=new ArrayList<>();
        SubmitNodeCollector collector=(SubmitNodeCollector)Proxy.newProxyInstance(SubmitNodeCollector.class.getClassLoader(),new Class[]{SubmitNodeCollector.class},(p,m,a)->{
            if(m.getName().equals("order"))return p;
            if(m.getName().equals("submitCustomGeometry"))callbacks.add(a[a.length-1]);
            return null;
        });
        try(var scope=NativeGuiClip.begin(new Matrix4f())) {
            NativeGuiClip.push(2,2,6,6,0);
            var source=AbstractBufferSource.wrap(collector);
            var out=source.getBuffer(rt);
            for(float[] xy:new float[][]{{0,0},{10,0},{10,10},{0,10}})
                out.vertex(xy[0],xy[1],0).color(100,150,200,128).endVertex();
            source.endBatch();
            NativeGuiClip.pop();
        }
        NativeGuiClipTest.Output output=new NativeGuiClipTest.Output();
        for(Object callback:callbacks) {
            Method render=Arrays.stream(callback.getClass().getInterfaces()[0].getMethods()).filter(m->m.getParameterCount()==2).findFirst().orElseThrow();
            render.invoke(callback,new PoseStack().last(),output);
        }
        NativeGuiClipTest.check(!output.vertices.isEmpty(),"background survives deferred batch");
        for(float[] v:output.vertices) {
            NativeGuiClipTest.check(v[5]==128,"background alpha remains 128");
            NativeGuiClipTest.check(v[0]>=1.999&&v[0]<=8.001&&v[1]>=1.999&&v[1]<=8.001,"actual batch retains mask after pop");
        }
        System.out.println("PASS: actual buffer batch preserves background alpha and deferred mask");
    }
}
