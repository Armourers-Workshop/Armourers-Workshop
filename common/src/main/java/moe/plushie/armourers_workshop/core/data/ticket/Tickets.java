package moe.plushie.armourers_workshop.core.data.ticket;

public class Tickets {

    public static final Ticket TEST = Ticket.limited(500, 500f);
    public static final Ticket TOOLTIP = Ticket.limited(500, 100f);
    public static final Ticket RENDERER = Ticket.limited(500, 200f);
    public static final Ticket INVENTORY = Ticket.limited(500, 300f);

    public static final Ticket PRELOAD = Ticket.normal(-500f);

    public static void invalidateAll() {
        TEST.invalidate();
        TOOLTIP.invalidate();
        RENDERER.invalidate();
        INVENTORY.invalidate();
        PRELOAD.invalidate();
    }
}
