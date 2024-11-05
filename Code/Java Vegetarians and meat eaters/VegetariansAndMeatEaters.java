import java.util.HashSet;

public class VegetariansAndMeatEaters {

	public static class BoardStateClass {
		public int MeatEatersL = 0;
		public int MeatEatersR = 3;
		public int VegetariansL = 0;
		public int VegetariansR = 3;
		String Boat = "R";

		public BoardStateClass() {

		}

		public BoardStateClass(BoardStateClass startValues, int changeMEL, int changeVEL, int changeMER, int changeVER, String Boat) {
			this.MeatEatersL = startValues.MeatEatersL + changeMEL;
			this.VegetariansL = startValues.VegetariansL + changeVEL;
			this.MeatEatersR = startValues.MeatEatersR + changeMER;
			this.VegetariansR = startValues.VegetariansR + changeVER;
			this.Boat = Boat;
		}

		public String toString() {
			String returnVal;
			switch (MeatEatersL) {
			case 0:
				returnVal="   ";
				break;
			case 1:
				returnVal = "M   ";
				break;
			case 2:
				returnVal = "MM ";
				break;
			case 3:
				returnVal="MMM";
				break;
			default:
				returnVal="ER"+MeatEatersL;
				break;
			}
			switch (VegetariansL) {
			case 0:
				returnVal+="   ";
				break;
			case 1:
				returnVal+= "V  ";
				break;
			case 2:
				returnVal+= "VV ";
				break;
			case 3:
				returnVal+="VVV";
				break;
			default:
				returnVal+="ER"+VegetariansL;
			}
			switch(Boat) {
			case "L":
				returnVal+="B___";
				break;
			case "R":
				returnVal+="___B";
				break;
			default:
				returnVal+="ERR" + Boat;
				break;
			}
			switch (MeatEatersR) {
			case 0:
				returnVal+="   ";
				break;
			case 1:
				returnVal+= "  M";
				break;
			case 2:
				returnVal += " MM";
				break;
			case 3:
				returnVal+="MMM";
				break;
			default:
				returnVal+="ER"+MeatEatersR;
				break;
			}
			switch (VegetariansR) {
			case 0:
				returnVal+="   ";
				break;
			case 1:
				returnVal+= "  V";
				break;
			case 2:
				returnVal+= " VV";
				break;
			case 3:
				returnVal+="VVV";
				break;
			default:
				returnVal+="ER"+VegetariansR;
			}				
			return returnVal;
		}

		// Useful to define, but not used by HashSet to determine equality.
		// See https://stackoverflow.com/questions/6187294/java-set-collection-override-equals-method
		public boolean equals(Object o) {
			if (o instanceof BoardStateClass) 
				return (this.MeatEatersL == ((BoardStateClass) o).MeatEatersL &&
				this.MeatEatersR == ((BoardStateClass) o).MeatEatersR &&
				this.VegetariansL== ((BoardStateClass) o).VegetariansL &&
				this.MeatEatersR == ((BoardStateClass) o).MeatEatersR &&
				this.Boat.equals(((BoardStateClass) o).Boat));
			else
				return false;
		}

		// Required to use HashSet
		// See https://stackoverflow.com/questions/6187294/java-set-collection-override-equals-method
		public int hashCode()
		{
			return MeatEatersL * 10000 + VegetariansL * 1000 + (Boat.equals("L")?100:0) + 
					MeatEatersR * 10 + VegetariansR;
		}
	} // End class BoardStateClass

	static HashSet<BoardStateClass> PastBoardStates = new HashSet<BoardStateClass>();
	static String solution = "";

	public static void main(String[] args) {
		// Set up initial problem	
		BoardStateClass BoardState = new BoardStateClass();		

		Solve(BoardState, solution);
		System.out.println("No solution found.");

	}

	public static Boolean Solve(BoardStateClass boardState, String moves)
	{				
		// Make a list of move options given the current board state
		String[] choices = listPossibleMoves(boardState);

		System.out.println("Solve called with board state: " + boardState);
		System.out.println("and move history" + moves);

		// Record where we are so we don't backtrack 
		PastBoardStates.add(boardState);

		// Stop at recursion level 20
		if (moves.length() < (20*20)) {

			// Loop through each of our choices
			for (String move: choices) {
				if (validMove(boardState, move)) {
					System.out.println("Trying move: " + boardState + " : " + move);

					String MovesArchive = moves;

					BoardStateClass newBoardState = DoMove(boardState, move);

					if (moves.equals(""))
						moves = move;
					else
						moves += ", " + move;

					// Check for win 
					if (isWin(newBoardState)) {
						System.out.println("************************************************");
						System.out.println("Game solved! A winning set of moves: " + moves);						
						System.exit(0);
						return true;
					}
					else if (isSomebodyEaten(newBoardState)) {
						System.out.println("Somebody got eaten!");
					} 

					// If we haven't won, and nobody has gotten eaten, and we haven't seen the proposed
					// board layout before, let's see if any child moves get us there.
					else if (keepGoing(newBoardState))				
						Solve(newBoardState, moves);
					else
						System.out.println("Out of options, undoing that move.");


					// Undo the last move	
					moves = MovesArchive;

					// Continue with the next move to try
				}											
			}
		} 
		else 
			System.out.println("Recursion too deep. Returning frolm Solve() without trying any child moves.");// if recursion level too deep

		// If we reach this point, we've tried all valid moves and shouldn't keep going
		return false;		
	}


