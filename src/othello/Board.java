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
// "public" は他のクラスから使えることを、"final" はこのクラスを継承(拡張)
// できないことを表す。盤面ルールをこのクラスの中だけで完結させる設計なので、
// 他のクラスに勝手に拡張されないよう final にしている。
public final class Board {

    /** 盤面の一辺のマス数。オセロは常に8x8。 */
    // "static" は「インスタンス(new して作った1つ1つのBoard)ごとではなく、
    // Boardクラス全体で1つだけ存在する値」であることを意味する。
    // "final" があるので一度8と決めたら変更できない、つまり定数になる。
    public static final int SIZE = 8;

    // 盤面の中身そのもの。Disc[][] は「Discの配列の配列」、つまり2次元配列。
    // cells[行][列] という形でアクセスすると、そのマスに何の石があるかがわかる。
    // "private" は Board クラスの外から直接触れないようにするための修飾子で、
    // 石の状態を勝手に書き換えられないようにしている(必ず placeDiscAt 経由にする)。
    private final Disc[][] cells = new Disc[SIZE][SIZE];
    // 今どちらの手番か(BLACK か WHITE)を覚えておく変数。
    private Disc currentTurn;
    // ゲームが終了したかどうかを表すフラグ。trueになったら以降石は置けない。
    private boolean gameOver;
    // 画面の上部に表示するための、現在の状況を説明する文字列
    // (例: "黒の番です" "白はパスします" "黒の勝ちです")。
    private String statusMessage;

    /**
     * 初期配置(中央に黒白2枚ずつ)から始まる新しい対局を作成する。
     */
    // コンストラクタ: "new Board()" と書いたときに1度だけ呼ばれる初期化処理。
    public Board() {
        // 盤面の中央4マスに黒白2枚ずつを配置する(下で定義しているメソッドに処理を任せる)。
        setUpInitialFourDiscs();
        // オセロは黒石のプレイヤーから始まるのがルールなので、最初の手番を黒にする。
        currentTurn = Disc.BLACK;
        // 起動直後の画面表示用メッセージをセットしておく。
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
        // まず「置けない状況」を先にはじく(ガード節と呼ばれる書き方)。
        // ゲームがすでに終わっている、または合法手ではない場合はすぐに false を返して終了。
        // "||" は「または」を意味する演算子で、左が true ならすぐ右は評価されない。
        // "!" は否定(true/falseをひっくり返す)を意味する。
        if (gameOver || !isLegalMove(position, currentTurn)) {
            return false;
        }
        // ここまで来たら合法手が確定しているので、挟んだ相手の石をすべて反転させる。
        flipCapturedDiscs(position, currentTurn);
        // 実際にそのマスへ、今の手番の色の石を置く(配列の該当要素を書き換える)。
        cells[position.row()][position.col()] = currentTurn;
        // 手番を次のプレイヤーに進める(パスや終了判定もこの中で行われる)。
        advanceToNextTurn();
        // 石を置けたことを呼び出し元(コマンドなど)に伝える。
        return true;
    }

    /** 指定したマスにある石を返す。 */
    // 盤面の状態を外部(View側の描画処理など)から読み取るためのメソッド。
    // これがあることで、外側のクラスは cells 配列に直接触れずに済む。
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

    // ここから下は private メソッド、つまり Board クラスの内部でしか
    // 呼び出されない「アルゴリズムの詳細」をまとめた部分。
    // 外から見えないようにすることで、上のpublicメソッドだけを見れば
    // 「何ができるか」が分かるようにしている。

