package ee.taltech.iti0202.gomoku.app.controllers;

import ee.taltech.iti0202.gomoku.app.Game;
import javafx.fxml.Initializable;

public abstract class Controller implements Initializable {

    protected Game game;

    public void registerGame(Game game) {
        this.game = game;
    }
}
