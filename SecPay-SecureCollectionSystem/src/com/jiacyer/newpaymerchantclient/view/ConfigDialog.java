package com.jiacyer.newpaymerchantclient.view;

import com.jiacyer.newpaymerchantclient.utils.ConfigUtil;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConfigDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = -1748804611563472326L;
	private final JPanel contentPanel = new JPanel();
	private JFrame frame;
	private JButton okButton;
	private JButton cancelButton;
	private JTextField serverText;

	public ConfigDialog(JFrame owner) {
		frame = owner;
		initConfigDialogView();
		setConfigTextView();
	}

	private void initConfigDialogView() {
		Font font = new Font("黑体",Font.BOLD,30);
		Font font1 = new Font("黑体",Font.PLAIN,20);

		setTitle("配置");
		setBounds(frame.getX()+100, frame.getY()+100, 600, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("保存");
				okButton.setActionCommand("OK");
				okButton.addActionListener(this);
				okButton.setFont(font1);
				okButton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
				okButton.setPreferredSize(new Dimension(100,30));
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton = new JButton("取消");
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(this);
				cancelButton.setFont(font1);
				cancelButton.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
				cancelButton.setPreferredSize(new Dimension(100,30));
				buttonPane.add(cancelButton);
			}
		}
		
		{
			JPanel centerPane = new JPanel();
			centerPane.setLayout(new GridLayout(2,1));
			getContentPane().add(centerPane, BorderLayout.CENTER);
			JLabel serverLabel = new AlphaLabel(" 请输入服务器IP：");
			serverLabel.setForeground(Color.BLACK);
			serverText = new JTextField(30);
			JPanel secondLine = new JPanel();

			serverLabel.setFont(font);
			serverText.setFont(font);
			secondLine.add(serverText);
			centerPane.add(serverLabel);
			centerPane.add(secondLine);
		}
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	
	private void setConfigTextView() {
		ConfigUtil configUtil = ConfigUtil.getInstance();
		serverText.setText(configUtil.getDefaultServer());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==okButton) {
			System.out.println("okBtton is clicked!");
			ConfigUtil configUtil = ConfigUtil.getInstance();
			configUtil.setDefaultServer(serverText.getText());
			configUtil.saveConfig();
			dispose();
		} else if(e.getSource()==cancelButton) {
			System.out.println("cancelButton is clicked!");
			dispose();
		}
	}

}
