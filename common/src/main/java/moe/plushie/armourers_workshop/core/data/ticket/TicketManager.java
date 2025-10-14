package moe.plushie.armourers_workshop.core.data.ticket;

public class TicketManager {

    public static final TicketHolder TEST = new TicketHolder("Test", 500, 500f);
    public static final TicketHolder TOOLTIP = new TicketHolder("Tooltip", 500, 100f);
    public static final TicketHolder RENDERER = new TicketHolder("Renderer", 500, 200f);
    public static final TicketHolder INVENTORY = new TicketHolder("Inventory", 500, 300f);

    public static final TicketHolder PRELOAD = new TicketHolder("Preload", -500f);

    public static void invalidateAll() {
        TEST.invalidate();
        TOOLTIP.invalidate();
        RENDERER.invalidate();
        INVENTORY.invalidate();
        PRELOAD.invalidate();
    }
}
