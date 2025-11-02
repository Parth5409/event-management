package com.eventflow.service;

import com.eventflow.dao.UserDao;
import com.eventflow.model.User;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {

    private UserDao userDao = new UserDao();
    private AuthService authService = new AuthService();

    public User registerUser(User user) {
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        return userDao.createUser(user);
    }

    public String loginUser(String email, String password) {
        User user = userDao.getUserByEmail(email);
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            return authService.generateToken(user);
        }
        return null;
    }

    public User getUserById(int id) {
        return userDao.getUserById(id);
    }
}
