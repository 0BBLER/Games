import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.*;
import java.util.Timer;
import java.util.*;

import static java.lang.Math.*;


public class Main implements MouseListener {

    public boolean PAUSED = false;

    public static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public static final int WIDTH = screenSize.width;
    public static final int HEIGHT = screenSize.height;
    public final boolean FREE = false;
    public static final int tileSize = 60;
    public static double ZOOMFACTOR = 0.75;
    public double SELLFACTOR = 0.5;
    public int TICK;
    public static int GAMEMODE = 0;
    public boolean WONGAME = false;
    /*
    0: title
    1: after title
    2: gameplay - placement
    3: gameplay - wave
    4: game over
    5: end screen
     */
    public JFrame frame = new JFrame();
    public final Point[] baseTiles = new Point[]{new Point(31, 8), new Point(31, 9)};

    public final int qtyAsteroidFields = 2;
    public final int turretGemCost = 10;
    public final int maxEnergyUpgradeBaseCost = 50;
    public final int creditGainUpgradeBaseCost = 50;
    public final int energyRegenUpgradeBaseCost = 50;
    public final String CREDITS_SYMBOL = "✪";
    public final int baseUpgradeCost = 5;
    public final int upgradeCostScale = 2;
    final int starCount = 500;
    public int CREDITS;
    public int maxHealth;
    public int HEALTH;
    public int energy_generation;
    public int maxEnergy;
    public int ENERGY = maxEnergy;
    public int maxEnergyUpgrade;
    public int energyRegenUpgrade;
    public int creditGainUpgrade;
    public double creditGainMultiplier;
    public int WAVE;
    public double SCORE;
    public double SCOREwave, SCOREturret, SCOREenergy, SCOREhealth, SCOREtime;
    public String SCOREcomment;
    public double FPS;
    public int enemiesLeft = 0;
    long waveStartTime = 0;
    public boolean waveDone = false;
    public int currentTurretId = 0;
    public int currentEnemyId = 0;
    public int currentProjectileId = 0;
    public boolean mouseDown = false;
    public int clickType = -1;
    public boolean placedTurret = false;
    //public boolean canReceiveSpace = false;
    public boolean spawnedEnemy = false; //for testing purposes
    public int selectedTurretId = -1;
    public int selectedPlaceTurret = -1;
    public static final Color bg = new Color(16, 16, 16);
    double direction;
    public final int enemySize = 40; //will change when images are added

    public int[] finalCutScreenList = new int[300]; // Listing of drones that appear in the final cut screen

    boolean placedBarrier = false;
    long last_gave_energy = 0;
    long startFrame = 0;
    volatile int mouseHoverX, mouseHoverY, mouseX, mouseY;
    boolean receivedWaveDone = false;
    boolean canReceivePause = false;
    boolean canReceiveResetAsteroid = false;
    int shakeTime;
    int gameEndTick;
    int baseImageId;
    int currentTip;
    boolean showingTip;
    boolean tutorialEnabled = true;
    float screenFade;
    int visibleGamemode;
    boolean fadingScreen;

    //int[][] pathfind = new int[32][18];
    ArrayList<Turret> turrets = new ArrayList<Turret>();
    ArrayList<Asteroid> asteroids = new ArrayList<Asteroid>();
    ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
    //ArrayList<Point> barriers = new ArrayList<Point>();
    ArrayList<SplashVisual> splashVisuals = new ArrayList<SplashVisual>();
    ArrayList<BufferedImage> turretImages = new ArrayList<BufferedImage>();
    ArrayList<BufferedImage> enemyImages = new ArrayList<BufferedImage>();
    ArrayList<BufferedImage> gemImages = new ArrayList<BufferedImage>();
    static ArrayList<BufferedImage> scaledImages = new ArrayList<BufferedImage>();
    ArrayList<BufferedImage> asteroidImages = new ArrayList<BufferedImage>();
    ArrayList<OnScreenGem> onScreenGems = new ArrayList<>();
    ArrayList<Star> stars = new ArrayList<Star>();
    ArrayList<BufferedImage> titleImages = new ArrayList<BufferedImage>();
    ArrayList<Particle> particles = new ArrayList<Particle>();
    BufferedImage titleBackground = ImageIO.read(getClass().getClassLoader().getResource("res/img/titleBackground.png"));

    int[] GEMS;
    boolean[] discoveredEnemies;
    volatile ArrayList<Tooltip> tooltips = new ArrayList<Tooltip>();
    int[] shake;
    int catalogIdx;
    final Font warningFont = new Font("Impact", Font.PLAIN, 90);
    boolean showingInfoScreen = false;

