import Pieces.*;
import Pieces.Rook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Board {
    int color = Main.WHITE;
    public ArrayList<ArrayList<Piece>> field = new ArrayList<>(8) {
    };

    Board() {
        ArrayList<Piece> pieces = new ArrayList<>(Arrays.asList(
                new Rook(Main.WHITE), new Knight(Main.WHITE), new Bishop(Main.WHITE), new Queen(Main.WHITE),
                new King(Main.WHITE), new Bishop(Main.WHITE), new Knight(Main.WHITE), new Rook(Main.WHITE)
        ));

        field.add(pieces);
        pieces = new ArrayList<>(Arrays.asList(
                new Pawn(Main.WHITE), new Pawn(Main.WHITE), new Pawn(Main.WHITE), new Pawn(Main.WHITE),
                new Pawn(Main.WHITE), new Pawn(Main.WHITE), new Pawn(Main.WHITE), new Pawn(Main.WHITE)
        ));

        field.add(pieces);
        for (int i = 0; i < 4; i++) {
            ArrayList<Piece> row = new ArrayList<>();
            for (int j = 0; j < 8; j++) {
                row.add(null);
            }
            field.add(row);
        }

        pieces = new ArrayList<>(Arrays.asList(
                new Pawn(Main.BLACK), new Pawn(Main.BLACK), new Pawn(Main.BLACK), new Pawn(Main.BLACK),
                new Pawn(Main.BLACK), new Pawn(Main.BLACK), new Pawn(Main.BLACK), new Pawn(Main.BLACK)
        ));

        field.add(pieces);
        pieces = new ArrayList<>(Arrays.asList(
                new Rook(Main.BLACK), new Knight(Main.BLACK), new Bishop(Main.BLACK), new Queen(Main.BLACK),
                new King(Main.BLACK), new Bishop(Main.BLACK), new Knight(Main.BLACK), new Rook(Main.BLACK)
        ));

        field.add(pieces);
    }

    /**
     * Переместить фигуру из точки (row, col) в точку (row1, col1).
     * Если перемещение возможно, метод выполнит его и вернет True.
     * Если нет --- вернет False
     */
    public boolean move_piece(int row, int col, int row1, int col1) {
        if (!Main.correctCoords(row, col) || !Main.correctCoords(row1, col1)) return false;
        if (row == row1 && col == col1) return false;
        Piece piece1 = field.get(row).get(col);
        if (piece1 == null) return false;
        if (piece1.getColor() != color) return false;
        Piece piece2 = field.get(row1).get(col1);
        if (piece2 == null) {
            if (!piece1.canMove(row, col, row1, col1, field)) return false;
        } else if (piece2.getColor() != piece1.getColor()) {
            if (!piece1.canAttack(row, col, row1, col1, field)) return false;
        } else return false;

        field.get(row).set(col, null);
        Piece p = field.get(row1).get(col1);
        field.get(row1).set(col1, piece1);
        if (isCheck(color)) {
            field.get(row).set(col, piece1);
            field.get(row1).set(col1, p);
            return false;
        }

        color = Main.opponent(color);
        return true;
    }

    /**
     * Возвращает строку из двух символов. Если в клетке (row, col)
     * находится фигура, символы цвета и фигуры. Если клетка пуста,
     * то два пробела.
     */
    public String cell(int row, int col) {
        Piece piece = field.get(row).get(col);
        if (piece != null) return piece.Char();
        return "  ";
    }

    public boolean movAndPromotePawn(int row, int col, int row1, int col1) {
        if (!Main.correctCoords(row, col) || !Main.correctCoords(row1, col1)) return false;
        if (!(field.get(row).get(col) instanceof Pawn)) return false;
        if (field.get(row1).get(col1) != null) {
            if (!field.get(row).get(col).canAttack(row, col, row1, col1, field)) return false;
            else if (!field.get(row).get(col).canMove(row, col, row1, col1, field)) return false;
        }
        if (!(row1 == 7 || row1 == 0)) return false;

        System.out.println("Пешка добралась до края доски");
        System.out.println("Введите 1 символ из списка, чтобы выбрать фигуру для превращения:");
        System.out.println("Q - королева");
        System.out.println("R - ладья");
        System.out.println("B - слон");
        System.out.println("N - конь");
        Scanner sc = new Scanner(System.in);
        char Char;
        while (true) {
            String str = sc.nextLine();
            if (str.length() == 1) Char = str.charAt(0);
            else {
                System.out.println("Неправильный ввод");
                System.out.println("Введите 1 символ из списка, чтобы выбрать фигуру для превращения:");
                System.out.println("Q - королева");
                System.out.println("R - ладья");
                System.out.println("B - слон");
                System.out.println("N - конь");
                continue;
            }
            switch (Char) {
                case 'Q':
                    field.get(row1).set(col1, new Queen(field.get(row).get(col).getColor()));
                    break;
                case 'R':
                    field.get(row1).set(col1, new Rook(field.get(row).get(col).getColor()));
                    break;
                case 'B':
                    field.get(row1).set(col1, new Bishop(field.get(row).get(col).getColor()));
                    break;
                case 'N':
                    field.get(row1).set(col1, new Knight(field.get(row).get(col).getColor()));
                    break;
                default: {
                    System.out.println("Такого варианта нет, попробуйте ещё раз");
                    System.out.println("Введите 1 символ из списка, чтобы выбрать фигуру для превращения:");
                    System.out.println("Q - королева");
                    System.out.println("R - ладья");
                    System.out.println("B - слон");
                    System.out.println("N - конь");
                    continue;
                }
            }
            field.get(row).set(col, null);
            color = Main.opponent(color);
            break;
        }

        return true;
    }

    public int currentPlayerColor() {
        return color;
    }

    public boolean castling0() {
        int row = (currentPlayerColor() == Main.WHITE) ? 0 : 7;
        if (!(field.get(row).get(4) instanceof King && field.get(row).get(0) instanceof Rook)) return false;
        if (!(field.get(row).get(4).getColor() == field.get(row).get(0).getColor())) return false;
        if (!field.get(row).get(0).canMove(row, 0, row, 3, field)) return false;

        Piece rc = field.get(row).get(0);
        Piece kc = field.get(row).get(4);
        field.get(row).set(0, null);
        field.get(row).set(4, null);
        field.get(row).set(3, rc);
        field.get(row).set(2, kc);
        color = Main.opponent(color);
        return true;
    }

    public boolean castling7() {
        int row = (currentPlayerColor() == Main.WHITE) ? 0 : 7;
        if (!(field.get(row).get(4) instanceof King && field.get(row).get(7) instanceof Rook)) return false;
        if (!(field.get(row).get(4).getColor() == field.get(row).get(7).getColor())) return false;
        if (!field.get(row).get(7).canMove(row, 7, row, 5, field)) return false;

        Piece rc = field.get(row).get(7);
        Piece kc = field.get(row).get(4);
        field.get(row).set(7, null);
        field.get(row).set(4, null);
        field.get(row).set(5, rc);
        field.get(row).set(6, kc);
        color = Main.opponent(color);
        return true;
    }

    public boolean isCheck(int color) {
        int[] coords = findKing(color);
        return is_under_attack(coords[0], coords[1], Main.opponent(color));
    }

    public int[] findKing(int color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = field.get(i).get(j);
                if (piece instanceof King && piece.getColor() == color)
                    return new int[]{i, j};
            }
        }

        return new int[]{};
    }

    public boolean isCheckmate(int color) {
        if (!isCheck(color)) {
            return false; // Если король не находится под шахом, то нет матовой позиции
        }

        int[] coords = findKing(color);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = field.get(i).get(j);
                if (!(piece != null && piece.getColor() == color)) continue;

                for (int x = 0; x < 8; x++) {
                    for (int y = 0; y < 8; y++) {
                        if (!piece.canMove(i, j, x, y, field)) continue;
                        // Попробуйте сделать ход и проверить, остается ли король под шахом
                        Piece targetPiece = field.get(x).get(y);
                        field.get(x).set(y, piece);
                        field.get(i).set(j, null);
                        boolean stillInCheck = is_under_attack(coords[0], coords[1], Main.opponent(color));

                        // Вернуть фигуры на место
                        field.get(i).set(j, piece);
                        field.get(x).set(y, targetPiece);

                        if (!stillInCheck) {
                            System.out.println(i + " " + j + " " + x + " " + y);
                            return false; // Найден ход, который убирает шах
                        }
                    }
                }
            }
        }

        return true; // Если нет возможных ходов для защиты короля, то это мат
    }

    public boolean is_under_attack(int row, int col, int color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (field.get(i).get(j) == null) continue;
                Piece piece = field.get(i).get(j);
                if (piece.getColor() == color && piece.canMove(i, j, row, col, field)) {
                    return true;
                }
            }
        }

        return false;
    }
}
