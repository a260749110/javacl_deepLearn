/*
 * JavaCL - Java API and utilities for OpenCL
 * http://javacl.googlecode.com/
 *
 * Copyright (c) 2009-2015, Olivier Chafik (http://ochafik.com/)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Olivier Chafik nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY OLIVIER CHAFIK AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
	




	

	
	



	

	
	
	

	
package com.nativelibs4java.opencl;
import static com.nativelibs4java.opencl.CLException.*;
import static com.nativelibs4java.opencl.JavaCL.CL;
import static com.nativelibs4java.opencl.library.OpenCLLibrary.*;
import static com.nativelibs4java.opencl.library.IOpenCLLibrary.*;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Map;

import com.nativelibs4java.opencl.library.IOpenCLLibrary.cl_device_id;
import com.nativelibs4java.opencl.library.IOpenCLLibrary.cl_event;
import com.nativelibs4java.opencl.library.IOpenCLLibrary.cl_kernel;
import com.nativelibs4java.util.NIOUtils;

import org.bridj.*;
import static org.bridj.Pointer.*;

/**
 * OpenCL kernel.<br>
 * A kernel is a function declared in a program. <br>
 * A kernel is identified by the __kernel qualifier applied to any function in a program. <br>
 * A kernel object encapsulates the specific __kernel function declared in a program and the argument values to be used when executing this __kernel function.<br>
 * <br>
 * Kernels can be queued for execution in a CLQueue (see enqueueTask and enqueueNDRange)
 * See {@link CLProgram#createKernel(java.lang.String, java.lang.Object[])} and {@link CLProgram#createKernels()} 
 * @author Olivier Chafik
 */
public class CLKernel extends CLAbstractEntity {

    protected final CLProgram program;
    protected String name;
    
    	protected static CLInfoGetter infos = new CLInfoGetter() {
		@Override
		protected int getInfo(long entity, int infoTypeEnum, long size, Pointer out, Pointer<SizeT> sizeOut) {
			return CL.clGetKernelInfo(entity, infoTypeEnum, size, getPeer(out), getPeer(sizeOut));
		}
	};

    private volatile CLInfoGetter kernelInfos;
    protected synchronized CLInfoGetter getKernelInfos() {
        if (kernelInfos == null)
            kernelInfos = new CLInfoGetter() {

                @Override
                protected int getInfo(long entity, int infoTypeEnum, long size, Pointer out, Pointer<SizeT> sizeOut) {
                    return CL.clGetKernelWorkGroupInfo(getEntity(), entity, infoTypeEnum, size, getPeer(out), getPeer(sizeOut));
                }
            };
        return kernelInfos;
    }

    private final static int MAX_TMP_ITEMS = 16, MAX_TMP_ITEM_SIZE = 8;
    
    private final Pointer<?> localPointer = Pointer.allocateBytes(MAX_TMP_ITEM_SIZE * MAX_TMP_ITEMS).withoutValidityInformation();
    
    private final int contextAddressBits;
    
    CLKernel(CLProgram program, String name, long entity) {
        super(entity);
        this.program = program;
        this.name = name;
        this.contextAddressBits = getProgram().getContext().getAddressBits();
    }
	
    public CLProgram getProgram() {
        return program;
    }

    public String toString() {
        return getFunctionName() + " {args: " + getNumArgs() + "}";//, workGroupSize = " + getWorkGroupSize() + ", localMemSize = " + getLocalMemSize() + "}";
    }

    /**
     * Returns the preferred multiple of work- group size for launch. <br>
     * This is a performance hint. <br>
     * Specifying a work- group size that is not a multiple of the value returned by this query as the value of the local work size argument to clEnqueueNDRangeKernel will not fail to enqueue the kernel for execution unless the work-group size specified is larger than the device maximum.
     * @since OpenCL 1.1
     */
    public Map<CLDevice, Long> getPreferredWorkGroupSizeMultiple() {
		program.getContext().getPlatform().requireMinVersionValue("CL_KERNEL_PREFERRED_WORK_GROUP_SIZE_MULTIPLE", 1.1);
    	CLDevice[] devices = program.getDevices();
        Map<CLDevice, Long> ret = new HashMap<CLDevice, Long>(devices.length);
        for (CLDevice device : devices)
            ret.put(device, getKernelInfos().getIntOrLong(device.getEntity(), CL_KERNEL_PREFERRED_WORK_GROUP_SIZE_MULTIPLE));
        return ret;
    }
    
