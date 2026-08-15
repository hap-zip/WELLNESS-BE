package com.example.login.dto;

public class LoginRequest {
    private String email;
    private String password;

    // Getter와 Setter (값을 꺼내고 넣기 위해 필수)
    public String getEmail() { 
        return email; 
    }
    
    public void setEmail(String email) { 
        this.email = email;
    }
    
    public String getPassword() { 
        return password; 
    }
    
    public void setPassword(String password) { 
        this.password = password;
    }
}