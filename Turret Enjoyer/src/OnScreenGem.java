import java.awt.*;
import java.awt.image.BufferedImage;

import static java.lang.Math.*;

public class OnScreenGem {
    private int type, x, y, imageId;
    private final double offset;

    public OnScreenGem(int type, int x, int y) {
        this.type = type; // 4 types
        this.x = x;
        this.y = y;
        imageId = (int) (round(random() * 2)); //3 images each
        offset = Math.random();
    }

    public BufferedImage getImage() {
        BufferedImage image;
        try {
            image = Main.scaledImages.get(((type) * 3) + 10 + imageId);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("type: " + type);
            System.out.println("imgid: " + imageId);
            System.out.println("trying to access " + (((type) * 3) + 10 + imageId) + " out of " + Main.scaledImages.size());
            e.printStackTrace();
            throw new RuntimeException();
        }
        return image;
    }

    public int getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getImageId() {
        return imageId;
    }

    public double getOffset() {
        return offset;
    }
}
