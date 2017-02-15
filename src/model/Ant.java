package model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Adiss on 2017.02.13..
 */
public class Ant extends Circle {

    private static int antCounter = 0;
    private Random rand = new Random();

    private Node currentNode;
    private List<Node> solution;
    private double cost = 0;

    public Ant(Node startNode) {
        this.currentNode = startNode;
        this.solution = new ArrayList<Node>(){{add(startNode);}};
        this.setCenterX(startNode.getCenterX());
        this.setCenterY(startNode.getCenterY());
        this.setRadius(10);
        this.setFill(Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
    }

    public void moveAnt(){
        /*
        * Ha az alpha = 0, akkor a feromonok kimaradnak a döntésből,
        * és csak az úthossz alapján történik a választás
        * */
        double alpha = 1;
        /*
        * Ha a beta = 0, akkor csak a feromonok működnek heurisztika nélkül.
        * Ez átalánosan rosszabb minődégű utakhoz vezet.
        * */
        double beta = 2;

        double probability = Double.MIN_VALUE;
        Node next = null;

        double divider = 0;

        if(solution.size() % Graph.INSTANCE.getAllNodes().size() != 0){
            cost = 0;
            for(Arc arc : Graph.INSTANCE.getAvailableArcs(currentNode, solution))
                divider += Math.pow(arc.getPheromone(), alpha) * Math.pow(1/arc.getCost(), beta);

            for(Arc arc : Graph.INSTANCE.getAvailableArcs(currentNode, solution)){
                double prob = (Math.pow(arc.getPheromone(), alpha) * Math.pow(1/arc.getCost(), beta)) / divider;
                if(probability < prob){
                    probability = prob;
                    if(arc.getNode1().equals(currentNode))
                        next = arc.getNode2();
                    else
                        next = arc.getNode1();
                }
            }

            this.currentNode = next;
            this.solution.add(next);

        } else {
            antPheromoneUpdate();
            for(int i = 0; i < solution.size() - 1; i++){
                cost += Graph.INSTANCE.getArc(solution.get(i), solution.get(i+1)).get().getCost();
            }
            //System.out.println("SOLUTION: " + solution + ", COST: " + cost);
            this.currentNode = solution.get(0);
            this.solution = new ArrayList<Node>(){{add(solution.get(0));}};
        }

    }

    public void antPheromoneUpdate(){
        double divider = 0;
        for(int i = 0; i < solution.size() - 1; i++){
            divider += Graph.INSTANCE.getArc(solution.get(i), solution.get(i+1)).get().getCost();
        }
        for(int i = 0; i < solution.size() - 1; i++){
            Graph.INSTANCE.getArc(solution.get(i), solution.get(i+1)).get().addPheromone(1/divider);
        }
    }

    public List<Node> getSolution() {
        return solution;
    }

    public double getCost() {
        return cost;
    }
}