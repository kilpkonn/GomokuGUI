package ee.kilpkonn.app.controllers.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class Banner extends StackPane {

    public Banner (String text, double width, double height) {
        this.setMaxSize(width, height / 3f);

        this.setTranslateX(0);
        this.setTranslateY(0);
        this.setAlignment(Pos.CENTER);

        this.getStyleClass().add("banner-pane");

        Label label = new Label(text);
        label.getStyleClass().add("banner-label");

        this.getChildren().add(label);
    }
}
