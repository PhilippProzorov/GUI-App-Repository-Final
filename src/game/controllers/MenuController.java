package game.controllers;

import game.Main;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.Optional;

public class MenuController {
    private Integer tilesPreset = 20;
    private Integer minesPreset = 75;
    private int area = tilesPreset * tilesPreset;

    @FXML
    private Text gameTitle;
    @FXML
    private TextField sizeField;
    @FXML
    private TextField mineField;
    @FXML
    private Label tilesCorrect;
    @FXML
    private Label minesCorrect;

    @FXML
    public void initialize() {
        sizeField.textProperty().addListener((observable, oldText, newText) -> ifTilesCorrect(newText));
        mineField.textProperty().addListener((observable, oldText, newText) -> ifMinesCorrect(newText));
    }
    private void ifMinesCorrect(String toCheck) {
        Integer mines = 0;
        try {
            mines = Integer.parseInt(toCheck);
            minesCorrect.setText("");
            minesCorrect.setTextFill(Color.BLACK);
        } catch (Exception e) {
            minesCorrect.setText("Incorrect input");
            minesCorrect.setTextFill(Color.BLACK);
        }
        if (mines <= 0) {
            minesCorrect.setText("Mines input must be above zero");
            minesCorrect.setTextFill(Color.BLACK);
        } else if (((area - (tilesPreset * 2)) <= (mines))){
            minesCorrect.setText("Too many mines for this field");
            minesCorrect.setTextFill(Color.BLACK);
        }
        else {
            minesPreset = mines;
        }
    }
    private void ifTilesCorrect(String toCheck) {
        final Integer max = 20;
        final Integer min = 5;
        Integer tiles = 0;
        try {
            tiles = Integer.parseInt(toCheck);
            tilesCorrect.setText("");
            tilesCorrect.setTextFill(Color.BLACK);
        } catch (Exception e) {
            tilesCorrect.setText("Incorrect input");
            tilesCorrect.setTextFill(Color.BLACK);
        }
        if(tiles < min) {
            tilesCorrect.setText("You're under tiles limit");
            tilesCorrect.setTextFill(Color.BLACK);
        } else if (tiles > max) {
            tilesCorrect.setText("You're over tiles limit");
            tilesCorrect.setTextFill(Color.BLACK);
        }
        else {
            tilesPreset = tiles;
        }
    }

    @FXML
    void startGame() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("fxml/Game.fxml"));
            gameTitle.getScene().getWindow().hide();
            GameController.setSize(tilesPreset);
            GameController.setMines(minesPreset);
            GameController.game(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void rules() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        ButtonType cancel = new ButtonType("Return", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(cancel);
        alert.setTitle(" ");
        alert.setHeaderText("Basics of the game");
        alert.setContentText("1) You are presented with a board of hexagons. Some of them contain mines, others " +
                "don't. If you click on a hex containing a mine, you lose. If you manage to open all " +
                "the hex (without clicking on any mines) you win.\n" + "\n" +
                "2) Clicking a tile which doesn't have a mine reveals the number of neighbouring tiles " +
                "containing mines. Use this information plus some guess work to avoid the mines.\n" + "\n" +
                "3) To open a tile, point at the hex and click on it. To mark a tile you think is a mine, " +
                "point and right-click.");
        Optional<ButtonType> actions = alert.showAndWait();
        if (actions.get() == cancel) {
            alert.close();
        }
    }

    @FXML
    void exitGame() {
        System.exit(1);
    }

}