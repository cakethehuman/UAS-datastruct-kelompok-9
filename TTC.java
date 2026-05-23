    // Kelompok 9 Kelas A
// 
// 
//

import java.util.*;

class Game{
    char[][] board = new char[3][3];
    
    // Helper functions
    
    public void clearScreen() {
        System.out.println("\033[H\033[2J");
        System.out.flush();
    }

    // Show board to start the game ig
    public void showBoard(){
        System.err.println("Board : ");
        int position = 1;
        for (int i = 0; i < 3; i++) {
            System.out.print("| ");
            for (int j = 0; j < 3; j++) {
                if(board[i][j] == ' '){
                    System.out.printf("%s ", position);
                } else{
                    System.out.printf("%s ",board[i][j]); 
                } 
                position++;
            }
            System.out.println("|");
        }
    }

    // Check if board is full
    public boolean checkWin(char player){
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                if(board[i][0] == player &&
                board[i][1] == player &&
                board[i][2] == player){
                    return true;
                }
                if(board[0][j] == player &&
                board[1][j] == player &&
                board[2][j] == player){
                    return true;
                }
                if(board[0][0] == player &&
                board[1][1] == player &&
                board[2][2] == player){
                    return true;
                }
                if(board[0][2] == player &&
                board[1][1] == player &&
                board[2][0] == player){
                    return true;
                }
            }
        }
        return false;
    }

    // winning logic
    public boolean isBoardFull() {

        for (int i = 0; i < 3; i++) {

            for (int j = 0; j < 3; j++) {

                if (board[i][j] != 'X' &&
                    board[i][j] != 'O') {

                    return false;
                }
            }
        }

        return true;
    }

    public int evaluate(){
        if(checkWin('O')){
            return 1;
        }

        if(checkWin('X')){
            return -1;
        }

        return 0;
    }

    // Inputing logic
    public boolean insertItem(int i, int j, char input){
        if(board[i][j] != 'X' && board[i][j] != 'O'){
            board[i][j] = input;
            return true;
        } else{
            System.out.printf("TIdak bisa input di %d %d karena sudah ada simbol di posisi tersebut coba lagi \n", i,j);
            return false;
        }
    }

    public boolean insertPosition(int position, char turn){
        try{
            switch (position) {
                case 1:
                    return insertItem(0, 0, turn);
                case 2:
                    return insertItem(0, 1, turn);
                case 3:
                    return insertItem(0, 2, turn);
                case 4:
                    return insertItem(1, 0, turn);
                case 5:
                    return insertItem(1, 1, turn);
                case 6:
                    return insertItem(1, 2, turn);
                case 7:
                    return insertItem(2, 0, turn);
                case 8:
                    return insertItem(2, 1, turn);
                case 9:
                    return insertItem(2, 2, turn);
                default:
                    System.err.println("Index out of range");
                    return false;
            }

        } 
        
        catch(Exception e){

            System.err.println("Error posisi tidak valid");

            return false;
        }
    }

    public int minmax(boolean isMax){
        int score = evaluate();

        if(score == 1){
            return score;
        } 
        
        if (score == -1){
            return score;
        }

        if(isBoardFull()){
            return 0;
        }

        if(isMax){
            int best = -1000;
            
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if(board[i][j] == ' '){
                        board[i][j] = 'O';
                        int value = minmax(false);
                        board[i][j] = ' ';
                        best = Math.max(best, value);
                    }
                }
            }
            return best;
        } else {
            int best = 1000;

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if(board[i][j] == ' '){
                        board[i][j] = 'X';
                        int value = minmax(true);
                        board[i][j] = ' ';
                        best = Math.min(best, value);
                    }
                }
            }
            return best;
        }
    }

    public int[] bestMove(){
        int bestScore = -100;

        int[] move = new int[2];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                
                if (board[i][j] == ' ') {
                    board[i][j] = 'O';
                    int score = minmax(false);
                    board[i][j] = ' ';
                    // if(score == 0){
                    //     System.out.println("Draw");
                    //     break;
                    // }
                    if(score > bestScore){
                        bestScore = score;

                        move[0] = i;
                        move[1] = j;
                    }
                }
            }
        }

        return move;
    }

    public void startGame(Scanner inputer){

        // input starting values from 1-9 to the board
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
        // show the board and inputs using a method so everything is seperated
        char turn = 'X';
        // if win lose logic ig
        while (true) {
            showBoard();

            boolean valid;

            if(turn == 'X'){

                System.out.println("Lagi turn : " + turn);

                System.out.print("Mau masukin di mana? : ");

                int position = inputer.nextInt();

                valid = insertPosition(position, turn);
            }
            else{

                System.out.println("AI TURN");

                int[] aiMove = bestMove();

                board[aiMove[0]][aiMove[1]] = 'O';

                valid = true;
            }
            
            // Check winner
            if(checkWin(turn)){
                System.out.println(turn + " win");
                showBoard();
                break;
            }

            if(isBoardFull()){
                System.out.println("Draw!!");
                showBoard();
                break;
            }

            // change turn
            if(turn == 'X'){
                turn = 'O';
            }else{
                turn = 'X';
            }
        }
    }

    // Start the game from AI turn
    public void startGameAI(Scanner inputer){

        // input starting values from 1-9 to the board
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
        // show the board and inputs using a method so everything is seperated
        char turn = 'O';
        // if win lose logic ig
        while (true) {
            showBoard();

            boolean valid;

            if(turn == 'O'){
                System.out.println("AI TURN");

                int[] aiMove = bestMove();

                board[aiMove[0]][aiMove[1]] = 'O';

                valid = true;
                
            }
            else{
                System.out.println("Lagi turn : " + turn);

                System.out.print("Mau masukin di mana? : ");

                int position = inputer.nextInt();

                valid = insertPosition(position, turn);
            }
            
            // Check winner
            if(checkWin(turn)){
                System.out.println(turn + " win");
                showBoard();
                break;
            }

            if(isBoardFull()){
                System.out.println("Draw!!");
                showBoard();
                break;
            }

            // change turn
            if(turn == 'X'){
                turn = 'O';
            }else{
                turn = 'X';
            }

   
        }
    }
}

public class TTC {
    public static void main(String[] args) {
        Scanner globalScanner = new Scanner(System.in);
        while(true){
            Game ttc = new Game();
            System.out.println("Mau mulai duluan atau tidak? (Y/N)");
            String Choice = globalScanner.next().toUpperCase();
            while(!Choice.equals("Y") && !Choice.equals("N")){
                System.out.println("Input tidak valid, silahkan input Y atau N");
                Choice = globalScanner.next().toUpperCase();
            }
            if(Choice.equals("Y")){
                ttc.startGame(globalScanner);
            } else if(Choice.equals("N")){
                ttc.startGameAI(globalScanner);
            }
            System.out.print("Main lagi? (Y/N): ");
            String playAgain = globalScanner.next();

            if (playAgain.equalsIgnoreCase("N")) {
                System.out.println("Terima kasih sudah bermain!");
                break; // This breaks out of the outer loop and stops the program
            }
            System.out.println("\nYatta, Let's Play Again");
            try{
                Thread.sleep(2000);
            } catch(InterruptedException e){
                System.err.println("Error: " + e.getMessage());
            }
            ttc.clearScreen();
        }
        
    }
}


// Expected to have a game tree + AI + result playing the game