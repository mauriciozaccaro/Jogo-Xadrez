package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class Panw extends ChessPiece {

	private ChessMatch chessMatch;
	
	public Panw(Board board, Color color, ChessMatch chessMatch) {
		super(board, color);
		this.chessMatch = chessMatch;
	}

	@Override
	public boolean[][] possibleMoves() {
		
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()]; // Aqui poderia ser boolean[8][8] daria na mesma
		
		Position p = new Position(0, 0);
		
		if(getColor() == Color.WHITE) { //peças Brancas
			p.setValues(position.getRow() -1, position.getColumn());
			if(getBoard().positionExists(p) && !getBoard().theresIsAPiece(p)) { // testa se o Peão pode mover-se 1 casa para frente
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() -2,  position.getColumn());
			Position p2 = new Position(position.getRow() -1, position.getColumn());
			if(getBoard().positionExists(p2) && !getBoard().theresIsAPiece(p2) && 
			   getBoard().positionExists(p) && !getBoard().theresIsAPiece(p) && getMoveCount() == 0) {
			 //testa se o Peão pode mover-se 2 casas para frente  
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() -1, position.getColumn() -1);
			if(getBoard().positionExists(p) && isThereOpponentPiece(p)) { // testa se o Peão pode mover-se 1 casa para o lado capturando uma peça adversária
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() -1, position.getColumn() +1);
			if(getBoard().positionExists(p) && isThereOpponentPiece(p)) { // testa se o Peão pode mover-se 1 casa para o lado capturando uma peça adversária
				mat[p.getRow()][p.getColumn()] = true;
			}
			
			// #specialmove en passant white
			if(position.getRow() == 3) {
				Position left = new Position(position.getRow(), position.getColumn() - 1);
				if(getBoard().positionExists(left) && isThereOpponentPiece(left) && getBoard().piece(left) == chessMatch.getEnPassantValverable()) {
					mat[left.getRow() -1][left.getColumn()] = true;
				}
				Position rigth = new Position(position.getRow(), position.getColumn() + 1);
				if(getBoard().positionExists(left) && isThereOpponentPiece(left) && getBoard().piece(left) == chessMatch.getEnPassantValverable()) {
					mat[rigth.getRow() -1][rigth.getColumn()] = true;
				}
			}
			
			 
		}
		else { // Peças Pretas
			p.setValues(position.getRow() +1, position.getColumn());
			if(getBoard().positionExists(p) && !getBoard().theresIsAPiece(p)) { // testa se o Peão pode mover-se 1 casa para frente
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() +2,  position.getColumn());
			Position p2 = new Position(position.getRow() +1, position.getColumn());
			if(getBoard().positionExists(p2) && !getBoard().theresIsAPiece(p2) && 
			   getBoard().positionExists(p) && !getBoard().theresIsAPiece(p) && getMoveCount() == 0) {
			 //testa se o Peão pode mover-se 2 casas para frente  
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() +1, position.getColumn() -1);
			if(getBoard().positionExists(p) && isThereOpponentPiece(p)) { // testa se o Peão pode mover-se 1 casa para o lado capturando uma peça adversária
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() +1, position.getColumn() +1);
			if(getBoard().positionExists(p) && isThereOpponentPiece(p)) { // testa se o Peão pode mover-se 1 casa para o lado capturando uma peça adversária
				mat[p.getRow()][p.getColumn()] = true;
			}

			// #specialmove en passant Black
			if(position.getRow() == 4) {
				Position left = new Position(position.getRow(), position.getColumn() - 1);
				if(getBoard().positionExists(left) && isThereOpponentPiece(left) && getBoard().piece(left) == chessMatch.getEnPassantValverable()) {
					mat[left.getRow() +1][left.getColumn()] = true;
				}
				Position rigth = new Position(position.getRow(), position.getColumn() + 1);
				if(getBoard().positionExists(left) && isThereOpponentPiece(left) && getBoard().piece(left) == chessMatch.getEnPassantValverable()) {
					mat[rigth.getRow() +1][rigth.getColumn()] = true;
				}
			}
		}
		return mat;
	}
	@Override
	public String toString() {
		return "P";
	}
}
