package com.nativelibs4java.opencl.util;
import com.nativelibs4java.opencl.CLAbstractUserProgram;
import com.nativelibs4java.opencl.CLBuffer;
import com.nativelibs4java.opencl.CLBuildException;
import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLEvent;
import com.nativelibs4java.opencl.CLKernel;
import com.nativelibs4java.opencl.CLProgram;
import com.nativelibs4java.opencl.CLQueue;
import java.io.IOException;
/** Wrapper around the OpenCL program LinearAlgebraKernels */
public class LinearAlgebraKernels extends CLAbstractUserProgram {
	public LinearAlgebraKernels(CLContext context) throws IOException {
		super(context, readRawSourceForClass(LinearAlgebraKernels.class));
	}
	public LinearAlgebraKernels(CLProgram program) throws IOException {
		super(program, readRawSourceForClass(LinearAlgebraKernels.class));
	}
	CLKernel mulMatDouble_kernel;
	public synchronized CLEvent mulMatDouble(CLQueue commandQueue, CLBuffer<Double > a, int aColumns, CLBuffer<Double > b, int bColumns, CLBuffer<Double > c, int globalWorkSizes[], int localWorkSizes[], CLEvent... eventsToWaitFor) throws CLBuildException {
		if ((mulMatDouble_kernel == null)) 
			mulMatDouble_kernel = createKernel("mulMatDouble");
		mulMatDouble_kernel.setArgs(a, aColumns, b, bColumns, c);
		return mulMatDouble_kernel.enqueueNDRange(commandQueue, globalWorkSizes, localWorkSizes, eventsToWaitFor);
	}
	CLKernel mulVecDouble_kernel;
	public synchronized CLEvent mulVecDouble(CLQueue commandQueue, CLBuffer<Double > a, int aColumns, CLBuffer<Double > b, int bSize, CLBuffer<Double > c, int globalWorkSizes[], int localWorkSizes[], CLEvent... eventsToWaitFor) throws CLBuildException {
		if ((mulVecDouble_kernel == null)) 
			mulVecDouble_kernel = createKernel("mulVecDouble");
		mulVecDouble_kernel.setArgs(a, aColumns, b, bSize, c);
		return mulVecDouble_kernel.enqueueNDRange(commandQueue, globalWorkSizes, localWorkSizes, eventsToWaitFor);
	}
	CLKernel transposeDouble_kernel;
	public synchronized CLEvent transposeDouble(CLQueue commandQueue, CLBuffer<Double > a, int aRows, int aColumns, CLBuffer<Double > out, int globalWorkSizes[], int localWorkSizes[], CLEvent... eventsToWaitFor) throws CLBuildException {
		if ((transposeDouble_kernel == null)) 
			transposeDouble_kernel = createKernel("transposeDouble");
		transposeDouble_kernel.setArgs(a, aRows, aColumns, out);
		return transposeDouble_kernel.enqueueNDRange(commandQueue, globalWorkSizes, localWorkSizes, eventsToWaitFor);
	}
	CLKernel mulMatFloat_kernel;
	public synchronized CLEvent mulMatFloat(CLQueue commandQueue, CLBuffer<Float > a, int aColumns, CLBuffer<Float > b, int bColumns, CLBuffer<Float > c, int globalWorkSizes[], int localWorkSizes[], CLEvent... eventsToWaitFor) throws CLBuildException {
		if ((mulMatFloat_kernel == null)) 
			mulMatFloat_kernel = createKernel("mulMatFloat");
		mulMatFloat_kernel.setArgs(a, aColumns, b, bColumns, c);
		return mulMatFloat_kernel.enqueueNDRange(commandQueue, globalWorkSizes, localWorkSizes, eventsToWaitFor);
	}
	CLKernel mulVecFloat_kernel;
	public synchronized CLEvent mulVecFloat(CLQueue commandQueue, CLBuffer<Float > a, int aColumns, CLBuffer<Float > b, int bSize, CLBuffer<Float > c, int globalWorkSizes[], int localWorkSizes[], CLEvent... eventsToWaitFor) throws CLBuildException {
		if ((mulVecFloat_kernel == null)) 
			mulVecFloat_kernel = createKernel("mulVecFloat");
		mulVecFloat_kernel.setArgs(a, aColumns, b, bSize, c);
		return mulVecFloat_kernel.enqueueNDRange(commandQueue, globalWorkSizes, localWorkSizes, eventsToWaitFor);
	}
	CLKernel transposeFloat_kernel;
	public synchronized CLEvent transposeFloat(CLQueue commandQueue, CLBuffer<Float > a, int aRows, int aColumns, CLBuffer<Float > out, int globalWorkSizes[], int localWorkSizes[], CLEvent... eventsToWaitFor) throws CLBuildException {
		if ((transposeFloat_kernel == null)) 
			transposeFloat_kernel = createKernel("transposeFloat");
		transposeFloat_kernel.setArgs(a, aRows, aColumns, out);
		return transposeFloat_kernel.enqueueNDRange(commandQueue, globalWorkSizes, localWorkSizes, eventsToWaitFor);
	}
}
