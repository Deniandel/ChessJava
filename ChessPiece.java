package fl.ed.suncoast.jdbp.chess;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/*
 * CHESS PIECE
 * 
 * The main component of the game.
 * Each piece has a color on the board,
 * as well as a state (captured or not).
 * 
 */


public class ChessPiece
{
	protected BufferedImage bi_spriteSheet;
	
	private BufferedImage bi_sprite;
	private String name;
	private boolean b_isWhite;
	private AbsolutePin pinStatus;

	enum State
	{
		UNMOVED,
		EN_PASSANT,
		MOVED,
		CHECKMATED,
		CAPTURED
	};
	private State state;	
	
	//Creates a new chess piece.
	public ChessPiece(boolean color)
	{
		//Load spritesheet.
		try
		{
			this.bi_spriteSheet = ImageIO.read(new File("ChessPiecesArray.png")); 
		} 
		catch (IOException ioe)
		{
			System.out.println("Couldn't load sprites.");
			System.exit(0);
		}
		
		this.setColor(color);
		this.setState(State.UNMOVED);
	}
	
	//Gets
	public String getName()
	{
		return this.name;
	}

	public boolean getColor()
	{
		return this.b_isWhite;
	}
	
	public BufferedImage getSprite()
	{
		return this.bi_sprite;
	}
	
	public State getState()
	{
		return this.state;
	}
	
		
	//Sets
	public void setName(String name)
	{
		this.name = name;
	}

	public void setColor(boolean color)
	{
		this.b_isWhite = color;
	}
	
	public void setSprite(BufferedImage sprite)
	{
		this.bi_sprite = sprite;
	}
	
	public void setState(State state)
	{
		this.state = state;
	}
	
	
	//Gets and displays the full legal moveset of this ChessPiece.
	public ArrayList<Square> getLegalMoves(int x, int y, boolean real)
	{
		return new ArrayList<Square>();
	}
	enum AbsolutePin {
	    LEFT,
	    RIGHT,
	    UP,
	    DOWN,
	    UP_LEFT,
	    UP_RIGHT,
	    DOWN_LEFT,
	    DOWN_RIGHT,
	    FREE
	};{
	
	
	this.setPin(AbsolutePin.FREE);}

	public AbsolutePin getPin() {
	    return this.pinStatus;
	}

	public void setPin(AbsolutePin pinStatus) {
	    this.pinStatus = pinStatus;
	}

	
	//Checks if a Square on a slider's path can be moved to.
	public boolean checkSquareOnPath(Square[][] squares, ArrayList<Square> list, int x, int y)
	{
		if(squares[x][y].getChesspiece() == null)
		{
			list.add(squares[x][y]);
			return false;
		}
		else if(squares[x][y].getChesspiece().getColor() != this.getColor())
		{
			list.add(squares[x][y]);
			return true;
		}
		return true;
	}
	
	//Tries to get a Rook's legal moveset.
	public ArrayList<Square> tryRookMovement(int x, int y)
	{
		Square[][] squares = ChessGUI.ChessBoard.getSquares();
		ArrayList<Square> list = new ArrayList<Square>();
		
		for(int i = x-1; i >= 0; i--)
		{
			if(this.checkSquareOnPath(squares, list, i, y)) break;
		}
		for(int i = x+1; i <= 7; i++)
		{
			if(this.checkSquareOnPath(squares, list, i, y)) break;
		}
	
		for(int j = y-1; j >= 0; j--)
		{
			if(this.checkSquareOnPath(squares, list, x, j)) break;
		}
		for(int j = y+1; j <= 7; j++)
		{
			if(this.checkSquareOnPath(squares, list, x, j)) break;
		}
				
		return list;
	}
	
	public ArrayList<Square> tryBishopMovement(int x, int y)
	{
		Square[][] squares = ChessGUI.ChessBoard.getSquares();
		ArrayList<Square> list = new ArrayList<Square>();
		
		for(int i = x-1, j = y-1; i >= 0 && j >= 0; i--, j--)
		{
			if(this.checkSquareOnPath(squares, list, i, j)) break;
		}
		for(int i = x+1, j = y+1; i <= 7 && j <= 7; i++, j++)
		{
			if(this.checkSquareOnPath(squares, list, i, j)) break;
		}
		for(int i = x+1, j = y-1; i <= 7 && j >= 0; i++, j--)
		{
			if(this.checkSquareOnPath(squares, list, i, j)) break;
		}
		for(int i = x-1, j = y+1; i >= 0 && j <= 7; i--, j++)
		{
			if(this.checkSquareOnPath(squares, list, i, j)) break;
		}
		
		return list;
	}
}