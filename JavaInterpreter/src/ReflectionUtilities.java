
/*
 * Shannon Duvall and Nick Cheatwood
 * This object does basic reflection functions
 */
import java.lang.reflect.*;
import java.lang.Class;
import java.util.Arrays;

public class ReflectionUtilities {

	/*
	 * Given a class and an object, tell whether or not the object represents either
	 * an int or an Integer, and the class is also either int or Integer. This is
	 * really yucky, but the reflection things we need like Class.isInstance(arg)
	 * just don't work when the arg is a primitive. Luckily, we're only worrying
	 * with ints. This method works - don't change it.
	 */
	private static boolean typesMatchInts(Class<?> maybeIntClass, Object maybeIntObj) {
		// System.out.println("I'm checking on "+maybeIntObj);
		// System.out.println(maybeIntObj.getClass());
		try {
			return (maybeIntClass == int.class) && (int.class.isAssignableFrom(maybeIntObj.getClass())
					|| maybeIntObj.getClass() == Class.forName("java.lang.Integer"));
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	/*
	 * TODO: typesMatch Takes an array of Classes and an array of Objects and tells
	 * whether or not the object is an instance of the associated class, and that
	 * the two arrays are the same length. For objects, the isInstance method makes
	 * this easy. For ints, use the method I provided above.
	 */
	public static boolean typesMatch(Class<?>[] formals, Object[] actuals) {
		if (formals.length != actuals.length) {
			// Arrays are not same length, so fails regardless
			return false;
		} else {
			// Arrays are same length, test each class vs object
			int failedInstances = 0;
			for (int i = 0; i < formals.length; i++) {
				if (formals[i].isInstance(actuals[i]) || typesMatchInts(formals[i], actuals[i])) {
					// proceed to next
				} else {
					// not same instance
					// attempt to use typesMatchInts if an int
					failedInstances += 1;
				}
			}
			return (failedInstances == 0);
		}
	}

	/*
	 * TODO: createInstance Given String representing fully qualified name of a
	 * class and the actual parameters, returns initialized instance of the
	 * corresponding class using matching constructor. You need to use typesMatch to
	 * do this correctly. Use the class to get all the Constructors, then check each
	 * one to see if the types of the constructor parameters match the actual
	 * parameters given.
	 */
	public static Object createInstance(String name, Object[] args) {

		System.out.println("Instance: " + name);
		Object object = null;
		Class<?> someClass;
		Constructor<?>[] constructors;

		// Create a class from the name
		try {
			someClass = Class.forName(name);
		} catch (ClassNotFoundException e) {
			e.getStackTrace();
			someClass = null;
		}

		// Use class to get all constructors
		try {
			constructors = someClass.getConstructors();
		} catch (IllegalArgumentException e) {
			e.getStackTrace();
			constructors = null;
		}

		// Check all constructors if the types of the parameters meet actual
		// parameters provided
		for (int i = 0; i < constructors.length; i++) {
			// if constructor matches then return a valid object, else null
			try {
				object = typesMatch(constructors[i].getParameterTypes(), args) ? constructors[i].newInstance(args) : null;
			} catch (InstantiationException e) {
				// Error, keep object null
			} catch (IllegalAccessException e) {
				// Error, keep object null
			} catch (IllegalArgumentException e) {
				// Error, keep object null
			} catch (InvocationTargetException e) {
				// Error, keep object null
			}
		}
		return object;
	}

	/*
	 * TODO: callMethod Given a target object with a method of the given name that
	 * takes the given actual parameters, call the named method on that object and
	 * return the result.
	 *
	 * If the method's return type is void, null is returned.
	 *
	 * Again, to do this correctly, you should get a list of the object's methods
	 * that match the method name you are given. Then check each one to see which
	 * has formal parameters to match the actual parameters given. When you find one
	 * that matches, invoke it.
	 */
	public static Object callMethod(Object target, String name, Object[] args) {
		Method method = null;
		Object methodResult = null;

		System.out.println("Target: " + target.toString());
		System.out.println("Class: " + target.getClass().toString());
		System.out.println("Method: " + name);
		System.out.println("Args: " + Arrays.toString(args));
		System.out.println("Arg types: " + args.getClass().getComponentType());

		try {
			// Define out method
			method = target.getClass().getDeclaredMethod(name, args.getClass().getComponentType());
			// See if types match
			if (typesMatch(method.getParameterTypes(), args)) {
				// Param types match, let's test the return types
				if (method.getReturnType().equals(Void.TYPE)) {
					// returns void, so return null
				} else {
					try {
						methodResult = method.invoke(target, args);
					} catch (IllegalAccessException e) {
						// Error
					} catch (InvocationTargetException e) {
						// Error
					}
				}
			}
		} catch (NoSuchMethodException e) {
			// Error
			return null;
		}

		return methodResult;
	}

}
