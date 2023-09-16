module com.prokopchuk.mediaplayer {
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.media;

  opens com.prokopchuk.mediaplayer to javafx.fxml;
  exports com.prokopchuk.mediaplayer;
}