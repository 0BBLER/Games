public class SplashVisual {
    public static final int viewTime = 20;
    private final int x, y;
    private int age;
    private final double radius;

    public SplashVisual(int x, int y, double radius, int age) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void changeAge(int change) {
        this.age += change;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getRadius() {
        return radius;
    }
}
