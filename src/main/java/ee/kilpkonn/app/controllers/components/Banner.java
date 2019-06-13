package ee.kilpkonn.app.controllers.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class Banner extends StackPane {

    public Banner (String text, String smallText,  double width, double height) {
        this.setMaxSize(width, height / 3f);

        this.setTranslateX(0);
        this.setTranslateY(0);
        this.setAlignment(Pos.CENTER);

        this.getStyleClass().add("banner-pane");

        VBox vBox = new VBox();
        vBox.getStyleClass().add("banner-vbox");
        this.getChildren().add(vBox);

        Label label = new Label(text);
        label.getStyleClass().add("banner-label");

        Label smallLabel = new Label(smallText);
        smallLabel.getStyleClass().add("banner-small-label");

        vBox.getChildren().add(label);
        vBox.getChildren().add(smallLabel);
    }
}
