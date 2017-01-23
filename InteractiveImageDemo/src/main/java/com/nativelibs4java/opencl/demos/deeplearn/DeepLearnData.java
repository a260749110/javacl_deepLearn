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

	public double[] getPa() {
		return pa;
	}

	public void setPa(double[] pa) {
		this.pa = pa;
	}

	public double[] getPb() {
		return pb;
	}

	public void setPb(double[] pb) {
		this.pb = pb;
	}

	public double[] getPc() {
		return pc;
	}

	public void setPc(double[] cc) {
		this.pc = cc;
	}

	public double[] getpD() {
		return pd;
	}

	public void setpD(double[] pD) {
		this.pd = pD;
	}

	public double getResultPer() {
		return resultPer;
	}

	public void setResultPer(double resultPer) {
		this.resultPer = resultPer;
	}

	public double getSuccessPer() {
		return successPer;
	}

	public void setSuccessPer(double successPer) {
		this.successPer = successPer;
	}

	private double[] pa = new double[Config.SAMPLING_SIZE];
	private double[] pb = new double[Config.SAMPLING_SIZE];
	private double[] pc = new double[Config.SAMPLING_SIZE];
	private double[] pd = new double[Config.SAMPLING_SIZE];


	private double resultPer;// 结果占比

	private double successPer;// 成功占比
	@JSONField(serialize = false)
	public static int size =  Config.SAMPLING_SIZE;

	public void fillFloatPd(List<Double> list) {
		for (int i = 0; i < pd.length; i++) {
			list.add(pd[i]);
		}

	}
	public void fillFloatPa(List<Double> list) {
		for (int i = 0; i < pd.length; i++) {
			list.add(pa[i]);
		}

	}
	public void fillFloatPb(List<Double> list) {
		for (int i = 0; i < pd.length; i++) {
			list.add(pb[i]);
		}

	}
	public void fillFloatPc(List<Double> list) {
		for (int i = 0; i < pd.length; i++) {
			list.add(pc[i]);
		}

	}
	public DeepLearnData() {
	}

	public DeepLearnData(DeepLearnData data) {
		this.pa = new double[data.pd.length];
		this.pb = new double[data.pd.length];
		this.pc = new double[data.pd.length];
		this.pd = new double[data.pd.length];
		System.arraycopy(data.pa, 0, this.pa, 0, data.pd.length);
		System.arraycopy(data.pb, 0, this.pb, 0, data.pd.length);
		System.arraycopy(data.pc, 0, this.pc, 0, data.pd.length);
		System.arraycopy(data.pd, 0, this.pd, 0, data.pd.length);
		this.successPer = data.successPer;
		this.resultPer = data.resultPer;
	}

	public DeepLearnData(DeeplearnResultPo po) {

		
		
		JSONArray jl = new JSONArray(po.getPd());
		pd = new double[jl.length()];
		for (int i = 0; i < jl.length(); i++) {
			pa[i] = po.getPa();
			pb[i] = po.getPb();
			pc[i] = po.getPc();
			pd[i] = (double) jl.getDouble(i);
		}
		setSuccessPer(69999999999f);
	}
}
