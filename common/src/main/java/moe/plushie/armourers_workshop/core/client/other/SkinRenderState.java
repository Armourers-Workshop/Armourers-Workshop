package moe.plushie.armourers_workshop.core.client.other;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class SkinRenderState {

    private int vao = 0;
    private int vbo = 0;
    private int ibo = 0;
    private int fbo = 0;

    private int programId;

    public void save() {
        programId = GL20.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        vao = GL30.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        vbo = GL30.glGetInteger(GL30.GL_ARRAY_BUFFER_BINDING);
        ibo = GL30.glGetInteger(GL30.GL_ELEMENT_ARRAY_BUFFER_BINDING);
        //fbo = GL30.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
    }

    public void load() {
        if (GL30.glIsVertexArray(vao)) {
            GL30.glBindVertexArray(vao);
        }
        if (GL30.glIsBuffer(vbo)) {
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo);
        }
        if (GL30.glIsBuffer(ibo)) {
            GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, ibo);
        }
        //if (GL30.glIsFramebuffer(fbo)) {
        //    GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
        //}
//        int error = GL30.glGetError();
//        if (error != 0) {
//            ModLog.info("gl error {}", error);
//        }
    }

    public int lastProgramId() {
        return programId;
    }
}
