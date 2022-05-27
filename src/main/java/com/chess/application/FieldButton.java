package com.chess.application;

import com.chess.root.Board;
import com.chess.root.Field;
import javafx.animation.FadeTransition;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.effect.Lighting;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.util.Duration;

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


    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    private void addEvents() {
        setOnMouseClicked((MouseEvent event) -> {
            if (board.isEditable() && event.getButton() == MouseButton.PRIMARY) {
                board.performManualMove(event);
            }
            event.consume();
        });
    }

}
