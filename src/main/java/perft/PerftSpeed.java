package perft;

import perft.PerftCompare.PerftResult;
import tools.FenToBoard;
import tscp.Board;
import tscp.Move;

import java.io.IOException;
import java.util.List;

public class PerftSpeed {

    public static void main(String[] args) throws IOException {
        perftTest();
    }

    private static void perftTest() {
        //voir http://chessprogramming.wikispaces.com/Perft+Results     
        String f = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        Board board = FenToBoard.toBoard(f);
        int max_depth = 6;
        double t0 = System.nanoTime();
        for (int depth = 1; depth <= max_depth; depth++) {
            PerftResult res = perft(new Board(board), depth);
            double t1 = System.nanoTime();
            System.out.println("Depth " + depth + " : " + (t1 - t0) / 1000000000 + " sec");
            System.out.println("Count = " + res.moveCount);
        }

    }

    private static PerftResult perft(Board board, int depth) {

        PerftResult result = new PerftResult();
        if (depth == 0) {
            result.moveCount++;
            return result;
        }
        board.gen(board.side);
        List<Move> moves = board.pseudomoves;
        for (Move move : moves) {
            if (board.makemove(move)) {
                PerftResult subPerft = perft(new Board(board), depth - 1);
                board.takeback();
                result.moveCount += subPerft.moveCount;
            }
        }
        return result;
    }
}
