package riskyken.armourersWorkshop.client.model.bake;

import java.util.ArrayList;

import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.util.Vec3;
import riskyken.armourersWorkshop.proxies.ClientProxy;
import riskyken.plushieWrapper.client.IRenderBuffer;

public class CustomTexturedQuad extends TexturedQuad {

    public CustomTexturedQuad(PositionTextureVertex[] p_i1153_1_,
            int p_i1153_2_, int p_i1153_3_, int p_i1153_4_, int p_i1153_5_,
            float p_i1153_6_, float p_i1153_7_) {
        super(p_i1153_1_, p_i1153_2_, p_i1153_3_, p_i1153_4_, p_i1153_5_, p_i1153_6_,
                p_i1153_7_);
    }
    
    public void draw(IRenderBuffer renderBuffer, float scale, int x, int y, int z, byte r, byte g, byte b, byte a) {
        Vec3 vec3 = this.vertexPositions[1].vector3D.subtract(this.vertexPositions[0].vector3D);
        Vec3 vec31 = this.vertexPositions[1].vector3D.subtract(this.vertexPositions[2].vector3D);
        Vec3 vec32 = vec31.crossProduct(vec3).normalize();
        renderBuffer.setNormal((float)vec32.xCoord, (float)vec32.yCoord, (float)vec32.zCoord);
        renderBuffer.setColourRGBA_B(r, g, b, a);
        for (int i = 0; i < 4; ++i) {
            PositionTextureVertex positiontexturevertex = this.vertexPositions[i];
            if (ClientProxy.useSafeTextureRender()) {
                renderBuffer.addVertexWithUV((double)((float)(positiontexturevertex.vector3D.xCoord + x) * scale), (double)((float)(positiontexturevertex.vector3D.yCoord + y) * scale), (double)((float)(positiontexturevertex.vector3D.zCoord + z) * scale), (double)positiontexturevertex.texturePositionX, (double)positiontexturevertex.texturePositionY);
            } else {
                renderBuffer.addVertex((double)((float)(positiontexturevertex.vector3D.xCoord + x) * scale), (double)((float)(positiontexturevertex.vector3D.yCoord + y) * scale), (double)((float)(positiontexturevertex.vector3D.zCoord + z) * scale));
            }
            
        }
    }

    public void buildDisplayListArray(ArrayList<ColouredVertexWithUV> vertexList, float scale, int x, int y, int z, byte r, byte g, byte b, byte a, byte paintType) {
        Vec3 vec3 = this.vertexPositions[1].vector3D.subtract(this.vertexPositions[0].vector3D);
        Vec3 vec31 = this.vertexPositions[1].vector3D.subtract(this.vertexPositions[2].vector3D);
        Vec3 vec32 = vec31.crossProduct(vec3).normalize();
        for (int i = 0; i < 4; ++i) {
            PositionTextureVertex positiontexturevertex = this.vertexPositions[i];
            ColouredVertexWithUV cVer = new ColouredVertexWithUV(
                    (double)((float)(positiontexturevertex.vector3D.xCoord + x) * scale), (double)((float)(positiontexturevertex.vector3D.yCoord + y) * scale), (double)((float)(positiontexturevertex.vector3D.zCoord + z) * scale),
                    positiontexturevertex.texturePositionX, positiontexturevertex.texturePositionY,
                    r, g, b, a,
                    (float)vec32.xCoord, (float)vec32.yCoord, (float)vec32.zCoord, paintType);
            vertexList.add(cVer);
        }
    }

}
