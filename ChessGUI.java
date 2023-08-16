package fl.ed.suncoast.jdbp.chess;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import fl.ed.suncoast.jdbp.chess.ChessPiece.AbsolutePin;
import fl.ed.suncoast.jdbp.chess.ChessPiece.State;

public class ChessGUI
{
	@SuppressWarnings("serial")
	public static class ChessBoard extends JPanel
	{
		//Panel attributes.
		private final int P_WIDTH = 600, P_HEIGHT = 500;
		private final int SQUARE_SIZE = this.P_HEIGHT/10;
				
		//Squares.
		private static Square[][] squares = new Square[8][8];
		private static Square[] promotionSquares = new Square[4];
		private static ArrayList<Square> blockingSquares = new ArrayList<Square>(), capturingSquares = new ArrayList<Square>();
		
		//Moving ChessPieces.
		ChessPiece cp_movingPiece, cp_promotingPiece;		
		int ppx = 0;
		
		//Mouse position on the screen.
		int mpx, mpy;
		
		//Boolean flags.
		boolean b_isWhiteMove = true, b_isMoving = false, b_isPromoting = false;
		
		//Move list.
		ArrayList<String> moveList = new ArrayList<String>();
		
		
		//Constructor
		public ChessBoard()
		{
			//Set panel attributes.
			this.setPreferredSize(new Dimension(P_WIDTH, P_HEIGHT));
			this.setBackground(Color.BLACK);
			
			//Load mouse adapters.
			this.addMouseListener(mouseActions);
			this.addMouseMotionListener(mouseActions);

			//Set initial conditions.
			this.cp_movingPiece = null;
			
			//Create Squares.
			Color clr_a = new Color(251,201,38), clr_b = new Color(0,86,22);
			
			for(int j = 0; j <= 7; j++) 
			{
				for(int i = 0; i <= 7; i++)
				{	
					ChessBoard.squares[i][j] = new Square(i,j,(i+j)%2==0?clr_a:clr_b);
				}
			}
			
			for(int k = 0; k <= 3; k++) ChessBoard.promotionSquares[k] = new Square(0,0,Color.WHITE);

			//Set initial conditions.
			this.initialize();
		}
		
		//Gets
		public static Square[][] getSquares()
		{
			return squares;
		}
		
		public static ArrayList<Square> getBlockingSquares()
		{
			return blockingSquares;
		}
		
		public static ArrayList<Square> getCapturingSquares()
		{
			return capturingSquares;
		}
		
		
		//Sets the initial positions.
		public void initialize()
		{
			ChessBoard.squares[0][0].setChesspiece(new Rook(false));
			ChessBoard.squares[1][0].setChesspiece(new Knight(false));
			ChessBoard.squares[2][0].setChesspiece(new Bishop(false));
			ChessBoard.squares[3][0].setChesspiece(new Queen(false));
			ChessBoard.squares[4][0].setChesspiece(new King(false));
			ChessBoard.squares[5][0].setChesspiece(new Bishop(false));
			ChessBoard.squares[6][0].setChesspiece(new Knight(false));
			ChessBoard.squares[7][0].setChesspiece(new Rook(false));
			
			for(int i = 0; i <= 7; i++)
			{
				ChessBoard.squares[i][1].setChesspiece(new Pawn(false));
				ChessBoard.squares[i][6].setChesspiece(new Pawn(true));
			}
			
			
			ChessBoard.squares[0][7].setChesspiece(new Rook(true));
			ChessBoard.squares[1][7].setChesspiece(new Knight(true));
			ChessBoard.squares[2][7].setChesspiece(new Bishop(true));
			ChessBoard.squares[3][7].setChesspiece(new Queen(true));
			ChessBoard.squares[4][7].setChesspiece(new King(true));
			ChessBoard.squares[5][7].setChesspiece(new Bishop(true));
			ChessBoard.squares[6][7].setChesspiece(new Knight(true));
			ChessBoard.squares[7][7].setChesspiece(new Rook(true));
			
			this.updateCheckEvasionSquares(squares[4][7]);
			this.pin(squares[4][7]);
		}
		
