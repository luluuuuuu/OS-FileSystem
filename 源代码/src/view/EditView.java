package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import controller.MainController;

import model.sys.FCB;
import model.ConstValue;
// TODO - layout views
public class EditView extends JFrame implements DocumentListener {

	private static final long serialVersionUID = 5359647733388619559L;
	private JTextPane textPane;

	private FCB dataFCB;
	private JMenu exitMenu;
	public boolean saveOnExit = true;
	public boolean edited = false;

	public EditView(FCB fcb, String content) {
		super();

		// 初始化
		this.dataFCB = fcb;
		this.textPane = new JTextPane();

		this.configureMenuBar();
		this.configureTextPane(content);

		// 主界面
		this.configureJFrame();
	}
	
	public void addExitMenuBarListener(ActionListener actionListener)
	{
		this.exitMenu.addActionListener(actionListener);
	}
	
	// 交互
	private void configureJFrame() {
		this.setTitle(this.dataFCB.filename + " - Edit");
		this.setSize(ConstValue.WINDOW_WIDTH,ConstValue.WINDOW_HEIGHT);
																// window
		this.setResizable(false);// 不可更改大小
		this.setLocationRelativeTo(null);// 设置窗口位置
		this.setBackground(Color.WHITE);

		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	private void configureMenuBar() {
		// 元素
		JMenuBar menuBar;
		JMenu fileMenu;
		JMenu helpMenu;
		JMenuItem helpMenuItem;

		// 菜单栏
		menuBar = new JMenuBar();
        final EditView editView = this;
		// 退出菜单
		exitMenu = new JMenu("Exit");
		exitMenu.setMnemonic(KeyEvent.VK_Q);
	    exitMenu.addMenuListener(new MenuListener(){  
		        public void menuSelected(MenuEvent arg0) {
		        	System.out.println("going to close ");
		            // operation here.
		        	if (!edited) {
						// 文本没有变化
		        		editView.dispose();
						return;
					}
					// 获取用户的选择
					int result = JOptionPane.showConfirmDialog(editView,
							"Would you like to SAVE before leaving?", "Exit",
							JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE);

					if (result == 0) {
						// 退出并保存
				        saveOnExit = true;
						editView.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
						editView.dispose();
					} else if (result == 1) {
						// 退出不保存
						saveOnExit = false;
						editView.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
						editView.dispose();
			
					} else {
						// 取消
						editView.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					}
					if (editView.edited && editView.saveOnExit) {
						// 保存文件
						MainController.systemCore().updateFile(
								editView.getDataFCB(), editView.getContent());
					}
		        	
		        }
		        public void menuDeselected(MenuEvent arg0) {
		        }
		        public void menuCanceled(MenuEvent arg0) {
		        }
		    });
	    
		// Build File Menu
		fileMenu = new JMenu("Edit");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		fileMenu.setEnabled(false);
		
		// Build About Menu
		helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
  
		// Add Menu Items to Menu "About"
		helpMenuItem = new JMenuItem("Help", KeyEvent.VK_H);
		helpMenu.add(helpMenuItem);

		// Add Menus "File" to Menu Bar
		menuBar.add(exitMenu);
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);
	
		// Add Components
		this.setJMenuBar(menuBar);
	}

	private void configureTextPane(String content) {
		this.textPane.setText(content);
		this.textPane.getDocument().addDocumentListener(this);
		this.add(this.textPane, BorderLayout.CENTER);
	}

	/**
	 * 获取当前TextPane的内容
	 * @return TextPane中所有文字内容
	 */
	public String getContent() {
		return this.textPane.getText();
	}
	
	public FCB getDataFCB() {
		return dataFCB;
	}

	// 监听文件
	public void insertUpdate(DocumentEvent e) {
		this.edited = true;
		
		// 改变窗口标题
		this.setTitle(this.dataFCB.filename + " - Edited");
	}

	public void removeUpdate(DocumentEvent e) {
		this.edited = true;
		
		// 改变窗口标题
		this.setTitle(this.dataFCB.filename + " - Edited");
	}

	public void changedUpdate(DocumentEvent e) {
		
	}

	
}
