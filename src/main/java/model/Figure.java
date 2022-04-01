package model;


import java.util.List;

public abstract class Figure {
    private Position pos;
    public final int value = 1;
    public abstract void setPos(Position pos);
    public abstract Position getPos();
    public abstract List<String> getMoves();
}
