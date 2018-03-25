package model;

import java.nio.charset.Charset;


public class ConstValue {
	public static final int BLOCK_SIZE = 512;
	public static final int BLOCK_COUNT = 8000;

	public static final int SYS_BLOCK_COUNT = 256;

	public static final int FILE_MAX_BLOCKS = 10;

	public static enum FILE_TYPE {
		FILE, DIRECTORY
	};

	// 窗口
	public static final int WINDOW_WIDTH = 800;
	public static final int WINDOW_HEIGHT = 600;

	// 文件图标
	public static final int FILE_ICON_SIZE = 80;
	public static final int FILE_ICON_PANEL_SIZE = 100;
	public static final Charset CHARSET = Charset.forName("UTF-8");
}
