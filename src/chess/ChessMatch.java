 package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {
	
	private Board board;
	
	public ChessMatch() {
		board = new Board(8, 8);
		initialSetup();
	}
	
	public ChessPiece[][] getPieces(){
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()]; 
		for(int i = 0; i < board.getRows(); i++) {
			for (int j = 0; j < board.getColumns(); j++) {
				mat[i][j] = (ChessPiece) board.piece(i, j);
			}
		}
		return mat;
	}
	
	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		// convertendo sourcePosition e targetPosition em posi��o da Matriz
		
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		
		validadeSourcePosition(source); // valida se existia uma pe�a na posi��o de origem
		
		Piece capturedPiece = makeMove(source, target);
		return (ChessPiece) capturedPiece; // downcasting  de Piece para ChessPiece
		
	}
	
	public Piece makeMove(Position source, Position target) {
		Piece p = board.removePiece(source); // retira a pe�a de origem
		Piece capturedPiece = board.removePiece(target); // retira a pe�a capturada
		
		board.placePiece(p, target);
		return capturedPiece;
	}
	private void validadeSourcePosition(Position position) {
		
		if(!board.theresIsAPiece(position)) {
			throw new ChessException("N�o existe pe�a na posi��o de origem!");
		}
	}
	
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		 board.placePiece(piece, new ChessPosition(column, row).toPosition());
	}
	
	private void initialSetup() {

		placeNewPiece('c', 1, new Rook(board, Color.WHITE));
        placeNewPiece('c', 2, new Rook(board, Color.WHITE));
        placeNewPiece('d', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 1, new Rook(board, Color.WHITE));
        placeNewPiece('d', 1, new King(board, Color.WHITE));

        placeNewPiece('c', 7, new Rook(board, Color.BLACK));
        placeNewPiece('c', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 8, new King(board, Color.BLACK));
	}
	
}
