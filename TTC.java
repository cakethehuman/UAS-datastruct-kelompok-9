import java.util.Scanner;
class Game{
    char[][] board = new char[3][3];
    
    // Show baord to start the game ig
    public void ShowBoard(){
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

    public void InsertItem(int i, int j, char input){
        board[i][j] = input;
    }

    public void StartGame(){
        int counter = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                counter += 1;
                board[i][j] = (char) (counter + '0');
            }
        }
        Scanner inputer = new Scanner(System.in);
        System.out.print("Mau masukin di mana? : ");
    
        int position = inputer.nextInt(); 
        switch (position) {
            case 1:
                InsertItem(0, 0, 'x');
                break;
            case 2:
                InsertItem(0, 1, 'x');
                break;
            case 3:
                InsertItem(0, 2, 'x');
                break;
            case 4:
                InsertItem(1, 0, 'x');
                break;
            case 5:
                InsertItem(1, 1, 'x');
                break;
            case 6:
                InsertItem(1, 2, 'x');
                break;
            case 7:
                InsertItem(2, 0, 'x');
                break;
            case 8:
                InsertItem(2, 1, 'x');
                break;
            case 9:
                InsertItem(2, 2, 'x');
                break;
            default:
                throw new AssertionError();
        }
    }
}
public class TTC {
    public static void main(String[] args) {
        Game ttc = new Game();
        ttc.StartGame();
        // ttc.InsertItem(1, 0, 'x');
        ttc.ShowBoard();
    }
}
