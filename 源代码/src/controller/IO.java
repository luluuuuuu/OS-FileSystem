package controller;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Vector;

import com.google.gson.Gson;

import model.sys.Block;
import model.sys.FCB;
import model.ConstValue;
public class IO {

	private Vector<Block> blocks = new Vector<Block>();
	public boolean online = false;

	public IO() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 初始化所有block，从磁盘同步内容
	 */
	public void init() {
		if (this.online) {
			return;
		}

		System.out.println("系统开始初始化...");
		File diskDir = new File("disk");
		boolean diskDirExists = diskDir.exists();
		if (!diskDirExists) {
			System.out.println("!!!物理磁盘上模拟信息不存在");
			System.out.println("在物理磁盘上新建目录...");
			diskDir.mkdirs();
			System.out.println("...在物理磁盘上新建目录完成");
		}

		// 初始化物理块
		System.out.println("初始化物理块...");
		for (int i = 0; i < ConstValue.BLOCK_COUNT; i++) {
			Block block = new Block(i);
			block.sync();
			this.blocks.add(block);
		}
		System.out.println("...物理块初始化完成");
		this.online = true;
		
		// 本地已有文件信息，无需初始化
		if (diskDirExists) {
			return;
		}
		
		// 初始化位图以及根目录
		this.initRootFile();
	}
	
	/**
	 * 初始化位图以及根目录
	 */
	public void initRootFile() {

		// 初始化位图
		System.out.println("初始化位图...");
		byte[] bitMap = new byte[ConstValue.BLOCK_COUNT / 8];
		bitMap[0] = 0x07; // 00000111，0和1号块被位图占用，2号块被根目录FCB占用
		bitMap[ConstValue.SYS_BLOCK_COUNT / 8] |= (1 << (ConstValue.SYS_BLOCK_COUNT % 8)); // 根目录的目录文件，放在256号块

		Gson gson = new Gson();

		// 初始化根目录
		// 根目录FCB，放在2号块，占一个块
		// 根目录目录文件，放在256号块
		System.out.println("初始化根目录...");
		FCB[] rootDir = new FCB[40];
		String rootDirJSON = gson.toJson(rootDir);

		FCB rootDirFCB = new FCB("root",-1,ConstValue.FILE_TYPE.DIRECTORY,ConstValue.FILE_MAX_BLOCKS,ConstValue.SYS_BLOCK_COUNT,2); 
				//new FCB("root", -1, ConstValue.FILE_TYPE.DIRECTORY,ConstValue.FILE_MAX_BLOCKS, ConstValue.SYS_BLOCK_COUNT, 2);
		String rootDirFCBJSON = gson.toJson(rootDirFCB);
		
		// 保存根目录FCB以及根目录目录文件
		this.write(rootDirFCB.blockId, 1, rootDirFCBJSON);
		this.write(ConstValue.SYS_BLOCK_COUNT, rootDirFCB.size, rootDirJSON);
		
		System.out.println("...根目录初始化完成");

		// 保存位图
		this.write(0, bitMap.length / 512 + 1, bitMap);
		System.out.println("...位图初始化完成");

		// 写回物理磁盘
		this.update();
		System.out.println("...系统初始化完成");
	}

	/**
	 * 将所有块的数据写回物理磁盘
	 */
	public void update() {
		System.out.println("系统数据写回物理磁盘...");
		for (int i = 0; i < ConstValue.BLOCK_COUNT; i++) {
			this.blocks.get(i).update();
		}
		System.out.println("...系统数据保存完毕");
	}

	/**
	 * 从Block中读取内容
	 * 
	 * @param startBlockId
	 *            起始的块的编号
	 * @param length
	 *            连续读入的块的个数
	 * @return 返回ByteBuffer，包含所需要的字符串内容
	 */
	public ByteBuffer read(int startBlockId, int length) {
		ByteBuffer resultBuffer = ByteBuffer.allocate(0);

		for (int i = startBlockId; i < startBlockId + length; i++) {
			ByteBuffer currentBinDataBuffer = this.blocks.get(i).getBinData();
			byte[] temp = resultBuffer.array();
			resultBuffer.rewind();
			resultBuffer.get(temp, 0, temp.length);

			// resize
			resultBuffer = ByteBuffer.allocate(temp.length
					+ currentBinDataBuffer.limit());

			// put back
			resultBuffer.put(temp, 0, temp.length);

			// append
			resultBuffer.put(currentBinDataBuffer.array(), 0,
					currentBinDataBuffer.limit());
			
			if (currentBinDataBuffer.limit() < currentBinDataBuffer.capacity()) {
				break;
			}
		}

		resultBuffer.rewind();
		return resultBuffer;
	}

	/**
	 * 向Block中写入字符串内容
	 * 
	 * @param startBlockId
	 *            起始的块的编号
	 * @param length
	 *            连续写入的块的个数
	 * @param content
	 *            写入的内容
	 */
	public void write(int startBlockId, int length, String content) {
		int i = startBlockId;
		int offset = 0;
		int l = Math.min(ConstValue.BLOCK_SIZE, content.length() - offset);
		while (l > 0 && i < startBlockId + length) {
			this.blocks.get(i).wipe(); // 清空该块，从头写起
			this.blocks.get(i).getBinData()
					.put(content.getBytes(ConstValue.CHARSET), offset, l); // 写入数据
			this.blocks.get(i++).getBinData().flip(); // 修改limit，保证刚刚被写入的数据在恰好在有效范围内

			offset += l;
			l = Math.min(ConstValue.BLOCK_SIZE, content.length() - offset);
		}

		// 内容大小小于写入的块，将之后的块清空
		while (i < startBlockId + length) {
			this.blocks.get(i).wipe();
			this.blocks.get(i++).getBinData().flip();
		}
	}

	/**
	 * 向Block中写入byte[]数据
	 * 
	 * @param startBlockId
	 *            起始块的编号
	 * @param length
	 *            连续写入的块的个数
	 * @param content
	 *            需要写入的byte[]数据
	 */
	public void write(int startBlockId, int length, byte[] content) {
		int i = startBlockId;
		int offset = 0;
		int l = Math.min(ConstValue.BLOCK_SIZE, content.length - offset);
		while (l > 0 && i < startBlockId + length) {
			this.blocks.get(i).wipe(); // 清空该块，从头写起
			this.blocks.get(i).getBinData().put(content, offset, l); // 写入数据
			this.blocks.get(i++).getBinData().flip(); // 修改limit，保证刚刚被写入的数据在恰好在有效范围内

			offset += l;
			l = Math.min(ConstValue.BLOCK_SIZE, content.length - offset);
		}

		// 内容大小小于写入的块，将之后的块清空
		while (i < startBlockId + length) {
			this.blocks.get(i).wipe();
			this.blocks.get(i++).getBinData().flip();
		}
	}
}
