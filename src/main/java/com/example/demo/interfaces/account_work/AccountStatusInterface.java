package com.example.demo.interfaces.account_work;

public interface AccountStatusInterface {
    void deactivate(String username, String password);
    void reactivate(String username, String password);

}

