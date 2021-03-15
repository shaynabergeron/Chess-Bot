package com.stephengware.java.games.chess.bot;
import java.util.Iterator;
import com.stephengware.java.games.chess.bot.Bot;
import com.stephengware.java.games.chess.state.*;
import com.stephengware.java.games.chess.Game;
import com.stephengware.java.games.chess.state.Player;
import com.stephengware.java.games.chess.state.State;

/**
 * A chess bot which doesn't select its next move at random.
 * Uses deep search methods and alpha beta pruning for min and max
 * 
 * Able to evaluate each State, return the current state of the moves 
 * that are the most desirable for the player.
 * 
 * 
 * 
 * @author Stephen G. Ware
 * @author Shayna Bergeron
 **/

public class Scberger extends Bot {
	/**
	 * Constructs a new chess bot named "Scberger"
	 *
	 * Note to self: don't forget to change class name lol
	 * */

	public Scberger() {
		super("Scberger");
	}

	/** chooseMove () */
	@Override
	protected State chooseMove(State root) {
		Result returnValue = new Result(root, 0.0);
        Result daValue = returnValue;
		boolean isMax = false;
		int depth = 4;
		/** this will create a deep search to explore nodes that will not exceed the limit allowed */
		/** max player */
        if(root.player.equals(Player.WHITE)) {
            isMax = true;           
        }
        returnValue = alphaBetaP(root, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, depth, isMax);
        if(returnValue.myState.previous.equals(root)) {
            return returnValue.myState;
        }
        while(!returnValue.myState.previous.equals(root)){
            returnValue.myState = returnValue.myState.previous;
        }
        return returnValue.myState;

        
	}
	/** This method will implement Alpha Beta pruning as learned in class and via Google
	 * 
	 * @param myState
	 * @param alpha
	 * @param beta
	 * @param depth
	 * @param isMax
	 * @return Result(State, Double)
	 */
	
	private Result alphaBetaP(State myState, double alpha, double beta, int depth, boolean isMax ){
		/** returns Result*/
        if(myState.over || depth == 0){
	     	return new Result(myState, scoreCounter(myState));      
        }
        /** maximum aka findMax" 
         * returns the node with the highest minimum result value for the player
         *  */
        if(isMax){
             Double scoreValue = Double.NEGATIVE_INFINITY;
             Result returnValue = new Result();
             Result daValue = returnValue;
             Iterator<State> iterator = myState.next().iterator();
             while(!myState.searchLimitReached() && iterator.hasNext()) {

                 Result child = new Result(iterator.next(), 0.0);
                 scoreValue = Math.max(scoreValue, alphaBetaP(child.myState, alpha, beta, depth - 1, false).score);
                 if(!myState.searchLimitReached() && Double.compare(scoreValue, alpha) > 0){
                	 daValue = child;
                 }
                 alpha = Math.max(alpha, scoreValue);
                 if(beta <= alpha)
                     break;
             }

             returnValue = new Result(daValue.myState, scoreValue);
             return returnValue ;
        }

 
        /** minimum aka "findMin" 
         * returns the lowest maximum result value for the player
         * */
        else{
		     Double scoreValue = Double.POSITIVE_INFINITY;
             Result returnValue = new Result();
             Result daValue = returnValue;
             Iterator<State> iterator = myState.next().iterator();
		     while(!myState.searchLimitReached() && iterator.hasNext() ){
		         Result child = new Result(iterator.next(), 0.0);
		         scoreValue = Math.min(scoreValue, alphaBetaP(child.myState, alpha, beta, depth -1, true).score);
		         if(!myState.searchLimitReached() && Double.compare(scoreValue, beta)< 0){
		        	 daValue = child;
                 }
                 beta = Math.min(beta, scoreValue);
		         if(beta <= alpha)
		             break;
             }
		     returnValue =  new Result(daValue.myState, scoreValue);
		     return returnValue;
        }

    }//end of Alpha Beta Pruning funtion

