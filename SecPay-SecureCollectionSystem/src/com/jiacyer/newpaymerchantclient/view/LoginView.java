package com.jiacyer.newpaymerchantclient.view;

import com.jiacyer.newpaymerchantclient.handler.LoginHandler;
import com.jiacyer.newpaymerchantclient.utils.ChangeImageSize;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class LoginView extends JFrame implements MouseListener, ActionListener, FocusListener {

	private JButton login;
	private JLabel bottomLabel;
	private JTextField accountInput;
	private JPasswordField pwInput;
	private LoginHandler handler;
	private ConfigDialog configDialog;

	public LoginView() {
		this.setSize(1500,950);
		this.setLocationRelativeTo(null);
		this.setTitle("SecPay 收款系统");
		this.setIconImage(Toolkit.getDefaultToolkit().createImage(LoginView.class.getResource("/com/jiacyer/newpaymerchantclient/image/logo.png")));
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
		initView();
		handler = LoginHandler.getInstance();
	}

	private void initView() {
		JLabel title = new AlphaLabel("SecPay");
		title.setFont(new Font("Bauhaus 93",Font.BOLD,110));

		JLabel title2 = new AlphaLabel("收款系统");
		title2.setFont(new Font("黑体",Font.BOLD,90));

//		Image logoImage = Toolkit.getDefaultToolkit().createImage(LoginView.class.getResource("/com/jiacyer/newpaymerchantclient/image/logo_with_name.png"));
//        JLabel logo = new AlphaLabel();
//        logo.setIcon(new ImageIcon(logoImage));
//		JLabel blackLabel = new AlphaLabel(" ");
//        blackLabel.setPreferredSize(new Dimension(120, 10));

        JLabel logoLabel = new AlphaLabel();
//        logoLabel.setPreferredSize(new Dimension(10, 20));
        try {
            BufferedImage bi = ImageIO.read(LoginView.class.getResourceAsStream("/com/jiacyer/newpaymerchantclient/image/logo_with_name.png"));
            Image logo = ChangeImageSize.scale(bi, 2, false);
            logoLabel.setIcon(new ImageIcon(logo));
        } catch (IOException e) {
            e.printStackTrace();
        }

        JLabel accountLabel = new AlphaLabel();
        try {
            BufferedImage bi = ImageIO.read(LoginView.class.getResourceAsStream("/com/jiacyer/newpaymerchantclient/image/iconAccount.png"));
            Image iconAccount = ChangeImageSize.scale(bi, 2, false);
            accountLabel.setIcon(new ImageIcon(iconAccount));
        } catch (IOException e) {
            e.printStackTrace();
        }
//		accountLabel.setFont(new Font("黑体",Font.BOLD,60));

		JLabel pwLabel = new AlphaLabel();
        try {
            BufferedImage bi = ImageIO.read(LoginView.class.getResourceAsStream("/com/jiacyer/newpaymerchantclient/image/iconPassword.png"));
            Image iconPassword = ChangeImageSize.scale(bi, 2, false);
            pwLabel.setIcon(new ImageIcon(iconPassword));
        } catch (IOException e) {
            e.printStackTrace();
        }
//		pwLabel.setFont(new Font("黑体",Font.BOLD,60));

		accountInput = new EditView();
		accountInput.setPreferredSize(new Dimension(550,50));
		accountInput.setFont(new Font("黑体",Font.PLAIN,32));
		accountInput.setMargin(new Insets(2,10,2,10));
        accountInput.setForeground(Color.GRAY);
        accountInput.setText("账号");
        accountInput.addFocusListener(this);

		pwInput = new JPasswordField();
		pwInput.setPreferredSize(new Dimension(550,50));
		pwInput.setFont(new Font("黑体",Font.PLAIN,32));
		pwInput.setMargin(new Insets(2,10,2,10));
        pwInput.setForeground(Color.GRAY);
        pwInput.setText("密码");
        pwInput.setEchoChar('\0');
        pwInput.addFocusListener(this);

		login = new JButton("登    录");
		login.setForeground(new Color(0xFFFFFF));
//		login.setContentAreaFilled(false);
//		login.setUI(new BasicButtonUI());
//		login.setMargin(new Insets(0,0,0,0));
//		login.setBorderPainted(false);
//		login.setFont(new Font("微软雅黑",Font.BOLD,60));
//        login = new JButton("收款");
        login.setFont(new Font("微软雅黑",Font.BOLD,30));
//        login.addActionListener(this);
        login.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
        login.setPreferredSize(new Dimension(500,60));
		login.addMouseListener(this);
		login.addActionListener(this);

		bottomLabel = new AlphaLabel("版权所有 © 2018 | Power by SecPay");
		bottomLabel.setFont(new Font("微软雅黑",Font.BOLD,15));
		bottomLabel.addMouseListener(this);

		this.getContentPane().removeAll();
		JPanel titlepanel = new NormalPanel();
		titlepanel.setOpaque(false);
		titlepanel.setLayout(new FlowLayout(FlowLayout.CENTER));
//		titlepanel.add(blackLabel);
		titlepanel.add(title);
		titlepanel.add(logoLabel);
		JPanel title2panel = new NormalPanel();
		title2panel.setOpaque(false);
		title2panel.setLayout(new FlowLayout(FlowLayout.CENTER));
//		title2panel.add("Center", logo);
		title2panel.add(title2);
		JPanel titlePanel = new NormalPanel();
		titlePanel.setOpaque(false);
		titlePanel.setLayout(new GridLayout(2,1));
		titlePanel.add(titlepanel);
		titlePanel.add(title2panel);

		JLabel l1 = new AlphaLabel();
		l1.setOpaque(false);
		l1.setPreferredSize(new Dimension(1000,60));
		JPanel accountPanel = new NormalPanel();
		accountPanel.setOpaque(false);
		accountPanel.setLayout(new FlowLayout());
		accountPanel.add(accountLabel);
		accountPanel.add(accountInput);
		JPanel pwPanel = new NormalPanel();
		pwPanel.setOpaque(false);
		pwPanel.setLayout(new FlowLayout());
		pwPanel.add(pwLabel);
		pwPanel.add(pwInput);
		JPanel button = new NormalPanel();
		button.setOpaque(false);
		button.setLayout(new FlowLayout());
		button.add(login);
		JPanel textPanel = new NormalPanel();
		textPanel.setOpaque(false);
		textPanel.setLayout(new GridLayout(3,1));
		textPanel.add(accountPanel);
		textPanel.add(pwPanel);
		textPanel.add(button);
		JPanel inputPanel = new NormalPanel();
		inputPanel.setOpaque(false);
		inputPanel.setLayout(new BorderLayout());
		inputPanel.add("North",l1);
		inputPanel.add("Center",textPanel);
		JPanel bottom = new NormalPanel(new FlowLayout());
		bottom.add(bottomLabel);

		JPanel panel = new BackgroundPanel(true);
		panel.setLayout(new BorderLayout());
		panel.add("North", titlePanel);
		panel.add("Center", inputPanel);
		panel.add("South", bottom);
		this.add(panel);
		panel.updateUI();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getSource() == bottomLabel) {
			if (configDialog == null)
				configDialog = new ConfigDialog(this);
			else {
				configDialog.dispose();
				configDialog = new ConfigDialog(this);
			}
//		} else if (e.getSource() == login) {
//			login.setForeground(new Color(0xB3B2B2));
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getSource() == login) {
//			login.setForeground(new Color(0xFFFFFF));
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (e.getSource() == bottomLabel) {
			bottomLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
	}

	@SuppressWarnings("unused")
	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == login) {
			System.out.println("点击！");
			if (getAccount().equals("") || getPassword().equals(""))
				JOptionPane.showMessageDialog(null, "帐号和密码不能为空！", "错误！",JOptionPane.PLAIN_MESSAGE);
			else
				handler.login();
		}
	}

	public String getAccount() {
		return accountInput.getText().trim();
	}

	public String getPassword() {
		return String.valueOf(pwInput.getPassword()).trim();
	}

	public void showLoginError() {
		JOptionPane.showMessageDialog(null, "登录失败！帐号或密码错误！", "错误！",JOptionPane.PLAIN_MESSAGE);
	}

    @Override
    public void focusGained(FocusEvent e) {
        if (e.getSource() == accountInput && accountInput.getText().equals("账号")) {
            accountInput.setText("");
            accountInput.setForeground(Color.BLACK);
        } else if (e.getSource() == pwInput && pwInput.getText().equals("密码")) {
            pwInput.setText("");
            pwInput.setForeground(Color.BLACK);
            pwInput.setEchoChar('*');
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (e.getSource() == accountInput && accountInput.getText().equals("")) {
            accountInput.setText("账号");
            accountInput.setForeground(Color.GRAY);
        } else if (e.getSource() == pwInput && pwInput.getText().equals("")) {
            pwInput.setText("密码");
            pwInput.setForeground(Color.GRAY);
            pwInput.setEchoChar('\0');
        }
    }
}
