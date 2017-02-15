package utility;

import model.Node;

/**
 * Created by Adiss on 2017.01.26..
 */
public enum MathUtils {
    INSTANCE;

    public Vector normalizeVector(Node n1, Node n2){
        double x = n2.getCenterX() - n1.getCenterX();
        double y = n2.getCenterY() - n1.getCenterY();
        double length = Math.sqrt(x*x + y*y);
        return new Vector(x/length, y/length);
    }

}
