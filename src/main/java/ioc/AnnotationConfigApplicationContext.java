package ioc;

import annotation.ComponentScan;
import annotation.Controller;
import annotation.Service;
import factoryinterface.BeanFactory;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yaoxiang
 * @date 2021-04-09 11:29
 */
public class AnnotationConfigApplicationContext  implements BeanFactory {
    // 用作于ioc容器
    private static Map<Class<?>, Object> map = new ConcurrentHashMap<Class<?>, Object>();
    // 用作与存放class类
    private static Set<Class<?>> set = new HashSet<Class<?>>();
    private static List<String> listClassName = new ArrayList<String>();
    // 初始化容器
    public AnnotationConfigApplicationContext(Class<?>  componentClasses){
        refresh(componentClasses);
    }

    private void refresh(Class<?> componentClasses) {
        if(!componentClasses.isAnnotationPresent(ComponentScan.class)){
            throw  new RuntimeException(componentClasses+"Not found ComponentScan-Annotation");
        }
        ComponentScan annotation = componentClasses.getAnnotation(ComponentScan.class);
        // 获取注解里面定义的路径
        String basePackages = annotation.basePackages();
        // 获取这个包里面所有的类
        URL url = Thread.currentThread().getContextClassLoader().getResource(basePackages.replace(".","/"));
        if (url == null){
            throw  new NullPointerException("url do not is null");
        }
        File file = new File(url.getPath());
        CreateFilePathGetClassName(file,basePackages);
        for(Class<?> an : set){
            if(an.isAnnotationPresent(Controller.class) || an.isAnnotationPresent(Service.class)){
                try {
                    Constructor<?> declaredConstructor = an.getDeclaredConstructor();
                    Object o = declaredConstructor.newInstance();
                    map.put(an,o);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void CreateFilePathGetClassName(File file,String basePackages) {
        File[] files = file.listFiles();
        for(int i=0;i<files.length;i++){
          // 判断 是否是文件夹 递归处理这里
            File absoluteFile = files[i].getAbsoluteFile();
            if(absoluteFile.isDirectory()){
                CreateFilePathGetClassName(absoluteFile,basePackages);
            }
            // 如果不是文件夹 就判断文件是不是class文件 如果是class文件 就需要判断类是否存在controller service注解
            else if(absoluteFile.toString().endsWith(".class")){
                String path = absoluteFile.toString().replace(File.separator,".");
                String className = path.substring(path.indexOf(basePackages));
                className = className.substring(0,className.lastIndexOf("."));
                try{
                    Class<?> name = Class.forName(className);
                    set.add(name);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public Object  getBean(Class<?> requiredType)  {
        return map.get(requiredType);
    }
}