    /**
     * This provides a mechanism for the application to query the maximum work-group size that can be used to execute a kernel on a specific device given by device. <br>
     * The OpenCL implementation uses the resource requirements of the kernel (register usage etc.) to determine what this work- group size should be.<br>
     * See <a href="http://www.khronos.org/registry/cl/sdk/1.0/docs/man/xhtml/clGetKernelWorkGroupInfo.html">CL_KERNEL_WORK_GROUP_SIZE</a>
     */
    public Map<CLDevice, Long> getWorkGroupSize() {
        CLDevice[] devices = program.getDevices();
        Map<CLDevice, Long> ret = new HashMap<CLDevice, Long>(devices.length);
        for (CLDevice device : devices)
            ret.put(device, getKernelInfos().getIntOrLong(device.getEntity(), CL_KERNEL_WORK_GROUP_SIZE));
        return ret;
    }

    /**
     * Returns the work-group size specified by the __attribute__((reqd_work_gr oup_size(X, Y, Z))) qualifier.<br>
     * Refer to section 6.7.2.<br>
     * If the work-group size is not specified using the above attribute qualifier (0, 0, 0) is returned.<br>
     * See <a href="http://www.khronos.org/registry/cl/sdk/1.0/docs/man/xhtml/clGetKernelWorkGroupInfo.html">CL_KERNEL_COMPILE_WORK_GROUP_SIZE</a>
     * @return for each CLDevice, array of 3 longs
     */
    public Map<CLDevice, long[]> getCompileWorkGroupSize() {
        CLDevice[] devices = program.getDevices();
        Map<CLDevice, long[]> ret = new HashMap<CLDevice, long[]>(devices.length);
        for (CLDevice device : devices)
            ret.put(device, getKernelInfos().getNativeSizes(device.getEntity(), CL_KERNEL_COMPILE_WORK_GROUP_SIZE, 3));
        return ret;
    }
    
    /**
     * Returns the amount of local memory in bytes being used by a kernel. <br>
     * This includes local memory that may be needed by an implementation to execute the kernel, variables declared inside the kernel with the __local address qualifier and local memory to be allocated for arguments to the kernel declared as pointers with the __local address qualifier and whose size is specified with clSetKernelArg.<br>
     * If the local memory size, for any pointer argument to the kernel declared with the __local address qualifier, is not specified, its size is assumed to be 0.<br>
     * See <a href="http://www.khronos.org/registry/cl/sdk/1.0/docs/man/xhtml/clGetKernelWorkGroupInfo.html">CL_KERNEL_LOCAL_MEM_SIZE</a>
     */
    public Map<CLDevice, Long> getLocalMemSize() {
        CLDevice[] devices = program.getDevices();
        Map<CLDevice, Long> ret = new HashMap<CLDevice, Long>(devices.length);
        for (CLDevice device : devices)
            ret.put(device, getKernelInfos().getIntOrLong(device.getEntity(), CL_KERNEL_LOCAL_MEM_SIZE));
        return ret;
    }
    
    public void setArgs(Object... args) {
		//assert getNumArgs() == args.length;
        for (int i = 0, n = args.length; i < n; i++) {
            setObjectArg(i, args[i]);
        }
    }

    public static final Object NULL_POINTER_KERNEL_ARGUMENT = new Object() {};
    
