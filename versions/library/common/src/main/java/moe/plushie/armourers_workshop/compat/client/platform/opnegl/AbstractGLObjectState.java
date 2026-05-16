package moe.plushie.armourers_workshop.compat.client.platform.opnegl;

import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

@OnlyIn(Dist.CLIENT)
public class AbstractGLObjectState {

    public int vao = 0;
    public int vbo = 0;
    public int ibo = 0;
    public int fbo = 0;

    public int programId;

    public void push() {
        programId = GL20.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        vbo = GL20.glGetInteger(GL20.GL_ARRAY_BUFFER_BINDING);
        ibo = GL20.glGetInteger(GL20.GL_ELEMENT_ARRAY_BUFFER_BINDING);
        //fbo = GL30.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        if (AbstractGLCapabilities.GL_ARB_vertex_array_object) {
            vao = GL30.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        }
    }

    public void pop() {
        reset();
        if (GL20.glIsBuffer(vbo)) {
            GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, vbo);
        }
        if (GL20.glIsBuffer(ibo)) {
            GL20.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, ibo);
        }
        if (AbstractGLCapabilities.GL_ARB_vertex_array_object) {
            if (GL30.glIsVertexArray(vao)) {
                GL30.glBindVertexArray(vao);
            }
        }
        //if (GL30.glIsFramebuffer(fbo)) {
        //    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
        //}
//        int error = GL30.glGetError();
//        if (error != 0) {
//            ModLog.info("gl error {}", error);
//        }
        vao = -1;
        vbo = -1;
        ibo = -1;
        programId = -1;
    }

    public void reset() {
        if (AbstractGLCapabilities.GL_ARB_vertex_array_object) {
            GL30.glBindVertexArray(0);
        }
        GL20.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
        GL20.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}
