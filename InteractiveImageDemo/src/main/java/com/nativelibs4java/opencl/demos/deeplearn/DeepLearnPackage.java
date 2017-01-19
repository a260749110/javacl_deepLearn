package com.nativelibs4java.opencl.demos.deeplearn;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bridj.Pointer;
import org.json.JSONArray;

import com.deeplearn.service.DeeplearnService;
import com.deeplearn.utils.AppContextUtil;
import com.nativelibs4java.opencl.CLBuffer;
import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLEvent;
import com.nativelibs4java.opencl.CLKernel;
import com.nativelibs4java.opencl.CLMem.Usage;
import com.nativelibs4java.opencl.CLQueue;
import com.sql.dto.DeepLearnValueDto;
import com.sql.po.DataBasePo;
import com.sql.po.DeeplearnResultPo;

public class DeepLearnPackage {
	private float[] dataEndList; //每天收盤價格
	private float[] dataTurnoverList;//每天成交量
	private float[] successList;
	private float[] parameterListDouble;
	private int size;
	private List<DeepLearnData> parameterList;
	private DeeplearnResultPo deeplearnResultPo;

	private int id;
	private DataBasePo firstPo;
	private int dataSize=0;
	private float successPro;
	private int successSize;
	private Random random=new Random();
	private int resultCellSize=3;
	private DeepLearnValueDto deepLearnValueDto;
	public DeepLearnPackage(int id) {
		this.id = id;
		deepLearnValueDto=DeepLearnValueDto.readFromSql(id);
		List<DataBasePo> basePos = AppContextUtil.getDeeplearnService().findAllDataBaseById(id);
		size = Config.CalculaSize;
		parameterList = new ArrayList<>();
		firstPo = basePos.get(0);
		initPo();
		initDataBase(basePos);
		initDeepLearnData();
		
	}
	private CLBuffer<Float> resultListArgs;
	public void setCLKernel(CLKernel clKernel,CLContext context)
	{
		CLBuffer<Float> dataEndListArg=null;
		CLBuffer<Float> dataTurnoverListArg=null;
		CLBuffer<Float> successListArg=null;
		CLBuffer<Float> parameterListArgs=null;
	
		FloatBuffer dataEndListBuffer= FloatBuffer.wrap(dataEndList);
		FloatBuffer dataTurnoverListBuffer= FloatBuffer.wrap(dataTurnoverList);
		FloatBuffer successListBuffer= FloatBuffer.wrap(successList);
		List<Float> dataList=new ArrayList<Float>();
		for (int i = 0; i < parameterList.size(); i++) {
			parameterList.get(i).fillFloat(dataList);
		}
		 parameterListDouble=new float[dataList.size()];
		for (int i = 0; i < parameterListDouble.length; i++) {
			parameterListDouble[i]=dataList.get(i);
		}
		FloatBuffer parameterListBuffer= FloatBuffer.wrap(parameterListDouble);	
		dataEndListArg=context.createFloatBuffer(Usage.InputOutput, dataEndListBuffer,true);
		dataTurnoverListArg=context.createFloatBuffer(Usage.InputOutput, dataTurnoverListBuffer,true);
		successListArg=context.createFloatBuffer(Usage.InputOutput, successListBuffer,true);
		parameterListArgs=context.createFloatBuffer(Usage.InputOutput, parameterListBuffer,true);
		resultListArgs=context.createFloatBuffer(Usage.InputOutput,resultCellSize*size);
		// dataEndListArg , dataTurnoverListArg,successListArg,dataSize,parameterListArgs ,resultListArgs,resultSize
		clKernel.setArgs(dataEndListArg,dataTurnoverListArg,successListArg,dataSize,Config.SAMPLING_SIZE,parameterListArgs,DeepLearnData.size,resultListArgs,resultCellSize);
	}
	public void tryCalculate(int x)
	{
		tryIndex=x;
		float success=0;
		float successCount=0.0f;
		for(int i=Config.SAMPLING_SIZE-1;i<dataSize-Config.SAMPLING_SIZE;i++)
		{	
			float result =calculation(dataEndList, dataTurnoverList,
		successList, dataSize,Config.SAMPLING_SIZE,parameterListDouble, DeepLearnData.size,
	 		null,resultCellSize,i,x);
			if(result>1)
			{ 
				success+=1;
				if(successList[i]>1)
				{
					successCount+=1;
				}
			}
		
			
		}
		System.err.println("try Result:"+success+"  "+successCount+"   "+successPro+"  "+successSize);
	}
	
