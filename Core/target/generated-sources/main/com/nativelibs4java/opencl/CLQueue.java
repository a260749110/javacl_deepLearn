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
import static com.nativelibs4java.opencl.CLException.error;
import static com.nativelibs4java.opencl.JavaCL.CL;
import static com.nativelibs4java.opencl.library.IOpenCLLibrary.CL_FALSE;
import static com.nativelibs4java.opencl.library.IOpenCLLibrary.CL_QUEUE_PROPERTIES;
import static com.nativelibs4java.opencl.library.IOpenCLLibrary.CL_TRUE;

import java.util.EnumSet;

import com.nativelibs4java.opencl.library.IOpenCLLibrary.cl_command_queue;
import com.nativelibs4java.opencl.library.IOpenCLLibrary.cl_event;
import com.nativelibs4java.opencl.library.IOpenCLLibrary.cl_mem;
import org.bridj.*;
import static org.bridj.Pointer.*;

/**
 * OpenCL command queue.<br>
 * OpenCL objects such as memory, program and kernel objects are created using a context. <br>
 * Operations on these objects are performed using a command-queue. <br>
 * The command-queue can be used to queue a set of operations (referred to as commands) in order. <br>
 * Having multiple command-queues allows applications to queue multiple independent commands without requiring synchronization. <br>
 * Note that this should work as long as these objects are not being shared. <br>
 * Sharing of objects across multiple command-queues will require the application to perform appropriate synchronization.<br>
 * <br>
 * A queue is bound to a single device.
 * see {@link CLDevice#createQueue(com.nativelibs4java.opencl.CLContext, com.nativelibs4java.opencl.CLDevice.QueueProperties[]) } 
 * see {@link CLDevice#createOutOfOrderQueue(com.nativelibs4java.opencl.CLContext) }
 * see {@link CLDevice#createProfilingQueue(com.nativelibs4java.opencl.CLContext) }
 * see {@link CLContext#createDefaultQueue(com.nativelibs4java.opencl.CLDevice.QueueProperties[]) }
 * see {@link CLContext#createDefaultOutOfOrderQueue() }
 * see {@link CLContext#createDefaultProfilingQueue() }
 * @author Olivier Chafik
 *
 */
public class CLQueue extends CLAbstractEntity {

    	protected static CLInfoGetter infos = new CLInfoGetter() {
		@Override
		protected int getInfo(long entity, int infoTypeEnum, long size, Pointer out, Pointer<SizeT> sizeOut) {
			return CL.clGetCommandQueueInfo(entity, infoTypeEnum, size, getPeer(out), getPeer(sizeOut));
		}
	};

	final CLContext context;
	final CLDevice device;

    CLQueue(CLContext context, long entity, CLDevice device) {
        super(entity);
        this.context = context;
		this.device = device;
    }
    
    public CLContext getContext() {
        return context;
    }
	public CLDevice getDevice() {
		return device;
	}
	
	volatile Boolean outOfOrder;
	public synchronized boolean isOutOfOrder() {
		if (outOfOrder == null)
			outOfOrder = getProperties().contains(CLDevice.QueueProperties.OutOfOrderExecModeEnable);
		return outOfOrder;
	}

	@InfoName("CL_QUEUE_PROPERTIES")
	public EnumSet<CLDevice.QueueProperties> getProperties() {
		return CLDevice.QueueProperties.getEnumSet(infos.getIntOrLong(getEntity(), CL_QUEUE_PROPERTIES));
	}

	@SuppressWarnings("deprecation")
	public void setProperty(CLDevice.QueueProperties property, boolean enabled) {
		context.getPlatform().requireMinVersionValue("clSetCommandQueueProperty", 1.0, 1.1);
		error(CL.clSetCommandQueueProperty(getEntity(), property.value(), enabled ? CL_TRUE : CL_FALSE, 0));
	}
	

    @Override
    protected void clear() {
        error(CL.clReleaseCommandQueue(getEntity()));
    }

    /**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clFinish.html">clFinish</a>.<br>
	 * Blocks until all previously queued OpenCL commands in this queue are issued to the associated device and have completed. <br>
	 * finish() does not return until all queued commands in this queue have been processed and completed. <br>
	 * finish() is also a synchronization point.
	 */
    public void finish() {
        error(CL.clFinish(getEntity()));
    }

