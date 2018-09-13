package com.jiacyer.newpaymerchantclient.view;

import com.google.zxing.WriterException;
import com.jiacyer.newpaymerchantclient.handler.MainHandler;
import com.jiacyer.newpaymerchantclient.utils.ChangeImageSize;
import com.jiacyer.newpaymerchantclient.utils.QRCodeFactory;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Pattern;

public class MainView extends JFrame implements ActionListener, ItemListener, DocumentListener, MouseListener {

	private JLabel qrcodeImage;
	private EditView classEdit;
	private EditView amountEdit;
	private EditView codeEdit;
	private JButton submitButton;
	private JButton updateCodeButton;
	private JButton logoutButton;
	private JComboBox<String> contentComboBox;
	private JLabel usrTitle;
	private JLabel locTitle;
	private JLabel bottomLabel;
	private ConfigDialog configDialog;
	private ImageIcon loadingGif;

	private BufferedImage bufferedImage;

	private MainHandler handler;

	private HashMap<String, String> datas;

	public MainView() {
		this.setSize(1500,950);
		this.setLocationRelativeTo(null);
		this.setTitle("SecPay 收款系统");
        this.setIconImage(Toolkit.getDefaultToolkit().createImage(MainView.class.getResource("/com/jiacyer/newpaymerchantclient/image/logo.png")));
        this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
		initData();
		initView();
		handler = MainHandler.getInstance();
	}

	public MainView(String publicKey) {
		this();
		updatePublicKeyQRCode(publicKey);
	}

	private void initData() {
		datas = new HashMap<>();
		datas.put("零食", "饮食");
		datas.put("水果", "饮食");
		datas.put("中餐", "饮食");
        datas.put("西餐", "饮食");
        datas.put("娘惹菜", "饮食");
        datas.put("火锅", "饮食");
        datas.put("书籍", "文教娱乐");
        datas.put("文具", "文教娱乐");
        datas.put("计算器", "文教娱乐");
        datas.put("抽纸", "生活用品");
        datas.put("沐浴露", "生活用品");
        datas.put("洗发水", "生活用品");
		datas.put("短袖", "服饰美容");
        datas.put("衬衣", "服饰美容");
        datas.put("牛仔裤", "服饰美容");
		datas.put("话费充值", "通讯");
		datas.put("流量充值", "通讯");
		datas.put("其他", "其他");
	}

