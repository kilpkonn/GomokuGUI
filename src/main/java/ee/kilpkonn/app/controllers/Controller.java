package ee.kilpkonn.app.controllers;

import ee.kilpkonn.app.Game;
import javafx.fxml.Initializable;

public abstract class Controller implements Initializable {

    protected Game game;

    public void registerGame(Game game) {
        this.game = game;
    }
}
