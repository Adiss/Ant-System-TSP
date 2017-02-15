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
import javafx.scene.paint.Color;
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
    private List<Ant> ants = new ArrayList<>();
    private Random rand = new Random();

    private int antsInBoard = 10;
    private int iterationCounter = 0;
    private int antSpeed = 600;

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

        primaryStage.setTitle("javafx");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void drawGraph() {
        root.getChildren().clear();

        for(Arc arc : Graph.INSTANCE.getAllArcs()){
            Line line = new Line(arc.getNode1().getCenterX(), arc.getNode1().getCenterY(), arc.getNode2().getCenterX(), arc.getNode2().getCenterY());
            if(arc.getPheromone() < 0.002) line.setOpacity(0.2);
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
        numberCities.setText("0");
    }

    private HBox createBottomPanel(){
        HBox bottomPanel = new HBox();
        Button bClearBoard = new Button("Clear");
        Button bStart = new Button("Start");
        Button bAddAnts = new Button("Add Ants");
        Button bStop = new Button("Stop");

        bClearBoard.setOnMouseClicked(event -> clearBoard());

        bAddAnts.setOnMouseClicked(event -> {
            for(int i = 0; i < antsInBoard; i++)
                ants.add(new Ant(Graph.INSTANCE.getAllNodes().get(rand.nextInt(Graph.INSTANCE.getAllNodes().size()))));
        });

        bStart.setOnMouseClicked(event -> {
            Graph.INSTANCE.initializePheromonesOnArcs(antsInBoard);
            drawAnts(ants);
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(antSpeed), ev -> {
                if(iterationCounter % Graph.INSTANCE.getAllNodes().size() == 0) {
                    Graph.INSTANCE.evaporatePheromone();
                    System.out.println("A TALALT UT: " +  ants.stream().min(Comparator.comparing(Ant::getCost)).get().getCost());
                }
                moveAnts(ants);
                iterationCounter++;
            }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        });

        bottomPanel.getStyleClass().add("bottom-panel");
        bottomPanel.getChildren().addAll(bClearBoard, bAddAnts, bStart);

        return bottomPanel;
    }

    private HBox createTopPanel(){
        HBox topPanel = new HBox();

        Text cities = new Text("Cities: ");
        numberCities = new Text("0");

        topPanel.getStyleClass().add("top-panel");
        topPanel.getChildren().addAll(cities, numberCities);

        return topPanel;
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
