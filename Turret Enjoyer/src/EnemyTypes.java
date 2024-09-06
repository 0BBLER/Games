import java.awt.*;

public class EnemyTypes {
    public static final int BASIC = 0;
    public static final int ARMOURED = 1;
    public static final int SPEED = 2;
    public static final int STEALTH = 3;
    public static final int MOTHERSHIP = 4;

    public static final int FORMATION_JUSTBASIC = 0;
    public static final int FORMATION_JUSTARMOURED = 1;
    public static final int FORMATION_JUSTSPEED = 2;
    public static final int FORMATION_JUSTSTEALTH = 3;
    public static final int FORMATION_ARMOUREDMOTHERSHIP = 4;


    static class Basic {
        public static final int type = BASIC;
        public static final double speed = 1.5;
        public static final int maxHealth = 200;
        public static final int value = 15;
        public static final double gemDropChance = 0.5;
        public static final Color particleCol = new Color(40, 69, 141);
    }
    static class Speed {
        public static final int type = SPEED;
        public static final double speed = 4;
        public static final int maxHealth = 130;
        public static final int value = 25;
        public static final double gemDropChance = 0.3;
        public static final Color particleCol = new Color(183, 158, 54);
    }
    static class Armoured {
        public static final int type = ARMOURED;
        public static final double speed = 1;
        public static final int maxHealth = 200;
        public static final int value = 25;
        public static final double gemDropChance = 0.4;
        public static final Color particleCol = new Color(102, 104, 107);
    }
    static class Mothership {
        public static final int type = MOTHERSHIP;
        public static final double speed = 1;
        public static final int maxHealth = 2500;
        public static final int value = 100;
        public static final double gemDropChance = 0.5;
        public static final Color particleCol = new Color(75, 20, 20);
    }
    static class Stealth {
        public static final int type = STEALTH;
        public static final double speed = 2;
        public static final int maxHealth = 150;
        public static final int value = 25;
        public static final double viewChance = 0.005;
        public static final double gemDropChance = 0.2;
        public static final Color particleCol = new Color(40, 40, 40, 163);
    }
    public static int getValue(int type) {
        if (type == BASIC) {
            return Basic.value;
        }
        if (type == SPEED) {
            return Speed.value;
        }
        if (type == ARMOURED) {
            return Armoured.value;
        }
        if (type == MOTHERSHIP) {
            return Mothership.value;
        }
        if (type == STEALTH) {
            return Stealth.value;
        }
        return 0;
    }
}
