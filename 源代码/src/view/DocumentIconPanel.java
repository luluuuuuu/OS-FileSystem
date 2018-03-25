package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.ConstValue;

public class DocumentIconPanel extends JPanel {

	private static final long serialVersionUID = 1952213928294715915L;
	private JLabel mainLabel;
	private String filename;
	private ConstValue.FILE_TYPE type;

	// 构造函数
	public DocumentIconPanel(ConstValue.FILE_TYPE type, String filename) {
		super();
		// TODO Auto-generated constructor stub
		
		this.filename = filename;
		this.type = type;

		this.setSize(new Dimension(ConstValue.FILE_ICON_PANEL_SIZE,
				ConstValue.FILE_ICON_PANEL_SIZE));
		this.setPreferredSize(new Dimension(ConstValue.FILE_ICON_PANEL_SIZE,
				ConstValue.FILE_ICON_PANEL_SIZE));

		// 设置布局
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// 设置背景色
		this.setBackground(Color.WHITE);

		// 交互
		this.configureDocumentIcon(type, filename);
	}


	// 交互
	private void configureDocumentIcon(ConstValue.FILE_TYPE type, String filename) {

		// 初始化图标
		ImageIcon icon = null;
		switch (type) {
		case FILE:
			icon = new ImageIcon("resource/DocumentIcon.png");
			break;
		case DIRECTORY:
			icon = new ImageIcon("resource/FolderIcon.png");
			break;
		default:
			System.out.println("error when load icon");
			break;
		}

		// 图片
		icon.setImage(icon.getImage().getScaledInstance(ConstValue.FILE_ICON_SIZE,
				ConstValue.FILE_ICON_SIZE, Image.SCALE_DEFAULT));

		this.mainLabel = new JLabel(filename, icon, JLabel.CENTER);
		this.mainLabel.setBorder(BorderFactory.createEmptyBorder(0,
				(ConstValue.FILE_ICON_PANEL_SIZE - ConstValue.FILE_ICON_SIZE) / 2, 0,
				(ConstValue.FILE_ICON_PANEL_SIZE - ConstValue.FILE_ICON_SIZE) / 2));
		this.mainLabel.setVerticalTextPosition(JLabel.BOTTOM);
		this.mainLabel.setHorizontalTextPosition(JLabel.CENTER);

		// 添加到文件栏
		this.add(this.mainLabel);
	}

	// 选中图标
	public void setSelected(boolean selected) {
		if (selected) {
			this.setBackground(Color.decode("#CFD8DC"));
			this.mainLabel.setForeground(Color.WHITE);
		} else {
			this.setBackground(Color.WHITE);
			this.mainLabel.setForeground(Color.BLACK);
		}
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public ConstValue.FILE_TYPE getType() {
		return type;
	}

	public void setType(ConstValue.FILE_TYPE type) {
		this.type = type;
	}

}
