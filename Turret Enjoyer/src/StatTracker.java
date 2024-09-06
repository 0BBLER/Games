public class StatTracker {
    public static long gameTime = 0;
    public static int damageDealt = 0, energySpent = 0, creditsSpent = 0, turretsPlaced = 0, gemsCollected = 0;
    public static String avgTurretDamage = "", damagePerEnergy = "";

    public static void resetStats() {
        gameTime = 0;
        damageDealt = 0;
        energySpent = 0;
        creditsSpent = 0;
        turretsPlaced = 0;
        gemsCollected = 0;
        avgTurretDamage = "";
        damagePerEnergy = "";
    }
}
