import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BingoGame extends JFrame implements ActionListener, Runnable {
    // UI 세팅용 변수
    JPanel pnlNorth, pnlNorthSub, pnlCenter;
    JLabel lbl;
    JButton startBtn;
    BingoGameJProgressBar bar;
    JButton[] btns = new JButton[16];// 버튼 16개
    String[] imgNames = {"1.png", "2.png", "3.png", "4.png", "5.png", "6.png", "7.png", "8.png",
            "1.png", "2.png", "3.png", "4.png", "5.png", "6.png", "7.png", "8.png"};
    List<String> imgList = Arrays.asList(imgNames);// 이미지 파일 컬렉션화

    // 카드 맞추기용 변수 (전부 기본값 0 들어있음)
    int tryCnt;// 시도 횟수
    int successCnt;// 카드 맞추기 성공한 횟수 (최대 8)
    int openCnt;// 내가 뒤집은 카드 개수 : 0, 1, 2
    int card1;// 처음 뒤집은 카드의 인덱스 : 0 ~ 15
    int card2;// 두번째 뒤집은 카드의 인덱스 : 0 ~ 15

    public BingoGame() {
        this.setDefaultCloseOperation(3);
        this.setSize(500, 600);
        this.setTitle("빙고 게임");

        init();// UI 구성
        mixCard();// 카드 섞기

        this.setVisible(true);
    }

    // UI 구성하는 메서드
    void init() {
        // ===== North 패널 시작 =====
        pnlNorth = new JPanel(new GridLayout(0, 1));
        pnlNorth.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        // 맨 위에 있는 라벨
        lbl = new JLabel("같은 카드를 찾으세요! 시도한 횟수 : " + tryCnt);
        lbl.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        lbl.setHorizontalAlignment(JLabel.CENTER);
        // 시작 버튼과 타이머 프로그레스바(패널로 묶음)
        pnlNorthSub = new JPanel();
        startBtn = new JButton("시작");
        startBtn.addActionListener(this);
        bar = new BingoGameJProgressBar(this);
        pnlNorthSub.add(startBtn);
        pnlNorthSub.add(bar);

        pnlNorth.add(lbl);
        pnlNorth.add(pnlNorthSub);
        // ===== North 패널 끝 =====

        // ===== Center 패널 시작 =====
        pnlCenter = new JPanel(new GridLayout(0, 4, 5, 5));
        // 버튼 16개 붙이기
        for (int i = 0; i < 16; i++) {
            btns[i] = new JButton();
            btns[i].setPreferredSize(new Dimension(100, 100));
            btns[i].setContentAreaFilled(false);// 버튼 투명화 작업1
            btns[i].setFocusPainted(false);// 버튼 투명화 작업2
            btns[i].setIcon(changeImage("q.png"));
            btns[i].addActionListener(this);
            pnlCenter.add(btns[i]);
        }
        // ===== Center 패널 끝 =====

        this.add(pnlNorth, "North");
        this.add(pnlCenter, "Center");
    }

    // 이미지를 버튼 사이즈에 맞게 조절하는 메서드
    ImageIcon changeImage(String fileName) {
        ImageIcon icon = new ImageIcon("img/" + fileName);
        Image originImage = icon.getImage();
        Image changeImage = originImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        ImageIcon newIcon = new ImageIcon(changeImage);
        return newIcon;
    }

    // 이미지파일 리스트 셔플하는 메서드
    void mixCard() {
        imgList = Arrays.asList(imgNames);
        Collections.shuffle(imgList);
    }

    // 내가 누른 버튼의 인덱스를 출력하는 메서드
    int getBtnIndex(JButton btn) {
        int idx = 0;// 인덱스를 담을 변수
        for (int i = 0; i < btns.length; i++) {// 버튼 총 16개
            if (btns[i] == btn) {// 내가 누른 버튼과 붙여놓은 16개의 버튼들이 같은 객체(주소값)냐
                idx = i;
            }
        }
        return idx;
    }

    // 내가 뒤집은 카드가 서로 같은지 판단하는 메서드
    boolean checkCard(int card1Idx, int card2Idx) {
        if (card1Idx == card2Idx) {// 같은 버튼을 두 번 클릭한 경우
            return false;// false를 반환한다
        }

        // 첫번째 버튼과 두번째 버튼에 있는 이미지 파일의 이름(문자열)을 비교한다!
        if (imgList.get(card1Idx).equals(imgList.get(card2Idx))) {
            return true;// 두 개의 이미지 파일 이름이 동일하면 true 반환

        } else {
            return false;// 이름 다르면 false 반환
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource()==startBtn) {// 시작 버튼 눌렀을 때
            playSound("timer.wav");
            // 타이머 라벨 스레드 실행
            (new Thread(bar)).start();

        } else {// 카드(그림있는 버튼)를 눌렀을 때
            playSound("flip.wav");
            if (openCnt == 2) {// 카드를 2개 이상 뒤집지 못하게 막기
                return;
            }

            JButton selBtn = (JButton)e.getSource();
            // 내가 클릭한 버튼의 인덱스 가져오기
            int idx = getBtnIndex(selBtn);
            // 가져온 인덱스와 같은 인덱스의 이미지 붙이기
            selBtn.setIcon(changeImage(imgList.get(idx)));

            openCnt++;// 버튼을 클릭하면 카운트 증가

            if (openCnt == 1) {// 카드 한 개만 뒤집은 상태
                card1 = idx;// 가져온 인덱스를 첫번째 카드의 값으로 넣기

            } else if (openCnt == 2) {// 카드 두 개 뒤집은 상태
                card2 = idx;// 가져온 인덱스를 두번째 카드의 값으로 넣기

                tryCnt++;// 카드 두 개 뒤집었으므로 시도 횟수 증가
                lbl.setText("같은 카드를 찾으세요! 시도한 횟수 : " + tryCnt);// 라벨 내용 변경

                // 뒤집힌 카드 2개가 서로 같은지 판단하는 작업
                boolean isSame = checkCard(card1, card2);

                if (isSame) {// true일 때
                    playSound("success.wav");
                    openCnt = 0;// 뒤집은 횟수 초기화
                    successCnt++;// 성공한 횟수 증가

                    if (successCnt == 8) {// 16장의 카드를 모두 맞췄을 때
                        lbl.setText("게임 종료! 시도한 횟수 : " + tryCnt);
                    }

                } else {// false일 때
                    playSound("fail.wav");
                    new Thread(this).start();// 물음표 버튼으로 다시 뒤집기
                }
            }
        }
    }

    @Override
    public void run() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        openCnt = 0;// 뒤집은 카드 횟수 초기화
        // 뒤집었던 카드의 이미지를 물음표로 바꾸기
        btns[card1].setIcon(changeImage("q.png"));
        btns[card2].setIcon(changeImage("q.png"));
    }

    public static void main(String[] args) {
        new BingoGame();
    }

    // 효과음 재생하는 메서드(없어도 됨)
    public void playSound(String fileName)	{
        File file = new File("./wav/" + fileName);
        if (file.exists()) {// 음악 파일이 존재한다면

            try {
                AudioInputStream stream = AudioSystem.getAudioInputStream(file);
                Clip clip = AudioSystem.getClip();
                clip.open(stream);
                clip.start();

            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }

        }
    }
}
