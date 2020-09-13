package com.app.utanami.chat.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.app.utanami.chat.entity.User;
import com.app.utanami.chat.service.LoginService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@ServerEndpoint(value = "/endpoint")
public class ChatController {
	// つないでるsessionのカウント
	private static Map<Session, User> userSession = new HashMap<Session, User>(); // セッションごとにユーザの名前を管理

	@OnMessage // クライアントからメッセージを受信したときに、実行
	synchronized public void onMessage(String message, Session session) throws IOException {
		Gson gson = new Gson();
		//jsonを文字列にしたものがmessage それをjsonobjに変換
		JsonObject jsonObj = gson.fromJson(message, JsonObject.class);
		String cmd = jsonObj.get("cmd").getAsString();

		// JSONコマンドが「login」の時の処理
		if (cmd.equals("login")) {
			// ログイン処理実行
			LoginService loginService = new LoginService();
			String userId = jsonObj.get("userId").getAsString();
			String pass = jsonObj.get("pass").getAsString();
			User user = loginService.execute(userId, pass);
			if (user != null) {
				userSession.put(session, user); // すべてのセッションとユーザをマッピングしセッション追加
			} else {
				userSession.put(session, null);
			}
			// jsへ（フロントで）文字列で送るためにjavaobjをjson型へ変換[gson.toJson(user)]
			String userJson = gson.toJson(user);
			try {
				session.getBasicRemote().sendText("{\"cmd\": \"loginResult\", \"result\":" + userJson + "}");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (cmd.equals("reconnect")) {
			String userId = jsonObj.get("userId").getAsString();
			for (Iterator<Session> iterator = userSession.keySet().iterator(); iterator.hasNext();) {
				Session s = iterator.next();
				User user = userSession.get(s);
				if (user != null && user.getUserId().equals(userId)) {
					userSession.put(session, user);
//					userSession.remove(s);
				}
			}
		} else if (cmd.equals("loginOK")) {
			for (Iterator<Session> iterator = userSession.keySet().iterator(); iterator.hasNext();) {
				Session s = iterator.next();
				if(s.isOpen()){
				s.getBasicRemote().sendText(
						"{\"cmd\": \"loginOK\", \"userName\":\"" + jsonObj.get("userName").getAsString() + "\"}");
				}
			}
		} else if (cmd.equals("chat")) {
			User user = userSession.get(session);// セッションに紐づいている名前を取得
			message = jsonObj.get("text").getAsString(); // JSONコマンド「text」に入力されて文字列を取得
			// すべてのセッション(つないでる人)に対しメッセージを送信
			for (Iterator<Session> iterator = userSession.keySet().iterator(); iterator.hasNext();) {
				Session s = iterator.next();
				if(s.isOpen()){
				s.getBasicRemote().sendText("{\"cmd\": \"chat\", \"name\":\"" + user.getName() + "\",\"text\":\"" + message + "\"}");
				}
			}
		}
	}

	@OnOpen // socket通信の開始 websocketがnewされたとき
	synchronized public void onOpen(Session session) {
		synchronized (session) {
			System.out.println(session);
			System.out.println("おんおーぷん" + userSession.get(session));
			userSession.put(session, null);
		}
	}

	@OnClose // socket通信が切れたとき
	synchronized public void onClose(Session session) {
//		userSession.remove(session); // セッションを削除
		// System.out.println("【Method】onClose() " + "【current sessions】" +
		// userSession.size());
		// System.out.println();
	}

	@OnError
	synchronized public void onError(Throwable throwable) {
		System.out.println(throwable.getCause());
		System.out.println();
	}
}