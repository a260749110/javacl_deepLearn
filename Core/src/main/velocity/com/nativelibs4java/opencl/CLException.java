#parse("main/Header.vm")
package com.nativelibs4java.opencl;

import static com.nativelibs4java.opencl.JavaCL.log;
import static com.nativelibs4java.opencl.library.OpenCLLibrary.*;
import static com.nativelibs4java.opencl.library.IOpenCLLibrary.*;

import com.nativelibs4java.opencl.library.OpenCLLibrary;
import com.ochafik.util.string.StringUtils;

import org.bridj.*;

import java.util.*;
import java.lang.reflect.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * OpenCL error
 * @author ochafik
 */
@SuppressWarnings("serial")
public class CLException extends RuntimeException {
    protected int code;
    CLException(String message, int code) {
        super(message);
        this.code = code;
    }
    public int getCode() {
        return code;
    }

  @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
  @interface ErrorCode {
    int value();
  }

	public static class CLVersionException extends CLException {
		public CLVersionException(String message) {
			super(message, 0);
		}
	}

	public static class CLTypedException extends CLException {
		protected String message;
		public CLTypedException() {
			super("", 0);
			ErrorCode code = getClass().getAnnotation(ErrorCode.class);
			this.code = code.value();
			this.message = getClass().getSimpleName();
		}

		@Override
		public String getMessage() {
			return message + logSuffix;
		}
		
		void setKernelArg(CLKernel kernel, int argIndex, long size, Pointer<?> ptr) {
			message += " (kernel name = " + kernel.getFunctionName();
      message += ", num args = " + kernel.getNumArgs();
      message += ", arg index = " + argIndex;
      message += ", arg size = " + size;
			CLProgram program = kernel.getProgram();
			if (program != null)
				message += ", source = <<<\n\t" + program.getSource().replaceAll("\n", "\n\t");
			
			message += "\n>>> )";
		}
	
	}

