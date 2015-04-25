package riskyken.armourersWorkshop.common.skin.cubes;

public class CubeColour implements ICubeColour {

    private byte[] r;
    private byte[] g;
    private byte[] b;
    
    public CubeColour() {
        r = new byte[6];
        g = new byte[6];
        b = new byte[6];
        for (int i = 0; i < 6; i++) {
            r[i] = (byte)255;
            g[i] = (byte)255;
            b[i] = (byte)255;
        }
    }
    
    public CubeColour(int colour) {
        r = new byte[6];
        g = new byte[6];
        b = new byte[6];
        for (int i = 0; i < 6; i++) {
            r[i] = (byte) (colour >> 16 & 0xff);
            g[i] = (byte) (colour >> 8 & 0xff);
            b[i] = (byte) (colour & 0xff);
        }
    }
    
    @Override
    public byte getRed(int side) {
        return r[side];
    }

    @Override
    public byte getGreen(int side) {
        return g[side];
    }

    @Override
    public byte getBlue(int side) {
        return b[side];
    }

    @Override
    public byte[] getRed() {
        return r;
    }

    @Override
    public byte[] getGreen() {
        return g;
    }

    @Override
    public byte[] getBlue() {
        return b;
    }
    
    @Override
    public void setColour(int colour, int side) {
        r[side] = (byte) (colour >> 16 & 0xff);
        g[side] = (byte) (colour >> 8 & 0xff);
        b[side] = (byte) (colour & 0xff);
    }
    
    @Deprecated
    @Override
    public void setColour(int colour) {
        for (int i = 0; i < 6; i++) {
            r[i] = (byte) (colour >> 16 & 0xff);
            g[i] = (byte) (colour >> 8 & 0xff);
            b[i] = (byte) (colour & 0xff);
        }
    }

    @Override
    public void setRed(byte red, int side) {
        r[side] = red;
    }

    @Override
    public void setGreen(byte green, int side) {
        g[side] = green;
    }

    @Override
    public void setBlue(byte blue, int side) {
        b[side] = blue;
    }

    @Override
    public void setRed(byte[] red) {
        r = red;
    }

    @Override
    public void setGreen(byte[] green) {
        g = green;
    }

    @Override
    public void setBlue(byte[] blue) {
        b = blue;
    }
}
