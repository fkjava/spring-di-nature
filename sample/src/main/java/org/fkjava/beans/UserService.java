package org.fkjava.beans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void test() {
        System.out.println("注入的对象：" + userRepository);
    }
}
