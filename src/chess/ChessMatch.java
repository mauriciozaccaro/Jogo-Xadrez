 package chess;

import java.security.InvalidParameterException;
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
	private ChessPiece enPassantValverable;
	private ChessPiece promoted;
	
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
	
	public ChessPiece getEnPassantValverable() {
		return enPassantValverable;
	}
	
	public ChessPiece getPromoted() {
		return promoted;
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
		// convertendo sourcePosition e targetPosition em posição da Matriz
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		validadeSourcePosition(source); // valida se existia uma peça na posição de origem
		validadeTargetPosition(source, target); // valida se existe uma peça na posição de destino
		Piece capturedPiece = makeMove(source, target);
		
		if(testCheck(currentPlayer)) {
			undoMove(source, target, capturedPiece);
			throw new ChessException("Voce nao pode se colocar em CHECK!");
		}
		
		ChessPiece movedPiece = (ChessPiece)board.piece(target); // aqui é para usar no enPassantVulnerable   para ver se a peça ficou vulneravel para tomar a jogada "en Passant"
		
		//#SpecialMove promoted
		promoted = null;
		if(movedPiece instanceof Panw) { // se a peça movida foi um Peão
			if(movedPiece.getColor() == Color.WHITE && target.getRow() == 0 ||
			   movedPiece.getColor() == Color.BLACK && target.getRow() == 7) {
				promoted = (ChessPiece)board.piece(target);
				promoted = replacePromotedPiece("Q");
			}
		}
		
		check = (testCheck(opponent(currentPlayer))) ? true : false;
		/* a Linha acima testa se o oponent do currentPlayer ficou em check após a jogada
		 se tiver ficado ele coloca o "check" como true, senão deixa como false */
		 
		if(testCheckMate(opponent(currentPlayer))) { // se o oponente do players atual estiver em check mate
			checkMate = true;
		}
		else {
			nextTurn();
		}			
		
		// #specialmove en passant
		if(movedPiece instanceof Panw && (target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2)) {
			enPassantValverable = movedPiece;
		}
		else {
			enPassantValverable = null;
		}
		
		
		return (ChessPiece) capturedPiece; // downcasting  de Piece para ChessPiece
	}
	
	public ChessPiece replacePromotedPiece(String type) {
		if(promoted == null) {
			throw new IllegalStateException("Não há peça para ser promovida!");
		}
		if(!type.equals("B") && !type.equals("N") && !type.equals("R") && !type.equals("Q")) {
			//throw new InvalidParameterException("Tipo inválido para Promoção!!");
			return promoted;
		}
		
		Position pos = promoted.getChessPosition().toPosition();
		Piece p = board.removePiece(pos);
		piecesOnTheBoard.remove(p);
		
		ChessPiece newPiece = newPiece(type, promoted.getColor());
		board.placePiece(newPiece, pos); // colocando a "nova peça" no lugar da peça promovida (pos)
		piecesOnTheBoard.add(newPiece);
		
		return newPiece;
	}
	
	public ChessPiece newPiece(String type, Color color) {
		if(type.equals("B")) return new Bishop(board, color);
		if(type.equals("N")) return new Knight(board, color);
		if(type.equals("Q")) return new Queen(board, color);
		return new Rook(board, color);
	}
	
	public Piece makeMove(Position source, Position target) {
		ChessPiece p = (ChessPiece)board.removePiece(source); // retira a peça de origem
		p.increaseMoveCount();// adicionando movimento ao metodo MoveCount()
		Piece capturedPiece = board.removePiece(target); // retira a peça capturada
		board.placePiece(p, target);
		if(capturedPiece != null) {
			piecesOnTheBoard.remove(capturedPiece);
			capturedPieces.add(capturedPiece);
		}	
		
		// #specialMove Castling Kingside Rook
		if(p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3); // estou pegando a posição da peça a +3 colunas a direita do Rei
					// que no caso é a própria Torre (Rook) e colocando na variavel sourceT que é a origem da peça
			Position targetT = new Position(source.getRow(), source.getColumn() + 1); // armazena na variavel targetT a posição de destino da peça (torre)
			
			ChessPiece rook = (ChessPiece)board.removePiece(sourceT); // estou removendo a peça que está na posição de origem (Torre)
			board.placePiece(rook, targetT); // passando a nova posição da peça que foi removida
			rook.increaseMoveCount(); // adicionando movimento a contagem
		}
		// #specialMove Castling Queenside Rook
		if(p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4); // estou pegando a posição da peça a +3 colunas a direita do Rei
					// que no caso é a própria Torre (Rook) e colocando na variavel sourceT que é a origem da peça
			Position targetT = new Position(source.getRow(), source.getColumn() - 1); // armazena na variavel targetT a posição de destino da peça (torre)
			
			ChessPiece rook = (ChessPiece)board.removePiece(sourceT); // estou removendo a peça que está na posição de origem (Torre)
			board.placePiece(rook, targetT); // passando a nova posição da peça que foi removida
			rook.increaseMoveCount(); // adicionando movimento a contagem
		}
		
		// #specialmove en passant
		if(p instanceof Panw) {
			if(source.getColumn() != target.getColumn() && capturedPiece == null) {
				Position pawnPosition;
				if(p.getColor() == Color.WHITE) {
					pawnPosition = new Position(target.getRow() + 1, target.getColumn());
				}
				else {
					pawnPosition = new Position(target.getRow() - 1, target.getColumn());
				}
				capturedPiece = board.removePiece(pawnPosition);
				capturedPieces.add(capturedPiece);
				piecesOnTheBoard.remove(capturedPiece);
			}
		}
		
		return capturedPiece;
	}
	
	private void undoMove(Position source, Position target, Piece capturedPiece) {
		// undoMove é tipo "desfazer movimento"
		ChessPiece p = (ChessPiece)board.removePiece(target);
		p.decreaseMoveCount(); // retirando movimento da peça 
		board.placePiece(p, source);
		if(capturedPiece != null) {
			board.placePiece(capturedPiece, target);
			capturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}
		// #specialMove Castling Kingside Rook
		if(p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3); 
			Position targetT = new Position(source.getRow(), source.getColumn() + 1); 
			
			ChessPiece rook = (ChessPiece)board.removePiece(targetT); 
			board.placePiece(rook, sourceT); 
			rook.decreaseMoveCount(); // retirando movimento da contagem
		}
		// #specialMove Castling Queenside Rook
		if(p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4); 
			Position targetT = new Position(source.getRow(), source.getColumn() - 1);
			
			ChessPiece rook = (ChessPiece)board.removePiece(targetT); 
			board.placePiece(rook, sourceT);
			rook.decreaseMoveCount(); // adicionando movimento a contagem
		}
		// #especialmove
		if(p instanceof Panw) {
			if(source.getColumn() != target.getColumn() && capturedPiece == enPassantValverable) {
				ChessPiece pawn = (ChessPiece)board.removePiece(target);
				Position pawnPosition;
				if(p.getColor() == Color.WHITE) {
					pawnPosition = new Position(3, target.getColumn());
				}
				else {
					pawnPosition = new Position(4, target.getColumn());
				}
				board.placePiece(pawn, pawnPosition);
			}
		}
	}
	
	private void validadeSourcePosition(Position position) {
		if(!board.theresIsAPiece(position)) {
			throw new ChessException("Não existe peça na posição de origem!");
		}
		if(currentPlayer != ((ChessPiece)board.piece(position)).getColor()) {
			throw new ChessException("A peça escolhida não é sua!");
		}
		if(!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessException("Não existe movimentos possiveis para a peça escolhida!");
		}
	}
	
	
	private void validadeTargetPosition(Position source, Position target) {
		if(!board.piece(source).possibleMove(target)) {
			throw new ChessException("Movimento Impossível:"
					+ " Peça não pode mover-se para o destino selecionado");
		}
	}
	
	private void nextTurn() {
		turn++;
		currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
		// acima é uma condição alternaria... testa se o Cor do currentPlayer for BRANCO
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
		throw new IllegalStateException("Não existe Rei " + color + "no tabuleiro.");
	}
	
	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = piecesOnTheBoard.stream().filter(x ->
						((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
		
		for(Piece p : opponentPieces){// para cada Piece "p" na lista de peças do oponent eu vou 
			//testar se tem algum movimento possivel que leva a peça do King
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
						if(!testCheck) { // se não estava em check
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
        placeNewPiece('e', 1, new King(board, Color.WHITE, this));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('g', 1, new Knight(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('a', 2, new Panw(board, Color.WHITE, this));
        placeNewPiece('b', 2, new Panw(board, Color.WHITE, this));
        placeNewPiece('c', 2, new Panw(board, Color.WHITE, this));
        placeNewPiece('d', 2, new Panw(board, Color.WHITE, this));
        placeNewPiece('e', 2, new Panw(board, Color.WHITE, this));
        placeNewPiece('f', 2, new Panw(board, Color.WHITE, this));
        placeNewPiece('g', 6, new Panw(board, Color.WHITE, this)); //mexi nesse era G2
        placeNewPiece('h', 2, new Panw(board, Color.WHITE, this));
        
		placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('d', 8, new Queen(board, Color.BLACK));
        placeNewPiece('e', 8, new King(board, Color.BLACK, this));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
        placeNewPiece('a', 7, new Panw(board, Color.BLACK, this));
        placeNewPiece('b', 7, new Panw(board, Color.BLACK, this));
        placeNewPiece('c', 7, new Panw(board, Color.BLACK, this));
        placeNewPiece('d', 7, new Panw(board, Color.BLACK, this));
        placeNewPiece('e', 7, new Panw(board, Color.BLACK, this));
        placeNewPiece('f', 7, new Panw(board, Color.BLACK, this));
        placeNewPiece('g', 7, new Panw(board, Color.BLACK, this));
        placeNewPiece('h', 7, new Panw(board, Color.BLACK, this));
        
	}
	
}
