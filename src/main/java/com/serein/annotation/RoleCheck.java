package com.serein.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/13
 * @Time: 0:20
 * @Description: 自定义角色鉴权注解
 */

@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface RoleCheck {

  /**
   * 必须有的角色
   *
   * @return
   */
  String mustRole() default "";

  /**
   * 有任何一个角色即可
   *
   * @return
   */
  String[] anyRole() default "";
}
