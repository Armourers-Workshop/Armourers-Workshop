package riskyken.armourers_workshop.client.model.skin;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import riskyken.armourers_workshop.api.common.skin.data.ISkinDye;
import riskyken.armourers_workshop.common.skin.data.Skin;

@SideOnly(Side.CLIENT)
public interface IEquipmentModel {
    
    public void render(Entity entity, Skin armourData, float limb1, float limb2, float limb3, float headY, float headX);
    
    public void render(Entity entity, ModelBiped modelBiped, Skin armourData, boolean showSkinPaint, ISkinDye skinDye, byte[] extraColour, boolean itemRender, double distance, boolean doLodLoading);
}
