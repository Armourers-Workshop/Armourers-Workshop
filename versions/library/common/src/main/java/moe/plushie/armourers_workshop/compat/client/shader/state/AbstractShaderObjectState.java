package moe.plushie.armourers_workshop.compat.client.shader.state;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class AbstractShaderObjectState {

    public int vao = 0;
    public int vbo = 0;
    public int ibo = 0;
    public int fbo = 0;

    public int programId;

    public void push() {
        programId = GL20.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        vao = GL30.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        vbo = GL30.glGetInteger(GL30.GL_ARRAY_BUFFER_BINDING);
        ibo = GL30.glGetInteger(GL30.GL_ELEMENT_ARRAY_BUFFER_BINDING);
        //fbo = GL30.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
    }

    public void pop() {
        if (GL30.glIsBuffer(vbo)) {
            GL30.glBindVertexArray(0);
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo);
        }
        if (GL30.glIsBuffer(ibo)) {
            GL30.glBindVertexArray(0);
            GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, ibo);
        }
        if (GL30.glIsVertexArray(vao)) {
            GL30.glBindVertexArray(vao);
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
}
