package presentation;

import java.nio.file.Paths;

import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class BackgroundMusic {
	
	private static BackgroundMusic instance;
	private MediaPlayer mediaPlayerMusic;
	private Slider volumeSlider;
	
	private BackgroundMusic() {
        Media mediaMusic = new Media(Paths.get("sound/bgm.mp3").toUri().toString());
		mediaPlayerMusic = new MediaPlayer(mediaMusic);
		mediaPlayerMusic.setCycleCount(MediaPlayer.INDEFINITE);

		volumeSlider = new Slider(0, 0.1, 0.05);
		mediaPlayerMusic.volumeProperty().bindBidirectional(volumeSlider.valueProperty());
	}
	
	public static BackgroundMusic getInstance() {
		if (instance == null) {
			instance = new BackgroundMusic();
		}

		return instance;
	}
	
	public MediaPlayer getMusicPlayer() {
		return mediaPlayerMusic;
	}
	
	public Slider getVolumeSlider() {
		return volumeSlider;
	}
}