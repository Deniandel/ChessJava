package fl.ed.suncoast.jdbp.chess;

import java.util.ArrayList;

import fl.ed.suncoast.jdbp.chess.ChessGUI.ChessBoard;

/*
 * KNIGHT
 * 
 * One of the six chess pieces.
 * This piece can jump 2 spaces in one orthogonal direction and 1 space in the other orthogonal direction.
 * 
 */
public class Knight extends ChessPiece
{

	public Knight(boolean color)
	{
		super(color);
		this.setName(color?"\u265e":"\u2658");		
		this.setSprite(super.bi_spriteSheet.getSubimage(180, this.getColor()?60:0, 60, 60));
	}
	
	@Override
	//Gets and displays the full legal moveset of this ChessPiece.
	public ArrayList<Square> getLegalMoves(int x, int y, boolean real)
	{
		Square[][] squares = ChessGUI.ChessBoard.getSquares();
		ArrayList<Square> list = super.getLegalMoves(x,y,real);
		
		//Try to move normally.
		for(int i = 0; i <= 7; i++)
		{
			for(int j = 0; j <= 7; j++)
			{
				//Only allow moves to the correct squares
				if((x-i)*(x-i) + (y-j)*(y-j) == 5)	
				{
					//Only allow moves not occupied by an allied piece.
					if(squares[i][j].getChesspiece() == null || squares[i][j].getChesspiece().getColor() != this.getColor())
					{
						list.add(squares[i][j]);
					}
					
				}
			}
		}
		

		if(real) list = Square.intersection(list, Square.union(ChessBoard.getBlockingSquares(), ChessBoard.getCapturingSquares()));
		
		return list;
	}

}