		//Moves ChessPiece cp from current Square cs to destination Square ds.
		public void movePiece(Square cs, Square ds)
		{
			String capture = "";
			String check = "";
			
			//Move the chesspiece from t.
			cs.setChesspiece(null);
			
			//If a piece is on ds, capture it.
			if(ds.getChesspiece() != null)
			{
				ds.getChesspiece().setState(State.CAPTURED);
				capture += "x";
			}

			//Move the chesspiece to s.
			ds.setChesspiece(cp_movingPiece);
			
			//Update the piece's state.
			if(cp_movingPiece instanceof Pawn && cp_movingPiece.getState() == State.UNMOVED)
			{
				cp_movingPiece.setState(State.EN_PASSANT);
			}
			else
			{
				cp_movingPiece.setState(State.MOVED);
			}
			
			//If this was a King, try to castle.
			if(ds.getChesspiece() instanceof King)
			{				
				//If this King did castle, also move the corresponding Rook.
				if(cs.getX() == 4)
				{
					if(ds.getX() == 2)
					{
						ChessBoard.squares[3][ds.getY()].setChesspiece(ChessBoard.squares[0][ds.getY()].getChesspiece());
						ChessBoard.squares[0][ds.getY()].setChesspiece(null);
						
						this.moveList.add("0-0-0");
						//Change control.
						b_isWhiteMove = !b_isWhiteMove;
						return;
					}
					else if(ds.getX() == 6)
					{
						ChessBoard.squares[5][ds.getY()].setChesspiece(ChessBoard.squares[7][ds.getY()].getChesspiece());
						ChessBoard.squares[7][ds.getY()].setChesspiece(null);
						
						this.moveList.add("0-0");
						//Change control.
						b_isWhiteMove = !b_isWhiteMove;
						return;
					}
				}
			}
			else if(ds.getChesspiece() instanceof Pawn)
			{
				//If this was a Pawn, check to see if it captured an enemy Pawn en passant.
				if(ChessBoard.squares[ds.getX()][cs.getY()].getChesspiece() != null)
				{
					if(ChessBoard.squares[ds.getX()][cs.getY()].getChesspiece().getState() == State.EN_PASSANT)
					{
						ChessBoard.squares[ds.getX()][cs.getY()].setChesspiece(null);
						capture += (capture.equals("")?"x":"");
					}
				}
			}
			
			check = this.updateBoard();
			
			//Also record the move.
			this.moveList.add(ChessBoard.squares[ds.getX()][ds.getY()].getChesspiece().getName() + capture + Square.getChessCoordinates(ds.getX(),ds.getY()) + check);	

			if(check.equals("++"))
			{
				this.moveList.add(b_isWhiteMove?"1-0":"0-1");
			}
			
			//Change control.
			b_isWhiteMove = !b_isWhiteMove;
			
		}
		
		public String updateBoard()
		{
			String check = "";
			
			for(Square[] ss : ChessBoard.squares)
			{
				for(Square s : ss)
				{
					if(s.getChesspiece() != null)
					{
						if(s.getChesspiece().getColor() != this.b_isWhiteMove)
						{
							//If any enemy pawns could have been captured en passant, change their state.
							if(s.getChesspiece().getState() == State.EN_PASSANT)
							{
								s.getChesspiece().setState(State.MOVED);
							}
							
							if(s.getChesspiece() instanceof King)
							{
								//Uncheck the enemy King.
								((King)s.getChesspiece()).setChecked(false);
								
								//Pin enemy pieces to the enemy King.
								this.pin(s);
								
								//Reset the set of blocking and capturing Squares.
								this.updateCheckEvasionSquares(s);
								
								//Try to check the enemy king.
								if(!s.getCheckingSquares(!this.b_isWhiteMove).isEmpty())								
								{
									((King)s.getChesspiece()).setChecked(true);
									check += "+";
									
									//Try to checkmate the enemy king.
									if(s.getCheckingSquares(!this.b_isWhiteMove).size() > 1 && s.getChesspiece().getLegalMoves(s.getX(), s.getY(), true).isEmpty())
									{
										//King is doubly checked and unable to move. End game.
										s.getChesspiece().setState(State.CHECKMATED);
										check += "+";										
									}
									else
									{
										//King is singly checked. Restrict movement to capture or block the attacker.
										this.updateCheckEvasionSquares(s);		
										
										//Try to checkmate. If the King can't move, and no other allied piece can move to a blocking or capturing Square, end game.
										ArrayList<Square> evasionSquares = new ArrayList<Square>();
										if(s.getChesspiece().getLegalMoves(s.getX(), s.getY(), true).isEmpty())
										{
											for(Square[] tt : squares)
											{
												for(Square t : tt)
												{
													if(t.getChesspiece() != null && t.getChesspiece().getColor() == s.getChesspiece().getColor()) 
													{
														evasionSquares = Square.union(evasionSquares, t.getChesspiece().getLegalMoves(t.getX(), t.getY(), true));
													}
												}
											}
											
											evasionSquares = Square.intersection(evasionSquares, Square.union(blockingSquares, capturingSquares));
											
											if(evasionSquares.isEmpty())
											{
												//King is checkmated. End game.
												s.getChesspiece().setState(State.CHECKMATED);
												check += "+";	
											}
										}
									}
								}
							}
						}
						else if(s.getChesspiece() instanceof King)
						{
							//Pin allied pieces to the allied King.
							this.pin(s);
						}
					}
				}
			}
			
			return check;
		}
		
