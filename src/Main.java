import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Ant;
import model.Arc;
import model.Graph;
import model.Node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Simple ant colony optimization for the Traveling salesman problem.
 * @author Jakab Ádám
 * @version 0.1
 * */
public class Main extends Application {

    private int winWidth = 800;
    private int winHeight = 600;
    private AnchorPane root;
    private Text numberCities;
    private Text numberBestTour;
    private List<Ant> ants = new ArrayList<>();
    private Random rand = new Random();

    private int antsInBoard = 10;
    private int iterationCounter = 0;
    private int antSpeed = 100;

    private Timeline animation;
    private List<Arc> bestTour = new ArrayList<>();
    private double bestTourLength = Double.MAX_VALUE;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        String style = getClass().getResource("/css/style.css").toExternalForm();

        BorderPane window = new BorderPane();
        window.getStylesheets().addAll(style);
        window.setMaxSize(winWidth, winHeight);

        root = new AnchorPane();

        window.setCenter(root);
        window.setBottom(createBottomPanel());
        window.setTop(createTopPanel());

        drawGraph();

        Scene scene = new Scene(window, winWidth, winHeight);
        scene.setOnMousePressed(event -> addNode(event.getSceneX(), event.getSceneY() - 51));

        primaryStage.setTitle("Ant System for Traveling Salesman Problem");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void drawGraph() {
        root.getChildren().clear();

        for(Arc arc : Graph.INSTANCE.getAllArcs()){
            Line line = new Line(arc.getNode1().getCenterX(), arc.getNode1().getCenterY(), arc.getNode2().getCenterX(), arc.getNode2().getCenterY());
            line.setOpacity(0.2);
            if(arc.getPheromone() > 0.0015){
                line.setOpacity(1);
            }
            root.getChildren().add(line);
        }
        for(Node n : Graph.INSTANCE.getAllNodes()){
            Text nodeName = new Text(String.valueOf(n.getCenterX()));
            nodeName.setX(n.getCenterX());
            nodeName.setY(n.getCenterY());
            root.getChildren().addAll(n, nodeName);
        }
    }

    private void addNode(double x, double y){
        if(x < winWidth && x > 0 && y < winHeight - 61 && y > 0){
            Graph.INSTANCE.addNode(new Node(x, y));
            drawGraph();
            numberCities.setText(String.valueOf(Graph.INSTANCE.getAllNodes().size()));
        }
    }

    private void clearBoard(){
        root.getChildren().clear();
        Graph.INSTANCE.clearGraph();
        ants = new ArrayList<>();
        numberCities.setText("0");
    }

    private HBox createBottomPanel(){
        HBox bottomPanel = new HBox();
        bottomPanel.getStyleClass().add("bottom-panel");

        Button bClearBoard = new Button("Clear");
        bClearBoard.setOnMouseClicked(event -> clearBoard());

        Button bStart = new Button("Start");
        bStart.setOnMouseClicked(event -> startAnimation());

        Button bAddAnts = new Button("Add Ants");
        bAddAnts.setOnMouseClicked(event -> addAnts());

        Button bStop = new Button("Stop");
        bStop.setOnMouseClicked(event -> animation.stop());

        bottomPanel.getChildren().addAll(bClearBoard, bAddAnts, bStart, bStop);

        return bottomPanel;
    }

    private HBox createTopPanel(){
        HBox topPanel = new HBox();
        topPanel.getStyleClass().add("top-panel");

        Text cities = new Text("Cities: ");
        numberCities = new Text("0");

        Text bestTour = new Text("Best Tour: ");
        numberBestTour = new Text("No tour yet.");

        topPanel.getChildren().addAll(cities, numberCities, bestTour, numberBestTour);

        return topPanel;
    }

    public void storeBestTour(){
        Ant bestAnt = ants.stream()
                .min(Comparator.comparing(Ant::getCost))
                .get();
        List<Node> bestNodes = bestAnt.getSolution();

        if(bestAnt.getCost() < bestTourLength){
            bestTour = new ArrayList<>();
            bestTourLength = bestAnt.getCost();
            for(int i = 0; i < bestNodes.size() - 1; i++){
                bestTour.add(Graph.INSTANCE.getArc(bestNodes.get(i), bestNodes.get(i+1)));
            }
        }
    }

    private void addAnts(){
        for(int i = 0; i < antsInBoard; i++)
            ants.add(new Ant(Graph.INSTANCE.getAllNodes().get(rand.nextInt(Graph.INSTANCE.getAllNodes().size()))));
        drawAnts(ants);
    }

    private void startAnimation(){
        Graph.INSTANCE.initializePheromonesOnArcs(antsInBoard);
        animation = new Timeline(new KeyFrame(Duration.millis(antSpeed), ev -> {
            if(iterationCounter % Graph.INSTANCE.getAllNodes().size() == 0) {
                Graph.INSTANCE.evaporatePheromone();
            }
            moveAnts(ants);
            iterationCounter++;
        }));
        animation.setCycleCount(Animation.INDEFINITE);
        animation.play();
    }

    private void drawAnts(List<Ant> ants){
        for(Ant ant : ants){
            root.getChildren().add(ant);
        }
    }

    private void moveAnts(List<Ant> ants){
        for(Ant ant : ants){
            Runnable r = () -> {
                //main timeline
                Timeline timeline = new Timeline();
                timeline.setCycleCount(1);

                ant.moveAnt();

                //create a keyValue with factory: move the ant from one node to another
                KeyValue keyValueX = new KeyValue(ant.centerXProperty(), ant.getSolution().get(ant.getSolution().size()-1).getCenterX());
                KeyValue keyValueY = new KeyValue(ant.centerYProperty(), ant.getSolution().get(ant.getSolution().size()-1).getCenterY());

                //create a keyFrame, the keyValue is reached at time 1s
                Duration duration = Duration.millis(antSpeed);
                //one can add a specific action when the keyframe is reached
                EventHandler<ActionEvent> onFinished = t -> {
                    ant.setCenterX(ant.getSolution().get(ant.getSolution().size()-1).getCenterX());
                    ant.setCenterY(ant.getSolution().get(ant.getSolution().size()-1).getCenterY());
                    timeline.getKeyFrames().clear();
                    timeline.playFromStart();
                    drawGraph();
                    drawAnts(ants);
                };

                KeyFrame keyFrame = new KeyFrame(duration, onFinished , keyValueX, keyValueY);

                //add the keyframe to the timeline
                timeline.getKeyFrames().add(keyFrame);
                timeline.play();
            };
            r.run();
        }

    }
}
