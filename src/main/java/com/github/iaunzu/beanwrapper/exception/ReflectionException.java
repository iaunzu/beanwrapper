package com.github.iaunzu.beanwrapper.exception;

public class ReflectionException extends RuntimeException {

    private static final long serialVersionUID = -1774810947066142195L;

    /**
     * Create a new ReflectionException with the specified message.
     * 
     * @param msg
     *            the detail message
     */
    public ReflectionException(String msg) {
	super(msg);
    }

    /**
     * Create a new ReflectionException with the specified root cause.
     * 
     * @param cause
     *            the root cause
     */
    public ReflectionException(Throwable cause) {
	super(cause);
    }

    /**
     * Create a new ReflectionException with the specified message and root cause.
     * 
     * @param msg
     *            the detail message
     * @param cause
     *            the root cause
     */
    public ReflectionException(String msg, Throwable cause) {
	super(msg, cause);
    }
}
