package com.nativelibs4java.opencl;

import java.util.Map;
import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import org.bridj.Pointer;
import static org.bridj.Pointer.*;
import java.util.List;
import org.junit.Ignore;
import org.junit.runners.Parameterized;

/**
 * @author Kazo Csaba
 */
@SuppressWarnings("unchecked")
public class BinaryKernelTest extends AbstractCommon {
    public BinaryKernelTest(CLDevice device) {
        super(device);
    }
    
    @Parameterized.Parameters
    public static List<Object[]> getDeviceParameters() {
        return AbstractCommon.getDeviceParameters();
    }

    @Test
    public void simpleTest() throws CLBuildException {
		if (!context.getCacheBinaries()) {
			System.out.println("Skipping binaries caching test");
			return;
		}
		CLProgram program = context.createProgram(
				  "__kernel void copy(__global int* a, __global int* b) {\n" +
				  "   int i = get_global_id(0);\n" +
				  "   b[i]=a[i];\n" +
				  "} ");
		program.build();
		Map<CLDevice, byte[]> binaries = program.getBinaries();
		String src = program.getSource();
		program.release();
		
		CLProgram binaryProgram = context.createProgram(binaries, null);
		CLKernel kernel = binaryProgram.createKernel("copy");

		CLBuffer<Integer> a=context.createBuffer(CLMem.Usage.Input, Integer.class, 4);
		CLBuffer<Integer> b=context.createBuffer(CLMem.Usage.Output, Integer.class, 4);

		Pointer<Integer> source = allocateInts(4).order(context.getByteOrder());
		for (int i=0; i<4; i++)
			source.set(i, 3*i+10);

		a.write(queue, source, true);

		kernel.setArgs(a, b);
		kernel.enqueueNDRange(queue, new int[]{4}).waitFor();

		Pointer<Integer> target = b.read(queue);

		assertEquals(target.getValidElements(), source.getValidElements());
		for (int i=0; i<4; i++)
			assertEquals(source.get(i), target.get(i));
    }
    
    /**
     * Test from issue https://github.com/ochafik/nativelibs4java/issues/453
     */
    @Test
    public void cachingTest() {
        CLContext clcontext = JavaCL.createBestContext();
        CLProgram ap = clcontext.createProgram("__kernel void add(int a, int b, __global int* c) { *c = a + b; }");
        ap.createKernel("add");
        Map<CLDevice, byte[]> addBins = ap.getBinaries();
        Map<CLDevice, byte[]> subBins = clcontext.createProgram("__kernel void sub(int a, int b, __global int* c) { *c = a - b; }").getBinaries();
        CLProgram ap2 = clcontext.createProgram(addBins, "__kernel void add(int a, int b, __global int* c) { *c = a + b; }");
        ap2.createKernel("add");
        CLProgram sp2 = clcontext.createProgram(subBins, "__kernel void sub(int a, int b, __global int* c) { *c = a - b; }");
        sp2.setCached(true); // set to false and it works
        sp2.createKernel("sub");
    }
}
