package fkjava.beans;

import fkjava.annotations.Autowired;
import fkjava.annotations.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void test() {
        System.out.println("注入的对象：" + userRepository);
    }
}
