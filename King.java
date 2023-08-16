package fl.ed.suncoast.jdbp.chess;

import java.util.ArrayList;

/*
 * KING
 * 
 * One of the six chess pieces.
 * This piece can move exactly one square orthogonally or diagonally.
 * It may also castle under certain conditions.
 * 
 * If this piece is captured, the game is over.
 * 
 */

public class King extends ChessPiece
{		
	private boolean b_isChecked = false;
	
	public King(boolean color)
	{
		super(color);
		this.setName(color?"\u265a":"\u2654");
		this.setSprite(super.bi_spriteSheet.getSubimage(60, this.getColor()?60:0, 60, 60));
	}
	
	//Gets
	public boolean getChecked()
	{
		return this.b_isChecked;
	}

	//Sets
	public void setChecked(boolean b_isChecked)
	{
		this.b_isChecked = b_isChecked;
	}

	@Override
	//Gets and displays the full legal moveset of this ChessPiece.
	public ArrayList<Square> getLegalMoves(int x, int y, boolean real)
	{
		Square[][] squares = ChessGUI.ChessBoard.getSquares();
		ArrayList<Square> list = super.getLegalMoves(x,y,real);
		
		//Try to castle.
		if(this.getState() == State.UNMOVED && !this.getChecked())
		{
			//Try to queenside castle.
			if(squares[0][y].getChesspiece() != null)
			{
				if((squares[0][y].getChesspiece().getState() == State.UNMOVED)
						&& (squares[1][y].getChesspiece() == null)
						&& (squares[2][y].getChesspiece() == null) && (squares[2][y].getCheckingSquares(this.getColor()).isEmpty())
						&& (squares[3][y].getChesspiece() == null) && (squares[3][y].getCheckingSquares(this.getColor()).isEmpty()))
				{
					list.add(squares[2][y]);
				}
			}
			
			
			//Try to kingside castle.
			if(squares[7][y].getChesspiece() != null)
			{
				if((squares[7][y].getChesspiece().getState() == State.UNMOVED)
						&& (squares[6][y].getChesspiece() == null) && (squares[6][y].getCheckingSquares(this.getColor()).isEmpty())
						&& (squares[5][y].getChesspiece() == null) && (squares[5][y].getCheckingSquares(this.getColor()).isEmpty()))
				{
					list.add(squares[6][y]);
				}
			}	
		}
			
		
		//Try to move normally.	
		for(int dx = Math.max(0, x-1); dx <= Math.min(7, x+1); dx++)
		{
			for(int dy = Math.max(0,y-1); dy <= Math.min(7, y+1); dy++)
			{
				if(squares[dx][dy].getChesspiece() == null || squares[dx][dy].getChesspiece().getColor() != this.getColor())
				{
					if(squares[dx][dy].getCheckingSquares(this.getColor()).isEmpty())
					{
						list.add(squares[dx][dy]);
					}
				}
			}
		}
		
		return list;
	}
}