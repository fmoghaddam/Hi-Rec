package util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import interfaces.AbstractRecommender;

/**
 * A helper class that accepts a class name + parameters and instantiates the
 * class. The code based is from Recommneder101:
 * http://ls13-www.cs.tu-dortmund.de/homepage/recommender101/index.shtml
 */
final public class ClassInstantiator {

    private static final Logger LOG = Logger
            .getLogger(ClassInstantiator.class.getCanonicalName());

    /**
     * The method accepts a string containing a class name
     * 
     * @param className
     * @return an instance of the class
     */
    public static
            Object instantiateClass(
                    final String className)
    {
        try {
            String classname = className;
            @SuppressWarnings("rawtypes")
            Class clazz = Class.forName(classname.trim());
            Object instance = clazz.newInstance();
            return instance;
        } catch (final Exception exception) {
            LOG.fatal("Can not load class:  " + className);
            System.exit(1);
            return null;
        }
    }

    public static
            void setParametersDynamically(
                    AbstractRecommender algorithm, int configId)
                    throws IllegalAccessException, InvocationTargetException
    {
        final Field[] allFields = algorithm.getClass().getDeclaredFields();
        final Map<String, Map<String, String>> configurabaleParameters = algorithm
                .getConfigurabaleParameters();
        for (Field field: allFields) {
            if (configurabaleParameters.containsKey(field.getName())) {
                final Map<String, String> map = configurabaleParameters
                        .get(field.getName());
                final String key = map.keySet().toArray(new String[0])[0];
                if (field.getType().equals(int.class)) {
                    BeanUtils.setProperty(algorithm, field.getName(),
                            Config.getInt("ALGORITHM_" + configId + "_" + key));
                } else if (field.getType().equals(double.class)) {
                    BeanUtils.setProperty(algorithm, field.getName(), Config
                            .getDouble("ALGORITHM_" + configId + "_" + key));
                } else if (field.getType().equals(float.class)) {
                    BeanUtils.setProperty(algorithm, field.getName(), Config
                            .getDouble("ALGORITHM_" + configId + "_" + key));
                } else if (field.getType().equals(boolean.class)) {
                    BeanUtils.setProperty(algorithm, field.getName(), Config
                            .getBoolean("ALGORITHM_" + configId + "_" + key));
                } else if (field.getType().equals(long.class)) {
                    BeanUtils.setProperty(algorithm, field.getName(), Config
                            .getLong("ALGORITHM_" + configId + "_" + key));
                } else if (field.getType().equals(String.class)) {
                    BeanUtils.setProperty(algorithm, field.getName(), Config
                            .getString("ALGORITHM_" + configId + "_" + key));
                }
            }
        }
    }
}
