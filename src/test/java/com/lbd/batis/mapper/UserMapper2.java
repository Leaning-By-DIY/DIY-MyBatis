package com.lbd.batis.mapper;

import java.util.List;

import com.lbd.batis.annotation.Param;
import com.lbd.batis.bean.User;


public interface UserMapper2 {

    User getUser(@Param("id") String id);

    List<User> getAll();

    int delete(@Param("id") String id);

    int update(@Param("id") String id, @Param("name") String name);

    int insert(@Param("id") String id, @Param("name") String name,
               @Param("password") String password, @Param("age") int age);
}
