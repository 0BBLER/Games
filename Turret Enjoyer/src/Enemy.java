import java.awt.*;

public class Enemy {
    private int id, type, fireTime;
    private double health;
    int maxHealth;
    private int movePhase;
    private double speedFactor, direction, speed, x, y, debuffFactor;
    private double debuffTime = 0;
    private double dropChance;
    private Color particleCol;


    public Enemy(int id, int type, int x, int y) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
        this.movePhase = 1;
        this.speedFactor = 1;
        this.direction = Math.toRadians(90);
        this.debuffFactor = 1;

        if (type == EnemyTypes.BASIC) {
            this.maxHealth = EnemyTypes.Basic.maxHealth;
            this.health = maxHealth;
            this.speed = EnemyTypes.Basic.speed;
            this.particleCol = EnemyTypes.Basic.particleCol;
            dropChance = EnemyTypes.Basic.gemDropChance;
        }

        if (type == EnemyTypes.SPEED) {
            this.maxHealth = EnemyTypes.Speed.maxHealth;
            this.health = maxHealth;
            this.speed = EnemyTypes.Speed.speed;
            this.particleCol = EnemyTypes.Speed.particleCol;
            dropChance = EnemyTypes.Speed.gemDropChance;

        }

        if (type == EnemyTypes.ARMOURED) {
            this.maxHealth = EnemyTypes.Armoured.maxHealth;
            this.health = maxHealth;
            this.speed = EnemyTypes.Armoured.speed;
            this.particleCol = EnemyTypes.Armoured.particleCol;
            dropChance = EnemyTypes.Armoured.gemDropChance;
        }

        if (type == EnemyTypes.MOTHERSHIP) {
            this.maxHealth = EnemyTypes.Mothership.maxHealth;
            this.health = maxHealth;
            this.speed = EnemyTypes.Mothership.speed;
            this.particleCol = EnemyTypes.Mothership.particleCol;
            dropChance = EnemyTypes.Mothership.gemDropChance;
        }

        if (type == EnemyTypes.STEALTH) {
            this.maxHealth = EnemyTypes.Stealth.maxHealth;
            this.health = maxHealth;
            this.speed = EnemyTypes.Stealth.speed;
            this.particleCol = EnemyTypes.Stealth.particleCol;
            dropChance = EnemyTypes.Stealth.gemDropChance;
        }
    }

    public Color getParticleCol() {
        return particleCol;
    }

    public double getDropChance() {
        return dropChance;
    }

    public int getFireTime() {
        return fireTime;
    }

    public void setFireTime(int fireTime) {
        this.fireTime = fireTime;
    }

    public void changeFireTime(int change) {
        fireTime += change;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public double getSpeed() {
        return speed;
    }

    public double getDirection() {
        return direction;
    }

    public double getDebuffFactor() {
        return debuffFactor;
    }

    public void setDirection(double direction) {
        this.direction = direction;
    }

    public void setDebuffFactor(double debuffFactor) {
        this.debuffFactor = debuffFactor;
    }

    public double getDebuffTime() {
        return debuffTime;
    }

    public void setDebuffTime(double debuffTime) {
        this.debuffTime = debuffTime;
    }

    public void changeDebuffTime(double change) {
        this.debuffTime += change;
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public double getX() {
        return x;
    }

    public void moveX(double x) {
        this.x += x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void moveY(double y) {
        this.y += y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setMovePhase(int phase) {
        this.movePhase = phase;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public void changeHealth(double health) {
        this.health += health;
        if (this.health > maxHealth) {
            this.health = maxHealth;
        }
    }

    public int getMovePhase() {
        return movePhase;
    }

    public double getSpeedFactor() {
        return speedFactor;
    }

    public void setSpeedFactor(double speedFactor) {
        this.speedFactor = speedFactor;
    }
}
