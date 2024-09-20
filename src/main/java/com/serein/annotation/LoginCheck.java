package com.serein.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/14
 * @Time: 17:31
 * @Description:
 */

@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface LoginCheck {
    String authorization();
}
