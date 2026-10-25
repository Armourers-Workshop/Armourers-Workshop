package moe.plushie.armourers_workshop.compat.client.gui.renderer;

public class NativeGuiLayerTest {
    public static void main(String[] args) {
        NativeGuiLayers<int[]> layers = new NativeGuiLayers<>();
        int[] background = layers.target("background", () -> new int[1]);
        background[0] = 0x80204060;
        int[] foreground = layers.target("foreground", () -> new int[1]);
        foreground[0] = 0x00000000;
        // Replay prepare(background), prepare(foreground), blit(background), blit(foreground).
        NativeGuiClipTest.check(background != foreground, "deferred layer texture views must be distinct");
        NativeGuiClipTest.check(background[0] == 0x80204060, "transparent foreground clear must not erase background");
        NativeGuiClipTest.check(layers.target("background", () -> new int[1]) == background, "reuse same layer across frames");
        int[] freed = {0}; layers.close(t -> freed[0]++);
        NativeGuiClipTest.check(freed[0] == 2, "release every target");
        System.out.println("PASS: deferred background survives transparent foreground clear");
    }
}
