package com.sql.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.deeplearn.service.DeeplearnService;
import com.deeplearn.utils.AppContextUtil;
import com.nativelibs4java.opencl.demos.deeplearn.Config;
import com.nativelibs4java.opencl.demos.deeplearn.DeepLearnData;
import com.nativelibs4java.opencl.demos.deeplearn.DeepLearnPackage;
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
	@JSONField(serialize = false)
	public DeepLearnData miniData;
	public int learnID=1;
	public double successPo=90000;
	public int learnCount=0;
	public boolean resetFlag=false;
	private DeepLearnValueDto() {
	}

	public DeepLearnData getRandOne(Random random) {
		if (getDataList().size() <= 0)
			return null;

		DeepLearnData data2 = getDataList().get(random.nextInt(getDataList().size()));
		DeepLearnData data = new DeepLearnData(data2);
		return data;
	}

	public void refresh(List<Double> result, int size, int cellSize, int indexSuccess, int indexCount,
			List<DeepLearnData> dataBase, int successSize, int sampleSize, DeeplearnResultPo po) {

		for (int i = 0; i < size; i++) {
			DeepLearnData data = dataBase.get(i);
			double readSuccess = result.get(cellSize * i + indexSuccess);
			double befor = data.getSuccessPer();
			double successRate=result.get(cellSize * i + 2);
//		System.err.println(i+" " +readSuccess+"  "+ data.getSuccessPer()+"  "+po.getSuccessPer() +"  ");
			data.setSuccessPer(readSuccess );
			data.setResultPer(successRate);
//			data.setResultPer((readCount / sampleSize) / (Float.valueOf(successSize) / Float.valueOf(sampleSize)));

			if (
//					(data.getSuccessPer()) <= po.getSuccessPer()&&
					(data.getSuccessPer()) <befor ) {
				changeFlag=true;
				 getDataList().add(data);
				System.err.println(" ok :" + data.getSuccessPer() + " bf: " + befor + " po: " + po.getSuccessPer()+" rate:"+successRate);
			}

		}

	}

	public static DeepLearnValueDto readFromSql(int id) {
		DeepLearnValueDto dto = new DeepLearnValueDto();
		dto.setId(id);
		DeeplearnValuePo po = AppContextUtil.getDeeplearnService().deeplearnValueDao.findOne((long)id);
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
		if (getDataList().size() > Config.UP_SIZE) {
			int removeCount=getDataList().size()- Config.UP_SIZE;
			dataList.sort(new Comparator<DeepLearnData>() {

				@Override
				public int compare(DeepLearnData o1, DeepLearnData o2) {
					double result=o1.getSuccessPer()-o2.getSuccessPer();
					return result<0?1:result==0?0:-1;
				}
			});
			List<DeepLearnData> removeList=new ArrayList<>();
			po.setSuccessPer(dataList.get(removeCount).getSuccessPer());
			
			miniData=dataList.get(dataList.size()-1);
			successPo=miniData.getSuccessPer();
			for (int i = 0; i < removeCount; i++) {
				removeList.add(dataList.get(i));
				
			}
			dataList.removeAll(removeList);
		
			AppContextUtil.getDeeplearnService().deeplearnResultDao.save(po);
		}

	
	} 
	
	
	private boolean check(float successPer,float resultPer) {

		List<DeepLearnData> removeList = new ArrayList<>();
		for (DeepLearnData data : getDataList()) {
			if (data.getSuccessPer() > successPer ) {
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
	
	public void save(DeepLearnPackage deepLearnPackage) {
		if(!changeFlag)
			return;
		changeFlag=false;
		DeeplearnValuePo po = new DeeplearnValuePo();
		if(miniData!=null)
		{
		po.setMinValue(miniData.getSuccessPer());
		}
		if(learnCount>=Config.maxLearnSize||resetFlag)
		{
			po.setId(getId()*1000000l+learnID);
			learnID++;
			learnCount=0;
			po.setValue(JSON.toJSONString(this));
			dataList.clear();
			miniData=null;
			FormValue formValue=new FormValue();
			formValue.value=deepLearnPackage.calculateResult;
			formValue.index=deepLearnPackage.dates;
			po.setFormValue(JSON.toJSONString(formValue));
			
		}
		else
		{
			learnCount++;
		po.setId((long)getId());
		po.setValue(JSON.toJSONString(this));
		po.setFormValue(null);
		}
	
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
public static class FormValue implements Serializable
{
public double[] value;
public String[] index;
}
}
