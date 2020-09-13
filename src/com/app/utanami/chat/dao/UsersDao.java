package com.app.utanami.chat.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.app.utanami.chat.entity.User;

public class UsersDao {

	private static final String USER_FIND_BY_ID_PASS = "SELECT * FROM USERS WHERE USER_ID = ? AND PASS = ?";

	/**
	 * ユーザーIDとパスワードによる1件検索メソッド
	 *
	 * @param userId
	 *            ユーザーID
	 * @param pass
	 *            パスワード
	 * @return ユーザー情報のEntity
	 */
	public User findByIdPass(String userId, String pass) {
		// 接続準備
		Connection con = null;
		PreparedStatement ps = null;
		User user = null;
		try {
			// DBに接続
			con = DBManager.getConnection();
			// SQL文を準備
			ps = con.prepareStatement(USER_FIND_BY_ID_PASS);

			// ユーザIDを検索条件に設定
			ps.setString(1, userId);

			// パスワードを検索条件に設定
			ps.setString(2, pass);

			// SQL文を実行
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				user = new User();
				user.setUserId(rs.getString("user_id"));
				user.setName(rs.getString("name"));
				user.setMail(rs.getString("mail"));
				user.setPass(rs.getString("pass"));
				user.setAuthority(rs.getString("authority"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBManager.close(ps, con);
		}
		return user;
	}

}
