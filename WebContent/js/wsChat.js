$(function() {
	var socket = new WebSocket("ws://localhost:55000/OriChatApp/endpoint"); // Websocketオブジェクト

	// イベントハンドラ登録 onopenがたたかれたときに作動
	socket.onopen = function() {
		console.log("websocket opened");
		var reconnectData = {
				cmd:    "reconnect",
				userId: localStorage.getItem("userId")
		};
		socket.send(JSON.stringify(reconnectData));
		setTimeout(function(){
			var userName = localStorage.getItem("userName");
			var loginOK = {
				cmd : "loginOK",
			    userName : userName,
			}
			// オブジェクトを文字列に変換して＠OnMessageへ送信
			var loginOKText = JSON.stringify(loginOK);
			socket.send(loginOKText);
			});
	}

	socket.onmessage = function(e) {
		console.log(e);
		var data = JSON.parse(e.data);
		if (data.cmd === "loginOK") {
			console.log(e);
			$("#log").prepend('<div class="chat-card demo-card-wide mdl-card mdl-shadow--2dp">' +
					'<div class="mdl-card__supporting-text">' +	data.userName + 'さんがログインしました</div></div>');
		}else if (data.cmd === "chat"){
			$("#log").prepend('<div class="chat-card demo-card-wide mdl-card mdl-shadow--2dp">' +
					'<div class="mdl-card__supporting-text">' + data.name + '</div>' +
		    '<div class="mdl-card__actions mdl-card--border"><div class="mdl-card__supporting-text">' +
			 data.text + ' </div></div>');
		}

	}

	socket.onerror = function() {
		console.log("websocket error");
	}

	socket.onclose = function() {
		console.log("websocket closed");
	}

	$("#submit").click(
			function() {
				if ($("#text").val() == "" || $("#text").val() == "　"
						|| $("#text").val() == " ")
					return; // 入力が無かったりスペース１つの時は送信させない
				var chatText = $("#text").val();
				var jsonText = {
					cmd : "chat",
					text : chatText
				};
				var strJsonText = JSON.stringify(jsonText);
				socket.send(strJsonText);
				$("#text").val("");
	});

});