	@ErrorCode(CL_DEVICE_PARTITION_FAILED)
	public static class DevicePartitionFailed extends CLTypedException {}
	@ErrorCode(CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST)
	public static class ExecStatusErrorForEventsInWaitList extends CLTypedException {}
	@ErrorCode(CL_MISALIGNED_SUB_BUFFER_OFFSET)
	public static class MisalignedSubBufferOffset extends CLTypedException {}
	@ErrorCode(CL_COMPILE_PROGRAM_FAILURE)
	public static class CompileProgramFailure extends CLTypedException {}
	@ErrorCode(CL_LINKER_NOT_AVAILABLE)
	public static class LinkerNotAvailable extends CLTypedException {}
	@ErrorCode(CL_LINK_PROGRAM_FAILURE)
	public static class LinkProgramFailure extends CLTypedException {}
	@ErrorCode(CL_KERNEL_ARG_INFO_NOT_AVAILABLE)
	public static class KernelArgInfoNotAvailable extends CLTypedException {}
	@ErrorCode(CL_IMAGE_FORMAT_MISMATCH)
	public static class ImageFormatMismatch extends CLTypedException {}
	@ErrorCode(CL_PROFILING_INFO_NOT_AVAILABLE)
	public static class ProfilingInfoNotAvailable extends CLTypedException {}
	@ErrorCode(CL_DEVICE_NOT_AVAILABLE)
	public static class DeviceNotAvailable extends CLTypedException {}
	@ErrorCode(CL_OUT_OF_RESOURCES)
	public static class OutOfResources extends CLTypedException {}
    @ErrorCode(CL_COMPILER_NOT_AVAILABLE)
	public static class CompilerNotAvailable extends CLTypedException {}
	@ErrorCode(CL_INVALID_GLOBAL_WORK_SIZE)
    public static class InvalidGlobalWorkSize extends CLTypedException {}
	@ErrorCode(CL_MAP_FAILURE)
	public static class MapFailure extends CLTypedException {}
	@ErrorCode(CL_MEM_OBJECT_ALLOCATION_FAILURE)
	public static class MemObjectAllocationFailure extends CLTypedException {}
    @ErrorCode(CL_INVALID_EVENT_WAIT_LIST)
	public static class InvalidEventWaitList extends CLTypedException {}
	@ErrorCode(CL_INVALID_ARG_INDEX)
	public static class InvalidArgIndex extends CLTypedException {}
	@ErrorCode(CL_INVALID_ARG_SIZE)
	public static class InvalidArgSize extends CLTypedException {}
	@ErrorCode(CL_INVALID_ARG_VALUE)
	public static class InvalidArgValue extends CLTypedException {}
	@ErrorCode(CL_INVALID_BINARY)
	public static class InvalidBinary extends CLTypedException {}
	@ErrorCode(CL_INVALID_EVENT)
	public static class InvalidEvent extends CLTypedException {}
	@ErrorCode(CL_INVALID_IMAGE_FORMAT_DESCRIPTOR)
	public static class InvalidImageFormatDescriptor extends CLTypedException {}
	@ErrorCode(CL_INVALID_IMAGE_SIZE)
	public static class InvalidImageSize extends CLTypedException {}
	@ErrorCode(CL_INVALID_WORK_DIMENSION)
	public static class InvalidWorkDimension extends CLTypedException {}
	@ErrorCode(CL_INVALID_WORK_GROUP_SIZE)
	public static class InvalidWorkGroupSize extends CLTypedException {}
	@ErrorCode(CL_INVALID_WORK_ITEM_SIZE)
	public static class InvalidWorkItemSize extends CLTypedException {}
	@ErrorCode(CL_INVALID_OPERATION)
	public static class InvalidOperation extends CLTypedException {}
	@ErrorCode(CL_INVALID_BUFFER_SIZE)
	public static class InvalidBufferSize extends CLTypedException {}
	@ErrorCode(CL_INVALID_GLOBAL_OFFSET)
	public static class InvalidGlobalOffset extends CLTypedException {}
	@ErrorCode(CL_OUT_OF_HOST_MEMORY)
	public static class OutOfHostMemory extends CLTypedException {}
	@ErrorCode(CL_INVALID_COMPILER_OPTIONS)
	public static class InvalidCompilerOptions extends CLTypedException {}
	@ErrorCode(CL_INVALID_DEVICE)
	public static class InvalidDevice extends CLTypedException {}
	@ErrorCode(CL_INVALID_DEVICE_PARTITION_COUNT)
	public static class InvalidDevicePartitionCount extends CLTypedException {}
	@ErrorCode(CL_INVALID_HOST_PTR)
	public static class InvalidHostPtr extends CLTypedException {}
	@ErrorCode(CL_INVALID_IMAGE_DESCRIPTOR)
	public static class InvalidImageDescriptor extends CLTypedException {}
	@ErrorCode(CL_INVALID_LINKER_OPTIONS)
	public static class InvalidLinkerOptions extends CLTypedException {}
	@ErrorCode(CL_INVALID_PLATFORM)
	public static class InvalidPlatform extends CLTypedException {}
	@ErrorCode(CL_INVALID_PROPERTY)
	public static class InvalidProperty extends CLTypedException {}
	@ErrorCode(CL_INVALID_COMMAND_QUEUE)
	public static class InvalidCommandQueue extends CLTypedException {}
    @ErrorCode(CL_MEM_COPY_OVERLAP)
	public static class MemCopyOverlap extends CLTypedException {}
	@ErrorCode(CL_INVALID_CONTEXT)
	public static class InvalidContext extends CLTypedException {}
	@ErrorCode(CL_INVALID_KERNEL)
	public static class InvalidKernel extends CLTypedException {}
	@ErrorCode(CL_INVALID_GL_CONTEXT_APPLE)
	public static class InvalidGLContextApple extends CLTypedException {}
    @ErrorCode(CL_INVALID_GL_SHAREGROUP_REFERENCE_KHR)
	public static class InvalidGLShareGroupReference extends CLTypedException {}
	@ErrorCode(CL_INVALID_GL_OBJECT)
	public static class InvalidGLObject extends CLTypedException {}
	@ErrorCode(CL_INVALID_KERNEL_ARGS)
	public static class InvalidKernelArgs extends CLTypedException {}
	@ErrorCode(CL_INVALID_KERNEL_DEFINITION)
	public static class InvalidKernelDefinition extends CLTypedException {}
	@ErrorCode(CL_INVALID_KERNEL_NAME)
	public static class InvalidKernelName extends CLTypedException {}
	@ErrorCode(CL_INVALID_MEM_OBJECT)
	public static class InvalidMemObject extends CLTypedException {}
	@ErrorCode(CL_INVALID_MIP_LEVEL)
	public static class InvalidMipLevel extends CLTypedException {}
	@ErrorCode(CL_INVALID_PROGRAM)
	public static class InvalidProgram extends CLTypedException {}
	@ErrorCode(CL_INVALID_PROGRAM_EXECUTABLE)
	public static class InvalidProgramExecutable extends CLTypedException {}
	@ErrorCode(CL_INVALID_QUEUE_PROPERTIES)
	public static class InvalidQueueProperties extends CLTypedException {}
	@ErrorCode(CL_INVALID_VALUE)
	public static class InvalidValue extends CLTypedException {}
	@ErrorCode(CL_INVALID_SAMPLER)
	public static class InvalidSampler extends CLTypedException {}
	@ErrorCode(CL_INVALID_DEVICE_TYPE)
	public static class InvalidDeviceType extends CLTypedException {}
	@ErrorCode(CL_INVALID_BUILD_OPTIONS)
	public static class InvalidBuildOptions extends CLTypedException {}
	@ErrorCode(CL_BUILD_PROGRAM_FAILURE)
	public static class BuildProgramFailure extends CLTypedException {}

