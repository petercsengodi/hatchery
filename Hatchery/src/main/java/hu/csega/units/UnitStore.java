package hu.csega.units;

import java.util.HashMap;
import java.util.Map;

public class UnitStore {

	public static <T> T instance(Class<T> unitClass) {
		return createOrGetUnit(null, unitClass);
	}

	@SuppressWarnings("unchecked")
	static <T> T createOrGetUnit(Object parent, Class<T> unitClass) {
		synchronized (unitStoreMap) {
			try {

				AlwaysNew alwaysNewAnnotation = unitClass.getAnnotation(AlwaysNew.class);
				boolean alwaysNew = (alwaysNewAnnotation != null);
				if(alwaysNew)
					return createNewObjectWE(parent, unitClass);

				T object = (T)unitStoreMap.get(unitClass);

				if(object == null) {
					object = createNewObjectWE(parent, unitClass);

					unitStoreMap.put(unitClass, object);
				}

				return object;
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

	static <T> T createNewObject(Object parent, Class<T> unitClass) {
		try {
			T object = createNewObjectWE(parent, unitClass);
			return object;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> T createNewObjectWE(Object parent, Class<T> unitClass) throws InstantiationException, IllegalAccessException {
		Class<?> implementation = unitClass;

		Unit unitAnnotation = unitClass.getAnnotation(Unit.class);
		Single singleAnnotation = unitClass.getAnnotation(Single.class);
		DefaultImplementation implementorAnnotation = unitClass.getAnnotation(DefaultImplementation.class);

		if(unitAnnotation == null && singleAnnotation == null && implementorAnnotation == null)
			throw new RuntimeException(unitClass.getName() +
					" is not a unit interface/class. Please, use either the " +
					'@' + Unit.class.getSimpleName() + " or " +
					'@' + DefaultImplementation.class.getSimpleName() + " or " +
					'@' + Single.class.getSimpleName() + " or " +
					'@' + AlwaysNew.class.getSimpleName() + " annotation!");

		if(implementorAnnotation != null && implementorAnnotation.value() != null)
			implementation = implementorAnnotation.value();

		T object = (T)implementation.newInstance();

		if(object instanceof AbstractUnit)
			((AbstractUnit)object).injectComponents(parent);

		return object;
	}

	private static Map<Class<?>, Object> unitStoreMap = new HashMap<>();
}
