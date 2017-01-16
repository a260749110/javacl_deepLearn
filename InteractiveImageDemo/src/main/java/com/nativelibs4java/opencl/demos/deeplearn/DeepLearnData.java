package com.nativelibs4java.opencl.demos.deeplearn;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 
 * 函数： y(i)= ∑dn*(ax1(i)+bx2(i)+c); n=(0~Config.SAMPLING_SIZE);
 * 
 * @author Leafy
 *
 */
public class DeepLearnData {
	private Random random = new Random();

	private float a, b, c;
	private float[] pD = new float[Config.SAMPLING_SIZE];
	private float ra, rb, rc, rd;
	private float resultPer;// 结果占比
	private float successPer;// 成功占比
	public int size=9+Config.SAMPLING_SIZE;
	
	
}
