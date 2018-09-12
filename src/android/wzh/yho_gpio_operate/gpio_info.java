package wzh.yho_gpio_operate;

public class gpio_info {

	public gpio_info() {
	}

	static {
		try {
			System.loadLibrary("gpio_jni");
		} catch (UnsatisfiedLinkError ule) {
			System.err.println("WARNING: Could not load gpio_jni library!");
		}
	}

	public static native int open_gpio();

	public static native int close_gpio();

	public static native int get_gpio_data(String gpio_name);

	public static native int set_gpio_data(String gpio_name, int value);
}
