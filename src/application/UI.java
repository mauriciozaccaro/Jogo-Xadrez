package application;

import chess.ChessPiece;

public class UI {
	
	public static void printBoard(ChessPiece[][] pieces) {
		
		for(int i = 0; i < pieces.length; i++ ) {
			System.out.print((8 - i) + " "); // caso precise lembrar.. aqui come�a com (8 - i) que � (8 - 0) = 8
			for(int j = 0; j < pieces.length; j++) {
				printPiece(pieces[i][j]);
			}
			System.out.println();
		}
		System.out.println("  A B C D F E G H");
		
	}
	
	private static void printPiece(ChessPiece piece) {
		if(piece == null) {
			System.out.print("-");
		}
		else {
			System.out.print(piece);
		}
		System.out.print(" ");			
	}
}
