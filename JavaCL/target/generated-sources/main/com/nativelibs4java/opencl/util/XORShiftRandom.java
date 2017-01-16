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
/** Wrapper around the OpenCL program XORShiftRandom */
public class XORShiftRandom extends CLAbstractUserProgram {
	public XORShiftRandom(CLContext context) throws IOException {
		super(context, readRawSourceForClass(XORShiftRandom.class));
	}
	public XORShiftRandom(CLProgram program) throws IOException {
		super(program, readRawSourceForClass(XORShiftRandom.class));
	}
	CLKernel gen_numbers_kernel;
	public synchronized CLEvent gen_numbers(CLQueue commandQueue, CLBuffer<Integer > seeds, CLBuffer<Integer > output, int globalWorkSizes[], int localWorkSizes[], CLEvent... eventsToWaitFor) throws CLBuildException {
		if ((gen_numbers_kernel == null)) 
			gen_numbers_kernel = createKernel("gen_numbers");
		gen_numbers_kernel.setArgs(seeds, output);
		return gen_numbers_kernel.enqueueNDRange(commandQueue, globalWorkSizes, localWorkSizes, eventsToWaitFor);
	}
	/** <i>native declaration : com\nativelibs4java\opencl\\util\XORShiftRandom.c</i> */
	public static final int NUMBERS_COUNT = (int)0;
}
