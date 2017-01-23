package com.nativelibs4java.opencl.demos.deeplearn;

import java.nio.DoubleBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bridj.Pointer;
import org.json.JSONArray;

import com.deeplearn.utils.AppContextUtil;
import com.draw.DrawForm;
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
	 double[] dataEndList; // 每天收盤價格
	 double[] dataTurnoverList;// 每天成交量
	 double[] successList;
	 double[] parameterListPaDouble;
	 double[] parameterListPbDouble;
	 double[] parameterListPcDouble;
	 double[] parameterListPdDouble;
	 double[] calculateResult;
 	 String[] dates;
	 public SimpleDateFormat dateFormat=new SimpleDateFormat("YY-MM-DD");
	private int size;

	private List<DeepLearnData> parameterList;
	private DeeplearnResultPo deeplearnResultPo;

	private int id;
	private DataBasePo firstPo;
	private int dataSize = 0;
	private double successPro;
	private int successSize;
	private Random random = new Random();
	private int resultCellSize = 3;
	private DeepLearnValueDto deepLearnValueDto;
	
	public DeepLearnPackage(int id) {
		this.id = id;
		deepLearnValueDto = DeepLearnValueDto.readFromSql(id);
		List<DataBasePo> basePos = AppContextUtil.getDeeplearnService().findAllDataBaseById(id);
		size = Config.CalculaSize;
	
		parameterList = new ArrayList<>();
		firstPo = basePos.get(0);
		initPo();
		initDataBase(basePos);
		initDeepLearnData();

	}

	private CLBuffer<Double> resultListArgs;
	CLBuffer<Double> calculateResultArgs = null;
	public void setCLKernel(CLKernel clKernel, CLContext context) {
		CLBuffer<Double> dataEndListArg = null;
		CLBuffer<Double> dataTurnoverListArg = null;
		CLBuffer<Double> successListArg = null;
		CLBuffer<Double> parameterListPaArgs = null;
		CLBuffer<Double> parameterListPbArgs = null;
		CLBuffer<Double> parameterListPcArgs = null;
		CLBuffer<Double> parameterListPdArgs = null;
	
		DoubleBuffer dataEndListBuffer = DoubleBuffer.wrap(dataEndList);
		DoubleBuffer dataTurnoverListBuffer = DoubleBuffer.wrap(dataTurnoverList);
		DoubleBuffer successListBuffer = DoubleBuffer.wrap(successList);
		List<Double> dataPaList = new ArrayList<Double>();
		List<Double> dataPbList = new ArrayList<Double>();
		List<Double> dataPcList = new ArrayList<Double>();
		List<Double> dataPdList = new ArrayList<Double>();
		for (int i = 0; i < parameterList.size(); i++) {
			parameterList.get(i).fillFloatPa(dataPaList);
			parameterList.get(i).fillFloatPb(dataPbList);
			parameterList.get(i).fillFloatPc(dataPcList);
			parameterList.get(i).fillFloatPd(dataPdList);
		}
		parameterListPaDouble = new double[dataPaList.size()];
		parameterListPbDouble = new double[dataPbList.size()];
		parameterListPcDouble = new double[dataPcList.size()];
		parameterListPdDouble = new double[dataPdList.size()];
		for (int i = 0; i < parameterListPdDouble.length; i++) {
			parameterListPaDouble[i] = dataPaList.get(i);
			parameterListPbDouble[i] = dataPbList.get(i);
			parameterListPcDouble[i] = dataPcList.get(i);
			parameterListPdDouble[i] = dataPdList.get(i);
		}
		DoubleBuffer parameterListPaBuffer = DoubleBuffer.wrap(parameterListPaDouble);
		DoubleBuffer parameterListPbBuffer = DoubleBuffer.wrap(parameterListPbDouble);
		DoubleBuffer parameterListPcBuffer = DoubleBuffer.wrap(parameterListPcDouble);
		DoubleBuffer parameterListPdBuffer = DoubleBuffer.wrap(parameterListPdDouble);
	
		dataEndListArg = context.createDoubleBuffer(Usage.InputOutput, dataEndListBuffer, true);
		dataTurnoverListArg = context.createDoubleBuffer(Usage.InputOutput, dataTurnoverListBuffer, true);
		successListArg = context.createDoubleBuffer(Usage.InputOutput, successListBuffer, true);
		parameterListPaArgs = context.createDoubleBuffer(Usage.InputOutput, parameterListPaBuffer, true);
		parameterListPbArgs = context.createDoubleBuffer(Usage.InputOutput, parameterListPbBuffer, true);
		parameterListPcArgs = context.createDoubleBuffer(Usage.InputOutput, parameterListPcBuffer, true);
		parameterListPdArgs = context.createDoubleBuffer(Usage.InputOutput, parameterListPdBuffer, true);
		resultListArgs = context.createDoubleBuffer(Usage.InputOutput, resultCellSize * size);
		calculateResultArgs = context.createDoubleBuffer(Usage.InputOutput, dataEndList.length);
		// dataEndListArg ,
		// dataTurnoverListArg,successListArg,dataSize,parameterListArgs
		// ,resultListArgs,resultSize
		clKernel.setArgs(dataEndListArg, dataTurnoverListArg, successListArg, dataSize, Config.SAMPLING_SIZE,
				parameterListPaArgs,parameterListPbArgs,parameterListPcArgs,parameterListPdArgs, DeepLearnData.size, resultListArgs, resultCellSize,calculateResultArgs,0);
	}

	public void tryCalculate(int x) {
		tryIndex = x;
		Double success = 0d;
		Double successCount = 0.0d;
		for (int i = Config.SAMPLING_SIZE - 1; i < dataSize - Config.SAMPLING_SIZE; i++) {
			float result = calculation(dataEndList, dataTurnoverList, successList, dataSize, Config.SAMPLING_SIZE,
					parameterListPdDouble, DeepLearnData.size, null, resultCellSize, i, x);
			if (result > 1) {
				success += 1;
				if (successList[i] > 1) {
					successCount += 1;
				}
			}

		}
		System.err.println("try Result:" + success + "  " + successCount + "   " + successPro + "  " + successSize);
	}

	private float calculation(double[] dataEndListArg, double[] dataTurnoverListArg, double[] successListArg, int dataSize,
			int samplingSize, double[] parameterListArgs, int parameterCellSize, double[] resultListArgs,
			int resultCellSize, int i, int x) {
		float result = 0;

		for (int j = samplingSize - 1; j >= 0; j--) {

			result += parameterListArgs[parameterCellSize * x + 3 + j]
					* (parameterListArgs[parameterCellSize * x] * dataEndListArg[i - j]
							+ parameterListArgs[parameterCellSize * x + 1] * dataTurnoverListArg[i - j]
							+ parameterListArgs[parameterCellSize * x + 2]);

		} 
		return result;
	}

	private int tryIndex = 0;

	public void readResult(CLQueue queue, CLEvent clEvent) {
		Pointer<Double> fp = resultListArgs.read(queue, clEvent);

		clEvent.waitFor();
		List<Double> result = fp.asList();
		Pointer<Double> fp1 = calculateResultArgs.read(queue, clEvent);

		clEvent.waitFor();
		List<Double> result1 = fp1.asList();
		calculateResult=new double[result1.size()];
		for (int i = 0; i < result1.size(); i++) {
			calculateResult[i]=result1.get(i);
		}
//		for (int i = 0; i < result.size(); i++) {
//			if (i == tryIndex * resultCellSize) {
//				System.err.println(result.get(i));
//				System.err.println(result.get(i + 1));
//			}
//		}

		deepLearnValueDto.refresh(result, size, resultCellSize, 0, 1, parameterList, successSize,
				dataEndList.length - Config.SAMPLING_SIZE, deeplearnResultPo);
		deepLearnValueDto.tryUpPo(deeplearnResultPo);
		deepLearnValueDto.save();

	}
	double[] minLine;
	private void initDataBase(List<DataBasePo> basePos) {
		dataSize = basePos.size();
		
		dataEndList = new double[basePos.size() ];
		dataTurnoverList = new double[basePos.size() ];
		dates=new String[basePos.size()];
		successList = new double[basePos.size() - Config.SAMPLING_SIZE];
		minLine = new double[basePos.size() - Config.SAMPLING_SIZE];
		for (int i = 0; i < basePos.size() ; i++) {
			dataEndList[i] =  basePos.get(i).getEnd();
			dataTurnoverList[i] =  basePos.get(i).getTurnover()/Config.Turnover_Sort_SIZE;
			dates[i]=dateFormat.format(basePos.get(i).getId().getDate());
			if(i < basePos.size() - Config.SAMPLING_SIZE)
			{
			double temp=getFeatureMax(i, basePos)-basePos.get(i).getEnd()*1.1 ;
			if(temp<0)
			{
				successList[i] =  -Math.sqrt(-(getFeatureMax(i, basePos)-basePos.get(i).getEnd()*1.1 ));
			}
			else
			{
				successList[i] =  Math.sqrt((getFeatureMax(i, basePos)-basePos.get(i).getEnd()*1.1 ));
			}
			
			
			
			minLine[i]=getFeatureMax(i, basePos)-basePos.get(i).getEnd()*1.1 ;
			}

		}

	}

	private double getFeatureMax(int i, List<DataBasePo> basePos) {
		double result = 0;
		for (int j = 1; j <= Config.SAMPLING_SIZE; j++) {
			if (result < basePos.get(i + j).getEnd()) {
				result =  basePos.get(i + j).getEnd();
			}
		}
		return result;
	}

	public void initDeepLearnData() {
		parameterList = new ArrayList<>();
		if(deepLearnValueDto.miniData!=null)
		{
			parameterList.add(new DeepLearnData(deepLearnValueDto.miniData));
		}
		for (int i = 0; parameterList.size() < Config.CalculaSize; i++) {
			if (random.nextFloat() < Config.NEW_DATA_RATE) {
				crateData();

			} else {
				DeepLearnData data = deepLearnValueDto.getRandOne(random);
				if (data == null) {
					crateData();
				} else {
					randomLearnData(data);
					parameterList.add(data);
				}
			}

		}
	}

	public void refresh() {
		initDeepLearnData();

	}

	private void crateData() {
		DeepLearnData data = new DeepLearnData(deeplearnResultPo);
		randomLearnData(data);
		parameterList.add(data);
	}

	private void randomLearnData(DeepLearnData data) {
	
		
		int ra = random.nextInt(Config.SAMPLING_SIZE/2);
		for (int i = 0; i < ra; i++) {
			int randomIndex = random.nextInt(Config.SAMPLING_SIZE);
				data.getPa()[randomIndex] = random(data.getPa()[randomIndex], deeplearnResultPo.getRa());
		}
		int rb = random.nextInt(Config.SAMPLING_SIZE/2);
		for (int i = 0; i < rb; i++) {
			int randomIndex = random.nextInt(Config.SAMPLING_SIZE);
				data.getPb()[randomIndex] = random(data.getPb()[randomIndex], deeplearnResultPo.getRb());
		}
		int rc = random.nextInt(Config.SAMPLING_SIZE/2);
		for (int i = 0; i < rc; i++) {
			int randomIndex = random.nextInt(Config.SAMPLING_SIZE);
				data.getPc()[randomIndex] = random(data.getPc()[randomIndex], deeplearnResultPo.getRc());
		}
		int rd = random.nextInt(Config.SAMPLING_SIZE/2);
		for (int i = 0; i < rd; i++) {
			int randomIndex = random.nextInt(Config.SAMPLING_SIZE);
				data.getpD()[randomIndex] = random(data.getpD()[randomIndex], deeplearnResultPo.getRd());
		}
	}

	private double random(double d, double randSize) {
		return (d + (random.nextGaussian()) * randSize + d * randSize * (random.nextGaussian()));
	}

	private void initPo() {
		deeplearnResultPo = AppContextUtil.getDeeplearnService().deeplearnResultDao.findOne(id);

		if (deeplearnResultPo.getPd()==null||deeplearnResultPo.getPd().isEmpty()) {
			fillPo();
		}
	}

	private void fillPo() {
		deeplearnResultPo.setPa(0);
		deeplearnResultPo.setPb(0);
		deeplearnResultPo.setPc(0);
//		deeplearnResultPo.setPa( (0.3d / firstPo.getEnd()));
//		deeplearnResultPo.setPb( (0.3d / (firstPo.getTurnover()/Config.Turnover_Sort_SIZE)));
//		deeplearnResultPo.setPc(0.3d);

		JSONArray jl = new JSONArray();
		
		for (int i = 0; i < Config.SAMPLING_SIZE; i++) {
			jl.put((1d / Config.SAMPLING_SIZE));

		}
		deeplearnResultPo.setPd(jl.toString());
	}
}
