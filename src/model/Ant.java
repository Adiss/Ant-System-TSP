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
        antCounter++;
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

        double divider = Graph.INSTANCE.getAvailableArcs(currentNode, solution).stream()
                            .mapToDouble(arc -> Math.pow(arc.getPheromone(), alpha) * Math.pow(1/arc.getCost(), beta))
                            .sum();

        if(solution.size() % Graph.INSTANCE.getAllNodes().size() != 0){
            cost = 0;

            Arc nextArc = Graph.INSTANCE.getAvailableArcs(currentNode, solution).stream()
                            .max((arc1, arc2) -> Double.compare(countProbability(arc1, alpha, beta, divider), countProbability(arc2, alpha, beta, divider)))
                            .get();

            this.currentNode = nextArc.getNode1().equals(currentNode) ? nextArc.getNode2() : nextArc.getNode1();
            this.solution.add(currentNode);

        } else {
            this.currentNode = solution.get(0);
            solution.add(currentNode);
            antPheromoneUpdate();
            for(int i = 0; i < solution.size() - 1; i++){
                cost += Graph.INSTANCE.getArc(solution.get(i), solution.get(i+1)).getCost();
            }
            this.solution = new ArrayList<Node>(){{add(solution.get(0));}};
        }

    }

    /*
    public void moveAnt(){

        double alpha = 1;

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
            this.currentNode = solution.get(0);
            solution.add(currentNode);
            antPheromoneUpdate();
            for(int i = 0; i < solution.size() - 1; i++){
                cost += Graph.INSTANCE.getArc(solution.get(i), solution.get(i+1)).get().getCost();
            }
            this.solution = new ArrayList<Node>(){{add(solution.get(0));}};
        }

    }
    */

    public void antPheromoneUpdate(){
        double divider = 0;
        for(int i = 0; i < solution.size() - 1; i++){
            divider += Graph.INSTANCE.getArc(solution.get(i), solution.get(i+1)).getCost();
        }
        for(int i = 0; i < solution.size() - 1; i++){
            Graph.INSTANCE.getArc(solution.get(i), solution.get(i+1)).addPheromone(1/divider);
        }
    }

    private double countProbability(Arc arc, double alpha, double beta, double divider){
        return (Math.pow(arc.getPheromone(), alpha) * Math.pow(1/arc.getCost(), beta)) / divider;
    }

    public List<Node> getSolution() {
        return solution;
    }

    public double getCost() {
        return cost;
    }
}
