package riskyken.armourers_workshop.client.render;

public class MannequinFakePlayer /*extends AbstractClientPlayer*/ {
    /*
    public MannequinFakePlayer(World world, GameProfile gameProfile) {
        super(world, gameProfile);
    }
    
    @Override
    public String getCommandSenderName() {
        return "[Mannequin]";
    }
    
    @Override
    public String getDisplayName() {
        return "[Mannequin]";
    }

    @Override
    public void onUpdate() {
        //super.onUpdate();
        this.field_71091_bM = this.field_71094_bP;
        this.field_71096_bN = this.field_71095_bQ;
        this.field_71097_bO = this.field_71085_bR;
        double d3 = this.posX - this.field_71094_bP;
        double d0 = this.posY - this.field_71095_bQ;
        double d1 = this.posZ - this.field_71085_bR;
        double d2 = 10.0D;

        if (d3 > d2)
        {
            this.field_71091_bM = this.field_71094_bP = this.posX;
        }

        if (d1 > d2)
        {
            this.field_71097_bO = this.field_71085_bR = this.posZ;
        }

        if (d0 > d2)
        {
            this.field_71096_bN = this.field_71095_bQ = this.posY;
        }

        if (d3 < -d2)
        {
            this.field_71091_bM = this.field_71094_bP = this.posX;
        }

        if (d1 < -d2)
        {
            this.field_71097_bO = this.field_71085_bR = this.posZ;
        }

        if (d0 < -d2)
        {
            this.field_71096_bN = this.field_71095_bQ = this.posY;
        }
        this.field_71094_bP += d3 * 0.25D;
        this.field_71085_bR += d1 * 0.25D;
        this.field_71095_bQ += d0 * 0.25D;
    }
    
    @Override public boolean canCommandSenderUseCommand(int i, String s){ return false; }
    @Override public ChunkCoordinates getPlayerCoordinates()
    {
        return new ChunkCoordinates(0,0,0);
    }

    @Override public void addChatComponentMessage(ITextComponent chatmessagecomponent){}
    @Override public void addStat(StatBase par1StatBase, int par2){}
    @Override public void openGui(Object mod, int modGuiId, World world, int x, int y, int z){}
    @Override public boolean isEntityInvulnerable(){ return true; }
    @Override public boolean canAttackPlayer(EntityPlayer player){ return false; }
    @Override public void onDeath(DamageSource source){ return; }
    @Override public void travelToDimension(int dim){ return; }
    @Override public void addChatMessage(IChatComponent p_145747_1_) {}
    @Override
    public IIcon getItemIcon(ItemStack stack, int pass) {
        if (stack.getItem().requiresMultipleRenderPasses()) {
            return stack.getItem().getIcon(stack, pass);
        }
        return stack.getIconIndex();
    }*/
}