	private void initView() {
		Image image = Toolkit.getDefaultToolkit().createImage(MainView.class.getResource("/com/jiacyer/newpaymerchantclient/image/loading.gif"));
        loadingGif = new ImageIcon(image);

		JLabel title_name = new AlphaLabel("SecPay ");
        title_name.setFont((new Font("Bauhaus 93",Font.BOLD,60)));
        try {
            BufferedImage bi = ImageIO.read(MainView.class.getResourceAsStream("/com/jiacyer/newpaymerchantclient/image/logo_with_name.png"));
            Image logo = ChangeImageSize.scale(bi, 4, false);
            title_name.setIcon(new ImageIcon(logo));
        } catch (IOException e) {
            e.printStackTrace();
        }
        JLabel title_system = new AlphaLabel("收款系统");
        title_system.setFont((new Font("黑体",Font.BOLD,45)));

		usrTitle = new AlphaLabel("尊敬的 XXX 用户，你好！");
        usrTitle.setFont((new Font("微软雅黑",Font.BOLD,20)));
        locTitle = new AlphaLabel("您的店铺位于：XXX");
        locTitle.setFont((new Font("微软雅黑",Font.BOLD,20)));
        JLabel leftBlackTitle = new AlphaLabel(" ");
        JLabel rightBlackTitle = new AlphaLabel(" ");
        leftBlackTitle.setPreferredSize(new Dimension(50, 10));
        rightBlackTitle.setPreferredSize(new Dimension(100, 10));

        JLabel contentLabel = new AlphaLabel("商品内容：");
		JLabel classLabel = new AlphaLabel("商品类型：");
		JLabel amountLabel = new AlphaLabel("总金额：");
		JLabel codeLabel = new AlphaLabel("付款码：");
		contentComboBox = new JComboBox<>();
		contentComboBox.setFont(new Font("黑体",Font.BOLD,30));
		contentComboBox.addItemListener(this);
		classEdit = new EditView();
		classEdit.setEnabled(false);
		amountEdit = new EditView();
		Document document = amountEdit.getDocument();
		document.addDocumentListener(this);
		codeEdit = new EditView();
		submitButton = new JButton("收 款");
		submitButton.setFont(new Font("微软雅黑",Font.BOLD,30));
		submitButton.addActionListener(this);
        submitButton.setForeground(Color.WHITE);
        submitButton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		submitButton.setPreferredSize(new Dimension(500,60));

		Set<String> keySet = datas.keySet();
		for (String data : keySet) {
			contentComboBox.addItem(data);
		}

        logoutButton = new JButton("登出");
        logoutButton.setFont(new Font("微软雅黑",Font.BOLD,20));
        logoutButton.addActionListener(this);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
        logoutButton.setPreferredSize(new Dimension(100,30));

		qrcodeImage = new AlphaLabel();
		updatePublicKeyQRCode("欢迎使用本系统");
		JLabel qrcodeTitle = new AlphaLabel("POS机身份信息二维码↑↑↑");
		qrcodeTitle.setFont(new Font("黑体",Font.BOLD,30));
		updateCodeButton = new JButton("更新POS机身份信息");
		updateCodeButton.setFont(new Font("微软雅黑",Font.BOLD,30));
		updateCodeButton.addActionListener(this);
		updateCodeButton.setForeground(Color.WHITE);
		updateCodeButton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		updateCodeButton.setPreferredSize(new Dimension(400,60));

		bottomLabel = new AlphaLabel("版权所有 © 2018 | Power by SecPay");
		bottomLabel.setFont(new Font("微软雅黑",Font.BOLD,15));
		bottomLabel.addMouseListener(this);

		JLabel blackLabel = new AlphaLabel("　");
		blackLabel.setPreferredSize(new Dimension(100, 40));
		JLabel leftWestLabel = new AlphaLabel("　");
		leftWestLabel.setPreferredSize(new Dimension(60, 100));

		JPanel mainPanel = new BackgroundPanel(new BorderLayout(), false);
		JPanel contentPanel = new NormalPanel(new GridLayout(1,2));
		JPanel leftCenterPanel = new NormalPanel(new GridLayout(10, 1));
		JPanel rightPanel = new NormalPanel(new BorderLayout());
		JPanel bottom = new NormalPanel(new FlowLayout());
		JPanel imagePanel = new NormalPanel(new FlowLayout());
		JPanel rightBottomPanelDown = new NormalPanel(new FlowLayout());
        JPanel titlePanel = new NormalPanel(new BorderLayout());
        JPanel titleCenterPanel = new NormalPanel(new FlowLayout());
        JPanel titleSouthPanel = new NormalPanel(new GridLayout(1, 2));
        JPanel titleSouthLeftPanel = new NormalPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel titleSouthRightPanel = new NormalPanel(new FlowLayout(FlowLayout.RIGHT));
        JPanel rightBottomPanel = new NormalPanel(new GridLayout(2,1));
		JPanel rightBottomPanelUp = new NormalPanel(new FlowLayout());
		JPanel button = new NormalPanel(new FlowLayout());
		JPanel leftPanel = new NormalPanel(new BorderLayout());
        JPanel leftNorthPanel = new NormalPanel(new FlowLayout(FlowLayout.RIGHT));

        button.add(submitButton);

		leftCenterPanel.add(contentLabel);
		leftCenterPanel.add(contentComboBox);
		leftCenterPanel.add(classLabel);
		leftCenterPanel.add(classEdit);
		leftCenterPanel.add(amountLabel);
		leftCenterPanel.add(amountEdit);
		leftCenterPanel.add(codeLabel);
		leftCenterPanel.add(codeEdit);
		leftCenterPanel.add(new AlphaLabel("　"));
		leftCenterPanel.add(button);

		leftPanel.add("Center", leftCenterPanel);
		leftPanel.add("West", leftWestLabel);

		leftNorthPanel.add(logoutButton);
        leftNorthPanel.add(blackLabel);

		imagePanel.add(qrcodeImage);
		rightBottomPanel.add(qrcodeTitle);
		rightBottomPanelUp.add(qrcodeTitle);
		rightBottomPanelDown.add(updateCodeButton);
		rightBottomPanel.add(rightBottomPanelUp);
		rightBottomPanel.add(rightBottomPanelDown);

		rightPanel.add("North", leftNorthPanel);
		rightPanel.add("Center", imagePanel);
		rightPanel.add("South", rightBottomPanel);
		bottom.add(bottomLabel);

		titleCenterPanel.add(title_name);
		titleCenterPanel.add(title_system);
		titleSouthLeftPanel.add(leftBlackTitle);
        titleSouthLeftPanel.add(usrTitle);
        titleSouthRightPanel.add(locTitle);
        titleSouthRightPanel.add(rightBlackTitle);
        titleSouthPanel.add(titleSouthLeftPanel);
        titleSouthPanel.add(titleSouthRightPanel);
		titlePanel.add("Center", titleCenterPanel);
		titlePanel.add("South", titleSouthPanel);

		contentPanel.add(leftPanel);
		contentPanel.add(rightPanel);
		mainPanel.add("North", titlePanel);
		mainPanel.add("Center", contentPanel);
		mainPanel.add("South", bottom);
		this.add(mainPanel);
		mainPanel.updateUI();
	}

