package com.chess.application;

import com.chess.root.Board;
import com.chess.root.Field;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.effect.Lighting;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * Class for the piece field.
 */
public class FieldButton extends Button {

    private final Board board;
    private Field field;

    /**
     * Constructor for the field button.
     * @param board The board.
     * @param field The field.
     */
    public FieldButton(Board board, Field field) {
        this.board = board;
        this.field = field;
        this.setMaxWidth(Double.MAX_VALUE);
        this.setMaxHeight(Double.MAX_VALUE);
        Lighting light = new Lighting();
        light.setSurfaceScale(0.8f);
        this.setEffect(light);
        this.getStyleClass().add("button-default");
        this.setCursor(Cursor.HAND);
        addEvents();
    }

    /**
     * getter for a game field associated with a field button
     * @return
     */
    public Field getField() {
        return field;
    }

    /**
     * sets game field associated with qa button
     * @param field to be associated with a button
     * */
    public void setField(Field field) {
        this.field = field;
    }

    private void addEvents() {
        setOnMouseClicked((MouseEvent event) -> {
            if (board.isPlayMode() && event.getButton() == MouseButton.PRIMARY) {
                board.performManualMove(event);
            }
            event.consume();
        });
    }

}