    public static String errorString(int err) {
        if (err == CL_SUCCESS)
            return null;

        List<String> candidates = new ArrayList<String>();
        for (Field f : OpenCLLibrary.class.getDeclaredFields()) {
            if (!Modifier.isStatic(f.getModifiers())) {
                continue;
            }
            if (f.getType().equals(Integer.TYPE)) {
                try {
                    int i = (Integer) f.get(null);
                    if (i == err) {
                        String name = f.getName(), lname = name.toLowerCase();
                        if (lname.contains("invalid") || lname.contains("bad") || lname.contains("illegal") || lname.contains("wrong")) {
                            candidates.clear();
                            candidates.add(name);
                            break;
                        } else
                            candidates.add(name);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return StringUtils.implode(candidates, " or ");
    }

    static boolean failedForLackOfMemory(int err, int previousAttempts) {
    	switch (err) {
    	case CL_SUCCESS:
    		return false;
    	case CL_OUT_OF_HOST_MEMORY:
    	case CL_OUT_OF_RESOURCES:
    	case CL_MEM_OBJECT_ALLOCATION_FAILURE:
    		if (previousAttempts <= 1) {
	    		System.gc();
	    		if (previousAttempts == 1) {
	    			try {
	    				Thread.sleep(100);
	    			} catch (InterruptedException ex) {}
	    		}
	    		return true;
    		}
		default:
			error(err);
			assert false; // won't reach
			return false;
    	}
    }
    static final String logSuffix = System.getenv("CL_LOG_ERRORS") == null ? " (make sure to log all errors with environment variable CL_LOG_ERRORS=stdout)" : "";
        
	static Map<Integer, Class<? extends CLTypedException>> typedErrorClassesByCode;
    @SuppressWarnings("unchecked")
	public static void error(int err) {
        if (err == CL_SUCCESS)
            return;

        if (typedErrorClassesByCode == null) {
                typedErrorClassesByCode = new HashMap<Integer, Class<? extends CLTypedException>>();
                for (Class<?> c : CLException.class.getDeclaredClasses()) {
                        if (c == CLTypedException.class || !CLTypedException.class.isAssignableFrom(c))
                                continue;
                        typedErrorClassesByCode.put(c.getAnnotation(ErrorCode.class).value(), (Class<? extends CLTypedException>)c);
                }
        }
        CLException toThrow = null;
        Class<? extends CLTypedException> c = typedErrorClassesByCode.get(err);
        if (c != null) {
			try {
				toThrow = c.newInstance();
			} catch (InstantiationException ex) {
				assert log(Level.SEVERE, null, ex);
			} catch (IllegalAccessException ex) {
				assert log(Level.SEVERE, null, ex);
			}
        }
        if (toThrow == null)
        		toThrow = new CLException("OpenCL Error : " + errorString(err) + logSuffix, err);
        	
        throw toThrow;
    }
}