    /**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clFlush.html">clFlush</a>.<br>
	 * Issues all previously queued OpenCL commands in this queue to the device associated with this queue. <br>
	 * flush() only guarantees that all queued commands in this queue get issued to the appropriate device. <br>
	 * There is no guarantee that they will be complete after flush() returns.
	 */
    public void flush() {
        error(CL.clFlush(getEntity()));
    }

	/**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clEnqueueWaitForEvents.html">clEnqueueWaitForEvents</a>.<br>
	 * Enqueues a wait for a specific event or a list of events to complete before any future commands queued in the this queue are executed.
	 */
	public void enqueueWaitForEvents(CLEvent... eventsToWaitFor) {
		context.getPlatform().requireMinVersionValue("clEnqueueWaitForEvents", 1.1, 1.2);
			ReusablePointers ptrs = ReusablePointers.get();
			int[] eventsInCount = ptrs.int1Array;
	Pointer<cl_event> eventsIn = 
		CLAbstractEntity.copyNonNullEntities(eventsToWaitFor, eventsInCount, ptrs.events_in);
        if (eventsIn == null)
            return;
        error(CL.clEnqueueWaitForEvents(getEntity(),  eventsInCount[0], getPeer(eventsIn) ));
	}
	

	/**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clEnqueueMigrateMemObjects.html">clEnqueueMigrateMemObjects</a>.<br>
	 * Enqueues a command to indicate which device a set of memory objects should be associated with.
	 */
	public CLEvent enqueueMigrateMemObjects(CLMem[] memObjects, EnumSet<CLMem.Migration> flags, CLEvent... eventsToWaitFor) {
		context.getPlatform().requireMinVersionValue("clEnqueueMigrateMemObjects", 1.2);
				ReusablePointers ptrs = ReusablePointers.get();
		int[] eventsInCount = ptrs.int1Array;
	Pointer<cl_event> eventsIn = 
		CLAbstractEntity.copyNonNullEntities(eventsToWaitFor, eventsInCount, ptrs.events_in);
		Pointer<cl_event> eventOut = 
		eventsToWaitFor == null || CLEvent.containsFireAndForget(eventsToWaitFor) 
		? null 
		: ptrs.event_out;
		int[] n = ptrs.int1Array;
		Pointer<SizeT> pMems = pointerToEntities(memObjects, n);
		error(CL.clEnqueueMigrateMemObjects(
			getEntity(),
			n[0],
			getPeer(pMems),
			CLMem.Migration.getValue(flags),
			  eventsInCount[0], getPeer(eventsIn) ,  getPeer(eventOut)  		));
		 return  CLEvent.createEventFromPointer(this, eventOut) ; 	}

	// 
	public interface NativeKernel {
		void execute(Pointer[] bufferPointers);
	}

	private static Pointer<SizeT> pointerToEntities(CLAbstractEntity[] entities, int[] n) {
		int nn = 0;
		Pointer<SizeT> pEntities = allocateSizeTs(entities.length);
		for (CLAbstractEntity entity : entities) {
			if (entity != null) {
				pEntities.setSizeTAtIndex(nn++, entity.getEntity());
			}
		}
		n[0] = nn;
		return pEntities;
	}
	/**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clEnqueueNativeKernel.html">clEnqueueNativeKernel</a>.<br>
	 * Enqueues a command to execute a Java callback with direct access to buffer memory.
	 */
	/*
	public CLEvent enqueueNativeKernel(NativeKernel kernel, CLMem[] buffers, CLEvent... eventsToWaitFor) {
		// TODO check 1.1 or 1.2?
		context.getPlatform().requireMinVersionValue("clEnqueueNativeKernel", 1.2);
				ReusablePointers ptrs = ReusablePointers.get();
		int[] eventsInCount = ptrs.int1Array;
	Pointer<cl_event> eventsIn = 
		CLAbstractEntity.copyNonNullEntities(eventsToWaitFor, eventsInCount, ptrs.events_in);
		Pointer<cl_event> eventOut = 
		eventsToWaitFor == null || CLEvent.containsFireAndForget(eventsToWaitFor) 
		? null 
		: ptrs.event_out;
		int[] n = ptrs.int1Array;
		Pointer<SizeT> pMems = pointerToEntities(buffers, n);
		error(CL.clEnqueueNativeKernel(
			getEntity(),
			n[0],
			getPeer(pMems),
			CLMem.Migration.getValue(flags),
			  eventsInCount[0], getPeer(eventsIn) ,  getPeer(eventOut)  		));
		 return  CLEvent.createEventFromPointer(this, eventOut) ; 	}
	*/

