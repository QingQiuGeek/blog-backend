package com.serein.domain;


import com.serein.domain.dto.LoginUserDTO;

public class UserHolder {
    private static final ThreadLocal<LoginUserDTO> tl = new ThreadLocal<>();

    public static void saveUser(LoginUserDTO user){
        tl.set(user);
    }

    public static LoginUserDTO getUser(){
        return tl.get();
    }

    public static void removeUser(){
        tl.remove();
    }
}
