package com.serein.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author:懒大王Smile
 * @Date: 2024/9/13
 * @Time: 0:20
 * @Description:
 */

@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface AuthCheck {
    int mustRole();
}
