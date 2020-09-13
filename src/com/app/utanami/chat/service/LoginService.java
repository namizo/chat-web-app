package com.app.utanami.chat.service;

import com.app.utanami.chat.dao.UsersDao;
import com.app.utanami.chat.entity.User;

public class LoginService {
	synchronized public User execute(String userId, String pass){
		UsersDao dao = new UsersDao();
		User user = dao.findByIdPass(userId, pass);
		return user;
	}
}
