package view;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseListener;

import javax.swing.*;

import model.sys.FCB;
import model.ConstValue;
import controller.MainController;

public class MainView extends JFrame {

	private static final long serialVersionUID = 6313156717813295316L;
	private JButton backButton;
	public JTextField addressTextField;
	public JButton goButton;
	public JPanel contentPanel;
	public JLabel pathLabel;
	// 构造函数
	public MainView(FCB[] fcbDir) {
		super();

		// 初始化
		this.contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		this.backButton = new JButton(new ImageIcon("resource/back.png"));
		this.addressTextField = new JTextField("test");
		addressTextField.setFont(new Font("宋体", 0, 20));
		this.goButton = new JButton(new ImageIcon("resource/open.png"));

		// UI界面
		//this.configureMenuBar();
		this.configureIndexPanel();
		this.configureToolPanel();
		this.configureContentScrollPane(fcbDir);

		// Main View
		this.configureJFrame();
	}

	// UI 界面
	private void configureJFrame() {
		this.setTitle("𐂂 Lulu's File System");
		this.setSize(ConstValue.WINDOW_WIDTH,ConstValue.WINDOW_HEIGHT);
																// window
		this.setResizable(false);// 不能更改大小
		this.setLocationRelativeTo(null);
											
		this.setBackground(Color.white);
	}


	private void configureToolPanel() {
		// 初始化工具栏
		JPanel toolPanel = new JPanel();

		// 设置背景色
		toolPanel.setBackground(Color.decode("#B2EBF2"));

		// 设置布局
		toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.X_AXIS));
		toolPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

		// 添加到工具栏
		toolPanel.add(this.backButton);
		toolPanel.add(this.addressTextField);
		toolPanel.add(this.goButton);

		this.add(toolPanel, BorderLayout.PAGE_START); 		// 添加到主界面
	}


    private void configureIndexPanel()
    {
		JPanel indexPanel = new JPanel(); // 初始化路径栏
		indexPanel.setBorder(BorderFactory.createEmptyBorder(15,20,15,100)); // 设置布局
		pathLabel  =new JLabel(MainController.systemCore.getCurrentPath()); // 初始化路径标签
		indexPanel.add(pathLabel); //添加
		indexPanel.setBackground(Color.decode("#ECEFF1"));
		this.add(indexPanel, BorderLayout.WEST);
    }
		
	

	private void configureContentScrollPane(FCB[] fcbDir) {
		// 移除所有components
		this.contentPanel.removeAll();

		// 设置背景色
		this.contentPanel.setBackground(Color.WHITE);

		// 为工具栏添加监听事件
		this.contentPanel.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				System.out.println("resize");

				// TODO Auto-generated method stub

				Dimension d = MainView.this.contentPanel.getPreferredSize();
				int con = MainView.this.contentPanel.getComponents().length;
				int col = ConstValue.WINDOW_WIDTH
						/ (ConstValue.FILE_ICON_PANEL_SIZE + 5);
				int row = con / col + 1;
				int newHeight = row * (ConstValue.FILE_ICON_PANEL_SIZE + 5) + 5;
				d.height = newHeight;
				d.width = ConstValue.WINDOW_WIDTH;
				MainView.this.contentPanel.setPreferredSize(d);
			}

			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub
			}

			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub
			}

		    public void componentHidden(ComponentEvent e){
			  
		    }
		});

		// 测试
		for (int i = 0; i < fcbDir.length; i++) {
			if (fcbDir[i] == null) {
				break;
			}

			DocumentIconPanel t = new DocumentIconPanel(fcbDir[i].type,
					fcbDir[i].filename);

			this.contentPanel.add(t);
		}

		// 初始化目录滚动栏
		System.out.println("initialize contentScrollPane");
		JScrollPane contentScrollPane = new JScrollPane(this.contentPanel);
		contentScrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		// 添加到主界面
		System.out.println("add to mainView");
		this.add(contentScrollPane, BorderLayout.CENTER);
	}

	// 显示界面
	public void showView() {
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	// 活动
	public void addDocument(DocumentIconPanel documentIconPanel) {
		this.contentPanel.add(documentIconPanel);
		this.contentPanel.revalidate();
	}

	public void addRightClickListener(MouseListener rightClickListener) {
		this.contentPanel.addMouseListener(rightClickListener);
	}

	public void addBackButtonActionListener(ActionListener actionListener) {
		this.backButton.addActionListener(actionListener);
	}

	public void addGoButtonActionListener(ActionListener actionListener) {
		this.goButton.addActionListener(actionListener);
	}
	public void addAddressTextFieldActionListener(ActionListener actionListener)
	{
		this.addressTextField.addActionListener(actionListener);
	}

	public void addDocumentIconPanelMouseListener(
			MouseListener documentIconPanelMouseListener) {
		for (Component item : this.contentPanel.getComponents()) {
			((DocumentIconPanel) item)
					.addMouseListener(documentIconPanelMouseListener);
		}
	}

	public void deselectDocuments() {
		for (Component item : this.contentPanel.getComponents()) {
			((DocumentIconPanel) item).setSelected(false);
		}
	}

	public void reloadContent(FCB[] fcbDir) {
		// 移除所有components
		this.contentPanel.removeAll();

		// 重新添加
		for (int i = 0; i < fcbDir.length; i++) {
			if (fcbDir[i] == null) {
				break;
			}
			DocumentIconPanel t = new DocumentIconPanel(fcbDir[i].type,
					fcbDir[i].filename);

			this.contentPanel.add(t);
		}

		this.contentPanel.repaint();
		this.contentPanel.revalidate();
	}
	
}