	private float calculation( float[] dataEndListArg, float[] dataTurnoverListArg,
			  float[] successListArg,int dataSize,int samplingSize, float[] parameterListArgs,int parameterCellSize,
				 float[] resultListArgs,int resultCellSize,int i,int x)
				{
				float result=0;

				for(int j=samplingSize-1;j>=0;j--)
				{
			
				
					 result+=parameterListArgs[parameterCellSize*x+3+j]*(parameterListArgs[parameterCellSize*x]*dataEndListArg[i-j]+
					parameterListArgs[parameterCellSize*x+1]*dataTurnoverListArg[i-j]+
					parameterListArgs[parameterCellSize*x+2]);
				
				  
				}	
				return result;
				}
	private int tryIndex=0;
	public void readResult(CLQueue queue,CLEvent clEvent  )
	{
		Pointer<Float> fp=resultListArgs.read(queue, clEvent);
		
	clEvent.waitFor();
	List<Float> result=fp.asList();
	for (int i = 0; i < result.size(); i++) {
		if( i==tryIndex*resultCellSize)
		{
		System.err.println(result.get(i));
		System.err.println(result.get(i+1));
		}
	}
	
	deepLearnValueDto.refresh(result,size,resultCellSize , 1, 0, parameterList,successSize, dataEndList.length-Config.SAMPLING_SIZE,deeplearnResultPo );
	deepLearnValueDto.tryUpPo(deeplearnResultPo);
	deepLearnValueDto.save();

	
	}
	
	
	private void initDataBase(List<DataBasePo> basePos)
	{
		dataSize=basePos.size();
		int successCount=0;
		dataEndList=new float[basePos.size()-Config.SAMPLING_SIZE];
		dataTurnoverList=new float[basePos.size()-Config.SAMPLING_SIZE];
		successList=new float[basePos.size()-Config.SAMPLING_SIZE];
		for (int i = 0; i < basePos.size()-Config.SAMPLING_SIZE; i++) {
			dataEndList[i]=(float) basePos.get(i).getEnd();
			dataTurnoverList[i]=(float) basePos.get(i).getTurnover();
			if(getFeatureMax(i, basePos)/basePos.get(i).getEnd()>=1.1)
			{
				successList[i]=10f;
				successCount++;
			}
			else
			{
				
				successList[i]=-10f;
				
			}
		}
	
	
		successSize=successCount;
		successPro=((float)successCount)/((float)basePos.size()-Config.SAMPLING_SIZE*2+1);
	
	
	}
	
	private float getFeatureMax(int i,List<DataBasePo> basePos)
	{
		float result=0;
		for (int j = 1; j <= Config.SAMPLING_SIZE; j++) {
			if(result<basePos.get(i+j).getEnd())
			{
				result=(float) basePos.get(i+j).getEnd();
			}
		}
		return result;
	}
	
	public void initDeepLearnData()
	{
		parameterList=new ArrayList<>();
		for (int i = 0; i < Config.CalculaSize; i++) {
			if(random.nextFloat()<Config.NEW_DATA_RATE)
			{
				 crateData();
				 
			}
			else
			{
				DeepLearnData data=deepLearnValueDto.getRandOne(random);
				if(data==null)
				{
					crateData();
				}
				else
				{
					randomLearnData(data);
					parameterList.add(data);
				}
			}
			
		}
	}
	
	public void refresh()
	{
		initDeepLearnData();
		
		
	}
	private  void crateData()
	{
		DeepLearnData data=new DeepLearnData(deeplearnResultPo);
		randomLearnData(data);
		parameterList.add(data);
	}
	private void randomLearnData(DeepLearnData data)
	{
		int size=(Config.SAMPLING_SIZE+3);
		for (int i = 0; i < size; i++) {
			int index= random.nextInt(Config.SAMPLING_SIZE+3);
			if(index==0)
			{
				data.setPa(random(data.getPa(),deeplearnResultPo.getRa() ));
			}
			if(index==1)
			{
				data.setPb(random(data.getPb(),deeplearnResultPo.getRb() ));
			}
			if(index==2)
			{
				data.setPc(random(data.getPc(),deeplearnResultPo.getRc() ));
			}
			if(index>2)
			{
				
				data.getpD()[index-3]=random(data.getpD()[index-3],deeplearnResultPo.getRd() );
		
			}
			
		}
	}
	private float random(float base,float randSize)
	{
		return base+(random.nextFloat()-0.5f)*randSize+base*randSize*(random.nextFloat()-0.5f);
	}
	private void initPo() {
		deeplearnResultPo = AppContextUtil.getDeeplearnService().deeplearnResultDao.findOne(id);

		if (deeplearnResultPo.getPa() <= 0 && deeplearnResultPo.getPb() <= 0 && deeplearnResultPo.getPc() <= 0) {
			fillPo();
		}
	}

	private void fillPo() {
		deeplearnResultPo.setPa((float) (0.3f / firstPo.getEnd()));
		deeplearnResultPo.setPb((float) (0.3f / firstPo.getTurnover()));
		deeplearnResultPo.setPc(0.3f);
		
		JSONArray jl = new JSONArray();
		for (int i = 0; i < Config.SAMPLING_SIZE; i++) {
			jl.put((double) (1f / Config.SAMPLING_SIZE));
			
		}
		deeplearnResultPo.setPd(jl.toString());
	}
}
