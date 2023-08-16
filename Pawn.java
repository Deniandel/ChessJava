package fl.ed.suncoast.jdbp.chess;

import java.util.ArrayList;

import fl.ed.suncoast.jdbp.chess.ChessGUI.ChessBoard;

/*
 * PAWN
 * 
 * One of the six chess pieces.
 * This piece can move one space forward.
 * 
 * It may also move two spaces forward, or be captured en passant, under certain conditions.
 * 
 */

public class Pawn extends ChessPiece
{
	public Pawn(boolean color)
	{
		super(color);
		this.setName("");
		//this.setName(color?"\u265f":"\u2659");		
		this.setSprite(super.bi_spriteSheet.getSubimage(300, this.getColor()?60:0, 60, 60));
	}
	
	@Override
	//Gets and displays the full legal moveset of this ChessPiece.
	public ArrayList<Square> getLegalMoves(int x, int y, boolean real)
	{
		Square[][] squares = ChessGUI.ChessBoard.getSquares();
		ArrayList<Square> list = super.getLegalMoves(x,y,real);
		
		//Pawns can move one or two squares forward on its first turn; one otherwise.
		for(int i = 1; i <= (this.getState() == State.UNMOVED?2:1); i++)
		{
			if(squares[x][y+(this.getColor()?-i:i)].getChesspiece() == null)
			{
				list.add(squares[x][y+(this.getColor()?-i:i)]);
			}
			else break;
		}
		

		list = Square.intersection(list, Square.union(ChessBoard.getBlockingSquares(), ChessBoard.getCapturingSquares()));
		
		//Try to capture diagonally left, including en passant.
		if(x != 0)
		{
			if(squares[x-1][y+(this.getColor()?-1:1)].getChesspiece() != null && squares[x-1][y+(this.getColor()?-1:1)].getChesspiece().getColor() != this.getColor() && ChessBoard.getCapturingSquares().contains(squares[x-1][y+(this.getColor()?-1:1)]))
			{
				list.add(squares[x-1][y+(this.getColor()?-1:1)]);
			}
			
			
			if(squares[x-1][y].getChesspiece() != null && squares[x-1][y].getChesspiece().getColor() != this.getColor() && squares[x-1][y].getChesspiece().getState() == State.EN_PASSANT)
			{
				//Also look for discovered check.
				//Temporarily capture the enemy pawn en passant and find out if the allied King would now be attacked.
				Pawn p = (Pawn)squares[x-1][y].getChesspiece();
				squares[x-1][y].setChesspiece(null);
				squares[x-1][y+(this.getColor()?-1:1)].setChesspiece(squares[x][y].getChesspiece());
				squares[x][y].setChesspiece(null);
				
				//Locate the allied King and find attackers.
				for(Square[] ss : squares)
				{
					for(Square s : ss)
					{
						if(s.getChesspiece() != null && s.getChesspiece() instanceof King 
								&& s.getChesspiece().getColor() == this.getColor())
						{
							if(s.getCheckingSquares(this.getColor()).isEmpty())
							{
								//If (and only if) the King would not have any attackers, allow en passant capture.
								list.add(squares[x-1][y+(this.getColor()?-1:1)]);
							}
							break;
						}
					}
				}
				
				//Restore pawns.
				squares[x][y].setChesspiece(squares[x-1][y+(this.getColor()?-1:1)].getChesspiece());
				squares[x-1][y+(this.getColor()?-1:1)].setChesspiece(null);
				squares[x-1][y].setChesspiece(p);
			}
		}
		
		
		//Try to capture diagonally right, including en passant.
		if(x != 7)
		{
			if(squares[x+1][y+(this.getColor()?-1:1)].getChesspiece() != null && squares[x+1][y+(this.getColor()?-1:1)].getChesspiece().getColor() != this.getColor() && ChessBoard.getCapturingSquares().contains(squares[x+1][y+(this.getColor()?-1:1)]))
			{
				list.add(squares[x+1][y+(this.getColor()?-1:1)]);	
			}
			
			if(squares[x+1][y].getChesspiece() != null && squares[x+1][y].getChesspiece().getColor() != this.getColor() && squares[x+1][y].getChesspiece().getState() == State.EN_PASSANT)
			{
				//Also look for discovered check.
				//Temporarily capture the enemy pawn en passant and find out if the allied King would now be attacked.
				Pawn p = (Pawn)squares[x+1][y].getChesspiece();
				squares[x+1][y].setChesspiece(null);
				squares[x+1][y+(this.getColor()?-1:1)].setChesspiece(squares[x][y].getChesspiece());
				squares[x][y].setChesspiece(null);
				
				//Locate the allied King and find attackers.
				for(Square[] ss : squares)
				{
					for(Square s : ss)
					{
						if(s.getChesspiece() != null && s.getChesspiece() instanceof King 
								&& s.getChesspiece().getColor() == this.getColor())
						{
							if(s.getCheckingSquares(this.getColor()).isEmpty())
							{
								//If (and only if) the King would not have any attackers, allow en passant capture.
								list.add(squares[x+1][y+(this.getColor()?-1:1)]);
							}
							break;
						}
					}
				}
				
				//Restore pawns.
				squares[x][y].setChesspiece(squares[x+1][y+(this.getColor()?-1:1)].getChesspiece());
				squares[x+1][y+(this.getColor()?-1:1)].setChesspiece(null);
				squares[x+1][y].setChesspiece(p);
			}
		}
	
		
		return list;
	}
}