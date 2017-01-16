package com.nativelibs4java.opencl.util.fft;
import com.nativelibs4java.opencl.CLAbstractUserProgram;
import com.nativelibs4java.opencl.CLBuffer;
import com.nativelibs4java.opencl.CLBuildException;
import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLEvent;
import com.nativelibs4java.opencl.CLKernel;
import com.nativelibs4java.opencl.CLProgram;
import com.nativelibs4java.opencl.CLQueue;
import java.io.IOException;
/** Wrapper around the OpenCL program FloatDFTProgram */
public class FloatDFTProgram extends CLAbstractUserProgram {
	public FloatDFTProgram(CLContext context) throws IOException {
		super(context, readRawSourceForClass(FloatDFTProgram.class));
	}
	public FloatDFTProgram(CLProgram program) throws IOException {
		super(program, readRawSourceForClass(FloatDFTProgram.class));
	}
	CLKernel dft_kernel;
	public synchronized CLEvent dft(CLQueue commandQueue, CLBuffer<Float > in, CLBuffer<Float > out, int length, int sign, int globalWorkSizes[], int localWorkSizes[], CLEvent... eventsToWaitFor) throws CLBuildException {
		if ((dft_kernel == null)) 
			dft_kernel = createKernel("dft");
		dft_kernel.setArgs(in, out, length, sign);
		return dft_kernel.enqueueNDRange(commandQueue, globalWorkSizes, localWorkSizes, eventsToWaitFor);
	}
}
