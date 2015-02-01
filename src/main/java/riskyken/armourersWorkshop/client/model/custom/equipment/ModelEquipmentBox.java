package riskyken.armourersWorkshop.client.model.custom.equipment;

import java.util.BitSet;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import riskyken.mcWrapper.client.IRenderBuffer;

public class ModelEquipmentBox extends ModelBox {
    
    private PositionTextureVertex[] vertexPositions;
    private CustomTexturedQuad[] quadList;
    public final float posX1;
    public final float posY1;
    public final float posZ1;
    public final float posX2;
    public final float posY2;
    public final float posZ2;
    
    public ModelEquipmentBox(ModelRenderer p_i1171_1_, int p_i1171_2_, int p_i1171_3_, float p_i1171_4_, float p_i1171_5_,
            float p_i1171_6_, int p_i1171_7_, int p_i1171_8_, int p_i1171_9_,
            float p_i1171_10_) {
        super(p_i1171_1_, p_i1171_2_, p_i1171_3_, p_i1171_4_, p_i1171_5_, p_i1171_6_,
                p_i1171_7_, p_i1171_8_, p_i1171_9_, p_i1171_10_);
        
        this.posX1 = p_i1171_4_;
        this.posY1 = p_i1171_5_;
        this.posZ1 = p_i1171_6_;
        this.posX2 = p_i1171_4_ + (float)p_i1171_7_;
        this.posY2 = p_i1171_5_ + (float)p_i1171_8_;
        this.posZ2 = p_i1171_6_ + (float)p_i1171_9_;
        this.vertexPositions = new PositionTextureVertex[8];
        this.quadList = new CustomTexturedQuad[6];
        float f4 = p_i1171_4_ + (float)p_i1171_7_;
        float f5 = p_i1171_5_ + (float)p_i1171_8_;
        float f6 = p_i1171_6_ + (float)p_i1171_9_;
        p_i1171_4_ -= p_i1171_10_;
        p_i1171_5_ -= p_i1171_10_;
        p_i1171_6_ -= p_i1171_10_;
        f4 += p_i1171_10_;
        f5 += p_i1171_10_;
        f6 += p_i1171_10_;

        if (p_i1171_1_.mirror)
        {
            float f7 = f4;
            f4 = p_i1171_4_;
            p_i1171_4_ = f7;
        }

        PositionTextureVertex positiontexturevertex7 = new PositionTextureVertex(p_i1171_4_, p_i1171_5_, p_i1171_6_, 0.0F, 0.0F);
        PositionTextureVertex positiontexturevertex = new PositionTextureVertex(f4, p_i1171_5_, p_i1171_6_, 0.0F, 8.0F);
        PositionTextureVertex positiontexturevertex1 = new PositionTextureVertex(f4, f5, p_i1171_6_, 8.0F, 8.0F);
        PositionTextureVertex positiontexturevertex2 = new PositionTextureVertex(p_i1171_4_, f5, p_i1171_6_, 8.0F, 0.0F);
        PositionTextureVertex positiontexturevertex3 = new PositionTextureVertex(p_i1171_4_, p_i1171_5_, f6, 0.0F, 0.0F);
        PositionTextureVertex positiontexturevertex4 = new PositionTextureVertex(f4, p_i1171_5_, f6, 0.0F, 8.0F);
        PositionTextureVertex positiontexturevertex5 = new PositionTextureVertex(f4, f5, f6, 8.0F, 8.0F);
        PositionTextureVertex positiontexturevertex6 = new PositionTextureVertex(p_i1171_4_, f5, f6, 8.0F, 0.0F);
        this.vertexPositions[0] = positiontexturevertex7;
        this.vertexPositions[1] = positiontexturevertex;
        this.vertexPositions[2] = positiontexturevertex1;
        this.vertexPositions[3] = positiontexturevertex2;
        this.vertexPositions[4] = positiontexturevertex3;
        this.vertexPositions[5] = positiontexturevertex4;
        this.vertexPositions[6] = positiontexturevertex5;
        this.vertexPositions[7] = positiontexturevertex6;
        this.quadList[0] = new CustomTexturedQuad(new PositionTextureVertex[] {positiontexturevertex4, positiontexturevertex, positiontexturevertex1, positiontexturevertex5}, p_i1171_2_ + p_i1171_9_ + p_i1171_7_, p_i1171_3_ + p_i1171_9_, p_i1171_2_ + p_i1171_9_ + p_i1171_7_ + p_i1171_9_, p_i1171_3_ + p_i1171_9_ + p_i1171_8_, p_i1171_1_.textureWidth, p_i1171_1_.textureHeight);
        this.quadList[1] = new CustomTexturedQuad(new PositionTextureVertex[] {positiontexturevertex7, positiontexturevertex3, positiontexturevertex6, positiontexturevertex2}, p_i1171_2_, p_i1171_3_ + p_i1171_9_, p_i1171_2_ + p_i1171_9_, p_i1171_3_ + p_i1171_9_ + p_i1171_8_, p_i1171_1_.textureWidth, p_i1171_1_.textureHeight);
        this.quadList[2] = new CustomTexturedQuad(new PositionTextureVertex[] {positiontexturevertex4, positiontexturevertex3, positiontexturevertex7, positiontexturevertex}, p_i1171_2_ + p_i1171_9_, p_i1171_3_, p_i1171_2_ + p_i1171_9_ + p_i1171_7_, p_i1171_3_ + p_i1171_9_, p_i1171_1_.textureWidth, p_i1171_1_.textureHeight);
        this.quadList[3] = new CustomTexturedQuad(new PositionTextureVertex[] {positiontexturevertex1, positiontexturevertex2, positiontexturevertex6, positiontexturevertex5}, p_i1171_2_ + p_i1171_9_ + p_i1171_7_, p_i1171_3_ + p_i1171_9_, p_i1171_2_ + p_i1171_9_ + p_i1171_7_ + p_i1171_7_, p_i1171_3_, p_i1171_1_.textureWidth, p_i1171_1_.textureHeight);
        this.quadList[4] = new CustomTexturedQuad(new PositionTextureVertex[] {positiontexturevertex, positiontexturevertex7, positiontexturevertex2, positiontexturevertex1}, p_i1171_2_ + p_i1171_9_, p_i1171_3_ + p_i1171_9_, p_i1171_2_ + p_i1171_9_ + p_i1171_7_, p_i1171_3_ + p_i1171_9_ + p_i1171_8_, p_i1171_1_.textureWidth, p_i1171_1_.textureHeight);
        this.quadList[5] = new CustomTexturedQuad(new PositionTextureVertex[] {positiontexturevertex3, positiontexturevertex4, positiontexturevertex5, positiontexturevertex6}, p_i1171_2_ + p_i1171_9_ + p_i1171_7_ + p_i1171_9_, p_i1171_3_ + p_i1171_9_, p_i1171_2_ + p_i1171_9_ + p_i1171_7_ + p_i1171_9_ + p_i1171_7_, p_i1171_3_ + p_i1171_9_ + p_i1171_8_, p_i1171_1_.textureWidth, p_i1171_1_.textureHeight);

        if (p_i1171_1_.mirror)
        {
            for (int j1 = 0; j1 < this.quadList.length; ++j1)
            {
                this.quadList[j1].flipFace();
            }
        }
    }

    public void render(IRenderBuffer renderBuffer, float scale, BitSet faceFlags, int x, int y, int z, float r, float g, float b, float a) {
        //0 = west
        //1 = east
        //2 = up
        //3 = down
        //4 = north
        //5 = south
        int size = this.quadList.length;
        for (int i = 0; i < size; ++i) {
            if (!faceFlags.get(i)) {
                this.quadList[i].draw(renderBuffer, scale, x, y, z, r, g, b, a);
            }
            
        }
    }
}
