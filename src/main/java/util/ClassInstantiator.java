package util;

import org.apache.log4j.Logger;

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
}
