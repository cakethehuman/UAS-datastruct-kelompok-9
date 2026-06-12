public class GameState {
    private String title;
    private String[] boardState;
    private String scoreInfo;
    private boolean isOptimal;
    private int rawScore; 
   
    private boolean isActualGamePath = false;

    public boolean isActualGamePath() { return isActualGamePath; }
    public void setActualGamePath(boolean actualGamePath) { isActualGamePath = actualGamePath; }

    public GameState(String title, String[] boardState, String scoreInfo, boolean isOptimal) {
        this.title = title;
        this.boardState = boardState;
        this.scoreInfo = scoreInfo;
        this.isOptimal = isOptimal;
        this.rawScore = 0; 
    }

    public String getTitle() { 
        return title; 
    }
    
    public void setTitle(String title) { 
        this.title = title; 
    } 

    public String[] getBoardState() { 
        return boardState; 
    }
    
    public String getScoreInfo() { 
        return scoreInfo; 
    }
    
    public void setScoreInfo(String scoreInfo) { 
        this.scoreInfo = scoreInfo; 
    }
    
    public boolean isOptimal() {
         return isOptimal; 
    }

    public void setOptimal(boolean optimal) {
         this.isOptimal = optimal; 
    }

    public int getRawScore() { 
        return rawScore; 
    }

    public void setRawScore(int rawScore) { 
        this.rawScore = rawScore; 
    }
}