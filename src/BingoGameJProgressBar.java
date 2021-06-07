import java.awt.Dimension;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

public class BingoGameJProgressBar extends JProgressBar implements Runnable {
    BingoGame bg;
    int min = 0;// 진행 최소값
    int max = 30;// 진행 최대값 (30초)
    int value = 30;// 진행중인 값

    public BingoGameJProgressBar(BingoGame bg) {
        this.bg = bg;
        this.setMinimum(min);
        this.setMaximum(max);
        this.setStringPainted(true);
        this.setValue(value);
        this.setPreferredSize(new Dimension(300, 20));
    }

    @Override
    public void run() {
        for (int i = value; i >= 0; i--) {
            this.setValue(i);
            try {
                Thread.sleep(1000);// 1초마다 감소
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (i == 0) {// 진행값이 0이 되었을 때 시간 초과 알림창
                JOptionPane.showMessageDialog(this, "Time Over!");
            }
        }
    }
}
