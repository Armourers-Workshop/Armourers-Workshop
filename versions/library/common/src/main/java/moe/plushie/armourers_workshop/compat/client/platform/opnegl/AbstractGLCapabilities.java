package moe.plushie.armourers_workshop.compat.client.platform.opnegl;

import org.lwjgl.opengl.GL;

public class AbstractGLCapabilities {

    public static boolean GL_ARB_vertex_array_object;

    static {
        var caps = GL.createCapabilities();
        GL_ARB_vertex_array_object = caps.OpenGL30 || caps.GL_ARB_vertex_array_object;
    }
}
