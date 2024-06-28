package moe.plushie.armourers_workshop.core.client.other;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.GL30;

@Environment(EnvType.CLIENT)
public class VertexArrayObject {

    private final int id;

    public VertexArrayObject() {
        this.id = GL30.glGenVertexArrays();
    }

    public void bind() {
        GL30.glBindVertexArray(id);
    }

    public static void unbind() {
        GL30.glBindVertexArray(0);
    }

    public void close() {
        GL30.glDeleteVertexArrays(id);
    }
}