    public void setObjectArg(int iArg, Object arg) {
        boolean supported = true;
        Class<?> cls;
        if (arg instanceof CLAbstractEntity) {
            setArg(iArg, (CLAbstractEntity) arg);
        } else if (arg instanceof Number) {
            if (arg instanceof Integer) {
              setArg(iArg, (Integer) arg);
            } else if (arg instanceof Long) {
                setArg(iArg, (Long) arg);
            } else if (arg instanceof Short) {
                setArg(iArg, (Short) arg);
            } else if (arg instanceof Byte) {
                setArg(iArg, (Byte) arg);
            } else if (arg instanceof Float) {
                setArg(iArg, (Float) arg);
            } else if (arg instanceof Double) {
                setArg(iArg, (Double) arg);
            } else {
              supported = false;
            } 
        } else if (arg instanceof LocalSize) {
			setArg(iArg, (LocalSize)arg);
		} else if (arg instanceof Boolean) {
            setArg(iArg, (Boolean)arg);
        } else if (arg instanceof SizeT) {
            setArg(iArg, (SizeT)arg);
        } else if (arg == NULL_POINTER_KERNEL_ARGUMENT) {
            setArg(iArg, SizeT.ZERO);
        } else if ((cls = arg.getClass()).isArray()) {
			if (arg instanceof int[]) {
				setArg(iArg, (int[])arg);
			} else if (arg instanceof long[]) {
				setArg(iArg, (long[])arg);
			} else if (arg instanceof short[]) {
				setArg(iArg, (short[])arg);
			} else if (arg instanceof double[]) {
				setArg(iArg, (double[])arg);
			} else if (arg instanceof float[]) {
				setArg(iArg, (float[])arg);
			} else if (arg instanceof byte[]) {
				setArg(iArg, (byte[])arg);
			} else if (arg instanceof boolean[]) {
				setArg(iArg, (boolean[])arg);
			} else {
				supported = false;
			}
        } else if (arg instanceof Pointer) {
            setArg(iArg, (Pointer)arg);
		} else if (arg instanceof Buffer) {
            setArg(iArg, pointerToBuffer((Buffer) arg));
        } else {
        		supported = false;
        }
        if (arg == null)
            throw new IllegalArgumentException("Null arguments are not accepted. Please use CLKernel.NULL_POINTER_KERNEL_ARGUMENT instead.");

        if (!supported) {
			throw new IllegalArgumentException("Cannot handle kernel arguments of type " + arg.getClass().getName() + ". Use CLKernel.get() and OpenCL4Java directly.");
        }
    }

    /**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clSetKernelArg.html">clSetKernelArg</a>.<br>
     */
    public void setArg(int i, LocalSize arg) {
        setLocalArg(i, arg.size);
    }
    public void setLocalArg(int argIndex, long localArgByteLength) {
        setKernelArg(argIndex, localArgByteLength, null);
    }

    private void setKernelArg(int i, long size, Pointer<?> ptr) {
    		if (size <= 0)
    			throw new IllegalArgumentException("Kernel args must have a known byte size, given " + size + " instead.");
    		try {
                error(CL.clSetKernelArg(getEntity(), i, size, getPeer(ptr)));
    		} catch (CLTypedException ex) {
    			ex.setKernelArg(this, i, size, ptr);
    			throw ex;
    		}
    }
    
    /**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clSetKernelArg.html">clSetKernelArg</a>.<br>
     */
    public void setArg(int i, Pointer<?> ptr) {
		setKernelArg(i, ptr.getValidBytes(), ptr);
    }
    
