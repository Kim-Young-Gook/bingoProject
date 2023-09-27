package bingo;

import java.awt.Color;
import java.awt.Font;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class BinGoServer extends JFrame {
	static final int PORT = 5000;
	private Socket socket;
	private ArrayList<BinGoServerThread> list;
	private JTextArea jta;
	private Font font=new Font("Server", Font.BOLD, 20);

	public JTextArea getJta() {
		return jta;
	}

	public void setJta(JTextArea jta) {
		this.jta = jta;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public ArrayList<BinGoServerThread> getList() {
		return list;
	}

	public void setList(ArrayList<BinGoServerThread> list) {
		this.list = list;
	}

	public BinGoServer() {
		this.setTitle("####BinGoServer####");
		JScrollPane jsp = new JScrollPane(jta = new JTextArea());
		jta.setBackground(Color.BLACK);
		jta.setForeground(Color.WHITE);
		jta.setFont(font);
		this.add(jsp);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(100, 100, 900, 600);
		this.setUndecorated(false);
		this.setVisible(true);
		boolean isStop = false;
		list = new ArrayList<BinGoServerThread>();
		jta.setLineWrap(true);
		jta.setEditable(false);
		jta.append("Server is Ready!" + "\r\n");
		jta.setCaretPosition(jta.getDocument().getLength());
		while (!isStop) {
			try {
				ServerSocket serverSocket = new ServerSocket(PORT);
				socket = serverSocket.accept();
				BinGoServerThread bingoServerThread = new BinGoServerThread(this);
				bingoServerThread.start();
				list.add(bingoServerThread);
				jta.append("Socket : " + socket + "\r\n");
				jta.setCaretPosition(jta.getDocument().getLength());
				jta.append("접속자 수 : " + list.size() + "\r\n");
				jta.setCaretPosition(jta.getDocument().getLength());
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	public static void main(String[] args) {
		new BinGoServer();
	}

}
