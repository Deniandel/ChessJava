package fl.ed.suncoast.jdbp.chess;

import java.util.ArrayList;

import fl.ed.suncoast.jdbp.chess.ChessGUI.ChessBoard;


/*
 * QUEEN
 * 
 * One of the six chess pieces.
 * This piece can move orthogonally or diagonally any number of spaces.
 * 
 */

public class Queen extends ChessPiece
{
	public Queen(boolean color)
	{
		super(color);
		this.setName(color?"\u265b":"\u2655");		
		this.setSprite(super.bi_spriteSheet.getSubimage(0, this.getColor()?60:0, 60, 60));
	}
	
	@Override
	//Gets and displays the full legal moveset of this ChessPiece.
	public ArrayList<Square> getLegalMoves(int x, int y, boolean real)
	{
		ArrayList<Square> list = super.getLegalMoves(x,y,real);
		
		//Try to move normally.
		list.addAll(super.tryRookMovement(x,y));
		list.addAll(super.tryBishopMovement(x,y));		
		
		if(real) list = Square.intersection(list, Square.union(ChessBoard.getBlockingSquares(), ChessBoard.getCapturingSquares()));
		
		return list;
	}
}