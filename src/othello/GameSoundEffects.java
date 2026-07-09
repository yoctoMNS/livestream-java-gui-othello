package othello;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

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

    private static final float SAMPLE_RATE = 44_100f;

    /** カーソル移動時の短く高い音を鳴らす。 */
    public void playCursorMoveSound() {
        playTone(880.0, 40);
    }

    /** 石を配置できたときの少し長く低い音を鳴らす。 */
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
        Thread soundThread = new Thread(() -> {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
            byte[] samples = generateSineWaveSamples(frequencyHz, durationMs, format);
            try (SourceDataLine line = AudioSystem.getSourceDataLine(format)) {
                line.open(format);
                line.start();
                line.write(samples, 0, samples.length);
                line.drain();
            } catch (LineUnavailableException e) {
                // 音声デバイスが使えない環境では効果音を諦めてゲーム自体は続行する。
            }
        });
        soundThread.setDaemon(true);
        soundThread.start();
    }

    private byte[] generateSineWaveSamples(double frequencyHz, int durationMs, AudioFormat format) {
        int sampleCount = (int) (format.getSampleRate() * durationMs / 1000.0);
        byte[] samples = new byte[sampleCount * 2];
        for (int i = 0; i < sampleCount; i++) {
            double angle = 2 * Math.PI * i * frequencyHz / format.getSampleRate();
            short value = (short) (Math.sin(angle) * Short.MAX_VALUE * 0.6);
            samples[i * 2] = (byte) (value & 0xFF);
            samples[i * 2 + 1] = (byte) ((value >> 8) & 0xFF);
        }
        return samples;
    }
}
