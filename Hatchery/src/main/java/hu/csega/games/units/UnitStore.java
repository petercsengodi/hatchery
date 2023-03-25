package hu.csega.games.units;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class UnitStore {

	public static <T> T instance(Class<T> unitClass) {
		return createOrGetUnit(unitClass);
	}

	public static void registerProvider(Class<?> interfaceClass, UnitProvider unitProvider) {
		synchronized (unitStoreMap) {
			providerMap.put(interfaceClass, unitProvider);
		}
	}

	public static void registerInstance(Class<?> interfaceClass, Object instance) {
		synchronized (unitStoreMap) {
			unitStoreMap.put(interfaceClass, instance);
		}
	}

	public static void registerDefaultImplementation(Class<?> interfaceClass, Class<?> defaultImplementation) {
		synchronized (unitStoreMap) {
			providerMap.put(interfaceClass, new UnitDefaultImplementationProvider(defaultImplementation));
		}
	}

	static <T> T createOrGetUnit(Class<T> unitClass) {
		synchronized (unitStoreMap) {
			try {
				return createOrGetUnitAlreadySynchronized(unitClass);
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> T createOrGetUnitAlreadySynchronized(Class<T> unitClass) throws InstantiationException, IllegalAccessException, InvocationTargetException {
		AlwaysNew alwaysNewAnnotation = unitClass.getAnnotation(AlwaysNew.class);
		boolean alwaysNew = (alwaysNewAnnotation != null);
		if(alwaysNew) {
			return createNewObjectWE(unitClass);
		}

		T object = (T)unitStoreMap.get(unitClass);

		if(object == null) {
			object = createNewObjectWE(unitClass);
			unitStoreMap.put(unitClass, object);

			for(Method method : object.getClass().getMethods()) {
				Dependency dependency = method.getAnnotation(Dependency.class);
				if(dependency != null) {
					Class<?>[] parameterTypes = method.getParameterTypes();
					if(parameterTypes != null && parameterTypes.length > 0) {
						int len = parameterTypes.length;
						Object[] parameters = new Object[len];
						for(int i = 0; i < len; i++) {
							parameters[i] = createOrGetUnitAlreadySynchronized(parameterTypes[i]);
						}

						method.invoke(object, parameters);
					}
				}
			}
		}

		return object;
	}

	@SuppressWarnings("unchecked")
	private static <T> T createNewObjectWE(Class<T> unitClass) throws InstantiationException, IllegalAccessException, InvocationTargetException {
		UnitProvider provider = providerMap.get(unitClass);
		T object;

		if(provider != null) {
			object = (T)provider.createNewObject(unitClass);
		} else {
			Class<?> implementation;
			DefaultImplementation implementorAnnotation = unitClass.getAnnotation(DefaultImplementation.class);
			if (implementorAnnotation != null && implementorAnnotation.value() != null) {
				implementation = implementorAnnotation.value();
			} else {
				implementation = unitClass;
			}

			object = (T)implementation.newInstance();
		}

		return object;
	}

	private static final Map<Class<?>, Object> unitStoreMap = new HashMap<>();
	private static final Map<Class<?>, UnitProvider> providerMap = new HashMap<>();
}
