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
import org.bridj.*;

/**
 *
 * @author ochafik
 */
final class ReusablePointer {
    private final Pointer<?> pointer;
    private final long bytesCapacity;

    public ReusablePointer(long bytesCapacity) {
        this.bytesCapacity = bytesCapacity;
        this.pointer = allocateAlignedBytes(bytesCapacity).withoutValidityInformation();
    }

    static Pointer<?> allocateAlignedBytes(long count) {
      // Allocate memory aligned to 128 bytes to match alignment of cl_double16.
      return Pointer.allocateAlignedBytes(null /* io */, count, 128 /* alignment */, null /* beforeDeallocation */);
    }

    public Pointer<Integer> pointerToInts(int... values) {
        if (values == null)
            return null;
        long needed = 4 * values.length;
        if (needed > bytesCapacity) {
            return Pointer.pointerToInts(values);
        } else {
            return (Pointer)pointer.setInts(values);
        }
    }
    public Pointer<SizeT> pointerToSizeTs(long... values) {
        if (values == null)
            return null;
        long needed = SizeT.SIZE * values.length;
        if (needed > bytesCapacity) {
            return Pointer.pointerToSizeTs(values);
        } else {
            return (Pointer)pointer.setSizeTs(values);
        }
    }
    public Pointer<SizeT> pointerToSizeTs(int... values) {
        if (values == null)
            return null;
        long needed = SizeT.SIZE * values.length;
        if (needed > bytesCapacity) {
            return Pointer.pointerToSizeTs(values);
        } else {
            return (Pointer)pointer.setSizeTs(values);
        }
    }
    public <T> Pointer<T> allocatedBytes(int needed) {
        if (needed == 0)
            return null;
        if (needed > bytesCapacity) {
            return (Pointer)allocateAlignedBytes(needed);
        } else {
            return (Pointer)pointer;
        }
    }
    public <T> Pointer<T> allocatedSizeTs(int needed) {
    	return allocatedBytes(needed * SizeT.SIZE);
    }
    public <T> Pointer<T> allocatedInts(int needed) {
    	return allocatedBytes(needed * 4);
    }
}
