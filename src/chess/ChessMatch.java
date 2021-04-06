 package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Panw;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {
	
	private Board board;
	private int turn;
	private Color currentPlayer;
	private boolean check;
	private boolean checkMate;
	
	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();
	
	public ChessMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
		check = false;
		initialSetup();
	}
	
	public boolean getCheckMate() {
		return checkMate;
	}
	
	public int getTurn() {
		return turn;
	}
	
	public Color getCurrentPlayer() {
		return currentPlayer;
	}
	
	public boolean getCheck() {
		return check;
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
	
	public boolean[][] possibleMoves(ChessPosition sourcePosition ){
		Position position = sourcePosition.toPosition();
		validadeSourcePosition(position);
		
		return board.piece(position).possibleMoves();
	}
	
	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		// convertendo sourcePosition e targetPosition em posi��o da Matriz
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		validadeSourcePosition(source); // valida se existia uma pe�a na posi��o de origem
		validadeTargetPosition(source, target); // valida se existe uma pe�a na posi��o de destino
		Piece capturedPiece = makeMove(source, target);
		
		if(testCheck(currentPlayer)) {
			undoMove(source, target, capturedPiece);
			throw new ChessException("Voce nao pode se colocar em CHECK!");
		}
		
		check = (testCheck(opponent(currentPlayer))) ? true : false;
		/* a Linha acima testa se o oponent do currentPlayer ficou em check ap�s a jogada
		 se tiver ficado ele coloca o "check" como true, sen�o deixa como false */
		 
		if(testCheckMate(opponent(currentPlayer))) { // se o oponente do players atual estiver em check mate
			checkMate = true;
		}
		else {
			nextTurn();
		}			
		return (ChessPiece) capturedPiece; // downcasting  de Piece para ChessPiece
	}
	
	public Piece makeMove(Position source, Position target) {
		ChessPiece p = (ChessPiece)board.removePiece(source); // retira a pe�a de origem
		p.increaseMoveCount();// adicionando movimento ao metodo MoveCount()
		Piece capturedPiece = board.removePiece(target); // retira a pe�a capturada
		board.placePiece(p, target);
		if(capturedPiece != null) {
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);
		}	
		return capturedPiece;
	}
	
	private void undoMove(Position source, Position target, Piece capturedPiece) {
		// undoMove � tipo "desfazer movimento"
		ChessPiece p = (ChessPiece)board.removePiece(target);
		p.decreaseMoveCount(); // retirando movimento da pe�a 
		board.placePiece(p, source);
		if(capturedPiece != null) {
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}
	}
	
	private void validadeSourcePosition(Position position) {
		if(!board.theresIsAPiece(position)) {
			throw new ChessException("N�o existe pe�a na posi��o de origem!");
		}
		if(currentPlayer != ((ChessPiece)board.piece(position)).getColor()) {
			throw new ChessException("A pe�a escolhida n�o � sua!");
		}
		if(!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("N�o existe movimentos possiveis para a pe�a escolhida!");
		}
	}
	
	
	private void validadeTargetPosition(Position source, Position target) {
		if(!board.piece(source).possibleMove(target)) {
			throw new ChessException("Movimento Imposs�vel:"
					+ " Pe�a n�o pode mover-se para o destino selecionado");
		}
	}
	
	private void nextTurn() {
		turn++;
		currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
		// acima � uma condi��o alternaria... testa se o Cor do currentPlayer for BRANCO
		// ele muda para PRETO e se for PRETO ele muda para BRANCO
	}
	
	private Color opponent(Color color) {
		return color == color.WHITE ? color.BLACK : color.WHITE;
	}
	
	private ChessPiece king (Color color) {
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> 
						((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for(Piece p : list) { // para cada Piece "p" da linha lista "list"
			if(p instanceof King) { // se "p" for uma instancia de King
				return (ChessPiece)p;
			}
		}
		throw new IllegalStateException("N�o existe Rei " + color + "no tabuleiro.");
	}
	
	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x ->
						((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
		
		for(Piece p : opponentPieces){// para cada Piece "p" na lista de pe�as do oponent eu vou 
			//testar se tem algum movimento possivel que leva a pe�a do King
			boolean[][] mat = p.possibleMoves();
			if(mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}	
		}
		return false;
	}
	
	private boolean testCheckMate(Color color) {
		if(!testCheck(color)) {
			return false;
		}
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> 
				((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for(Piece p : list) {
			boolean[][] mat = p.possibleMoves();
			for (int i = 0; i < board.getRows(); i++) {
				for (int j = 0; j < board.getColumns(); j++) {
					if(mat[i][j]) {
						Position source = ((ChessPiece)p).getChessPosition().toPosition();
						Position target = new Position(i,j);
						Piece capturedPiece = makeMove(source, target);
						boolean testCheck = testCheck(color);
						undoMove(source, target, capturedPiece);
						if(!testCheck) { // se n�o estava em check
							return false;
						} 
					}
				}
			}
		}
		return true; 
	}
	
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		 board.placePiece(piece, new ChessPosition(column, row).toPosition());
		 piecesOnTheBoard.add(piece);
	}
	
	private void initialSetup() {

		placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('b', 1, new Knight(board, Color.WHITE));
        placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('d', 1, new Queen(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('g', 1, new Knight(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('a', 2, new Panw(board, Color.WHITE));
        placeNewPiece('b', 2, new Panw(board, Color.WHITE));
        placeNewPiece('c', 2, new Panw(board, Color.WHITE));
        placeNewPiece('d', 2, new Panw(board, Color.WHITE));
        placeNewPiece('e', 2, new Panw(board, Color.WHITE));
        placeNewPiece('f', 2, new Panw(board, Color.WHITE));
        placeNewPiece('g', 2, new Panw(board, Color.WHITE));
        placeNewPiece('h', 2, new Panw(board, Color.WHITE));
        
		placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('d', 8, new Queen(board, Color.BLACK));
        placeNewPiece('e', 8, new King(board, Color.BLACK));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
        placeNewPiece('a', 7, new Panw(board, Color.BLACK));
        placeNewPiece('b', 7, new Panw(board, Color.BLACK));
        placeNewPiece('c', 7, new Panw(board, Color.BLACK));
        placeNewPiece('d', 7, new Panw(board, Color.BLACK));
        placeNewPiece('e', 7, new Panw(board, Color.BLACK));
        placeNewPiece('f', 7, new Panw(board, Color.BLACK));
        placeNewPiece('g', 7, new Panw(board, Color.BLACK));
        placeNewPiece('h', 7, new Panw(board, Color.BLACK));
        
	}
	
}
