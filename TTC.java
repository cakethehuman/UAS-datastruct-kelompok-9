// Kelompok 9 Kelas A
// 
// 
//

import java.util.*;

class Game{
    char[][] board = new char[3][3];
    
    // Helper functions
    
    // Show baord to start the game ig
    public void showBoard(){
        System.err.println("Board : ");
        for (int i = 0; i < 3; i++) {
            System.out.print('|');
            for (int j = 0; j < 3; j++) {
                if(j == 2){
                   System.out.print(board[i][j]); 
                   System.out.println('|');
                } else{
                   System.out.print(board[i][j]); 
                   System.out.print(' '); 
                }
            }
        }
    }

    // Check if board is full
    public boolean checkWin(char player){
        for(int i = 0; i < 3; i++){
            if(board[i][0] == player &&
            board[i][1] == player &&
            board[i][2] == player){
                return true;
            }
        }
        for(int j = 0; j < 3; j++){
            if(board[0][j] == player &&
            board[1][j] == player &&
            board[2][j] == player){
                return true;
            }
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
            return 10;
        }

        if(checkWin('X')){
            return -10;
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

    // Start the game
    public void startGame(){
        // need variable to play the game
        int counter = 0;  

        // input starting values from 1-9 to the board
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                counter += 1;
                board[i][j] = (char) (counter + '0');
            }
        }
        // show the board and inputs using a method so everything is seperated
        Scanner inputer = new Scanner(System.in);
        char turn = 'X';
        // if win lose logic ig
        while (true) {
            showBoard();

            System.out.println("Lagi turn : " + turn);
            System.out.print("Mau masukin di mana? : ");
            int position = inputer.nextInt();
            
            boolean valid = insertPosition(position, turn);   

            if(!valid){
                continue;
            }
            
            // Check winner
            if(checkWin(turn)){
                System.out.println(turn + " win");
                break;
            }

            // change turn
            if(turn == 'X'){
                turn = 'O';
            }else{
                turn = 'X';
            }

   
        }
        inputer.close();
    }
}
public class TTC {
    public static void main(String[] args) {
        Game ttc = new Game();
        ttc.startGame();

    }
}


// Expected to have a game tree + AI + result playing the game