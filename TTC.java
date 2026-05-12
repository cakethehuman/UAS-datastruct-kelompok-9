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
        board[i][j] = input;
    }

    public void checkWin(){

    }

    public void insertPosition(int position){
        try{
            switch (position) {
            case 1:
                insertItem(0, 0, 'X');
                break;
            case 2:
                insertItem(0, 1, 'X');
                break;
            case 3:
                insertItem(0, 2, 'X');
                break;
            case 4:
                insertItem(1, 0, 'X');
                break;
            case 5:
                insertItem(1, 1, 'x');
                break;
            case 6:
                insertItem(1, 2, 'X');
                break;
            case 7:
                insertItem(2, 0, 'X');
                break;
            case 8:
                insertItem(2, 1, 'X');
                break;
            case 9:
                insertItem(2, 2, 'X');
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

        // if win lose logic ig
  

        // input starting values
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                counter += 1;
                board[i][j] = (char) (counter + '0');
            }
        }
        // show the board and inputs using a method so everything is seperated
        Scanner inputer = new Scanner(System.in);
        while (!board.equals("XXX")) {
            showBoard();
            System.out.print("Mau masukin di mana? : ");
            int position = inputer.nextInt();
            insertPosition(position);   
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
