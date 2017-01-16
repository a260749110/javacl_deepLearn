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
import static com.nativelibs4java.opencl.JavaCL.check;
import static com.nativelibs4java.opencl.library.IOpenCLLibrary.CL_FALSE;
import static com.nativelibs4java.opencl.library.IOpenCLLibrary.CL_IMAGE_ELEMENT_SIZE;
import static com.nativelibs4java.opencl.library.IOpenCLLibrary.CL_IMAGE_FORMAT;
import static com.nativelibs4java.opencl.library.IOpenCLLibrary.CL_TRUE;
import static com.nativelibs4java.util.NIOUtils.directCopy;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import com.nativelibs4java.opencl.library.cl_image_format;
import com.nativelibs4java.opencl.library.IOpenCLLibrary.cl_event;
import com.nativelibs4java.opencl.library.IOpenCLLibrary.cl_mem;
import com.nativelibs4java.util.NIOUtils;

import com.nativelibs4java.util.Pair;
import org.bridj.*;
import static org.bridj.Pointer.*;


/**
 * OpenCL Image Memory Object.<br>
 * An image object is used to store a two- or three- dimensional texture, frame-buffer or image<br>
 * An image object is used to represent a buffer that can be used as a texture or a frame-buffer. The elements of an image object are selected from a list of predefined image formats.
 * @author Oliveir Chafik
 */
public abstract class CLImage extends CLMem {

	CLImageFormat format;
	CLImage(CLContext context, long entityPeer, CLImageFormat format) {
        super(context, -1, entityPeer);
		this.format = format;
	}

	protected abstract long[] getDimensions();

	/**
	 * Return image format descriptor specified when image is created with CLContext.create{Input|Output|InputOutput}{2D|3D}.
	 */
	@InfoName("CL_IMAGE_FORMAT")
	public CLImageFormat getFormat() {
		if (format == null) {
			format = new CLImageFormat(new cl_image_format(infos.getMemory(getEntity(), CL_IMAGE_FORMAT)));
		}
		return format;
	}

	/**
	 * Return size of each element of the image memory object given by image. <br>
	 * An element is made up of n channels. The value of n is given in cl_image_format descriptor.
	 */
	@InfoName("CL_IMAGE_ELEMENT_SIZE")
	public long getElementSize() {
		return infos.getIntOrLong(getEntity(), CL_IMAGE_ELEMENT_SIZE);
	}

	/**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clEnqueueReadImage.html">clEnqueueReadImage</a>.<br>
     */
    protected CLEvent read(CLQueue queue, Pointer<SizeT> origin, Pointer<SizeT> region, long rowPitch, long slicePitch, Buffer out, boolean blocking, CLEvent... eventsToWaitFor) {
		return read(queue, origin, region, rowPitch, slicePitch, pointerToBuffer(out), blocking, eventsToWaitFor);
	}
	/**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clEnqueueReadImage.html">clEnqueueReadImage</a>.<br>
     */
    protected CLEvent read(CLQueue queue, Pointer<SizeT> origin, Pointer<SizeT> region, long rowPitch, long slicePitch, Pointer<?> out, boolean blocking, CLEvent... eventsToWaitFor) {
		/*if (!out.isDirect()) {

		}*/
				ReusablePointers ptrs = ReusablePointers.get();
		int[] eventsInCount = ptrs.int1Array;
	Pointer<cl_event> eventsIn = 
		CLAbstractEntity.copyNonNullEntities(eventsToWaitFor, eventsInCount, ptrs.events_in);
		Pointer<cl_event> eventOut = 
		blocking || eventsToWaitFor == null || CLEvent.containsFireAndForget(eventsToWaitFor) 
		? null 
		: ptrs.event_out;
        error(CL.clEnqueueReadImage(
        	queue.getEntity(), 
        	getEntity(),
			blocking ? CL_TRUE : CL_FALSE,
			getPeer(origin),
			getPeer(region),
			rowPitch,
			slicePitch,
			getPeer(out),
			  eventsInCount[0], getPeer(eventsIn) ,  getPeer(eventOut)  		));
		 return  CLEvent.createEventFromPointer(queue, eventOut) ; 	}