    /**
     * Sets a vector type value as the arg at index {@code i}.
     *
     * For instance to pass a {@code int3}, one must pass a {@code int[]} of size {@code 3}. 
     *
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clSetKernelArg.html">clSetKernelArg</a>.<br>
     * @param i index of the argument in the kernel's parameters list
     * @param arg array of ints (of length 2, 3, 4, 8 or 16, which must match the arity of the OpenCL vector type used by the kernel).
     */
    public void setArg(int i, int[] arg) {
        int length = normalizeVectorTypeArity(arg.length);
        setKernelArg(i, length * 4, length <= MAX_TMP_ITEMS ? localPointer.setInts(arg) : pointerToInts(arg));
    }
    /**
     * Sets a vector type value as the arg at index {@code i}.
     *
     * For instance to pass a {@code long3}, one must pass a {@code long[]} of size {@code 3}. 
     *
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clSetKernelArg.html">clSetKernelArg</a>.<br>
     * @param i index of the argument in the kernel's parameters list
     * @param arg array of longs (of length 2, 3, 4, 8 or 16, which must match the arity of the OpenCL vector type used by the kernel).
     */
    public void setArg(int i, long[] arg) {
        int length = normalizeVectorTypeArity(arg.length);
        setKernelArg(i, length * 8, length <= MAX_TMP_ITEMS ? localPointer.setLongs(arg) : pointerToLongs(arg));
    }
    /**
     * Sets a vector type value as the arg at index {@code i}.
     *
     * For instance to pass a {@code short3}, one must pass a {@code short[]} of size {@code 3}. 
     *
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clSetKernelArg.html">clSetKernelArg</a>.<br>
     * @param i index of the argument in the kernel's parameters list
     * @param arg array of shorts (of length 2, 3, 4, 8 or 16, which must match the arity of the OpenCL vector type used by the kernel).
     */
    public void setArg(int i, short[] arg) {
        int length = normalizeVectorTypeArity(arg.length);
        setKernelArg(i, length * 2, length <= MAX_TMP_ITEMS ? localPointer.setShorts(arg) : pointerToShorts(arg));
    }
    /**
     * Sets a vector type value as the arg at index {@code i}.
     *
     * For instance to pass a {@code byte3}, one must pass a {@code byte[]} of size {@code 3}. 
     *
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clSetKernelArg.html">clSetKernelArg</a>.<br>
     * @param i index of the argument in the kernel's parameters list
     * @param arg array of bytes (of length 2, 3, 4, 8 or 16, which must match the arity of the OpenCL vector type used by the kernel).
     */
    public void setArg(int i, byte[] arg) {
        int length = normalizeVectorTypeArity(arg.length);
        setKernelArg(i, length * 1, length <= MAX_TMP_ITEMS ? localPointer.setBytes(arg) : pointerToBytes(arg));
    }
    /**
     * Sets a vector type value as the arg at index {@code i}.
     *
     * For instance to pass a {@code char3}, one must pass a {@code char[]} of size {@code 3}. 
     *
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clSetKernelArg.html">clSetKernelArg</a>.<br>
     * @param i index of the argument in the kernel's parameters list
     * @param arg array of chars (of length 2, 3, 4, 8 or 16, which must match the arity of the OpenCL vector type used by the kernel).
     */
    public void setArg(int i, char[] arg) {
        int length = normalizeVectorTypeArity(arg.length);
        setKernelArg(i, length * 2, length <= MAX_TMP_ITEMS ? localPointer.setChars(arg) : pointerToChars(arg));
    }
    /**
     * Sets a vector type value as the arg at index {@code i}.
     *
     * For instance to pass a {@code float3}, one must pass a {@code float[]} of size {@code 3}. 
     *
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clSetKernelArg.html">clSetKernelArg</a>.<br>
     * @param i index of the argument in the kernel's parameters list
     * @param arg array of floats (of length 2, 3, 4, 8 or 16, which must match the arity of the OpenCL vector type used by the kernel).
     */
    public void setArg(int i, float[] arg) {
        int length = normalizeVectorTypeArity(arg.length);
        setKernelArg(i, length * 4, length <= MAX_TMP_ITEMS ? localPointer.setFloats(arg) : pointerToFloats(arg));
    }
    /**
     * Sets a vector type value as the arg at index {@code i}.
     *
     * For instance to pass a {@code double3}, one must pass a {@code double[]} of size {@code 3}. 
     *
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clSetKernelArg.html">clSetKernelArg</a>.<br>
     * @param i index of the argument in the kernel's parameters list
     * @param arg array of doubles (of length 2, 3, 4, 8 or 16, which must match the arity of the OpenCL vector type used by the kernel).
     */
    public void setArg(int i, double[] arg) {
        int length = normalizeVectorTypeArity(arg.length);
        setKernelArg(i, length * 8, length <= MAX_TMP_ITEMS ? localPointer.setDoubles(arg) : pointerToDoubles(arg));
    }
    /**
     * Sets a vector type value as the arg at index {@code i}.
     *
     * For instance to pass a {@code boolean3}, one must pass a {@code boolean[]} of size {@code 3}. 
     *
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clSetKernelArg.html">clSetKernelArg</a>.<br>
     * @param i index of the argument in the kernel's parameters list
     * @param arg array of booleans (of length 2, 3, 4, 8 or 16, which must match the arity of the OpenCL vector type used by the kernel).
     */
    public void setArg(int i, boolean[] arg) {
        int length = normalizeVectorTypeArity(arg.length);
        setKernelArg(i, length * 1, length <= MAX_TMP_ITEMS ? localPointer.setBooleans(arg) : pointerToBooleans(arg));
    }

    private static int normalizeVectorTypeArity(int length) {
      switch (length) {
        case 3:
          // cl_<prim>3 is identical in size, alignment and behavior to cl_<prim>4. See section 6.1.5
          return 4;
        case 2:
        case 4:
        case 8:
        case 16:
          return length;
        default:
          throw new IllegalArgumentException("Invalid vector type length: " + length + " (valid vector type sizes are 2, 3, 4, 8, 16)");
      }
    }

