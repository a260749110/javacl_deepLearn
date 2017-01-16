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
import org.bridj.util.*;
import java.lang.reflect.Type;
/**
 * Size in bytes of a __local argument.
 */
public class LocalSize {
	long size;
	public LocalSize(long size) {
		this.size = size;
	}
	
	
	/**
	 * Returns the size in bytes of an array of int values of the specified length.<br>
	 * @return <code>arrayLength * sizeof(int) = arrayLength * 4</code>
	 */
	public static LocalSize ofIntArray(long arrayLength) {
		return new LocalSize(arrayLength * 4);
	}
	
	
	/**
	 * Returns the size in bytes of an array of long values of the specified length.<br>
	 * @return <code>arrayLength * sizeof(long) = arrayLength * 8</code>
	 */
	public static LocalSize ofLongArray(long arrayLength) {
		return new LocalSize(arrayLength * 8);
	}
	
	
	/**
	 * Returns the size in bytes of an array of short values of the specified length.<br>
	 * @return <code>arrayLength * sizeof(short) = arrayLength * 2</code>
	 */
	public static LocalSize ofShortArray(long arrayLength) {
		return new LocalSize(arrayLength * 2);
	}
	
	
	/**
	 * Returns the size in bytes of an array of byte values of the specified length.<br>
	 * @return <code>arrayLength * sizeof(byte) = arrayLength * 1</code>
	 */
	public static LocalSize ofByteArray(long arrayLength) {
		return new LocalSize(arrayLength * 1);
	}
	
	
	/**
	 * Returns the size in bytes of an array of char values of the specified length.<br>
	 * @return <code>arrayLength * sizeof(char) = arrayLength * 2</code>
	 */
	public static LocalSize ofCharArray(long arrayLength) {
		return new LocalSize(arrayLength * 2);
	}
	
	
	/**
	 * Returns the size in bytes of an array of float values of the specified length.<br>
	 * @return <code>arrayLength * sizeof(float) = arrayLength * 4</code>
	 */
	public static LocalSize ofFloatArray(long arrayLength) {
		return new LocalSize(arrayLength * 4);
	}
	
	
	/**
	 * Returns the size in bytes of an array of double values of the specified length.<br>
	 * @return <code>arrayLength * sizeof(double) = arrayLength * 8</code>
	 */
	public static LocalSize ofDoubleArray(long arrayLength) {
		return new LocalSize(arrayLength * 8);
	}
	
		
	/**
	 * Returns the size in bytes of an array of T values of the specified length.<br>
	 */
	public static LocalSize ofArray(long arrayLength, Type componentType) {
		PointerIO io = PointerIO.getInstance(componentType);
		if (io == null)
			throw new RuntimeException("Unsupported type : " + Utils.toString(componentType));
		return new LocalSize(arrayLength * io.getTargetSize());
	}
}