		//Moves the the Pawn on Square p to Square s, and promotes it.
		public void promotePawn(Square p, Square s)
		{
			if(!this.b_isPromoting)
			{
				this.b_isPromoting = true;
				
				//Setup promote menu.
				ChessBoard.promotionSquares[0].setChesspiece(new Queen(p.getChesspiece().getColor()));
				ChessBoard.promotionSquares[1].setChesspiece(new Rook(p.getChesspiece().getColor()));
				ChessBoard.promotionSquares[2].setChesspiece(new Bishop(p.getChesspiece().getColor()));
				ChessBoard.promotionSquares[3].setChesspiece(new Knight(p.getChesspiece().getColor()));
				
				//Adjust promoting Pawn's position.
				ChessBoard.squares[s.getX()][s.getY()].setChesspiece(p.getChesspiece());
				ChessBoard.squares[p.getX()][p.getY()].setChesspiece(null);
				this.ppx = s.getX();
			}
			else
			{
				//Actually promote this Pawn.
				String capture = "";
				Square q = ChessBoard.squares[this.ppx][this.mpy + (this.b_isWhiteMove?-1:1)];
				if(q.getChesspiece() != null && mpx != ppx) capture += "x";
				
				q.setChesspiece(this.cp_promotingPiece);				
				String check = this.updateBoard();
				
				//Also record the move.
				this.moveList.add(capture + Square.getChessCoordinates(p.getX(),p.getY()) + "=" + cp_promotingPiece.getName() + check);	
				
				if(check.equals("++"))
				{
					this.moveList.add(b_isWhiteMove?"1-0":"0-1");
				}
				
				//Change control.
				b_isWhiteMove = !b_isWhiteMove;
			}
			
			this.repaint();
		}
		
