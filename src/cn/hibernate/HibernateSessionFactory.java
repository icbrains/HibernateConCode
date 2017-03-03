package cn.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;


public class HibernateSessionFactory {

    //创建配置文件的路径
    private static String CONFIG_FILE_LOCATION="/User.hbm.xml";
    //创建ThreadLocal对象
    private static final ThreadLocal<Session> threadLocal=new ThreadLocal<Session>();
    //创建Configuration对象
    private static Configuration configuration=new Configuration();
    //定义SessionFactory对象
    private static SessionFactory sessionFactory;
    //定义configFile属性并赋值
    private static String configFile=CONFIG_FILE_LOCATION;


    static
    {
        try {
            //读取配置文件
            configuration.configure();
            //生成一个注册机对象
            ServiceRegistry serviceRegistry=
                    new ServiceRegistryBuilder().
                            applySettings(configuration.getProperties()).buildServiceRegistry();
            //通过ServiceRegistry对象来创建SessionFactory对象
            sessionFactory=configuration.buildSessionFactory(serviceRegistry);

        }catch (HibernateException e){
            e.printStackTrace();
        }
    }

    //创建无参数的构造方法
    public HibernateSessionFactory(){

    }

    //获得SessionFactory对象
    public static SessionFactory getSessionFactory(){
        return sessionFactory;
    }

    //重建SessionFactory
    public static void rebuildSessionFactory(){
        synchronized (sessionFactory){
            try {
                //获得配置文件
                configuration.configure(configFile);
                //创建注册机对象
                ServiceRegistry serviceRegistry=
                        new ServiceRegistryBuilder().
                                applySettings(configuration.getProperties()).buildServiceRegistry();

            }catch (HibernateException e){
                e.printStackTrace();
            }
        }
    }

    //获得Session
    public static Session getSession(){
        //获得ThreadLocal对象管理的Session对象
        Session session=threadLocal.get();
        try {
            //判断Session是否存在或者已经打开
            if (session == null || !session.isOpen()){
                if (sessionFactory==null){
                    rebuildSessionFactory();
                }
            }

            session=(sessionFactory !=null)?sessionFactory.openSession():null;
            threadLocal.set(session);
        }catch (Exception e){
            e.printStackTrace();
        }

        return session;

    }

    //关闭Session
    public static void closeSession(){
        Session session=threadLocal.get();
        threadLocal.set(null);
        try {
            if (session !=null && session.isOpen()){
                session.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //configFile的setter和getter方法

    public static String getConfigFile() {
        return configFile;
    }

    public static void setConfigFile(String configFile) {
        HibernateSessionFactory.configFile = configFile;
    }
}
