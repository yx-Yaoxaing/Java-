package factoryinterface;

public interface BeanFactory {
    public Object getBean(Class<?> requiredType);
}
