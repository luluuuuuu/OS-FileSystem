package model.sys;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import model.ConstValue;
public class Block {
	public static final int BLOCK_SIZE = 512;

	private int ID; // id为该块的编号
	private ByteBuffer binData; // binData为该块存储内容的buffer
	private String filePath; // filePath为该块在物理磁盘上*.bin文件的存储路径

	public Block(int mark) {
		super();
		// TODO Auto-generated constructor stub
		this.ID = mark;
		this.binData = ByteBuffer.allocate(ConstValue.BLOCK_SIZE);
		this.filePath = "disk/" + this.ID + ".bin";
	}


	public void wipe() {
		this.binData.clear(); //重置标记，达到清空的效果
	}
	
	public void sync() {
		File binFile = new File(this.filePath);
		if (!binFile.exists()) {
			System.out.println("Warning: \"" + this.filePath + "\" not found");
			return;
		}
		
		FileChannel inputChannel;
		try {
			inputChannel = new FileInputStream(binFile).getChannel();
			inputChannel.read(this.binData);
			this.binData.flip();
			inputChannel.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void update() {
		File binFile = new File(this.filePath);
		
		FileChannel outputChannel;
		try {
			outputChannel = new FileOutputStream(binFile).getChannel();
			this.binData.rewind();
			outputChannel.write(this.binData);
			this.binData.flip();
			outputChannel.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ByteBuffer getBinData() {
		return this.binData;
	}
}