	/**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clEnqueueBarrierWithWaitList.html">clEnqueueBarrierWithWaitList</a>.<br>
	 * Enqueue a barrier operation.<br>
	 * The enqueueBarrier() command ensures that all queued commands in command_queue have finished execution before the next batch of commands can begin execution. <br>
	 * enqueueBarrier() is a synchronization point.
	 * @param eventsToWaitFor Events that need to complete before this particular command can be executed. Special value {@link CLEvent#FIRE_AND_FORGET} can be used to avoid returning a CLEvent.  
     * @return Event object that identifies this command and can be used to query or queue a wait for the command to complete, or null if eventsToWaitFor contains {@link CLEvent#FIRE_AND_FORGET}.
	 */
	public CLEvent enqueueBarrier(CLEvent... eventsToWaitFor) {
		if (context.getPlatform().getVersionValue() >= 1.2 ||
			eventsToWaitFor != null && eventsToWaitFor.length > 0)
		{
			context.getPlatform().requireMinVersionValue("clEnqueueBarrierWithWaitList", 1.2);
					ReusablePointers ptrs = ReusablePointers.get();
		int[] eventsInCount = ptrs.int1Array;
	Pointer<cl_event> eventsIn = 
		CLAbstractEntity.copyNonNullEntities(eventsToWaitFor, eventsInCount, ptrs.events_in);
		Pointer<cl_event> eventOut = 
		eventsToWaitFor == null || CLEvent.containsFireAndForget(eventsToWaitFor) 
		? null 
		: ptrs.event_out;
			error(CL.clEnqueueBarrierWithWaitList(
				getEntity(),
				  eventsInCount[0], getPeer(eventsIn) ,  getPeer(eventOut)  			));
			 return  CLEvent.createEventFromPointer(this, eventOut) ; 		} else {
			context.getPlatform().requireMinVersionValue("clEnqueueBarrier", 1.1, 1.2);
			error(CL.clEnqueueBarrier(getEntity()));
			return null;
		}
	}

	/**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clEnqueueMarkerWithWaitList.html">clEnqueueMarkerWithWaitList</a>.<br>
	 * Enqueue a marker command to command_queue. <br>
	 * The marker command returns an event which can be used by to queue a wait on this marker event i.e. wait for all commands queued before the marker command to complete.
	 * @param eventsToWaitFor Events that need to complete before this particular command can be executed. Special value {@link CLEvent#FIRE_AND_FORGET} can be used to avoid returning a CLEvent.  
     * @return Event object that identifies this command and can be used to query or queue a wait for the command to complete, or null if eventsToWaitFor contains {@link CLEvent#FIRE_AND_FORGET}.
	 */
	@Deprecated
	public CLEvent enqueueMarker(CLEvent... eventsToWaitFor) {
		if (context.getPlatform().getVersionValue() >= 1.2 ||
			eventsToWaitFor != null && eventsToWaitFor.length > 0)
		{
			context.getPlatform().requireMinVersionValue("clEnqueueMarkerWithWaitList", 1.2);
					ReusablePointers ptrs = ReusablePointers.get();
		int[] eventsInCount = ptrs.int1Array;
	Pointer<cl_event> eventsIn = 
		CLAbstractEntity.copyNonNullEntities(eventsToWaitFor, eventsInCount, ptrs.events_in);
		Pointer<cl_event> eventOut = 
		eventsToWaitFor == null || CLEvent.containsFireAndForget(eventsToWaitFor) 
		? null 
		: ptrs.event_out;
	    	error(CL.clEnqueueMarkerWithWaitList(
				getEntity(),
				  eventsInCount[0], getPeer(eventsIn) ,  getPeer(eventOut)  			));
			 return  CLEvent.createEventFromPointer(this, eventOut) ; 		} else {
			context.getPlatform().requireMinVersionValue("clEnqueueMarker", 1.1, 1.2);
				ReusablePointers ptrs = ReusablePointers.get();
			Pointer<cl_event> eventOut = ptrs.event_out;
			error(CL.clEnqueueMarker(getEntity(), getPeer(eventOut)));
			 return  CLEvent.createEventFromPointer(this, eventOut) ; 		}
	}

