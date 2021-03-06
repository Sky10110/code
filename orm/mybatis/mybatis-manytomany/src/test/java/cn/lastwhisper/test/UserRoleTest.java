package cn.lastwhisper.test;

import cn.lastwhisper.mybatis.manytomany.dao.RoleMapper;
import cn.lastwhisper.mybatis.manytomany.dao.UserMapper;
import cn.lastwhisper.mybatis.manytomany.domain.Role;
import cn.lastwhisper.mybatis.manytomany.domain.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class UserRoleTest {
    private InputStream in;
    private SqlSessionFactory factory;
    private SqlSession session;
    private RoleMapper roleMapper;
    private UserMapper userMapper;

    // 用户到角色一对多
    @Test
    public void testFindAllUser() {
        List<User> users = userMapper.findAll();
        for(User user : users){
            System.out.println("---每个用户的信息----");
            System.out.println(user);
            System.out.println(user.getRoles());
        }
    }

    // 角色到用户一对多
    @Test
    public void testFindAllRole() {
        List<Role> roles = roleMapper.findAll();
        for(Role role : roles){
            System.out.println("---每个角色的信息----");
            System.out.println(role);
            System.out.println(role.getUsers());
        }
    }

    @Before//在测试方法执行之前执行
    public void init() throws Exception {
        //1.读取配置文件
        in = Resources.getResourceAsStream("SqlMapConfig.xml");
        //2.创建构建者对象
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        //3.创建 SqlSession 工厂对象
        factory = builder.build(in);
        //4.创建 SqlSession 对象
        session = factory.openSession();
        //5.创建 Dao 的代理对象
        roleMapper = session.getMapper(RoleMapper.class);
        userMapper = session.getMapper(UserMapper.class);
    }

    @After//在测试方法执行完成之后执行
    public void destroy() throws Exception {
        session.commit();
        //7.释放资源
        session.close();
        in.close();
    }
}