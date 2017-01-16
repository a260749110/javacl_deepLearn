package com.nativelibs4java.opencl.demos.mandelbrot;
import com.nativelibs4java.opencl.CLAbstractUserProgram;
import com.nativelibs4java.opencl.CLBuffer;
import com.nativelibs4java.opencl.CLBuildException;
import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLEvent;
import com.nativelibs4java.opencl.CLKernel;
import com.nativelibs4java.opencl.CLProgram;
import com.nativelibs4java.opencl.CLQueue;
import java.io.IOException;
/** Wrapper around the OpenCL program Mandelbrot */
public class Mandelbrot extends CLAbstractUserProgram {
	public Mandelbrot(CLContext context) throws IOException {
		super(context, readRawSourceForClass(Mandelbrot.class));
	}
	public Mandelbrot(CLProgram program) throws IOException {
		super(program, readRawSourceForClass(Mandelbrot.class));
	}
	CLKernel mandelbrot_kernel;
	public synchronized CLEvent mandelbrot(CLQueue commandQueue, float delta[], float minimum[], int maxIter, int magicNumber, int hRes, CLBuffer<Integer > outputi, int globalWorkSizes[], int localWorkSizes[], CLEvent... eventsToWaitFor) throws CLBuildException {
		if ((mandelbrot_kernel == null)) 
			mandelbrot_kernel = createKernel("mandelbrot");
		checkArrayLength(delta, 2, "delta");
		checkArrayLength(minimum, 2, "minimum");
		mandelbrot_kernel.setArgs(delta, minimum, maxIter, magicNumber, hRes, outputi);
		return mandelbrot_kernel.enqueueNDRange(commandQueue, globalWorkSizes, localWorkSizes, eventsToWaitFor);
	}
}