	/**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clEnqueueAcquireGLObjects.html">clEnqueueAcquireGLObjects</a>.<br>
	 * Used to acquire OpenCL memory objects that have been created from OpenGL objects. <br>
	 * These objects need to be acquired before they can be used by any OpenCL commands queued to a command-queue. <br>
	 * The OpenGL objects are acquired by the OpenCL context associated with this queue and can therefore be used by all command-queues associated with the OpenCL context.
	 * @param objects CL memory objects that correspond to GL objects.
	 * @param eventsToWaitFor Events that need to complete before this particular command can be executed. Special value {@link CLEvent#FIRE_AND_FORGET} can be used to avoid returning a CLEvent.  
     * @return Event object that identifies this command and can be used to query or queue a wait for the command to complete, or null if eventsToWaitFor contains {@link CLEvent#FIRE_AND_FORGET}.
	 */
	public CLEvent enqueueAcquireGLObjects(CLMem[] objects, CLEvent... eventsToWaitFor) {
        		ReusablePointers ptrs = ReusablePointers.get();
		int[] eventsInCount = ptrs.int1Array;
	Pointer<cl_event> eventsIn = 
		CLAbstractEntity.copyNonNullEntities(eventsToWaitFor, eventsInCount, ptrs.events_in);
		Pointer<cl_event> eventOut = 
		eventsToWaitFor == null || CLEvent.containsFireAndForget(eventsToWaitFor) 
		? null 
		: ptrs.event_out;
		Pointer<SizeT> mems = allocateSizeTs(objects.length);
		for (int i = 0; i < objects.length; i++) {
			mems.setSizeTAtIndex(i, objects[i].getEntity());
		}
        error(CL.clEnqueueAcquireGLObjects(
			getEntity(), 
			objects.length,
			getPeer(mems),
			  eventsInCount[0], getPeer(eventsIn) ,  getPeer(eventOut)  		));
		 return  CLEvent.createEventFromPointer(this, eventOut) ; 	}

	/**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clEnqueueReleaseGLObjects.html">clEnqueueReleaseGLObjects</a>.<br>
	 * Used to release OpenCL memory objects that have been created from OpenGL objects. <br>
	 * These objects need to be released before they can be used by OpenGL. <br>
	 * The OpenGL objects are released by the OpenCL context associated with this queue.
	 * @param objects CL memory objects that correpond to GL objects.
	 * @param eventsToWaitFor Events that need to complete before this particular command can be executed. Special value {@link CLEvent#FIRE_AND_FORGET} can be used to avoid returning a CLEvent.  
     * @return Event object that identifies this command and can be used to query or queue a wait for the command to complete, or null if eventsToWaitFor contains {@link CLEvent#FIRE_AND_FORGET}.
	 */
	public CLEvent enqueueReleaseGLObjects(CLMem[] objects, CLEvent... eventsToWaitFor) {
        		ReusablePointers ptrs = ReusablePointers.get();
		int[] eventsInCount = ptrs.int1Array;
	Pointer<cl_event> eventsIn = 
		CLAbstractEntity.copyNonNullEntities(eventsToWaitFor, eventsInCount, ptrs.events_in);
		Pointer<cl_event> eventOut = 
		eventsToWaitFor == null || CLEvent.containsFireAndForget(eventsToWaitFor) 
		? null 
		: ptrs.event_out;
		Pointer<?> mems = getEntities(objects, (Pointer)allocateSizeTs(objects.length));
        error(CL.clEnqueueReleaseGLObjects(
			getEntity(), 
			objects.length, 
			getPeer(mems),
			  eventsInCount[0], getPeer(eventsIn) ,  getPeer(eventOut)  		));
		 return  CLEvent.createEventFromPointer(this, eventOut) ; 	}
}
