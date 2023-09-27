package bingo;

import javax.swing.JOptionPane;

public class BingoClientThread extends Thread {
	private BinGo bingo;
	private String request;

	public BingoClientThread(BinGo bingo) {
		super();
		this.bingo = bingo;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		boolean isStop = false;
		String fromServer = null;
		try {

			fromServer = (String) bingo.getOis().readObject();
			if (fromServer.startsWith("[PLAYERLABEL]")) {
				bingo.setPlayerLabel(fromServer.substring(13));
				if (bingo.getPlayerLabel().equals("1번")) {
					JOptionPane.showMessageDialog(bingo, "당신은 방장입니다.");
					bingo.getBingoBtn().setText("START");
					bingo.getBingoBtn().setFont(bingo.getBingoFont());
					bingo.setOppBorderTitle();
				} else {
					JOptionPane.showMessageDialog(bingo, "당신은 " + bingo.getPlayerLabel() + " 플레이어입니다.");
					bingo.getBingoBtn().setEnabled(true);
					bingo.setOppBorderTitle();
				}
			}
			while (!isStop) {
				fromServer = (String) bingo.getOis().readObject();
				if (fromServer.startsWith("[CHATBUTTON]")) {
					bingo.getChatArea().append(fromServer.substring(12) + "\r\n");
					bingo.getChatArea().setCaretPosition(bingo.getChatArea().getDocument().getLength());
					// 자동 스크롤 내리기 0604 추가
				}
				if (fromServer.startsWith("[WHISPER]")) {
					String whisper = fromServer.trim();
					if (whisper.split("@")[2].trim().equals(bingo.getId())) {
						bingo.getChatArea()
								.append(whisper.split("@")[1] + " 님의 귓속말 : " + whisper.split("@")[3] + "\r\n");
						bingo.getChatArea().setCaretPosition(bingo.getChatArea().getDocument().getLength());
					}
				}
				if (fromServer.startsWith("[STAT]")) {
					request = fromServer.split("@")[1].trim();
					if (fromServer.split("@")[2].trim().equals(bingo.getId())) {
						bingo.getOos().writeObject(
								"[WINCNT]" + bingo.getId() + " 님의 승리 횟수 : " + bingo.getWinCnt() + "번" + "\r\n");
					}
				}
				if (fromServer.startsWith("[WINCNT]")) {
					if (bingo.getId().equals(request)) {
						bingo.getChatArea().append(fromServer.substring(8));
						bingo.getChatArea().setCaretPosition(bingo.getChatArea().getDocument().getLength());
					}
					request = null;
				}
				if (fromServer.startsWith("[KICK]") && fromServer.substring(6).trim().equals(bingo.getId())) {
					System.exit(0);
				}
				if (fromServer.startsWith("[NUMBER]")) {
					request = fromServer.split("@")[1].trim();
					if (fromServer.split("@")[2].trim().equals(bingo.getId())) {
						bingo.getOos().writeObject(
								"[MYNUMBER]" + bingo.getId() + " 님의 플레이어 넘버 : " + bingo.getPlayerLabel() + "\r\n");
					}
				}
				if (fromServer.startsWith("[MYNUMBER]")) {
					if (bingo.getId().equals(request)) {
						bingo.getChatArea().append(fromServer.substring(10));
						bingo.getChatArea().setCaretPosition(bingo.getChatArea().getDocument().getLength());
					}
					request = null;
				}
				if (fromServer.startsWith("[EXIT]")) {
					bingo.getChatArea().append((fromServer.substring(6) + "\n"));
					bingo.getChatArea().setCaretPosition(bingo.getChatArea().getDocument().getLength());
					// 자동 스크롤 내리기 0604 추가
				}
				if (fromServer.startsWith("[READY]")) {
					bingo.getChatArea()
							.append("System Info : " + fromServer.substring(7) + " 플레이어 is Ready !!!" + "\r\n");
					bingo.getChatArea().setCaretPosition(bingo.getChatArea().getDocument().getLength());
					// 자동 스크롤 내리기 0604 추가
				}
				if (fromServer.equals("[START]")) {
					bingo.getChatArea().append("@@@@@@@@@@@@@ GAMESTART @@@@@@@@@@@@" + "\r\n");
					bingo.getChatArea().setCaretPosition(bingo.getChatArea().getDocument().getLength());
					// 자동 스크롤 내리기 0604 추가
				}
				if (fromServer.equals("[READYCOMPLETE]") && bingo.getPlayerLabel().equals("1번")) {
					bingo.getBingoBtn().setEnabled(true);
				}
				if (fromServer.startsWith("[BINGOBUTTON")) {
					for (int i = 0; i < bingo.getPlayerBtn().length; i++) {
						if (bingo.getPlayerBtn()[i].getText().equals(fromServer.substring(13))) {
							bingo.getPlayerBtn()[i].setEnabled(false);
							bingo.getPlayerBtn()[i].setBackground(bingo.getSelected());
							bingo.selectedCheck();
							bingo.winCheck();
						}
					}
				}
				if (fromServer.startsWith("[TURNLOG]")) {
					bingo.getTurnLog().append(fromServer.substring(9) + "["
							+ bingo.getSdf().format(System.currentTimeMillis()) + "]" + "\r\n");
					bingo.getTurnLog().setCaretPosition(bingo.getTurnLog().getDocument().getLength());
					// 자동 스크롤 내리기 0604 추가
				}
				if (fromServer.startsWith("[LOG]")) {
					bingo.getNumLog().append(fromServer.substring(5));
					bingo.getNumLog().setCaretPosition(bingo.getNumLog().getDocument().getLength());
					// 자동 스크롤 내리기 0604 추가
				}
				if (fromServer.startsWith("[TURN]") && fromServer.substring(6).equals(bingo.getPlayerLabel())) {
					JOptionPane.showMessageDialog(bingo, "당신의 차례입니다");
					bingo.onlyYourTurn(true);
				}
				if (fromServer.startsWith("[OPPDATA]")
						&& !(fromServer.substring(9, 11).equals(bingo.getPlayerLabel()))) {
					switch (fromServer.substring(9, 11)) {
					case "1번":
						for (int i = 0; i < bingo.getT().length; i++) {
							if (bingo.getT()[i].getTitle().equals("1번")) {
								bingo.getOppBtn().get(i)[Integer.parseInt(fromServer.substring(11))]
										.setBackground(bingo.getSelected());
							}
						}
						break;
					case "2번":
						for (int i = 0; i < bingo.getT().length; i++) {
							if (bingo.getT()[i].getTitle().equals("2번")) {
								bingo.getOppBtn().get(i)[Integer.parseInt(fromServer.substring(11))]
										.setBackground(bingo.getSelected());
							}
						}
						break;
					case "3번":
						for (int i = 0; i < bingo.getT().length; i++) {
							if (bingo.getT()[i].getTitle().equals("3번")) {
								bingo.getOppBtn().get(i)[Integer.parseInt(fromServer.substring(11))]
										.setBackground(bingo.getSelected());
							}
						}
						break;
					case "4번":
						for (int i = 0; i < bingo.getT().length; i++) {
							if (bingo.getT()[i].getTitle().equals("4번")) {
								bingo.getOppBtn().get(i)[Integer.parseInt(fromServer.substring(11))]
										.setBackground(bingo.getSelected());
							}
						}
						break;
					case "5번":
						for (int i = 0; i < bingo.getT().length; i++) {
							if (bingo.getT()[i].getTitle().equals("5번")) {
								bingo.getOppBtn().get(i)[Integer.parseInt(fromServer.substring(11))]
										.setBackground(bingo.getSelected());
							}
						}
						break;

					default:
						break;
					}
				}
				if (fromServer.startsWith("[GAMEOVER]")) {
					if (Integer.parseInt(fromServer.substring(10)) == 0) {
						bingo.getChatArea().append("@@@@@@@@@@@@@ GAMEOVER @@@@@@@@@@@@@" + "\r\n"
								+ "                                Winner Of This Game !!!" + "\r\n");
						bingo.getChatArea().setCaretPosition(bingo.getChatArea().getDocument().getLength());
					}
					// 자동 스크롤 내리기 0604 추가
					if (bingo.getAmIWin() == 1 && bingo.getResetCount() == 0) {
						JOptionPane.showMessageDialog(bingo, "YOU WIN");
						bingo.reset();
					} else if (bingo.getAmIWin() == 0 && bingo.getResetCount() == 0) {
						JOptionPane.showMessageDialog(bingo, "YOU LOSE");
						bingo.reset();
					}
				}
				if (fromServer.startsWith("[WINNER]")) {
					bingo.getChatArea().append("CONGRATULATION !!! " + fromServer.substring(8) + " 님이 승리하셨습니다" + "\r\n");
					bingo.getChatArea().setCaretPosition(bingo.getChatArea().getDocument().getLength());
					// 자동 스크롤 내리기 0604 추가
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			isStop = true;
		}
	}

}
