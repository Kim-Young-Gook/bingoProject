package bingo;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Stack;

public class BinGoServerThread extends Thread {
	private BinGoServer server;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Socket socket;

	private String[] playerLabel = { "1��", "2��", "3��", "4��", "5��" };
	static int labelIndex = 0;
	private String isWho;
	static int readyCnt = 0;
	static int turnIndex = 0;
	
	static int winnerCnt=0;

	static int exitCheck = 0;
	private static Stack<String> whoIsExit = new Stack<String>();

	public BinGoServerThread(BinGoServer server) {
		super();
		this.server = server;
	}

	public String getIsWho() {
		return isWho;
	}

	public void setIsWho(String isWho) {
		this.isWho = isWho;
	}

	public void broadCast(String fromClient) {
		for (BinGoServerThread a : server.getList()) {
			a.sendfromClient(fromClient);
		}
	}

	public void sendfromClient(String fromClient) {
		try {
			oos.writeObject(fromClient);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		boolean isStop = false;
		try {
			socket = server.getSocket();
			ois = new ObjectInputStream(socket.getInputStream());
			oos = new ObjectOutputStream(socket.getOutputStream());
			String fromClient = null;
			if (exitCheck == 0) {
				oos.writeObject("[PLAYERLABEL]" + (isWho = playerLabel[labelIndex++]));
			} else {
				oos.writeObject("[PLAYERLABEL]" + (isWho = whoIsExit.get(exitCheck - 1)));
				whoIsExit.pop();
				exitCheck--;
			}
			if (labelIndex == playerLabel.length) {
				labelIndex = 0;
			}
			while (!isStop) {
				fromClient = (String) ois.readObject();
				if(fromClient.startsWith("[ENTER")) {
					broadCast(fromClient);
				}
				if (fromClient.startsWith("[CHATBUTTON]")) {
					broadCast(fromClient);
				}
				if (fromClient.startsWith("[WHISPER]")) {
					broadCast(fromClient);
				}
				if(fromClient.startsWith("[STAT]")) {
					broadCast(fromClient);
				}
				if (fromClient.startsWith("[WINCNT]")) {
					broadCast(fromClient);
				}
				if(fromClient.startsWith("[KICK]")) {
					broadCast(fromClient);
				}
				if(fromClient.startsWith("[NUMBER]")) {
					broadCast(fromClient);
				}
				if (fromClient.startsWith("[MYNUMBER]")) {
					broadCast(fromClient);
				}
				if (fromClient.startsWith("[EXIT]")) {
					broadCast(fromClient);
					isStop = true;
				}
				if (fromClient.startsWith("[BINGOBUTTON]")) {
					broadCast(fromClient.split("@")[0]);
					broadCast(fromClient.split("@")[1]);
					broadCast(fromClient.split("@")[2]);
					broadCast(fromClient.split("@")[3]);
					if (isWho.equals("5��")) {
						turnIndex = 0;
						broadCast("[TURN]" + playerLabel[turnIndex]);
					} else {
						broadCast("[TURN]" + playerLabel[turnIndex += 1]);
					}
				}
				if (fromClient.startsWith("[READY]")) {
					readyCnt++;
					broadCast(fromClient);
					if (readyCnt == playerLabel.length - 1) {
						broadCast("[READYCOMPLETE]");
						readyCnt = 0;
					}
				}
				if (fromClient.equals("[START]")) {
					turnIndex = 0;
					winnerCnt=0;
					broadCast(fromClient);
					broadCast("[TURN]" + playerLabel[turnIndex]); // �������� �𸣰����� oos�� ���ִ� ���� broadCast�� ����
				}
				if (fromClient.equals("[GAMEOVER]")) {
					turnIndex = 0; // ���� �� ������ ���� �� �ε��� �ʱ�ȭ_180530 �߰�
					broadCast("[GAMEOVER]"+winnerCnt);
					winnerCnt = 1;
				}
				if (fromClient.startsWith("[WINNER]")) {
					broadCast(fromClient);
				}
			}
			for (int i = 0; i < server.getList().size(); i++) {
				if (isWho.equals(server.getList().get(i).getIsWho())) {
					whoIsExit.push(isWho);
					exitCheck++;
				}
			}
			server.getList().remove(this);
//			System.out.println(socket.getInetAddress() + "�� ���� ����");
//			System.out.println("���� ������ ��:" + server.getList().size() + "��");
			server.getJta().append(socket.getInetAddress() + "�� ���� ����"+"\r\n");
			server.getJta().append("���� ������ ��:" + server.getList().size() + "��"+"\r\n");
			server.getJta().setCaretPosition(server.getJta().getDocument().getLength());
			ois.close();
			oos.close();
		} catch (Exception e) {
			// TODO: handle exception
			for (int i = 0; i < server.getList().size(); i++) {
				if (isWho.equals(server.getList().get(i).getIsWho())) {
					whoIsExit.push(isWho);
					exitCheck++;
				}
			}
			server.getList().remove(this);
			server.getJta().append(socket.getInetAddress() + "�� ���� ����"+"\r\n");
			server.getJta().append("���� ������ ��:" + server.getList().size() + "��"+"\r\n");
			server.getJta().setCaretPosition(server.getJta().getDocument().getLength());
			try {
				ois.close();
				oos.close();				
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}

}