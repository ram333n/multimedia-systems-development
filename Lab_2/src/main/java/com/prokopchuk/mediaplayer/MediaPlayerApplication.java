package com.prokopchuk.mediaplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MediaPlayerApplication extends Application {

  private static final int SCENE_WIDTH = 1200;
  private static final int SCENE_HEIGHT = 900;
  private static final String STAGE_TITLE = "Media player";

  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(MediaPlayerApplication.class.getResource("media-player-view.fxml"));
    Scene scene = new Scene(fxmlLoader.load(), SCENE_WIDTH, SCENE_HEIGHT);
    stage.setTitle(STAGE_TITLE);
    stage.setScene(scene);
    stage.show();
  }

  public static void main(String[] args) {
    launch();
  }

}