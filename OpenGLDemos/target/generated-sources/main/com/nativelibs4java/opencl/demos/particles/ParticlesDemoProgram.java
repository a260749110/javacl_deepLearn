package com.nativelibs4java.opencl.demos.particles;
import com.nativelibs4java.opencl.CLAbstractUserProgram;
import com.nativelibs4java.opencl.CLBuffer;
import com.nativelibs4java.opencl.CLBuildException;
import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLEvent;
import com.nativelibs4java.opencl.CLKernel;
import com.nativelibs4java.opencl.CLProgram;
import com.nativelibs4java.opencl.CLQueue;
import java.io.IOException;
/** Wrapper around the OpenCL program ParticlesDemoProgram */
public class ParticlesDemoProgram extends CLAbstractUserProgram {
	public ParticlesDemoProgram(CLContext context) throws IOException {
		super(context, readRawSourceForClass(ParticlesDemoProgram.class));
	}
	public ParticlesDemoProgram(CLProgram program) throws IOException {
		super(program, readRawSourceForClass(ParticlesDemoProgram.class));
	}
	CLKernel updateParticle_kernel;
	public synchronized CLEvent updateParticle(CLQueue commandQueue, CLBuffer<Float > masses, CLBuffer<Float > velocities, CLBuffer<Float > particles, float mousePos[], float dimensions[], float massFactor, float speedFactor, float slowDownFactor, float mouseWeight, byte limitToScreen, int globalWorkSizes[], int localWorkSizes[], CLEvent... eventsToWaitFor) throws CLBuildException {
		if ((updateParticle_kernel == null)) 
			updateParticle_kernel = createKernel("updateParticle");
		checkArrayLength(mousePos, 2, "mousePos");
		checkArrayLength(dimensions, 2, "dimensions");
		updateParticle_kernel.setArgs(masses, velocities, particles, mousePos, dimensions, massFactor, speedFactor, slowDownFactor, mouseWeight, limitToScreen);
		return updateParticle_kernel.enqueueNDRange(commandQueue, globalWorkSizes, localWorkSizes, eventsToWaitFor);
	}
	/** <i>native declaration : com\nativelibs4java\opencl\demos\particles\ParticlesDemoProgram.c</i> */
	public static final float REPULSION_FORCE = (float)4.0f;
	/** <i>native declaration : com\nativelibs4java\opencl\demos\particles\ParticlesDemoProgram.c</i> */
	public static final float CENTER_FORCE2 = (float)0.0005f;
	/** <i>native declaration : com\nativelibs4java\opencl\demos\particles\ParticlesDemoProgram.c</i> */
	public static final float PI = (float)3.141f;
}
