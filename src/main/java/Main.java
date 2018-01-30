package main.java;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private static final String FILE_NAME =
            "/Users/garrethgolding/dev/code/SudokuVerifier/src/main/resources/input_sudoku.txt";

    public static void main(String[] args) {
        Path path = Paths.get(FILE_NAME);

        SudokuBoard sudokuBoard = new SudokuBoard(path.toFile());
        sudokuBoard.verify();
    }
}
