package com.nativelibs4java.opencl.library;
import com.nativelibs4java.opencl.library.IOpenCLLibrary.cl_mem;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
import org.bridj.ann.Ptr;
/**
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> or <a href="http://bridj.googlecode.com/">BridJ</a> .
 */
@Library("OpenCL") 
public class cl_image_desc extends StructObject {
	/** C type : cl_mem_object_type */
	@Field(0) 
	public int image_type() {
		return this.io.getIntField(this, 0);
	}
	/** C type : cl_mem_object_type */
	@Field(0) 
	public cl_image_desc image_type(int image_type) {
		this.io.setIntField(this, 0, image_type);
		return this;
	}
	@Ptr 
	@Field(1) 
	public long image_width() {
		return this.io.getSizeTField(this, 1);
	}
	@Ptr 
	@Field(1) 
	public cl_image_desc image_width(long image_width) {
		this.io.setSizeTField(this, 1, image_width);
		return this;
	}
	@Ptr 
	@Field(2) 
	public long image_height() {
		return this.io.getSizeTField(this, 2);
	}
	@Ptr 
	@Field(2) 
	public cl_image_desc image_height(long image_height) {
		this.io.setSizeTField(this, 2, image_height);
		return this;
	}
	@Ptr 
	@Field(3) 
	public long image_depth() {
		return this.io.getSizeTField(this, 3);
	}
	@Ptr 
	@Field(3) 
	public cl_image_desc image_depth(long image_depth) {
		this.io.setSizeTField(this, 3, image_depth);
		return this;
	}
	@Ptr 
	@Field(4) 
	public long image_array_size() {
		return this.io.getSizeTField(this, 4);
	}
	@Ptr 
	@Field(4) 
	public cl_image_desc image_array_size(long image_array_size) {
		this.io.setSizeTField(this, 4, image_array_size);
		return this;
	}
	@Ptr 
	@Field(5) 
	public long image_row_pitch() {
		return this.io.getSizeTField(this, 5);
	}
	@Ptr 
	@Field(5) 
	public cl_image_desc image_row_pitch(long image_row_pitch) {
		this.io.setSizeTField(this, 5, image_row_pitch);
		return this;
	}
	@Ptr 
	@Field(6) 
	public long image_slice_pitch() {
		return this.io.getSizeTField(this, 6);
	}
	@Ptr 
	@Field(6) 
	public cl_image_desc image_slice_pitch(long image_slice_pitch) {
		this.io.setSizeTField(this, 6, image_slice_pitch);
		return this;
	}
	/** C type : cl_uint */
	@Field(7) 
	public int num_mip_levels() {
		return this.io.getIntField(this, 7);
	}
	/** C type : cl_uint */
	@Field(7) 
	public cl_image_desc num_mip_levels(int num_mip_levels) {
		this.io.setIntField(this, 7, num_mip_levels);
		return this;
	}
	/** C type : cl_uint */
	@Field(8) 
	public int num_samples() {
		return this.io.getIntField(this, 8);
	}
	/** C type : cl_uint */
	@Field(8) 
	public cl_image_desc num_samples(int num_samples) {
		this.io.setIntField(this, 8, num_samples);
		return this;
	}
	/** C type : cl_mem */
	@Field(9) 
	public cl_mem buffer() {
		return this.io.getTypedPointerField(this, 9);
	}
	/** C type : cl_mem */
	@Field(9) 
	public cl_image_desc buffer(cl_mem buffer) {
		this.io.setPointerField(this, 9, buffer);
		return this;
	}
	public cl_image_desc() {
		super();
	}
	public cl_image_desc(Pointer pointer) {
		super(pointer);
	}
}