	public void updatePublicKeyQRCode(String publicKey) {
		try {
			bufferedImage = new QRCodeFactory().CreateQrBufferedImage(publicKey,null,500);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriterException e) {
			e.printStackTrace();
		}
//		Image image = Toolkit.getDefaultToolkit().createImage("./qrcode.jpg");
		qrcodeImage.setIcon(new ImageIcon(bufferedImage));
//		File imageFile = new File("./qrcode.jpg");
//		if (imageFile.exists())
//			imageFile.delete();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == submitButton && getPayAmount() != 0 && getPayCode() != null) {
			handler.newPayRequest();
		} else if (e.getSource() == updateCodeButton) {
			qrcodeImage.setIcon(loadingGif);
			handler.updatePublicKey();
		} else if (e.getSource() == logoutButton) {
		    handler.logout(false);
        }
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		String key = (String) e.getItem();
		String value = datas.get(key);
		if (value != null)
			classEdit.setText(value);
		else
			classEdit.setText("其他");
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		String amount = amountEdit.getText();
		if (!isDouble(amount)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					String s = amount.substring(0, amount.length()-1);
					amountEdit.setText(s);
					amountEdit.setCaretPosition(s.length());
				}
			}).start();
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {

	}

	@Override
	public void changedUpdate(DocumentEvent e) {

	}

	private static boolean isDouble(String str) {
		Pattern pattern = Pattern.compile("^[\\d]*\\.?[\\d]{0,2}$");
		return pattern.matcher(str).matches();
	}

	public String getPayContent() {
		return ((String)contentComboBox.getSelectedItem()).trim();
	}

	public String getPayClass() {
		String key = (String) contentComboBox.getSelectedItem();
		if (key == null) {
			JOptionPane.showMessageDialog(null, "无效的商品内容！", "错误！",JOptionPane.PLAIN_MESSAGE);
			return "";
		} else
			return datas.get(key).trim();
	}

	public double getPayAmount() {
		String s = amountEdit.getText();
		if (s == null || s.equals("")) {
			JOptionPane.showMessageDialog(null, "尚未输入商品金额！", "错误！",JOptionPane.PLAIN_MESSAGE);
			return 0;
		} else
			return  Double.valueOf(s);
	}

	public String getPayCode() {
		String s = codeEdit.getText();
		if (s == null || s.equals("")) {
			JOptionPane.showMessageDialog(null, "尚未输入付款码！", "错误！",JOptionPane.PLAIN_MESSAGE);
			return null;
		} else
			return s.trim();
	}

	public void showPaymentSucceed() {
		JOptionPane.showMessageDialog(null, "付款成功！", "系统消息",JOptionPane.PLAIN_MESSAGE);
		amountEdit.setText("");
		codeEdit.setText("");
	}

	public void showErrorPaymentWarningMsg() {
		JOptionPane.showMessageDialog(null, "付款码异常，请再次付款！", "系统消息",JOptionPane.ERROR_MESSAGE);
		codeEdit.setText("");
	}

	public void showMoneyNotEnough() {
		JOptionPane.showMessageDialog(null, "账户余额不足，请使用其他支付方式！", "系统消息",JOptionPane.WARNING_MESSAGE);
		amountEdit.setText("");
		codeEdit.setText("");
	}

	public void showStatusError() {
		JOptionPane.showMessageDialog(null, "登陆状态异常，请重新登陆！", "系统消息",JOptionPane.PLAIN_MESSAGE);
	}

    public void setMerchantInfo(String usrName, String usrLoc) {
	    String name = usrTitle.getText();
	    name = name.replace("XXX", usrName);
	    String loc = locTitle.getText();
	    loc = loc.replace("XXX", usrLoc);
	    usrTitle.setText(name);
	    locTitle.setText(loc);
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
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (e.getSource() == bottomLabel) {
			bottomLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
}
