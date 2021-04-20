package application;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import chess.ChessException;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;

public class Program {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		ChessMatch chessMatch = new ChessMatch();
		List<ChessPiece> captured = new ArrayList<>();

		while (!chessMatch.getCheckMate()) {

			try {
				UI.clearScreen(); // serve para limpar o Console, mas fica bugando quando executa no proprio
									// Eclipse
				UI.printMatch(chessMatch, captured);
				System.out.println();
				System.out.print("Source: ");
				ChessPosition source = UI.readChessPosition(sc);

				UI.clearScreen();
				boolean[][] possibleMoves = chessMatch.possibleMoves(source);
				UI.printBoard(chessMatch.getPieces(), possibleMoves);

				System.out.println();
				System.out.print("Target: ");

				ChessPosition target = UI.readChessPosition(sc);

				ChessPiece capturePiece = chessMatch.performChessMove(source, target);
				
				if (capturePiece != null) {
					captured.add(capturePiece);
				}
				
				if(chessMatch.getPromoted() != null) {
					System.out.println("Digite a pe�a que ser� promovida (B/N/R/Q)");
					String type = sc.nextLine();
					chessMatch.replacePromotedPiece(type);
				}

			} catch (ChessException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			} catch (InputMismatchException e) {
				System.out.println(e.getMessage());
				sc.nextLine();
			}
		}
		UI.clearScreen();
		UI.printMatch(chessMatch, captured);
	}
}
