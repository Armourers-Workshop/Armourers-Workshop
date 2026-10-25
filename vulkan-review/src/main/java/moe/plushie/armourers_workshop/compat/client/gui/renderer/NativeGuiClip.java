package moe.plushie.armourers_workshop.compat.client.gui.renderer;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;

/** Captures GUI clip shapes with deferred geometry, independent of framebuffer/backend state. */
public final class NativeGuiClip {
    private static final ThreadLocal<State> CURRENT = ThreadLocal.withInitial(State::new);
    private NativeGuiClip() { }

    private static final class State {
        final ArrayDeque<Shape> shapes = new ArrayDeque<>();
        final Matrix4f inverse;
        int drawOrder;
        boolean active;
        State() { inverse = new Matrix4f(); }
        State(Matrix4fc logicalToRender) { inverse = new Matrix4f(logicalToRender).invert(); active = true; }
    }

    public static Scope begin(Matrix4fc logicalToRender) {
        State previous = CURRENT.get();
        CURRENT.set(new State(logicalToRender));
        return new Scope(previous);
    }

    public static final class Scope implements AutoCloseable {
        private final State previous;
        private Scope(State previous) { this.previous = previous; }
        @Override public void close() { CURRENT.set(previous); }
    }

    public static net.minecraft.client.renderer.OrderedSubmitNodeCollector ordered(net.minecraft.client.renderer.SubmitNodeCollector collector) {
        State state = CURRENT.get();
        return state.active ? collector.order(state.drawOrder++) : collector;
    }

    public static void push(float x, float y, float width, float height, float radius) {
        CURRENT.get().shapes.addLast(new Shape(x, y, Math.max(0, width), Math.max(0, height), radius));
    }

    public static void pop() {
        State state = CURRENT.get();
        if (!state.shapes.isEmpty()) state.shapes.removeLast();
    }

    public static Snapshot snapshot() {
        State state = CURRENT.get();
        return new Snapshot(List.copyOf(state.shapes), new Matrix4f(state.inverse));
    }

    public record Point(float x, float y) { }

    public static final class Shape {
        final float width, height;
        final List<Point> polygon;
        Shape(float x, float y, float width, float height, float radius) {
            this.width = width;
            this.height = height;
            float r = Math.max(0, Math.min(radius, Math.min(width, height) * 0.5f));
            ArrayList<Point> points = new ArrayList<>();
            if (r == 0) {
                points.add(new Point(x, y)); points.add(new Point(x + width, y));
                points.add(new Point(x + width, y + height)); points.add(new Point(x, y + height));
            } else {
                // Bound each chord's error below 0.05 GUI pixels, including scaled/PiP windows.
                int steps = Math.max(4, (int) Math.ceil(Math.PI / 2 / Math.acos(Math.max(-1, 1 - 0.05 / r))));
                arc(points, x + width - r, y + r, r, -Math.PI / 2, steps);
                arc(points, x + width - r, y + height - r, r, 0, steps);
                arc(points, x + r, y + height - r, r, Math.PI / 2, steps);
                arc(points, x + r, y + r, r, Math.PI, steps);
            }
            polygon = List.copyOf(points);
        }
        private static void arc(List<Point> points, float x, float y, float r, double start, int steps) {
            for (int i = 0; i <= steps; i++) {
                double a = start + Math.PI / 2 * i / steps;
                points.add(new Point(x + r * (float) Math.cos(a), y + r * (float) Math.sin(a)));
            }
        }
    }

    public record Snapshot(List<Shape> shapes, Matrix4f inverse) {
        public ClippedConsumer wrap(VertexConsumer output, PrimitiveTopology topology) {
            if (shapes.isEmpty()) return null;
            return new ClippedConsumer(output, topology, this);
        }
    }

