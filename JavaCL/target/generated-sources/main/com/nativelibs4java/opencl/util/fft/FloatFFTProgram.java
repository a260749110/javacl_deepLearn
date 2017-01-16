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
/** Wrapper around the OpenCL program FloatFFTProgram */
public class FloatFFTProgram extends CLAbstractUserProgram {
	public FloatFFTProgram(CLContext context) throws IOException {
		super(context, readRawSourceForClass(FloatFFTProgram.class));
	}
	public FloatFFTProgram(CLProgram program) throws IOException {
		super(program, readRawSourceForClass(FloatFFTProgram.class));
	}
	CLKernel rotateValue2_kernel;
	public synchronized CLEvent rotateValue2(CLQueue commandQueue, float value[], float c, float s, int globalWorkSizes[], int localWorkSizes[], CLEvent... eventsToWaitFor) throws CLBuildException {
		if ((rotateValue2_kernel == null)) 
			rotateValue2_kernel = createKernel("rotateValue2");
		checkArrayLength(value, 2, "value");
		rotateValue2_kernel.setArgs(value, c, s);
		return rotateValue2_kernel.enqueueNDRange(commandQueue, globalWorkSizes, localWorkSizes, eventsToWaitFor);
	}
	CLKernel cooleyTukeyFFTCopy_kernel;
	public synchronized CLEvent cooleyTukeyFFTCopy(CLQueue commandQueue, CLBuffer<Float > X, CLBuffer<Float > Y, int N, CLBuffer<Integer > offsetsX, float factor, int globalWorkSizes[], int localWorkSizes[], CLEvent... eventsToWaitFor) throws CLBuildException {
		if ((cooleyTukeyFFTCopy_kernel == null)) 
			cooleyTukeyFFTCopy_kernel = createKernel("cooleyTukeyFFTCopy");
		cooleyTukeyFFTCopy_kernel.setArgs(X, Y, N, offsetsX, factor);
		return cooleyTukeyFFTCopy_kernel.enqueueNDRange(commandQueue, globalWorkSizes, localWorkSizes, eventsToWaitFor);
	}
	CLKernel cooleyTukeyFFTTwiddleFactors_kernel;
	public synchronized CLEvent cooleyTukeyFFTTwiddleFactors(CLQueue commandQueue, int N, CLBuffer<Float > twiddleFactors, int globalWorkSizes[], int localWorkSizes[], CLEvent... eventsToWaitFor) throws CLBuildException {
		if ((cooleyTukeyFFTTwiddleFactors_kernel == null)) 
			cooleyTukeyFFTTwiddleFactors_kernel = createKernel("cooleyTukeyFFTTwiddleFactors");
		cooleyTukeyFFTTwiddleFactors_kernel.setArgs(N, twiddleFactors);
		return cooleyTukeyFFTTwiddleFactors_kernel.enqueueNDRange(commandQueue, globalWorkSizes, localWorkSizes, eventsToWaitFor);
	}
	CLKernel cooleyTukeyFFT_kernel;
	public synchronized CLEvent cooleyTukeyFFT(CLQueue commandQueue, CLBuffer<Float > Y, int N, CLBuffer<Float > twiddleFactors, int inverse, int globalWorkSizes[], int localWorkSizes[], CLEvent... eventsToWaitFor) throws CLBuildException {
		if ((cooleyTukeyFFT_kernel == null)) 
			cooleyTukeyFFT_kernel = createKernel("cooleyTukeyFFT");
		cooleyTukeyFFT_kernel.setArgs(Y, N, twiddleFactors, inverse);
		return cooleyTukeyFFT_kernel.enqueueNDRange(commandQueue, globalWorkSizes, localWorkSizes, eventsToWaitFor);
	}
}
