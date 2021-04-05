package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Panw extends ChessPiece {

	public Panw(Board board, Color color) {
		super(board, color);
	}

	@Override
	public boolean[][] possibleMoves() {
		
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()]; // Aqui poderia ser boolean[8][8] daria na mesma
		
		Position p = new Position(0, 0);
		
		if(getColor() == Color.WHITE) {
			p.setValues(position.getRow() -1, position.getColumn());
			if(getBoard().positionExists(p) && !getBoard().theresIsAPiece(p)) { // testa se o Pe�o pode mover-se 1 casa para frente
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() -2,  position.getColumn());
			Position p2 = new Position(position.getRow() -1, position.getColumn());
			if(getBoard().positionExists(p2) && !getBoard().theresIsAPiece(p2) && 
			   getBoard().positionExists(p) && !getBoard().theresIsAPiece(p) && getMoveCount() == 0) {
			 //testa se o Pe�o pode mover-se 2 casas para frente  
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() -1, position.getColumn() -1);
			if(getBoard().positionExists(p) && isThereOpponentPiece(p)) { // testa se o Pe�o pode mover-se 1 casa para o lado capturando uma pe�a advers�ria
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() -1, position.getColumn() +1);
			if(getBoard().positionExists(p) && isThereOpponentPiece(p)) { // testa se o Pe�o pode mover-se 1 casa para o lado capturando uma pe�a advers�ria
				mat[p.getRow()][p.getColumn()] = true;
			}
		}
		else {
			p.setValues(position.getRow() +1, position.getColumn());
			if(getBoard().positionExists(p) && !getBoard().theresIsAPiece(p)) { // testa se o Pe�o pode mover-se 1 casa para frente
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() +2,  position.getColumn());
			Position p2 = new Position(position.getRow() +1, position.getColumn());
			if(getBoard().positionExists(p2) && !getBoard().theresIsAPiece(p2) && 
			   getBoard().positionExists(p) && !getBoard().theresIsAPiece(p) && getMoveCount() == 0) {
			 //testa se o Pe�o pode mover-se 2 casas para frente  
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() +1, position.getColumn() -1);
			if(getBoard().positionExists(p) && isThereOpponentPiece(p)) { // testa se o Pe�o pode mover-se 1 casa para o lado capturando uma pe�a advers�ria
				mat[p.getRow()][p.getColumn()] = true;
			}
			p.setValues(position.getRow() +1, position.getColumn() +1);
			if(getBoard().positionExists(p) && isThereOpponentPiece(p)) { // testa se o Pe�o pode mover-se 1 casa para o lado capturando uma pe�a advers�ria
				mat[p.getRow()][p.getColumn()] = true;
			}
		}
		return mat;
	}
	@Override
	public String toString() {
		return "P";
	}
}
