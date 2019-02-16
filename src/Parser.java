import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Parser {

    // variables
    ArrayList<Clue> clues;
    ArrayList<String> hints;
    int[] colorsOfBlocks;
    int[] numbersOfBlocks;
    ArrayList<String> hints_across;
    ArrayList<String> hints_down;
    ArrayList<String> letters;

    String html;
    String date;
    String textFile = "24AprilPuzzleCode";
    String answersFile = "24AprilPuzzleAnswers";

   /* public static void main(String[] args) throws IOException {
        Parser parser = new Parser();

        for( int i = 0; i < parser.clues.size(); i++) {
            parser.clues.get(i).printClue();
        }

        Scrapper scrapper = new Scrapper(parser.clues);
    }*/


    public Parser() throws IOException {
        // initialization
        clues = new ArrayList<>();
        hints = new ArrayList<String>();
        hints_across = new ArrayList<String>();
        hints_down = new ArrayList<String>();
        colorsOfBlocks = new int[25];
        numbersOfBlocks = new int[25];
        letters = new ArrayList<String>();

        // use for today's puzzle
        //html = fetch("https://www.nytimes.com/crosswords/game/mini");

        // use for old puzzles
        convertToString();

    }

    // getters and setters
    public void setHTMLString(String html) {
        this.html = html;
    }
    public String getHTMLString() {
        return html;
    }
    public int[] getNumbersOfBlocks() {
        return numbersOfBlocks;
    }
    public int[] getColorsOfBlocks() {
        return colorsOfBlocks;
    }
    public ArrayList<Clue> getClues() { return clues; }
    public void setClues(ArrayList<Clue> clues) { this.clues = clues; }
    public ArrayList<String> getAllHints() {
        return hints;
    }
    public ArrayList<String> getLetters() {
        return letters;
    }
    public ArrayList<String> getAcrossHints() {
        for( int i = 0; i < 5; i++) {
            hints_across.add( hints.get(i));
        }
        for( int i = 0; i < 5; i++) {
            System.out.println(hints_across.get(i));
        }
        return hints_across;
    }
    public ArrayList<String> getDownHints() {
        for( int i = 5; i < 10; i++) {
            hints_down.add( hints.get(i));
        }
        for( int i = 0; i < 5; i++) {
            System.out.println(hints_down.get(i));
        }
        return hints_across;
    }
    public void setPuzzleDate( String date) {
        this.date = date;
    }
    public String getPuzzleDate() {
        return date;
    }

    // for using old puzzles
    public void convertToString() throws IOException {
        String entireFileText = new Scanner(new File(textFile + ".txt")).useDelimiter("\\A").next();
        setHTMLString(entireFileText);
        getHints(entireFileText);
        getColors(entireFileText);
        getNumbers(entireFileText);
        getLetters(answersFile);
        getPuzzleDate( entireFileText);
        setWordLengths();
        setCoordinates();
    }


    // this method fetchs the html code from the website and writes it to the textfile (FOR TODAY)
    public String fetch(String html) {
        try {
            Document doc = Jsoup.connect(html).get();
            String htmlDocument = doc.toString();
            Elements links = doc.select("link");
            Elements scripts = doc.select("script");
            for (Element element : links) {
                htmlDocument += element.absUrl("href");
            }
            for (Element element : scripts) {
                htmlDocument += element.absUrl("src");
            }

            // save html code in the text file
            String path = "C:/Users/User/Desktop/School/cs461/Project/" + textFile + ".txt";
            Files.write(Paths.get(path), htmlDocument.getBytes(), StandardOpenOption.CREATE);

            getHints(htmlDocument);
            getColors(htmlDocument);
            getNumbers(htmlDocument);
            scrapeLetters(); // gets the solution and adds it into the letters arrayList
            setWordLengths();
            setCoordinates();

            return htmlDocument;

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    // method gets the solution of today's puzzle
    public void scrapeLetters() throws IOException {
        // opens crhome
        System.setProperty("webdriver.chrome.driver", "C:/Users/User/Desktop/School/chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.nytimes.com/crosswords/game/mini"); // done

        // presses ok button
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[3]/div/main/div[2]/div/div[2]/div[2]/article/div[2]/button/div/span")).click(); // done

        // presses reveal button
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[3]/div/main/div[2]/div/div/ul/div[1]/li[2]/button")).click(); // done

        // presses puzzle button
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div/div[3]/div/main/div[2]/div/div/ul/div[1]/li[2]/ul/li[3]/a")).click(); // done

        // presses second reveal button
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/article/div[2]/button[2]/div/span")).click(); // done

        // presses x button
        driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div[2]/div/a")).click(); // done

        // copy solutions
        List<WebElement> allLinks = driver.findElements(By.tagName("text"));
        System.out.println("Links count is: "+ allLinks.size());
        String answers = "";
        String path = "C:/Users/User/Desktop/School/cs461/Project/" + answersFile + ".txt";
        for(WebElement link : allLinks) {
            if( !link.getText().matches("-?\\d+")) {
                answers += link.getText();
                answers += "\n";
            }
        }

        // save answers in the text file
        Files.write(Paths.get(path), answers.getBytes(), StandardOpenOption.CREATE);
        getLetters(answersFile);

    }

    public void getPuzzleDate( String html) {
        Document document = Jsoup.parse( html);
        Elements elements = document.getElementsByClass("PuzzleDetails-date--1HNzj");
        setPuzzleDate( elements.text());
    }

    public void getHints(String html) {
        Document document = Jsoup.parse(html);
        Elements numbers = document.getElementsByClass("Clue-label--2IdMY");
        Elements clues = document.getElementsByClass("Clue-text--3lzl7");

        for (int i = 0; i < numbers.size() && i < clues.size(); i++) {
            hints.add(numbers.get(i).text() + ". " + clues.get(i).text());
            System.out.println(numbers.get(i).text() + ". " + clues.get(i).text());
            if(i < 5) {
                Clue temp = new Clue("Across", clues.get(i).text());
                temp.setNumber(Integer.parseInt(numbers.get(i).text()));
                this.clues.add(temp);
            }
            else{
                Clue temp = new Clue("Down", clues.get(i).text());
                temp.setNumber(Integer.parseInt(numbers.get(i).text()));
                this.clues.add(temp);
            }
        }
    }

    // extract the grid part from the text file
    public String cutHTML(String html) {
        String startIndex = "<g data-group=\"cells\"";
        String endIndex = "<g data-group=\"grid\"";

        int start = html.indexOf(startIndex) + startIndex.length();
        int end = html.indexOf(endIndex) - 1;

        return html.substring(start, end);
    }

    // i need to get colors for blocks
    public void getColors(String html) {
        int size = 25;

        // cutting html only cell parts left
        String newHTML = cutHTML(html);

        for (int i = 0; i < size; i++) {
            int indexStart = newHTML.indexOf("class=\"") + 7;
            int indexEnd = indexStart + 16;
            String color = newHTML.substring(indexStart, indexEnd);

            if (color.equals("Cell-block--1oNa"))
                colorsOfBlocks[i] = 1; //black
            else
                colorsOfBlocks[i] = 0; //white

            newHTML = newHTML.substring(indexEnd);
        }
        for (int i = 0; i < 25; i++)
            System.out.print(colorsOfBlocks[i] + " ");
        System.out.println("end");
    }

    // i need to get numbers for blocks
    public void getNumbers(String html) {
        int size = 25;

        String newHTML = cutHTML(html);
        Document document = Jsoup.parse(newHTML);
        Elements elements = document.getElementsByTag("g");

        for (int i = 0; i < elements.size() && i < size; i++) {
            String text = elements.get(i).text();
            if (!text.isEmpty()) {
                text = text.substring(0, 1);
                if( text.matches("-?\\d+"))
                    numbersOfBlocks[i] = Integer.parseInt(text);
                else
                    numbersOfBlocks[i] = 0;
            } else
                numbersOfBlocks[i] = 0;
        }
        for (int i = 0; i < 25; i++)
            System.out.print(numbersOfBlocks[i] + " ");
        System.out.println("end");
    }

    public ArrayList<String> getLetters(String answersFile) throws IOException { // for old puzzles, scrapes the answers from the textfile
        //String file = new Scanner(new File(answersFile + ".txt")).useDelimiter("\\A").next();
        BufferedReader reader = new BufferedReader(new FileReader(answersFile + ".txt"));
        String line = "";
        for( int i = 0; i < 25; i++) {
            if( colorsOfBlocks[i] == 1)
                letters.add(" ");
            else {
                if( (line = reader.readLine()) != null)
                    letters.add(line);
            }
        }
        reader.close();
        /*for( int i = 0; i < letters.size(); i++) {
            System.out.println(letters.get(i));
        }*/
        return letters;
    }
    // get the number of letters for the answer to a across clue
    public void setWordLengths(){
        for(int x = 0; x < clues.size(); x++){  // for each clue in the clues list
            int index = clues.get(x).getNumber();   // index = number of clue
            int i = 0;
            int count = 0;
            while(numbersOfBlocks[i] != index){ // find the position of index in the numbersOfBlocks list
                i++;
            }
            // Across Clues
            if(clues.get(x).getValue().equals("Across")) {
                do {
                    count++;
                    i++;
                }
                while (i % 5 != 0 && colorsOfBlocks[i] != 1); // while we are in the same row and block color is not black
                clues.get(x).setWordLength(count);
            }
            // Down Clues
            else{
                do{
                    i += 5;
                    count++;
                }while( i<25 && colorsOfBlocks[i] != 1); // while within the grid and block is not black
                clues.get(x).setWordLength(count);
            }
        }
    }

    public void setCoordinates(){
        for(int i = 0; i < clues.size(); i++){ // for each clue
            Clue temp = clues.get(i);           // get the clue
            int number = temp.getNumber();      // get the number of clue
            int index;
            for(index = 0; index < 25; index++){
                if(numbersOfBlocks[index] == number)
                    break;
            }
            int x = index % 5;
            int y = index / 5;
            clues.get(i).setCoordinates(x, y);
        }
    }
}


    /*public ArrayList<String> getLetters() { // for current puzzles
        /*int size = 25;
        char[] temp = new char[25];
        int j = 0;

        String newHTML = cutHTML(html);

        for (int i = 0; i < size; i++) {
            int indexStart = newHTML.indexOf("66.67\"") + 7;
            int indexEnd = indexStart + 1;
            String letter = newHTML.substring(indexStart, indexEnd);
            temp[i] = letter.charAt(0);
            newHTML = newHTML.substring(indexEnd);
        }
        for( int i = 0; i < size; i++) {
            if( colorsOfBlocks[i] == 1)
                lettersOfBlocks[i] = ' ';
            else {
                lettersOfBlocks[i] = temp[j];
                j++;
            }
        }
        for (int i = 0; i < 25; i++)
            System.out.print(lettersOfBlocks[i] + " ");
        System.out.println("end");*/
       /* return letters;
    }*/
