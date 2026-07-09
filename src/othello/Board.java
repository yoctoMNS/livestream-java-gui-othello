package othello;

/**
 * オセロの盤面とルールを管理するモデルクラス(MVC における Model)。
 *
 * <p>このクラスは「8x8の盤面に石を置いたときに何が起こるか」という
 * ゲームルールのみを知っており、画面描画やキー入力については
 * 一切関知しない。View・Controller から独立してテストできることを
 * 意図した設計になっている(関心の分離 / SOLIDの単一責任原則)。</p>
 *
 * <p>実装している最小限のルールは次のとおり:</p>
 * <ul>
 *   <li>石を置けるのは、その場所を起点に少なくとも1方向で
 *       相手の石を挟んで自分の色に変えられる場合のみ</li>
 *   <li>石を置いたら、挟めるすべての方向の相手の石を反転させる</li>
 *   <li>次の手番のプレイヤーが置ける場所を持たない場合はパスする</li>
 *   <li>両者とも置ける場所がない場合はゲーム終了とし、石数で勝敗を決める</li>
 * </ul>
 */
public final class Board {

    /** 盤面の一辺のマス数。オセロは常に8x8。 */
    public static final int SIZE = 8;

    private final Disc[][] cells = new Disc[SIZE][SIZE];
    private Disc currentTurn;
    private boolean gameOver;
    private String statusMessage;

    /**
     * 初期配置(中央に黒白2枚ずつ)から始まる新しい対局を作成する。
     */
    public Board() {
        setUpInitialFourDiscs();
        currentTurn = Disc.BLACK;
        statusMessage = "黒の番です";
    }

    /**
     * 現在の手番のプレイヤーとして、指定したマスに石を置く。
     *
     * <p>これがこのクラスの中心となるメソッドで、
     * 「置けるか判定する→反転させる→手番を進める」という
     * 一連の流れをそのまま読める順序で呼び出している。
     * 個々のアルゴリズムの詳細(挟み判定や反転処理)は
     * private メソッドの中に隠蔽している。</p>
     *
     * @param position 石を置きたいマスの座標
     * @return 実際に石を置けた場合は true、置けなかった場合は false
     */
    public boolean placeDiscAt(Position position) {
        if (gameOver || !isLegalMove(position, currentTurn)) {
            return false;
        }
        flipCapturedDiscs(position, currentTurn);
        cells[position.row()][position.col()] = currentTurn;
        advanceToNextTurn();
        return true;
    }

    /** 指定したマスにある石を返す。 */
    public Disc discAt(Position position) {
        return cells[position.row()][position.col()];
    }

    /** 現在の手番の石の色を返す。 */
    public Disc getCurrentTurn() {
        return currentTurn;
    }

    /** 画面上部に表示すべき状態メッセージ(手番・パス・勝敗)を返す。 */
    public String getStatusMessage() {
        return statusMessage;
    }

    /** ゲームが終了している(両者ともに置ける場所がない)かどうかを返す。 */
    public boolean isGameOver() {
        return gameOver;
    }

    private void setUpInitialFourDiscs() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col] = Disc.EMPTY;
            }
        }
        cells[3][3] = Disc.WHITE;
        cells[3][4] = Disc.BLACK;
        cells[4][3] = Disc.BLACK;
        cells[4][4] = Disc.WHITE;
    }

    /**
     * 指定したプレイヤーが指定したマスに石を置けるかどうかを判定する。
     *
     * <p>空きマスであり、かつ8方向のうち少なくとも1方向で
     * 相手の石を挟める場合にのみ合法手となる。</p>
     */
    private boolean isLegalMove(Position position, Disc player) {
        if (discAt(position) != Disc.EMPTY) {
            return false;
        }
        for (Direction direction : Direction.values()) {
            if (canCaptureInDirection(position, player, direction)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 指定した方向に相手の石が1枚以上連続し、その先に自分の石があるかどうかを調べる。
     * これが成り立つ場合、その方向の相手の石をすべて反転できる。
     */
    private boolean canCaptureInDirection(Position position, Disc player, Direction direction) {
        Position current = position.moved(direction);
        boolean sawOpponentDisc = false;
        while (isInsideBoard(current) && discAt(current) == player.opponent()) {
            sawOpponentDisc = true;
            current = current.moved(direction);
        }
        return sawOpponentDisc && isInsideBoard(current) && discAt(current) == player;
    }

    private void flipCapturedDiscs(Position position, Disc player) {
        for (Direction direction : Direction.values()) {
            if (canCaptureInDirection(position, player, direction)) {
                flipDiscsInDirection(position, player, direction);
            }
        }
    }

    private void flipDiscsInDirection(Position position, Disc player, Direction direction) {
        Position current = position.moved(direction);
        while (discAt(current) == player.opponent()) {
            cells[current.row()][current.col()] = player;
            current = current.moved(direction);
        }
    }

    /**
     * 手番を次のプレイヤーに進める。
     *
     * <p>次のプレイヤーが置ける場所を持たない場合はパスとして
     * 現在のプレイヤーの手番を継続させ、双方とも置ける場所がなければ
     * ゲームを終了させて勝敗メッセージを組み立てる。</p>
     */
    private void advanceToNextTurn() {
        Disc next = currentTurn.opponent();
        if (hasAnyLegalMove(next)) {
            currentTurn = next;
            statusMessage = discName(next) + "の番です";
        } else if (hasAnyLegalMove(currentTurn)) {
            statusMessage = discName(next) + "は置ける場所がないためパスします";
        } else {
            gameOver = true;
            statusMessage = buildResultMessage();
        }
    }

    private boolean hasAnyLegalMove(Disc player) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (isLegalMove(new Position(row, col), player)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String buildResultMessage() {
        int blackCount = countDiscs(Disc.BLACK);
        int whiteCount = countDiscs(Disc.WHITE);
        String scoreText = String.format("(黒 %d - 白 %d)", blackCount, whiteCount);
        if (blackCount > whiteCount) {
            return "黒の勝ちです " + scoreText;
        } else if (whiteCount > blackCount) {
            return "白の勝ちです " + scoreText;
        } else {
            return "引き分けです " + scoreText;
        }
    }

    private int countDiscs(Disc target) {
        int count = 0;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (cells[row][col] == target) {
                    count++;
                }
            }
        }
        return count;
    }

    private boolean isInsideBoard(Position position) {
        return position.row() >= 0 && position.row() < SIZE
                && position.col() >= 0 && position.col() < SIZE;
    }

    private String discName(Disc disc) {
        return disc == Disc.BLACK ? "黒" : "白";
    }
}
