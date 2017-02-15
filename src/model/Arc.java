package model;

import java.util.Objects;

/**
 * Created by Adiss on 2016.12.17..
 */
public class Arc {

    /*
    * Egy élt egyértelműen meghatároz annak két pontja.
    * */
    private Node node1;
    private Node node2;

    /*
    * A hangyák által hagyott feromonértékeket az adott él fogja tárolni.
    * Ezt olvassák és írják majd a hangyák.
    * Kezdetben ez egy konstans érték, jelenleg 1.
    * */
    private volatile double pheromone;

    /**
     * Az útnak van egy költsége
     * */
    private double cost;

    public Arc(Node node1, Node node2) {
        this.node1 = node1;
        this.node2 = node2;
        cost = calculateCost();
        pheromone = 0.0;
    }

    /*
    * Minden élnek van hosszúsága.
    * Jelenleg ez lesz az adott él költsége.
    * */
    public double calculateCost(){
        double distanceX = Math.abs(node1.getCenterX() - node2.getCenterX());
        double distanceY = Math.abs(node1.getCenterY() - node2.getCenterY());
        return Math.sqrt(distanceX * distanceX + distanceY * distanceY);
    }

    /*
    * SETTER & GETTER
    * */

    public Node getNode1() {
        return node1;
    }
    public void setNode1(Node node1) {
        this.node1 = node1;
    }

    public Node getNode2() {
        return node2;
    }
    public void setNode2(Node node2) {
        this.node2 = node2;
    }

    public double getPheromone() {
        return pheromone;
    }
    public void addPheromone(double pheromone) {
        this.pheromone += pheromone;
    }

    public double getCost() { return cost; }

    /*
    * EQUALS & HASHCODE
    * */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arc arc = (Arc) o;
        return  (Objects.equals(node1, arc.node1) && Objects.equals(node2, arc.node2)) || (Objects.equals(node1, arc.node2) && Objects.equals(node2, arc.node1));
    }

    @Override
    public int hashCode() {
        return Objects.hash(node1, node2);
    }

    /*
    * TOSTRING
    * */
    @Override
    public String toString() {
        return "Arc{ from = " + node1 + " to = " + node2 + "}\n";
    }
}