    /**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clSetKernelArg.html">clSetKernelArg</a>.<br>
     */
    public void setArg(int i, SizeT arg) {
        switch (contextAddressBits) {
            case 32:
                setKernelArg(i, 4, localPointer.setInt(arg.intValue()));
                break;
            case 64:
                setKernelArg(i, 8, localPointer.setLong(arg.longValue()));
                break;
            default:
                setKernelArg(i, SizeT.SIZE, localPointer.setSizeT(arg.longValue()));
                break;
        }
    }

    /**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clSetKernelArg.html">clSetKernelArg</a>.<br>
     */
    public void setArg(int i, int arg) {
        setKernelArg(i, 4, localPointer.setInt(arg));
    }

    /**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clSetKernelArg.html">clSetKernelArg</a>.<br>
     */
    public void setArg(int i, long arg) {
        setKernelArg(i, 8, localPointer.setLong(arg));
    }

    /**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clSetKernelArg.html">clSetKernelArg</a>.<br>
     */
    public void setArg(int i, short arg) {
        setKernelArg(i, 2, localPointer.setShort(arg));
    }

    /**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clSetKernelArg.html">clSetKernelArg</a>.<br>
     */
    public void setArg(int i, byte arg) {
        setKernelArg(i, 1, localPointer.setByte(arg));
    }

    /**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clSetKernelArg.html">clSetKernelArg</a>.<br>
     */
    public void setArg(int i, boolean arg) {
        setKernelArg(i, 1, localPointer.setByte(arg ? (byte)1 : (byte)0));
    }

    /**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clSetKernelArg.html">clSetKernelArg</a>.<br>
     */
    public void setArg(int i, float arg) {
        setKernelArg(i, 4, localPointer.setFloat(arg));
    }

    /**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clSetKernelArg.html">clSetKernelArg</a>.<br>
     */
    public void setArg(int i, double arg) {
        setKernelArg(i, 8, localPointer.setDouble(arg));
    }

    /**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clSetKernelArg.html">clSetKernelArg</a>.<br>
     */
    public void setArg(int i, CLAbstractEntity arg) {
        setKernelArg(i, Pointer.SIZE, localPointer.setSizeT(arg.getEntity()));
    }

    @Override
    protected void clear() {
        error(CL.clReleaseKernel(getEntity()));
    }

    private static final Pointer<SizeT> oneNL = pointerToSizeT(1);
    /**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clEnqueueTask.html">clEnqueueTask</a>.<br>
     * Enqueues a command to execute a kernel on a device. <br>
     * The kernel is executed using a single work-item.
     * @param queue
	 * @param eventsToWaitFor Events that need to complete before this particular command can be executed. Special value {@link CLEvent#FIRE_AND_FORGET} can be used to avoid returning a CLEvent.  
     * @return Event object that identifies this command and can be used to query or queue a wait for the command to complete, or null if eventsToWaitFor contains {@link CLEvent#FIRE_AND_FORGET}.
     */
    public CLEvent enqueueTask(CLQueue queue, CLEvent... eventsToWaitFor) {
        		ReusablePointers ptrs = ReusablePointers.get();
		int[] eventsInCount = ptrs.int1Array;
	Pointer<cl_event> eventsIn = 
		CLAbstractEntity.copyNonNullEntities(eventsToWaitFor, eventsInCount, ptrs.events_in);
		Pointer<cl_event> eventOut = 
		eventsToWaitFor == null || CLEvent.containsFireAndForget(eventsToWaitFor) 
		? null 
		: ptrs.event_out;
        error(CL.clEnqueueTask(queue.getEntity(), getEntity(),   eventsInCount[0], getPeer(eventsIn) ,  getPeer(eventOut)  ));
         return  CLEvent.createEventFromPointer(queue, eventOut) ;     }

