package com.sql.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.deeplearn.service.DeeplearnService;
import com.deeplearn.utils.AppContextUtil;
import com.nativelibs4java.opencl.demos.deeplearn.Config;
import com.nativelibs4java.opencl.demos.deeplearn.DeepLearnData;
import com.sql.po.DeeplearnResultPo;
import com.sql.po.DeeplearnValuePo;
import com.sun.glass.ui.Size;

import sun.awt.AppContext;

public class DeepLearnValueDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<DeepLearnData> dataList = new ArrayList<>();

	private int id;
	private boolean changeFlag=false;
	public DeepLearnValueDto(List<Float> result, int size, int cellSize, int indexSuccess, int indexCount,
			List<DeepLearnData> dataBase, int successSize, int sampleSize, DeeplearnResultPo po, int id) {
		getDataList().clear();
		this.setId(id);
		for (int i = 0; i < size; i++) {
			DeepLearnData data = dataBase.get(i);
			float readSuccess = result.get(cellSize * i + indexSuccess);
			float readCount = result.get(cellSize * i + indexCount);

			data.setSuccessPer(readSuccess / successSize);
			data.setResultPer((readCount / sampleSize) / (Float.valueOf(successSize) / Float.valueOf(sampleSize)));

			if (data.getSuccessPer() >= po.getSuccessPer() && data.getResultPer() <= po.getResultPer()) {
				getDataList().add(data);
				System.err.println(" ok :" + data.getSuccessPer() + "  " + data.getResultPer() + " :  " + sampleSize);
				changeFlag=true;
			}

		}
	}

	private DeepLearnValueDto() {
	}

	public DeepLearnData getRandOne(Random random) {
		if (getDataList().size() <= 0)
			return null;

		DeepLearnData data2 = getDataList().get(random.nextInt(getDataList().size()));
		DeepLearnData data = new DeepLearnData(data2);
		return data;
	}

	public void refresh(List<Float> result, int size, int cellSize, int indexSuccess, int indexCount,
			List<DeepLearnData> dataBase, int successSize, int sampleSize, DeeplearnResultPo po) {

		for (int i = 0; i < size; i++) {
			DeepLearnData data = dataBase.get(i);
			float readSuccess = result.get(cellSize * i + indexSuccess);
			float readCount = result.get(cellSize * i + indexCount);
			float readSuccessRate = readSuccess / successSize;
			float readCountRate = (readCount / sampleSize) / (Float.valueOf(successSize) / Float.valueOf(sampleSize));
//			System.err.println(i+" " +data.getResultPer()+"  "+ data.getSuccessPer()+"  "+ readCountRate+"  "+readSuccessRate+"  "+po.getSuccessPer() +"  "+po.getResultPer());
			data.setSuccessPer(readSuccess / successSize);
			data.setResultPer((readCount / sampleSize) / (Float.valueOf(successSize) / Float.valueOf(sampleSize)));

			if (data.getSuccessPer() >= po.getSuccessPer() && data.getResultPer() <= po.getResultPer()) {
				changeFlag=true;
				 getDataList().add(data);
				System.err.println(" ok :" + data.getSuccessPer() + "  " + data.getResultPer() + " :  " + sampleSize);
			}

		}

	}

	public static DeepLearnValueDto readFromSql(int id) {
		DeepLearnValueDto dto = new DeepLearnValueDto();
		dto.setId(id);
		DeeplearnValuePo po = AppContextUtil.getDeeplearnService().deeplearnValueDao.findOne(id);
		if (po != null) {
			try {
				dto = JSON.parseObject(po.getValue(), DeepLearnValueDto.class);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		dto.setId(id);
		return dto;
	}
	private float upSizeSucess=0.9999f,upSizeResult=1.0001f;
	public void tryUpPo(DeeplearnResultPo po) {
		boolean saveFlag=false;
		while (getDataList().size() > Config.UP_SIZE) {
			float successPer=0;float resultPer=0;
			for (int i = 0; i < dataList.size(); i++) {
				successPer+=dataList.get(i).getSuccessPer();
				resultPer+=dataList.get(i).getResultPer();
						
			}
			successPer/=(float)dataList.size();
			resultPer/=(float)dataList.size();
			po.setSuccessPer(successPer);
			po.setResultPer(resultPer);
			check(successPer, resultPer);
			saveFlag=true;
		}
		if(saveFlag)
		AppContextUtil.getDeeplearnService().deeplearnResultDao.save(po);
	
	} 
	
	
	private boolean check(float successPer,float resultPer) {

		List<DeepLearnData> removeList = new ArrayList<>();
		for (DeepLearnData data : getDataList()) {
			if (data.getSuccessPer() < successPer || data.getResultPer() > resultPer) {
				 {
					
					 {
					removeList.add(data);
					changeFlag=true;
					 }
				}
			}
		}
		 TreeSet<Integer> removeSet=new TreeSet<>();
		 if(getDataList().size()-removeList.size() < Config.UP_SIZE)
		 {
			 Random random=new Random();
			
		for (int i = 0; i < removeList.size()+Config.UP_SIZE-getDataList().size(); i++) {
			int index=random.nextInt(removeList.size());
			while(removeSet.contains(index))
			{
				index=random.nextInt(removeList.size());
			}
			removeSet.add(index);
		}
	
		 }
		 
		for (int i = 0; i < removeList.size(); i++) {
			if(!removeSet.contains(i))
			{
				dataList.remove(removeList.get(i));
			}
		}
		return true;
	}

	public void save() {
		if(!changeFlag)
			return;
		changeFlag=false;
		DeeplearnValuePo po = new DeeplearnValuePo();
		po.setId(getId());
		po.setValue(JSON.toJSONString(this));
		po.setTime(new Timestamp(System.currentTimeMillis()));
	
		AppContextUtil.getDeeplearnService().deeplearnValueDao.save(po);
	}

	public List<DeepLearnData> getDataList() {
		return dataList;
	}

	public void setDataList(List<DeepLearnData> dataList) {
		this.dataList = dataList;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
