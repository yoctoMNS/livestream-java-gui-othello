package othello;

// javax.sound.sampled は、Java標準ライブラリに含まれる「生の音声データを扱う」ための機能群。
// mp3やwavファイルの再生ライブラリを別途用意しなくても、この標準APIだけで音を鳴らせる。
import javax.sound.sampled.AudioFormat;            // 音声データの形式(サンプリングレート・ビット数など)を表す
import javax.sound.sampled.AudioSystem;            // 音声再生用の出力先(ライン)を取得するための入り口
import javax.sound.sampled.LineUnavailableException; // 音声デバイスが使えないときに投げられる例外
import javax.sound.sampled.SourceDataLine;         // 実際に音声データを流し込んで再生する出力先

/**
 * カーソル移動時と石の配置時に鳴らす効果音を担当するクラス。
 *
 * <p>効果音ファイル(WAV等)は一切使わず、{@code javax.sound.sampled} だけを
 * 使ってその場で短いサイン波(単純な電子音)を生成し再生している。
 * こうすることで音声アセットを用意する手間をなくし、Java標準APIのみで
 * 完結させている。移動音より配置音のほうが低く長い音になるように
 * 周波数と長さを変えて、操作の違いが耳でも分かるようにしている。</p>
 */
public final class GameSoundEffects {

    // サンプリングレート: 1秒間に音声データを何回記録するかを表す値。
    // 44100は音楽CDと同じ、人間の耳には十分きれいに聞こえる標準的な値。
    // "44_100f" の "_" は数字の桁を読みやすくするための区切り文字(アンダースコア)で、
    // 実際の値には影響しない(44100と同じ)。末尾の "f" は float型であることを示す。
    private static final float SAMPLE_RATE = 44_100f;

    /** カーソル移動時の短く高い音を鳴らす。 */
    // 周波数880Hz(高めの音)を40ミリ秒(とても短い時間)だけ鳴らす。
    public void playCursorMoveSound() {
        playTone(880.0, 40);
    }

    /** 石を配置できたときの少し長く低い音を鳴らす。 */
    // 周波数440Hz(移動音より低い音)を120ミリ秒(移動音より長い時間)鳴らす。
    public void playDiscPlacedSound() {
        playTone(440.0, 120);
    }

    /**
     * 指定した周波数・長さのサイン波を生成し、別スレッドで再生する。
     *
     * <p>Swing のイベント処理スレッド(EDT)上で音声再生をブロッキングで
     * 行うと画面がカクつくため、専用のデーモンスレッドで再生している。</p>
     */
    private void playTone(double frequencyHz, int durationMs) {
        // Thread(スレッド)は「プログラムの中で並行して動く、もう1つの処理の流れ」。
        // 音の再生には時間がかかるため、画面描画を止めないよう別スレッドで行う。
        // "() -> { ... }" の部分はラムダ式と呼ばれる書き方で、
        // 「このスレッドが開始したときに実行する処理」をその場で定義している。
        Thread soundThread = new Thread(() -> {
            // 音声データの形式を決める: サンプリングレート、16ビット、1チャンネル(モノラル)、
            // signed(符号あり)、false(リトルエンディアン=バイトの並び順)。
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
            // 指定した周波数・長さのサイン波の生データ(バイト列)を作る。
            byte[] samples = generateSineWaveSamples(frequencyHz, durationMs, format);
            // "try (...) { ... }" は try-with-resources という書き方。
            // カッコ内で用意したもの(ここではSourceDataLine)は、
            // このブロックを抜けるときに自動的にclose()(後片付け)される。
            try (SourceDataLine line = AudioSystem.getSourceDataLine(format)) {
                line.open(format);  // 指定した形式で出力先を開く準備をする
                line.start();       // 音声の再生を開始できる状態にする
                line.write(samples, 0, samples.length); // 作った音声データを流し込んで再生させる
                line.drain();       // 書き込んだデータをすべて再生し終わるまで待つ
            } catch (LineUnavailableException e) {
                // 音声デバイスが使えない環境では効果音を諦めてゲーム自体は続行する。
            }
        });
        // デーモンスレッドに設定しておくと、アプリ本体(ウィンドウ)を閉じたときに
        // このスレッドが動作中でも一緒にプログラム全体を終了できる。
        soundThread.setDaemon(true);
        // 上で定義した処理を、実際に別スレッドとして動かし始める。
        soundThread.start();
    }

    // 指定した周波数・長さのサイン波(電子音のもとになる波形)を、
    // 音声データとして扱えるバイト列に変換して作るメソッド。
    private byte[] generateSineWaveSamples(double frequencyHz, int durationMs, AudioFormat format) {
        // 「1秒あたりのサンプル数 × 秒に直した再生時間」で、必要なサンプルの個数を求める。
        int sampleCount = (int) (format.getSampleRate() * durationMs / 1000.0);
        // 16ビット(2バイト)で1サンプルを表すため、必要なバイト数はサンプル数の2倍になる。
        byte[] samples = new byte[sampleCount * 2];
        // サンプルを1つずつ計算していくループ。
        for (int i = 0; i < sampleCount; i++) {
            // サイン波(sin)の角度を計算する。iが増えるごとに波が少しずつ進む。
            double angle = 2 * Math.PI * i * frequencyHz / format.getSampleRate();
            // Math.sin(angle) は -1.0〜1.0 の範囲の値を返すので、
            // それを16ビットで表現できる最大値(Short.MAX_VALUE)に掛け合わせて音量を決める。
            // "* 0.6" は音が大きくなりすぎないように少し控えめにするための係数。
            short value = (short) (Math.sin(angle) * Short.MAX_VALUE * 0.6);
            // short型(2バイト)の値を、下位バイトと上位バイトに分けて配列に書き込む。
            // これはリトルエンディアン形式(下位バイトを先に置く)でのバイト列の作り方。
            samples[i * 2] = (byte) (value & 0xFF);         // 下位8ビットを取り出す
            samples[i * 2 + 1] = (byte) ((value >> 8) & 0xFF); // 上位8ビットを取り出す
        }
        return samples;
    }
}