		//Pins all pieces allied to the King on Square k to that King, if necessary.
		public void pin(Square k)
		{
			for(Square ss[] : ChessBoard.squares)
			{
				for(Square s : ss)
				{
					if(s.getChesspiece() != null && s.getChesspiece().getColor() == k.getChesspiece().getColor())
					{
						s.getChesspiece().setPin(AbsolutePin.FREE);
					}
				}
			}
			
			/*
			 * (6/11)
			 * After unpinning everything, we have to search for pieces which
			 * need to be pinned to the King, which lies on Square k. Here's how:
			 * 
			 * 1) Starting at Square k, with coordinates x and y, search in each of
			 *    the eight directions using for-loops.
			 * 2) For each direction, search each Square in that direction until a ChessPiece
			 *    is found.
			 * 3) See if that ChessPiece, cp, is the same color as this King.
			 * 4) If it is, keep searching in that direction until a second ChessPiece,
			 *    cp2, is found. If it's not, stop immediately. 
			 * 5) See if cp2 and cp are different colors. If they are, see if cp2 is a 
			 *    Queen or a Rook/Bishop (depending on direction).
			 * 6) If all of that is true, pin cp in that direction.
			 */
			
			//Down-right
			int x = k.getX(), y = k.getY();
			ChessPiece king = k.getChesspiece(), cp = null, cp2 = null;
			
			
			for(int i = x+1, j = y+1; i <= 7 && j <=7; i++,j++)
			{
				//Search for the first ChessPiece.
				if(cp == null)
				{
					cp = squares[i][j].getChesspiece();
					
					//If the first ChessPiece is the same color, it might be able to be pinned.
					if(cp != null && cp.getColor() == king.getColor()) continue;
					//If not, stop immediately.
					else break;
				}
				//Search for the second ChessPiece.
				else
				{
					cp2 = squares[i][j].getChesspiece();
					
					//If the second ChessPiece is an enemy Queen or Rook, pin the first Chesspiece.
					if(cp2 != null && cp2.getColor() != cp.getColor())
					{
						if(cp2 instanceof Queen || cp2 instanceof Bishop)
						{
							cp.setPin(AbsolutePin.RIGHT);
						}
					}
				}
			}
			cp = null; cp2 = null;
			for(int i = x-1, j = y+1; i <= 7 && j <=7; i++,j++)
			{
				//Search for the first ChessPiece.
				if(cp == null)
				{
					cp = squares[i][j].getChesspiece();
					
					//If the first ChessPiece is the same color, it might be able to be pinned.
					if(cp != null && cp.getColor() == king.getColor()) continue;
					//If not, stop immediately.
					else break;
				}
				//Search for the second ChessPiece.
				else
				{
					cp2 = squares[i][j].getChesspiece();
					
					//If the second ChessPiece is an enemy Queen or Rook, pin the first Chesspiece.
					if(cp2 != null && cp2.getColor() != cp.getColor())
					{
						if(cp2 instanceof Queen || cp2 instanceof Bishop)
						{
							cp.setPin(AbsolutePin.RIGHT);
						}
					}
				}
			}
			cp = null; cp2 = null;
			for(int i = x+1, j = y-1; i <= 7 && j <=7; i++,j++)
			{
				//Search for the first ChessPiece.
				if(cp == null)
				{
					cp = squares[i][j].getChesspiece();
					
					//If the first ChessPiece is the same color, it might be able to be pinned.
					if(cp != null && cp.getColor() == king.getColor()) continue;
					//If not, stop immediately.
					else break;
				}
				//Search for the second ChessPiece.
				else
				{
					cp2 = squares[i][j].getChesspiece();
					
					//If the second ChessPiece is an enemy Queen or Rook, pin the first Chesspiece.
					if(cp2 != null && cp2.getColor() != cp.getColor())
					{
						if(cp2 instanceof Queen || cp2 instanceof Bishop)
						{
							cp.setPin(AbsolutePin.RIGHT);
						}
					}
				}
			}
			cp = null; cp2 = null;
			for(int i = x-1, j = y-1; i <= 7 && j <=7; i++,j++)
			{
				//Search for the first ChessPiece.
				if(cp == null)
				{
					cp = squares[i][j].getChesspiece();
					
					//If the first ChessPiece is the same color, it might be able to be pinned.
					if(cp != null && cp.getColor() == king.getColor()) continue;
					//If not, stop immediately.
					else break;
				}
				//Search for the second ChessPiece.
				else
				{
					cp2 = squares[i][j].getChesspiece();
					
					//If the second ChessPiece is an enemy Queen or Rook, pin the first Chesspiece.
					if(cp2 != null && cp2.getColor() != cp.getColor())
					{
						if(cp2 instanceof Queen || cp2 instanceof Bishop)
						{
							cp.setPin(AbsolutePin.RIGHT);
						}
					}
				}
			}
			cp = null; cp2 = null;
			for(int i = x+1; i <= 7 ; i++)
			{
				//Search for the first ChessPiece.
				if(cp == null)
				{
					cp = squares[i][y].getChesspiece();
					
					//If the first ChessPiece is the same color, it might be able to be pinned.
					if(cp != null && cp.getColor() == king.getColor()) continue;
					//If not, stop immediately.
					else break;
				}
				//Search for the second ChessPiece.
				else
				{
					cp2 = squares[i][y].getChesspiece();
					
					//If the second ChessPiece is an enemy Queen or Rook, pin the first Chesspiece.
					if(cp2 != null && cp2.getColor() != cp.getColor())
					{
						if(cp2 instanceof Queen || cp2 instanceof Bishop)
						{
							cp.setPin(AbsolutePin.RIGHT);
						}
					}
				}
			}
			cp = null; cp2 = null;
			for(int i = x-1; i <= 7 ; i++)
			{
				//Search for the first ChessPiece.
				if(cp == null)
				{
					cp = squares[i][y].getChesspiece();
					
					//If the first ChessPiece is the same color, it might be able to be pinned.
					if(cp != null && cp.getColor() == king.getColor()) continue;
					//If not, stop immediately.
					else break;
				}
				//Search for the second ChessPiece.
				else
				{
					cp2 = squares[i][y].getChesspiece();
					
					//If the second ChessPiece is an enemy Queen or Rook, pin the first Chesspiece.
					if(cp2 != null && cp2.getColor() != cp.getColor())
					{
						if(cp2 instanceof Queen || cp2 instanceof Bishop)
						{
							cp.setPin(AbsolutePin.RIGHT);
						}
					}
				}
			}
			cp = null; cp2 = null;
			for(int j = y-1; j <= 7 ; j++)
			{
				//Search for the first ChessPiece.
				if(cp == null)
				{
					cp = squares[x][j].getChesspiece();
					
					//If the first ChessPiece is the same color, it might be able to be pinned.
					if(cp != null && cp.getColor() == king.getColor()) continue;
					//If not, stop immediately.
					else break;
				}
				//Search for the second ChessPiece.
				else
				{
					cp2 = squares[x][j].getChesspiece();
					
					//If the second ChessPiece is an enemy Queen or Rook, pin the first Chesspiece.
					if(cp2 != null && cp2.getColor() != cp.getColor())
					{
						if(cp2 instanceof Queen || cp2 instanceof Bishop)
						{
							cp.setPin(AbsolutePin.RIGHT);
						}
					}
				}
			}
			cp = null; cp2 = null;
			for(int j = y+1; j <= 7 ; j++)
			{
				//Search for the first ChessPiece.
				if(cp == null)
				{
					cp = squares[x][j].getChesspiece();
					
					//If the first ChessPiece is the same color, it might be able to be pinned.
					if(cp != null && cp.getColor() == king.getColor()) continue;
					//If not, stop immediately.
					else break;
				}
				//Search for the second ChessPiece.
				else
				{
					cp2 = squares[x][j].getChesspiece();
					
					//If the second ChessPiece is an enemy Queen or Rook, pin the first Chesspiece.
					if(cp2 != null && cp2.getColor() != cp.getColor())
					{
						if(cp2 instanceof Queen || cp2 instanceof Bishop)
						{
							cp.setPin(AbsolutePin.RIGHT);
						}
					}
				}
			}
		}
		
		
		//Calculates the set of Squares a piece can move to to capture a piece attacking a checked King on Square k.
		public void updateCheckEvasionSquares(Square k)
		{
			
			ArrayList<Square> attackers = k.getCheckingSquares(k.getChesspiece().getColor());
					
			blockingSquares.clear();
			capturingSquares.clear();
			
			if(!((King)k.getChesspiece()).getChecked() || attackers.isEmpty())
			{
				//This King is not being attacked. Allow capture of all enemy pieces, and movement to all open Squares.
				for(Square[] ss : ChessBoard.squares)
				{
					for(Square s : ss)
					{
						if(s.getChesspiece() == null)
						{
							blockingSquares.add(s);
						}
						else if(s.getChesspiece().getColor() != k.getChesspiece().getColor())
						{
							capturingSquares.add(s);
						}
					}
				}
			}
			else if(attackers.size() == 1)
			{
				//This King is singly checked. Only allow the capture of the attacking piece.
				capturingSquares.add(attackers.get(0));
				
				//Only allow blocking of attacking Queens, Rooks, and Bishops.
				ChessPiece cp = attackers.get(0).getChesspiece();
				if(cp instanceof Queen || cp instanceof Rook || cp instanceof Bishop)
				{
					//find blocking squares based on the location of this piece
					int x1 = k.getX(), y1 = k.getY(), x2 = attackers.get(0).getX(), y2 = attackers.get(0).getY();
					int dx = x2 - x1, dy = y2 - y1;
					
					if(dx == 0)
					{
						//Attacker is vertically aligned with King.
						for(int j = Math.min(y1,y2) + 1; j < Math.max(y1, y2); j++)
						{
							blockingSquares.add(ChessBoard.squares[x1][j]);
						}
					}
					else if(dy == 0)
					{
						//Attacker is horizontally aligned with King.
						for(int i = Math.min(x1, x2) + 1; i < Math.max(x1, x2); i++)
						{
							blockingSquares.add(ChessBoard.squares[i][y1]);
						}
					}
					else if(Math.abs(dx) == Math.abs(dy))
					{
						//Attacker is diagonally aligned with King.
						for(int i = x2, j = y2; i != x1 && j!= y1; i+=(dx<0?1:-1), j+=(dy<0?1:-1))
						{
							blockingSquares.add(ChessBoard.squares[i][j]);
						}
					}
				}
			}
			//If there is more than one attacking piece, don't allow any captures or blocks.		
		}
				
