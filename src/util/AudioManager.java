package util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
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
    private Clip clip;
    private Song lastPlayed;
    private Song lastCombatPlayed;
    private Song lastOverworldPlayed;
    private Song lastTownPlayed;
    public static void main(String[] args) {
        try {
            AudioManager am = new AudioManager();
            am.clip = AudioSystem.getClip();
            JFrame f = new JFrame();
            f.getContentPane().setLayout(new BoxLayout(f.getContentPane(), BoxLayout.Y_AXIS));
            File folder = new File("./res/music");
            for (File songFile : folder.listFiles()) {
                Song mb = am.new Song(songFile);
                am.songs.add(mb);
                f.add(mb);
            }
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
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setBounds(0, 0, 600, 400);
            f.pack();
            f.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    class Song extends JButton{
        private Type type;
        private File song; 

        public enum Type {
            COMBAT,
            OVERWORLD,
            TOWN,
            OTHER
        }
        public Song(File song) {
            super(song.getName());
            this.song = song;
            if(isCombatSong()) type = Type.COMBAT;
            else if(isOverworldSong()) type = Type.OVERWORLD;
            else if(isTownSong()) type = Type.TOWN;
            else type = Type.OTHER;
            addActionListener(e -> {
                play();
            });
        }
        public void play() {
            try {
                clip.close();
                AudioInputStream as = AudioSystem.getAudioInputStream(song);
                clip.open(as);
                clip.start();
                System.out.println("Playing song: " + song.getName());
                LineListener ll = new LineListener() {
                    public void update(LineEvent e) {
                        if(e.getType()==LineEvent.Type.STOP) {
                            clip.removeLineListener(this);
                            playSongByType(type);
                        }
                    };
                };
                clip.addLineListener(ll);
                switch(type) {
                    case COMBAT:lastCombatPlayed = this;break;
                    case OVERWORLD:lastOverworldPlayed=this;break;
                    case TOWN:lastTownPlayed = this;break;
                    case OTHER:lastPlayed = this;break;
                    default:break;
                }
            }catch (IllegalStateException t) {
                //ignore 
            } catch (IOException|LineUnavailableException|UnsupportedAudioFileException t)  {
                t.printStackTrace();
            } 

        }
        public Type getType() {
            return type;
        }

        public boolean isCombatSong() {
            return song.getName().equals("combat1.wav") || song.getName().equals("combat2.wav");
        }
        public boolean isTownSong() {
            return song.getName().equals("townTheme1.wav") || song.getName().equals("townTheme2.wav");
        }
        public boolean isOverworldSong() {
            return song.getName().equals("overworld1.wav") || song.getName().equals("overworld2.wav")|| song.getName().equals("overworld3.wav");
        }
    }

    public void playSongByType(Song.Type t) {
        Song lastPlayedByType;
        switch(t) {
            case COMBAT:lastPlayedByType = lastCombatPlayed;break;
            case OVERWORLD:lastPlayedByType = lastOverworldPlayed;break;
            case TOWN:lastPlayedByType = lastTownPlayed;break;
            case OTHER:lastPlayed.play();return;
            default:return;
            
        }
        ArrayList<Song> availableSongs = new ArrayList<Song>();
        songs.forEach(s -> {
            if(!s.equals(lastPlayedByType) && s.getType().equals(t)) availableSongs.add(s);
        });
        Random r = new Random();
        availableSongs.get(r.nextInt(availableSongs.size())).play();
    }
}

