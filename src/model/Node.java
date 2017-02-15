package model;

import javafx.scene.shape.Circle;
import java.util.Objects;

/**
 * Created by Adiss on 2016.12.17..
 */
public class Node extends Circle {

    /*
    * A gráf egy pontját annak x és y koordinátájával adjuk meg.
    * */
    public Node(double x, double y) {
        this.setCenterX(x);
        this.setCenterY(y);
        this.setRadius(8);
    }

    /*
    * EQUALS & HASHCODE
    * */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return this.getCenterX() == node.getCenterX() &&
                this.getCenterY() == node.getCenterY();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getCenterX(), this.getCenterY());
    }
    /*
    * TO-STRING
    * */

    @Override
    public String toString() {
        return "Node{ x = " + this.getCenterX() + ", y = " + this.getCenterY() + '}';
    }
}
