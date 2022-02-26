package util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class AudioManager {
    private ArrayList<Song> songs = new ArrayList<Song>();
    private ArrayList<Sound> sounds = new ArrayList<Sound>();
    private Clip musicClip;
    private Song lastNonCombatSong;
    private Song lastPlayedSong;
    private Song lastCombatPlayedSong;
    private Song lastOverworldPlayedSong;
    private Song lastTownPlayedSong;

    public AudioManager() {
        setup(true);
    }
    public AudioManager(boolean playMain) {
        setup(playMain);
    }
    public void setup(boolean playMain) {
        try {
            musicClip = AudioSystem.getClip();
            File folder = new File("./res/music");
            for (File songFile : folder.listFiles()) {
                Song mb = new Song(songFile);
                songs.add(mb);
                if(mb.isMainSong()&&playMain) mb.play();
            }
            folder = new File("./res/sounds");
            for(File soundFile : folder.listFiles()) {
                Sound s = new Sound(soundFile);
                sounds.add(s);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        try {
            AudioManager am = new AudioManager(false);
            JFrame f = new JFrame();
            f.getContentPane().setLayout(new BoxLayout(f.getContentPane(), BoxLayout.Y_AXIS));
            am.songs.forEach(s -> f.add(am.new SongButton(s)));
            
            JButton randomTown = new JButton("Random Town Song");
            randomTown.addActionListener(e -> {
                am.playSongByType(Song.Type.TOWN);
            });
            JButton randomOverworld = new JButton("Random Overworld Song");
            randomOverworld.addActionListener(e -> {
                am.playSongByType(Song.Type.OVERWORLD);
            });
            JButton randomCombat = new JButton("Random Combat Song");
            randomCombat.addActionListener(e -> {
                am.playSongByType(Song.Type.COMBAT);
            });
            f.add(randomTown);
            f.add(randomCombat);
            f.add(randomOverworld);
            am.sounds.forEach(s -> f.add(am.new SoundButton(s)));
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setBounds(0, 0, 600, 400);
            f.pack();
            f.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Song getSongByFile(File f) {
        for (Song song : songs) {
            if(song.getFile().equals(f)) return song;
        }
        return null;
    }
    class SongButton extends JButton {
        private Song song;
        public SongButton(Song s) {
            super(s.getFile().getName());
            this.song = s;
            addActionListener(e -> {
                song.play();
            });
        }
    }

    class SoundButton extends JButton {
        private Sound sound;
        public SoundButton(Sound s) {
            super(s.getFile().getName());
            this.sound = s;
            addActionListener(e -> {
                sound.play();
            });
        }
    }

    class Song {
        private Type type;
        private File file; 

        public enum Type {
            COMBAT,
            OVERWORLD,
            TOWN,
            GUILDHALL,
            BOSS,
            CAVES,
            SEWERS,
            MAIN
        }
        public Song(File file) {
            super();
            this.file = file;
            if(isCombatSong()) type = Type.COMBAT;
            else if(isOverworldSong()) type = Type.OVERWORLD;
            else if(isTownSong()) type = Type.TOWN;
            else if(isGuildHallSong()) type = Type.GUILDHALL;
            else if(isBossSong()) type = Type.BOSS;
            else if(isCavesSong()) type = Type.CAVES;
            else if(isSewerSong()) type = Type.SEWERS;
            else if(isMainSong()) type = Type.MAIN;
        }
        public void play() {
            try {
                musicClip.close();
                AudioInputStream as = AudioSystem.getAudioInputStream(file);
                musicClip.open(as);
                musicClip.start();
                System.out.println("Playing song: " + file.getName());
                LineListener ll = new LineListener() {
                    public void update(LineEvent e) {
                        if(e.getType()==LineEvent.Type.STOP) {
                            musicClip.removeLineListener(this);
                            playSongByType(type);
                        }
                    };
                };
                musicClip.addLineListener(ll);
                switch(type) {
                    case COMBAT:lastCombatPlayedSong = this;break;
                    case OVERWORLD:lastOverworldPlayedSong=this;break;
                    case TOWN:lastTownPlayedSong = this;break;
                    default:break;
                }
                lastPlayedSong = this;
                if(!type.equals(Type.COMBAT)) lastNonCombatSong = this;
            }catch (IllegalStateException t) {
                //ignore 
            } catch (IOException|LineUnavailableException|UnsupportedAudioFileException t)  {
                t.printStackTrace();
            } 

        }
        public Type getType() {
            return type;
        }

        public File getFile() {
            return file;
        }

        public boolean isCombatSong() {
            return file.getName().equals("combat1.wav") || file.getName().equals("combat2.wav");
        }
        public boolean isTownSong() {
            return file.getName().equals("townTheme1.wav") || file.getName().equals("townTheme2.wav");
        }
        public boolean isOverworldSong() {
            return file.getName().equals("overworld1.wav") || file.getName().equals("overworld2.wav")|| file.getName().equals("overworld3.wav");
        }
        public boolean isGuildHallSong() {
            return file.getName().equals("guildHall.wav");
        }
        public boolean isBossSong() {
            return file.getName().equals("boss.wav");
        }
        public boolean isCavesSong() {
            return file.getName().equals("caves.wav");
        }
        public boolean isSewerSong() {
            return file.getName().equals("sewer.wav");
        }
        public boolean isMainSong() {
            return file.getName().equals("mainTheme.wav");
        }
    }

    class Sound {
        private File folder; 

        public Sound(File folder) {
            super();
            this.folder = folder;
        }
        public File chooseRandom() {
            return folder.listFiles()[new Random().nextInt(folder.listFiles().length)];
        }
        public void play() {
            try {
                Clip soundClip = AudioSystem.getClip();
                AudioInputStream as = AudioSystem.getAudioInputStream(chooseRandom());
                //System.out.println("Playing sound: " + chooseRandom().getName());
                try {
                    soundClip.open(as);
                    soundClip.start();
                    soundClip.addLineListener(e -> {
                        if(e.getType().equals(LineEvent.Type.STOP)) {
                            soundClip.close();
                        }
                    });
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                
            }catch (IllegalStateException t) {
                //ignore 
            } catch (IOException|LineUnavailableException|UnsupportedAudioFileException t)  {
                t.printStackTrace();
            } 

        }

        public File getFile() {
            return folder;
        }
    }

    public void playSoundByName(String name) {
        sounds.forEach(s -> {
            if(s.getFile().getName().equals(name)) s.play();
        });
    }

    public void playSongByTileId(int id) {
        switch(id) {
            case (1357 + 16):   //overworld
                if(!Song.Type.OVERWORLD.equals(lastPlayedSong.getType())) {
                    playSongByType(Song.Type.OVERWORLD);
                }
                break;
            case (1912 + 123):  //town
                if(!Song.Type.TOWN.equals(lastPlayedSong.getType())) {
                    playSongByType(Song.Type.TOWN);
                }
                break;
            case (5152 + 1681): //guildhall
                if(!Song.Type.GUILDHALL.equals(lastPlayedSong.getType())) {
                    playSongByType(Song.Type.GUILDHALL);
                }
                break;
            case (4314 + 16):   //boss
                if(!Song.Type.BOSS.equals(lastPlayedSong.getType())) {
                    playSongByType(Song.Type.BOSS);
                }
                break;
            case (639 + 36):    //caves
                if(!Song.Type.CAVES.equals(lastPlayedSong.getType())) {
                    playSongByType(Song.Type.CAVES);
                }
                break;
            case (3424 + 81):   //sewers
                if(!Song.Type.SEWERS.equals(lastPlayedSong.getType())) {
                    playSongByType(Song.Type.SEWERS);
                }
                break;
            case (666):
                if(!Song.Type.COMBAT.equals(lastPlayedSong.getType())) {
                    playSongByType(Song.Type.COMBAT);
                }
                break;
            case (0):
                if(lastPlayedSong.getType().equals(Song.Type.COMBAT)) {
                    playSongByType(lastNonCombatSong.getType());
                }
                break;
            default:
                break;
        }
    }

    public void playSongByType(Song.Type t) {
        Song lastPlayedByType;
        switch(t) {
            case COMBAT:lastPlayedByType = lastCombatPlayedSong;break;
            case OVERWORLD:lastPlayedByType = lastOverworldPlayedSong;break;
            case TOWN:lastPlayedByType = lastTownPlayedSong;break;
            default:lastPlayedByType = null;break;
        }
        ArrayList<Song> availableSongs = new ArrayList<Song>();
        songs.forEach(s -> {
            if(!s.equals(lastPlayedByType) && s.getType().equals(t)) availableSongs.add(s);
        });
        Random r = new Random();
        availableSongs.get(r.nextInt(availableSongs.size())).play();
    }

    public File getLastCombatPlayedSong() {
        return lastCombatPlayedSong==null? null : lastCombatPlayedSong.getFile();
    }

    public File getLastNonCombatSong() {
        return lastNonCombatSong==null? null : lastNonCombatSong.getFile();
    }

    public File getLastPlayedSong() {
        return lastPlayedSong==null? null : lastPlayedSong.getFile();
    }

    public File getLastOverworldPlayedSong() {
        return lastOverworldPlayedSong==null? null : lastOverworldPlayedSong.getFile();
    }

    public File getLastTownPlayedSong() {
        return lastTownPlayedSong==null? null : lastTownPlayedSong.getFile();
    }

    public void setLastCombatPlayedSong(Song lastCombatPlayedSong) {
        this.lastCombatPlayedSong = lastCombatPlayedSong;
    }

    public void setLastNonCombatSong(Song lastNonCombatSong) {
        this.lastNonCombatSong = lastNonCombatSong;
    }
    public void setLastOverworldPlayedSong(Song lastOverworldPlayedSong) {
        this.lastOverworldPlayedSong = lastOverworldPlayedSong;
    }
    public void setLastPlayedSong(Song lastPlayedSong) {
        this.lastPlayedSong = lastPlayedSong;
    }
    public void setLastTownPlayedSong(Song lastTownPlayedSong) {
        this.lastTownPlayedSong = lastTownPlayedSong;
    }
    public void playLastPlayed() {
        playSongByType(lastPlayedSong.getType());
    }
}

