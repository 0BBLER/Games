import java.awt.*;

public class Projectile {
    private int x, y, x2, y2, id, type, age;
    private boolean isFire = false, isDebuff = false, isSplash = false;
    private boolean laserDidDamage = false;
    private boolean alreadyHit = false;
    private double splashRadius;
    private double causeDebuffTime = 0;
    private int speed = 15;
    private double direction;
    private int damage;

    public Projectile(int type, int id, int x, int y, int x2, int y2, double direction, double power) {
        this.type = type;
        this.id = id;
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
        this.direction = direction;

        if (type == ProjectileTypes.BASIC) {
            damage = ProjectileTypes.Basic.damage;
        }
        if (type == ProjectileTypes.LASER) {
            damage = ProjectileTypes.Laser.damage;
        }
        if (type == ProjectileTypes.MISSILE) {
            damage = ProjectileTypes.Missile.damage;
        }


        damage *= power / 10;
        age = 0;
    }

    public Color getColor() {
        if(isFire) return new Color(192, 100, 0);
        if(isDebuff) return new Color(50, 140, 6);
        if(isSplash) return new Color(189, 160, 8);
        return new Color(135, 163, 183);
    }

    public boolean alreadyHit() {
        return alreadyHit;
    }

    public void setAlreadyHit(boolean alreadyHit) {
        this.alreadyHit = alreadyHit;
    }

    public boolean isSplash() {
        return isSplash;
    }

    public void setSplash(boolean splash) {
        isSplash = splash;
    }

    public double getSplashRadius() {
        return splashRadius;
    }

    public void setSplashRadius(double splashRadius) {
        this.splashRadius = splashRadius;
    }

    public void setFire(boolean fire) {
        isFire = fire;
    }

    public boolean isFire() {
        return isFire;
    }

    public boolean isLaserDidDamage() {
        return laserDidDamage;
    }

    public void setLaserDidDamage(boolean laserDidDamage) {
        this.laserDidDamage = laserDidDamage;
    }

    public boolean isDebuff() {
        return isDebuff;
    }

    public void setDebuff(boolean debuff) {
        isDebuff = debuff;
    }

    public double getCauseDebuffTime() {
        return causeDebuffTime;
    }

    public void setCauseDebuffTime(double causeDebuffTime) {
        this.causeDebuffTime = causeDebuffTime;
    }

    public int getAge() {
        return age;
    }

    public void changeAge(int change) {
        this.age += change;
    }

    public int getType() {
        return type;
    }

    public int getDamage() {
        return damage;
    }

    public int getId() {
        return id;
    }

    public double getDirection() {
        return direction;
    }

    public int getSpeed() {
        return speed;
    }

    public int getX() {
        return x;
    }

    public void moveX(int x) {
        this.x += x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void moveY(int y) {
        this.y += y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }
}
