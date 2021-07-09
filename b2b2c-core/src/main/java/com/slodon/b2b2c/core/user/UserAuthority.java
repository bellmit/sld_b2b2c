package com.slodon.b2b2c.core.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 用户&权限
 * @param <T> 用户类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthority<T> implements Serializable {

    private static final long serialVersionUID = -951520447429875639L;
    /**
     * 用户信息
     */
    private T t;

    /**
     * 用户权限
     */
    private Set<String> authorities = new HashSet<>();

    //添加权限
    public void addAuthority(String authority){
        this.authorities.add(authority);
    }

    /**
     * map转对象
     * @param map
     * @return
     */
    public static UserAuthority<Object> mapToUser(Map map){
        Object t = map.get("t");
        Collection authorities = (Collection) map.get("authorities");
        UserAuthority<Object> userAuthority = new UserAuthority<>();
        userAuthority.setT(t);
        for (Object authority : authorities) {
            userAuthority.addAuthority((String) authority);
        }
        return userAuthority;
    }
}
