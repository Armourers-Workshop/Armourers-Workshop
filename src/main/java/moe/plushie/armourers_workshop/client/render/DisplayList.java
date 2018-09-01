package moe.plushie.armourers_workshop.client.render;

import java.util.concurrent.atomic.AtomicInteger;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GLAllocation;

public class DisplayList {
    
    private static AtomicInteger LIST_COUNT = new AtomicInteger(0);
    private int list; 
    private boolean compiled = false;
    
    public static int getListCount() {
        return LIST_COUNT.get();
    }
    
    public void begin() {
        if (compiled) {
            cleanup();
        }
        list = GLAllocation.generateDisplayLists(1);
        LIST_COUNT.incrementAndGet();
        GL11.glNewList(list, GL11.GL_COMPILE);
    }
    
    public boolean isCompiled() {
        return compiled;
    }
    
    public void end() {
        GL11.glEndList();
        compiled = true;
    }
    
    public void render() {
        if (compiled) {
            GL11.glCallList(list);
        }
    }
    
    public void cleanup() {
        GLAllocation.deleteDisplayLists(list);
        LIST_COUNT.decrementAndGet();
        compiled = false;
    }
    
    @Override
    protected void finalize() throws Throwable {
        if (compiled) {
            cleanup();
        }
        super.finalize();
    }
}
