$(function() {
	var socket = new WebSocket("ws://localhost:55000/OriChatApp/endpoint"); // Websocketオブジェクト

	// イベントハンドラ登録 onopenがたたかれたときに作動
	socket.onopen = function() {
		console.log("websocket opened");
	}

	socket.onmessage = function(e) {
		var data = JSON.parse(e.data);
		if (data.cmd === "loginResult" && data.result) {
			// webstorageへデータの保存
			localStorage.setItem("userId", data.result.userId);
			localStorage.setItem("userName", data.result.name);
			location.href = location.origin + "/OriChatApp/html/chat.html";
		} else if (data.cmd === "loginResult" && !data.result) {
			$("#log").text("ユーザIDまたはパスワードが間違っています");
		}
	}

	socket.onerror = function() {
		console.log("websocket error");
	}

	socket.onclose = function() {
		console.log("websocket closed");
	}

	$("#login").click(function() {
				var $userId = $("#userId");
				var $pass = $("#pass");
				if ($userId.val() === "" || $userId.val() === null
						|| $pass.val() === "" || $pass.val() === null)
					return; // 入力が無かったりスペース１つの時は送信させない
				var userId = $userId.val();
				var pass = $pass.val();
				var loginInfo = {
					cmd : "login",
					userId : userId,
					pass : pass
				}
				// オブジェクトを文字列に変換して＠OnMessageへ送信
				var loginInfoText = JSON.stringify(loginInfo);
				console.log("a");
				socket.send(loginInfoText);
	});
});
