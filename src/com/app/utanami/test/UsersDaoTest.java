package com.app.utanami.test;

import com.app.utanami.chat.dao.UsersDao;
import com.app.utanami.chat.entity.User;

public class UsersDaoTest {
	public static void main(String[] args) {
		testFindByLogin1();
		testFindByLogin2();
	}

	private static void testFindByLogin1() {
		UsersDao dao = new UsersDao();
		User result = dao.findByIdPass("1","1");
		if(result != null && result.getUserId().equals("1") && result.getPass().equals("1") &&
				result.getMail().equals("a@a") && result.getName().equals("てすと")){
			System.out.println("OK");
		}else{
			System.out.println("NG");
		}

	}

	private static void testFindByLogin2() {
		UsersDao dao = new UsersDao();
		User result = dao.findByIdPass("1","11");
		if(result == null){
			System.out.println("OK");
		}else{
			System.out.println("NG");
		}
	}
}