		//Draws to the screen.
		@Override
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D)g;	
			
			//Write your name on the screen.
			g2d.setFont(new Font(g2d.getFont().getFontName(),Font.BOLD, 30 * this.SQUARE_SIZE/100));
			g2d.setColor(Color.WHITE);
			g2d.drawString("CHESS",this.P_WIDTH * 0.85f,this.P_HEIGHT * 0.3f);
			g2d.setFont(new Font(g2d.getFont().getFontName(),Font.BOLD, 15 * this.SQUARE_SIZE/100));
			g2d.drawString("A Java DB Programming Project", this.P_WIDTH * 0.8f, this.P_HEIGHT * 0.35f);
			
			//Write coordinates on the screen.
			g2d.setFont(new Font(g2d.getFont().getFontName(),Font.BOLD, 20 * this.SQUARE_SIZE/100));			
			for(int i = 1; i <= 8; i++)
			{
				g2d.drawString(Character.toString((char)(96 + i)), this.SQUARE_SIZE * (i + 0.45f), this.SQUARE_SIZE * 0.9f);
				g2d.drawString(Integer.toString(9 - i), this.SQUARE_SIZE * 0.85f, this.SQUARE_SIZE * (i + 0.55f));
			}

			//Draw the move list.
			g2d.setFont(new Font(g2d.getFont().getFontName(),Font.BOLD, 30 * this.SQUARE_SIZE/100));
			for(int i = 0; i < this.moveList.size(); i += 2)
			{
				//Only display the last few move pairs.
				int numPairs = 10;
				if(i == 0 && this.moveList.size() > 2*numPairs)
				{
					i += 2*((this.moveList.size() - 2*numPairs + 1)/2);
				}
				
				//Draw moves in pairs.
				g2d.drawString(i/2 + 1 + ". " + this.moveList.get(i) + " " + (this.moveList.size() - i > 1?this.moveList.get(i+1):""), this.P_WIDTH * 0.8f, this.P_HEIGHT*0.4f + (i - (this.moveList.size() > 2*numPairs? 2*((this.moveList.size() - 2*numPairs + 1)/2):0))/2 * 35 *  this.SQUARE_SIZE/100);
			}
			