	/**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clEnqueueWriteImage.html">clEnqueueWriteImage</a>.<br>
     */
    protected CLEvent write(CLQueue queue, Pointer<SizeT> origin, Pointer<SizeT> region, long rowPitch, long slicePitch, Buffer in, boolean blocking, CLEvent... eventsToWaitFor) {
		return write(queue, origin, region, rowPitch, slicePitch, pointerToBuffer(in), blocking, eventsToWaitFor);
	}
	/**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clEnqueueWriteImage.html">clEnqueueWriteImage</a>.<br>
     */
    protected CLEvent write(CLQueue queue, Pointer<SizeT> origin, Pointer<SizeT> region, long rowPitch, long slicePitch, Pointer<?> in, boolean blocking, CLEvent... eventsToWaitFor) {
				ReusablePointers ptrs = ReusablePointers.get();
		int[] eventsInCount = ptrs.int1Array;
	Pointer<cl_event> eventsIn = 
		CLAbstractEntity.copyNonNullEntities(eventsToWaitFor, eventsInCount, ptrs.events_in);
		Pointer<cl_event> eventOut = 
		blocking || eventsToWaitFor == null || CLEvent.containsFireAndForget(eventsToWaitFor) 
		? null 
		: ptrs.event_out;
        error(CL.clEnqueueWriteImage(
        	queue.getEntity(),
        	getEntity(),
			blocking ? CL_TRUE : CL_FALSE,
			getPeer(origin),
			getPeer(region),
			rowPitch,
			slicePitch,
			getPeer(in),
			  eventsInCount[0], getPeer(eventsIn) ,  getPeer(eventOut)  		));
		CLEvent evt =  CLEvent.createEventFromPointer(queue, eventOut) ;

		if (!blocking) {
			final Pointer<?> toHold = in;
			evt.invokeUponCompletion(new Runnable() {
				public void run() {
					// Make sure the GC held a reference to directData until the write was completed !
					toHold.order();
				}
			});
		}

		return evt;
	}

    /**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clEnqueueFillImage.html">clEnqueueFillImage</a>.<br>
     * @param queue
     * @param queue Queue on which to enqueue this fill buffer command.
     * @param color Color components to fill the buffer with.
	 * @param eventsToWaitFor Events that need to complete before this particular command can be executed. Special value {@link CLEvent#FIRE_AND_FORGET} can be used to avoid returning a CLEvent.  
     * @return Event object that identifies this command and can be used to query or queue a wait for the command to complete, or null if eventsToWaitFor contains {@link CLEvent#FIRE_AND_FORGET}.
     */
    public CLEvent fillImage(CLQueue queue, Object color, CLEvent... eventsToWaitFor) {
    	long[] region = getDimensions();
    	long[] origin = new long[region.length];
    	return fillImage(queue, color, origin, region, eventsToWaitFor);
    }

