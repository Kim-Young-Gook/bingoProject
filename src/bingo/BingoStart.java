package bingo;

import java.awt.event.*;
import java.net.URL;

import javax.swing.*;
import java.awt.*;

public class BingoStart extends JFrame implements ActionListener {
	ImageIcon icon1, icon2, icon3;
	JTextField id, ip;
	JButton jbtn1;
	String idtxt, iptxt;
	private URL loginTop = getClass().getClassLoader().getResource("loginTop.jpg");
	private URL loginMid = getClass().getClassLoader().getResource("loginMid.jpg");
	private URL loginStart = getClass().getClassLoader().getResource("loginStart.jpg");

	@Override
	public void actionPerformed(ActionEvent e) {
		if (id.getText().equals(" ") || id.getText().length() == 0 || ip.getText().equals(" ")
				|| ip.getText().length() == 0) {
			JOptionPane.showMessageDialog(this, "아이디, 아이피를 입력해주세요", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		idtxt = id.getText();
		iptxt = ip.getText();
		new BinGo(iptxt.trim(), idtxt.trim()).connect();
		this.dispose();
	}

	public void paint() {

		icon1 = new ImageIcon(loginTop);
		JPanel background1 = new JPanel() {
			public void paintComponent(Graphics g) {
				g.drawImage(icon1.getImage(), 0, 0, null);
				setOpaque(false);
			}
		};

		background1.setPreferredSize(new Dimension(500, 293));

		icon2 = new ImageIcon(loginMid);
		JPanel background2 = new JPanel() {

			public void paintComponent(Graphics g) {
				g.drawImage(icon2.getImage(), 0, 0, null);
				setOpaque(false);
			}
		};

		background2.setLayout(null);
		background2.setPreferredSize(new Dimension(500, 250));
		background2.add(id = new JTextField(10));
		id.setFont(new Font("나눔고딕", Font.BOLD, 22));
		id.setForeground(new Color(186, 74, 36));
		background2.add(ip = new JTextField(10));
		background2.add(jbtn1 = new JButton(new ImageIcon(loginStart)));
		ip.setFont(new Font("나눔고딕", Font.BOLD, 22));
		ip.setForeground(new Color(186, 74, 36));
		id.setBounds(109, 55, 170, 40);
		ip.setBounds(109, 105, 170, 45);
		id.setOpaque(false);
		id.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		ip.setOpaque(false);
		ip.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		jbtn1.setBounds(313, 63, 160, 75);
		jbtn1.addActionListener(this);
		this.add("North", background1);
		this.add("Center", background2);
		this.setTitle("####Play Bingo####");
		this.pack();
	}

	public BingoStart() {
		paint();
	}

	public static void main(String[] args) {
		BingoStart frame = new BingoStart();

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 505, 570);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
	}
}