    // 盤面をすべて空きマスにしたうえで、中央4マスにオセロの初期配置を作る。
    private void setUpInitialFourDiscs() {
        // 二重for文で盤面全体(8x8=64マス)を1マスずつ順番に処理する。
        // 外側のforが行(row)、内側のforが列(col)を0から7まで動かす。
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                // まずは全マスを「空き」状態にしておく。
                cells[row][col] = Disc.EMPTY;
            }
        }
        // 盤面の中央にある4マスだけ、オセロのルール通りに石を置く。
        // 行・列とも0始まりなので、cells[3][3]は「上から4番目・左から4番目」のマス。
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
        // すでに何か石が置かれているマスには置けないので、その場合は即座にfalse。
        if (discAt(position) != Disc.EMPTY) {
            return false;
        }
        // Direction.values() は8方向すべて(NORTH, SOUTH, ...)が入った配列を返す。
        // これを1つずつ順番に direction という変数に取り出しながらループする(拡張for文)。
        for (Direction direction : Direction.values()) {
            // どれか1方向でも相手を挟めるなら、その時点で「合法手である」と確定してよい。
            if (canCaptureInDirection(position, player, direction)) {
                return true;
            }
        }
        // 8方向すべて試しても挟めなかったので、この場所には置けない。
        return false;
    }

    /**
     * 指定した方向に相手の石が1枚以上連続し、その先に自分の石があるかどうかを調べる。
     * これが成り立つ場合、その方向の相手の石をすべて反転できる。
     */
    private boolean canCaptureInDirection(Position position, Disc player, Direction direction) {
        // まず1マス分だけ、調べたい方向に進んだ座標を求める。
        Position current = position.moved(direction);
        // 「途中で相手の石を1枚でも見たか」を記録するためのフラグ。最初はまだ見ていないのでfalse。
        boolean sawOpponentDisc = false;
        // 条件: 盤面の中に収まっていて、かつそのマスが「相手の色」である間はループを続ける。
        // つまり、相手の石が連続している限りどんどん先に進んでいく。
        while (isInsideBoard(current) && discAt(current) == player.opponent()) {
            sawOpponentDisc = true; // 相手の石を1枚見たことを記録
            current = current.moved(direction); // さらに1マス先へ進める
        }
        // ループを抜けた理由は次の3パターンのどれか:
        //   1. 盤面の外に出た → 挟めない
        //   2. 空きマスに着いた → 挟めない
        //   3. 自分の色のマスに着いた → 間に相手の石が1枚以上あれば挟める!
        // この3つを1つの式で表現している。
        return sawOpponentDisc && isInsideBoard(current) && discAt(current) == player;
    }

    // 石を置いたマスを起点に、実際に挟めるすべての方向について反転処理を行う。
    private void flipCapturedDiscs(Position position, Disc player) {
        for (Direction direction : Direction.values()) {
            // その方向で本当に挟めるかどうかをもう一度確認してから反転する。
            if (canCaptureInDirection(position, player, direction)) {
                flipDiscsInDirection(position, player, direction);
            }
        }
    }

    // 指定した1方向にある相手の石を、自分の色にどんどん置き換えていく。
    private void flipDiscsInDirection(Position position, Disc player, Direction direction) {
        Position current = position.moved(direction);
        // 相手の石である間は反転を続ける(この時点で挟めることは確認済みなので、
        // 盤面の外に出る心配はない=isInsideBoardのチェックが不要)。
        while (discAt(current) == player.opponent()) {
            cells[current.row()][current.col()] = player; // 石の色を自分の色に書き換える
            current = current.moved(direction); // 次のマスへ進む
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
        // 今の手番の相手(次に打つはずだったプレイヤー)を求める。
        Disc next = currentTurn.opponent();
        // パターン1: 次のプレイヤーが置ける場所を持っている → 普通に手番交代。
        if (hasAnyLegalMove(next)) {
            currentTurn = next;
            statusMessage = discName(next) + "の番です";
        // パターン2: 次のプレイヤーは置けないが、今のプレイヤーはまだ置ける
        //           → 次のプレイヤーはパスし、今のプレイヤーの手番が続く。
        } else if (hasAnyLegalMove(currentTurn)) {
            statusMessage = discName(next) + "は置ける場所がないためパスします";
        // パターン3: どちらも置ける場所がない → ゲーム終了、勝敗を決める。
        } else {
            gameOver = true;
            statusMessage = buildResultMessage();
        }
    }

    // 指定したプレイヤーが、盤面のどこかに1箇所でも置ける場所を持っているかを調べる。
    private boolean hasAnyLegalMove(Disc player) {
        // 盤面全マスを総当たりでチェックする(8x8=64マス、非常に軽い処理)。
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                // 1箇所でも合法手が見つかったら、それ以上調べずにtrueを返してよい。
                if (isLegalMove(new Position(row, col), player)) {
                    return true;
                }
            }
        }
        // 全マスを調べても見つからなかった。
        return false;
    }

    // ゲーム終了時に表示する「勝敗メッセージ」を組み立てる。
    private String buildResultMessage() {
        int blackCount = countDiscs(Disc.BLACK); // 黒石の数を数える
        int whiteCount = countDiscs(Disc.WHITE); // 白石の数を数える
        // String.format は "%d" の部分に数値を埋め込んだ文字列を作る仕組み。
        // 例: blackCount=20, whiteCount=15 なら "(黒 20 - 白 15)" になる。
        String scoreText = String.format("(黒 %d - 白 %d)", blackCount, whiteCount);
        if (blackCount > whiteCount) {
            return "黒の勝ちです " + scoreText;
        } else if (whiteCount > blackCount) {
            return "白の勝ちです " + scoreText;
        } else {
            // 石数が同じ場合は引き分け。
            return "引き分けです " + scoreText;
        }
    }

    // 盤面全体から、指定した色(target)の石が何個あるかを数える。
    private int countDiscs(Disc target) {
        int count = 0; // 数を数えるためのカウンター変数、最初は0個
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                // そのマスの石がtargetと同じ色なら、カウンターを1増やす。
                if (cells[row][col] == target) {
                    count++;
                }
            }
        }
        return count;
    }

    // 指定した座標が盤面(0〜7行・0〜7列)の内側に収まっているかどうかを判定する。
    // 8方向への探索は盤面の端で外にはみ出す可能性があるため、あちこちで使われる。
    private boolean isInsideBoard(Position position) {
        return position.row() >= 0 && position.row() < SIZE
                && position.col() >= 0 && position.col() < SIZE;
    }

    // Disc.BLACK / Disc.WHITE を、画面表示用の日本語("黒"/"白")に変換する。
    // 三項演算子 "条件 ? Aの場合の値 : Bの場合の値" を使った短い書き方。
    private String discName(Disc disc) {
        return disc == Disc.BLACK ? "黒" : "白";
    }
}