    private static final class Vertex {
        float x, y, z, lx, ly, u, v, nx, ny, nz, lineWidth = 1;
        int red = 255, green = 255, blue = 255, alpha = 255, overlayX, overlayY, lightX, lightY;
        int attributes;
        Vertex interpolate(Vertex b, float t) {
            Vertex c = new Vertex();
            c.x = mix(x,b.x,t); c.y = mix(y,b.y,t); c.z = mix(z,b.z,t);
            c.lx = mix(lx,b.lx,t); c.ly = mix(ly,b.ly,t);
            c.u = mix(u,b.u,t); c.v = mix(v,b.v,t);
            c.red = mix(red,b.red,t); c.green = mix(green,b.green,t);
            c.blue = mix(blue,b.blue,t); c.alpha = mix(alpha,b.alpha,t);
            c.nx = mix(nx,b.nx,t); c.ny = mix(ny,b.ny,t); c.nz = mix(nz,b.nz,t);
            c.overlayX = mix(overlayX,b.overlayX,t); c.overlayY = mix(overlayY,b.overlayY,t);
            c.lightX = mix(lightX,b.lightX,t); c.lightY = mix(lightY,b.lightY,t);
            c.lineWidth = mix(lineWidth,b.lineWidth,t); c.attributes = attributes | b.attributes;
            return c;
        }
        private static float mix(float a,float b,float t) { return a + (b-a)*t; }
        private static int mix(int a,int b,float t) { return Math.round(a + (b-a)*t); }
        void emit(VertexConsumer target) {
            target.addVertex(x,y,z);
            if ((attributes & 1) != 0) target.setColor(red,green,blue,alpha);
            if ((attributes & 2) != 0) target.setUv(u,v);
            if ((attributes & 4) != 0) target.setUv1(overlayX,overlayY);
            if ((attributes & 8) != 0) target.setUv2(lightX,lightY);
            if ((attributes & 16) != 0) target.setNormal(nx,ny,nz);
            if ((attributes & 32) != 0) target.setLineWidth(lineWidth);
        }
    }

    /** Clips complete primitives before emitting any vertices into the Vulkan-backed renderer. */
    public static final class ClippedConsumer implements VertexConsumer {
        private final VertexConsumer output;
        private final PrimitiveTopology topology;
        private final Snapshot snapshot;
        private final ArrayList<Vertex> primitive = new ArrayList<>(4);
        private Vertex current;
        private Vertex stripPrevious, stripFirst;
        private int stripCount;


