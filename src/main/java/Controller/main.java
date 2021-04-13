package Controller;

import config.ComponentScanController;
import ioc.AnnotationConfigApplicationContext;

/**
 * @author yaoxiang
 * @date 2021-04-09 11:33
 */
public class main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ComponentScanController.class);
        UserController userController =(UserController) context.getBean(UserController.class);
        userController.testMethod();
    }
}
