package com.nativelibs4java.opencl.demos.particles;
import com.nativelibs4java.opencl.CLAbstractUserProgram;
import com.nativelibs4java.opencl.CLBuildException;
import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLEvent;
import com.nativelibs4java.opencl.CLKernel;
import com.nativelibs4java.opencl.CLProgram;
import com.nativelibs4java.opencl.CLQueue;
import java.io.IOException;
/** Wrapper around the OpenCL program HSVtoRGB */
public class HSVtoRGB extends CLAbstractUserProgram {
	public HSVtoRGB(CLContext context) throws IOException {
		super(context, readRawSourceForClass(HSVtoRGB.class));
	}
	public HSVtoRGB(CLProgram program) throws IOException {
		super(program, readRawSourceForClass(HSVtoRGB.class));
	}
	CLKernel HSVAtoRGBA_kernel;
	public synchronized CLEvent HSVAtoRGBA(CLQueue commandQueue, float hsva[], int globalWorkSizes[], int localWorkSizes[], CLEvent... eventsToWaitFor) throws CLBuildException {
		if ((HSVAtoRGBA_kernel == null)) 
			HSVAtoRGBA_kernel = createKernel("HSVAtoRGBA");
		checkArrayLength(hsva, 4, "hsva");
		HSVAtoRGBA_kernel.setArgs(hsva);
		return HSVAtoRGBA_kernel.enqueueNDRange(commandQueue, globalWorkSizes, localWorkSizes, eventsToWaitFor);
	}
}
