public class Star {
    private final int x, y, size;
    private final double offset;

    public Star(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
        offset = Math.random();
    }

    public double getOffset() {
        return offset;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSize() {
        return size;
    }
}
