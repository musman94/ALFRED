import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;


public class SolutionBox {
    final int CELLWIDTH = 90;
    final int CELLHEIGHT = 90;
    BorderPane border;
    ArrayList<Rectangle> cellList;
    Label[] numLabelList;
    int[] blockColors;
    Parser parser;
    GridPane gridPane;



    public void display() throws IOException {
        parser = new Parser();
        cellList = new ArrayList<>();
        blockColors = parser.getColorsOfBlocks();
        numLabelList = new Label[25];

        StackPane layout = new StackPane();

        // add background image
        Image image = new Image(Paths.get("C:/Users/User/Desktop/School/cs461/Project/background2.jpg").toUri().toString(), true);
        layout.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

        Stage window = new Stage();
        window.setResizable( false);
        window.setTitle("Solution");
        window.setHeight(500);
        window.setWidth( 500);


        fillCells();
        createGridPane();

        Label[] chars = new Label[25];
        int row = -1, col = 0;
        for(int i = 0; i < 25; i++){
            if(col % 5 == 0){
                col = 0;
                row += 1;
            }
            if(!parser.getLetters().get(i).equals(" ")) {
                chars[i] = new Label(parser.getLetters("23AprilPuzzleAnswers").get(i));
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
                gridPane.getChildren().add(chars[i]);
            }
        }

        border = new BorderPane();
        border.setCenter( gridPane);
        layout.getChildren().add(border);
        Scene scene = new Scene( layout);
        window.setScene( scene);
        window.showAndWait();

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
            }
            // black cells
            else{
                Rectangle temp2 = new Rectangle(CELLWIDTH, CELLHEIGHT);
                temp2.setFill(Color.BLACK);
                cellList.add(temp2);
            }
            i += 1;
        }
    }
}
