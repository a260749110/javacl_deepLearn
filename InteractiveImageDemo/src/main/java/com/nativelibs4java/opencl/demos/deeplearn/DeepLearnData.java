package com.nativelibs4java.opencl.demos.deeplearn;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;

import com.alibaba.fastjson.annotation.JSONField;
import com.sql.po.DataBasePo;
import com.sql.po.DeeplearnResultPo;

/**
 * 
 * 函数： y(i)= ∑dn*(ax1(i)+bx2(i)+c); n=(0~Config.SAMPLING_SIZE);
 * 
 * @author Leafy
 *
 */
public class DeepLearnData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public float getPa() {
		return pa;
	}

	public void setPa(float pa) {
		this.pa = pa;
	}

	public float getPb() {
		return pb;
	}

	public void setPb(float pb) {
		this.pb = pb;
	}

	public float getPc() {
		return pc;
	}

	public void setPc(float cc) {
		this.pc = cc;
	}

	public float[] getpD() {
		return pd;
	}

	public void setpD(float[] pD) {
		this.pd = pD;
	}

	public float getResultPer() {
		return resultPer;
	}

	public void setResultPer(float resultPer) {
		this.resultPer = resultPer;
	}

	public float getSuccessPer() {
		return successPer;
	}

	public void setSuccessPer(float successPer) {
		this.successPer = successPer;
	}

	private float[] pd = new float[Config.SAMPLING_SIZE];
	private float pa, pb, pc;

	private float resultPer;// 结果占比

	private float successPer;// 成功占比
	@JSONField(serialize = false)
	public static int size = 3 + Config.SAMPLING_SIZE;

	public void fillFloat(List<Float> list) {
		list.add(pa);
		list.add(pb);
		list.add(pc);
		for (int i = 0; i < pd.length; i++) {
			list.add(pd[i]);
		}

	}

	public DeepLearnData() {
	}

	public DeepLearnData(DeepLearnData data) {
		this.pa = data.pa;
		this.pb = data.pb;
		this.pc = data.pc;
		this.pd = new float[data.pd.length];
		System.arraycopy(data.pd, 0, this.pd, 0, data.pd.length);
		this.successPer = data.successPer;
		this.resultPer = data.resultPer;
	}

	public DeepLearnData(DeeplearnResultPo po) {

		pa = po.getPa();
		pb = po.getPb();
		pc = po.getPc();
		JSONArray jl = new JSONArray(po.getPd());
		pd = new float[jl.length()];
		for (int i = 0; i < jl.length(); i++) {
			pd[i] = (float) jl.getDouble(i);
		}
	}
}
