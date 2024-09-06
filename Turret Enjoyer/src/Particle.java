import java.awt.*;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Particle {
    private double x, y, speed;
    private final double dir;
    private int time, size;
    private final Color color;

    public Particle(double x, double y, double dir, double speed, int size, Color color) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.size = size;
        this.color = color;
        this.speed = speed;
        time = 0;
    }

    public Color getColor() {
        return color;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void tick() {
        x += sin(Math.toRadians(dir)) * speed;
        y += cos(Math.toRadians(dir)) * speed;
        speed *= 0.98;
        time++;
    }

    public int getSize() {
        return size;
    }

    public int getTime() {
        return time;
    }
}
