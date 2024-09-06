import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Turret {

    private int id;
    private int type;
    private int x;
    private int y;
    private boolean active;
    private double chargeTime, power, cost, range, efficiency, energyCost;
    private double powerLevel, rangeLevel, rechargeLevel, effLevel;
    private double splashRadius = 0;
    private String name;
    private double charge = 0;
    private boolean toggledActiveThisClick = false;

    public Turret(int ID, int type, int x, int y) {
        this.id = ID;
        this.type = type;
        this.x = x;
        this.y = y;
        this.active = true;
        this.powerLevel = 0;
        this.rangeLevel = 0;
        this.rechargeLevel = 0;
        this.effLevel = 0;

        if (type == TurretTypes.BASIC) {
            this.chargeTime = TurretTypes.Basic.chargeTime;
            this.power = TurretTypes.Basic.power;
            this.cost = TurretTypes.Basic.cost;
            this.range = TurretTypes.Basic.range;
            this.name = TurretTypes.Basic.name;
            this.energyCost = TurretTypes.Basic.energyCost;
            this.efficiency = TurretTypes.Basic.efficiency;
        }

        if (type == TurretTypes.FIRE) {
            this.chargeTime = TurretTypes.Fire.chargeTime;
            this.power = TurretTypes.Fire.power;
            this.cost = TurretTypes.Fire.cost;
            this.range = TurretTypes.Fire.range;
            this.name = TurretTypes.Fire.name;
            this.energyCost = TurretTypes.Fire.energyCost;
            this.efficiency = TurretTypes.Fire.efficiency;
        }
        if (type == TurretTypes.LASER) {
            this.chargeTime = TurretTypes.Laser.chargeTime;
            this.power = TurretTypes.Laser.power;
            this.cost = TurretTypes.Laser.cost;
            this.range = TurretTypes.Laser.range;
            this.name = TurretTypes.Laser.name;
            this.energyCost = TurretTypes.Laser.energyCost;
            this.efficiency = TurretTypes.Laser.efficiency;
        }

        if (type == TurretTypes.DEBUFF) {
            this.chargeTime = TurretTypes.Debuff.chargeTime;
            this.power = TurretTypes.Debuff.power;
            this.cost = TurretTypes.Debuff.cost;
            this.range = TurretTypes.Debuff.range;
            this.name = TurretTypes.Debuff.name;
            this.energyCost = TurretTypes.Debuff.energyCost;
            this.efficiency = TurretTypes.Debuff.efficiency;
        }
        if (type == TurretTypes.SPLASH) {
            this.chargeTime = TurretTypes.Splash.chargeTime;
            this.power = TurretTypes.Splash.power;
            this.cost = TurretTypes.Splash.cost;
            this.range = TurretTypes.Splash.range;
            this.name = TurretTypes.Splash.name;
            this.energyCost = TurretTypes.Splash.energyCost;
            this.efficiency = TurretTypes.Splash.efficiency;
            this.splashRadius = 200;
        }

        this.charge = chargeTime;
    }

    public int getValue() {
        int powerVal = (int) (((20 * powerLevel / 2 * (powerLevel / 2 + 1)) / 2));
        int rechargeVal = (int) (((20 * rechargeLevel / 2 * (rechargeLevel / 2 + 1)) / 2));
        int rangeVal = (int) (((20 * rangeLevel / 2 * (rangeLevel / 2 + 1)) / 2));
        int effVal = (int) (((20 * effLevel / 2 * (effLevel / 2 + 1)) / 2));
        return (int) (powerVal + rechargeVal + rangeVal + effVal + cost);
    }

    public double getSplashRadius() {
        return splashRadius;
    }

    public void setSplashRadius(double splashRadius) {
        this.splashRadius = splashRadius;
    }

    public double getEnergyCost() {
        return energyCost;
    }

    public void setEnergyCost(double energyCost) {
        this.energyCost = energyCost;
    }

    public double getCharge() {
        return charge;
    }

    public void setCharge(double charge) {
        this.charge = charge;
        if (this.charge > chargeTime) this.charge = chargeTime;
        if (this.charge < 0) charge = chargeTime;
    }

    public void changeCharge(double change) {
        charge += change;
        if (charge > chargeTime) charge = chargeTime;
        if (charge < 0) charge = chargeTime;
    }

    public double getPowerLevel() {
        return powerLevel;
    }

    public double getRangeLevel() {
        return rangeLevel;
    }

    public double getRechargeLevel() {
        return rechargeLevel;
    }

    public double getEffLevel() {
        return effLevel;
    }

    public void upgradePower() {
        powerLevel += 2;
        power = TurretTypes.getTurretPower(this.type) * (1 + (powerLevel / 10f));
    }

    public void upgradeEff() {
        effLevel += 2;
        efficiency = TurretTypes.getTurretEff(this.type) * (1 + (effLevel / 10f));
    }

    public void upgradeRange() {
        rangeLevel += 2;
        range = TurretTypes.getTurretRange(this.type) * (1 + (rangeLevel / 10f));
    }

    public void upgradeChargeTime() {
        rechargeLevel += 2;
        chargeTime = TurretTypes.getTurretChargeTime(this.type) * (1 - rechargeLevel/20);
        /*
        System.out.println("turret charge time: " + TurretTypes.getTurretChargeTime(this.type));
        System.out.println("recharge level: " + rechargeLevel);
        System.out.println("recharge level / 3: " + (rechargeLevel / 3));
        System.out.println("charge time: " + chargeTime);
        System.out.println();

         */
    }


    public double getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(double efficiency) {
        this.efficiency = efficiency;
    }

    public String getName() {
        return name;
    }

    public double getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public boolean isActive() {
        return active;
    }

    public void toggleActive() {
        active = !active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isToggledActiveThisClick() {
        return toggledActiveThisClick;
    }

    public void setToggledActiveThisClick(boolean toggledActiveThisClick) {
        this.toggledActiveThisClick = toggledActiveThisClick;
    }

    public double getChargeTime() {
        return chargeTime;
    }

    public void setChargeTime(int chargeTime) {
        this.chargeTime = chargeTime;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public double getCost() {
        return cost;
    }

    public int getId() {
        return id;
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
    public int getPixelY() {
//        return (int) (y * Main.tileSize * Main.ZOOMFACTOR - (Main.tileSize * Main.ZOOMFACTOR / 2));
        return (int) (y * Main.tileSize);
    }

    public int getPixelX() {
//        return (int) (x * Main.tileSize * Main.ZOOMFACTOR - (Main.tileSize * Main.ZOOMFACTOR / 2));
        return (int) (x * Main.tileSize);
    }
}
