import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main extends Application{

    final int WIDTH = 1150;
    final int HEIGHT = 600;

    final int CELLWIDTH = 50;
    final int CELLHEIGHT = 50;
    Stage window;
    Scene scene1, scene2;
    Button button1;
    Label date, solutionLabel;
    BorderPane border;
    ArrayList<String> clueList;
    ArrayList<Label> labelList;
    ArrayList<Rectangle> cellList, solutionCellList;
    ArrayList<TextArea> textList;
    Label[] numLabelList, solutionNumLabelList;
    Label[] letterList;
    int[] blockColors;
    Parser parser;
    Button solution;
    Label across, down, trace;
    GridPane gridPane, solutionGridPane;
    VBox cluePane, crossword, solutionCrossword;
    HBox mainLayout;
    SolutionBox box;
    Rectangle traceBox;
    Scrapper scrapper;
    ArrayList<Clue> clues;
    public TextArea test;
    public String text = "";
    String answerFile = "24AprilPuzzleAnswers";

    ArrayList<Constraint> constraints;

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        parser = new Parser();
        clues = parser.clues;
        scrapper = new Scrapper(clues, this);

        scrapper.scrap();


        box = new SolutionBox();
        window = primaryStage;
        border = new BorderPane();

        StackPane layout = new StackPane();

        // add background image
        Image image = new Image(Paths.get("C:/Users/Unas/Desktop/Courses/CS342 - AI/Project/Project/background.jpg").toUri().toString(), true);
        layout.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

        // initialize
        solutionLabel = new Label("Crossword Solution");
        trace = new Label( "Trace Box");
        // solutionLabel.setId("date-text");
        //solutionLabel.setTextAlignment(TextAlignment.CENTER);
        //solutionLabel.setPadding(new Insets(20, 300, 30, 10));
        letterList = new Label[25];

        for( int i = 0; i < letterList.length; i++) {
            letterList[i] = null;
        }

        constraints = new ArrayList<>();
        for(Clue clue : clues){
            constraints.add(new Constraint(clue));
        }
        while( !isSolved()) {
            satisfyConstraints();
            for(Clue clue: clues){
                clue.cleanCandidates();
                System.out.println("\n\nClue:\n" + clue.getCandidates().toString() );
            }
        }

        clueList = new ArrayList<>();
        clueList = parser.getAllHints();
        labelList = new ArrayList<>();
        cellList = new ArrayList<>();
        solutionCellList = new ArrayList<>();
        textList = new ArrayList<>();
        blockColors = parser.getColorsOfBlocks();
        numLabelList = new Label[25];
        solutionNumLabelList = new Label[25];
        test = new TextArea();
        test.setPrefHeight(180);
        test.setPrefWidth(400);
        traceBox = new Rectangle();
        traceBox.setHeight(180);
        traceBox.setWidth(400);
        traceBox.setFill(Color.WHITE);
        traceBox.setStrokeWidth(3.0);
        traceBox.setStroke(Color.BLACK);

        for(int i = 0; i < 10; i++){
            labelList.add( new Label( clueList.get(i) ));
        }

        // style labels
        styleLabels();

        // Create Cells
        fillCells();
        fillSolutionCells();

        // creating grid and adding cells
        createGridPane();
        createSolutionGridPane();

        // create HBoxes
        // adding clues to VBox
        addToVbox();
        createHBoxes();

        /*for( int i = 0; i < textList.size(); i++) {
            textList.get(i).setPrefWidth(10);
            textList.get(i).setPrefHeight(10);
        }*/


        // add to borderpane
       // border.setTop(label_and_button);
        //border.setLeft(crossword);
        border.setCenter(mainLayout);
        border.setPadding(new Insets(40, 50, 10, 50));

        layout.getChildren().add(border);

        // add style
        String style = this.getClass().getResource("style.css").toExternalForm();
        scene1 = new Scene(layout, WIDTH, HEIGHT);
        scene1.getStylesheets().add(style);

        window.setScene(scene1);
        window.setTitle("NYT Mini Crossword");
        window.show();
    }
    public boolean isSolved() {
        boolean solved = true;
        for( Clue clue : clues) {
            if( clue.getCandidates().size() > 1)
                solved = false;
        }
        return solved;
    }
    public void showSolution() throws IOException {
        //TextArea area = new TextArea();
        /*String text = "";
        for( int i = 0; i < scrapper.getTrace().size(); i++) {
            text += scrapper.getTrace().get(i);
            text += "\n";
        }
        test.setText(text);*/
        //test.setText("HELLO");

        //scrapper.scrap();

        for (int i = 0; i < clues.size(); i++) {
            if (clues.get(i).getValue().equals("Across")) {
                int y = clues.get(i).yPos;
                int x = clues.get(i).xPos;
                Label[] first = new Label[clues.get(i).getWordLength()];
                for (int j = 0; j < first.length; j++) {
                    first[j] = new Label(clues.get(i).getLetters().get(j));
                    first[j].setFont(new Font(20));
                    if( cellIsEmpty(x, y)) {
                        letterList[5*y + x] = first[j];
                        gridPane.add(letterList[5*y + x], x, y);
                        gridPane.setHalignment(letterList[5*y + x], HPos.CENTER);
                        //letterList[5*y + x] = first[j];
                    }
                    else {
                        clearTheCell(x, y);
                        letterList[5*y + x] = first[j];
                        gridPane.add(letterList[5*y + x], x, y);
                        gridPane.setHalignment(letterList[5*y + x], HPos.CENTER);
                        //letterList[5*y + x] = first[j];
                    }
                    x++;
                }
            }
           // System.out.println("cell is empty at 1, 4 " + cellIsEmpty(1, 4));
           // System.out.println("cell is empty at 4, 0 " + cellIsEmpty(4, 0));
            else if( clues.get(i).getValue().equals("Down")) {
                int y = clues.get(i).yPos;
                int x = clues.get(i).xPos;
                Label[] first = new Label[clues.get(i).getWordLength()];
                for (int j = 0; j < first.length; j++) {
                    first[j] = new Label(clues.get(i).getLetters().get(j));
                    first[j].setFont(new Font(20));
                    if( cellIsEmpty(x, y)) {
                        letterList[5*y + x] = first[j];
                        gridPane.add(letterList[5*y + x], x, y);
                        gridPane.setHalignment(letterList[5*y + x], HPos.CENTER);
                        //letterList[5*y + x] = first[j];
                    }
                    else {
                        clearTheCell(x, y);
                        letterList[5*y + x] = first[j];
                        gridPane.add(letterList[5*y + x], x, y);
                        gridPane.setHalignment(letterList[5*y + x], HPos.CENTER);
                        //letterList[5*y + x] = first[j];
                    }
                    y++;
                }
            }
        }
    }
    public boolean cellIsEmpty(int row, int col) { // works fine
         return letterList[5*col + row] == null;
    }
    public void clearTheCell( int row, int col) {
        letterList[5*col + row].setText(" ");
    }
    public void setTextForTrace(String value) {
        value = this.text + "\n";
        this.text = value;
        test.setText(value);
    }

    public void styleLabels() throws IOException {
        // date label
        date = new Label(parser.getPuzzleDate());
        date.setId("date-text");
        solutionLabel.setId("date-text");
        trace.setId("date-text");

        // solution button
        solution = new Button("Solve");
        solution.setId("green");
        solution.setPrefSize(250, 50);
        solution.setOnAction(e -> {
            try {
                showSolution();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        solution.setOnMouseEntered(new EventHandler<MouseEvent>
                () {

            @Override
            public void handle(MouseEvent t) {
                solution.setStyle("-fx-background-color:#c3c4c4;");
            }
        });
        solution.setOnMouseExited(new EventHandler<MouseEvent>
                () {

            @Override
            public void handle(MouseEvent t) {
                solution.setStyle("-fx-background-color:\n" +
                        "        #dae7f3,\n" +
                        "        linear-gradient(#d6d6d6 50%, white 100%),\n" +
                        "        radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%);");
            }
        });

        // across and down labels
        across = new Label("Across");
        across.setId("across_and_down");
        down = new Label("Down");
        down.setId("across_and_down");
    }

    public void createGridPane() {
        gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(0);
        gridPane.setHgap(0);

        int row = -1, col = 0;
        for(int i = 0; i < 25; i++){
            if(col % 5 == 0){
                col = 0;
                row += 1;
            }
            GridPane.setConstraints(cellList.get(i), col, row);
            //GridPane.setConstraints(textList.get(i), col, row);
            col += 1;
        }

        for(Rectangle rect : cellList) {
            gridPane.getChildren().add(rect);
        }

        // making number labels for blocks
        row = -1; col = 0;
        for(int i = 0; i < 25; i++){
            if(col % 5 == 0){
                col = 0;
                row += 1;
            }
            if(parser.getNumbersOfBlocks()[i] != 0) {
                numLabelList[i] = new Label(" " + String.valueOf(parser.getNumbersOfBlocks()[i]));
                GridPane.setConstraints(numLabelList[i], col, row);
                GridPane.setValignment(numLabelList[i], VPos.TOP);
            }
            else{
                numLabelList[i] = null;
            }
            col += 1;
        }

        for(int i = 0; i < 25; i++){
            if(numLabelList[i] != null){
                gridPane.getChildren().add(numLabelList[i]);
            }
        }

        row = -1; col = 0;
        for(int i = 0; i < 25; i++){
            if(col % 5 == 0){
                col = 0;
                row += 1;
            }
            //GridPane.setConstraints(textList.get(i), col, row);
            //GridPane.setHalignment(textList.get(i), HPos.CENTER);
            col += 1;
        }

        for( int i = 0; i < 25; i++) {
            if( cellList.get(i).getFill() != Color.BLACK) {
                //gridPane.getChildren().add(textList.get(i));
            }
        }
    }

    public void fillCells() {
        int i = 0;
        for (int value : blockColors) {
            // white cells
            if(value == 0){
                Rectangle temp1 = new Rectangle(CELLWIDTH, CELLHEIGHT);
                temp1.setFill(Color.WHITE);
                temp1.setStroke(Color.BLACK);
                temp1.setStrokeWidth(1);
                cellList.add(temp1);
                //TextArea area = new TextArea();
                //area.setMaxSize( 10, 10);
                //area.setPrefHeight(10);
                //area.setPrefWidth(10);
                //textList.add(area);
            }
            // black cells
            else{
                Rectangle temp2 = new Rectangle(CELLWIDTH, CELLHEIGHT);
                temp2.setFill(Color.BLACK);
                cellList.add(temp2);
                //textList.add(new TextArea());
            }
            i += 1;
        }
    }

    public void createSolutionGridPane() throws IOException {
        solutionGridPane = new GridPane();
        solutionGridPane.setPadding(new Insets(10, 10, 10, 10));
        solutionGridPane.setVgap(0);
        solutionGridPane.setHgap(0);

        int row = -1, col = 0;
        for(int i = 0; i < 25; i++){
            if(col % 5 == 0){
                col = 0;
                row += 1;
            }
            GridPane.setConstraints(solutionCellList.get(i), col, row);
                col += 1;
        }

        for(Rectangle rect : solutionCellList) {
            solutionGridPane.getChildren().add(rect);
        }

        // making number labels for blocks
        row = -1; col = 0;
        for(int i = 0; i < 25; i++){
            if(col % 5 == 0){
                col = 0;
                row += 1;
            }
            if(parser.getNumbersOfBlocks()[i] != 0) {
                solutionNumLabelList[i] = new Label(" " + String.valueOf(parser.getNumbersOfBlocks()[i]));
                GridPane.setConstraints(solutionNumLabelList[i], col, row);
                GridPane.setValignment(solutionNumLabelList[i], VPos.TOP);
            }
            else{
                solutionNumLabelList[i] = null;
            }
            col += 1;
        }
        for(int i = 0; i < 25; i++){
            if(solutionNumLabelList[i] != null){
                solutionGridPane.getChildren().add(solutionNumLabelList[i]);
            }
        }

        addLetters();
    }
    public void addLetters() throws IOException {
        Label[] chars = new Label[25];
        int row = -1, col = 0;
        for(int i = 0; i < 25; i++){
            if(col % 5 == 0){
                col = 0;
                row += 1;
            }
            if(!parser.getLetters().get(i).equals(" ")) {
                chars[i] = new Label(parser.getLetters(answerFile).get(i));
                chars[i].setFont(new Font(20));
                GridPane.setConstraints(chars[i], col, row);
                GridPane.setHalignment(chars[i], HPos.CENTER);
            }
            else{
                chars[i] = null;
            }
            col += 1;
        }

        for(int i = 0; i < 25; i++){
            if(chars[i] != null){
                solutionGridPane.getChildren().add(chars[i]);
            }
        }
    }

    public void fillSolutionCells() {
        int i = 0;
        for (int value : blockColors) {
            // white cells
            if(value == 0){
                Rectangle temp1 = new Rectangle(CELLWIDTH, CELLHEIGHT);
                temp1.setFill(Color.WHITE);
                temp1.setStroke(Color.BLACK);
                temp1.setStrokeWidth(1);
                solutionCellList.add(temp1);
            }
            // black cells
            else{
                Rectangle temp2 = new Rectangle(CELLWIDTH, CELLHEIGHT);
                temp2.setFill(Color.BLACK);
                solutionCellList.add(temp2);
            }
            i += 1;
        }
    }

    public void addToVbox() {
        cluePane = new VBox(10);
        cluePane.getChildren().add(across);

        for( int i = 0; i < 10; i++){
            if(i == 5)
                cluePane.getChildren().add(down);
            cluePane.getChildren().add(labelList.get(i));
        }

        crossword = new VBox( 20);
        crossword.getChildren().addAll( date, gridPane, trace, test); // also will be added the trace box

        solutionCrossword = new VBox( 20);
        solutionCrossword.getChildren().addAll(solutionLabel, solutionGridPane, solution);


    }

    public void createHBoxes() {
        mainLayout = new HBox();
        mainLayout.getChildren().addAll(crossword, cluePane, solutionCrossword);
        mainLayout.setSpacing(50);
    }

    public void satisfyConstraints(){
        for(int c = 0; c < constraints.size(); c++){
            System.out.println("c value: " + c);
            Clue clue1 = constraints.get(c).clue;
            System.out.println("clue1: " + clue1.getText());
            for(int n = 0; n < constraints.get(c).nodes.size(); n++){
                System.out.println("n value: " + n);
                Node node = constraints.get(c).nodes.get(n);
                Clue clue2 = node.clue;
                System.out.println("clue2: " + clue2.getText());
                int[] intersect = node.intersection;
                //if(clue1.xPos == intersect[0]){
                if(clue1.getValue().equals("Across")){
                    //clue1 is across
                    System.out.println("intersect[0]: " + intersect[0] + " intersect1: " + intersect[1]);
                    int index1 = intersect[0] - clue1.xPos; // clue1 char position
                    int index2 = intersect[1] - clue2.yPos; // clue2 char position
                    for(int cand1 = 0; cand1 < clue1.getCandidates().size(); cand1++){
                        boolean eliminate = true;
                        String candidate1 = clue1.getCandidates().get(cand1);
                        if(candidate1 == null)
                            continue;
                        for(int cand2 = 0; cand2 < clue2.getCandidates().size(); cand2++){
                            String candidate2 = clue2.getCandidates().get(cand2);
                            System.out.println("Index1: " + index1 + " index2: " + index2 + " candi1: " + candidate1 + " candi2: " + candidate2);
                            if(candidate2 != null) {
                                if (candidate1.charAt(index1) == candidate2.charAt(index2)) {
                                    eliminate = false;
                                }
                            }
                        }
                        if(eliminate){
                            clue1.getCandidates().set(cand1, null);
                        }
                    }
                }
                else{
                    // clue1 is down
                    int index1 = intersect[1] - clue1.yPos;
                    int index2 = intersect[0] - clue2.xPos;
                    for(int cand1 = 0; cand1 < clue1.getCandidates().size(); cand1++){
                        boolean eliminate = true;
                        String candidate1 = clue1.getCandidates().get(cand1);
                        if(candidate1 == null)
                            continue;
                        for(int cand2 = 0; cand2 < clue2.getCandidates().size(); cand2++){
                            String candidate2 = clue2.getCandidates().get(cand2);
                            System.out.println("Index1: " + index1 + " index2: " + index2 + " candi1: " + candidate1 + " candi2: " + candidate2);
                            if(candidate2 != null) {
                                if (candidate1.charAt(index1) == candidate2.charAt(index2)) {
                                    eliminate = false;
                                }
                            }
                        }
                        if(eliminate){
                            clue1.getCandidates().set(cand1, null);
                        }
                    }
                }
            }
        }
    }










    private class Node{
        Clue clue;
        int[] intersection;
    }




    private class Constraint{
        Clue clue;
        ArrayList<Node> nodes;

        public Constraint(Clue clue){
            this.clue = clue;
            nodes = new ArrayList<>();
            for(int i = 0; i < clues.size(); i++){
                Clue temp = clues.get(i);
                int[] intersection = clue.getIntersection(temp);
                if(intersection != null){
                    Node node = new Node();
                    node.clue = temp;
                    node.intersection = intersection;
                    nodes.add(node);
                }
            }
        }

    }



}