    private static void redirectSystemErr(String outputPath) {
        try {
            PrintStream filePrintStream = new PrintStream(new FileOutputStream(outputPath, true));

            System.setErr(filePrintStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void loadImages() throws IOException {
        System.out.println("Loading images...");
        turretImages.add(resizeImage(ImageIO.read(getClass().getClassLoader().getResource("res/img/turret_basic_body.png")), 60, 60));
        turretImages.add(resizeImage(ImageIO.read(getClass().getClassLoader().getResource("res/img/turret_fire_body.png")), 60, 60));
        turretImages.add(resizeImage(ImageIO.read(getClass().getClassLoader().getResource("res/img/turret_laser_body.png")), 60, 60));
        turretImages.add(resizeImage(ImageIO.read(getClass().getClassLoader().getResource("res/img/turret_debuff_body.png")), 60, 60));
        turretImages.add(resizeImage(ImageIO.read(getClass().getClassLoader().getResource("res/img/turret_splash_body.png")), 60, 60));


        enemyImages.add(resizeImage(ImageIO.read(getClass().getClassLoader().getResource("res/img/enemy_basic.png")), enemySize, enemySize));
        enemyImages.add(resizeImage(ImageIO.read(getClass().getClassLoader().getResource("res/img/enemy_armoured.png")), enemySize, enemySize));
        enemyImages.add(resizeImage(ImageIO.read(getClass().getClassLoader().getResource("res/img/enemy_speed.png")), enemySize, enemySize));
        enemyImages.add(resizeImage(ImageIO.read(getClass().getClassLoader().getResource("res/img/enemy_stealth.png")), enemySize, enemySize));
        enemyImages.add(resizeImage(ImageIO.read(getClass().getClassLoader().getResource("res/img/enemy_mothership.png")), enemySize * 2, enemySize * 2));

        asteroidImages.add(ImageIO.read(getClass().getClassLoader().getResource("res/img/asteroid/0.png")));
        asteroidImages.add(ImageIO.read(getClass().getClassLoader().getResource("res/img/asteroid/1.png")));
        asteroidImages.add(ImageIO.read(getClass().getClassLoader().getResource("res/img/asteroid/2.png")));
        asteroidImages.add(ImageIO.read(getClass().getClassLoader().getResource("res/img/asteroid/3.png")));
        asteroidImages.add(ImageIO.read(getClass().getClassLoader().getResource("res/img/asteroid/4.png")));

        titleImages.add(ImageIO.read(getClass().getClassLoader().getResource("res/img/playButton.png")));
        titleImages.add(ImageIO.read(getClass().getClassLoader().getResource("res/img/tutorialOn.png")));
        titleImages.add(ImageIO.read(getClass().getClassLoader().getResource("res/img/tutorialOff.png")));
        titleImages.add(ImageIO.read(getClass().getClassLoader().getResource("res/img/ovoid_games_logo.png")));
        titleImages.add(ImageIO.read(getClass().getClassLoader().getResource("res/img/closingBackground.png")));

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                gemImages.add(resizeImage(ImageIO.read(getClass().getClassLoader().getResource("res/img/gem/" + gemNumToName(i) + j + ".png")), 20, 20));
            }
        }

        scaleImages();
        System.out.println("Images loaded successfully");
    }

    public String gemNumToName(int num) {
        return switch (num) {
            case 0 -> "orange";
            case 1 -> "red";
            case 2 -> "green";
            case 3 -> "yellow";
            default -> "";
        };
    }

    public void scaleImages() throws IOException {
        scaledImages.clear();
        for (BufferedImage image : turretImages) {//+5, 5
            scaledImages.add(resizeImage(image, (int) (image.getWidth() * ZOOMFACTOR), (int) (image.getHeight() * ZOOMFACTOR)));
        }
        for (BufferedImage image : enemyImages) {//+5, 10
            scaledImages.add(resizeImage(image, (int) (image.getWidth() * ZOOMFACTOR), (int) (image.getHeight() * ZOOMFACTOR)));
        }
        for (BufferedImage image : gemImages) {//+12, 22
            scaledImages.add(resizeImage(image, (int) (image.getWidth() * ZOOMFACTOR), (int) (image.getHeight() * ZOOMFACTOR)));
        }
        //+5, 27
        scaledImages.add(resizeImage(ImageIO.read(getClass().getClassLoader().getResource("res/img/base.png")), 60, 1080));
        scaledImages.add(resizeImage(ImageIO.read(getClass().getClassLoader().getResource("res/img/base_damaged0.png")), 60, 1080));
        scaledImages.add(resizeImage(ImageIO.read(getClass().getClassLoader().getResource("res/img/base_damaged1.png")), 60, 1080));
        scaledImages.add(resizeImage(ImageIO.read(getClass().getClassLoader().getResource("res/img/base_damaged2.png")), 60, 1080));
        scaledImages.add(resizeImage(ImageIO.read(getClass().getClassLoader().getResource("res/img/base_damaged3.png")), 60, 1080));
        //+1, 28
        scaledImages.add(ImageIO.read(getClass().getClassLoader().getResource("res/img/bottombar.png")));
    }

    public Main() throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        frame.setUndecorated(true);
        frame.setVisible(true);
        frame.setSize(WIDTH, HEIGHT);
        frame.setTitle("Tower Defence");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.addMouseListener(this);
        frame.createBufferStrategy(2);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("res/img/icon.png")));

        frame.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(getClass().getResource("res/img/cursor.png")), new Point(16, 16), "custom cursor"));

        resetGame();

        loadImages();
        //createPathfindMap();

        System.out.println("Starting game!");
        game();
    }

    public static void main(String[] args) throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        System.out.println("Program started!");
        //redirectSystemErr("logs.txt");
        new Main();
    }

    public void generateStars(int amount) {
        stars.clear();
        for (int i = 0; i < amount; i++) {
            stars.add(new Star((int) (random() * WIDTH), (int) (random() * HEIGHT), (int) (random() * 4)));
        }
    }

    public void createFormation(int type, int x, int y) {
        switch (type) {
            case EnemyTypes.FORMATION_JUSTBASIC -> {
                createEnemy(EnemyTypes.BASIC, x, y);
                createEnemy(EnemyTypes.BASIC, x - 50, y - 25);
                createEnemy(EnemyTypes.BASIC, x - 50, y + 25);
            }
            case EnemyTypes.FORMATION_JUSTARMOURED -> {
                createEnemy(EnemyTypes.ARMOURED, x, y);
                createEnemy(EnemyTypes.ARMOURED, x - 50, y - 25);
                createEnemy(EnemyTypes.ARMOURED, x - 50, y + 25);
            }
            case EnemyTypes.FORMATION_JUSTSPEED -> {
                createEnemy(EnemyTypes.SPEED, x, y);
                createEnemy(EnemyTypes.SPEED, x - 50, y - 25);
                createEnemy(EnemyTypes.SPEED, x - 50, y + 25);
            }
            case EnemyTypes.FORMATION_JUSTSTEALTH -> {
                createEnemy(EnemyTypes.STEALTH, x, y);
            }
            case EnemyTypes.FORMATION_ARMOUREDMOTHERSHIP -> {
                createEnemy(EnemyTypes.ARMOURED, x, y - 35);
                createEnemy(EnemyTypes.ARMOURED, x, y + 35);
                createEnemy(EnemyTypes.ARMOURED, x - 60, y - 90);
                createEnemy(EnemyTypes.ARMOURED, x - 60, y + 90);
                createEnemy(EnemyTypes.ARMOURED, x - 120, y - 90);
                createEnemy(EnemyTypes.ARMOURED, x - 120, y + 90);
                createEnemy(EnemyTypes.ARMOURED, x - 180, y - 35);
                createEnemy(EnemyTypes.ARMOURED, x - 180, y + 35);
                createEnemy(EnemyTypes.MOTHERSHIP, x - 75, y - 10);
            }
        }
    }

    public void setupFinalCutScreen() {
        for (int i = 0; i < 100; i++) {
            finalCutScreenList[i * 3 + 0] = (i % 5);
            finalCutScreenList[i * 3 + 1] = (int) (WIDTH / 10 * (1 + 8 * Math.random()));
            finalCutScreenList[i * 3 + 2] = (int) (HEIGHT / 3 * (1 + 1 * Math.random()));
        }
    }

    public void createEnemy(int type, int x, int y) {
        enemies.add(new Enemy(currentEnemyId, type, x, y));
        currentEnemyId++;
    }

    public void createAsteroids() {

        int i, j, k, initElement;
        int fieldLength;
        int[] locationX = new int[100]; // Can this be made dynamically sized?
        int[] locationY = new int[100]; // Can this be made dynamically sized?
        int[] astSize = new int[100]; // Can this be made dynamically sized?
        int startX, startY;
        int endngX, endngY;
        double adjustX, adjustY;
        int currentX, currentY, currentSize, currentDistance, curveDirection;
        double direction, directionalFavour;

        curveDirection = random() < 0.5 ? -1 : 1;

        for (initElement = 0; initElement < 100; initElement++) { // Initialize the arrays
            locationX[initElement] = 0;
            locationY[initElement] = 0;
            astSize[initElement] = 0;
        }

        for (i = 0; i < qtyAsteroidFields; i++) { // Each field
            fieldLength = (int) round(20 + random() * 10);

            startX = (int) round((0.3 + random() * 0.4) * screenSize.getWidth());
            startY = (int) round((-0.1 + random() * 0.2) * screenSize.getHeight());
            endngX = (int) round((0.3 + random() * 0.4) * screenSize.getWidth());
            endngY = (int) round((1.1 - random() * 0.2) * screenSize.getHeight() + startY * 2);
/*
            startX = (int) round(0.1 * screenSize.getWidth());
            startY = (int) round(0.1 * screenSize.getHeight());
            endngX = (int) round(0.9 * screenSize.getWidth());
            endngY = (int) round(0.5 * screenSize.getHeight());
*/

            directionalFavour = Math.atan((double) (endngX - startX) / (double) (endngY - startY));

            for (j = 0; j < 4; j++) {  // Each strand of asteroids (the field is made up of 4 strands)
                locationX[0] = startX + (int) ((random() - 0.5) * 100); // Add a little random element to the starting point
                locationY[0] = startY + (int) ((random() - 0.5) * 100); // Add a little random element to the starting point
                astSize[0] = (int) (round(random() * 20) + 10);

                for (k = 0; k < fieldLength; k++) { // For each asteroid of each strand
                    direction = directionalFavour + ((random() - 0.5) * Math.PI / 3);
                    currentDistance = astSize[k] / 2;

                    // Setup for next asteroid
                    astSize[k + 1] = (int) (round(random() * 20) + 10);
                    currentDistance += astSize[k + 1] / 2 + (int) (random() * 50 + 10); // randomly add space between circles
                    locationX[k + 1] = locationX[k] + (int) (currentDistance * sin(direction));
                    locationY[k + 1] = locationY[k] + (int) (currentDistance * cos(direction));
                }

                adjustX = (double) (endngX - locationX[fieldLength - 1]) / (double) fieldLength;
                adjustY = (double) (endngY - locationY[fieldLength - 1]) / (double) fieldLength;

//                System.out.println("fieldlength = " + fieldLength + ", adjustX = " + adjustX + ", adjustY = " + adjustY);

                for (k = 0; k < fieldLength; k++) { // Last x,y was built up, so adjust to match the desired end location
                    locationX[k] = locationX[k] + (int) (k * adjustX);
                    locationY[k] = locationY[k] + (int) (k * adjustY);
                }
                for (k = 0; k < fieldLength; k++) { // Flex the middle in one direction or the other
                    locationX[k] = locationX[k] + (int) (Math.sin((double) k / (double) fieldLength * PI) * 300 * curveDirection);
                    locationY[k] = locationY[k] + (int) (k * adjustY);
                }


                for (k = 0; k < fieldLength; k++) { // For each asteroid of each strand
                    asteroids.add(new Asteroid(locationX[k], locationY[k], astSize[k])); // should the first value be i ???

//                    System.out.println(locationX[k] + ", " + locationY[k] + ", " + astSize[k]);
                }
            }


        }
/*
        int fieldLength;
        int startX, startY;
        int endngX, endngY;
        int currentX, currentY, currentSize, currentDistance;
        double direction, directionalFavour;

        for (int i = 0; i < qtyAsteroidFields; i++) {
            fieldLength = (int) round(10 + random() * 20);
            startX = (int) round((0.1 + random() * 0.7) * screenSize.getWidth());
            startY = (int) round((-0.1 + random() * 0.3) * screenSize.getHeight());
            endngX = (int) round((0.1 + random() * 0.7) * screenSize.getWidth());
            endngY = (int) round((1.1 - random() * 0.3) * screenSize.getHeight());

            directionalFavour = Math.atan((double) (endngX - startX) / (double) (endngY - startY));

            System.out.println(endngX + ", " + startX + ", " + endngY + ", " + startY + ", " + directionalFavour);

            //directionalFavour = (random() - 0.5) * Math.PI;

            for (int j = 0; j < 4; j++) {
                currentX = startX + (int) ((random() - 0.5) * 200); // Add a little random element to the starting point
                currentY = startY;
                currentSize = (int) (round(random() * 20) + 20);

                for (int k = 0; k < fieldLength; k++) {
                    asteroids.add(new Asteroid(i, currentX, currentY, currentSize));
                    direction = directionalFavour + ((random() - 0.5) * Math.PI / 3);
                    currentDistance = currentSize / 2;

                    // Setup for next asteroid
                    currentSize = (int) (round(random() * 20) + 20);
                    currentDistance += currentSize / 2 + (int) (random() * 50 + 10); // randomly add space between circles
                    currentX += (int) (currentDistance * sin(direction));
                    currentY += (int) (currentDistance * cos(direction));
                }
            }
        }
*/
    }

    public void createTurret(int gridX, int gridY, int type) throws LineUnavailableException, IOException {
        SoundManager.startSound("place.wav");
        turrets.add(new Turret(currentTurretId, type, gridX, gridY));
        StatTracker.turretsPlaced++;
        currentTurretId++; //each turret has a different id
        if (currentTip == 0) {
            currentTip = 1;
            showingTip = true;
        } else if (currentTip == 2) {
            showingTip = false;
        }
    }

    public void createParticle(int x, int y, double direction, double speed, Color color, int size) {
        particles.add(new Particle(x, y, direction, speed, size, color));
    }

    public void createSplashEffect(int x, int y, double radius) throws LineUnavailableException, IOException {
        splashVisuals.add(new SplashVisual(x, y, radius, 0));
        SoundManager.startSound("weakexplosion.wav");
        shakeScreen(10);
    }

    public void createOnScreenGem(int type, int x, int y) {
        onScreenGems.add(new OnScreenGem(type, x, y));
        if (currentTip == 1) {
            currentTip = 2;
            showingTip = true;
        }
    }

    /*
    public void createPathfindMap() {
        System.out.println("Creating pathfinding map...");
        boolean[][] activeTiles = new boolean[32][18];
        boolean[][] toBeActive = new boolean[32][18];
        for (int x = 0; x < 32; x++) {
            for (int y = 0; y < 18; y++) {
                pathfind[x][y] = -1;
            }
        }
        /*
        -1: not set
        1: left
        2: up
        3: right
        4: down

        int cycles = 0;
        while (cycles < 576) {
            activeTiles[baseTiles[0].x][baseTiles[0].y] = true;
            for (int x = 0; x < 32; x++) {
                for (int y = 0; y < 18; y++) {
                    if (activeTiles[x][y]) {
                        try {
                            if (!activeTiles[x - 1][y] && !isPathfindDir(pathfind[x - 1][y]) && !getBarrier(x - 1, y)) {
                                toBeActive[x - 1][y] = true;
                                pathfind[x - 1][y] = 3;
                            }
                        } catch (ArrayIndexOutOfBoundsException ignored) {
                        }
                        try {
                            if (!activeTiles[x + 1][y] && !isPathfindDir(pathfind[x + 1][y]) && !getBarrier(x + 1, y)) {
                                toBeActive[x + 1][y] = true;
                                pathfind[x + 1][y] = 1;
                            }
                        } catch (ArrayIndexOutOfBoundsException ignored) {
                        }
                        try {
                            if (!activeTiles[x][y - 1] && !isPathfindDir(pathfind[x][y - 1]) && !getBarrier(x, y - 1)) {
                                toBeActive[x][y - 1] = true;
                                pathfind[x][y - 1] = 4;
                            }
                        } catch (ArrayIndexOutOfBoundsException ignored) {
                        }
                        try {
                            if (!activeTiles[x][y + 1] && !isPathfindDir(pathfind[x][y + 1]) && !getBarrier(x, y + 1)) {
                                toBeActive[x][y + 1] = true;
                                pathfind[x][y + 1] = 2;
                            }
                        } catch (ArrayIndexOutOfBoundsException ignored) {
                        }

                        activeTiles[x][y] = false;
                    }
                }
            }
            for (int x = 0; x < 32; x++) {
                for (int y = 0; y < 18; y++) {
                    if (toBeActive[x][y]) {
                        activeTiles[x][y] = true;
                    }
                }
            }
            toBeActive = new boolean[32][18];
            cycles++;
        }
    }
    */

    public void game() {
        System.out.println("Generating asteroids and stars...");
        generateStars(starCount);

        setupFinalCutScreen(); // assigns ships to the final cut screen.

        Timer frameRefresh = new Timer();

        frameRefresh.schedule(new TimerTask() { //main loop
            @Override
            public void run() {
                startFrame = System.nanoTime();
                try {
                    gameHandler();
                    keyHandler();
                    if (!PAUSED && GAMEMODE != 4 && GAMEMODE != 5) {
                        enemyMovement();
                        turretMovement();
                        projectileMovement();
                    }
                    drawScreen();
                } catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
                    throw new RuntimeException(e);
                }
                TICK++;
                calcFps();
            }
        }, 0, 20);
    }

    public void calcFps() {
        long delta = System.nanoTime() - startFrame;
        FPS = 1f / (delta / 1000000000d);
    }

    public void gameHandler() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        //SoundManager.checkSoundtrack();

        SoundManager.checkSounds();

        mouseX = (int) MouseInfo.getPointerInfo().getLocation().getX();
        mouseY = (int) MouseInfo.getPointerInfo().getLocation().getY();
        int adjustedTileSize = (int) (tileSize * ZOOMFACTOR);
        mouseHoverX = round(mouseX / adjustedTileSize);
        mouseHoverY = round(mouseY / adjustedTileSize);
        //double seconds = ((double) System.nanoTime() / 1_000_000_000);

        //if (System.nanoTime() > last_gave_energy + 1000000000) {
        if (TICK % 28 == 0) {
            last_gave_energy = System.nanoTime();
            ENERGY += energy_generation;
            if (ENERGY > maxEnergy) ENERGY = maxEnergy;
        }

        if (GAMEMODE == 3) {
            enemiesLeft = enemies.size();
            waveDone = enemiesLeft == 0;
        }

        if (!receivedWaveDone && waveDone) {
            receivedWaveDone = true;
            finishedWave();
        }

        if (shakeTime > 0) {
            shake[0] = (int) ((random() - 0.5) * 10);
            shake[1] = (int) ((random() - 0.5) * 10);
            shakeTime--;
        } else {
            shake[0] = 0;
            shake[1] = 0;
        }

    }

    public void endGame(boolean won) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        setGamemode(4);
        StatTracker.avgTurretDamage = StatTracker.turretsPlaced == 0 ? "n/a" : (int) ((double) StatTracker.damageDealt / (double) StatTracker.turretsPlaced) + "";
        StatTracker.damagePerEnergy = StatTracker.energySpent == 0 ? "n/a" : (round(100 * (double) StatTracker.damageDealt / (double) StatTracker.energySpent) / 100f) + "";
        if (won) {
            SoundManager.setTrack(SoundManager.WIN);
        } else {
            HEALTH = 0;
            SoundManager.setTrack(SoundManager.LOSE);
        }
        //SoundManager.endSoundtrack();
        gameEndTick = TICK;
    }

    public void finishedWave() throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        StatTracker.gameTime += System.currentTimeMillis() - waveStartTime;
        if (WAVE >= 16) {
            WONGAME = true;
            endGame(WONGAME);
        }
        for (Turret turret : turrets) {
            if (!turret.isActive()) {
                turret.toggleActive();
            }
        }
    }

    public void drawScreen() throws LineUnavailableException, IOException, UnsupportedAudioFileException {

        //get drawing stuff ready
        BufferStrategy strategy = frame.getBufferStrategy();
        Graphics2D g = (Graphics2D) strategy.getDrawGraphics();

        //background
        g.setColor(bg);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        render(g);

        //screen fading
        if (screenFade > 0.02) {
            g.setColor(new Color(0, 0, 0, screenFade));
            g.fillRect(0, 0, WIDTH, HEIGHT);
        }

        if (fadingScreen && screenFade < 1) {
            screenFade += 0.2;
        } else if (screenFade > 0) {
            if (visibleGamemode != GAMEMODE) {
                visibleGamemode = GAMEMODE;
                setVisibleGamemode(visibleGamemode);
            }
            screenFade -= 0.2;
            fadingScreen = false;
        }
        if (screenFade > 1) screenFade = 1f;
        if (screenFade < 0) screenFade = 0f;

        //close button
        g.setStroke(new BasicStroke(4));
        if (mouseIn(mouseX, mouseY, WIDTH - 30, -10, 40, 40)) {
            g.setColor(new Color(255, 0, 0, 211));
            if (mouseDown) {
                System.exit(0);
            }
        } else {
            g.setColor(new Color(255, 0, 0, 34));
        }
        g.fillRoundRect(WIDTH - 30, 0, 30, 30, 5, 5);

        g.setColor(new Color(0, 0, 0, 79));
        g.drawLine(WIDTH - 25, 5, WIDTH - 5, 25);
        g.drawLine(WIDTH - 5, 5, WIDTH - 25, 25);

        //show and dispose resources
        strategy.show();
        g.dispose();
    }

    public void createProjectile(int type, int x, int y, double direction, double power, boolean setOnFire, boolean causeDebuff, boolean isSplash, double splashRadius) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        Projectile projectile = new Projectile(type, currentProjectileId, x, y, 0, 0, direction, power);
        if (setOnFire) {
            projectile.setFire(true);
        }
        if (causeDebuff) {
            projectile.setDebuff(true);
        }
        if (isSplash) {
            projectile.setSplash(true);
            projectile.setSplashRadius(splashRadius);
        }
        if (type == ProjectileTypes.BASIC || type == ProjectileTypes.MISSILE) {
            projectiles.add(projectile);
        }
        currentProjectileId++;
    }

    public void createLaser(int x, int y, double direction, double power, double length) {

        //check to see if Asteroids are in the way
        int laserSteps = (int) round(length / 10);
        int i;
        boolean inAsteroid = false;
        boolean firstAsteroid = true;
        int testX, testY;
        int asteroidX = 0, asteroidY = 0;

        for (i = 0; i < laserSteps; i++) {
            testX = x + (int) (Math.sin(direction) * 10 * i);
            testY = y + (int) (Math.cos(direction) * 10 * i);

            for (Asteroid asteroid : asteroids) {
                if (distance(testX, testY, asteroid.getX(), asteroid.getY()) < asteroid.getSize() / 2f) {
                    inAsteroid = true;
                    if (inAsteroid && firstAsteroid) {
                        asteroidX = testX;
                        asteroidY = testY;
                        firstAsteroid = false;
                    }
                }
            }
        }

        if (inAsteroid) { // laser travels to first asteroid
            projectiles.add(new Projectile(ProjectileTypes.LASER, currentProjectileId, x, y, asteroidX, asteroidY, direction, power));
        } else { // laser travels to first enemy
            projectiles.add(new Projectile(ProjectileTypes.LASER, currentProjectileId, x, y, (int) ((int) x + (sin(direction) * length)), y + (int) (Math.cos(direction) * length), direction, power));
        }
        currentProjectileId++;
    }

    /*
    public void setBarrier(int gridX, int gridY, boolean setBarrier) {
        if (getTurretAt(gridX, gridY) == null) {
            boolean contains = barriers.contains(new Point(gridX, gridY));
            if (setBarrier) {
                if (!contains) {
                    barriers.add(new Point(gridX, gridY));
                }
            } else {
                if (contains) {
                    barriers.remove(new Point(gridX, gridY));
                }
            }
        }
        createPathfindMap();
    }


    public boolean getBarrier(int gridX, int gridY) {
        return barriers.contains(new Point(gridX, gridY));
    }
     */

    public void projectileMovement() throws LineUnavailableException, IOException {
        ArrayList<Integer> toRemove = new ArrayList<Integer>();
        Point splashHitPoint = null;
        for (Projectile projectile : projectiles) {
            boolean removeThis = false;
            if (projectile.getType() == ProjectileTypes.LASER) {
                if (projectile.getAge() < ProjectileTypes.laserViewTime) {
                    projectile.changeAge(1);
                } else {
                    removeThis = true;
                }
                if (!removeThis && !projectile.isLaserDidDamage()) {
                    for (Enemy enemy : enemies) {
                        if (pointIsInBox(projectile.getX2(), projectile.getY2(), (int) round(enemy.getX()) - enemySize / 2, (int) round(enemy.getY()) - enemySize / 2, enemySize, enemySize)) {
                            if (!(((projectile.getDirection() >= Math.PI) || (projectile.getDirection() <= 0)) && (enemy.getType() == EnemyTypes.ARMOURED))) { // Don't take damage if hitting armoured enemy from the front
                                enemy.changeHealth(-projectile.getDamage());
                                StatTracker.damageDealt += projectile.getDamage();
                                projectile.setLaserDidDamage(true);
                            }
                        }
                    }
                }

            } else {
                projectile.moveX((int) (Math.sin(projectile.getDirection()) * projectile.getSpeed()));
                projectile.moveY((int) (Math.cos(projectile.getDirection()) * projectile.getSpeed()));
                int x = projectile.getX();
                int y = projectile.getY();

                if (x < -100 || y < 0 || x > WIDTH || y > HEIGHT) {
                    removeThis = true;
                } else {
                    for (Asteroid asteroid : asteroids) {
                        if (distance(x, y, asteroid.getX(), asteroid.getY()) < asteroid.getSize() / 2f) {

                            if (projectile.isSplash()) {
                                splashHitPoint = new Point((int) asteroid.getX(), (int) asteroid.getY());
                                createSplashEffect((int) asteroid.getX(), (int) asteroid.getY(), projectile.getSplashRadius());
                            }

                            removeThis = true;
                            //System.out.println("deleting projectile");
                            break;
                        }
                    }
                    if (!removeThis) {
                        for (Enemy enemy : enemies) {
                            if (pointIsInBox(x, y, (int) round(enemy.getX()) - enemySize / 2, (int) round(enemy.getY()) - enemySize / 2, enemySize, enemySize) && !projectile.alreadyHit()) {
                                if (!(((projectile.getDirection() >= Math.PI) || (projectile.getDirection() <= 0)) && (enemy.getType() == EnemyTypes.ARMOURED))) { // Don't take damage if hitting armoured enemy from the front

                                    if (projectile.isFire()) {
                                        enemy.setFireTime(150);
                                    }
                                    if (projectile.isDebuff()) {
                                        enemy.setDebuffTime(150);
                                        enemy.setDebuffFactor(0.6);
                                    }
                                    if (!(projectile.isSplash())) {
                                        enemy.changeHealth(-projectile.getDamage());
                                        StatTracker.damageDealt += projectile.getDamage();
                                    }
                                }

                                if (projectile.isSplash()) {
                                    splashHitPoint = new Point((int) enemy.getX(), (int) enemy.getY());
                                    createSplashEffect((int) enemy.getX(), (int) enemy.getY(), projectile.getSplashRadius());
                                }

                                removeThis = true;
                                projectile.setAlreadyHit(true);
                            }
                        }
                        if (splashHitPoint != null) {
                            for (Enemy enemy : enemies) {
                                if (distance(splashHitPoint.x, splashHitPoint.y, (int) enemy.getX(), (int) enemy.getY()) <= projectile.getSplashRadius()) {
                                    enemy.changeHealth(-projectile.getDamage());
                                    StatTracker.damageDealt += projectile.getDamage();

                                }

                            }
                        }
                    }
                }
            }
            if (removeThis) {
                toRemove.add(projectile.getId());
            }
        }

        for (Integer remove : toRemove) {
            projectiles.remove(getProjectileById(remove));
        }
        //System.out.println(projectiles.size() + " projectiles");
    }

    public void turretMovement() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        for (int i = 0; i < 3; i++) {
            for (Turret turret : turrets) {
                if ((turret.getType() == TurretTypes.SPLASH && i == 0) || (turret.getType() == TurretTypes.LASER && i == 1) || (turret.getType() != TurretTypes.SPLASH && turret.getType() != TurretTypes.LASER && i == 2)) {
                    turret.changeCharge(1.0);

                    if (turret.isActive() && (turret.getCharge() > turret.getChargeTime() - 1)) {
                        int closest = 99999999;
                        double closestDist = 999999999;
                        boolean enemySeen;

                        for (Enemy enemy : enemies) {
                            enemySeen = false;
                            double distance = distance((int) round(enemy.getX()), (int) round(enemy.getY()), turret.getX() * tileSize, turret.getY() * tileSize);
                            if ((distance < turret.getRange() * tileSize / 2) && ((turret.getType() != TurretTypes.DEBUFF) || (turret.getType() == TurretTypes.DEBUFF && enemy.getDebuffTime() < 1))) {
                                if (distance < closestDist) {
                                    if (turret.getType() == TurretTypes.DEBUFF) { // Debuff turrets can see everything
                                        enemySeen = true;
                                    } else {
                                        if (enemy.getType() == EnemyTypes.STEALTH) {
                                            if ((enemy.getDebuffTime() > 0) && (random() < EnemyTypes.Stealth.viewChance)) { // Stealth enemies might be seen if they are debuffed
                                                enemySeen = true;
                                            } else {
                                                enemySeen = false; // redundant, but here for completeness
                                            }
                                        } else {
                                            enemySeen = true; // Enemy is not stealthy
                                        }
                                    }
                                    if (enemySeen) { // If the enemy is seen, then set it as the closest target if it is closer than the current closest target
                                        closest = enemy.getId();
                                        closestDist = distance;
                                    }
                                }
                            }
                        }

                        if (closest != 99999999) {

                            Enemy enemy = getEnemyById(closest);

                            if ((enemy.getY() - turret.getPixelY()) == 0) {
                                if (enemy.getX() - turret.getPixelX() < 0) {
                                    direction = -Math.PI / 2;
                                } else {
                                    direction = Math.PI / 2;
                                }
                            } else {
                                direction = Math.atan(((double) (enemy.getX() - turret.getPixelX()) / (double) (enemy.getY() - turret.getPixelY())));

                                if ((enemy.getY() - turret.getPixelY()) < 0) {
                                    direction += Math.PI;
                                }
                            }

                            //System.out.println(Math.toDegrees(direction));
                            //System.out.println("DeltaX =" + (enemy.getX() - turret.getPixelX()) + " DeltaY = " + (enemy.getY() - turret.getPixelY()));
                            //System.out.println(direction);
                            if (ENERGY >= (int) (turret.getEnergyCost() * (1 / turret.getEfficiency()))) {
                                ENERGY -= (int) (turret.getEnergyCost() * (1 / turret.getEfficiency()));
                                turret.setCharge(0.0);
                                StatTracker.energySpent += turret.getEnergyCost() * (1 / turret.getEfficiency());
                                if (turret.getType() == TurretTypes.LASER) {
                                    SoundManager.startSound("shoot_laser.wav");
                                    createLaser(turret.getX() * tileSize, turret.getY() * tileSize, direction, turret.getPower(), closestDist);
                                } else {
                                    createProjectile(ProjectileTypes.BASIC, turret.getX() * tileSize, turret.getY() * tileSize, direction, turret.getPower(), turret.getType() == TurretTypes.FIRE, turret.getType() == TurretTypes.DEBUFF, turret.getType() == TurretTypes.SPLASH, turret.getSplashRadius());
                                    playRandomShootSound();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void enemyMovement() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        ArrayList<Integer> toRemove = new ArrayList<Integer>();
        for (Enemy enemy : enemies) {
            double xTest = 0;
            double yTest = 0;

            int gridTileX = (int) round(enemy.getX() / tileSize);
            int gridTileY = (int) round(enemy.getY() / tileSize);

            if (enemy.getMovePhase() == 1) {  // Not trying to avoid a barrier
                if (((((baseTiles[0].y + baseTiles[1].y) / 2f * tileSize) - enemy.getY()) == 0) || (enemy.getX() < screenSize.getWidth() * 0.75)) {
                    enemy.setDirection(Math.PI / 2);
                } else {
                    enemy.setDirection(Math.atan((double) (baseTiles[1].x * tileSize - enemy.getX()) / (double) ((baseTiles[0].y + baseTiles[1].y) / 2f * tileSize - enemy.getY())));

                    if (((baseTiles[0].y + baseTiles[1].y) / 2f * tileSize - enemy.getY()) < 0) {
                        enemy.setDirection(enemy.getDirection() + Math.PI);
                    }
                }

                /*

                xTest = (sin(enemy.getDirection()) * enemy.getSpeed() * enemy.getSpeedFactor() * enemy.getDebuffFactor());
                yTest = (cos(enemy.getDirection()) * enemy.getSpeed() * enemy.getSpeedFactor() * enemy.getDebuffFactor());
                if (inBarrier((int) (enemy.getX() + xTest), (int) (enemy.getY() + yTest))) { // If moving in the current direction would hit a barrier, set movePhase to 2
                    enemy.setMovePhase(2);
                }

                 */
            }

            /*
            if (enemy.getMovePhase() == 2) {  // Trying to avoid a barrier
                try {
                    enemy.setDirection(toRadians(dirToDeg(pathfind[gridTileX][gridTileY])));
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }
            }

            // If there are no barriers within one block of the enemy, and it is moving to the right, change movePhase to 1
            if (!inBarrier((int) (enemy.getX()), (int) (enemy.getY() + tileSize)) && !inBarrier((int) (enemy.getX()), (int) (enemy.getY() - tileSize)) && !inBarrier((int) (enemy.getX() + tileSize), (int) (enemy.getY())) && (!inBarrier((int) (enemy.getX() - tileSize), (int) (enemy.getY()))) && enemy.getDirection() == Math.PI / 2) {
                enemy.setMovePhase(1);
            }

             */

            enemy.moveX(sin(enemy.getDirection()) * enemy.getSpeed() * enemy.getSpeedFactor() * enemy.getDebuffFactor());
            enemy.moveY(cos(enemy.getDirection()) * enemy.getSpeed() * enemy.getSpeedFactor() * enemy.getDebuffFactor());

            boolean inAsteroid = false;
            for (Asteroid asteroid : asteroids) {
                if (distance((int) enemy.getX(), (int) enemy.getY(), asteroid.getX(), asteroid.getY()) < asteroid.getSize() / 2f) {
                    inAsteroid = true;
                }
            }
            if (inAsteroid || (enemy.getMovePhase() == 2 && !(enemy.getDirection() == Math.PI))) {
                enemy.setSpeedFactor(0.5);
            } else {
                enemy.setSpeedFactor(1);
            }

            enemy.changeFireTime(-1);
            if (enemy.getFireTime() > 0) {
                enemy.changeHealth(enemy.getMaxHealth() * -0.002);
                StatTracker.damageDealt += enemy.getMaxHealth() * 0.002;
            }
            if (enemy.getDebuffTime() > 0) {
                enemy.changeDebuffTime(-1);
                if (enemy.getDebuffTime() < 1) {
                    enemy.setDebuffFactor(1);
                }
            }
            if (enemy.getHealth() < 1) { //enemy death
                toRemove.add(enemy.getId());
                if (enemy.getType() == EnemyTypes.MOTHERSHIP) {
                    shakeScreen(6);
                }
                discoveredEnemies[enemy.getType()] = true;
                gainCredits((int) (EnemyTypes.getValue(enemy.getType()) * creditGainMultiplier));
                if ((enemy.getDropChance() * creditGainMultiplier) > random()) { //chance based on type and also whether the salvaging was improved
                    createOnScreenGem((int) floor(random() * 4), (int) (enemy.getX()), (int) (enemy.getY()));
                }
                SoundManager.startSound("smallexplosion" + round(random()) + ".wav");
                int amntParticles = (int) (round(random() * 20) + 12) * (enemy.getType() == EnemyTypes.MOTHERSHIP ? 8 : 1);
                for (int i = 0; i < amntParticles; i++) {
                    createParticle((int) enemy.getX(), (int) enemy.getY(), (360f / amntParticles * i), ((random() * 3) + 2) * (enemy.getType() == EnemyTypes.MOTHERSHIP ? 4 : 1), enemy.getParticleCol(), (int) ((random() * 8) + 2));
                }

            } else {
                if (pointIsInBox((int) enemy.getX(), (int) enemy.getY(), baseTiles[0].x * tileSize, baseTiles[0].y * tileSize, tileSize, tileSize * 2)) {
                    HEALTH -= enemy.getHealth();
                    if ((double) HEALTH / (double) maxHealth < 0.75) {
                        baseImageId = 1;
                        if ((double) HEALTH / (double) maxHealth < 0.5) {
                            baseImageId = 2;
                            if ((double) HEALTH / (double) maxHealth < 0.2) {
                                baseImageId = 3;
                            }
                        }
                    }
                    SoundManager.startSound("damagebig.wav");
                    shakeScreen(10);
                    toRemove.add(enemy.getId());
                    if (HEALTH < 1) {
                        baseImageId = 4;
                        WONGAME = false;
                        endGame(WONGAME);
                        StatTracker.gameTime += System.currentTimeMillis() - waveStartTime;
                    }
                    //make screen flash red
                }
            }

        }
        for (Integer id : toRemove) {
            enemies.remove(getEnemyById(id));
        }
    }

    public boolean pointIsInBox(int x, int y, int x2, int y2, int width, int height) {
        return (x >= x2) && (x <= (x2 + width)) && (y >= y2) && (y <= (y2 + height));
    }

    public double distance(int x1, int y1, int x2, int y2) {
        //System.out.println(Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)));
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }


    public void keyHandler() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        if (Keyboard.isKeyPressed(27)) { //close program on escape key
            System.exit(0);
        }

        if (Keyboard.isKeyPressed(80)) { //toggle pause on p
            if (canReceivePause) {
                canReceivePause = false;
                PAUSED = !PAUSED;
            }
        } else {
            canReceivePause = true;
        }

        /*
        if (Keyboard.isKeyPressed(32)) { //toggle gamemode on space
            if (canReceiveSpace) {
                canReceiveSpace = false;

                if (GAMEMODE == 2) {
                    setGamemode(3);
                } else {
                    setGamemode(2);
                }
            }
        } else {
            canReceiveSpace = true;
        }



        if (GAMEMODE == 2) {
            if (Keyboard.isKeyPressed(49)) { //1
                selectedPlaceTurret = TurretTypes.BASIC;
            }
            if (Keyboard.isKeyPressed(50)) { //2
                selectedPlaceTurret = TurretTypes.FIRE;
            }
            if (Keyboard.isKeyPressed(51)) { //3
                selectedPlaceTurret = TurretTypes.LASER;
            }
            if (Keyboard.isKeyPressed(52)) { //4
                selectedPlaceTurret = TurretTypes.DEBUFF;
            }
            if (Keyboard.isKeyPressed(53)) { //5
                selectedPlaceTurret = TurretTypes.SPLASH;
            }
            if (Keyboard.isKeyPressed(192)) { // ` to reset
                selectedPlaceTurret = -1;
            }
        }
        if (Keyboard.isKeyPressed(66)) { //toggle on b
            if (!placedBarrier) {
                //setBarrier(mouseHoverX, mouseHoverY, !getBarrier(mouseHoverX, mouseHoverY));
                showingInfoScreen = !showingInfoScreen;
                placedBarrier = true;
            }
        } else {
            placedBarrier = false;
        }
*/

        if (Keyboard.isKeyPressed(65) && Keyboard.isKeyPressed(16) && Keyboard.isKeyPressed(18)) { //asteroid on alt shift a
            if (canReceiveResetAsteroid) {
                asteroids.clear();
                createAsteroids();
                generateStars(starCount);

                canReceiveResetAsteroid = false;
            }
        } else {
            canReceiveResetAsteroid = true;
        }

        if (Keyboard.isKeyPressed(69) && Keyboard.isKeyPressed(16) && Keyboard.isKeyPressed(18)) { //enemy on alt shift e
            createEnemy(EnemyTypes.SPEED, 0, HEIGHT / 2);
        }

        /*
        if (Keyboard.isKeyPressed(69)) { //e to spawn enemy
            if (!spawnedEnemy) {
                //createFormation((int) round(random() * 5), 20, (int) round(random() * screenSize.getHeight()));
                createEnemy(EnemyTypes.SPEED, 0, HEIGHT / 2);
            }
            //spawnedEnemy = true;
        } else {
            spawnedEnemy = false;
        }
        */
/*
        if (Keyboard.isKeyPressed(87)) { //end game on w
            WONGAME = true;
            endGame(WONGAME);
        }
*/

    }

    public void setGamemode(int mode) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        GAMEMODE = mode;

        if (mode == 0) {
            SoundManager.setTrack(SoundManager.INTRO);
            visibleGamemode = mode;
        }
        if (mode == 1) {
            fadingScreen = true;
        }
        if (mode == 2) {
            fadingScreen = true;
        }
        if (mode == 3) {
            fadingScreen = true;
        }
        if (mode == 4) {
            selectedTurretId = -1;
            selectedPlaceTurret = -1;
            ZOOMFACTOR = 1;
            visibleGamemode = mode;
        }
        if (mode == 5) {
            visibleGamemode = mode;
        }

        scaleImages();
    }

    public void setVisibleGamemode(int mode) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        if (mode == 2) {
            if (tutorialEnabled) {
                mouseDown = false; //reset mouse to make sure that you don't press start wave or something
                if (WAVE == 3 || WAVE == 6 || WAVE == 9 || WAVE == 12) {
                    showingInfoScreen = true;
                }
            }
            if (WAVE == 15) {
                showingInfoScreen = true;
                mouseDown = false;

            }
            ZOOMFACTOR = 0.75;
            catalogIdx = -1;
            SoundManager.setTrack(SoundManager.MENU);
        }
        if (mode == 3) {
            showingTip = false;
            selectedTurretId = -1;
            selectedPlaceTurret = -1;
            ZOOMFACTOR = 1;
        }
        scaleImages();
    }

    public void render(Graphics2D g) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        if (visibleGamemode == 0) {
            g.drawImage(titleBackground, 0, 0, null);

            if (mouseIn(mouseX, mouseY, WIDTH / 2 - titleImages.get(0).getWidth() / 2, HEIGHT - 400, 480, 240)) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
                if (mouseDown) {
                    mouseDown = false;

                    SoundManager.startSound("click.wav");
                    setGamemode(1);
                    //SoundManager.setTrack(SoundManager.MENU);
                }
            } else {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
            }
            g.drawImage(titleImages.get(0), WIDTH / 2 - titleImages.get(0).getWidth() / 2, HEIGHT - 400, null);

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

            g.setColor(new Color(255, 255, 255));
            g.setFont(new Font("Monospaced", Font.BOLD, 18));
            FontMetrics metrics = g.getFontMetrics(new Font("Monospaced", Font.BOLD, 18));
            g.drawString("v1.0 [Feb 2024]", WIDTH - metrics.stringWidth("v1.0 [Feb 2024]"), HEIGHT - 2);

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            if (TICK < 150) {
                if (TICK < 51) {
                    g.setColor(new Color(0, 0, 0));
                } else {
                    g.setColor(new Color(0, 0, 0, (int) (255 - ((TICK - 50) * 2.5))));
                }
                g.fillRect(0, 0, WIDTH, HEIGHT);
            }
            return;
        }
        if (visibleGamemode == 1) {
            g.setColor(Color.WHITE);
            g.setColor(new Color(255, 0, 0));
            g.setFont(new Font("SansSerif", Font.BOLD, 60));
            g.drawString("Urgent Communiqué:", 100, 100);
            g.setColor(new Color(189, 189, 189));
            g.setFont(new Font("SansSerif", Font.PLAIN, 40));
            FontMetrics metrics = g.getFontMetrics(new Font("SansSerif", Font.PLAIN, 40));
            g.drawString("Just after setting up our base we were discovered by a space pirate collective intent on smashing", 100, 200);
            g.drawString("through our atmospheric dome using attack drones and plundering our resources.", 100, 250);
            //g.drawString("", 100, 300);
            g.drawString("As the Turret Enjoyer of the Stationary Defence Forces, your job is to acquire and install turrets", 100, 350);
            g.drawString("in geosynchronous orbit above our base to defend it against the attacking drones.", 100, 400);
            //g.drawString("", 100, 450);
            g.drawString("The turrets will be powered from energy beamed from the base.  But the energy supply is not", 100, 500);
            g.drawString("limitless.  Thankfully, we were able to install a basic solar generator and battery system, which", 100, 550);
            g.drawString("should be sufficient to supply a few turrets.  However, as you install more turrets, you will", 100, 600);
            g.drawString("likely need to increase the capacities of the generator and battery.", 100, 650);
            //g.drawString("", 100, 700);
            g.drawString("The turrets as well as base upgrades all require Ovoidium (" + CREDITS_SYMBOL + ").  We have enough to get you started.", 100, 750);
            g.drawString("If you manage to shoot down some pirate drones, we can scavenge the debris for more Ovoidium as", 100, 800);
            g.drawString("well.  If you're lucky, the debris may also contain intact power crystals that we can use to make", 100, 850);
            g.drawString("more powerful turrets.", 100, 900);
            //g.drawString("", 100, 950);
            g.setColor(new Color(255, 0, 0));
            g.drawString("Good Luck, Turret Enjoyer!", 100, 1000);

            g.setColor(new Color(160, 0, 240));
            g.drawString("Turret Enjoyer", 100 + metrics.stringWidth("As the "), 350);
            g.drawString("generator", 100 + metrics.stringWidth("limitless.  Thankfully, we were able to install a basic solar "), 550);
            g.drawString("battery", 100 + metrics.stringWidth("limitless.  Thankfully, we were able to install a basic solar generator and "), 550);
            g.drawString("Ovoidium", 100 + metrics.stringWidth("The turrets as well as base upgrades all require "), 750);
            g.drawString(CREDITS_SYMBOL, 100 + metrics.stringWidth("The turrets as well as base upgrades all require Ovoidium ("), 750);
            g.drawString("power crystals", 100 + metrics.stringWidth("well.  If you're lucky, the debris may also contain intact "), 850);

            g.setFont(new Font("SansSerif", Font.BOLD, 30));
            g.setColor(new Color(189, 189, 189));
            if (tutorialEnabled) {
                g.drawString("(Currently Enabled)", 1239, 1010);
            } else {
                g.drawString("(Currently Disabled)", 1233, 1010);
            }

            if (mouseIn(mouseX, mouseY, WIDTH - 700, HEIGHT - 150, 312, 50)) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
                if (mouseDown) {
                    mouseDown = false;
                    SoundManager.startSound("click.wav");
                    tutorialEnabled = !tutorialEnabled;
                }
            } else {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
            }

            g.drawImage(resizeImage(titleImages.get(tutorialEnabled ? 2 : 1), 312, 50), WIDTH - 700, HEIGHT - 150, null);


            if (mouseIn(mouseX, mouseY, WIDTH - 350, HEIGHT - 200, 300, 150)) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
                if (mouseDown) {
                    mouseDown = false;
                    SoundManager.startSound("click.wav");
                    setGamemode(2);
                    currentTip = 0;
                    showingTip = true;
                }
            } else {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
            }
            g.drawImage(resizeImage(titleImages.get(0), 300, 150), WIDTH - 350, HEIGHT - 200, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            return;
        }
        if (visibleGamemode == 2 && showingInfoScreen) {
            if (WAVE != 15) {
                g.setColor(new Color(189, 189, 189));
                g.setFont(new Font("SansSerif", Font.BOLD, 40));
                g.drawString("Long range sensors have detected a new type of drone ship.", 150, 200);
                rightJustify(1770, 875, "Make sure your turrets can deal with this new threat!", g);
                rightJustify(1770, 950, "Click the mouse button to continue", g);

                switch (WAVE) {
                    case 3 -> {
                        g.drawImage(resizeImage(enemyImages.get(1), 128, 128), WIDTH / 2 - 64, HEIGHT / 2 - 64, null);
                        g.drawString("It appears to have a reinforced bow.", 150, 275);
                    }
                    case 6 -> {
                        g.drawImage(resizeImage(enemyImages.get(2), 128, 128), WIDTH / 2 - 64, HEIGHT / 2 - 64, null);
                        g.drawString("It moves much faster than the other drones.", 150, 275);
                    }
                    case 9 -> {
                        g.drawImage(resizeImage(enemyImages.get(3), 128, 128), WIDTH / 2 - 64, HEIGHT / 2 - 64, null);
                        g.drawString("It seems to have a cloaking ability.", 150, 275);
                    }
                    case 12 -> {
                        g.drawImage(resizeImage(enemyImages.get(4), 256, 256), WIDTH / 2 - 128, HEIGHT / 2 - 128, null);
                        g.drawString("It looks huge and is always surrounded by armoured escorts.", 150, 275);
                    }
                }
            } else {
                g.setColor(new Color(189, 189, 189));
                g.setFont(new Font("SansSerif", Font.BOLD, 40));
                g.drawString("Long range sensors have detected a massive armada approaching.", 150, 200);
                rightJustify(1770, 950, "Click the mouse button to continue", g);

                for (int i = 0; i < 100; i++) {
                    if (((i + 1) % 5) == 0) {
                        g.drawImage(resizeImage(enemyImages.get(finalCutScreenList[i * 3 + 0]), 128, 128), finalCutScreenList[i * 3 + 1] - 64, finalCutScreenList[i * 3 + 2] - 64, null);
                    } else {
                        g.drawImage(resizeImage(enemyImages.get(finalCutScreenList[i * 3 + 0]), 64, 64), finalCutScreenList[i * 3 + 1] - 32, finalCutScreenList[i * 3 + 2] - 32, null);
                    }
                }

                g.setFont(new Font("Impact", Font.PLAIN, 80));
                FontMetrics metrics = g.getFontMetrics(new Font("Impact", Font.PLAIN, 80));
                g.setColor(new Color(240, 0, 0));
                g.drawString("PREPARE FOR THE FINAL BATTLE", WIDTH / 2 - metrics.stringWidth("PREPARE FOR THE FINAL BATTLE") / 2, 875);
            }
            if (mouseDown) {
                showingInfoScreen = false;
                mouseDown = false;

            }
            return;
        }

        if (visibleGamemode == 5) {
            g.setColor(new Color(9, 9, 9));
            g.fillRect(0, 0, WIDTH, HEIGHT);

            for (Star star : stars) {
                g.setColor(new Color(255, 255, 255, (int) (((sin(star.getOffset() * TICK / 10f) + 1) / 2) * 200) + 55));
                g.drawOval((int) (star.getX() - star.getSize() / 2f), (int) (star.getY() - star.getSize() / 2f), (star.getSize()), (star.getSize()));
            }

            g.drawImage(titleImages.get(4), 0, 0, null);
            g.drawImage(titleImages.get(3), 10, HEIGHT - 210, null);

            g.setColor(new Color(190, 190, 190));
            FontMetrics metrics = g.getFontMetrics(new Font("SansSerif", Font.BOLD, 80));
            g.setFont(new Font("SansSerif", Font.BOLD, 80));
            g.drawString("Thank you for playing", WIDTH / 2 - metrics.stringWidth("Thank you for playing") / 2, 100);

            return;
        }

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));

        tooltips.clear();

        int adjustedTileSize = (int) (tileSize * ZOOMFACTOR);

        for (Star star : stars) {
            g.setColor(new Color(255, 255, 255, (int) (((sin(star.getOffset() * TICK / 10f) + 1) / 2) * 200) + 55));
            g.drawOval(adjustX((star.getX() - star.getSize() / 2f) * ZOOMFACTOR), adjustY((star.getY() - star.getSize() / 2f) * ZOOMFACTOR), (int) (star.getSize() * ZOOMFACTOR), (int) (star.getSize() * ZOOMFACTOR));
        }

        g.setColor(new Color(255, 255, 255, 5));
        g.setStroke(new BasicStroke(4));

        for (int x = 0; x < WIDTH / tileSize; x++) {
            for (int y = 0; y < HEIGHT / tileSize; y++) {
                if (mouseHoverX < WIDTH * ZOOMFACTOR && mouseHoverY < HEIGHT * ZOOMFACTOR && mouseHoverX == x && mouseHoverY == y) { //fill rect that mouse is hovering

                    if (selectedPlaceTurret != -1) {
                        /*
                        g.setColor(TurretTypes.getTurretColor(selectedPlaceTurret));
                        g.fillRect(adjustX(x * adjustedTileSize - adjustedTileSize / 2f), adjustY(y * adjustedTileSize - adjustedTileSize / 2f), adjustedTileSize, adjustedTileSize);
                        g.setColor(bg_half);
                        g.fillRect(adjustX(x * adjustedTileSize - adjustedTileSize / 2f), adjustY(y * adjustedTileSize - adjustedTileSize / 2f), adjustedTileSize, adjustedTileSize);
                        g.fillRect(adjustX(x * adjustedTileSize - adjustedTileSize / 2f), adjustY(y * adjustedTileSize - adjustedTileSize / 2f), adjustedTileSize, adjustedTileSize);
                         */
                        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                        g.drawImage(scaledImages.get(selectedPlaceTurret), adjustX(x * adjustedTileSize - adjustedTileSize / 2f), adjustY(y * adjustedTileSize - adjustedTileSize / 2f), null);
                        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
                        g.setColor(new Color(255, 255, 255, 26));
                        g.drawOval((int) (adjustX(mouseHoverX * adjustedTileSize) - (TurretTypes.getTurretRange(selectedPlaceTurret) / 2 * adjustedTileSize)), (int) (adjustY(mouseHoverY * adjustedTileSize) - (TurretTypes.getTurretRange(selectedPlaceTurret) / 2 * adjustedTileSize)), (int) (TurretTypes.getTurretRange(selectedPlaceTurret) * adjustedTileSize), (int) (TurretTypes.getTurretRange(selectedPlaceTurret) * adjustedTileSize));
                    } else {
                        g.setColor(new Color(255, 255, 255, 37));
                        g.drawRect(adjustX(x * adjustedTileSize - adjustedTileSize / 2f), adjustY(y * adjustedTileSize - adjustedTileSize / 2f), adjustedTileSize, adjustedTileSize);
                    }


                    if(visibleGamemode !=5) {
                        if (mouseDown && !placedTurret && clickType == MouseEvent.BUTTON1) {
                            if (getTurretAt(x, y) == null && selectedPlaceTurret != -1 && canPlace(x, y) && canAffordTurret(selectedPlaceTurret)) { //placing turret
                                mouseDown = false;
                                placedTurret = true;
                                createTurret(x, y, selectedPlaceTurret);
                                if (!FREE) {
                                    CREDITS -= TurretTypes.getTurretCost(selectedPlaceTurret);
                                    StatTracker.creditsSpent += TurretTypes.getTurretCost(selectedPlaceTurret);
                                    if (selectedPlaceTurret != TurretTypes.BASIC) {
                                        GEMS[selectedPlaceTurret - 1] -= turretGemCost;
                                    }
                                }
                                selectedTurretId = getTurretAt(x, y).getId();
                            } else { //select turret
                                if (getTurretAt(x, y) != null) {
                                    if (selectedTurretId == getTurretAt(x, y).getId()) { //already selected
                                        mouseDown = false;
                                        selectedTurretId = -1;
                                    } else {
                                        selectedTurretId = getTurretAt(x, y).getId();
                                        SoundManager.startSound("select.wav");
                                        mouseDown = false;
                                    }
                                } else if (visibleGamemode == 2) {
                                    mouseDown = false;
                                    if (selectedPlaceTurret != -1) SoundManager.startSound("deny.wav");
                                    selectedTurretId = -1;
                                } else {
                                    selectedTurretId = -1;
                                }
                            }
                        }
                        if (mouseDown && clickType == MouseEvent.BUTTON3) { //toggle active
                            if (getTurretAt(x, y) != null && !getTurretAt(x, y).isToggledActiveThisClick()) {
                                //placedTurret = true;
                                getTurretAt(x, y).toggleActive();
                                getTurretAt(x, y).setToggledActiveThisClick(true);
                            }
                        } else {
                            for (Turret turret : turrets) {
                                turret.setToggledActiveThisClick(false);
                            }
                        }
                    }
                } else { //no mouse hover
                    g.setColor(visibleGamemode == 2 ? new Color(255, 255, 255, 5) : new Color(255, 255, 255, 1));
                    g.drawRect(adjustX(x * adjustedTileSize - adjustedTileSize / 2f), adjustY(y * adjustedTileSize - adjustedTileSize / 2f), adjustedTileSize, adjustedTileSize);
                }
            }
        }

        for (Asteroid asteroid : asteroids) { //draw asteroids
            g.drawImage(resizeImage(asteroidImages.get(asteroid.getImageId()), asteroid.getSize(), asteroid.getSize()), (int) adjustX((asteroid.getX() * ZOOMFACTOR) - (asteroid.getSize() * ZOOMFACTOR) / 2f), (int) adjustY((asteroid.getY() * ZOOMFACTOR) - (asteroid.getSize() * ZOOMFACTOR) / 2f), null);
        }

        for (Turret turret : turrets) { //draw turrets
            if (!turret.isActive()) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
            }

            g.drawImage(scaledImages.get(turret.getType()), adjustX((turret.getX() * adjustedTileSize - adjustedTileSize / 2f)), adjustY((turret.getY() * adjustedTileSize - adjustedTileSize / 2f)), null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
            if (turret.getId() == selectedTurretId) { //selected turret
                g.setColor(new Color(253, 250, 250, 132));
                g.drawRect(adjustX((turret.getX() * adjustedTileSize - adjustedTileSize / 2f)), adjustY((turret.getY() * adjustedTileSize - adjustedTileSize / 2f)), adjustedTileSize, adjustedTileSize);

                g.setColor(new Color(253, 250, 250, 61));
                g.drawOval((int) (adjustX(turret.getX() * adjustedTileSize) - (turret.getRange() / 2 * adjustedTileSize)), (int) (adjustY(turret.getY() * adjustedTileSize) - (turret.getRange() / 2 * adjustedTileSize)), (int) (turret.getRange() * adjustedTileSize), (int) (turret.getRange() * adjustedTileSize));
            }


        }

        //draw gems
        g.setColor(new Color(255, 255, 255, 92));
        ArrayList<OnScreenGem> gemsToRemove = new ArrayList<>();
        for (OnScreenGem gem : onScreenGems) {
            RescaleOp rescaleOp = new RescaleOp(((float) (sin((TICK + gem.getOffset() * 25f) / 20f) + 1) / 2) + 1, 0, null);
            int w = gem.getImage().getWidth();
            int h = gem.getImage().getHeight();
            g.drawImage(rescaleOp.filter(gem.getImage(), null), adjustX((gem.getX() - w / 2f) * ZOOMFACTOR), adjustY((gem.getY() - h / 2f) * ZOOMFACTOR), null);
            if (distance(mouseX, mouseY, adjustX(gem.getX() * ZOOMFACTOR), adjustY(gem.getY() * ZOOMFACTOR)) < 50) {
                g.fillOval(adjustX((gem.getX() - (w + 12) / 2f) * ZOOMFACTOR), adjustY((gem.getY() - (h + 12) / 2f) * ZOOMFACTOR), w + 12, h + 12);
                gemsToRemove.add(gem);
                GEMS[gem.getType()]++;
                StatTracker.gemsCollected++;
                SoundManager.startSound("ding.wav");
            }
        }
        onScreenGems.removeAll(gemsToRemove);
        //draw pathfinding overlay
