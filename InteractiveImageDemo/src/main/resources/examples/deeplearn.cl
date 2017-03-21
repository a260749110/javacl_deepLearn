/**
	Identity example : simply copy the input image into the output image.
	Written by Olivier Chafik, no right reserved :-) */	

// See http://www.khronos.org/registry/cl/sdk/1.0/docs/man/xhtml/sampler_t.html
	
	
	double calculation(__global double* dataEndListArg,__global double* dataTurnoverListArg,
 	__global double* successListArg,int dataSize,int samplingSize,__global double* parameterListPaArgs,
	__global double* parameterListPbArgs,__global double* parameterListPcArgs,__global double* parameterListPdArgs,
	int parameterCellSize,
__global double* resultListArgs,int resultCellSize,int i,int x)
	{
		double result=0;
		int setX=x*parameterCellSize;
				for(int j=samplingSize-1;j>=0;j--)
				{
			
				
					 result+=parameterListPdArgs[setX+j]*(parameterListPaArgs[setX+j]*dataEndListArg[i-j]+
					parameterListPbArgs[setX+j]*dataTurnoverListArg[i-j]+
					parameterListPcArgs[setX+j]);
				
				 
				}	
				return result;
	}
	
__kernel void test(__global double* dataEndListArg,__global double* dataTurnoverListArg,
 	__global double* successListArg,int dataSize,int samplingSize,__global double* parameterListPaArgs,
	__global double* parameterListPbArgs,__global double* parameterListPcArgs,__global double* parameterListPdArgs,
	int parameterCellSize,
__global double* resultListArgs,int resultCellSize,__global double* calculateResultArgs,int calculateIndex,__global double* calculateSuccessArgs)
{ 
	int x = get_global_id(0);
	double success=0.0f;
	double maybeSuccess=0.0f;
	double maybeSuccessTrue=0.0f;
	double successCount=0.0f;
	double countS=0;

	double[10] beforCells;
	if(x==calculateIndex)
	{
	for(int i=0;i<dataSize;i++)
	{
	calculateResultArgs[i]=0;
	}	
	}
	double resultBefor=0;
	for(int i=samplingSize-1;i<dataSize;i++)
	{	
		double result =calculation(dataEndListArg, dataTurnoverListArg,
	successListArg, dataSize,samplingSize,parameterListPaArgs,parameterListPbArgs,parameterListPcArgs,parameterListPdArgs, parameterCellSize,
 		resultListArgs,resultCellSize,i,x);
			if(x==calculateIndex)
		{
			calculateResultArgs[i]=result;
			}
		if(resultBefor<0&&result>0)
		{
			maybeSuccess+=1.0f;
			for(int j=0;j<10;j++)
			{
				if(successListArg[i+j]>0)
				{
					maybeSuccessTrue+=1.0f;
					break;
				}
			}
		
		}
		resultBefor=result;
		double temp=successListArg[i]-result;

		if(i<dataSize-samplingSize)
		{
		if(successListArg[i]<0&&result>=0)
		{
		success+=temp*temp*1.1;
		countS+=1;
		}
		else if(successListArg[i]>0&&result<=0)
		{
		success+=temp*temp;
		countS+=1;


		}
		else
		{
		success+=temp*temp*0.95;
		countS+=1;
		}
		}
	}
	resultListArgs[x*resultCellSize]=success/countS;
	resultListArgs[x*resultCellSize+1]=maybeSuccess;
		resultListArgs[x*resultCellSize+2]=maybeSuccessTrue/maybeSuccess;

}
