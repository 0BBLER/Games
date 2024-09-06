import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class SoundManager {
    static ArrayList<Clip> sounds = new ArrayList<>();
    static ArrayList<Long> startTime = new ArrayList<>();
    public static AudioInputStream FIGHT, MENU, INTRO, WIN, LOSE;

    static long songStartTime;
    static AudioInputStream song;
    static Clip soundtrack;

    static {
        try {
            //FIGHT = AudioSystem.getAudioInputStream(new File("src/res/sound/music/fight.wav"));
            FIGHT = AudioSystem.getAudioInputStream(SoundManager.class.getClassLoader().getResource("res/sound/music/fight.wav"));
            MENU = AudioSystem.getAudioInputStream(SoundManager.class.getClassLoader().getResource("res/sound/music/menu.wav"));
            INTRO = AudioSystem.getAudioInputStream(SoundManager.class.getClassLoader().getResource("res/sound/music/intro.wav"));
            WIN = AudioSystem.getAudioInputStream(SoundManager.class.getClassLoader().getResource("res/sound/music/win.wav"));
            LOSE = AudioSystem.getAudioInputStream(SoundManager.class.getClassLoader().getResource("res/sound/music/lose.wav"));
            soundtrack = AudioSystem.getClip();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setTrack(AudioInputStream track) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        soundtrack.flush();
        soundtrack.close();
        songStartTime = System.currentTimeMillis();
        song = track;

        soundtrack = AudioSystem.getClip();

        soundtrack.open(song);

        if (track.equals(FIGHT) || track.equals(INTRO)) {
            FloatControl gainControl = (FloatControl) soundtrack.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(4f);
//            gainControl.setValue(20f * (float) Math.log10(0.4f));
        } else {
            FloatControl gainControl = (FloatControl) soundtrack.getControl(FloatControl.Type.MASTER_GAIN);
            //gainControl.setValue(1f);
            gainControl.setValue(20f * (float) Math.log10(0.4f));
        }


        soundtrack.loop(Clip.LOOP_CONTINUOUSLY);
        soundtrack.start();

        FIGHT = AudioSystem.getAudioInputStream(SoundManager.class.getClassLoader().getResource("res/sound/music/fight.wav"));
        MENU = AudioSystem.getAudioInputStream(SoundManager.class.getClassLoader().getResource("res/sound/music/menu.wav"));
        INTRO = AudioSystem.getAudioInputStream(SoundManager.class.getClassLoader().getResource("res/sound/music/intro.wav"));
        WIN = AudioSystem.getAudioInputStream(SoundManager.class.getClassLoader().getResource("res/sound/music/win.wav"));
        LOSE = AudioSystem.getAudioInputStream(SoundManager.class.getClassLoader().getResource("res/sound/music/lose.wav"));
    }

    public static void endSoundtrack() {
        soundtrack.flush();
        soundtrack.close();
    }

    /*
    public static void checkSoundtrack() throws LineUnavailableException, IOException {
        if (soundtrack != null) {
            if (System.currentTimeMillis() - songStartTime >= songTotalRuntime * 1000) {
                System.out.println("src/restarting song");

                soundtrack.close();
                songStartTime = System.currentTimeMillis();

                soundtrack = AudioSystem.getClip();

                soundtrack.open(song);
                soundtrack.start();
                soundtrack.
            }
        }
    }

     */

    public static void startSound(String name) throws LineUnavailableException, IOException {
        AudioInputStream audioInputStream;

        try {
            audioInputStream = AudioSystem.getAudioInputStream(SoundManager.class.getClassLoader().getResource("res/sound/"+name));
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
            return;
        }
        sounds.add(AudioSystem.getClip());
        startTime.add(System.currentTimeMillis() / 1000);
        sounds.get(sounds.size() - 1).open(audioInputStream);
        sounds.get(sounds.size() - 1).start();
    }

    public static void checkSounds() {
        ArrayList<Integer> toRemove = new ArrayList<>();
        for (int i = 0; i < sounds.size(); i++) {
            if (startTime.get(i) + 2 < System.currentTimeMillis() / 1000) {
                toRemove.add(i);
            }
        }
        for (int i = toRemove.size() - 1; i > -1; i--) {
            sounds.get(i).close();
            startTime.remove(toRemove.get(i).intValue());
            sounds.remove(toRemove.get(i).intValue());
        }
    }
}
