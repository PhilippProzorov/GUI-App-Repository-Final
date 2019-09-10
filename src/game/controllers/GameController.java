package game.controllers;

import java.util.*;

import javafx.util.*;
import game.Hexagon;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.scene.Scene;

public class GameController {
    public static int isFirst = 0;
    public static int firstClickedRow = 0;
    public static int firstClickedColumn = 0;
    public static int openedCounter = 0;
    public static boolean won = false;
    public static boolean lost = false;
    private static int tiles = 20;
    private static int tilesFlagged;
    private static int area = tiles * tiles;
    private static int mines = 75;
    private static int tilesSize = 20;
    private static int mineCounter = 0;
    private static HBox box = new HBox();
    private static Button newGameButton = new Button("☺");
    private static List<Pair<Integer, Integer>> isEven = new ArrayList<>();
    private static List<Pair<Integer, Integer>> isOdd = new ArrayList<>();
    private static Hexagon[][] field = new Hexagon[tiles][tiles];
    private static Text[][] flagField = new Text[tiles][tiles];
    private static Group root = new Group();

    static void setSize(Integer sizeInput) {
        tiles = sizeInput;
    }

    static void setMines(Integer minesInput) {
        mines = minesInput;
    }

    static void game(Stage primaryStage) {
        primaryStage.setTitle("Minesweeper");
        Scene scene = new Scene(root, 900, 850);
        primaryStage.setScene(scene);
        scene.setFill(Paint.valueOf("WHITE"));
        createField(root);
        primaryStage.setResizable(true);
        primaryStage.show();
        setButton();
    }

    private static void createField(Group root) {
        for (int row = 0; row < tiles; row++) {
            for (int column = 0; column < tiles; column++) {
                Hexagon hex = new Hexagon();
                field[row][column] = hex.create(row, column, tilesSize, 1);
                field[row][column].rowCoordinate = row;
                field[row][column].columnCoordinate = column;
                root.getChildren().add(field[row][column]);
            }
        }
    }

    public static void addMines(int firstClickedRow, int firstClickedColumn) {
        Random random = new Random();
        int x;
        int y;
        while (mineCounter < mines) {
            do {
                x = random.nextInt(tiles);
                y = random.nextInt(tiles);
            } while (field[x][y].isMined());
            if ((x != firstClickedRow) && (y != firstClickedColumn)) {
                field[x][y].mined();
                mineCounter++;
            }
        }
        aroundEven();
        aroundOdd();//NOTE: FORMS ALL POSSIBLE PAIRS OF HEX TO GATHER INFO ABOUT SURROUNDINGS
        for (int row = 0; row < tiles; row++) {
            for (int column = 0; column < tiles; column++) {
                if (!field[row][column].isMined()) {
                    if (row % 2 == 0) {
                        for (Pair<Integer, Integer> move : isEven)
                            if (outOfBounds(row, column, move))
                                if (field[row + move.getKey()][column + move.getValue()].isMined())
                                    field[row][column].minesNearby++;
                    } else {
                        for (Pair<Integer, Integer> move : isOdd)
                            if (outOfBounds(row, column, move))
                                if (field[row + move.getKey()][column + move.getValue()].isMined())
                                    field[row][column].minesNearby++;
                    }
                }
            }
        }//NOTE: TOTAL FIELD CHECK FOR THE POSSIBILITIES OF MINES AND VISUALIZING
    }

    public static void reveal(int row, int column) {
        if (field[row][column].isMined()) { lost = true;
            for (int x = 0; x < tiles; x++)
                for (int y = 0; y < tiles; y++) {
                    if (field[x][y].isMined()) {
                        field[x][y].setFill(Paint.valueOf("RED"));
                        root.getChildren().add(mine(field[x][y].rowPixel, field[x][y].columnPixel));
                    }
                }
            field[row][column].setFill(Paint.valueOf("RED"));
            message("You lost");
        } else {
            if ((field[row][column].mineCounter() > 0) && (field[row][column].closed())) {
                field[row][column].openOne();
                field[row][column].setFill(Paint.valueOf("GREY"));
                root.getChildren().add(minesNearby(field[row][column].rowPixel + 8,
                        field[row][column].columnPixel - 8, field[row][column].mineCounter()));
            } else {
                if (field[row][column].closed() && !field[row][column].isFlagged()
                        && field[row][column].mineCounter() == 0) {
                    field[row][column].openOne();
                    field[row][column].setFill(Paint.valueOf("SILVER"));
                    if (row % 2 == 0) {
                        for (Pair<Integer, Integer> move : isEven) {
                            if (outOfBounds(row, column, move) &&
                                    !field[row + move.getKey()][column + move.getValue()].isFlagged()) {
                                reveal(row + move.getKey(), column + move.getValue());
                            }
                        }
                    } else {
                        for (Pair<Integer, Integer> move : isOdd) {
                            if (outOfBounds(row, column, move) &&
                                    !field[row + move.getKey()][column + move.getValue()].isFlagged()) {
                                reveal(row + move.getKey(), column + move.getValue());
                            }
                        }
                    }
                }
            }//NOTE: REVEALS EMPTY HEX AND PROCEDES TO THE ONES THAT HAVE MINES NEARBY
        }//NOTE: CONDITION DEALING WITH ALL HEX OUTCOMES
        won = ((tilesFlagged == mines) && (openedCounter == ((area) - mines)));
    }