/*
        for (int x = 0; x < 32; x++) {
            for (int y = 0; y < 18; y++) {
                g.setColor(new Color(255, 255, 255, 55));
                g.drawString(dirToString(pathfind[x][y]), adjustX(x * adjustedTileSize), adjustY(y * adjustedTileSize));
            }
        }

 */

        //draw enemies
        for (Enemy enemy : enemies) {
            BufferedImage enemyImg = scaledImages.get(enemy.getType() + 5);
            int xpos = (int) ((int) (enemy.getX() * ZOOMFACTOR));
            int ypos = (int) ((int) (enemy.getY() * ZOOMFACTOR));
            if (enemy.getFireTime() > 0) {
                g.setColor(new Color(255, 0, 0, 23));
                g.fillRect((int) adjustX(xpos - (enemyImg.getWidth() / 2f) - 5 * ZOOMFACTOR), (int) adjustY(ypos - (enemyImg.getHeight() / 2f) - 5 * ZOOMFACTOR), (int) ((int) (enemyImg.getWidth() + 10 * ZOOMFACTOR)), (int) ((int) (enemyImg.getHeight() + 10 * ZOOMFACTOR)));
            } else {
                if (enemy.getDebuffTime() > 0) {
                    g.setColor(new Color(63, 215, 11, 23));
                    g.fillRect((int) adjustX(xpos - (enemyImg.getWidth() / 2f) - 5 * ZOOMFACTOR), (int) adjustY(ypos - (enemyImg.getHeight() / 2f) - 5 * ZOOMFACTOR), ((int) (enemyImg.getWidth() + 10 * ZOOMFACTOR)), (int) ((int) (enemyImg.getHeight() + 10 * ZOOMFACTOR)));
                }
            }
            /*
            switch (enemy.getType()) {
                case EnemyTypes.BASIC: {
                    g.setColor(new Color(0, 240, 240));
                    break;
                }
                case EnemyTypes.SPEED: {
                    g.setColor(new Color(240, 240, 0));
                    break;
                }
                case EnemyTypes.ARMOURED: {
                    g.setColor(new Color(112, 112, 112));
                    break;
                }
                case EnemyTypes.MOTHERSHIP: {
                    g.setColor(new Color(43, 103, 0));
                    break;
                }
                case EnemyTypes.STEALTH: {
                    g.setColor(new Color(28, 28, 28));
                    break;
                }
            }


            g.fillRect((int) adjustX(xpos - (enemySize / 2f * ZOOMFACTOR)), (int) adjustY(ypos - (enemySize / 2f * ZOOMFACTOR)), (int) (enemySize * ZOOMFACTOR), (int) (enemySize * ZOOMFACTOR));
*/

            g.drawImage(enemyImg, adjustX(xpos - (enemyImg.getWidth() / 2f)), (int) adjustY(ypos - (enemyImg.getHeight() / 2f)), null);

            //health bar
            if (enemy.getType() == EnemyTypes.STEALTH) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.05f));
            }
            g.setColor(new Color(255, 255, 255, 32));
            g.fillRoundRect(adjustX(xpos - (enemySize / 2f * ZOOMFACTOR)), (int) adjustY(ypos - (enemySize / 2f * ZOOMFACTOR) - 10 * ZOOMFACTOR), (int) (enemySize * ZOOMFACTOR), (int) (5 * ZOOMFACTOR), (int) (4 * ZOOMFACTOR), (int) (4 * ZOOMFACTOR));
            double healthPercentage = enemy.getHealth() / (double) (enemy.getMaxHealth());
            g.setColor(new Color(Color.HSBtoRGB((float) ((float) healthPercentage * 0.4 - 0.1), 1, 1)));
            g.fillRoundRect(adjustX(xpos - (enemySize / 2f * ZOOMFACTOR)), adjustY(ypos - (enemySize / 2f * ZOOMFACTOR) - 10 * ZOOMFACTOR), (int) ((int) (healthPercentage * enemySize) * ZOOMFACTOR), (int) (5 * ZOOMFACTOR), (int) (4 * ZOOMFACTOR), (int) (4 * ZOOMFACTOR));
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        }

        //draw particles
        ArrayList<Particle> particlesToRemove = new ArrayList<>();
        for (Particle particle : particles) {
            g.setColor(particle.getColor());
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1 - (particle.getTime() / 20f)));
            g.fillOval(adjustX(particle.getX() - particle.getSize() / 2f), adjustY(particle.getY() - particle.getSize() / 2f), (int) (particle.getSize() * ZOOMFACTOR), (int) (particle.getSize() * ZOOMFACTOR));
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
            particle.tick();
            if (particle.getTime() > 20) {
                particlesToRemove.add(particle);
            }
        }
        particles.removeAll(particlesToRemove);

        //draw splash visuals
        if (splashVisuals.size() > 0) {
            ArrayList<SplashVisual> toRemove = new ArrayList<>();
            for (SplashVisual splash : splashVisuals) {
                g.setColor(new Color(240, 240, 0, (int) ((1 - ((double) splash.getAge() / (double) SplashVisual.viewTime)) * 100d)));
                g.fillOval(adjustX((splash.getX() - splash.getRadius()) * ZOOMFACTOR), adjustY((splash.getY() - splash.getRadius()) * ZOOMFACTOR), (int) ((splash.getRadius() * 2) * ZOOMFACTOR), (int) ((splash.getRadius() * 2) * ZOOMFACTOR));
                splash.changeAge(1);
                if (splash.getAge() >= SplashVisual.viewTime) toRemove.add(splash);
            }
            splashVisuals.removeAll(toRemove);
        }

        //draw barriers
        /*
        for (Point barrier : barriers) {
            g.setColor(new Color(47, 47, 47));
            g.fillRect(adjustX(barrier.getX() * tileSize * ZOOMFACTOR - (tileSize * ZOOMFACTOR / 2f)), adjustY(barrier.getY() * tileSize * ZOOMFACTOR - (tileSize * ZOOMFACTOR / 2f)), (int) (tileSize * ZOOMFACTOR), (int) (tileSize * ZOOMFACTOR));
        }

         */

        //draw base
        g.drawImage(resizeImage(scaledImages.get(22 + baseImageId), (int) (scaledImages.get(22 + baseImageId).getWidth() * ZOOMFACTOR), (int) (scaledImages.get(22 + baseImageId).getHeight() * ZOOMFACTOR)), adjustedTileSize * 31 + shake[0], shake[1], null);

        /*
        g.setColor(new Color(44, 0, 49, 128));
        for (Point tile : baseTiles) {
            g.fillRect(adjustedTileSize * tile.x, adjustedTileSize * tile.y, adjustedTileSize, adjustedTileSize);
        }


         */

        //draw projectiles
        for (Projectile projectile : projectiles) {
            if (projectile.getType() == ProjectileTypes.LASER) {
                g.setColor(new Color(255, 3, 3, (int) ((1 - ((double) projectile.getAge() / (double) ProjectileTypes.laserViewTime)) * 100d)));
                g.drawLine(adjustX(projectile.getX() * ZOOMFACTOR), adjustY(projectile.getY() * ZOOMFACTOR), adjustX(projectile.getX2() * ZOOMFACTOR), adjustY(projectile.getY2() * ZOOMFACTOR));
            } else {
                g.setColor(projectile.getColor());
                g.fillOval(adjustX(projectile.getX() * ZOOMFACTOR - 4 * ZOOMFACTOR), adjustY(projectile.getY() * ZOOMFACTOR - 4 * ZOOMFACTOR), (int) (8 * ZOOMFACTOR), (int) (8 * ZOOMFACTOR));
            }
        }
        int sidebarWidth = (int) (WIDTH - WIDTH * ZOOMFACTOR);
        int bottombarHeight = (int) (HEIGHT - HEIGHT * ZOOMFACTOR);


        if (visibleGamemode == 2) {
            //draw bottom bar
            //g.setColor(new Color(80, 80, 80));
            //g.fillRect(0, (int) (HEIGHT * ZOOMFACTOR), WIDTH, bottombarHeight);
            g.drawImage(scaledImages.get(27), 0, (int) (HEIGHT * ZOOMFACTOR), null);
            //System.out.println(WIDTH + ", " + bottombarHeight);

            //base upgrades
            if (mouseIn(mouseX, mouseY, 340, HEIGHT - bottombarHeight + 20, 220, 67)) { //upgrade max energy
                drawTooltip(mouseX, mouseY, "Increases the storage capacity \nof the battery by 250 ⚡.");
                g.setColor(new Color(178, 148, 60).brighter());
                if (mouseDown) {
                    mouseDown = false;
                    if (FREE || CREDITS >= round((maxEnergyUpgrade + 1) * (maxEnergyUpgradeBaseCost + (maxEnergyUpgrade) / 2f * maxEnergyUpgradeBaseCost))) {
                        if (!FREE) {
                            CREDITS -= round((maxEnergyUpgrade + 1) * (maxEnergyUpgradeBaseCost + (maxEnergyUpgrade) / 2f * maxEnergyUpgradeBaseCost));
                            StatTracker.creditsSpent += round((maxEnergyUpgrade + 1) * (maxEnergyUpgradeBaseCost + (maxEnergyUpgrade) / 2f * maxEnergyUpgradeBaseCost));
                        }
                        upgradeMaxEnergy();
                    } else {
                        SoundManager.startSound("deny.wav");
                    }
                }
            } else {
                g.setColor(new Color(178, 148, 60));
            }
            g.fillRect(340, HEIGHT - bottombarHeight + 20, 220, 67);

            if (mouseIn(mouseX, mouseY, 340, HEIGHT - bottombarHeight + 100, 220, 67)) { //upgrade energy regeneration
                drawTooltip(mouseX, mouseY, "Increases the recharge rate \nof the battery by 50 ⚡/s.");
                g.setColor(new Color(178, 148, 60).brighter());
                if (mouseDown) {
                    mouseDown = false;
                    if (FREE || CREDITS >= round((energyRegenUpgrade + 1) * (energyRegenUpgradeBaseCost + (energyRegenUpgrade) / 2f * maxEnergyUpgradeBaseCost))) {
                        if (!FREE) {
                            CREDITS -= round((energyRegenUpgrade + 1) * (energyRegenUpgradeBaseCost + (energyRegenUpgrade) / 2f * maxEnergyUpgradeBaseCost));
                            StatTracker.creditsSpent += round((energyRegenUpgrade + 1) * (energyRegenUpgradeBaseCost + (energyRegenUpgrade) / 2f * maxEnergyUpgradeBaseCost));
                        }
                        upgradeEnergyRegen();
                    } else {
                        SoundManager.startSound("deny.wav");
                    }

                }
            } else {
                g.setColor(new Color(178, 148, 60));
            }
            g.fillRect(340, HEIGHT - bottombarHeight + 100, 220, 67);

            if (mouseIn(mouseX, mouseY, 340, HEIGHT - bottombarHeight + 180, 220, 67)) { //upgrade credit gain
                drawTooltip(mouseX, mouseY, "Increases " + CREDITS_SYMBOL + " and crystals \ngained from salvage missions \nby 20%.");
                g.setColor(new Color(178, 148, 60).brighter());
                if (mouseDown) {
                    mouseDown = false;
                    if (FREE || CREDITS >= round((creditGainUpgrade + 1) * (creditGainUpgradeBaseCost + (creditGainUpgrade) / 2f * maxEnergyUpgradeBaseCost))) {
                        if (!FREE) {
                            CREDITS -= round((creditGainUpgrade + 1) * (creditGainUpgradeBaseCost + (creditGainUpgrade) / 2f * maxEnergyUpgradeBaseCost));
                            StatTracker.energySpent += round((creditGainUpgrade + 1) * (creditGainUpgradeBaseCost + (creditGainUpgrade) / 2f * maxEnergyUpgradeBaseCost));
                        }
                        upgradeCreditGain();
                    } else {
                        SoundManager.startSound("deny.wav");
                    }
                }
            } else {
                g.setColor(new Color(178, 148, 60));
            }
            g.fillRect(340, HEIGHT - bottombarHeight + 180, 220, 67);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Monospaced", Font.BOLD, 20));

            g.drawString("UPGRADE BATTERY", 345, HEIGHT - bottombarHeight + 40);
            g.drawString("Increase Max ⚡", 345, HEIGHT - bottombarHeight + 60);
            g.drawString("To Level " + (maxEnergyUpgrade + 1) + ": " + round((maxEnergyUpgrade + 1) * (maxEnergyUpgradeBaseCost + (maxEnergyUpgrade) / 2f * maxEnergyUpgradeBaseCost)) + "" + CREDITS_SYMBOL, 345, HEIGHT - bottombarHeight + 80);

            g.drawString("UPGRADE GENERATOR", 345, HEIGHT - bottombarHeight + 120);
            g.drawString("Increase ⚡ gain", 345, HEIGHT - bottombarHeight + 140);
            g.drawString("To Level " + (energyRegenUpgrade + 1) + ": " + round((energyRegenUpgrade + 1) * (energyRegenUpgradeBaseCost + (energyRegenUpgrade) / 2f * maxEnergyUpgradeBaseCost)) + "" + CREDITS_SYMBOL, 345, HEIGHT - bottombarHeight + 160);

            g.drawString("UPGRADE SALVAGING", 345, HEIGHT - bottombarHeight + 200);
            g.drawString("Increase looting", 345, HEIGHT - bottombarHeight + 220);
            g.drawString("To Level " + (creditGainUpgrade + 1) + ": " + round((creditGainUpgrade + 1) * (creditGainUpgradeBaseCost + (creditGainUpgrade) / 2f * maxEnergyUpgradeBaseCost)) + "" + CREDITS_SYMBOL, 345, HEIGHT - bottombarHeight + 240);

            //turret selection menu
            int x = 600;

            g.setColor(new Color(255, 255, 255, 26));
            if (mouseIn(mouseX, mouseY, x, (int) ((HEIGHT * ZOOMFACTOR) + 20), 100, 100)) { //deselect placing
                g.setColor(new Color(96, 96, 96).brighter());
                if (mouseDown) {
                    mouseDown = false;
                    selectTurret(-1);
                }
            }
            g.drawRect(x, (int) ((HEIGHT * ZOOMFACTOR) + 20), 100, 100);

            x += 110;

            if (mouseIn(mouseX, mouseY, x, (int) ((HEIGHT * ZOOMFACTOR) + 20), 100, 100)) { //place basic turret
                g.drawRect(x, (int) ((HEIGHT * ZOOMFACTOR) + 20), 100, 100);
                drawTooltip(mouseX, mouseY, "Cheap and effective.");
                if (mouseDown) {
                    mouseDown = false;
                    selectTurret(TurretTypes.BASIC);
                }
            }

            if (!canAffordTurret(TurretTypes.BASIC)) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
            }

            g.drawImage(resizeImage(turretImages.get(0), 100, 100), x, (int) ((HEIGHT * ZOOMFACTOR) + 20), null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));

            x += 110;

            if (mouseIn(mouseX, mouseY, x, (int) ((HEIGHT * ZOOMFACTOR) + 20), 100, 100)) { //place fire turret
                g.drawRect(x, (int) ((HEIGHT * ZOOMFACTOR) + 20), 100, 100);
                drawTooltip(mouseX, mouseY, "Shoots incendiary projectiles. \nFire reduces ship health by \n30% over a few seconds.");
                if (mouseDown) {
                    mouseDown = false;
                    selectTurret(TurretTypes.FIRE);
                }
            }

            if (!canAffordTurret(TurretTypes.FIRE)) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
            }

            g.drawImage(resizeImage(gemImages.get(0), 30, 30), x + (String.valueOf(GEMS[0]).length() * 25), HEIGHT - 50, null);

            g.drawImage(resizeImage(turretImages.get(1), 100, 100), x, (int) ((HEIGHT * ZOOMFACTOR) + 20), null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));

            x += 110;

            if (mouseIn(mouseX, mouseY, x, (int) ((HEIGHT * ZOOMFACTOR) + 20), 100, 100)) { //place laser turret
                g.drawRect(x, (int) ((HEIGHT * ZOOMFACTOR) + 20), 100, 100);
                drawTooltip(mouseX, mouseY, "Shoots powerful lasers. More \neffective than projectiles \nagainst fast moving targets.");
                if (mouseDown) {
                    mouseDown = false;
                    selectTurret(TurretTypes.LASER);
                }
            }

            if (!canAffordTurret(TurretTypes.LASER)) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
            }

            g.drawImage(resizeImage(gemImages.get(3), 30, 30), x + (String.valueOf(GEMS[1]).length() * 25), HEIGHT - 50, null);
            g.drawImage(resizeImage(turretImages.get(2), 100, 100), x, (int) ((HEIGHT * ZOOMFACTOR) + 20), null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));

            x += 110;

            if (mouseIn(mouseX, mouseY, x, (int) ((HEIGHT * ZOOMFACTOR) + 20), 100, 100)) { //place debuff turret
                g.drawRect(x, (int) ((HEIGHT * ZOOMFACTOR) + 20), 100, 100);
                drawTooltip(mouseX, mouseY, "Shoots a projectile that does \nno damage, but disrupts a \nship's systems, e.g. engines \nand cloaking devices.");
                if (mouseDown) {
                    mouseDown = false;
                    selectTurret(TurretTypes.DEBUFF);
                }
            }

            if (!canAffordTurret(TurretTypes.DEBUFF)) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
            }

            g.drawImage(resizeImage(gemImages.get(6), 30, 30), x + (String.valueOf(GEMS[2]).length() * 25), HEIGHT - 50, null);
            g.drawImage(resizeImage(turretImages.get(3), 100, 100), x, (int) ((HEIGHT * ZOOMFACTOR) + 20), null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));

            x += 110;

            if (mouseIn(mouseX, mouseY, x, (int) ((HEIGHT * ZOOMFACTOR) + 20), 100, 100)) { //place splash turret
                g.drawRect(x, (int) ((HEIGHT * ZOOMFACTOR) + 20), 100, 100);
                drawTooltip(mouseX, mouseY, "Shoots a projectile that \nexplodes on impact and \ndamages all ships within its \nblast radius.");
                if (mouseDown) {
                    mouseDown = false;
                    selectTurret(TurretTypes.SPLASH);
                }
            }

            if (!canAffordTurret(TurretTypes.SPLASH)) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
            }

            g.drawImage(resizeImage(gemImages.get(9), 30, 30), x + (String.valueOf(GEMS[3]).length() * 25), HEIGHT - 50, null);
            g.drawImage(resizeImage(turretImages.get(4), 100, 100), x, (int) ((HEIGHT * ZOOMFACTOR) + 20), null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));

            x = 600;

            g.setColor(new Color(255, 255, 255, 123));
            switch (selectedPlaceTurret) {
                case -1 -> g.drawRect(x, (int) ((HEIGHT * ZOOMFACTOR) + 20), 100, 100);
                case TurretTypes.BASIC -> {
                    x += 110;
                    g.drawRect(x, (int) ((HEIGHT * ZOOMFACTOR) + 20), 100, 100);
                }
                case TurretTypes.FIRE -> {
                    x += 110 * 2;
                    g.drawRect(x, (int) ((HEIGHT * ZOOMFACTOR) + 20), 100, 100);
                }
                case TurretTypes.LASER -> {
                    x += 110 * 3;
                    g.drawRect(x, (int) ((HEIGHT * ZOOMFACTOR) + 20), 100, 100);
                }
                case TurretTypes.DEBUFF -> {
                    x += 110 * 4;
                    g.drawRect(x, (int) ((HEIGHT * ZOOMFACTOR) + 20), 100, 100);
                }
                case TurretTypes.SPLASH -> {
                    x += 110 * 5;
                    g.drawRect(x, (int) ((HEIGHT * ZOOMFACTOR) + 20), 100, 100);
                }

            }

            //turret info

            g.setFont(new Font("Monospaced", Font.BOLD, 32));
            x = 825;
            g.drawString(GEMS[0] + "", x, HEIGHT - 23);
            x += 110;
            g.drawString(GEMS[1] + "", x, HEIGHT - 23);
            x += 110;
            g.drawString(GEMS[2] + "", x, HEIGHT - 23);
            x += 110;
            g.drawString(GEMS[3] + "", x, HEIGHT - 23);

            x = 710;

            g.setColor(Color.BLACK);
            g.setFont(new Font("Monospaced", Font.BOLD, 13));
            g.drawString(TurretTypes.Basic.name, x, (int) ((HEIGHT * ZOOMFACTOR) + 135));
            g.drawString(TurretTypes.Basic.cost + " " + CREDITS_SYMBOL, x, (int) ((HEIGHT * ZOOMFACTOR) + 150));
            x += 110;
            g.drawString(TurretTypes.Fire.name, x, (int) ((HEIGHT * ZOOMFACTOR) + 135));
            g.drawString(TurretTypes.Fire.cost + " " + CREDITS_SYMBOL, 820, (int) ((HEIGHT * ZOOMFACTOR) + 150));
            x += 110;
            g.drawString(TurretTypes.Laser.name, x, (int) ((HEIGHT * ZOOMFACTOR) + 135));
            g.drawString(TurretTypes.Laser.cost + " " + CREDITS_SYMBOL, 930, (int) ((HEIGHT * ZOOMFACTOR) + 150));
            x += 110;
            g.drawString(TurretTypes.Debuff.name, x, (int) ((HEIGHT * ZOOMFACTOR) + 135));
            g.drawString(TurretTypes.Debuff.cost + " " + CREDITS_SYMBOL, 1040, (int) ((HEIGHT * ZOOMFACTOR) + 150));
            x += 110;
            g.drawString(TurretTypes.Splash.name, x, (int) ((HEIGHT * ZOOMFACTOR) + 135));
            g.drawString(TurretTypes.Splash.cost + " " + CREDITS_SYMBOL, 1150, (int) ((HEIGHT * ZOOMFACTOR) + 150));

            x = 710;

            g.setFont(new Font("Monospaced", Font.PLAIN, 11).deriveFont(createCharSpacing(-0.08f))); // basic turret
            g.setColor(new Color(213, 213, 213));
            g.drawString("Power: " + (int) TurretTypes.Basic.power, x, (int) ((HEIGHT * ZOOMFACTOR) + 165));
            g.setColor(new Color(54, 150, 39));
            g.drawString("Range: " + (int) TurretTypes.Basic.range, x, (int) ((HEIGHT * ZOOMFACTOR) + 180));
            g.setColor(new Color(183, 164, 0));
            g.drawString("Recharge: " + (int) TurretTypes.Basic.chargeTime, x, (int) ((HEIGHT * ZOOMFACTOR) + 195));
            g.setColor(new Color(81, 128, 194));
            g.drawString("Efficiency: " + (int) TurretTypes.Basic.efficiency, x, (int) ((HEIGHT * ZOOMFACTOR) + 210));

            x += 110;
            g.setFont(new Font("Monospaced", Font.PLAIN, 11).deriveFont(createCharSpacing(-0.08f))); // fire turret
            g.setColor(new Color(213, 213, 213));
            g.drawString("Power: " + (int) TurretTypes.Fire.power, x, (int) ((HEIGHT * ZOOMFACTOR) + 165));
            g.setColor(new Color(54, 150, 39));
            g.drawString("Range: " + (int) TurretTypes.Fire.range, x, (int) ((HEIGHT * ZOOMFACTOR) + 180));
            g.setColor(new Color(183, 164, 0));
            g.drawString("Recharge: " + (int) TurretTypes.Fire.chargeTime, x, (int) ((HEIGHT * ZOOMFACTOR) + 195));
            g.setColor(new Color(81, 128, 194));
            g.drawString("Efficiency: " + (int) TurretTypes.Fire.efficiency, x, (int) ((HEIGHT * ZOOMFACTOR) + 210));

            x += 110;
            g.setFont(new Font("Monospaced", Font.PLAIN, 11).deriveFont(createCharSpacing(-0.08f))); // laser turret
            g.setColor(new Color(213, 213, 213));
            g.drawString("Power: " + (int) TurretTypes.Laser.power, x, (int) ((HEIGHT * ZOOMFACTOR) + 165));
            g.setColor(new Color(54, 150, 39));
            g.drawString("Range: " + (int) TurretTypes.Laser.range, x, (int) ((HEIGHT * ZOOMFACTOR) + 180));
            g.setColor(new Color(183, 164, 0));
            g.drawString("Recharge: " + (int) TurretTypes.Laser.chargeTime, x, (int) ((HEIGHT * ZOOMFACTOR) + 195));
            g.setColor(new Color(81, 128, 194));
            g.drawString("Efficiency: " + (int) TurretTypes.Laser.efficiency, x, (int) ((HEIGHT * ZOOMFACTOR) + 210));

            x += 110;
            g.setFont(new Font("Monospaced", Font.PLAIN, 11).deriveFont(createCharSpacing(-0.08f))); // debuff turret
            g.setColor(new Color(213, 213, 213));
            g.drawString("Power: " + (int) TurretTypes.Debuff.power, x, (int) ((HEIGHT * ZOOMFACTOR) + 165));
            g.setColor(new Color(54, 150, 39));
            g.drawString("Range: " + (int) TurretTypes.Debuff.range, x, (int) ((HEIGHT * ZOOMFACTOR) + 180));
            g.setColor(new Color(183, 164, 0));
            g.drawString("Recharge: " + (int) TurretTypes.Debuff.chargeTime, x, (int) ((HEIGHT * ZOOMFACTOR) + 195));
            g.setColor(new Color(81, 128, 194));
            g.drawString("Efficiency: " + (int) TurretTypes.Debuff.efficiency, x, (int) ((HEIGHT * ZOOMFACTOR) + 210));

            x += 110;
            g.setFont(new Font("Monospaced", Font.PLAIN, 11).deriveFont(createCharSpacing(-0.08f))); // splash turret
            g.setColor(new Color(213, 213, 213));
            g.drawString("Power: " + (int) TurretTypes.Splash.power, x, (int) ((HEIGHT * ZOOMFACTOR) + 165));
            g.setColor(new Color(54, 150, 39));
            g.drawString("Range: " + (int) TurretTypes.Splash.range, x, (int) ((HEIGHT * ZOOMFACTOR) + 180));
            g.setColor(new Color(183, 164, 0));
            g.drawString("Recharge: " + (int) TurretTypes.Splash.chargeTime, x, (int) ((HEIGHT * ZOOMFACTOR) + 195));
            g.setColor(new Color(81, 128, 194));
            g.drawString("Efficiency: " + (int) TurretTypes.Splash.efficiency, x, (int) ((HEIGHT * ZOOMFACTOR) + 210));


            //stat display
            g.setColor(Color.BLACK);
            g.setFont(new Font("Monospaced", Font.BOLD, 40));
