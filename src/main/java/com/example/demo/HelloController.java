package com.example.demo;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class HelloController {
    @FXML
    private Label label1, labelWords, labelCheck, labelTheme, labelTime, labelMultiplier, labelScore, labelWordNum;
    @FXML
    private TextField txtRow, txtCol;
    @FXML
    private GridPane gdpPlayGrid;
    @FXML
    private ChoiceBox cbRow, cbCol;
    @FXML
    private Button btnStart, btnInitialize, btnConfirm;
    @FXML
    private ListView lstWords,lstDifficulty;
    private GridSpot[][] board;
    private Button[][] boardSpotsBTN;
//    private GridSpot[][] board = new GridSpot[10][10];
//    private Button[][] boardSpotsBTN = new Button[10][10];
    private int rowIndex, colIndex;
    private boolean top, right, bottom, left, topRight, bottomRight, bottomLeft, topLeft, noFit;
    private ArrayList<String> words = new ArrayList<>();
    private int countNum = 0;
    private ArrayList<Integer> chosenSpots = new ArrayList<>();
    private String chosenText = "";
    private ArrayList<Stage> stage = new ArrayList<>();
    private int limitNum = 0;
    private long startTime, endTime;
    private Stage chosenStage;
    private double multiplier;

    public HelloController(){
        stage.add(new Stage("easy", "Foods", 3, 20));
        stage.add(new Stage("normal", "Sports", 5, 25));
        stage.add(new Stage("hard", "Country", 10, 30));
        startTime = 0;
        endTime = 0;
        top = false;
        right = false;
        bottom = false;
        left = false;
        topRight = false;
        bottomRight = false;
        bottomLeft = false;
        topLeft = false;
    }
    @FXML
    public void handleStartGame(){
        btnInitialize.setDisable(true);
        lstDifficulty.setDisable(false);
        lstDifficulty.getItems().add(stage.get(0).getStageName());
        lstDifficulty.getItems().add(stage.get(1).getStageName());
        lstDifficulty.getItems().add(stage.get(2).getStageName());

    }
    @FXML
    public void handleGrid(){
        btnConfirm.setDisable(false);
    }
    @FXML
    public void handleConfirm(){
        lstDifficulty.setDisable(true);
        btnStart.setDisable(false);
        btnConfirm.setDisable(true);
        cbRow.setDisable(true);
        cbCol.setDisable(true);
    }


    @FXML
    public void handleTheme(){
        cbRow.setDisable(false);
        cbCol.setDisable(false);
        labelTheme.setText(selectedStage().getTheme());
        chosenStage = selectedStage();
        cbRow.getItems().clear();
        cbCol.getItems().clear();
        int countNum = 0;
        for(int i=chosenStage.getMinSide(); i<=chosenStage.getMaxSide(); i++){
            cbRow.getItems().add(chosenStage.getMinSide() + countNum);
            cbCol.getItems().add(chosenStage.getMinSide() + countNum);
            countNum++;
        }
    }

    @FXML
    public void handleGiveUp(){
        btnStart.setDisable(false);
    }


    @FXML
    public void handleStart() {
        btnStart.setDisable(true);
        startTime = System.nanoTime();
        int rowNum = Integer.parseInt(cbRow.getSelectionModel().getSelectedItem().toString());
        int colNum = Integer.parseInt(cbCol.getSelectionModel().getSelectedItem().toString());
        words = chosenStage.getGridWords();
        multiplier = findMultiplier(rowNum, colNum);
        labelMultiplier.setText("Multiplier : x " + roundNum(2, multiplier));
        board = new GridSpot[rowNum][colNum];
        boardSpotsBTN = new Button[rowNum][colNum];
//        board = new GridSpot[selectedStage().getSideLength()][selectedStage().getSideLength()];
//        boardSpotsBTN = new Button[selectedStage().getSideLength()][selectedStage().getSideLength()];
        resetEverything();
        handleLoad();
        selectedStage().setMaxWord(selectedStage().getGridWords().size());
        System.out.println(chosenStage.getGridWords());

        for(String word : words){
            lstWords.getItems().add(word);
        }
        countNum = 0;
//        gdpPlayGrid.setHgap(10);
//        gdpPlayGrid.setVgap(10);
//        gdpPlayGrid.setPadding(new Insets(20));
        gdpPlayGrid.setGridLinesVisible(true);
//        gdpPlayGrid.setAlignment(Pos.CENTER);

        for(int i=0; i<boardSpotsBTN.length; i++){
            for(int j=0; j<boardSpotsBTN[0].length; j++){
                boardSpotsBTN[i][j] = new Button();
                board[i][j] = new GridSpot();
                board[i][j].clickedSpot("-");
                boardSpotsBTN[i][j].setMinHeight(27);
                boardSpotsBTN[i][j].setMinWidth(27);
                boardSpotsBTN[i][j].setPrefHeight(27);
                boardSpotsBTN[i][j].setPrefWidth(27);
                gdpPlayGrid.add(boardSpotsBTN[i][j], j, i);
            }
        }
        createBoard();
        if(noFit){
            return;
        }
        putWordGrid();
        if(countNum == words.size()-1){
//            fillGrid();
        }
        EventHandler z = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                rowIndex = GridPane.getRowIndex(((Button) t.getSource()));
                colIndex = GridPane.getColumnIndex(((Button) t.getSource()));
                labelCheck.setText("");
                if (chosenSpots.size() >= 4) {
                    chosenSpots.clear();
                }
                chosenSpots.add(rowIndex);
                chosenSpots.add(colIndex);
                labelWords.setText(board[rowIndex][colIndex].getBtnText());
                if(chosenSpots.size() == 4){
                    chooseWord();
                }
            }
        };
        for(int i=0; i<boardSpotsBTN.length; i++){
            for(int j=0; j<boardSpotsBTN[0].length; j++){
                boardSpotsBTN[i][j].setOnAction(z);
            }
        }
    }

    public void putWordGrid(){
        for(int i=0; i<board.length; i++){
            for(int j=0; j<board[0].length; j++){
                boardSpotsBTN[i][j].setText(board[i][j].getBtnText());
            }
        }
    }



    public void chooseWord(){
        int startRow = chosenSpots.get(0);
        int startCol = chosenSpots.get(1);
        int endRow = chosenSpots.get(2);
        int endCol = chosenSpots.get(3);
        String chosenWord = "";
        boolean diagonals = Math.abs(startRow - endRow) == Math.abs(startCol - endCol);
        // UP
        if(startRow - endRow > 0 && startCol == endCol){
            for(int i=0; i<Math.abs(startRow - endRow) + 1; i++){
                chosenWord += board[startRow - i][startCol].getBtnText();
            }
            if(checkWord(chosenWord)){
                for(int i=0; i<chosenWord.length(); i++){
                    boardSpotsBTN[startRow - i][startCol].setStyle("-fx-background-color: #f0f");
                }
            }
        }
        // DOWN
        if(startRow - endRow < 0 && startCol == endCol){
            for(int i=0; i<Math.abs(startRow - endRow) + 1; i++){
                chosenWord += board[startRow + i][startCol].getBtnText();
            }
            if(checkWord(chosenWord)){
                for(int i=0; i<chosenWord.length(); i++){
                    boardSpotsBTN[startRow + i][startCol].setStyle("-fx-background-color: #f0f");
                }
            }
        }
        //RIGHT
        if(startCol - endCol < 0 && startRow == endRow){
            for(int i=0; i<Math.abs(startCol - endCol) + 1; i++){
                chosenWord += board[startRow][startCol + i].getBtnText();
            }
            if(checkWord(chosenWord)){
                for(int i=0; i<chosenWord.length(); i++){
                    boardSpotsBTN[startRow][startCol + i].setStyle("-fx-background-color: #f0f");
                }
            }
        }
        //LEFT
        if(startCol - endCol > 0 && startRow == endRow){
            for(int i=0; i<Math.abs(startCol - endCol) + 1; i++){
                chosenWord += board[startRow][startCol - i].getBtnText();
            }
            if(checkWord(chosenWord)){
                for(int i=0; i<chosenWord.length(); i++){
                    boardSpotsBTN[startRow][startCol - i].setStyle("-fx-background-color: #f0f");
                }
            }
        }
        //UP RIGHT
        if(startRow - endRow > 0 && startCol - endCol < 0 && diagonals){
            for(int i=0; i<Math.abs(startCol - endCol) + 1; i++){
                chosenWord += board[startRow - i][startCol + i].getBtnText();
            }
            if(checkWord(chosenWord)){
                for(int i=0; i<chosenWord.length(); i++){
                    boardSpotsBTN[startRow - i][startCol + i].setStyle("-fx-background-color: #f0f");
                }
            }
        }
        //DOWN RIGHT
        if(startRow - endRow < 0 && startCol - endCol < 0 && diagonals){
            for(int i=0; i<Math.abs(startCol - endCol) + 1; i++){
                chosenWord += board[startRow + i][startCol + i].getBtnText();
            }
            if(checkWord(chosenWord)){
                for(int i=0; i<chosenWord.length(); i++){
                    boardSpotsBTN[startRow + i][startCol + i].setStyle("-fx-background-color: #f0f");
                }
            }
        }
        //DOWN LEFT
        if(startRow - endRow < 0 && startCol - endCol > 0 && diagonals){
            for(int i=0; i<Math.abs(startCol - endCol) + 1; i++){
                chosenWord += board[startRow + i][startCol - i].getBtnText();
            }
            if(checkWord(chosenWord)){
                for(int i=0; i<chosenWord.length(); i++){
                    boardSpotsBTN[startRow + i][startCol - i].setStyle("-fx-background-color: #f0f");
                }
            }
        }
        //UP LEFT
        if(startRow - endRow > 0 && startCol - endCol > 0 && diagonals){
            for(int i=0; i<Math.abs(startCol - endCol) + 1; i++){
                chosenWord += board[startRow - i][startCol - i].getBtnText();
            }
            if(checkWord(chosenWord)){
                for(int i=0; i<chosenWord.length(); i++){
                    boardSpotsBTN[startRow - i][startCol - i].setStyle("-fx-background-color: #f0f");
                }
            }
        }
        labelWords.setText(chosenWord);
        //game end
        if(words.size() == 0){
            gameEnd();
        }
    }

    public void gameEnd(){
        lstDifficulty.setDisable(false);
        endTime = System.nanoTime();
        double clearTime = roundNum(2, ((endTime - startTime) / 1000000000.0));
        labelWords.setText("");
        labelCheck.setText("");
        label1.setText("game cleared");
        labelTime.setText("Time : " + clearTime);
        labelWordNum.setText("Words : " + selectedStage().getMaxWord());
        labelScore.setText("Score : " + calculateScore(clearTime, multiplier));

        gdpPlayGrid.setDisable(true);
    }

    public void createBoard(){
        String word = words.get(countNum);
        int row = (int)(Math.random()*board.length);
        int col = (int)(Math.random()*board[0].length);
        int num = (int)(Math.random() * 8);
        putWord(row, col, word, num);
    }


    public void putWord(int row, int col, String word, int direction){
        limitNum++;
        if(limitNum > 1000 || allChecked()){
            label1.setText("words could not fit in the grid");
            noFit = true;
            lstDifficulty.setDisable(false);
            return;
        }
        if(!(top && right && bottom && left && topRight && bottomRight && bottomLeft && topLeft) && !board[row][col].isChecked()){
            // UP
            if(direction == 0){
                if(checkTop(word, row) && canPut(word, row, col, direction)){
                    for(int i=0; i<word.length(); i++){
                        board[row - i][col].clickedSpot(word.substring(i, i+1));
                        board[row - i][col].setChecked();
                    }
                    resetDirections();
                    uncheckAll();
                    if(countNum<words.size() - 1){
                        countNum++;
                        createBoard();
                    }
                } else {
                    top = true;
                    putWord(row, col, word, (int)(Math.random() * 8));
                }
            }
            // RIGHT
            if(direction == 1){
                if(checkRight(word, col) && canPut(word, row, col, direction)){
                    for(int i=0; i<word.length(); i++){
                        board[row][col + i].clickedSpot(word.substring(i, i+1));
                    }
                    resetDirections();
                    uncheckAll();
                    if(countNum<words.size() - 1){
                        countNum++;
                        createBoard();
                    }
                } else {
                    right = true;
                    putWord(row, col, word, (int)(Math.random() * 8));
                }
            }
            // DOWN
            if(direction == 2){
                if(checkBottom(word, row) && canPut(word, row, col, direction)){
                    for(int i=0; i<word.length(); i++){
                        board[row + i][col].clickedSpot(word.substring(i, i+1));
                    }
                    resetDirections();
                    uncheckAll();
                    if(countNum<words.size() - 1){
                        countNum++;
                        createBoard();
                    }
                } else {
                    bottom = true;
                    putWord(row, col, word, (int)(Math.random() * 8));
                }
            }
            // LEFT
            if(direction == 3){
                if(checkLeft(word, col) && canPut(word, row, col, direction)){
                    for(int i=0; i<word.length(); i++){
                        board[row][col - i].clickedSpot(word.substring(i, i+1));
                    }
                    resetDirections();
                    uncheckAll();
                    if(countNum<words.size() - 1){
                        countNum++;
                        createBoard();
                    }
                } else {
                    left = true;
                    putWord(row, col, word, (int)(Math.random() * 8));
                }
            }
            // UP RIGHT
            if(direction == 4){
              if(checkTop(word, row) && checkRight(word, col) && canPut(word, row, col, direction)){
                  for(int i=0; i<word.length(); i++){
                      board[row - i][col + i].clickedSpot(word.substring(i, i+1));
                  }
                  resetDirections();
                  uncheckAll();
                  if(countNum<words.size() - 1){
                      countNum++;
                      createBoard();
                  }
              } else {
                  topRight = true;
                  putWord(row, col, word, (int)(Math.random() * 8));
              }
            }
            // DOWN RIGHT
            if(direction == 5){
                if(checkBottom(word, row) && checkRight(word, col) && canPut(word, row, col, direction)){
                    for(int i=0; i<word.length(); i++){
                        board[row + i][col + i].clickedSpot(word.substring(i, i+1));
                    }
                    resetDirections();
                    uncheckAll();
                    if(countNum<words.size() - 1){
                        countNum++;
                        createBoard();
                    }
                } else {
                    bottomRight = true;
                    putWord(row, col, word, (int)(Math.random() * 8));
                }
            }
            // DOWN LEFT
            if(direction == 6){
                if(checkBottom(word, row) && checkLeft(word, col) && canPut(word, row, col, direction)){
                    for(int i=0; i<word.length(); i++){
                        board[row + i][col - i].clickedSpot(word.substring(i, i+1));
                    }
                    resetDirections();
                    uncheckAll();
                    if(countNum<words.size() - 1){
                        countNum++;
                        createBoard();
                    }
                } else {
                    bottomLeft = true;
                    putWord(row, col, word, (int)(Math.random() * 8));
                }
            }
            // UP RIGHT
            if(direction == 7){
                if(checkTop(word, row) && checkLeft(word, col) && canPut(word, row, col, direction)){
                    for(int i=0; i<word.length(); i++){
                        board[row - i][col - i].clickedSpot(word.substring(i, i+1));
                    }
                    resetDirections();
                    uncheckAll();
                    if(countNum<words.size() - 1){
                        countNum++;
                        createBoard();
                    }
                } else {
                    topLeft = true;
                    putWord(row, col, word, (int)(Math.random() * 8));
                }
            }
        } else {
            board[row][col].setChecked();
            resetDirections();
            createBoard();
        }
    }
    public boolean allChecked(){
        for(int i=0; i<board.length; i++){
            for(int j=0; j<board[0].length; j++){
                if(!board[i][j].isChecked()){
                    return false;
                }
            }
        }
        return true;
    }

    public void fillGrid(){
        for(int i=0; i<board.length; i++){
            for(int j=0; j<board[0].length; j++){
                if(board[i][j].getBtnText().equals("-")){
                    String randomLetter = getRandomLetter();
                    boardSpotsBTN[i][j].setText(randomLetter);
                    board[i][j].clickedSpot(randomLetter);
                }
            }
        }
    }

    public double findMultiplier(int rowNum, int colNum){
        double baseNum = rowNum + colNum;
        baseNum *= 0.1;
        roundNum(2, baseNum);
        return baseNum;
    }

    public boolean checkWord(String chosenWord){
        for(String word : words){
            if(word.equals(chosenWord)){
                chosenText = word;
                words.remove(word);
                labelWords.setText(word);
                lstWords.getItems().remove(word);
                labelCheck.setText("GOOD JOB!");
                return true;
            }
        }
        labelCheck.setText("Not in the Word List!");
        return false;
    }

    public String getRandomLetter(){
        String selectedLetter;
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        int randomNum = (int)(Math.random()*26);
        selectedLetter = alphabet.substring(randomNum, randomNum+1);
        return selectedLetter;
    }
    public void uncheckAll(){
        for(int i=0; i<board.length; i++){
            for(int j=0; j<board[0].length; j++){
                if(board[i][j] != null)
                board[i][j].unCheck();
            }
        }
    }
    public String calculateScore(double clearTime, double multiplier){
        double score;
        double baseNum = roundNum(2, 100 / clearTime) * multiplier;
        System.out.println(baseNum);
        int wordSize = selectedStage().getMaxWord();
        System.out.println(wordSize);
        score = roundNum(2, baseNum * wordSize);
        System.out.println(score);
        System.out.println(score);
        if(score > 20){
            return "A";
        }
        if(score > 15){
            return "B";
        }
        if(score > 10){
            return "C";
        }
        if(score > 5){
            return "D";
        }
        return "F";
    }
    public void resetEverything(){
        gdpPlayGrid.getChildren().clear();
        gdpPlayGrid.setDisable(false);
        label1.setText("");
        limitNum = 0;
        words.clear();
        lstWords.getItems().clear();
        noFit = false;
        uncheckAll();
        resetDirections();
    }
    public void resetDirections(){
        top = false;
        right = false;
        bottom = false;
        left = false;
        topRight = false;
        bottomRight = false;
        bottomLeft = false;
        topLeft = false;
    }

    public boolean canPut(String word, int rowNum, int colNum, int direction){
        int canNum = 0;
        if(direction == 0){
            for(int i=0; i<word.length(); i++){
                if(board[rowNum - i][colNum].getBtnText().equals("-") || board[rowNum - i][colNum].getBtnText().equals(word.substring(i, i+1))){
                    canNum++;
                }
            }
        }
        if(direction == 1){
            for(int i=0; i<word.length(); i++){
                if(board[rowNum][colNum + i].getBtnText().equals("-") || board[rowNum][colNum + i].getBtnText().equals(word.substring(i, i+1))){
                    canNum++;
                }
            }
        }
        if(direction == 2){
            for(int i=0; i<word.length(); i++){
                if(board[rowNum + i][colNum].getBtnText().equals("-") || board[rowNum +  i][colNum].getBtnText().equals(word.substring(i, i+1))){
                    canNum++;
                }
            }
        }
        if(direction == 3){
            for(int i=0; i<word.length(); i++){
                if(board[rowNum][colNum - i].getBtnText().equals("-") || board[rowNum][colNum - i].getBtnText().equals(word.substring(i, i+1))){
                    canNum++;
                }
            }
        }
        if(direction == 4){
            for(int i=0; i<word.length(); i++){
                if(board[rowNum - i][colNum + i].getBtnText().equals("-") || board[rowNum - i][colNum+ i].getBtnText().equals(word.substring(i, i+1))){
                    canNum++;
                }
            }
        }
        if(direction == 5){
            for(int i=0; i<word.length(); i++){
                if(board[rowNum + i][colNum + i].getBtnText().equals("-") || board[rowNum + i][colNum + i].getBtnText().equals(word.substring(i, i+1))){
                    canNum++;
                }
            }
        }
        if(direction == 6){
            for(int i=0; i<word.length(); i++){
                if(board[rowNum + i][colNum - i].getBtnText().equals("-") || board[rowNum + i][colNum -  i].getBtnText().equals(word.substring(i, i+1))){
                    canNum++;
                }
            }
        }
        if(direction == 7){
            for(int i=0; i<word.length(); i++){
                if(board[rowNum - i][colNum - i].getBtnText().equals("-") || board[rowNum - i][colNum - i].getBtnText().equals(word.substring(i, i+1))){
                    canNum++;
                }
            }
        }

        if(canNum == word.length()){
            return true;
        } else {
            return false;
        }
    }


    public boolean checkTop(String word, int rowNum){
        if(rowNum < word.length() - 1){
            return false;
        } else {
            return true;
        }
    }
    public boolean checkBottom(String word, int rowNum){
        if(board.length - rowNum < word.length()){
            return false;
        } else {
            return true;
        }
    }
    public boolean checkRight(String word, int colNum){
        if(board[0].length - colNum < word.length()){
            return false;
        } else {
            return true;
        }
    }
    public boolean checkLeft(String word, int colNum){
        if(colNum < word.length() - 1){
            return false;
        } else {
            return true;
        }
    }
    public double roundNum(int place, double num){
        double decPlace = Math.pow(10, place);
        num *= decPlace;
        num = Math.round(num);
        num /= decPlace;
        return num;
    }

    public Stage selectedStage(){
        for(Stage chosenStage : stage){
            if(chosenStage.getStageName().equals(lstDifficulty.getSelectionModel().getSelectedItem().toString())){
                return chosenStage;
            }
        }
        return null;
    }

    public void handleLoad(){
        try{
            FileReader reader = new FileReader("src/main/resources/foodText.txt");
            Scanner in = new Scanner(reader);
            while(in.hasNextLine()) {
                String temp = in.nextLine();
                stage.get(0).getGridWords().add(temp);
            }
        } catch (FileNotFoundException var){
            System.out.println("Something went wrong");
        }
        try{
            FileReader reader = new FileReader("src/main/resources/sportText.txt");
            Scanner in = new Scanner(reader);
            while(in.hasNextLine()) {
                String temp = in.nextLine();
                stage.get(1).getGridWords().add(temp);
            }
        } catch (FileNotFoundException var){
            System.out.println("Something went wrong");
        }        try{
            FileReader reader = new FileReader("src/main/resources/countryText.txt");
            Scanner in = new Scanner(reader);
            while(in.hasNextLine()) {
                String temp = in.nextLine();
                stage.get(2).getGridWords().add(temp);
            }
        } catch (FileNotFoundException var){
            System.out.println("Something went wrong");
        }

    }
}