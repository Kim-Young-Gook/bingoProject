package bingo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class BinGo extends JFrame implements ActionListener {

	private JPanel systemPan, mainPan, bingoPan, chatPan, statusPan, currentNo, currentPlayer, oppPlayerPan, oppPan1,
			oppPan2, oppPan3, oppPan4;
	private JTextArea chatArea, numLog, turnLog;
	private TextField chatField;
	private JButton exitBtn, bingoBtn;
	private JButton[] playerBtn = new JButton[25];
	private JButton[] opp1Btn = new JButton[25];
	private JButton[] opp2Btn = new JButton[25];
	private JButton[] opp3Btn = new JButton[25];
	private JButton[] opp4Btn = new JButton[25];
	private GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	private ImageIcon playBingo;
	private int width = gd.getDisplayMode().getWidth();
	private int height = gd.getDisplayMode().getHeight();
	private int m[] = new int[25];
	private String k[] = new String[25];
	private Font font = new Font("나눔고딕", Font.BOLD, 50);
	private Font bingoFont = new Font("나눔고딕", Font.BOLD, 80);
	// -------------------------------------------------------------------------레이아웃-------------------------------------------------------------------------
	private int cnt = 0;
	private Vector<Integer> v = new Vector<Integer>();
	private Color selected = Color.BLACK;
	private Vector<Integer> temp1, temp2, temp3, temp4, temp5, temp6, temp7, temp8, temp9, temp10, temp11, temp12;
	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	private String playerLabel;
	private int amIWin = 0;
	private int resetCount = 0;
	private String nextPlayer;
	// -------------------------------------------------------------------------게임진행관련-------------------------------------------------------------------------
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private static final int PORT = 5000;
	private String id, ip;
	private int winCnt = 0;
	// -------------------------------------------------------------------------네트워크설정-------------------------------------------------------------------------
	private String waiting = "Unknown Player";
	private TitledBorder[] t = new TitledBorder[4];
	private Vector<JButton[]> oppBtn = new Vector<JButton[]>();
	private URL banner = getClass().getClassLoader().getResource("banner.jpg");
	// -------------------------------------------------------------------------인스턴스변수선언-------------------------------------------------------------------------

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public ObjectOutputStream getOos() {
		return oos;
	}

	public void setOos(ObjectOutputStream oos) {
		this.oos = oos;
	}

	public ObjectInputStream getOis() {
		return ois;
	}

	public void setOis(ObjectInputStream ois) {
		this.ois = ois;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	// -------------------------------------------------------------------------네트워크기본세팅-------------------------------------------------------------------------
	public Font getBingoFont() {
		return bingoFont;
	}

	public void setBingoFont(Font bingoFont) {
		this.bingoFont = bingoFont;
	}

	public JButton getBingoBtn() {
		return bingoBtn;
	}

	public void setBingoBtn(JButton bingoBtn) {
		this.bingoBtn = bingoBtn;
	}

	public String getPlayerLabel() {
		return playerLabel;
	}

	public void setPlayerLabel(String playerLabel) {
		this.playerLabel = playerLabel;
	}

	public JButton[] getPlayerBtn() {
		return playerBtn;
	}

	public void setPlayerBtn(JButton[] playerBtn) {
		this.playerBtn = playerBtn;
	}

	public JTextArea getNumLog() {
		return numLog;
	}

	public void setNumLog(JTextArea numLog) {
		this.numLog = numLog;
	}

	public JTextArea getTurnLog() {
		return turnLog;
	}

	public void setTurnLog(JTextArea turnLog) {
		this.turnLog = turnLog;
	}

	public int getAmIWin() {
		return amIWin;
	}

	public void setAmIWin(int amIWin) {
		this.amIWin = amIWin;
	}

	public Color getSelected() {
		return selected;
	}

	public void setSelected(Color selected) {
		this.selected = selected;
	}

	public int getResetCount() {
		return resetCount;
	}

	public void setResetCount(int resetCount) {
		this.resetCount = resetCount;
	}

	public TitledBorder[] getT() {
		return t;
	}

	public void setT(TitledBorder[] t) {
		this.t = t;
	}

	public Vector<JButton[]> getOppBtn() {
		return oppBtn;
	}

	public void setOppBtn(Vector<JButton[]> oppBtn) {
		this.oppBtn = oppBtn;
	}

	public JTextArea getChatArea() {
		return chatArea;
	}

	public void setChatArea(JTextArea chatArea) {
		this.chatArea = chatArea;
	}

	public TextField getChatField() {
		return chatField;
	}

	public void setChatField(TextField chatField) {
		this.chatField = chatField;
	}

	public SimpleDateFormat getSdf() {
		return sdf;
	}

	public void setSdf(SimpleDateFormat sdf) {
		this.sdf = sdf;
	}

	public int getWinCnt() {
		return winCnt;
	}

	public void setWinCnt(int winCnt) {
		this.winCnt = winCnt;
	}

	// -------------------------------------------------------------------------인스턴스세팅-------------------------------------------------------------------------
	public void connect() {
		try {
			socket = new Socket(ip, PORT);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			bingo.BingoClientThread ct = new bingo.BingoClientThread(this);
			ct.start();
		} catch (Exception e) {
			// TODO: handle exception
			// e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Connect Fail", "Error", JOptionPane.ERROR_MESSAGE);
			this.dispose();
		}
	}
	// 서버와의 통신을 위한 네트워크 접속 메소드

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == chatField) {
				if (chatField.getText().equals(" ") || chatField.getText().length() == 0) {
					JOptionPane.showMessageDialog(this, "보낼 메시지가 없습니다", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				} else if (chatField.getText().startsWith("/w")) {
					String wisper = chatField.getText().trim();
					oos.writeObject("[WHISPER]" + "@" + id + "@" + wisper.substring(2));
					chatArea.append(
							wisper.substring(2).trim().split("@")[0] + " 님에게 귓속말 : " + wisper.split("@")[1] + "\r\n");
					chatField.setText("\0");
					chatField.requestFocus();
					return;
				} else if (chatField.getText().equals("/?")) {
					chatArea.append("@@@@@@@@@@@@@@ 도움말 @@@@@@@@@@@@@" + "\r\n");
					chatArea.append("1, 게임 규칙" + "\r\n");
					chatArea.append(" " + "\r\n");
					chatArea.append("1) 방장을 제외한 모든 플레이어가 READY 버튼을 눌러야" + "\r\n");
					chatArea.append("게임 시작이 가능합니다." + "\r\n");
					chatArea.append(" " + "\r\n");
					chatArea.append("2) 한 턴에 하나의 버튼만 누를 수 있습니다." + "\r\n");
					chatArea.append(" " + "\r\n");
					chatArea.append("3) 이미 선택된 버튼(검은색)도 누를 수 있으니 신중히 플레이 하십시오." + "\r\n");
					chatArea.append(" " + "\r\n");
					chatArea.append("4) 빙고의 개수가 3개가 되면 승리합니다." + "\r\n");
					chatArea.append("참고로, 여러 명의 승자가 있을 수 있습니다." + "\r\n");
					chatArea.append(" " + "\r\n");
					chatArea.append("5) 게임이 한 번 종료되면 빙고판의 숫자는 다시 무작위로 배열됩니다." + "\r\n");
					chatArea.append("단, 승리 기록은 저장됩니다." + "\r\n");
					chatArea.append(" " + "\r\n");
					chatArea.append("6) 재경기를 위해서는 다시 방장을 제외한 모든 플레이어가" + "\r\n");
					chatArea.append("READY 버튼을 눌러야 합니다." + "\r\n");
					chatArea.append(" " + "\r\n");
					chatArea.append("2, 그밖의 정보" + "\r\n");
					chatArea.append(" " + "\r\n");
					chatArea.append("1) /? : 도움말 표시" + "\r\n");
					chatArea.append(" " + "\r\n");
					chatArea.append("2) 귓속말 보내기 규칙 : /w + 상대방의 ID + @ + 보낼 메시지" + "\r\n");
					chatArea.append(" " + "\r\n");
					chatArea.append("3) 전적 확인 규칙 : /stat + 전적을 확인할 플레이어의 ID" + "\r\n");
					chatArea.append(" " + "\r\n");
					chatArea.append("4) 강제 퇴장(방장만 가능) 규칙 : /kick + 퇴장시킬 플레이어의 ID" + "\r\n");
					chatArea.append(" " + "\r\n");
					chatArea.append("5) 플레이어 넘버 확인 규칙 : /number + 넘버를 확인할 플레이어의 ID" + "\r\n");
					chatArea.append(" " + "\r\n");
					chatArea.setCaretPosition(chatArea.getDocument().getLength());
					chatField.setText("\0");
					chatField.requestFocus();
					return;
				} else if (chatField.getText().startsWith("/stat")) {
					oos.writeObject("[STAT]" + "@" + id + "@" + chatField.getText().substring(5));
					chatField.setText("\0");
					chatField.requestFocus();
					return;
				} else if (playerLabel.equals("1번") && chatField.getText().startsWith("/kick")) {
					oos.writeObject("[KICK]" + chatField.getText().substring(5).trim());
					chatField.setText("\0");
					chatField.requestFocus();
					return;
				} else if (chatField.getText().startsWith("/number")) {
					oos.writeObject("[NUMBER]" + "@" + id + "@" + chatField.getText().substring(7));
					chatField.setText("\0");
					chatField.requestFocus();
					return;
				} else {
					oos.writeObject("[CHATBUTTON]" + id + " 님의 말 : " + chatField.getText());
					chatField.setText("\0");
					chatField.requestFocus();
					return;
				}
			}
			if (e.getSource() == exitBtn) {
				oos.writeObject("[EXIT]" + playerLabel + "님이 나갔습니다");
				System.exit(0);
			}
			// -------------------------------------------------------------------------채팅-------------------------------------------------------------------------
			for (int j = 0; j < playerBtn.length; j++) {
				if (e.getSource() == playerBtn[j]) {
					oos.writeObject("[BINGOBUTTON]" + playerBtn[j].getText() + "@" + "[LOG]" + "Number ["
							+ playerBtn[j].getText() + "] has chosen by " + playerLabel + " 플레이어" + "["
							+ sdf.format(System.currentTimeMillis()) + "]" + "\r\n" + "@" + "[OPPDATA]" + playerLabel
							+ j + "@" + "[TURNLOG]" + nextPlayer + " 플레이어의 턴입니다");
					playerBtn[j].setEnabled(false);
					playerBtn[j].setBackground(selected);
					onlyYourTurn(false);
				}
			}
			// -------------------------------------------------------------------------빙고버튼누르기-------------------------------------------------------------------------
			if (e.getSource() == bingoBtn) {
				resetCount = 0;
				nextPlayer = playerLabel.equals("1번") ? "2번"
						: playerLabel.equals("2번") ? "3번"
								: playerLabel.equals("3번") ? "4번" : playerLabel.equals("4번") ? "5번" : "1번";
				if (bingoBtn.isEnabled() && bingoBtn.getText().equals("START")) {
					bingoBtn.setEnabled(false);
					bingoBtn.setBackground(new Color(65, 110, 188));
					JOptionPane.showMessageDialog(this, "시자아아아아악");
					oos.writeObject("[START]");
				} else {
					oos.writeObject("[READY]" + playerLabel);
					bingoBtn.setEnabled(false);
					bingoBtn.setBackground(new Color(65, 110, 188));
				}
			}
			// -------------------------------------------------------------------------레디버튼누르기-------------------------------------------------------------------------
		} catch (Exception e2) {
			// TODO: handle exception
			e2.printStackTrace();
		}
	}

	public void setOppBorderTitle() {
		if (playerLabel.equals("1번")) {
			t[0].setTitle("2번");
			t[1].setTitle("3번");
			t[2].setTitle("4번");
			t[3].setTitle("5번");
			repaint();
		} else if (playerLabel.equals("2번")) {
			t[0].setTitle("1번");
			t[1].setTitle("3번");
			t[2].setTitle("4번");
			t[3].setTitle("5번");
			repaint();
		} else if (playerLabel.equals("3번")) {
			t[0].setTitle("1번");
			t[1].setTitle("2번");
			t[2].setTitle("4번");
			t[3].setTitle("5번");
			repaint();
		} else if (playerLabel.equals("4번")) {
			t[0].setTitle("1번");
			t[1].setTitle("2번");
			t[2].setTitle("3번");
			t[3].setTitle("5번");
			repaint();
		} else if (playerLabel.equals("5번")) {
			t[0].setTitle("1번");
			t[1].setTitle("2번");
			t[2].setTitle("3번");
			t[3].setTitle("4번");
			repaint();
		}
	}
	// 상대 플레이어판에 이름 세팅_180530 추가

	public void reset() {
		resetCount = 1;
		ranNum();
		onlyYourTurn(false);
		bingoBtn.setBackground(new Color(100, 100, 100));
		if (bingoBtn.getText().equals("READY")) {
			bingoBtn.setEnabled(true);
		}
		amIWin = 0;
		cnt = 0; // 다음 판 플레이를 위한 빙고 카운트 초기화_180530 추가
		for (int i = 0; i < opp1Btn.length; i++) {
			opp1Btn[i].setBackground(new Color(247, 234, 200));
			opp2Btn[i].setBackground(new Color(247, 234, 200));
			opp3Btn[i].setBackground(new Color(247, 234, 200));
			opp4Btn[i].setBackground(new Color(247, 234, 200));
		}
		subCheck();
		v = new Vector<Integer>();
		// 재게임을 위한 백터 초기화_0605 추가
	}
	// 종료 조건(빙고 3개 : cnt==3) 달성 시 다음 판을 위한 리셋 메소드

	public void onlyYourTurn(boolean isMyTurn) {
		for (int i = 0; i < playerBtn.length; i++) {
			playerBtn[i].setEnabled(isMyTurn);
		}
	}
	// 자신의 턴을 확인하는 메소드

	public void selectedCheck() {
		v.removeAllElements(); // 중복값 제거를 위한 코드_0605 추가
		for (int i = 0; i < playerBtn.length; i++) {
			if (playerBtn[i].getBackground().equals(selected)) {
				v.add(i);
			}
		}
	}
	// 빙고의 기준인 검은색 버튼을 찾는 메소드

	public void subCheck() {
		temp1 = new Vector<Integer>();
		for (int i = 0; i < 5; i++) {
			temp1.add(i);
		}
		temp2 = new Vector<Integer>();
		for (int i = 5; i < 10; i++) {
			temp2.add(i);
		}
		temp3 = new Vector<Integer>();
		for (int i = 10; i < 15; i++) {
			temp3.add(i);
		}
		temp4 = new Vector<Integer>();
		for (int i = 15; i < 20; i++) {
			temp4.add(i);
		}
		temp5 = new Vector<Integer>();
		for (int i = 20; i < 25; i++) {
			temp5.add(i);
		}
		temp6 = new Vector<Integer>();
		for (int i = 0; i < 25; i += 5) {
			temp6.add(i);
		}
		temp7 = new Vector<Integer>();
		for (int i = 1; i < 25; i += 5) {
			temp7.add(i);
		}
		temp8 = new Vector<Integer>();
		for (int i = 2; i < 25; i += 5) {
			temp8.add(i);
		}
		temp9 = new Vector<Integer>();
		for (int i = 3; i < 25; i += 5) {
			temp9.add(i);
		}
		temp10 = new Vector<Integer>();
		for (int i = 4; i < 25; i += 5) {
			temp10.add(i);
		}
		temp11 = new Vector<Integer>();
		for (int i = 0; i < 25; i += 6) {
			temp11.add(i);
		}
		temp12 = new Vector<Integer>();
		for (int i = 4; i < 21; i += 4) {
			temp12.add(i);
		}
	}
	// 빙고의 경우의 수를 세팅하는 족보 메소드

	public void winCheck() {
		if (temp1 != null && v.containsAll(temp1)) {
			cnt++;
			temp1 = null;
		}
		if (temp2 != null && v.containsAll(temp2)) {
			cnt++;
			temp2 = null;
		}
		if (temp3 != null && v.containsAll(temp3)) {
			cnt++;
			temp3 = null;
		}
		if (temp4 != null && v.containsAll(temp4)) {
			cnt++;
			temp4 = null;
		}
		if (temp5 != null && v.containsAll(temp5)) {
			cnt++;
			temp5 = null;
		}
		if (temp6 != null && v.containsAll(temp6)) {
			cnt++;
			temp6 = null;
		}
		if (temp7 != null && v.containsAll(temp7)) {
			cnt += 1;
			temp7 = null;
		}
		if (temp8 != null && v.containsAll(temp8)) {
			cnt++;
			temp8 = null;
		}
		if (temp9 != null && v.containsAll(temp9)) {
			cnt++;
			temp9 = null;
		}
		if (temp10 != null && v.containsAll(temp10)) {
			cnt++;
			temp10 = null;
		}
		if (temp11 != null && v.containsAll(temp11)) {
			cnt++;
			temp11 = null;
		}
		if (temp12 != null && v.containsAll(temp12)) {
			cnt++;
			temp12 = null;
		}
		try {
			if (cnt == 3) {
				amIWin = 1;
				v = null;
				// 재게임을 위한 백터 비우기_0605 추가
				winCnt++;
				oos.writeObject("[GAMEOVER]");
				oos.writeObject("[WINNER]" + id);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	// 빙조 조건 충족 여부를 가리는 메소드

	public void ranNum() {
		for (int i = 0; i < m.length; i++) {
			m[i] = (int) (Math.random() * 25 + 1);
			for (int j = 0; j < i; j++) {
				if (m[i] == m[j]) {
					i--;
					break;
				}
			}
		}
		for (int j = 0; j < m.length; j++) {
			k[j] = Integer.toString(m[j]);
			playerBtn[j].setBackground(new Color(247, 234, 200));
			playerBtn[j].setFont(font);
			playerBtn[j].setText(k[j]);
		}
	}
	// 빙고판 위의 버튼들에 무작위 번호를 부여하는 메소드

	public void spreadBtn(JButton[] btn, JPanel pan) {
		for (int i = 0; i < btn.length; i++) {
			btn[i] = new JButton();
			pan.add(btn[i]);
			if (btn[i] != playerBtn[i]) {
				btn[i].setEnabled(false);
				btn[i].setBackground(new Color(247, 234, 200));
			}
		}
	}
	// 플레이어 버튼과 다른 참가자 판의 버튼을 세팅하는 메소드

	public void bingoUIFrame() {
		playBingo = new ImageIcon(banner);
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		systemPan = new JPanel() {
			public void paintComponent(Graphics g) {
				g.drawImage(playBingo.getImage(), 0, 0, null);
				setOpaque(false);
			}
		};
		systemPan.setPreferredSize(new Dimension(1920, 156));
		mainPan = new JPanel();
		bingoPan = new JPanel();
		bingoPan.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 2), "My Bingo Panel"));
		spreadBtn(playerBtn, bingoPan);
		ranNum();
		bingoPan.setLayout(new GridLayout(5, 5));
		statusPan = new JPanel();
		statusPan.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 2), "Status", 0, 0, null, Color.BLACK));
		currentNo = new JPanel();
		currentNo
				.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 2), "Current Number", 0, 0, null, Color.BLACK));
		currentPlayer = new JPanel();
		currentPlayer
				.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 2), "Who's Turn", 0, 0, null, Color.BLACK));
		statusPan.add("North", currentNo);
		currentNo.setPreferredSize(new Dimension((int) (width * 0.19), (int) (height * 0.21)));
		JScrollPane jsp1 = new JScrollPane(numLog = new JTextArea());
		jsp1.setPreferredSize(new Dimension(352, 195));
		numLog.setEditable(false);
		numLog.setBackground(new Color(250, 246, 85));
		numLog.setForeground(Color.BLACK);
		numLog.setCaretPosition(numLog.getDocument().getLength()); // 자동 스크롤 내리기 0604 추가
		currentNo.add(jsp1);

		statusPan.add("Center", currentPlayer);
		currentPlayer.setPreferredSize(new Dimension((int) (width * 0.19), (int) (height * 0.21)));
		JScrollPane jsp2 = new JScrollPane(turnLog = new JTextArea());
		jsp2.setPreferredSize(new Dimension(352, 195));
		turnLog.setEditable(false);
		turnLog.setBackground(new Color(250, 246, 85));
		turnLog.setForeground(Color.BLACK);
		turnLog.setCaretPosition(turnLog.getDocument().getLength()); // 자동 스크롤 내리기 0604 추가
		currentPlayer.add(jsp2);

		statusPan.add("South", bingoBtn = new JButton("READY"));
		bingoBtn.setFont(bingoFont);
		bingoBtn.setPreferredSize(new Dimension(360, 99));
		bingoBtn.setBackground(new Color(100, 100, 100));
		bingoBtn.addActionListener(this);
		bingoBtn.setEnabled(false);
		chatPan = new JPanel();
		chatPan.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 2), "Chatting Panel", 0, 0, null, Color.BLACK));
		mainPan.add("West", bingoPan);
		bingoPan.setPreferredSize(new Dimension((int) (width * 0.57), (int) (height * 0.55)));
		mainPan.add("Center", statusPan);
		statusPan.setPreferredSize(new Dimension((int) (width * 0.20), (int) (height * 0.55)));
		mainPan.add("East", chatPan);
		chatPan.setPreferredSize(new Dimension((int) (width * 0.22), (int) (height * 0.55)));
		chatArea = new JTextArea(20, 20);
		chatArea.setEditable(false);
		chatArea.setLineWrap(true);
		chatArea.setBackground(new Color(250, 246, 85));
		chatArea.setCaretPosition(chatArea.getDocument().getLength()); // 자동 스크롤 내리기 0604 추가
		JScrollPane jsp = new JScrollPane(chatArea);
		jsp.setPreferredSize(new Dimension((int) (width * 0.21), (int) (height * 0.49)));
		chatPan.add(jsp, BorderLayout.CENTER);
		chatPan.add("South", chatField = new TextField(44));
		chatField.setBackground(new Color(250, 246, 85));
		chatField.addActionListener(this);
		chatPan.add("South", exitBtn = new JButton(" EXIT "));
		exitBtn.setBackground(Color.orange);
		exitBtn.addActionListener(this);
		this.add("North", systemPan);
		this.add("Center", mainPan);
		oppPan1 = new JPanel();
		oppPan1.setBorder(t[0] = new TitledBorder(new LineBorder(Color.black, 2), waiting));
		oppPan2 = new JPanel();
		oppPan2.setBorder(t[1] = new TitledBorder(new LineBorder(Color.black, 2), waiting));
		oppPan3 = new JPanel();
		oppPan3.setBorder(t[2] = new TitledBorder(new LineBorder(Color.black, 2), waiting));
		oppPan4 = new JPanel();
		oppPan4.setBorder(t[3] = new TitledBorder(new LineBorder(Color.black, 2), waiting));
		oppPlayerPan = new JPanel();
		oppPlayerPan.setPreferredSize(new Dimension(width, (int) (height * 0.22)));
		oppPlayerPan.add(oppPan1);
		oppPan1.setPreferredSize(new Dimension(width / 4 - 10, (int) (height * 0.18)));
		spreadBtn(opp1Btn, oppPan1);
		oppPan1.setLayout(new GridLayout(5, 5));
		oppPlayerPan.add(oppPan2);
		oppPan2.setPreferredSize(new Dimension(width / 4 - 10, (int) (height * 0.18)));
		spreadBtn(opp2Btn, oppPan2);
		oppPan2.setLayout(new GridLayout(5, 5));
		oppPlayerPan.add(oppPan3);
		oppPan3.setPreferredSize(new Dimension(width / 4 - 10, (int) (height * 0.18)));
		spreadBtn(opp3Btn, oppPan3);
		oppPan3.setLayout(new GridLayout(5, 5));
		oppPlayerPan.add(oppPan4);
		oppPan4.setPreferredSize(new Dimension(width / 4 - 10, (int) (height * 0.18)));
		spreadBtn(opp4Btn, oppPan4);
		oppPan4.setLayout(new GridLayout(5, 5));
		oppPlayerPan.setBorder(new TitledBorder(new LineBorder(Color.BLACK, 2), "Opponent"));

		this.add("South", oppPlayerPan);
		bingoPan.setBackground(new Color(241, 102, 58));
		oppPlayerPan.setBackground(new Color(241, 102, 58));
		mainPan.setBackground(new Color(241, 102, 58));
		systemPan.setBackground(new Color(241, 102, 58));
		chatPan.setBackground(new Color(241, 102, 58));
		oppPan1.setBackground(new Color(241, 102, 58));
		oppPan2.setBackground(new Color(241, 102, 58));
		oppPan3.setBackground(new Color(241, 102, 58));
		oppPan4.setBackground(new Color(241, 102, 58));
		statusPan.setBackground(new Color(241, 102, 58));
		currentNo.setBackground(new Color(241, 102, 58));
		currentPlayer.setBackground(new Color(241, 102, 58));

		subCheck();
		for (int i = 0; i < playerBtn.length; i++) {
			playerBtn[i].addActionListener(this);
		}
		// 재경기 때 액션 리스너 두 번 붙는거 방지
	}
	// 전체적인 UI를 세팅하는 메소드

	public BinGo(String ip, String id) {
		this.ip = ip;
		this.id = id;
		bingoUIFrame();
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setUndecorated(false);
		this.setTitle("####PLAY BINGO####");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		onlyYourTurn(false);
		oppBtn.add(opp1Btn);
		oppBtn.add(opp2Btn);
		oppBtn.add(opp3Btn);
		oppBtn.add(opp4Btn);
		chatArea.append("System Info : /? 을 입력하시면, 도움말을 볼 수 있습니다" + "\r\n");
	}
	// 생성자
}
