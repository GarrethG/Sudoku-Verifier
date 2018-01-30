package main.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class SudokuBoard {

    private final File file;

    SudokuBoard(File file) {
        this.file = file;
    }

    public void verify() {
        List<List<Integer>> sudokuBoard = getSudokuBoard(file);

        String msg = verifyHorizontalLines(sudokuBoard)
                && verifyVerticalLines(sudokuBoard)
                && verifyThreeByThreeGrids(sudokuBoard) ? "Sudoku board is valid" : "Sudoku board is invalid";

        System.out.println(msg);
    }

    /**
     * Moving left to right verify the vertical lines on the Sudoku board.
     */
    private boolean verifyVerticalLines(List<List<Integer>> sudokuBoard) {
        System.out.println("## Verifying Vertical Lines ##");

        return verifyVerticalLine(sudokuBoard, 0);
    }

    private boolean verifyVerticalLine(List<List<Integer>> sudokuBoard, final int index) {
        if (index > 8) return true;

        Set<Integer> set = sudokuBoard.stream()
                .parallel()//Order doesn't matter here so we can run in parallel
                .map(line -> line.get(index))
                .collect(Collectors.toSet());


        return isValid(set) && verifyHorizontalLine(sudokuBoard, index + 1);
    }

    /**
     * Moving top to bottom verify the horizontal lines on the Sudoku board.
     */
    private boolean verifyHorizontalLines(List<List<Integer>> sudokuBoard) {
        System.out.println("## Verifying Horizontal Lines ##");

        return verifyHorizontalLine(sudokuBoard, 0);
    }

    private boolean verifyHorizontalLine(List<List<Integer>> sudokuBoard, final int index) {
        if (index > 8) return true;

        return isValid(new HashSet<>(sudokuBoard.get(index)))
                && verifyVerticalLine(sudokuBoard, index + 1);
    }

    private boolean verifyThreeByThreeGrids(List<List<Integer>> sudokuBoard) {
        System.out.println("## Verifying 3x3 Grids ##");

        return verifyThreeByThreeGrid(sudokuBoard, 0, 0);
    }

    private boolean verifyThreeByThreeGrid(List<List<Integer>> sudokuBoard, final int depth, final int breadth) {
        Set<Integer> grid = new HashSet<>();

        for (int i = depth; i < depth + 3; i++) {
            for (int j = breadth; j < breadth + 3; j++) {
                grid.add(sudokuBoard.get(i).get(j));
            }
        }

        boolean valid = isValid(grid);

        if (valid && depth < 6) {
            int b = breadth < 6 ? breadth + 3 : 0; //Reach right most side of board. Start from left again.
            int d = b < 6 ? depth + 3 : 0; //If breadth has been reset to zero move down vertically

            valid = verifyThreeByThreeGrid(sudokuBoard, d, b);
        }

        return valid;
    }

    /**
     * The idea here is that a group is valid when it contains 9 elements meaning there was no duplication.
     * <p>
     * A group can be either:
     * - A vertical line
     * - A horizontal line
     * - A 3x3 grid
     */
    private boolean isValid(Set<Integer> group) {
        return group.size() == 9;
    }

    /**
     * A Sudoku board is a 9x9 grid. We represent it here as a List horizontal lines.
     */
    private List<List<Integer>> getSudokuBoard(File file) {
        try (BufferedReader bufferedReader = Files.newBufferedReader(file.toPath())) {
            return bufferedReader
                    .lines()//Order is important here for the 3x3 grids so we cannot run in parallel
                    .filter(line -> line != null && !line.isEmpty())
                    .map(line -> {
                        List<Integer> list = new ArrayList<>();
                        char[] arr = line.toCharArray();
                        for (int i = 0; i < line.length(); i++) {
                            list.add(Character.getNumericValue(arr[i]));
                        }
                        return list;
                    })
                    .collect(Collectors.toList());

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return Collections.emptyList();
    }
}