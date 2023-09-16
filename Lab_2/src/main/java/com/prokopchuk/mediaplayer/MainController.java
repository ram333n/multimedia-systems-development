package com.prokopchuk.mediaplayer;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.media.MediaView;

public class MainController {

  @FXML
  private Label welcomeText;

  @FXML
  private MediaView mediaView;

  @FXML
  protected void onHelloButtonClick() {
    welcomeText.setText("Welcome to JavaFX Application!");
  }

}