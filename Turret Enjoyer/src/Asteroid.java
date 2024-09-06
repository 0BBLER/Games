import static java.lang.Math.random;
import static java.lang.Math.round;

public class Asteroid {

    private int x;
    private int y;
    private int size;
    private int imageId;

    public Asteroid(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
        imageId = (int) round(random()*4);
    }

    public int getImageId() {
        return imageId;
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