    public static void invertFlag(int row, int column) {
        if (field[row][column].closed()) {
            field[row][column].unflag();
            if (field[row][column].isFlagged()) {
                if (field[row][column].isMined()) tilesFlagged++;
                flagField[row][column] = flag(field[row][column].rowPixel + 7,
                        field[row][column].columnPixel - 5);
                root.getChildren().add(flagField[row][column]);
            } else {
                if (field[row][column].isMined())
                    tilesFlagged = tilesFlagged - 1;
                root.getChildren().remove(flagField[row][column]);
            }
        }
        won = ((tilesFlagged == mines) && (openedCounter == ((area) - mines)));
        if (won) {
            message("You won");
        }
    }//NOTE: CREATES AN OUTCOME DEPENDING ON THE CONTENTS OF THE TILE

    private static void aroundEven() {
        isEven.add(new Pair<>(0, -1));
        isEven.add(new Pair<>(-1, 0));
        isEven.add(new Pair<>(-1, +1));
        isEven.add(new Pair<>(0, +1));
        isEven.add(new Pair<>(+1, +1));
        isEven.add(new Pair<>(+1, 0));
    }//NOTE: PAIRS THE HEX WITHING THE EVEN ROW WITH SURROUNDING HEX

    private static void aroundOdd() {
        isOdd.add(new Pair<>(0, -1));
        isOdd.add(new Pair<>(-1, -1));
        isOdd.add(new Pair<>(-1, 0));
        isOdd.add(new Pair<>(0, +1));
        isOdd.add(new Pair<>(+1, 0));
        isOdd.add(new Pair<>(+1, -1));
    }//NOTE: PAIRS THE HEX WITHING THE ODD ROW WITH SURROUNDING HEX

    private static boolean outOfBounds(int row, int column, Pair<Integer, Integer> coordinates) {
        int incrementRow = row + coordinates.getKey();
        int incrementColumn = column + coordinates.getValue();
        return ((0 <= incrementRow) && (incrementRow < tiles) &&
                (0 <= incrementColumn) && (incrementColumn < tiles));
    }//NOTE: CHECKS FOR POSSIBLE ABSENCE OF NEARBY TILES

    private static Hexagon mine(double rowPixel, double columnPixel) {
        Hexagon hex = new Hexagon();
        hex.setTranslateY(rowPixel);
        hex.setTranslateX(columnPixel);
        hex.setFill(Paint.valueOf("RED"));
        return hex;
    }//NOTE: VISUALISATION OF THE MINE

    private static Text flag(double rowPixel, double columnPixel) {
        Text text = new Text();
        text.setTranslateX(columnPixel - 5);
        text.setTranslateY(rowPixel - 2);
        text.setText(".");
        text.setFont(Font.font("Colibri", 96));
        return text;
    }//NOTE: MARKS SELECTED TILE

    private static Text minesNearby(double rowPixel, double columnPixel, int amount) {
        Text text = new Text();
        text.setTranslateX(columnPixel + 2);
        text.setTranslateY(rowPixel);
        text.setText(Integer.toString(amount));
        text.setFont(Font.font("Impact", 22));
        return text;
    }//NOTE: SHOWS AMOUNT OF MINES NEARBY

    private static void message(String text) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game over");
        alert.setHeaderText(text);
        alert.setContentText("Do you wish to restart?");
        ButtonType confirm = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(confirm, cancel);
        alert.showAndWait().ifPresent(type -> {
            if (type == confirm) {
                restart();
                alert.close();
            } else if (type == cancel) {
                alert.close();
                System.exit(1);
            }
        });
    }//NOTE: SHOWS MESSAGE DEPENDING ON THE OUTCOME OF THE GAME

    private static void restart() {
        flagField = new Text[tiles][tiles];
        root.getChildren().clear();
        isEven.clear();
        isOdd.clear();
        tilesFlagged = 0;
        openedCounter = 0;
        mineCounter = 0;
        won = false;
        lost = false;
        isFirst = 0;
        root.getChildren().addAll(box);
        createField(root);
    }//NOTE: RESETS ALL PROGRESS MADE BEFORE

    private static void button() {
        newGameButton.setOnAction(event -> restart());
    }
    private  static void setButton() {
        box.getChildren().add(newGameButton);
        box.setLayoutX(450);
        box.setLayoutY(25);
        root.getChildren().add(box);
        button();
    }
}