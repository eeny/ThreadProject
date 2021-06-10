import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class BingGoGame extends JFrame implements ActionListener, Runnable {
    JButton btnRank, btnPlay, btnReplay, btnExit, item1, item2;// ,b1,b2,b3,b4,b5,b6,b7,b8,b9,
    JButton btns[];
    JProgressBar bar;
    int speed = 500;
    int levelspeed;
    int value = 100;
    int lastTime;
    int levelChoose;
    boolean isStop = false;
    boolean isbar = false;
    boolean isStart = false;
    boolean isCard = false;
    boolean isIce = false;
    boolean isTurtle = false;
    int minus = 0;

    JLabel title, score, score2;
    RankDialog rd;
    String userName;

    String[] imgNames = { "1.png", "2.png", "3.png", "4.png", "5.png", "6.png", "7.png", "8.png", "1.png", "2.png",
            "3.png", "4.png", "5.png", "6.png", "7.png", "8.png" };
    List<String> imgList;// 이미지 파일명 컬렉션화
    ImageIcon changeIcon, changeIcon2, changeIcon3;
    ImageIcon[] changeIcons = new ImageIcon[16];

    // 카드 맞추기용 변수 (전부 기본값 0 들어있음)
    int tryCnt;// 시도 횟수
    int successCnt;// 카드 맞추기 성공한 횟수 (최대 8)
    int openCnt;// 내가 뒤집은 카드 개수 : 0, 1, 2
    int card1;// 처음 뒤집은 카드의 인덱스 : 0 ~ 15
    int card2;// 두번째 뒤집은 카드의 인덱스 : 0 ~ 15
    int comboCnt;// 카드 맞춘 횟수 (맞추면 최대 8, 틀리면 무조건 0)

    public BingGoGame() {
        setDefaultCloseOperation(3);
        setSize(500, 540);
        setTitle("카드 맞추기 게임");
        setLocation(700, 300);

        ImageIcon iconBack = new ImageIcon("img/back.jpg");
        JPanel pnl = new JPanel();
        // 전체 패널에 배경 넣기(레이아웃은 null)
        pnl = new JPanel(null) {
            public void paintComponent(Graphics g) {
                g.drawImage(iconBack.getImage(), 0, 0, null);
                setOpaque(false);
                super.paintComponent(g);
            }
        };

        // 이미지 파일 버튼 크기에 맞추기
        ImageIcon icon = new ImageIcon("img/q.png");// 물음표 이미지
        Image img = icon.getImage();
        Image ChangeImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        changeIcon = new ImageIcon(ChangeImg);

        ImageIcon icon2 = new ImageIcon("img/ice.png");// 얼음 이미지
        Image img2 = icon2.getImage();
        Image ChangeImg2 = img2.getScaledInstance(40, 30, Image.SCALE_SMOOTH);
        changeIcon2 = new ImageIcon(ChangeImg2);

        ImageIcon icon3 = new ImageIcon("img/turtle.png");// 거북이 이미지
        Image img3 = icon3.getImage();
        Image ChangeImg3 = img3.getScaledInstance(45, 35, Image.SCALE_SMOOTH);
        changeIcon3 = new ImageIcon(ChangeImg3);

        // 카드 섞기
        shuffle();

        JPanel p1 = new JPanel();
        p1.setOpaque(false);
        JPanel p2 = new JPanel();
        p2.setOpaque(false);
        JPanel p3 = new JPanel();
        p3.setOpaque(false);

        // 플레이 버튼
        p2.setLayout(new GridLayout(0, 4, 15, 15));
        btnPlay = new JButton("PLAY");
        btnReplay = new JButton("REPLAY");
        btnReplay.setEnabled(false);
        btnPlay.setBackground(Color.WHITE);
        btnReplay.setBackground(Color.WHITE);
        btnPlay.addActionListener(this);
        btnReplay.addActionListener(this);

        // Find the same picture

        title = new JLabel("FIND THE SAME PICTURE! :)");
        Font font = new Font("맑은 고딕", Font.BOLD, 20);
        title.setFont(font);

        // 그림맞추기 게임

        // 시간 타이머 (프로그레스 바)
        JLabel timer = new JLabel("TIMER");
        bar = new JProgressBar();
        bar.setValue(100);
        bar.setPreferredSize(new Dimension(400, 20));

        // score
        score = new JLabel("SCORE:");
        score2 = new JLabel("");

        // 그림 맞추기의 그림 버튼들...
        btns = new JButton[16];
        for (int i = 0; i < 16; i++) {
            btns[i] = new JButton(changeIcon);// 물음표 그림
            // btns[i] = new JButton(changeIcons[i]);// 동물 그림
            btns[i].setBackground(Color.white);
            btns[i].setBorderPainted(true);
            btns[i].addActionListener(this);
            btns[i].setEnabled(false);
            p2.add(btns[i]);
        }

        // 랭크 버튼
        btnRank = new JButton("RANK");
        btnRank.addActionListener(this);
        btnRank.setBackground(Color.WHITE);
        Font font2 = new Font("맑은 고딕", Font.BOLD, 11);
        btnRank.setFont(font2);

        // 즉시 종료 버튼
        btnExit = new JButton("EXIT");
        btnExit.addActionListener(this);
        btnExit.setBackground(Color.WHITE);

        // 아이템 버튼
        item1 = new JButton(changeIcon2);
        item1.setEnabled(false);
        item1.setBackground(Color.WHITE);
        item2 = new JButton(changeIcon3);
        item2.setEnabled(false);
        item2.setBackground(Color.WHITE);
        item1.addActionListener(this);
        item2.addActionListener(this);

        // 버튼 천천히 나타나게하기
        // 타이머 구현

        p1.add(btnPlay);
        p1.add(btnReplay);
        p3.add(bar);

        p1.setBounds(301, 20, 160, 33); // play 버튼
        p2.setBounds(95, 110, 350, 350); // 그림들
        p3.setBounds(55, 55, 400, 50); // 프로그레스 바
        timer.setBounds(10, 60, 40, 20); // 타이머 레이블
        item1.setBounds(20, 110, 50, 40); // 아이템 버튼1
        item2.setBounds(20, 160, 50, 40); // 아이템 버튼2
        title.setBounds(10, 10, 350, 50);
        score.setBounds(320, 70, 60, 50);
        score2.setBounds(370, 70, 60, 50);
        btnExit.setBounds(379, 465, 65, 30);
        btnRank.setBounds(17, 426, 65, 33);

        pnl.add(p1);
        pnl.add(p3);
        pnl.add(p2);
        pnl.add(timer);
        pnl.add(item1);
        pnl.add(item2);
        pnl.add(title);
        pnl.add(score);
        pnl.add(score2);
        pnl.add(btnExit);
        pnl.add(btnRank);

        this.add(pnl);
        setVisible(true);
    }

    void shuffle() {
        imgList = Arrays.asList(imgNames);
        Collections.shuffle(imgList);
        for (int i = 0; i < imgNames.length; i++) {// 동물 이미지
            ImageIcon icon4 = new ImageIcon("img/" + imgList.get(i));
            Image img4 = icon4.getImage();
            Image ChangeImg4 = img4.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            changeIcons[i] = new ImageIcon(ChangeImg4);
        }
    }

    void saveFile() {
        File f = new File("rank.txt");
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            fw = new FileWriter(f, true);
            pw = new PrintWriter(fw);
            pw.println(userName + "/" + lastTime);
        } catch (IOException e1) {

            e1.printStackTrace();
        } finally {
            pw.close();
        }
    }

    public static void main(String[] args) {
        new BingGoGame();
    }

    // 스레드 실행
    @Override
    public void run() {
        // 시작하면 잠깐 카드 보여주고 다시 가리는 스레드
        if (isStart) {
            for (int i = 0; i < 16; i++) {
                btns[i].setIcon(changeIcons[i]);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < 16; i++) {
                btns[i].setIcon(changeIcon);
            }

            isStart = false;
        }

        // 프로그래스 바 관련 스레드(아이템 포함)
        int x = 0;
        int speed2 = 1500;
        if (isbar) {

            for (int i = 100; i >= 0; i--) {
                if (isStop == false) {
                    try {
                        // 얼음
                        if (isIce == true) {
                            //minus = 0;
                            btnReplay.setEnabled(false);
                            bar.setForeground(Color.BLUE);
                            Thread.sleep(3000);
                            isIce = false;
                        } else if (isIce == false) {
                            //minus = 0;
                            btnReplay.setEnabled(true);
                            bar.setForeground(Color.lightGray);
                            Thread.sleep(speed);

                        }

                        // 거북이
                        if (isTurtle) {
                            //minus = 0;
                            while (true) {
                                btnReplay.setEnabled(false);
                                bar.setForeground(Color.GREEN);
                                Thread.sleep(1000);
                                x = x + speed2;// 1500 3000 4500
                                i--;
                                bar.setValue(i - minus);
                                if (x >= 5000) {
                                    Thread.sleep(speed);
                                    isTurtle = false;
                                    break;
                                }
                            }
                        } else {
                            btnReplay.setEnabled(true);
                            bar.setForeground(Color.lightGray);
                        }

                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }

                    bar.setValue(i - minus);

                    if (bar.getValue()<=0) {
                        JOptionPane.showMessageDialog(this, "GAME OVER!");
                        for (int j = 0; j < 16; j++) {
                            btns[j].setEnabled(false);
                        }
                        break;
                    }
                }
            }
        }

        // 틀린 카드 2장 물음표로 뒤집는 스레드
        if (isCard) {
            try {// 틀린 카드 뒤집는 부분
                Thread.sleep(levelspeed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            openCnt = 0;// 뒤집은 카드 횟수 초기화
            // 뒤집었던 카드의 이미지를 물음표로 바꾸기
            btns[card1].setIcon(changeIcon);
            btns[card2].setIcon(changeIcon);
            isCard = false;

        }
    }

    // 내가 누른 버튼의 인덱스를 출력하는 메서드
    int getBtnIndex(JButton btn) {
        int idx = 0;// 인덱스를 담을 변수
        for (int i = 0; i < 16; i++) {// 버튼 총 16개
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

    void levelSelect() {
        String[] level = { "쉬움", "보통", "어려움" };
        levelChoose = JOptionPane.showOptionDialog(this, "난이도를 선택해 주세요!", "난이도 선택창", JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, level, null);
        // 쉬움 선택시
        if (levelChoose == 0) {
            speed = 400;
            levelspeed = 1000;
            // 보통 선택시
        } else if (levelChoose == 1) {
            speed = 250;
            levelspeed = 300;
            // 어려움 선택시
        } else if (levelChoose == 2) {
            speed = 150;
            levelspeed = 80;
        }
        (new Thread(this)).start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int userChoose = 0;
        if (e.getSource() == btnPlay) {// play 버튼
            btnPlay.setEnabled(false);

            // 버튼 활성화
            btnReplay.setEnabled(true);
            item1.setEnabled(true);
            item2.setEnabled(true);
            for (int i = 0; i < btns.length; i++) {
                btns[i].setEnabled(true);
            }

            JOptionPane.showMessageDialog(this, "게임을 시작합니다!");
            isStart = true;

            // 프로그래스바 실행은 난이도 따라 다르게
            isStop = false;
            isbar = true;

            levelSelect();

        } else if (e.getSource() == btnReplay) {// replay 버튼
            isStop = true;// 스레드 중지
            item1.setEnabled(true);
            item2.setEnabled(true);
            btnPlay.setEnabled(false);
            successCnt = 0;// 성공횟수 초기화
            minus = 0;

            // 버튼 활성화
            for (int i = 0; i < btns.length; i++) {
                btns[i].setEnabled(true);
                btns[i].setIcon(changeIcon);// 물음표 그림
            }

            userChoose = JOptionPane.showConfirmDialog(this, "다시 플레이 하시겠습니까?", "Confirm", JOptionPane.YES_NO_OPTION);

            if (userChoose == JOptionPane.NO_OPTION) {
                System.exit(0);
            } else if (userChoose == JOptionPane.YES_OPTION) {
                // 다시 플레이 한다고 예를 눌렀을때 게임이 시작됩니다! 5 4 3 2 1 이렇게 카운트 되게 하고 싶음
                bar.setValue(100);
                shuffle();
                (new Thread(this)).start();
            }

            levelSelect();
            isStart = true;
            isStop = false;
            isbar = true;

        } else if (e.getSource() == item1) {// 얼음 아이템 버튼
            isIce = true;
            item1.setEnabled(false);

        } else if (e.getSource() == item2) {// 거북이 아이템 버튼
            isTurtle = true;
            item2.setEnabled(false);

        } else if (e.getSource() == btnExit) {// 종료 버튼
            System.exit(0);

        } else if (e.getSource() == btnRank) {// 랭크 버튼
            rd = new RankDialog(this, "Ranking");

        } else {// 그림 카드 버튼

            if (openCnt == 2) {// 카드를 2개 이상 뒤집지 못하게 막기
                return;
            }

            // 내가 클릭한 버튼의 인덱스 가져오기
            JButton selBtn = (JButton) e.getSource();
            // 가져온 인덱스와 같은 인덱스의 이미지 붙이기
            int idx = getBtnIndex(selBtn);
            selBtn.setIcon(changeIcons[idx]);

            openCnt++;// 버튼을 클릭하면 카운트 증가

            if (openCnt == 1) {// 카드 한 개만 뒤집은 상태
                card1 = idx;// 가져온 인덱스를 첫번째 카드의 값으로 넣기

            } else if (openCnt == 2) {// 카드 두 개 뒤집은 상태
                card2 = idx;// 가져온 인덱스를 두번째 카드의 값으로 넣기

                tryCnt++;// 카드 두 개 뒤집었으므로 시도 횟수 증가

                // 뒤집힌 카드 2개가 서로 같은지 판단하는 작업
                boolean isSame = checkCard(card1, card2);

                if (isSame) {// true일 때
                    openCnt = 0;// 뒤집은 횟수 초기화
                    successCnt++;// 성공한 횟수 증가
                    comboCnt++;// 콤보카운트 증가
                    System.out.println("combo?" + comboCnt);

                    if (comboCnt > 2) {
                        item1.setEnabled(true);
                        item2.setEnabled(true);
                        comboCnt = 0;
                    }

                    btns[card1].setEnabled(false);
                    btns[card2].setEnabled(false);

                    if (successCnt == 8) {// 16장의 카드를 모두 맞췄을 때
                        isStop = true;
                        item1.setEnabled(false);
                        item2.setEnabled(false);
                        btnPlay.setEnabled(false);

                        lastTime = bar.getValue();
                        for (int i = 0; i < btns.length; i++) {
                            btns[i].setEnabled(false);
                        }
                        score2.setText(lastTime + "");
                        JOptionPane.showMessageDialog(this, "축하합니다!");
                        userName = JOptionPane.showInputDialog(this, "아이디를 입력하세요!");

                        saveFile();
                    }

                } else {// false일 때
                    isbar = false;
                    isCard = true;
                    minus += 5;
                    bar.setForeground(Color.RED);
                    comboCnt = 0;// 콤보카운트 초기화
                    System.out.println("combo?" + comboCnt);
                    (new Thread(this)).start();// 물음표 버튼으로 다시 뒤집기
                }
            }
        }
    }
}