    /**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clEnqueueNDRangeKernel.html">clEnqueueNDRangeKernel</a>.<br>
     * Enqueues a command to execute a kernel on a device (see {@link CLKernel#enqueueNDRange(CLQueue, int[], int[], int[], CLEvent[])})
     * @param globalWorkSizes Each element describes the number of global work-items in a dimension that will execute the kernel function. The total number of global work-items is computed as globalWorkSizes[0] * ... * globalWorkSizes[globalWorkSizes.length - 1].
     * @param localWorkSizes Each element describes the number of work-items that make up a work-group (also referred to as the size of the work-group) that will execute the kernel specified by kernel. The total number of work-items in a work-group is computed as localWorkSizes[0] * ... * localWorkSizes[localWorkSizes.length - 1]. The total number of work-items in the work-group must be less than or equal to the CL_DEVICE_MAX_WORK_GROUP_SIZE value specified in table 4.3 and the number of work- items specified in localWorkSizes[0], ... localWorkSizes[localWorkSizes.length - 1] must be less than or equal to the corresponding values specified by CLDevice.getMaxWorkItemSizes()[dimensionIndex].	The explicitly specified localWorkSize will be used to determine how to break the global work-items specified by global_work_size into appropriate work-group instances. If localWorkSize is specified, the values specified in globalWorkSize[dimensionIndex] must be evenly divisible by the corresponding values specified in localWorkSize[dimensionIndex]. This parameter can be left null, in which case the OpenCL implementation will choose good values.
     * @param queue This kernel will be queued for execution on the device associated with that queue.
	 * @param eventsToWaitFor Events that need to complete before this particular command can be executed. Special value {@link CLEvent#FIRE_AND_FORGET} can be used to avoid returning a CLEvent.  
     * @return Event object that identifies this command and can be used to query or queue a wait for the command to complete, or null if eventsToWaitFor contains {@link CLEvent#FIRE_AND_FORGET}.
     */
    public CLEvent enqueueNDRange(CLQueue queue /*, int[] globalOffsets*/, int[] globalWorkSizes, int[] localWorkSizes, CLEvent... eventsToWaitFor) {
    	return enqueueNDRange(queue, null, globalWorkSizes, localWorkSizes, eventsToWaitFor);
    }
    
    /**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clEnqueueNDRangeKernel.html">clEnqueueNDRangeKernel</a>.<br>
     * Enqueues a command to execute a kernel on a device, using local work sizes chosen by the OpenCL implementation.
     * See {@link CLKernel#enqueueNDRange(CLQueue, int[], int[], int[], CLEvent[])}
     * @param globalWorkSizes Each element describes the number of global work-items in a dimension that will execute the kernel function. The total number of global work-items is computed as globalWorkSizes[0] * ... * globalWorkSizes[globalWorkSizes.length - 1].
     * @param queue This kernel will be queued for execution on the device associated with that queue.
	 * @param eventsToWaitFor Events that need to complete before this particular command can be executed. Special value {@link CLEvent#FIRE_AND_FORGET} can be used to avoid returning a CLEvent.  
     * @return Event object that identifies this command and can be used to query or queue a wait for the command to complete, or null if eventsToWaitFor contains {@link CLEvent#FIRE_AND_FORGET}.
     */
    public CLEvent enqueueNDRange(CLQueue queue /*, int[] globalOffsets*/, int[] globalWorkSizes, CLEvent... eventsToWaitFor) {
    	return enqueueNDRange(queue, null, globalWorkSizes, null, eventsToWaitFor);
    }
    
