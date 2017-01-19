/**
	Identity example : simply copy the input image into the output image.
	Written by Olivier Chafik, no right reserved :-) */	

// See http://www.khronos.org/registry/cl/sdk/1.0/docs/man/xhtml/sampler_t.html
	
	
	float calculation(__global float* dataEndListArg,__global float* dataTurnoverListArg,
	 __global float* successListArg,int dataSize,int samplingSize,__global float* parameterListArgs,int parameterCellSize,
	__global float* resultListArgs,int resultCellSize,int i,int x)
	{
		float result=0;

				for(int j=samplingSize-1;j>=0;j--)
				{
			
				
					 result+=parameterListArgs[parameterCellSize*x+32]*(parameterListArgs[parameterCellSize*x]*dataEndListArg[i-j]+
					parameterListArgs[parameterCellSize*x+1]*dataTurnoverListArg[i-j]+
					parameterListArgs[parameterCellSize*x+2]);
				
				 
				}	
				return result;
	}
	
__kernel void test(__global float* dataEndListArg,__global float* dataTurnoverListArg,
 __global float* successListArg,int dataSize,int samplingSize,__global float* parameterListArgs,int parameterCellSize,
__global float* resultListArgs,int resultCellSize)
{ 
	int x = get_global_id(0);
	float success=0.0f;
	float successCount=0.0f;
	
	for(int i=samplingSize-1;i<dataSize-samplingSize;i++)
	{	
		float result =calculation(dataEndListArg, dataTurnoverListArg,
	successListArg, dataSize,samplingSize,parameterListArgs, parameterCellSize,
 		resultListArgs,resultCellSize,i,x);
		if(result>1)
		{
			success+=1;
		if(successListArg[i]>1)
		{
			successCount+=1;
			}
		}
	
	}
	resultListArgs[x*resultCellSize]=success;
	resultListArgs[x*resultCellSize+1]=successCount;

}
 