	// listPossibleMoves

	public static String[] listPossibleMoves(BoardStateClass BoardState) {
		// Possible moves if the boat is on the left
		String[] leftMoves = {"MM ->", "MV ->", "VV ->", "M  ->", "V  ->" };
		String[] rightMoves =  {"<- MM", "<- MV", "<- VV", "<-  M", "<-  V" };

		// We could do a LOT more filtering here, such as checking for valid board states after the move.
		// But it makes more sense to _try_ the move, and then check the results

		if (BoardState.Boat.equals("R")) 
			return rightMoves;
		else
			return leftMoves;
	}

	// Returns True if a given move is valid
	// Returns False if not
	//
	// Things to check:
	// * Are there enough of each thing to actually make this move?
	// Does somebody get eaten? Gets checked in KeepGoing.

	public static Boolean validMove(BoardStateClass bs, String Move) {		
		switch(Move) {
		case "MM ->":
			if (bs.MeatEatersL < 2)
				return false;
			break;
		case "MV ->":
			if (bs.MeatEatersL < 1 || bs.VegetariansL < 1)
				return false;			
			break;
		case "VV ->":
			if (bs.VegetariansL < 2)
				return false;	
			break;
		case  "M  ->":
			if (bs.MeatEatersL < 1)
				return false;	
			break;
		case  "V  ->":
			if (bs.VegetariansL < 1)
				return false;		
			break;
		case "<- MM":
			if (bs.MeatEatersR < 2)
				return false;	
			break;
		case "<- MV":
			if (bs.MeatEatersR < 1 || bs.VegetariansR < 1)
				return false;	
			break;
		case "<- VV":
			if (bs.VegetariansR < 2)
				return false;	
			break;
		case "<-  M":
			if (bs.MeatEatersR < 1)
				return false;	
			break;
		case "<-  V" :
			if (bs.VegetariansR < 1)
				return false;
			break;
		}

		return true;

	}

	public static BoardStateClass DoMove(BoardStateClass BoardState, String Move) {

		switch(Move) {
		case "MM ->":
			return (new BoardStateClass(BoardState, -2, 0, +2, 0, "R"));

		case "MV ->":
			return (new BoardStateClass(BoardState, -1, -1, +1, +1, "R"));		

		case "VV ->":
			return (new BoardStateClass(BoardState, -0, -2, +0, +2, "R"));

		case  "M  ->":
			return (new BoardStateClass(BoardState, -1, -0, +1, +0, "R"));

		case  "V  ->":
			return (new BoardStateClass(BoardState, -0, -1, +0, +1, "R"));	

		case "<- MM":
			return (new BoardStateClass(BoardState, +2, +0, -2, -0, "L"));

		case "<- MV":
			return (new BoardStateClass(BoardState, +1, +1, -1, -1, "L"));

		case "<- VV":
			return (new BoardStateClass(BoardState, +0, +2, -0, -2, "L"));

		case "<-  M":
			return (new BoardStateClass(BoardState, +1, +0, -1, -0, "L"));

		case "<-  V" :
			return (new BoardStateClass(BoardState, +0, +1, -0, -1, "L"));

		default:
			System.out.println("Invalid move passed to DoMove():" + Move);
			System.exit(0);
			return (null);			
		}
	}

	public static Boolean isWin(BoardStateClass bs) {
		return (bs.MeatEatersL == 3 && bs.VegetariansL == 3);
	}

	public static Boolean keepGoing(BoardStateClass bs) {
		if (PastBoardStates.contains(bs))
			return false;
		return true;
	}

	public static Boolean isSomebodyEaten(BoardStateClass bs) {
		return ((bs.VegetariansL > 0 && bs.MeatEatersL > bs.VegetariansL) || 
				(bs.VegetariansR > 0 && bs.MeatEatersR > bs.VegetariansR) );
	}


	public static void testSets() {

		// Testing Set operation 
		BoardStateClass bs1 = new BoardStateClass();

		BoardStateClass bs2 = new BoardStateClass(bs1, 0,0,0,0,"R"); 
		BoardStateClass bs3 = new BoardStateClass(); 
		BoardStateClass bs4 = new BoardStateClass(bs1,1,1,-1,-1,"R");

		PastBoardStates.add(bs1); System.out.println("With bs1 in the set:");
		System.out.println((PastBoardStates.contains(bs1)?"does":"does not") + " contain bs1." );
		System.out.println((PastBoardStates.contains(bs2)?"does":"does not") + " contain bs2." );
		System.out.println((PastBoardStates.contains(bs3)?"does":"does not") + " contain bs3." );
		System.out.println((PastBoardStates.contains(bs4)?"does":"does not") + " contain bs4." );

		System.out.println("**********");

		PastBoardStates.add(bs2); System.out.println("With bs1 and bs2 in the set:");
		System.out.println((PastBoardStates.contains(bs1)?"does":"does not") + " contain bs1." );
		System.out.println((PastBoardStates.contains(bs2)?"does":"does not") + " contain bs2." );
		System.out.println((PastBoardStates.contains(bs3)?"does":"does not") + " contain bs3." );
		System.out.println((PastBoardStates.contains(bs4)?"does":"does not") + " contain bs4." );

	}
}

