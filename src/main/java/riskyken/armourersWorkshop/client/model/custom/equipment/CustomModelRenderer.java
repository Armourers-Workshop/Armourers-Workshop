package riskyken.armourersWorkshop.client.model.custom.equipment;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.Tessellator;

public class CustomModelRenderer extends ModelRenderer {

    public CustomModelRenderer(ModelBase p_i1174_1_, int p_i1174_2_,
            int p_i1174_3_) {
        super(p_i1174_1_, p_i1174_2_, p_i1174_3_);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void render(float p_78785_1_) {
        Tessellator tessellator = Tessellator.instance;

        for (int i = 0; i < this.cubeList.size(); ++i)
        {
            ((ModelBox)this.cubeList.get(i)).render(tessellator, p_78785_1_);
        }

    }
    

}