			//Draw the chessboard and pieces onto the screen.
			AffineTransform at = new AffineTransform();
			at.scale(this.SQUARE_SIZE/60.0, this.SQUARE_SIZE/60.0);
		    at.translate(60, 60);
		    
			for(int x = 0; x <= 7; x++)
			{
				for(int y = 0; y <= 7; y++)
				{
					g2d.setColor(ChessBoard.squares[x][y].getColor());
					g2d.fillRect((x+1)*this.SQUARE_SIZE, (y+1)*this.SQUARE_SIZE, this.SQUARE_SIZE, this.SQUARE_SIZE);
					
					if(ChessBoard.squares[x][y].getChesspiece() != null)
					{
						at.translate(60*x, 60*y);
				    	g2d.drawImage(ChessBoard.squares[x][y].getChesspiece().getSprite(), at, this);
				    	at.translate(-60*x, -60*y);
					}
					
					if(ChessBoard.squares[x][y].getHighlighted())
					{
						g2d.setColor(new Color(150,150,150,192));
						g2d.fillRect((x+1)*this.SQUARE_SIZE, (y+1)*this.SQUARE_SIZE, this.SQUARE_SIZE, this.SQUARE_SIZE);
					}
				}
			}
			
			//Show pawn promotion menu if necessary.
			if(this.b_isPromoting)
			{
				g2d.setColor(ChessBoard.promotionSquares[0].getColor());
				int px = this.ppx*this.SQUARE_SIZE, py = (this.mpy-(this.b_isWhiteMove?0:1))*this.SQUARE_SIZE*7/4;
				px += this.SQUARE_SIZE*3/2;
				int[] tx = {px-this.SQUARE_SIZE/5,px,px+this.SQUARE_SIZE/5}, ty = {py,py-this.SQUARE_SIZE/2,py};
				px -= this.SQUARE_SIZE*3/2;
				g2d.fillPolygon(tx,ty,3);

				at.translate((this.ppx-1)*60.0, (this.mpy-(this.b_isWhiteMove?0.25:-1.75))*60.0);
				for(int i = 0; i <= 3; i++)
				{
					g2d.fillRect(px + i*this.SQUARE_SIZE, py, this.SQUARE_SIZE, this.SQUARE_SIZE);					
					g2d.drawImage(ChessBoard.promotionSquares[i].getChesspiece().getSprite(), at, this);
					at.translate(60, 0);
				}
			}
			
