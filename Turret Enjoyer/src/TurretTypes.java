import java.awt.*;

public class TurretTypes {

    public static final int BASIC = 0;
    public static final int FIRE = 1;
    public static final int LASER = 2;
    public static final int DEBUFF = 3;
    public static final int SPLASH = 4;

    /*
    public static Object getBasic() {
        return new Basic();
    }
    public static Object getFire() {
        return new Fire();
    }
    public static Object getLaser() {
        return new Laser();
    }
    public static Object getDebuff() {
        return new Debuff();
    }

    public static Object getSplash() {
        return new Splash();
    }

     */

    public static class Basic {
        public static final String name = "Basic Turret";
        public static final double chargeTime = 10;
        public static final double power = 10;
        public static final double cost = 25;
        public static final double range = 8;
        public static final double energyCost = 20;
        public static final double efficiency = 1;
        public static final Color color = new Color(0, 18, 72);
    }

    public static class Fire {
        public static final String name = "Fire Turret";
        public static final double chargeTime = 20;
        public static final double power = 10;
        public static final double cost = 100;
        public static final double range = 8;
        public static final double energyCost = 20;
        public static final double efficiency = 1;
        public static final Color color = new Color(159, 68, 25);
    }

    public static class Laser {
        public static final String name = "Laser Turret";
        public static final double chargeTime = 60;
        public static final double power = 40;
        public static final double cost = 100;
        public static final double range = 8;
        public static final double energyCost = 100;
        public static final double efficiency = 1;
        public static final Color color = new Color(155, 15, 15);
    }

    public static class Debuff {
        public static final String name = "Debuff Turret";
        public static final double chargeTime = 40;
        public static final double power = 0;
        public static final double cost = 100;
        public static final double range = 8;
        public static final double energyCost = 50;
        public static final double efficiency = 1;
        public static final Color color = new Color(38, 86, 16);
    }

    public static class Splash {
        public static final String name = "Splash Turret";
        public static final double chargeTime = 100;
        public static final double power = 15;
        public static final double cost = 100;
        public static final double range = 8;
        public static final double energyCost = 100;
        public static final double efficiency = 1;
        public static final Color color = new Color(138, 124, 31);
    }


    public static Color getTurretColor(int type) {
        if (type == BASIC) return Basic.color;
        if (type == FIRE) return Fire.color;
        if (type == LASER) return Laser.color;
        if (type == DEBUFF) return Debuff.color;
        if (type == SPLASH) return Splash.color;

        return null;
    }

    public static double getTurretPower(int type) {
        if (type == BASIC) return Basic.power;
        if (type == FIRE) return Fire.power;
        if (type == LASER) return Laser.power;
        if (type == DEBUFF) return Debuff.power;
        if (type == SPLASH) return Splash.power;

        return 0;
    }

    public static double getTurretRange(int type) {
        if (type == BASIC) return Basic.range;
        if (type == FIRE) return Fire.range;
        if (type == LASER) return Laser.range;
        if (type == DEBUFF) return Debuff.range;
        if (type == SPLASH) return Splash.range;

        return 0;
    }

    public static double getTurretEff(int type) {
        if (type == BASIC) return Basic.efficiency;
        if (type == FIRE) return Fire.efficiency;
        if (type == LASER) return Laser.efficiency;
        if (type == DEBUFF) return Debuff.efficiency;
        if (type == SPLASH) return Splash.efficiency;

        return 0;
    }

    public static double getTurretChargeTime(int type) {
        if (type == BASIC) return Basic.chargeTime;
        if (type == FIRE) return Fire.chargeTime;
        if (type == LASER) return Laser.chargeTime;
        if (type == DEBUFF) return Debuff.chargeTime;
        if (type == SPLASH) return Splash.chargeTime;

        return 0;
    }

    public static double getTurretCost(int type) {
        if (type == BASIC) return Basic.cost;
        if (type == FIRE) return Fire.cost;
        if (type == LASER) return Laser.cost;
        if (type == DEBUFF) return Debuff.cost;
        if (type == SPLASH) return Splash.cost;

        return 0;
    }
}
