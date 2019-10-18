package game;

import static game.controllers.GameController.*;
import static java.lang.Math.PI;
import static java.lang.StrictMath.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;

public class Hexagon extends Polygon {
    private boolean opened = false;
    private boolean flagged = false;
    private boolean mined = false;
    public int minesNearby;
    public int rowCoordinate;
    public int columnCoordinate;
    public double rowPlacement;
    public double columnPlacement;

    public int mineCounter() {
        return minesNearby;
    }
    public boolean closed() {
        return !opened;
    }
    public void mined() {
        mined = true;
    }
    public boolean isMined() {
        return mined;
    }
    public boolean isFlagged() {
        return flagged;
    }
    public void unflag() {
        flagged = !flagged;
    }

    public Hexagon create(int row, int column, int radius, int gapBetween) {
        Hexagon hex = new Hexagon();
        for (int i = 0; i < 6; i++) {
            hex.getPoints().add(radius * cos(i * PI / 3));
            hex.getPoints().add(radius * sin(i * PI / 3));
        }
        double halfDiameter = sqrt(PI) * radius / 2;
        hex.rowPlacement = row * (gapBetween + radius * 1.6) + 40;
        hex.columnPlacement = (column * 2 - row % 2) * halfDiameter + column * gapBetween + 100;
        hex.setTranslateX(hex.rowPlacement);
        hex.setTranslateY(hex.columnPlacement);
        hex.setFill(Paint.valueOf("GREY"));

        hex.setOnMousePressed(event -> {
            if ((!lost) && (!won)) {
                if ((event.isPrimaryButtonDown()) && (!hex.isFlagged())) {
                    isFirst++;
                    if (isFirst == 1) {
                        firstClickedRow = row;
                        firstClickedColumn = column;
                        addMines(firstClickedRow, firstClickedColumn);
                    }//NOTE: SETS THE GAME UP DEPENDING ON THE TILE CLICKED
                    reveal(hex.rowCoordinate, hex.columnCoordinate);
                }
                if (event.isSecondaryButtonDown()) {
                    invertFlag(hex.rowCoordinate, hex.columnCoordinate);
                }
            }
        });
        return hex;
    }
    public void openOne() {
        if (!mined) openedCounter++;
        lost = mined;
        opened = true;
    }
}
