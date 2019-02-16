import org.apache.commons.text.WordUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

public class Clue {
    // Properties
    int xPos;
    int yPos;
    private int number;
    private String value;
    private String text;
    private String[] googleResult;
    private boolean solved;
    private int wordLength;
    private ArrayList<String> candidates;

    // Constructors
    Clue(String value, String text)
    {
        this.value = value;
        this.text = text;
        googleResult = null;
        solved = false;
        wordLength = 0;
        this.number = -1;
        xPos = -1;
        yPos = -1;
    }

    Clue()
    {
        value = "";
        text  = "";
        googleResult = null;
        solved = false;
        wordLength = 0;
        number = -1;
        xPos = -1;
        yPos = -1;
    }

    // Methods
    public void printClue(){
        System.out.println("Clue Number: " + number + "\nValue: " + value + "\nText: " + text + "\nWordLength: " + wordLength + "\nCoordinates: (" + xPos + "," + yPos + ")\n");
    }

    public void setCoordinates(int x, int y){
        xPos = x;
        yPos = y;
    }

    public ArrayList<String> getCandidates() {
        return candidates;
    }

    public void setCandidates(ArrayList<String> candidates) {
        this.candidates = candidates;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getValue()
    {
        return value;
    }

    public String getText()
    {
        return text;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public void setGoogleResult(String[] result)
    {
        googleResult = result;
    }
    public void cleanResults( int j) {
        System.out.println("For clue number " + (j + 1));
        ArrayList<String> candids = new ArrayList<>();
        String candidate = "";
        for( int i = 0; i < googleResult.length; i++) {
            String[] line = googleResult[i].split("\\s+");
            for( int k = 0; k < line.length; k++) {
                line[k] = line[k].replaceAll("[^\\w]", "");
                line[k] = line[k].toUpperCase();
            }
            for( int m = 0; m < line.length; m++) {
                if( line[m].length() == wordLength && line[m].matches("[A-Za-z]+")) {
                    candids.add(line[m]);
                }
            }
        }

        // removing duplicates
        Set<String> candidsWithoutDuplicates = new LinkedHashSet<String>(candids);
        candids.clear();
        candids.addAll(candidsWithoutDuplicates);

        /*for( int i = 0; i < candids.size(); i++) {
            if( candids.get(i).length() != wordLength) {
                candids.remove(i);
            }
        }*/
        //System.out.println("candids size: " + candids.size());
        setCandidates(candids);
        for( int i = 0; i < candids.size(); i++) {
            System.out.println(candids.get(i));
        }
    }

    public String[] getGoogleResult()
    {
        return googleResult;
    }

    public void setSolved(boolean value)
    {
        solved = value;
    }

    public boolean getSolved()
    {
        return solved;
    }

    public void setWordLength(int value)
    {
        wordLength = value;
    }

    public int getWordLength()
    {
        return wordLength;
    }

    public int[] getIntersection(Clue clue){
        if(this.getValue().equals(clue.getValue())){
            return null;  // no intersection
        }

        if(this.getValue() == "Across"){
            int i = this.xPos;
            if(i > clue.xPos)
                return null;
            if(this.wordLength-1 < clue.xPos){
                return null;
            }
            while(i != clue.xPos){
                i++;
            }
            int[] result = {i, yPos};
            return result;
        }
        else{           // DOWN clue
            int i = this.yPos;
            if(i > clue.yPos)
                return null;
            if(this.xPos > clue.xPos + clue.wordLength -1){
                return null;
            }
            while(i != clue.yPos){
                i++;
            }
            int[] result = {xPos, i};
            return result;
        }
    }
    public ArrayList<String> getLetters() {
        ArrayList<String> word = new ArrayList<>();
        for( int i = 0; i < candidates.get(0).length(); i++) {
            word.add(String.valueOf(candidates.get(0).charAt(i)));
        }
        return word;
    }
    public void cleanCandidates() {
        for( int i = 0; i < candidates.size(); i++) {
            if( candidates.get(i) == null)
                candidates.remove(i);
        }
    }
}