        ClippedConsumer(VertexConsumer output, PrimitiveTopology topology, Snapshot snapshot) {
            this.output=output; this.topology=topology; this.snapshot=snapshot;
        }
        @Override public VertexConsumer addVertex(float x,float y,float z) {
            commit(); current=new Vertex(); current.x=x; current.y=y; current.z=z;
            Vector3f logical = snapshot.inverse.transformPosition(new Vector3f(x,y,z));
            current.lx=logical.x; current.ly=logical.y;
            return this;
        }
        @Override public VertexConsumer setColor(int r,int g,int b,int a) {
            current.red=r; current.green=g; current.blue=b; current.alpha=a; current.attributes |= 1; return this;
        }
        @Override public VertexConsumer setColor(int argb) {
            return setColor((argb>>>16)&255,(argb>>>8)&255,argb&255,(argb>>>24)&255);
        }
        @Override public VertexConsumer setUv(float u,float v) { current.u=u; current.v=v; current.attributes|=2; return this; }
        @Override public VertexConsumer setUv1(int x,int y) { current.overlayX=x; current.overlayY=y; current.attributes|=4; return this; }
        @Override public VertexConsumer setUv2(int x,int y) { current.lightX=x; current.lightY=y; current.attributes|=8; return this; }
        @Override public VertexConsumer setNormal(float x,float y,float z) { current.nx=x; current.ny=y; current.nz=z; current.attributes|=16; return this; }
        @Override public VertexConsumer setLineWidth(float width) { current.lineWidth=width; current.attributes|=32; return this; }
        public void finish() { commit(); primitive.clear(); }
        private void commit() {
            if (current==null) return;
            Vertex vertex=current; current=null;
            if (topology==PrimitiveTopology.POINTS) {
                if (inside(vertex)) vertex.emit(output);
                return;
            }
            if (topology==PrimitiveTopology.DEBUG_LINE_STRIP) {
                // The strip is clipped as independent segments; outside vertices are not submitted.
                // Connected line strips are handled by the native geometry adapter below.
                if (stripPrevious!=null) emitLine(stripPrevious,vertex);
                stripPrevious=vertex;
                return;
            }
            primitive.add(vertex);
            int group = topology==PrimitiveTopology.QUADS || topology==PrimitiveTopology.LINES ? 4
                : topology==PrimitiveTopology.DEBUG_LINES ? 2 : 3;
            if (topology==PrimitiveTopology.TRIANGLE_STRIP || topology==PrimitiveTopology.TRIANGLE_FAN) {
                stripCount++;
                if (stripCount<3) return;
                if (stripFirst==null) stripFirst=primitive.get(0);
                int n=primitive.size();
                Vertex a=topology==PrimitiveTopology.TRIANGLE_FAN ? stripFirst : primitive.get(n-3);
                Vertex b=primitive.get(n-2), c=primitive.get(n-1);
                if (topology==PrimitiveTopology.TRIANGLE_STRIP && (stripCount&1)==0) triangle(b,a,c);
                else triangle(a,b,c);
                if (primitive.size()>3) primitive.remove(0);
                return;
            }
            if (primitive.size()==group) {
                if (group==2) emitLine(primitive.get(0),primitive.get(1));
                else if (topology==PrimitiveTopology.LINES) {
                    emitExpandedLine(primitive.get(0),primitive.get(1),primitive.get(2),primitive.get(3));
                } else {
                    triangle(primitive.get(0),primitive.get(1),primitive.get(2));
                    if (group==4) triangle(primitive.get(2),primitive.get(3),primitive.get(0));
                }
                primitive.clear();
            }
        }
        private boolean inside(Vertex v) {
            for (Shape s:snapshot.shapes) {
                if (s.width<=0 || s.height<=0) return false;
                for(int i=0;i<s.polygon.size();i++)
                    if (distance(s.polygon.get(i),s.polygon.get((i+1)%s.polygon.size()),v)<-1e-5f) return false;
            }
            return true;
        }
        private void triangle(Vertex a,Vertex b,Vertex c) {
            List<Vertex> vertices=new ArrayList<>(List.of(a,b,c));
            for (Shape shape:snapshot.shapes) {
                if (shape.width<=0 || shape.height<=0) return;
                for(int i=0;i<shape.polygon.size() && !vertices.isEmpty();i++) {
                    Point p=shape.polygon.get(i), q=shape.polygon.get((i+1)%shape.polygon.size());
                    ArrayList<Vertex> clipped=new ArrayList<>();
                    Vertex prev=vertices.get(vertices.size()-1); float dPrev=distance(p,q,prev);
                    for(Vertex next:vertices) {
                        float dNext=distance(p,q,next);
                        boolean prevIn=dPrev>=-1e-5f, nextIn=dNext>=-1e-5f;
                        if(prevIn!=nextIn) clipped.add(prev.interpolate(next,Math.max(0,Math.min(1,dPrev/(dPrev-dNext)))));
                        if(nextIn) clipped.add(next);
                        prev=next; dPrev=dNext;
                    }
                    vertices=clipped;
                }
            }
            for(int i=1;i+1<vertices.size();i++) emitTriangle(vertices.get(0),vertices.get(i),vertices.get(i+1));
        }
        private void emitTriangle(Vertex a,Vertex b,Vertex c) {
            a.emit(output); b.emit(output); c.emit(output);
            if(topology==PrimitiveTopology.QUADS) c.emit(output); // second indexed triangle is degenerate
        }
        private void emitExpandedLine(Vertex a,Vertex a2,Vertex b,Vertex b2) {
            Vertex[] clipped=clipLine(a,b);
            if(clipped!=null) { clipped[0].emit(output); clipped[0].emit(output); clipped[1].emit(output); clipped[1].emit(output); }
        }
        private void emitLine(Vertex a,Vertex b) {
            Vertex[] clipped=clipLine(a,b);
            if(clipped!=null) { clipped[0].emit(output); clipped[1].emit(output); }
        }
        private Vertex[] clipLine(Vertex a,Vertex b) {
            float from=0,to=1;
            for(Shape s:snapshot.shapes) {
                if(s.width<=0 || s.height<=0) return null;
                for(int i=0;i<s.polygon.size();i++) {
                    Point p=s.polygon.get(i),q=s.polygon.get((i+1)%s.polygon.size());
                    float da=distance(p,q,a),db=distance(p,q,b);
                    if(da<0 && db<0) return null;
                    if(da<0) from=Math.max(from,da/(da-db));
                    if(db<0) to=Math.min(to,da/(da-db));
                }
            }
            return from<=to ? new Vertex[]{a.interpolate(b,from),a.interpolate(b,to)} : null;
        }
        private static float distance(Point a,Point b,Vertex p) {
            return (b.x-a.x)*(p.ly-a.y)-(b.y-a.y)*(p.lx-a.x);
        }
    }
}
