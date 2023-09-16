package com.prokopchuk.mediaplayer;

import java.util.List;
import java.util.Objects;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

public class MainController {

  private static final List<String> SUPPORTED_EXTENTIONS = List.of("*.mp4", "*.mp3", "*.wav");
  private static final int SKIP_BACK_SECONDS = -10;
  private static final int SKIP_FORWARD_SECONDS = 10;

  @FXML
  private Slider playerSlider;

  @FXML
  private Slider volumeSlider;

  @FXML
  private Label durationLabel;

  @FXML
  private MediaView mediaView;

  private MediaPlayer mediaPlayer;
  private final DurationDataFormatter durationDataFormatter = new DurationDataFormatter();

  @FXML
  private void onClickOpenButton(ActionEvent event) {
    String filePath = selectFile();

    if (Objects.isNull(filePath)) {
      return;
    }

    clearPreviousData();
    initMediaView(filePath);
    initVolumeSlider();
    initPlayerSlider();
  }

  private String selectFile() {
    FileChooser fileChooser = new FileChooser();
    FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Select file", SUPPORTED_EXTENTIONS);
    fileChooser.getExtensionFilters().add(extensionFilter);

    return fileChooser.showOpenDialog(null).toURI().toString();
  }

  private void clearPreviousData() {
    this.playerSlider.setValue(0);
    this.durationLabel.setText(durationDataFormatter.stringifyEmpty());
  }

  private void initMediaView(String filePath) {
    Media media = new Media(filePath);
    this.mediaPlayer = new MediaPlayer(media);
    this.mediaView.setMediaPlayer(mediaPlayer);

    this.mediaPlayer.currentTimeProperty().addListener(observable -> refreshSlider());
  }

  private void refreshSlider() {
    double passedMinutes = this.mediaPlayer.getCurrentTime().toMinutes();
    double totalMinutes = this.mediaPlayer.getTotalDuration().toMinutes();
    double sliderValue = passedMinutes * 100 / totalMinutes;
    this.playerSlider.setValue(sliderValue);
    refreshDurationLabel(this.mediaPlayer.getCurrentTime());
  }

  private void refreshDurationLabel(Duration currentTime) {
    String newLabelValue = this.durationDataFormatter.getDurationsString(currentTime, mediaPlayer.getTotalDuration());
    this.durationLabel.setText(newLabelValue);
  }

  private void initVolumeSlider() {
    this.volumeSlider.setValue(this.mediaPlayer.getVolume() * 100);
    this.volumeSlider.valueProperty().addListener(
        observable -> this.mediaPlayer.setVolume(volumeSlider.getValue() / 100)
    );
  }

  private void initPlayerSlider() {
    this.playerSlider.setOnMouseClicked(
        mouseEvent -> evaluateDurationFromSlider()
    );
  }

  private void evaluateDurationFromSlider() {
    double totalSeconds = this.mediaPlayer.getTotalDuration().toSeconds();
    double currentDurationInSeconds = this.playerSlider.getValue() * totalSeconds / 100;
    this.mediaPlayer.seek(Duration.seconds(currentDurationInSeconds));
  }

  @FXML
  private void onClickPauseButton(ActionEvent event) {
    this.mediaPlayer.pause();
  }

  @FXML
  private void onClickPlayButton(ActionEvent event) {
    this.mediaPlayer.play();
  }

  @FXML
  private void onClickStopButton(ActionEvent event) {
    this.mediaPlayer.stop();
  }

  @FXML
  private void onClickSkipBackButton(ActionEvent event) {
    seekMediaPlayer(Duration.seconds(SKIP_BACK_SECONDS));
  }

  private void seekMediaPlayer(Duration duration) {
    this.mediaPlayer.seek(mediaPlayer.getCurrentTime().add(duration));
  }

  @FXML
  private void onClickSkipForwardButton(ActionEvent event) {
    seekMediaPlayer(Duration.seconds(SKIP_FORWARD_SECONDS));
  }

  private static class DurationDataFormatter {
    private static final String SEPARATOR_FOR_TIMEUNITS = ":";
    private static final String SEPARATOR_FOR_DURATIONS = "/";
    private static final String TIMEUNIT_PLACEHOLDER = "-";
    private static final String EMPTY_DURATIONS = initEmptyDurations();

    private static String initEmptyDurations() {
      return TIMEUNIT_PLACEHOLDER.repeat(2)
          + SEPARATOR_FOR_TIMEUNITS
          + TIMEUNIT_PLACEHOLDER.repeat(2)
          + SEPARATOR_FOR_DURATIONS
          + TIMEUNIT_PLACEHOLDER.repeat(2)
          + SEPARATOR_FOR_TIMEUNITS
          + TIMEUNIT_PLACEHOLDER.repeat(2);
    }

    public String getDurationsString(Duration currentDuration, Duration totalDuration) {
      String currentDurationString = stringifyDuration(currentDuration);
      String totalDurationString = stringifyDuration(totalDuration);

      return currentDurationString + SEPARATOR_FOR_DURATIONS + totalDurationString;
    }

    private String stringifyEmpty() {
      return EMPTY_DURATIONS;
    }

    private String stringifyDuration(Duration duration) {
      int hours = (int) duration.toHours();
      int minutes = (int) duration.toMinutes();
      int seconds = (int) duration.toSeconds();

      StringBuilder result = new StringBuilder();

      if (hours > 0) {
        result.append(stringifyTimeunit(hours))
            .append(SEPARATOR_FOR_TIMEUNITS);
      }

      result.append(stringifyTimeunit(minutes))
          .append(SEPARATOR_FOR_TIMEUNITS)
          .append(stringifyTimeunit(seconds));

      return result.toString();
    }

    private String stringifyTimeunit(int value) {
      String timeunitString = String.valueOf(value);

      return value / 10 > 0 ? timeunitString : "0" + timeunitString;
    }
  }

}