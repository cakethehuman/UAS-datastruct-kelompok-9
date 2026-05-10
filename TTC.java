import java.util.Scanner;
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

    public boolean startGame(){
        // need variable to play the game
        int counter = 0;

        // if win lose logic ig
        if(board[1][1] == 'x'){
            return true;
        }
        // input starting values
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                counter += 1;
                board[i][j] = (char) (counter + '0');
            }
        }
        // show the board and inputs
        showBoard();
        Scanner inputer = new Scanner(System.in);
        System.out.print("Mau masukin di mana? : ");
    
        int position = inputer.nextInt(); 
        inputer.close();
        switch (position) {
            case 1:
                insertItem(0, 0, 'x');
                break;
            case 2:
                insertItem(0, 1, 'x');
                break;
            case 3:
                insertItem(0, 2, 'x');
                break;
            case 4:
                insertItem(1, 0, 'x');
                break;
            case 5:
                insertItem(1, 1, 'x');
                break;
            case 6:
                insertItem(1, 2, 'x');
                break;
            case 7:
                insertItem(2, 0, 'x');
                break;
            case 8:
                insertItem(2, 1, 'x');
                break;
            case 9:
                insertItem(2, 2, 'x');
                break;
            default:
                throw new AssertionError();
        }
        return startGame();
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
