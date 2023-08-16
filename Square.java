package fl.ed.suncoast.jdbp.chess;

import java.awt.Color;
import java.util.ArrayList;

/*
 * SQUARE
 * 
 * The CHESSBOARD is made up of 64 squares.
 * Each square has a Color, an x and y coordinate,
 * and can hold one (and only one) CHESSPIECE.
 * 
 */

public class Square
{
	private int x, y;
	private Color c;
	private ChessPiece cp;
	boolean b_isHighlighted;
	
	public Square(int x, int y, Color c)
	{
		this.setX(x);
		this.setY(y);
		this.setColor(c);
		this.setChesspiece(null);
		this.setHighlighted(false);
	}
	
	//Gets
	public int getX()
	{
		return this.x;
	}
	
	public int getY()
	{
		return this.y;
	}
	
	public Color getColor()
	{
		return this.c;
	}
	
	public ChessPiece getChesspiece()
	{
		return this.cp;
	}
	
	public boolean getHighlighted()
	{
		return this.b_isHighlighted;
	}
	
	
	//Sets	
	public void setX(int x)
	{
		this.x = x;
	}
	
	public void setY(int y)
	{
		this.y = y;
	}
	
	public void setColor(Color c)
	{
		this.c = c;
	}
	
	public void setChesspiece(ChessPiece cp)
	{
		this.cp = cp;
	}
	
	public void setHighlighted(boolean b)
	{
		this.b_isHighlighted = b;
	}
	
	
	//Gets this square's location on the board in chess coordinates.
	public static String getChessCoordinates(int x, int y)
	{
		String s = "";
		s += (char)(x + 97);
		s += (8 - y);
		return s;
	}
	
	//Gets the union of two sets of Squares, a and b.
	public static ArrayList<Square> union(ArrayList<Square> a, ArrayList<Square> b)
	{
		ArrayList<Square> u = new ArrayList<Square>();
		
		for(Square s : b)
		{
			if(a.contains(s)) a.remove(s);
		}
		
		u.addAll(a);
		u.addAll(b);
		
		return u;
	}
	
	//Gets the intersection of two sets of Squares, a and b.
	public static ArrayList<Square> intersection(ArrayList<Square> a, ArrayList<Square> b)
	{
		ArrayList<Square> n = new ArrayList<Square>();
		
		for(Square s : b)
		{
			if(a.contains(s)) n.add(s);
		}
		
		return n;
	}
	
	//Determines which, if any, Squares contain enemy pieces attacking this Square.
	public ArrayList<Square> getCheckingSquares(boolean color)
	{
		ArrayList<Square> list = new ArrayList<Square>();
		
		//Temporarily remove this color's King from play.
		King k = null;
		int x = 0, y = 0;
		for(Square[] tt : ChessGUI.ChessBoard.getSquares())
		{
			for(Square t : tt)
			{
				if(t.getChesspiece() != null && t.getChesspiece() instanceof King && t.getChesspiece().getColor() == color)
				{
					k = (King)t.getChesspiece();
					x = t.getX();
					y = t.getY();
					t.setChesspiece(null);
					break;
				}
			}
		}
		
		//Create a temporary square.
		Square s = new Square(this.getX(), this.getY(), Color.BLACK);
		
		//Try to move a phantom Rook from this square to an actual enemy Rook or Queen.
		s.setChesspiece(new Rook(color));
		for(Square t : s.getChesspiece().getLegalMoves(s.getX(), s.getY(), false))
		{
			if(t.getChesspiece() != null && t.getChesspiece().getColor() == !color && (t.getChesspiece() instanceof Rook || t.getChesspiece() instanceof Queen))
			{
				list.add(t);
			}
		}
		
		//Try to move a phantom Bishop from this square to an actual enemy Bishop or Queen.
		s.setChesspiece(new Bishop(color));
		for(Square t : s.getChesspiece().getLegalMoves(s.getX(), s.getY(), false))
		{
			if(t.getChesspiece() != null && t.getChesspiece().getColor() == !color && (t.getChesspiece() instanceof Bishop || t.getChesspiece() instanceof Queen))
			{
				list.add(t);
			}
		}
		
		//Try to move a phantom Knight from this square to an actual enemy Knight.
		s.setChesspiece(new Knight(color));
		for(Square t : s.getChesspiece().getLegalMoves(s.getX(), s.getY(), false))
		{
			if(t.getChesspiece() != null && t.getChesspiece().getColor() == !color && (t.getChesspiece() instanceof Knight))
			{
				list.add(t);
			}
		}
		
		//Look for Kings and Pawns within striking distance.
		for(Square[] tt : ChessGUI.ChessBoard.getSquares())
		{
			for(Square t : tt)
			{
				if(t.getChesspiece() != null && t.getChesspiece().getColor() != color)
				{	
					if(t.getChesspiece() instanceof King && Math.pow(this.getX() - t.getX(), 2) + Math.pow(this.getY() - t.getY(), 2) <= 2)
					{
						list.add(t);
					}
					else if(t.getChesspiece() instanceof Pawn && Math.abs(t.getX() - this.getX()) == 1 && t.getY() == this.getY() + (t.getChesspiece().getColor()?1:-1))
					{
						list.add(t);
					}
				}
			}
		}
		
		//Restore this color's King.
		ChessGUI.ChessBoard.getSquares()[x][y].setChesspiece(k);
		
		return list;
	}
}