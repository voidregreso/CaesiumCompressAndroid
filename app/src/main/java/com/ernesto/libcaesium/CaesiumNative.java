package com.ernesto.libcaesium;

public class CaesiumNative {
	
	static {
		System.loadLibrary("caesium_jni");
	}
	public static native boolean compressPic(String inf, String outf, CCSParameter conf);
	
}
