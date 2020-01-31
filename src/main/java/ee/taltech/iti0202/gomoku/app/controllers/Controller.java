package ee.taltech.gomoku.app.controllers;

import ee.taltech.gomoku.app.Game;
import javafx.fxml.Initializable;

public abstract class Controller implements Initializable {

    protected Game game;

    public void registerGame(Game game) {
        this.game = game;
    }
}
