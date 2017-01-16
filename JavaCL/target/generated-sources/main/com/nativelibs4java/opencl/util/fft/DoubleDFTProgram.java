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
/** Wrapper around the OpenCL program DoubleDFTProgram */
public class DoubleDFTProgram extends CLAbstractUserProgram {
	public DoubleDFTProgram(CLContext context) throws IOException {
		super(context, readRawSourceForClass(DoubleDFTProgram.class));
	}
	public DoubleDFTProgram(CLProgram program) throws IOException {
		super(program, readRawSourceForClass(DoubleDFTProgram.class));
	}
	CLKernel dft_kernel;
	public synchronized CLEvent dft(CLQueue commandQueue, CLBuffer<Double > in, CLBuffer<Double > out, int length, int sign, int globalWorkSizes[], int localWorkSizes[], CLEvent... eventsToWaitFor) throws CLBuildException {
		if ((dft_kernel == null)) 
			dft_kernel = createKernel("dft");
		dft_kernel.setArgs(in, out, length, sign);
		return dft_kernel.enqueueNDRange(commandQueue, globalWorkSizes, localWorkSizes, eventsToWaitFor);
	}
}
