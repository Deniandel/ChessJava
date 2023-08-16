package fl.ed.suncoast.jdbp.chess;

import java.util.ArrayList;

import fl.ed.suncoast.jdbp.chess.ChessGUI.ChessBoard;

/*
 * BISHOP
 * 
 * One of the six chess pieces.
 * This piece can move in any diagonal direction any number of spaces.
 * 
 */

public class Bishop extends ChessPiece
{
	public Bishop(boolean color)
	{
		super(color);
		this.setName(color?"\u265d":"\u2657");		
		this.setSprite(super.bi_spriteSheet.getSubimage(240, this.getColor()?60:0, 60, 60));
	}
	
	@Override
	//Gets and displays the full legal moveset of this ChessPiece.
	public ArrayList<Square> getLegalMoves(int x, int y, boolean real)
	{
		ArrayList<Square> list = super.getLegalMoves(x,y,real);
		
		//Try to move normally.
		list.addAll(super.tryBishopMovement(x,y));
		

		if(real) list = Square.intersection(list, Square.union(ChessBoard.getBlockingSquares(), ChessBoard.getCapturingSquares()));
		
		return list;
	}
}