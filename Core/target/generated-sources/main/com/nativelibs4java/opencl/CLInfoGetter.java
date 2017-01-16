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

import org.bridj.*;
import static org.bridj.Pointer.*;

import org.bridj.ann.Ptr;

/**
 *
 * @author ochafik
 */
abstract class CLInfoGetter {

    protected abstract int getInfo(long entity, int infoTypeEnum, long size, Pointer out, Pointer<SizeT> sizeOut);

    public String getString(@Ptr long entity, int infoName) {
        	ReusablePointers ptrs = ReusablePointers.get();
        Pointer<SizeT> pLen = ptrs.sizeT1;
        error(getInfo(entity, infoName, 0, null, pLen));

        long len = pLen.getSizeT();
        if (len == 0) {
            return "";
        }
        Pointer<?> buffer = allocateBytes(len + 1);
        error(getInfo(entity, infoName, len, buffer, null));
        String s = buffer.getCString();
        buffer.release();
        return s;
    }

    public Pointer getPointer(@Ptr long entity, int infoName) {
        	ReusablePointers ptrs = ReusablePointers.get();
        Pointer<SizeT> pLen = ptrs.sizeT1;
        Pointer<Pointer<?>> mem = ptrs.ptr1;
        error(getInfo(entity, infoName, Pointer.SIZE, mem, pLen));
        if (pLen.getSizeT() != Pointer.SIZE) {
            throw new RuntimeException("Not a pointer : len = " + pLen.get());
        }
        return mem.get();
    }

    public Pointer<?> getMemory(@Ptr long entity, int infoName) {
        	ReusablePointers ptrs = ReusablePointers.get();
        Pointer<SizeT> pLen = ptrs.sizeT1;
        error(getInfo(entity, infoName, 0, null, pLen));

        int len = (int)pLen.getSizeT();
        Pointer<?> buffer = allocateBytes(len);
        error(getInfo(entity, infoName, len, buffer, null));

        return buffer;
    }

    public long[] getNativeSizes(@Ptr long entity, int infoName, int n) {
        long nBytes = SizeT.SIZE * n;
        	ReusablePointers ptrs = ReusablePointers.get();
        Pointer<SizeT> pLen = ptrs.sizeT3_1.pointerToSizeTs(nBytes);
        Pointer<SizeT> mem = ptrs.sizeT3_2.allocatedSizeTs(n);
        error(getInfo(entity, infoName, nBytes, mem, pLen));

        int actualLen = (int)pLen.getSizeT();
        if (actualLen != nBytes) {
            throw new RuntimeException("Not a Size[" + n + "] : len = " + actualLen);
        }
        return mem.getSizeTs(n);
    }

    public int getOptionalFeatureInt(@Ptr long entity, int infoName) {
    	try {
    		return getInt(entity, infoName);
    	} catch (CLException.InvalidValue ex) {
    		throw new UnsupportedOperationException("Cannot get value " + infoName, ex);
    	} catch (CLException.InvalidOperation ex) {
    		throw new UnsupportedOperationException("Cannot get value " + infoName, ex);
    	}
    }
    public int getInt(@Ptr long entity, int infoName) {
        return (int)getIntOrLong(entity, infoName);
    }

    public boolean getBool(@Ptr long entity, int infoName) {
        	ReusablePointers ptrs = ReusablePointers.get();
        Pointer<SizeT> pLen = ptrs.sizeT1;
        Pointer<?> mem = ptrs.sizeT3_1.allocatedBytes(8);
        error(getInfo(entity, infoName, 8, mem, pLen));

        long len = pLen.getSizeT();
        switch ((int)len) {
        	case 1: 
        		return mem.getByte() != 0;
        	case 2:
        		return mem.getShort() != 0;
        	case 4:
        		return mem.getInt() != 0;
        	case 8:
        		return mem.getLong() != 0;
        	case 0:
        		// HACK to accommodate ATI Stream on Linux 32 bits (CLPlatform.isAvailable())
        		//if (JNI.isLinux())
        		return true;
        default:
            throw new RuntimeException("Not a BOOL : len = " + len);
        }
    }

    public long getIntOrLong(@Ptr long entity, int infoName) {
        	ReusablePointers ptrs = ReusablePointers.get();
        Pointer<SizeT> pLen = ptrs.sizeT1;
        Pointer<Long> mem = ptrs.long1;
        error(getInfo(entity, infoName, 8, mem, pLen));

        switch ((int)pLen.getSizeT()) {
            case 4:
                return mem.getInt();
            case 8:
                return mem.getLong();
            default:
                throw new RuntimeException("Not a native long : len = " + pLen.get());
        }
    }
}