    /**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clEnqueueNDRangeKernel.html">clEnqueueNDRangeKernel</a>.<br>
     * Enqueues a command to execute a kernel on a device.
     * @param globalOffsets Must be null in OpenCL 1.0. Each element describes the offset used to calculate the global ID of a work-item. If globalOffsets is null, the global IDs start at offset (0, 0, ... 0).
     * @param globalWorkSizes Each element describes the number of global work-items in a dimension that will execute the kernel function. The total number of global work-items is computed as globalWorkSizes[0] * ... * globalWorkSizes[globalWorkSizes.length - 1].
     * @param localWorkSizes Each element describes the number of work-items that make up a work-group (also referred to as the size of the work-group) that will execute the kernel specified by kernel. The total number of work-items in a work-group is computed as localWorkSizes[0] * ... * localWorkSizes[localWorkSizes.length - 1]. The total number of work-items in the work-group must be less than or equal to the CL_DEVICE_MAX_WORK_GROUP_SIZE value specified in table 4.3 and the number of work- items specified in localWorkSizes[0], ... localWorkSizes[localWorkSizes.length - 1] must be less than or equal to the corresponding values specified by CLDevice.getMaxWorkItemSizes()[dimensionIndex].	The explicitly specified localWorkSize will be used to determine how to break the global work-items specified by global_work_size into appropriate work-group instances. If localWorkSize is specified, the values specified in globalWorkSize[dimensionIndex] must be evenly divisible by the corresponding values specified in localWorkSize[dimensionIndex]. This parameter can be left null, in which case the OpenCL implementation will choose good values.
     * @param queue This kernel will be queued for execution on the device associated with that queue.
	 * @param eventsToWaitFor Events that need to complete before this particular command can be executed. Special value {@link CLEvent#FIRE_AND_FORGET} can be used to avoid returning a CLEvent.  
     * @return Event object that identifies this command and can be used to query or queue a wait for the command to complete, or null if eventsToWaitFor contains {@link CLEvent#FIRE_AND_FORGET}.
     */
    public CLEvent enqueueNDRange(CLQueue queue, long[] globalOffsets, long[] globalWorkSizes, long[] localWorkSizes, CLEvent... eventsToWaitFor) {
        int nDims = globalWorkSizes.length;
        if (localWorkSizes != null && localWorkSizes.length != nDims) {
            throw new IllegalArgumentException("Global and local sizes must have same dimensions, given " + globalWorkSizes.length + " vs. " + localWorkSizes.length);
        }
        
        		ReusablePointers ptrs = ReusablePointers.get();
		int[] eventsInCount = ptrs.int1Array;
	Pointer<cl_event> eventsIn = 
		CLAbstractEntity.copyNonNullEntities(eventsToWaitFor, eventsInCount, ptrs.events_in);
		Pointer<cl_event> eventOut = 
		eventsToWaitFor == null || CLEvent.containsFireAndForget(eventsToWaitFor) 
		? null 
		: ptrs.event_out;
        error(CL.clEnqueueNDRangeKernel(
            queue.getEntity(),
            getEntity(),
            nDims,
            getPeer(ptrs.sizeT3_1.pointerToSizeTs(globalOffsets)),
            getPeer(ptrs.sizeT3_2.pointerToSizeTs(globalWorkSizes)),
            getPeer(ptrs.sizeT3_3.pointerToSizeTs(localWorkSizes)),
              eventsInCount[0], getPeer(eventsIn) ,  getPeer(eventOut)          ));
         return  CLEvent.createEventFromPointer(queue, eventOut) ;     }
    
    /**
     * @deprecated Use {@link CLKernel#enqueueNDRange(CLQueue, long[], long[], long[], CLEvent[])} instead.
     */
    @Deprecated
    public CLEvent enqueueNDRange(CLQueue queue, int[] globalOffsets, int[] globalWorkSizes, int[] localWorkSizes, CLEvent... eventsToWaitFor) {
        int nDims = globalWorkSizes.length;
        if (localWorkSizes != null && localWorkSizes.length != nDims) {
            throw new IllegalArgumentException("Global and local sizes must have same dimensions, given " + globalWorkSizes.length + " vs. " + localWorkSizes.length);
        }
        		ReusablePointers ptrs = ReusablePointers.get();
		int[] eventsInCount = ptrs.int1Array;
	Pointer<cl_event> eventsIn = 
		CLAbstractEntity.copyNonNullEntities(eventsToWaitFor, eventsInCount, ptrs.events_in);
		Pointer<cl_event> eventOut = 
		eventsToWaitFor == null || CLEvent.containsFireAndForget(eventsToWaitFor) 
		? null 
		: ptrs.event_out;
        error(CL.clEnqueueNDRangeKernel(
            queue.getEntity(),
            getEntity(),
            nDims,
            getPeer(ptrs.sizeT3_1.pointerToSizeTs(globalOffsets)),
            getPeer(ptrs.sizeT3_2.pointerToSizeTs(globalWorkSizes)),
            getPeer(ptrs.sizeT3_3.pointerToSizeTs(localWorkSizes)),
              eventsInCount[0], getPeer(eventsIn) ,  getPeer(eventOut)  ));
         return  CLEvent.createEventFromPointer(queue, eventOut) ;     }
	
	/**
	 * Return the number of arguments to kernel.
	 */
    @InfoName("CL_KERNEL_NUM_ARGS")
    public int getNumArgs() {
    		int numArgs = infos.getInt(getEntity(), CL_KERNEL_NUM_ARGS);
    		//System.out.println("numArgs = " + numArgs);
        return numArgs;
    }

    /**
     * Return the kernel function name.
     */
    @InfoName("CL_KERNEL_FUNCTION_NAME")
    public String getFunctionName() {
        if (name == null)
            name = infos.getString(getEntity(), CL_KERNEL_FUNCTION_NAME);
        return name;
    }
}