    /**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clEnqueueFillImage.html">clEnqueueFillImage</a>.<br>
     * @param queue
     * @param queue Queue on which to enqueue this fill buffer command.
     * @param color Color components to fill the buffer with.
     * @param origin Origin point.
     * @param region Size of the region to fill.
	 * @param eventsToWaitFor Events that need to complete before this particular command can be executed. Special value {@link CLEvent#FIRE_AND_FORGET} can be used to avoid returning a CLEvent.  
     * @return Event object that identifies this command and can be used to query or queue a wait for the command to complete, or null if eventsToWaitFor contains {@link CLEvent#FIRE_AND_FORGET}.
     */
    public CLEvent fillImage(CLQueue queue, Object color, long[] origin, long[] region, CLEvent... eventsToWaitFor) {
    	context.getPlatform().requireMinVersionValue("clEnqueueFillImage", 1.2);
		Pointer<?> pColor;
		if (color instanceof int[]) {
			pColor = pointerToInts((int[]) color);
		} else if (color instanceof float[]) {
			pColor = pointerToFloats((float[]) color);
		} else {
			throw new IllegalArgumentException("Color should be an int[] or a float[] with 4 elements.");
		}
		check(pColor.getValidElements() == 4, "Color should have 4 elements.");

				ReusablePointers ptrs = ReusablePointers.get();
		int[] eventsInCount = ptrs.int1Array;
	Pointer<cl_event> eventsIn = 
		CLAbstractEntity.copyNonNullEntities(eventsToWaitFor, eventsInCount, ptrs.events_in);
		Pointer<cl_event> eventOut = 
		eventsToWaitFor == null || CLEvent.containsFireAndForget(eventsToWaitFor) 
		? null 
		: ptrs.event_out;
		error(CL.clEnqueueFillImage(
			queue.getEntity(),
			getEntity(),
			getPeer(pColor),
			getPeer(writeOrigin(origin, ptrs.sizeT3_1)),
			getPeer(writeRegion(region, ptrs.sizeT3_2)),
			  eventsInCount[0], getPeer(eventsIn) ,  getPeer(eventOut)  		));
         return  CLEvent.createEventFromPointer(queue, eventOut) ;     }

	// clEnqueueFillImage (	cl_command_queue command_queue,
 // 	cl_mem image,
 // 	const void *fill_color,
 // 	const size_t *origin,
 // 	const size_t *region,
 // 	cl_uint num_events_in_wait_list,
 // 	const cl_event *event_wait_list,
 // 	cl_event *event)

    protected Pair<ByteBuffer, CLEvent> map(CLQueue queue, MapFlags flags,
            Pointer<SizeT> offset3, Pointer<SizeT> length3,
            Long imageRowPitch,
            Long imageSlicePitch,
            boolean blocking, CLEvent... eventsToWaitFor)
    {
        if (flags == MapFlags.WriteInvalidateRegion) {
            context.getPlatform().requireMinVersionValue("CL_MAP_WRITE_INVALIDATE_REGION", 1.2);
        }
				ReusablePointers ptrs = ReusablePointers.get();
		int[] eventsInCount = ptrs.int1Array;
	Pointer<cl_event> eventsIn = 
		CLAbstractEntity.copyNonNullEntities(eventsToWaitFor, eventsInCount, ptrs.events_in);
		Pointer<cl_event> eventOut = 
		blocking || eventsToWaitFor == null || CLEvent.containsFireAndForget(eventsToWaitFor) 
		? null 
		: ptrs.event_out;
			Pointer<Integer> pErr = ptrs.pErr;
        long mappedPeer = CL.clEnqueueMapImage(
            queue.getEntity(), 
            getEntity(), 
            blocking ? CL_TRUE : CL_FALSE,
            flags.value(),
			getPeer(offset3),
            getPeer(length3),
            imageRowPitch == null ? 0 : getPeer(ptrs.sizeT3_1.pointerToSizeTs((long)imageRowPitch)),
            imageSlicePitch == null ? 0 : getPeer(ptrs.sizeT3_2.pointerToSizeTs((long)imageSlicePitch)),
			  eventsInCount[0], getPeer(eventsIn) ,  getPeer(eventOut)  ,
			getPeer(pErr)
		);
			error(pErr.getInt());
        return new Pair<ByteBuffer, CLEvent>(
			pointerToAddress(mappedPeer).getByteBuffer(getByteCount()),
			 CLEvent.createEventFromPointer(queue, eventOut) 		);
    }