	/** scoreCounter calculates the score for each given State
	 *  Note to self: com.stephengware.java.games.chess.state.Board for edits
	 *  countPieces: Returns the number of pieces on the board for both players
	 *  file: The piece's file (column) on the board (A to H), as an integer (0 to 7, where 0=A, 1=B, etc.)
	 *  rank: The piece's rank (row) on the board (1 to 8), as an integer (0 to 7, where 0=1, 1=2, etc.)
	 *  
	 *  returns the calculation of the score after each given State by file and rank
     */
	private double scoreCounter(State myState){
		     double maxScore = 0;
		     double minScore = 0;
		     double score = 0.0;
		     maxScore += myState.board.countPieces(Player.WHITE) - myState.board.countPieces(Player.BLACK);
             if(myState.over){

                 if(myState.check) {

                     if (myState.player.equals(Player.WHITE)) {
                         return -1000;
                     }
                     return 1000;
                 }
                 return 0;// stale mate aka a draw 
             }
            if(myState.check) {
                if (myState.player.equals(Player.WHITE)) {
                    return maxScore -= 2;
                }
                    return minScore -= 2;
            }


             for(Piece piece : myState.board){

                 if(piece.player.equals(Player.WHITE)){

                     if(piece instanceof Queen){
                         if(myState.board.countPieces() > 20 ) {
                             maxScore += 9 + piece.rank*0.08;
                         }
                         maxScore += 10 + piece.rank*0.01;
                     }
                     else if(piece instanceof Rook){
                         if(myState.board.countPieces() > 20 ) {
                             maxScore += 5 + piece.rank * 0.03;
                         }
                         maxScore += 5.5 + piece.rank * 0.05;
                     }
                     else if(piece instanceof Bishop){
                         if(myState.board.countPieces() > 18 ) {
                             maxScore += 4;
                         }
                         maxScore += 4 + piece.rank* 0.04;

                     }
                     else if (piece instanceof Knight){
                         if(myState.board.countPieces() <= 12){
                             maxScore += 3.5;
                         }
                         maxScore += 4 + piece.rank * 0.2;
                     }
                     else if(piece instanceof Pawn){
                         if(myState.turn < 3){
                             if(piece.file == 2 || piece.file == 4 || piece.file ==5){
                                 maxScore += 4 + (piece.rank)* 0.05;
                             }
                         }
                         maxScore += 1 + ((piece.rank)*0.05);
                     }

                 }//max player 
                 else{

                     if(piece instanceof Queen){
                         if(myState.board.countPieces() > 20 ) {
                             minScore += 9 + ( 7 - piece.rank)*0.24;
                         }
                         minScore += 10 + (7 - piece.rank)*0.1;
                     }
                     else if(piece instanceof Rook){
                         if(myState.board.countPieces() > 20 ) {
                             minScore += 5 + (7 - piece.rank) * 0.03;
                         }
                         minScore += 5.5 + piece.rank * 0.05;
                     }
                     else if(piece instanceof Bishop){
                         if(myState.board.countPieces() > 18 ) {
                             minScore += 4;
                         }
                         minScore += 4 + (7 - piece.rank)* 0.04;

                     }
                     else if (piece instanceof Knight){

                         if(myState.board.countPieces() <= 18){
                             minScore += 3.5;
                         }
                         minScore += 4 + (7 - piece.rank )* 0.2;
                     }
                     else if(piece instanceof Pawn){

                         if(myState.turn < 3){
                             if(piece.file == 2 || piece.file ==4 || piece.file == 5){
                                 minScore += 4 + (7 - piece.rank) * 0.05;
                             }
                         }
                         minScore += 1 + ((7 - piece.rank)*0.05);
                     }

                 }
             }//end of for loop
             score = maxScore - minScore;
             return score;
	}//end of scoreCounter()
	
}//MyBot class ending
/** class to give result given State*/
class Result {
	public State myState;
    public double score;


    public Result(State givenState, double givenValue){
        this.myState = givenState;
        this.score = givenValue;

    }

    public Result() {
        this.myState = null;
        this.score = 0.0;

    }
}//Result class ending