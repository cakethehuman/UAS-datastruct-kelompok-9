import java.util.*;
class Game{
    char[][] board = new char[3][3];
    
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

    public void insertItem(int i, int j, char input){
        if(board[i][j] != 'X' && board[i][j] != 'O'){
            board[i][j] = input;
        } else{
            System.out.println("Full");
        }
    }

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

    public void insertPosition(int position, char turn){
        try{
            switch (position) {
            case 1:
                insertItem(0, 0, turn);
                break;
            case 2:
                insertItem(0, 1, turn);
                break;
            case 3:
                insertItem(0, 2, turn);
                break;
            case 4:
                insertItem(1, 0, turn);
                break;
            case 5:
                insertItem(1, 1, turn);
                break;
            case 6:
                insertItem(1, 2, turn);
                break;
            case 7:
                insertItem(2, 0, turn);
                break;
            case 8:
                insertItem(2, 1, turn);
                break;
            case 9:
                insertItem(2, 2, turn);
                break;
            default:
                throw new AssertionError();
            }
        } catch(Exception e){
            System.out.println("Error lah");
        }
    }

    public void startGame(){
        // need variable to play the game
        int counter = 0;  

        // input starting values
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

            // change turn
            if(turn == 'X'){
                turn = 'O';
            }else{
                turn = 'X';
            }

            System.out.println("Lagi turn : " + turn);
            System.out.print("Mau masukin di mana? : ");
            int position = inputer.nextInt();
            
            insertPosition(position, turn);   

            if(checkWin('X')){
                System.out.println("X win");
                break;
            }

            if(checkWin('O')){
                System.out.println("O win");
                break;
            }
        }
        inputer.close();
    }
}
public class TTC {
    public static void main(String[] args) {
        Game ttc = new Game();
        ttc.startGame();
        // ttc.InsertItem(1, 0, 'x');
        ttc.showBoard();
    }
}
