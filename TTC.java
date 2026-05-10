class Game{
    char[][] board = new char[3][3];
    
    // Show baord to start the game ig
    public void ShowBoard(){
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(j == 2){
                   System.out.println(board[i][j]); 
                } else{
                   System.out.print(board[i][j]); 
                }
            }
        }
    }

    public void InsertItem(int i, int j, char input){
        board[i][j] = input;
    }

    public void StartGame(){
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                
                board[i][j] = '_';
            }
        }
    }
}
public class TTC {
    public static void main(String[] args) {
        Game ttc = new Game();
        ttc.StartGame();
        ttc.InsertItem(1, 0, 'x');
        ttc.ShowBoard();
    }
}
