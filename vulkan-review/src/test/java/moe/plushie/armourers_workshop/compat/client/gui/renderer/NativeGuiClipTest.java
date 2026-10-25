package moe.plushie.armourers_workshop.compat.client.gui.renderer;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import org.joml.Matrix4f;
public class NativeGuiClipTest {
    static class Output implements VertexConsumer {
        ArrayList<float[]> vertices = new ArrayList<>();
        float[] current;
        public VertexConsumer addVertex(float x,float y,float z) { current=new float[]{x,y,z,0,0,0}; vertices.add(current); return this; }
        public VertexConsumer setColor(int r,int g,int b,int a) { current[5]=a; return this; }
        public VertexConsumer setColor(int argb) { return setColor(0,0,0,argb>>>24); }
        public VertexConsumer setUv(float u,float v) { current[3]=u;current[4]=v;return this; }
        public VertexConsumer setUv1(int x,int y) {return this;}
        public VertexConsumer setUv2(int x,int y) {return this;}
        public VertexConsumer setNormal(float x,float y,float z) {return this;}
        public VertexConsumer setLineWidth(float w) {return this;}
    }
    static void check(boolean ok,String what) { if(!ok)throw new AssertionError(what); }
    static void quad(VertexConsumer c,float offset) {
        c.addVertex(offset,0,0).setUv(0,0).setColor(255,255,255,0);
        c.addVertex(offset+10,0,0).setUv(1,0).setColor(255,255,255,200);
        c.addVertex(offset+10,10,0).setUv(1,1).setColor(255,255,255,200);
        c.addVertex(offset,10,0).setUv(0,1).setColor(255,255,255,0);
    }
    public static void main(String[] args) {
        NativeGuiClip.Snapshot saved;
        try(var scope=NativeGuiClip.begin(new Matrix4f().translate(100,0,0))) {
            NativeGuiClip.push(2,2,6,6,0); saved=NativeGuiClip.snapshot();NativeGuiClip.pop();
        }
        Output o=new Output();var c=saved.wrap(o,PrimitiveTopology.QUADS);quad(c,100);c.finish();
        check(!o.vertices.isEmpty(),"deferred snapshot retained");
        for(float[] v:o.vertices) {
            check(v[0]>=102-0.001 && v[0]<=108.001 && v[1]>=1.999 && v[1]<=8.001,"PiP rectangle bounds");
            check(Math.abs(v[3]-(v[0]-100)/10)<0.001,"UV interpolation");
            check(Math.abs(v[5]-v[3]*200)<=1,"alpha interpolation");
        }
        try(var scope=NativeGuiClip.begin(new Matrix4f())) {
            NativeGuiClip.push(0,0,10,10,3); NativeGuiClip.push(0,0,5,10,0);
            o=new Output();c=NativeGuiClip.snapshot().wrap(o,PrimitiveTopology.QUADS);quad(c,0);c.finish();
            check(!o.vertices.isEmpty(),"rounded mask draws");
            for(float[] v:o.vertices) { check(v[0]<=5.001,"nested mask"); if(v[0]<3 && v[1]<3)check(Math.hypot(v[0]-3,v[1]-3)<=3.001,"round corner"); }
            NativeGuiClip.pop();NativeGuiClip.pop(); NativeGuiClip.push(0,0,0,10,0);
            o=new Output();c=NativeGuiClip.snapshot().wrap(o,PrimitiveTopology.QUADS);quad(c,0);c.finish();check(o.vertices.isEmpty(),"empty mask");
        }
        try {
            var ctor = net.minecraft.client.renderer.rendertype.RenderType.class.getDeclaredConstructor(String.class, net.minecraft.client.renderer.rendertype.RenderSetup.class);
            ctor.setAccessible(true);
            var pipeline = com.mojang.blaze3d.pipeline.RenderPipeline.builder()
                .withLocation(net.minecraft.resources.Identifier.fromNamespaceAndPath("armourers_workshop", "test/strip"))
                .withVertexShader("core/gui").withFragmentShader("core/gui")
                .withVertexBinding(0, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR)
                .withPrimitiveTopology(PrimitiveTopology.TRIANGLE_STRIP)
                .withColorTargetState(new com.mojang.blaze3d.pipeline.ColorTargetState(com.mojang.blaze3d.pipeline.BlendFunction.TRANSLUCENT))
                .build();
            var original = ctor.newInstance("clip-test", net.minecraft.client.renderer.rendertype.RenderSetup.builder(pipeline).createRenderSetup());
            var split = NativeGuiRenderTypes.independent(original);
            check(split.primitiveTopology()==PrimitiveTopology.TRIANGLES,"connected topology converted");
            check(split.pipeline().getColorTargetState().equals(pipeline.getColorTargetState()),"alpha blend state retained");
            check(split.format()==original.format(),"vertex format retained");
        } catch (ReflectiveOperationException e) { throw new AssertionError(e); }
        System.out.println("PASS: deferred rectangle, PiP transform, UV, alpha, rounded/nested/empty masks");
    }
}
