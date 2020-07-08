package com.example.xaperture;

import java.util.HashMap;
import java.util.Map;

public class User {
    HashMap<String ,String> user=new HashMap<>();
    User(String name,String email)
    {
        user.put(name,email);
    }
}
