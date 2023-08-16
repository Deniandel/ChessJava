package fl.ed.suncoast.jdbp.chess;

import java.util.ArrayList;

import fl.ed.suncoast.jdbp.chess.ChessGUI.ChessBoard;

/*
 * ROOK
 * 
 * One of the six chess pieces.
 * This piece can move in any orthogonal direction any number of spaces.
 * 
 */

public class Rook extends ChessPiece
{
	public Rook(boolean color)
	{
		super(color);
		this.setName(color?"\u265c":"\u2656");		
		this.setSprite(super.bi_spriteSheet.getSubimage(120, this.getColor()?60:0, 60, 60));
	}
	
	@Override
	//Gets and displays the full legal moveset of this ChessPiece.
	public ArrayList<Square> getLegalMoves(int x, int y, boolean real)
	{
		ArrayList<Square> list = super.getLegalMoves(x,y,real);
		
		//Try to move normally.
		list.addAll(super.tryRookMovement(x,y));		
		
		if(real) list = Square.intersection(list, Square.union(ChessBoard.getBlockingSquares(), ChessBoard.getCapturingSquares()));
				
		return list;
	}
}