//           g.drawString("Wave " + WAVE, 20, (int) ((HEIGHT * ZOOMFACTOR) + 20) + 30);

            rightJustify(280, (int) (((HEIGHT * ZOOMFACTOR) + 20) + 30), "Wave " + WAVE + "/16", g);
            rightJustify(240, (int) (((HEIGHT * ZOOMFACTOR) + 20) + 70), HEALTH + "", g);
            rightJustify(240, (int) (((HEIGHT * ZOOMFACTOR) + 20) + 110), CREDITS + "", g);
            rightJustify(240, (int) (((HEIGHT * ZOOMFACTOR) + 20) + 150), ENERGY + "/" + maxEnergy, g);
            rightJustify(240, (int) (((HEIGHT * ZOOMFACTOR) + 20) + 190), energy_generation + "", g);

            g.drawString("♡", 247, (int) ((HEIGHT * ZOOMFACTOR) + 20) + 70);
            g.drawString(CREDITS_SYMBOL, 245, (int) ((HEIGHT * ZOOMFACTOR) + 20) + 110);
            g.drawString("⚡", 250, (int) ((HEIGHT * ZOOMFACTOR) + 20) + 150);

            g.setFont(new Font("Monospaced", Font.BOLD, 20));
            g.drawString("⚡/s", 250, (int) ((HEIGHT * ZOOMFACTOR) + 20) + 185);

            g.setFont(new Font("Monospaced", Font.BOLD, 20));
            g.drawString("FPS: " + ((round(FPS * 100f)) / 100), 20, HEIGHT - 20);

            //sidebar
            g.setColor(new Color(56, 56, 56));
            g.fillRect((int) (WIDTH * ZOOMFACTOR), 0, sidebarWidth, HEIGHT);

            //sidebar enemy catalog
            RescaleOp rescaleOp = new RescaleOp(0f, 0, null);

            g.setColor(new Color(51, 41, 32));
            g.fillRect((int) (WIDTH * ZOOMFACTOR) + 10, 700, sidebarWidth - 20, 295);
            g.setColor(new Color(23, 13, 2));
            g.setFont(new Font("Monospaced", Font.BOLD, 40));
            g.drawRect((int) (WIDTH * ZOOMFACTOR) + 10, 700, sidebarWidth - 20, 295);
            g.drawString("Ship Catalog", (int) (WIDTH * ZOOMFACTOR) + 20, 745);
            for (int x2 = 0; x2 < 5; x2++) {
                if (catalogIdx != x2) {
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                }
                if (discoveredEnemies[x2]) {
                    g.drawImage(scaledImages.get(5 + x2), (int) (WIDTH * ZOOMFACTOR) + 20 + x2 * 90, x2 == 4 ? 755 : 770, null);
                } else {
                    g.drawImage(rescaleOp.filter(scaledImages.get(5 + x2), null), (int) (WIDTH * ZOOMFACTOR) + 20 + x2 * 90, x2 == 4 ? 755 : 770, null);
                }
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
                if (mouseIn(mouseX, mouseY, (int) (WIDTH * ZOOMFACTOR) + 20 + x2 * 90, 770, scaledImages.get(5 + x2).getWidth(), scaledImages.get(5 + x2).getHeight())) {
                    if (mouseDown) {
                        mouseDown = false;
                        catalogIdx = x2;
                        SoundManager.startSound("click.wav");
                    }
                }
            }
            g.setColor(new Color(176, 176, 176));
            g.setColor(new Color(23, 13, 2));
            if (catalogIdx != -1) {
                if (discoveredEnemies[catalogIdx]) {
                    g.setFont(new Font("Monospaced", Font.BOLD, 30));
                    switch (catalogIdx) {
                        case EnemyTypes.BASIC -> {
                            g.drawString("Drone ship", (int) (WIDTH * ZOOMFACTOR) + 20, 830);
                            g.setFont(new Font("Monospaced", Font.BOLD, 20));
                            drawStringWithLineBreaks(g, "This is the basic drone used by \nthe pirates.", (int) (WIDTH * ZOOMFACTOR) + 20, 840);
                        }
                        case EnemyTypes.ARMOURED -> {
                            g.drawString("Armoured ship", (int) (WIDTH * ZOOMFACTOR) + 20, 830);
                            g.setFont(new Font("Monospaced", Font.BOLD, 20));
                            drawStringWithLineBreaks(g, "With armoured plating at the bow, \nthese drones can only be damaged \nif shot from behind or if they get \ncaught within the blast radius of \na splash turret projectile.", (int) (WIDTH * ZOOMFACTOR) + 20, 840);
                        }
                        case EnemyTypes.SPEED -> {
                            g.drawString("Speedy ship", (int) (WIDTH * ZOOMFACTOR) + 20, 830);
                            g.setFont(new Font("Monospaced", Font.BOLD, 20));
                            drawStringWithLineBreaks(g, "Less robust than a standard drone \nship, but moves much faster.", (int) (WIDTH * ZOOMFACTOR) + 20, 840);
                        }
                        case EnemyTypes.STEALTH -> {

                            g.drawString("Stealth ship", (int) (WIDTH * ZOOMFACTOR) + 20, 830);
                            g.setFont(new Font("Monospaced", Font.BOLD, 20));
                            drawStringWithLineBreaks(g, "While cloaked, it can only be \ntargeted by a Debuff Turret. \nEven while debuffed, other turrets \nhave difficulty tracking it.", (int) (WIDTH * ZOOMFACTOR) + 20, 840);
                        }
                        case EnemyTypes.MOTHERSHIP -> {
                            g.drawString("Mothership", (int) (WIDTH * ZOOMFACTOR) + 20, 830);
                            g.setFont(new Font("Monospaced", Font.BOLD, 20));
                            drawStringWithLineBreaks(g, "A large fortified drone ship with \na lot of health. It is always \nescorted by several armoured ships.", (int) (WIDTH * ZOOMFACTOR) + 20, 840);
                        }
                    }
                } else {
                    g.setFont(new Font("Monospaced", Font.BOLD, 30));
                    g.drawString("Undiscovered ship", (int) (WIDTH * ZOOMFACTOR) + 20, 830);
                }
            }

            if (selectedTurretId != -1) {
                Turret selectedTurret = getTurretById(selectedTurretId);
                g.setColor(TurretTypes.getTurretColor(selectedTurret.getType()));
                g.setFont(new Font("Monospaced", Font.BOLD, 50));
                g.drawString(selectedTurret.getName(), (int) (WIDTH * ZOOMFACTOR) + 10, 40);
                g.setColor(new Color(0, 0, 0));
                g.setFont(new Font("Monospaced", Font.BOLD, 30));
                g.drawString(selectedTurret.isActive() ? "Active" : "Inactive", (int) (WIDTH * ZOOMFACTOR) + 10, 80);
                if (selectedTurret.getPower() < 10) {
                    g.drawString("Power:       " + selectedTurret.getPower(), (int) (WIDTH * ZOOMFACTOR) + 10, 120);
                } else {
                    g.drawString("Power:      " + selectedTurret.getPower(), (int) (WIDTH * ZOOMFACTOR) + 10, 120);
                }
                if (selectedTurret.getRange() < 10) {
                    g.drawString("Range:       " + selectedTurret.getRange(), (int) (WIDTH * ZOOMFACTOR) + 10, 150);
                } else {
                    g.drawString("Range:      " + selectedTurret.getRange(), (int) (WIDTH * ZOOMFACTOR) + 10, 150);
                }
                if (selectedTurret.getChargeTime() < 10) {
                    g.drawString("Recharge:    " +
                            "" + (round(selectedTurret.getChargeTime() * 1000f) / 1000f), (int) (WIDTH * ZOOMFACTOR) + 10, 180);
                } else {
                    if (selectedTurret.getChargeTime() < 100) {
                        g.drawString("Recharge:   " + (round(selectedTurret.getChargeTime() * 1000f) / 1000f), (int) (WIDTH * ZOOMFACTOR) + 10, 180);
                    } else {
                        g.drawString("Recharge:  " + (round(selectedTurret.getChargeTime() * 1000f) / 1000f), (int) (WIDTH * ZOOMFACTOR) + 10, 180);
                    }
                }
                g.drawString("Efficiency:  " + selectedTurret.getEfficiency(), (int) (WIDTH * ZOOMFACTOR) + 10, 210);

                //upgrade buttons
//                g.setFont(new Font("Monospaced", Font.BOLD, 50));
                g.drawString("Upgrade Options", (int) (WIDTH * ZOOMFACTOR) + 10, 280);
                g.setFont(new Font("Monospaced", Font.BOLD, 30));

                Color bgCol;

                if (selectedTurret.getType() == TurretTypes.DEBUFF) {
                    bgCol = new Color(213, 213, 213, 131);
                } else {
                    bgCol = new Color(213, 213, 213);
                }
                g.setColor(bgCol.darker());
                g.fillRect((int) (WIDTH * ZOOMFACTOR) + 10, 300, sidebarWidth - 20, 70);
                if (mouseIn(mouseX, mouseY, (int) (WIDTH * ZOOMFACTOR) + 10, 300, sidebarWidth - 20, 70)) { //upgrade power button
                    if (selectedTurret.getType() == TurretTypes.DEBUFF) {
                        drawTooltip(mouseX, mouseY, "Debuff turrets do not deal\ndamage");
                    } else {
                        g.setColor(CREDITS >= (baseUpgradeCost * (selectedTurret.getPowerLevel() + 2) * upgradeCostScale) ? new Color(255, 255, 255, 92) : new Color(0, 0, 0, 92));
                        g.drawRect((int) (WIDTH * ZOOMFACTOR) + 10, 300, sidebarWidth - 20, 70);
                        g.setColor(bgCol.brighter());
                        if (mouseDown) {
                            mouseDown = false;
                            if ((CREDITS >= (baseUpgradeCost * (selectedTurret.getPowerLevel() + 2) * upgradeCostScale) || FREE) && selectedTurret.getPowerLevel() < 10) {
                                if (!FREE) {
                                    CREDITS -= (baseUpgradeCost * (selectedTurret.getPowerLevel() + 2) * upgradeCostScale);
                                    StatTracker.energySpent += (baseUpgradeCost * (selectedTurret.getPowerLevel() + 2) * upgradeCostScale);
                                }
                                getTurretById(selectedTurretId).upgradePower();
                                upgradeEffect(true);
                            } else {
                                upgradeEffect(false);
                            }
                        }
                    }
                } else {
                    g.setColor(bgCol);
                }

                g.fillRect((int) (WIDTH * ZOOMFACTOR) + 10, 300, (int) (selectedTurret.getPowerLevel() / 10f * (sidebarWidth - 20)), 70);


                bgCol = new Color(54, 150, 39);
                g.setColor(bgCol.darker());
                g.fillRect((int) (WIDTH * ZOOMFACTOR) + 10, 380, sidebarWidth - 20, 70);
                if (mouseIn(mouseX, mouseY, (int) (WIDTH * ZOOMFACTOR) + 10, 380, sidebarWidth - 20, 70)) { //upgrade range button
                    g.setColor(CREDITS >= (baseUpgradeCost * (selectedTurret.getRangeLevel() + 2) * upgradeCostScale) ? new Color(255, 255, 255, 92) : new Color(0, 0, 0, 92));
                    g.drawRect((int) (WIDTH * ZOOMFACTOR) + 10, 380, sidebarWidth - 20, 70);
                    g.setColor(bgCol.brighter());
                    if (mouseDown) {
                        mouseDown = false;
                        if ((CREDITS >= (baseUpgradeCost * (selectedTurret.getRangeLevel() + 2) * upgradeCostScale) || FREE) && selectedTurret.getRangeLevel() < 10) {
                            if (!FREE) {
                                CREDITS -= (baseUpgradeCost * (selectedTurret.getRangeLevel() + 2) * upgradeCostScale);
                                StatTracker.creditsSpent += (baseUpgradeCost * (selectedTurret.getRangeLevel() + 2) * upgradeCostScale);
                            }
                            getTurretById(selectedTurretId).upgradeRange();
                            upgradeEffect(true);
                        } else {
                            upgradeEffect(false);
                        }
                    }
                } else {
                    g.setColor(bgCol);
                }

                g.fillRect((int) (WIDTH * ZOOMFACTOR) + 10, 380, (int) (selectedTurret.getRangeLevel() / 10f * (sidebarWidth - 20)), 70);

                bgCol = new Color(183, 164, 0);
                g.setColor(bgCol.darker());
                g.fillRect((int) (WIDTH * ZOOMFACTOR) + 10, 460, sidebarWidth - 20, 70);
                if (mouseIn(mouseX, mouseY, (int) (WIDTH * ZOOMFACTOR) + 10, 460, sidebarWidth - 20, 70)) { //upgrade recharge button
                    g.setColor(CREDITS >= (baseUpgradeCost * (selectedTurret.getRechargeLevel() + 2) * upgradeCostScale) ? new Color(255, 255, 255, 92) : new Color(0, 0, 0, 92));
                    g.drawRect((int) (WIDTH * ZOOMFACTOR) + 10, 460, sidebarWidth - 20, 70);
                    g.setColor(bgCol.brighter());
                    if (mouseDown) {
                        mouseDown = false;
                        if ((CREDITS >= (baseUpgradeCost * (selectedTurret.getRechargeLevel() + 2) * upgradeCostScale) || FREE) && selectedTurret.getRechargeLevel() < 10) {
                            if (!FREE) {
                                CREDITS -= (baseUpgradeCost * (selectedTurret.getRechargeLevel() + 2) * upgradeCostScale);
                                StatTracker.creditsSpent += (baseUpgradeCost * (selectedTurret.getRechargeLevel() + 2) * upgradeCostScale);
                            }
                            getTurretById(selectedTurretId).upgradeChargeTime();
                            upgradeEffect(true);
                        } else {
                            upgradeEffect(false);
                        }
                    }
                } else {
                    g.setColor(bgCol);
                }

                g.fillRect((int) (WIDTH * ZOOMFACTOR) + 10, 460, (int) (selectedTurret.getRechargeLevel() / 10f * (sidebarWidth - 20)), 70);

                bgCol = new Color(47, 107, 185);
                g.setColor(bgCol.darker());
                g.fillRect((int) (WIDTH * ZOOMFACTOR) + 10, 540, sidebarWidth - 20, 70);
                if (mouseIn(mouseX, mouseY, (int) (WIDTH * ZOOMFACTOR) + 10, 540, sidebarWidth - 20, 70)) { //upgrade efficiency button
                    g.setColor(CREDITS >= (baseUpgradeCost * (selectedTurret.getEffLevel() + 2) * upgradeCostScale) ? new Color(255, 255, 255, 92) : new Color(0, 0, 0, 92));
                    g.drawRect((int) (WIDTH * ZOOMFACTOR) + 10, 540, sidebarWidth - 20, 70);
                    g.setColor(bgCol.brighter());
                    if (mouseDown) {
                        mouseDown = false;
                        if ((CREDITS >= (baseUpgradeCost * (selectedTurret.getEffLevel() + 2) * upgradeCostScale) || FREE) && selectedTurret.getEffLevel() < 10) {
                            if (!FREE) {
                                CREDITS -= (baseUpgradeCost * (selectedTurret.getEffLevel() + 2) * upgradeCostScale);
                                StatTracker.creditsSpent += (baseUpgradeCost * (selectedTurret.getEffLevel() + 2) * upgradeCostScale);
                            }
                            getTurretById(selectedTurretId).upgradeEff();
                            upgradeEffect(true);
                        } else {
                            upgradeEffect(false);
                        }
                    }
                } else {
                    g.setColor(bgCol);
                }

                g.fillRect((int) (WIDTH * ZOOMFACTOR) + 10, 540, (int) (selectedTurret.getEffLevel() / 10f * (sidebarWidth - 20)), 70);

                bgCol = new Color(168, 12, 12);
                if (mouseIn(mouseX, mouseY, (int) (WIDTH * ZOOMFACTOR) + 10, 620, sidebarWidth - 20, 70)) { //scrap button
                    g.setColor(new Color(255, 255, 255, 92));
                    g.drawRect((int) (WIDTH * ZOOMFACTOR) + 10, 620, sidebarWidth - 20, 70);
                    g.setColor(bgCol.brighter().brighter());
                    if (mouseDown) {
                        mouseDown = false;
                        CREDITS += round((double) selectedTurret.getValue() * SELLFACTOR);
                        turrets.remove(getTurretById(selectedTurretId));
                        selectedTurretId = -1;
                    }
                } else {
                    g.setColor(bgCol);
                }

                g.fillRect((int) (WIDTH * ZOOMFACTOR) + 10, 620, sidebarWidth - 20, 70);

                if (selectedTurretId != -1) {
                    g.setFont(new Font("Monospaced", Font.BOLD, 25));

                    if (selectedTurret.getType() == TurretTypes.DEBUFF) {
                        g.setColor(new Color(0, 0, 0, 74));
                    } else {
                        g.setColor(new Color(0, 0, 0));
                    }

                    if (selectedTurret.getPowerLevel() < 10) {
                        g.drawString("Increase power to " + (int) (100 + (getTurretById(selectedTurretId).getPowerLevel() + 2) * 10) + "%", (int) (WIDTH * ZOOMFACTOR) + 20, 330);
                        g.drawString("Cost: " + round(baseUpgradeCost * (selectedTurret.getPowerLevel() + 2) * upgradeCostScale) + CREDITS_SYMBOL, (int) (WIDTH * ZOOMFACTOR) + 20, 360);
                    } else {
                        g.drawString("Maxed power upgrades!", (int) (WIDTH * ZOOMFACTOR) + 20, 330);
                    }

                    g.setColor(new Color(0, 0, 0));
                    if (selectedTurret.getRangeLevel() < 10) {
                        g.drawString("Increase range to " + (int) (100 + (getTurretById(selectedTurretId).getRangeLevel() + 2) * 10) + "%", (int) (WIDTH * ZOOMFACTOR) + 20, 410);
                        g.drawString("Cost: " + round(baseUpgradeCost * (selectedTurret.getRangeLevel() + 2) * upgradeCostScale) + CREDITS_SYMBOL, (int) (WIDTH * ZOOMFACTOR) + 20, 440);
                    } else {
                        g.drawString("Maxed range upgrades!", (int) (WIDTH * ZOOMFACTOR) + 20, 410);
                    }

                    if (selectedTurret.getRechargeLevel() < 10) {
                        g.drawString("Decrease recharge time to " + (int) (100 - (getTurretById(selectedTurretId).getRechargeLevel() + 2) * 5) + "%", (int) (WIDTH * ZOOMFACTOR) + 20, 490);
                        g.drawString("Cost: " + round(baseUpgradeCost * (selectedTurret.getRechargeLevel() + 2) * upgradeCostScale) + CREDITS_SYMBOL, (int) (WIDTH * ZOOMFACTOR) + 20, 520);
                    } else {
                        g.drawString("Maxed recharge upgrades!", (int) (WIDTH * ZOOMFACTOR) + 20, 490);
                    }

                    if (selectedTurret.getEffLevel() < 10) {
                        g.drawString("Increase efficiency to " + (int) (100 + (getTurretById(selectedTurretId).getEffLevel() + 2) * 10) + "%", (int) (WIDTH * ZOOMFACTOR) + 20, 570);
                        g.drawString("Cost: " + round(baseUpgradeCost * (selectedTurret.getEffLevel() + 2) * upgradeCostScale) + CREDITS_SYMBOL, (int) (WIDTH * ZOOMFACTOR) + 20, 600);
                    } else {
                        g.drawString("Maxed efficiency upgrades!", (int) (WIDTH * ZOOMFACTOR) + 20, 570);
                    }

                    g.drawString("Sell turret for " + round((double) selectedTurret.getValue() * SELLFACTOR) + " Credits", (int) (WIDTH * ZOOMFACTOR) + 20, 650);

                }

            }

        }
        if (visibleGamemode == 3) {
            g.setColor(new Color(176, 176, 176));
            g.fillRect(WIDTH - 200, 0, 200, 120);
            g.setColor(new Color(126, 126, 126));
            g.drawRect(WIDTH - 200, 0, 200, 120);

            //ingame stat display
            g.setColor(new Color(0, 0, 0));
            g.setFont(new Font("Monospaced", Font.BOLD, 30).deriveFont(createCharSpacing(-0.07f)));
            g.drawString("Wave " + WAVE, WIDTH - 190, 30);
            g.drawString(CREDITS + " " + CREDITS_SYMBOL, WIDTH - 190, 60);
            g.drawString(HEALTH + " ♡", WIDTH - 190, 85);
            g.drawString(ENERGY + "/" + maxEnergy + " ⚡", WIDTH - 190, 110);

            //warnings
            if (((double) ENERGY / (double) maxEnergy) < 0.2f) {
                g.setColor(new Color(224, 0, 0, (int) (((sin(TICK / 3f) + 1) / 2) * 155) + 100));
                g.setFont(warningFont);
                FontMetrics metrics = g.getFontMetrics(warningFont);

                if (ENERGY == 0) {
                    g.drawString("NO ENERGY!", (WIDTH / 2f) - (metrics.stringWidth("NO ENERGY!") / 2f), 1050);
                } else {
                    g.drawString("ENERGY LEVEL CRITICAL!", (WIDTH / 2f) - (metrics.stringWidth("ENERGY LEVEL CRITICAL!") / 2f), 1050);
                }
            }
            if ((double) HEALTH / (double) maxHealth < 0.2f) {
                g.setColor(new Color(224, 0, 0, (int) (((sin(TICK / 3f) + 1) / 2) * 155) + 100));
                g.setFont(warningFont);
                FontMetrics metrics = g.getFontMetrics(warningFont);


                g.drawString("BASE STRUCTURAL INTEGRITY FAILING!", (WIDTH / 2f) - (metrics.stringWidth("BASE STRUCTURAL INTEGRITY FAILING!") / 2f), 100);

            }

        }

        //start button
        if (visibleGamemode == 2 || (visibleGamemode == 3 && waveDone)) {
            g.setFont(new Font("Monospaced", Font.BOLD, 24));
            if (visibleGamemode != 3) {
                if (mouseIn(mouseX, mouseY, WIDTH - 185, HEIGHT - 75, 165, 55)) {
                    g.setColor(new Color(15, 133, 9).brighter());
                    if (mouseDown) {
                        mouseDown = false;
                        WAVE++;
                        createWave(WAVE);
                        if (visibleGamemode != 3) {
                            SoundManager.setTrack(SoundManager.FIGHT);
                        }
                        setGamemode(3);
                    }
                } else {
                    g.setColor(new Color(15, 133, 9));
                }
                g.fillRoundRect(WIDTH - 185, HEIGHT - 75, 165, 55, 5, 5);
                g.setColor(Color.BLACK);
                g.drawRoundRect(WIDTH - 185, HEIGHT - 75, 165, 55, 5, 5);

                g.drawString("START WAVE", WIDTH - 173, HEIGHT - 42);
            }

            //return button
            if (visibleGamemode == 3 && waveDone) {
                if (mouseIn(mouseX, mouseY, WIDTH - 145, HEIGHT - 75, 125, 55)) {
                    g.setColor(new Color(44, 100, 208).brighter());
                    if (mouseDown) {
                        mouseDown = false;
                        setGamemode(2);
                        SoundManager.startSound("select.wav");
                        if (currentTip == 3) {
                            currentTip = 4;
                            showingTip = true;
                        }
                        if (currentTip == 2) {
                            currentTip = 3;
                            showingTip = true;
                        }
                    }
                } else {
                    g.setColor(new Color(44, 100, 208));
                }
                g.fillRoundRect(WIDTH - 145, HEIGHT - 75, 125, 55, 5, 5);
                g.setColor(Color.BLACK);
                g.drawRoundRect(WIDTH - 145, HEIGHT - 75, 125, 55, 5, 5);
                g.drawString("RETURN", WIDTH - 126, HEIGHT - 42);
            }
        }
        //draw tooltips
        for (Tooltip tooltip : tooltips) {
            g.setColor(new Color(59, 59, 59, 221));
            g.fillRect(tooltip.getX(), tooltip.getY(), tooltip.getWidth(), tooltip.getHeight());
            g.setColor(new Color(0, 0, 0, 205));
            g.drawRect(tooltip.getX(), tooltip.getY(), tooltip.getWidth(), tooltip.getHeight());
            g.setFont(new Font("Monospaced", Font.PLAIN, 12));
            g.setColor(new Color(0, 0, 0));
            drawStringWithLineBreaks(g, tooltip.getText(), tooltip.getX() + 5, tooltip.getY());
        }

        if (visibleGamemode == 4) {
            g.setColor(new Color(0, 0, 0, (Math.min(TICK - gameEndTick, 200))));
            g.fillRect(0, 0, WIDTH, HEIGHT);
            FontMetrics metrics = g.getFontMetrics(new Font("Monospaced", Font.BOLD, 260));
            g.setFont(new Font("Monospaced", Font.BOLD, 260));
            if (WONGAME) {
                g.setColor(new Color(0, 255, 0, Math.min(TICK - gameEndTick, 255)));
                g.drawString("VICTORY", (WIDTH - metrics.stringWidth("VICTORY")) / 2, HEIGHT / 3);
            } else {
                g.setColor(new Color(255, 0, 0, Math.min(TICK - gameEndTick, 255)));
                g.drawString("DEFEATED", (WIDTH - metrics.stringWidth("DEFEATED")) / 2, HEIGHT / 3);
            }
            //stat display
            g.setColor(new Color(189, 189, 189, Math.min(TICK - gameEndTick, 255)));
            g.setFont(new Font("Monospaced", Font.BOLD, 40));
            //metrics = g.getFontMetrics(new Font("Monospaced", Font.BOLD, 40));

            // Score is based on final wave, average damage per turret (typically around 3000), and average damage per energy (typically around 0.7).
            SCOREwave = (double) (WAVE * WAVE); // Ranging from 1 to 256 points
            SCOREturret = (double) StatTracker.damageDealt / (double) StatTracker.turretsPlaced / 15; // So typically about 200 points
            SCOREenergy = (double) StatTracker.damageDealt / (double) StatTracker.energySpent * 250; // So typically about 175 points
            SCOREhealth = 0;
            SCOREtime = 0;
            if (WONGAME) { // If won game, score is also based on remaining base health and time spent
                SCOREhealth = (double) HEALTH;
                SCOREtime = (double) max(0, 2 * ((12 * 60) - (Math.floor(StatTracker.gameTime / 1000d)))); // bonus if completed in less than 12 minutes
            }

            SCORE = SCOREwave + SCOREturret + SCOREenergy + SCOREhealth + SCOREtime;

            int column1x = 250, column2x = 1000;

            g.drawString("Remaining base health: " + max(0, HEALTH), column1x, HEIGHT / 3 + 70);
            g.drawString("Last wave played:      " + WAVE + "/16", column1x, HEIGHT / 3 + 110);
            g.drawString("Time spent in battle:  " + (int) (Math.floor((StatTracker.gameTime / 1000d) / 60)) + "m " + (int) ((StatTracker.gameTime / 1000d) % 60) + "s", column1x, HEIGHT / 3 + 150);
            g.drawString("Total damage dealt:    " + StatTracker.damageDealt, column1x, HEIGHT / 3 + 190);

            g.drawString("Turrets placed:          " + StatTracker.turretsPlaced, column2x, HEIGHT / 3 + 70);
            g.drawString("Average turret damage:   " + StatTracker.avgTurretDamage, column2x, HEIGHT / 3 + 110);
            g.drawString("Energy spent:            " + StatTracker.energySpent, column2x, HEIGHT / 3 + 150);
            g.drawString("Damage per energy spent: " + StatTracker.damagePerEnergy, column2x, HEIGHT / 3 + 190);

//            g.drawString("Ovoidium spent: " + StatTracker.creditsSpent, 300, HEIGHT / 3 + 510);
//            g.drawString("Crystals collected: " + StatTracker.gemsCollected, 300, HEIGHT / 3 + 550);

            g.drawString("SCORE BREAKDOWN", column1x, HEIGHT / 3 + 270);
            g.drawString("For last wave played:         " + (int) (SCOREwave), column1x, HEIGHT / 3 + 310);
            g.drawString("For damage/turret efficiency: " + (int) (SCOREturret), column1x, HEIGHT / 3 + 350);
            g.drawString("For damage/energy efficiency: " + (int) (SCOREenergy), column1x, HEIGHT / 3 + 390);
            g.drawString("For remaining base health:    " + (int) (SCOREhealth), column1x, HEIGHT / 3 + 430);
            g.drawString("Victory time bonus (if <12m): " + (int) (SCOREtime), column1x, HEIGHT / 3 + 470);
            g.drawString("FINAL SCORE: " + (int) (SCORE), column1x, HEIGHT / 3 + 550);
            g.drawString("RATING:      " + SCOREcomment, column1x, HEIGHT / 3 + 590);

            if (SCORE >= 1800) {
                g.setColor(new Color(240, 0, 0, Math.min(TICK - gameEndTick, 255)));
                SCOREcomment = "★★★★★ Turret God";
            } else {
                if (SCORE >= 1500) {
                    g.setColor(new Color(240, 160, 0, Math.min(TICK - gameEndTick, 255)));
                    SCOREcomment = "★★★★☆ Turret Savant";
                } else {
                    if (SCORE >= 1000) {
                        g.setColor(new Color(255, 215, 0, Math.min(TICK - gameEndTick, 255)));
                        SCOREcomment = "★★★☆☆ Turret Enjoyer";
                    } else {
                        if (SCORE >= 500) {
                            g.setColor(new Color(0, 240, 0, Math.min(TICK - gameEndTick, 255)));
                            SCOREcomment = "★★☆☆☆ Turret Amateur";
                        } else {
                            g.setColor(new Color(0, 240, 240, Math.min(TICK - gameEndTick, 255)));
                            SCOREcomment = "★☆☆☆☆ Turret Novice";
                        }
                    }
                }
            }
            g.drawString("             " + (int) (SCORE), column1x, HEIGHT / 3 + 550);
            g.drawString("             " + SCOREcomment, column1x, HEIGHT / 3 + 590);


            if (mouseIn(mouseX, mouseY, 1200, HEIGHT / 3 + 270, 315, 100)) {
                g.setColor(new Color(28, 157, 34, Math.min(TICK - gameEndTick, 255)));
                if (mouseDown) {
                    mouseDown = false;
                    setGamemode(0);
                    resetGame();
                }
            } else {
                g.setColor(new Color(0, 100, 5, Math.min(TICK - gameEndTick, 255)));
            }
            g.fillRect(1200, HEIGHT / 3 + 270, 315, 100);
            g.setColor(new Color(0, 0, 0, Math.min(TICK - gameEndTick, 255)));
            g.setFont(new Font("Monospaced", Font.BOLD, 68));
            g.drawString("Restart", 1215, HEIGHT / 3 + 340);

            if (mouseIn(mouseX, mouseY, 1200, HEIGHT / 3 + 380, 282, 100)) {
                g.setColor(new Color(155, 14, 14, Math.min(TICK - gameEndTick, 255)));
                if (mouseDown) {
                    setGamemode(5);
                }
            } else {
                g.setColor(new Color(103, 0, 0, Math.min(TICK - gameEndTick, 255)));
            }
            g.fillRect(1200, HEIGHT / 3 + 380, 282, 100);
            g.setColor(new Color(0, 0, 0, Math.min(TICK - gameEndTick, 255)));
            g.drawString("Retire", 1215, HEIGHT / 3 + 450);

        }

        if ((visibleGamemode == 2 || visibleGamemode == 3) && showingTip && tutorialEnabled && !fadingScreen) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
            g.setFont(new Font("Monospaced", Font.BOLD, 20));
            g.setColor(new Color(172, 105, 255));
            switch (currentTip) {
                case 0 -> {
                    g.fillRect(490, 510, 880, 280);
                    g.setColor(new Color(0, 0, 0));
                    drawStringWithLineBreaks(g, "This game is largely about strategically placing and upgrading your \nturrets. A total of 16 enemy waves of increasing intensity will fly in \nfrom the left side of the screen. Although turrets cannot shoot through \nasteroid fields, the pirate drones can travel through them (at a \nreduced speed), so choose your placements wisely! To begin, select a \nBasic Turret from the section below and move the mouse over the grid \nabove. You will see a circle showing the targeting range of the turret. \nFind a suitable location and click the left mouse button to permanently \nplace the turret. You may repeat this procedure until you run out of \nOvoidium (see " + CREDITS_SYMBOL + " at bottom left panel of screen).", 500, 510);
                }

                case 1 -> {
                    g.fillRect(930, 70, 440, 720);
                    g.setColor(new Color(0, 0, 0));
                    drawStringWithLineBreaks(g, "After a turret is placed, clicking \non it will toggle the display of \nthe parameters info (on the right) \nfor that particular turret:\n\nPower:      Weapon damage \nRange:      Targeting range \nRecharge:   Time needed to reload \nEfficiency: Energy efficiency of \n            the turret\n\nThese parameters can be upgraded \nby clicking on the coloured buttons \non the right. You may also sell \na turret and recover half of the \nOvoidium that was used in its \nconstruction (including upgrades).\n\n(Hint: Early in the game, it is \nmore cost effective to use your \nlimited resources for installing \nmore turrets than for upgrading \nexisting ones.)\n\nPress the green START WAVE button \nwhen you are ready for the pirates!", 940, 70);
                }

                case 2 -> {
                    g.fillRect(20, 300, 665, 30);
                    g.setColor(new Color(0, 0, 0));
                    drawStringWithLineBreaks(g, "Collect crystals by moving your mouse cursor over them.", 25, 295);
                }

                case 3 -> {
                    g.fillRect(30, 645, 535, 150);
                    g.fillRect(575, 700, 840, 95);
                    g.fillRect(1520, 900, 360, 85);
                    g.setColor(new Color(0, 0, 0));
                    drawStringWithLineBreaks(g, "Base upgrades (yellow buttons below) can \nimprove your overall effectiveness: \nBattery:   Energy storage for turrets \nGenerator: Battery recharge rate \nSalvaging: Debris salvaging effectiveness", 40, 645);
                    drawStringWithLineBreaks(g, "In addition to Ovoidium, it costs 10 crystals of a particular colour \nto construct a non-basic turret. Hover over the turret selections \nbelow to see what they can do.", 585, 700);
                    drawStringWithLineBreaks(g, "Click on a ship that you have \npreviously destroyed to learn \nmore about it.", 1525, 895);
                }

                case 4 -> {
                    g.fillRect(500, 570, 750, 230);
                    g.setColor(new Color(0, 0, 0));
                    drawStringWithLineBreaks(g, "Turrets will always shoot towards the closest enemy ship. \nTo conserve energy when your battery is low, you will want \nto command particular turrets to temporarily stop shooting. \nRight-clicking turrets during a battle will toggle them \noff and on. (Hint: you can keep the right button depressed \nand sweep over a bunch of turrets at a time.)\n\nThis is the last tutorial screen, so Good Luck and Have Fun!", 510, 570);
                }
            }
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        }

    }

    public void resetGame() throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        StatTracker.resetStats();

        TICK = 0;
        CREDITS = 100;
        maxHealth = 1000;
        HEALTH = maxHealth;
        energy_generation = 200; //200
        maxEnergy = 4000; //4000
        ENERGY = maxEnergy;
        maxEnergyUpgrade = 0;
        energyRegenUpgrade = 0;
        creditGainUpgrade = 0;
        creditGainMultiplier = 1;
        WAVE = 0;

        turrets = new ArrayList<Turret>();
        asteroids = new ArrayList<Asteroid>();
        enemies = new ArrayList<Enemy>();
        projectiles = new ArrayList<Projectile>();
        onScreenGems = new ArrayList<OnScreenGem>();
        particles = new ArrayList<Particle>();

        shakeTime = 0;
        gameEndTick = 0;

        GEMS = new int[4];
        discoveredEnemies = new boolean[5];
        tooltips = new ArrayList<Tooltip>();
        shake = new int[]{0, 0};
        catalogIdx = -1;
        baseImageId = 0;
        showingInfoScreen = false;

        currentTip = -1;
        showingTip = false;
        screenFade = 0f;
        fadingScreen = false;

        createAsteroids();

        setGamemode(0);
        visibleGamemode = 0;
    }

    void drawStringWithLineBreaks(Graphics2D g, String text, int x, int y) {
        for (String line : text.split("\n"))
            g.drawString(line, x, y += g.getFontMetrics().getHeight());
    }

    public void drawTooltip(int x, int y, String text) {
        int size = 12;
        int lineBreak = 220;
        tooltips.add(new Tooltip(x + 12, y, lineBreak, (int) (ceil((text.length() + 5) / floor((double) lineBreak / (double) size)) * size) + 12, text));
    }

    public void upgradeMaxEnergy() throws LineUnavailableException, IOException {
        maxEnergyUpgrade++;
        maxEnergy += 250;
        SoundManager.startSound("click.wav");
        if (currentTip == 2) {
            showingTip = false;
        }
    }

    public void upgradeEnergyRegen() throws LineUnavailableException, IOException {
        energyRegenUpgrade++;
        energy_generation += 50;
        SoundManager.startSound("click.wav");
        if (currentTip == 2) {
            showingTip = false;
        }
    }

    public void upgradeCreditGain() throws LineUnavailableException, IOException {
        creditGainUpgrade++;
        creditGainMultiplier += 0.2;
        SoundManager.startSound("click.wav");
        if (currentTip == 2) {
            showingTip = false;
        }

    }

    public void gainCredits(int amount) throws LineUnavailableException, IOException {
        CREDITS += amount;
    }

    public void upgradeEffect(boolean allow) throws LineUnavailableException, IOException {
        if (allow) {
            SoundManager.startSound("click.wav");
        } else {
            SoundManager.startSound("deny.wav");
        }
    }

    public void selectTurret(int type) throws LineUnavailableException, IOException {
        selectedPlaceTurret = type;
        SoundManager.startSound("select.wav");
    }


    private String dirToString(int dir) {
        return switch (dir) {
            case -1 -> "";
            case 1 -> "left";
            case 2 -> "up";
            case 3 -> "right";
            case 4 -> "down";
            default -> "error";
        };
    }

    private int dirToDeg(int dir) {
        return switch (dir) {
            case 1 -> 270;
            case 2 -> 180;
            case 3 -> 90;
            case 4 -> 0;
            default -> 0;
        };
    }

    private boolean isPathfindDir(int dir) {
        return dir == 1 || dir == 2 || dir == 3 || dir == 4;
    }

    public Color setOpacity(Color color, int opacity) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity);
    }

    public boolean canAffordTurret(int type) {
        return ((CREDITS >= TurretTypes.getTurretCost(type) && (type == TurretTypes.BASIC || GEMS[type - 1] >= turretGemCost)) || FREE);
    }

    public void summonEnemies(int numBasic, int numArmoured, int numSpeed, int numStealth, int numMothership, double xStart, double xSpread, double ySpread) {
        // spread goes from 0 to 1
        // Stealh ships start 200 pixels further back

        int i;

        for (i = 0; i < numBasic; i++) {
            createFormation(EnemyTypes.FORMATION_JUSTBASIC, (int) round(xStart + (random() - 0.5) * xSpread), (int) round((0.5 + ((random() - 0.5) * ySpread)) * screenSize.getHeight()));
        }
        for (i = 0; i < numArmoured; i++) {
            createFormation(EnemyTypes.FORMATION_JUSTARMOURED, (int) round(xStart + (random() - 0.5) * xSpread), (int) round((0.5 + ((random() - 0.5) * ySpread)) * screenSize.getHeight()));
        }
        for (i = 0; i < numSpeed; i++) {
            createFormation(EnemyTypes.FORMATION_JUSTSPEED, (int) round(xStart * 2 + (random() - 0.5) * xSpread * 2), (int) round((0.5 + ((random() - 0.5) * ySpread)) * screenSize.getHeight()));
        }
        for (i = 0; i < numStealth; i++) {
            createFormation(EnemyTypes.FORMATION_JUSTSTEALTH, (int) round(xStart + (random() - 0.5) * xSpread - 300), (int) round((0.5 + ((random() - 0.5) * ySpread)) * screenSize.getHeight()));
        }
        for (i = 0; i < numMothership; i++) {
            createFormation(EnemyTypes.FORMATION_ARMOUREDMOTHERSHIP, (int) round(xStart + (random() - 0.5) * xSpread), (int) round((0.5 + ((random() - 0.5) * ySpread)) * screenSize.getHeight()));
        }
    }

    public void createWave(int wave) {
        waveStartTime = System.currentTimeMillis();
        receivedWaveDone = false;
        waveDone = false;
        ENERGY = maxEnergy;

        switch (wave) {
            case 1 -> summonEnemies(1, 0, 0, 0, 0, -100, 200, 0);
            case 2 -> summonEnemies(2, 0, 0, 0, 0, -100, 200, 0.2);
            case 3 -> summonEnemies(3, 0, 0, 0, 0, -100, 200, 0.3);
            case 4 -> summonEnemies(4, 1, 0, 0, 0, -100, 200, 0.4);
            case 5 -> summonEnemies(5, 2, 0, 0, 0, -100, 200, 0.5);
            case 6 -> summonEnemies(6, 3, 0, 0, 0, -100, 200, 0.6);
            case 7 -> summonEnemies(6, 3, 2, 0, 0, -100, 200, 0.7);
            case 8 -> summonEnemies(6, 4, 3, 0, 0, -100, 200, 0.8);
            case 9 -> summonEnemies(6, 4, 4, 0, 0, -100, 200, 0.8);
            case 10 -> summonEnemies(6, 4, 5, 2, 0, -100, 200, 0.9);
            case 11 -> summonEnemies(6, 4, 6, 4, 0, -100, 200, 0.9);
            case 12 -> summonEnemies(6, 4, 8, 8, 0, -100, 200, 0.9);
            case 13 -> summonEnemies(8, 0, 10, 10, 1, -150, 300, 0.9);
            case 14 -> summonEnemies(8, 0, 10, 10, 2, -150, 300, 0.9);
            case 15 -> summonEnemies(8, 0, 12, 12, 4, -150, 300, 0.9);
            case 16 -> summonEnemies(8, 0, 12, 12, 8, -150, 300, 0.9);
        }

//        int amount = wave;

//        createFormation(EnemyTypes.FORMATION_JUSTBASIC, 10, (int) round(random() * screenSize.getHeight()));
//        createFormation(EnemyTypes.FORMATION_JUSTARMOURED, 10, (int) round(random() * screenSize.getHeight()));
//        createFormation(EnemyTypes.FORMATION_JUSTSPEED, 10, (int) round(random() * screenSize.getHeight()));
//        createFormation(EnemyTypes.FORMATION_JUSTSTEALTH, (int) round(random() * 100 - 200), (int) round(random() * screenSize.getHeight()));
//        createFormation(EnemyTypes.FORMATION_ARMOUREDMOTHERSHIP, 10, (int) round(random() * screenSize.getHeight()));

    }

    public void rightJustify(int rightLimit, int y, String text, Graphics2D g) {
        g.drawString(text, (int) (rightLimit - g.getFontMetrics().getStringBounds(text, g).getWidth()), y);
    }

    public void playRandomShootSound() throws LineUnavailableException, IOException {
        SoundManager.startSound("shoot" + round(random() * 4) + ".wav");
    }

    public void shakeScreen(int time) {
        shakeTime = time;
    }

    public Map<TextAttribute, Object> createCharSpacing(float space) {
        Map<TextAttribute, Object> attributes = new HashMap<>();
        attributes.put(TextAttribute.TRACKING, space);
        return attributes;
    }

    public Enemy getEnemyById(int id) {
        for (Enemy enemy : enemies) {
            if (enemy.getId() == id) {
                return enemy;
            }
        }

        return null;
    }

    public Projectile getProjectileById(int id) {
        for (Projectile projectile : projectiles) {
            if (projectile.getId() == id) {
                return projectile;
            }
        }

        return null;
    }

    /*
    public boolean inBarrier(int x, int y) {
        for (Point barrier : barriers) {
//            if(pointIsInBox(x, y, barrier.x*tileSize, barrier.y*tileSize, tileSize, tileSize)) return true;
//            if (pointIsInBox(x, y, (int)(barrier.getX() * tileSize * ZOOMFACTOR - (tileSize * ZOOMFACTOR / 2f)), (int) (barrier.getY() * tileSize * ZOOMFACTOR - (tileSize * ZOOMFACTOR / 2f)), (int) (tileSize * ZOOMFACTOR), (int) (tileSize * ZOOMFACTOR) )) return true;
            if (pointIsInBox(x, y, (int) (barrier.getX() * tileSize - (tileSize / 2f)), (int) (barrier.getY() * tileSize - (tileSize / 2f)), tileSize, tileSize))
                return true;
        }

        return false;
    }

     */

    public boolean canPlace(int x, int y) {
        //test for placement on base
        return !Arrays.asList(baseTiles).contains(new Point(x, y));
        /*
        if (Arrays.asList(baseTiles).contains(new Point(x, y))) {
            return false;
        }

        return !getBarrier(x, y);

         */
    }

    public boolean mouseIn(int mouseX, int mouseY, int x1, int y1, int width, int height) {
        return mouseX > x1 && mouseX < x1 + width && mouseY > y1 && mouseY < y1 + height;
    }

    public Turret getTurretAt(int gridX, int gridY) {
        for (Turret turret : turrets) {
            if (turret.getX() == gridX && turret.getY() == gridY) {
                return turret;
            }
        }
        return null;
    }

    public Turret getTurretById(int id) {
        for (Turret turret : turrets) {
            if (turret.getId() == id) {
                return turret;
            }
        }
        return null;
    }

    public static BufferedImage resizeImage(BufferedImage img, int newW, int newH) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage newImg = new BufferedImage(newW, newH, img.getType());
        Graphics2D g = newImg.createGraphics();
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return newImg;
    }

    //all adjusts add half a tile to the final result, then shake
    public int adjustX(int x) {
        return (int) ((x + (tileSize * ZOOMFACTOR) / 2f)) + shake[0];
    }

    public int adjustX(double x) {
        return (int) ((x + (tileSize * ZOOMFACTOR) / 2f)) + shake[0];
    }

    public int adjustY(int y) {
        return (int) ((y + (tileSize * ZOOMFACTOR) / 2f)) + shake[1];
    }

    public int adjustY(double y) {
        return (int) ((y + (tileSize * ZOOMFACTOR) / 2f)) + shake[1];
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        clickType = e.getButton();
        mouseDown = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseDown = false;
        placedTurret = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {
        mouseDown = false;
        placedTurret = false;
    }
}
