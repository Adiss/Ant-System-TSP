package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Adiss on 2017.02.10..
 */
public enum Graph {

    INSTANCE;

    List<Node> nodes = new ArrayList<>();
    List<Arc> arcs = new ArrayList<>();

    public List<Node> getAllNodes() { return nodes; }
    public List<Arc> getAllArcs() { return arcs; }

    public List<Arc> getAvailableArcs(Node currentNode, List<Node> prevoiusNodes) {
        return arcs.stream()
                .filter(arc -> arc.getNode1().equals(currentNode) && !prevoiusNodes.contains(arc.getNode2()) || arc.getNode2().equals(currentNode) && !prevoiusNodes.contains(arc.getNode1()))
                .collect(Collectors.toList());
    }

    public Arc getArc(Node n1, Node n2){
        Arc temp = arcs.stream()
                .filter(arc -> arc.getNode1().equals(n1) && arc.getNode2().equals(n2) || arc.getNode2().equals(n1) && arc.getNode1().equals(n2))
                .findFirst()
                .get();
        return temp.getNode1().equals(n1) ? temp : new Arc(n1, temp.getNode1());
    }

    public void addNode(Node node){
        arcs.addAll(this.nodes.stream()
                .map(n -> new Arc(n, node))
                .collect(Collectors.toList())
        );
        nodes.add(node);
    }

    public void clearGraph(){
        nodes = new ArrayList<>();
        arcs = new ArrayList<>();
    }

    public void initializePheromonesOnArcs(int numAnts){
        double nnh = nearestNeighborHeuristic();
        System.out.println("A LEGROVIDEBB UT: " + nnh);
        for(Arc arc : arcs){
            arc.addPheromone(numAnts / nnh);
        }
    }

    private double nearestNeighborHeuristic(){
        List<Node> shortestPath = new ArrayList<>();
        shortestPath.add(nodes.get(0));

        double shortestDistance = Double.MAX_VALUE;
        Arc tempArc = null;
        double solution = 0;

        while(shortestPath.size() < nodes.size()){
            for(Arc arc : getAvailableArcs(shortestPath.get(0), shortestPath)){
                if(arc.getCost() < shortestDistance){
                    tempArc = arc;
                    shortestDistance = arc.getCost();
                }
            }
            shortestPath.add(tempArc.getNode2());
            shortestDistance = Double.MAX_VALUE;
            solution += tempArc.getCost();
        }
        return solution;
    }

    public void evaporatePheromone(){
        double p = 0.5;
        for(Arc arc : arcs){
            arc.addPheromone(-1*((1-p)*arc.getPheromone()));
        }
    }

}