    /**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clEnqueueUnmapMemObject.html">clEnqueueUnmapMemObject</a>.<br>
     * see {@link CLImage2D#map(com.nativelibs4java.opencl.CLQueue, com.nativelibs4java.opencl.CLMem.MapFlags, com.nativelibs4java.opencl.CLEvent[]) }
     * see {@link CLImage3D#map(com.nativelibs4java.opencl.CLQueue, com.nativelibs4java.opencl.CLMem.MapFlags, com.nativelibs4java.opencl.CLEvent[]) }
     * @param queue
     * @param buffer
	 * @param eventsToWaitFor Events that need to complete before this particular command can be executed. Special value {@link CLEvent#FIRE_AND_FORGET} can be used to avoid returning a CLEvent.  
     * @return Event object that identifies this command and can be used to query or queue a wait for the command to complete, or null if eventsToWaitFor contains {@link CLEvent#FIRE_AND_FORGET}.
     */
    public CLEvent unmap(CLQueue queue, ByteBuffer buffer, CLEvent... eventsToWaitFor) {
        		ReusablePointers ptrs = ReusablePointers.get();
		int[] eventsInCount = ptrs.int1Array;
	Pointer<cl_event> eventsIn = 
		CLAbstractEntity.copyNonNullEntities(eventsToWaitFor, eventsInCount, ptrs.events_in);
		Pointer<cl_event> eventOut = 
		eventsToWaitFor == null || CLEvent.containsFireAndForget(eventsToWaitFor) 
		? null 
		: ptrs.event_out;
        Pointer<?> pBuffer = pointerToBuffer(buffer);
        error(CL.clEnqueueUnmapMemObject(queue.getEntity(), getEntity(), getPeer(pBuffer),   eventsInCount[0], getPeer(eventsIn) ,  getPeer(eventOut)  ));
		 return  CLEvent.createEventFromPointer(queue, eventOut) ;     }

    protected abstract Pointer<SizeT> writeOrigin(long[] origin, ReusablePointer out);
    protected abstract Pointer<SizeT> writeRegion(long[] region, ReusablePointer out);

    /**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clEnqueueCopyImage.html">clEnqueueCopyImage</a>.<br>
     * @param queue
	 * @param eventsToWaitFor Events that need to complete before this particular command can be executed. Special value {@link CLEvent#FIRE_AND_FORGET} can be used to avoid returning a CLEvent.  
     * @return Event object that identifies this command and can be used to query or queue a wait for the command to complete, or null if eventsToWaitFor contains {@link CLEvent#FIRE_AND_FORGET}.
     */
    public CLEvent copyTo(CLQueue queue, CLImage destination, CLEvent... eventsToWaitFor) {
        long[] region = getDimensions();
        long[] origin = new long[region.length];
        return copyTo(queue, destination, origin, origin, region, eventsToWaitFor);
    }

    /**
  * Calls <a href="http://www.khronos.org/registry/cl/sdk/1.2/docs/man/xhtml/clEnqueueCopyImage.html">clEnqueueCopyImage</a>.<br>
     * @param queue
	 * @param eventsToWaitFor Events that need to complete before this particular command can be executed. Special value {@link CLEvent#FIRE_AND_FORGET} can be used to avoid returning a CLEvent.  
     * @return Event object that identifies this command and can be used to query or queue a wait for the command to complete, or null if eventsToWaitFor contains {@link CLEvent#FIRE_AND_FORGET}.
     */
    public CLEvent copyTo(CLQueue queue, CLImage destination, long[] sourceOrigin, long[] destinationOrigin, long[] region, CLEvent... eventsToWaitFor) {
        		ReusablePointers ptrs = ReusablePointers.get();
		int[] eventsInCount = ptrs.int1Array;
	Pointer<cl_event> eventsIn = 
		CLAbstractEntity.copyNonNullEntities(eventsToWaitFor, eventsInCount, ptrs.events_in);
		Pointer<cl_event> eventOut = 
		eventsToWaitFor == null || CLEvent.containsFireAndForget(eventsToWaitFor) 
		? null 
		: ptrs.event_out;
        error(CL.clEnqueueCopyImage(
            queue.getEntity(),
            getEntity(),
            destination.getEntity(),
            getPeer(writeOrigin(sourceOrigin, ptrs.sizeT3_1)),
            getPeer(writeOrigin(destinationOrigin, ptrs.sizeT3_2)),
            getPeer(writeRegion(region, ptrs.sizeT3_3)),
              eventsInCount[0], getPeer(eventsIn) ,  getPeer(eventOut)  ));
		 return  CLEvent.createEventFromPointer(queue, eventOut) ;     }
}