			g2d.dispose();
		}
	
		//Handles all mouse actions.
		private MouseAdapter mouseActions = new MouseAdapter()
		{
			//When any mouse button is pressed, do this.
			@Override
			public void mousePressed(MouseEvent e)
			{
				int x = e.getX()/SQUARE_SIZE - 1;
				int y = e.getY()/SQUARE_SIZE - 1;
				
				if(!b_isPromoting)
				{
					if(x >= 0 && x <= 7 && y >= 0 && y <= 7)
					{
						if(!b_isMoving)
						{
							//Check if a ChessPiece was clicked on.
							if(squares[x][y].getChesspiece() != null)
							{
								//If so, check if the correct color ChessPiece was clicked on.
								if(squares[x][y].getChesspiece().getColor() == b_isWhiteMove)
								{						
									for(Square s : squares[x][y].getChesspiece().getLegalMoves(x,y,true))
									{
										s.setHighlighted(true);
										
										cp_movingPiece = squares[x][y].getChesspiece();
										mpx = x;
										mpy = y;
										b_isMoving = true;
									}								
								}
							}
						}
						else
						{
							//Check if a highlighted square was clicked on.
							if(squares[x][y].getHighlighted())
							{
								//If this is a Pawn moving to its 8th rank, promote it first.
								if(cp_movingPiece instanceof Pawn && y == (b_isWhiteMove?0:7))
								{
									cp_promotingPiece = squares[mpx][mpy].getChesspiece();
									promotePawn(squares[mpx][mpy],squares[x][y]);
								}
								else
								{
									//Move the chesspiece to this Square.
									movePiece(squares[mpx][mpy],squares[x][y]);
								}
							}		
							
							//Reset the board and unhighlight all Squares.
							b_isMoving = false;
							if(!b_isPromoting) cp_movingPiece = null;
							for(Square[] s : squares)
							{
								for(Square t : s)
								{
									t.setHighlighted(false);
								}
							}
						}
					}
				}
				else
				{
					//Only allow mouseclicks to promote the relevant pawn.
					int px = e.getX(); 
					double py = (double)e.getY()/SQUARE_SIZE;
					if(px >= ppx*SQUARE_SIZE && px <= (ppx+4)*SQUARE_SIZE && py >= (b_isWhiteMove?1.75:8.75) && py <= (b_isWhiteMove?2.75:9.75)) 
					{
						px /= SQUARE_SIZE;
						switch(px-ppx)
						{
							case 0: cp_promotingPiece = new Queen(b_isWhiteMove); break;
							case 1: cp_promotingPiece = new Rook(b_isWhiteMove); break;
							case 2: cp_promotingPiece = new Bishop(b_isWhiteMove); break;
							case 3: cp_promotingPiece = new Knight(b_isWhiteMove); break;
							default: 
						}
						
						promotePawn(squares[mpx][mpy],squares[ppx][mpy]);
						b_isPromoting = false;
						b_isMoving = false;
					}
				}
				repaint();
			}
		};
	}
	
	
	public static void main(String[] args)
	{
		JFrame jf_main = new JFrame("Suncoast Chess");
		jf_main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Add panel to frame.
		jf_main.getContentPane().add(new ChessBoard());
		
		//Pack and set visible.
		jf_main.pack();
		jf_main.setResizable(false);
		jf_main.setVisible(true);
	}
}