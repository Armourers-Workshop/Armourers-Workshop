package riskyken.armourersWorkshop.api.client.render.npc;

import net.minecraft.entity.Entity;
import riskyken.armourersWorkshop.api.common.skin.IEntityEquipment;

public interface ISkinnableEntityRenderer {

    public void render(Entity entity, IEntityEquipment entityEquipment);
}
