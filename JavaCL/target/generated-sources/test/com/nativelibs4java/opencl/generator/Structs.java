package com.nativelibs4java.opencl.generator;
import com.nativelibs4java.opencl.CLAbstractUserProgram;
import com.nativelibs4java.opencl.CLBuffer;
import com.nativelibs4java.opencl.CLBuildException;
import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLEvent;
import com.nativelibs4java.opencl.CLKernel;
import com.nativelibs4java.opencl.CLProgram;
import com.nativelibs4java.opencl.CLQueue;
import java.io.IOException;
import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Field;
/** Wrapper around the OpenCL program Structs */
public class Structs extends CLAbstractUserProgram {
	public Structs(CLContext context) throws IOException {
		super(context, readRawSourceForClass(Structs.class));
	}
	public Structs(CLProgram program) throws IOException {
		super(program, readRawSourceForClass(Structs.class));
	}
	/** <i>native declaration : com\nativelibs4java\opencl\generator\Structs.c</i> */
	public static class S extends StructObject {
		static {
			BridJ.register();
		}
		@Field(0) 
		public int a() {
			return this.io.getIntField(this, 0);
		}
		@Field(0) 
		public S a(int a) {
			this.io.setIntField(this, 0, a);
			return this;
		}
		@Field(1) 
		public int b() {
			return this.io.getIntField(this, 1);
		}
		@Field(1) 
		public S b(int b) {
			this.io.setIntField(this, 1, b);
			return this;
		}
		public S() {
			super();
		}
		public S(Pointer pointer) {
			super(pointer);
		}
	};
	CLKernel f_kernel;
	public synchronized CLEvent f(CLQueue commandQueue, CLBuffer<S > pS, int globalWorkSizes[], int localWorkSizes[], CLEvent... eventsToWaitFor) throws CLBuildException {
		if ((f_kernel == null)) 
			f_kernel = createKernel("f");
		f_kernel.setArgs(pS);
		return f_kernel.enqueueNDRange(commandQueue, globalWorkSizes, localWorkSizes, eventsToWaitFor);
	}
	CLKernel g_kernel;
	public synchronized CLEvent g(CLQueue commandQueue, float floats[], CLBuffer<Float > out, int globalWorkSizes[], int localWorkSizes[], CLEvent... eventsToWaitFor) throws CLBuildException {
		if ((g_kernel == null)) 
			g_kernel = createKernel("g");
		checkArrayLength(floats, 3, "floats");
		g_kernel.setArgs(floats, out);
		return g_kernel.enqueueNDRange(commandQueue, globalWorkSizes, localWorkSizes, eventsToWaitFor);
	}
}
