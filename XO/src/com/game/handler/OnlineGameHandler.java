package com.game.handler;

import java.util.List;

import net.protocol.Protocol;
import android.os.Handler;
import android.os.Message;
import android.widget.BaseAdapter;

import com.entity.OneMove;
import com.entity.Player;
import com.entity.TypeFieldElement;
import com.game.GameActionHandler;
import com.game.GameFiledSource;
import com.game.GameType;
import com.game.activity.R;
import com.game.adapters.GameFieldAdapter;
import com.net.online.OnlineConectionGameWorker;
import com.net.online.protobuf.ProtoType;
import com.utils.Loger;

public class OnlineGameHandler implements GameFiledSource {

	private Handler handler;
	private GameActionHandler gameActionHandler;
	private OnlineConectionGameWorker onlineGameWorker;
	private Player player;
	private Player opponet;
	private GameFieldAdapter gameFieldAdapter;

	public OnlineGameHandler(OnlineConectionGameWorker onlineGameWorker,
			Player player, Player opponent) {
		this.player = player;
		this.opponet = opponent;
		this.onlineGameWorker = onlineGameWorker;
		gameActionHandler = new GameActionHandler();

		this.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				ProtoType protoType = ProtoType.fromInt(msg.what);
				switch (protoType) {
				case CCHATMESSAGE:
					break;

				case CDIDMOVE:
					Protocol.CDidMove cDidMove = (Protocol.CDidMove) msg.obj;
					Loger.printLog(cDidMove.toString());
					TypeFieldElement typeFieldElement = (cDidMove.getType()
							.equals(Protocol.TypeMove.X) ? TypeFieldElement.X
							: TypeFieldElement.O);
					OneMove oneMove = new OneMove(cDidMove.getY(),
							cDidMove.getX(), typeFieldElement);
					gameFieldAdapter.opponentDidOneMove(oneMove);
					List<OneMove> list = gameActionHandler.oneMove(oneMove);
					if (list != null)
						gameFieldAdapter.drawWinLine(list);
					break;

				}

				super.handleMessage(msg);
			}

		};

		onlineGameWorker.setHanlerd(handler);
	}

	public List<OneMove> oneMove(OneMove oneMove) {
		Protocol.SDidMove sDidMove = Protocol.SDidMove
				.newBuilder()
				.setOpponentId(opponet.getId())
				.setPlayerId(player.getId())
				.setX(oneMove.j)
				.setY(oneMove.i)
				.setType(
						(oneMove.type.equals(TypeFieldElement.X)) ? Protocol.TypeMove.X
								: Protocol.TypeMove.O).build();

		onlineGameWorker.sendPacket(sDidMove);

		List<OneMove> list = gameActionHandler.oneMove(oneMove);
		if (list != null)
			gameActionHandler.newGame();
		return list;
	}

	public void sendMessage(String message) {
		Protocol.SChatMessage chatMessage = Protocol.SChatMessage.newBuilder()
				.setMessage(message).setPlayerId(player.getId())
				.setOpponentId(opponet.getId()).build();
		onlineGameWorker.sendPacket(chatMessage);
	}

	public GameType getGameType() {

		return GameType.ONLINE;
	}

	public Handler getHandler() {
		return handler;
	}

	@Override
	public void setAdapter(GameFieldAdapter adapter) {
		this.gameFieldAdapter = adapter;
		adapter.getPlayer1().setText(player.getName());
		adapter.getPlayer2().setText(opponet.getName());

	}

	@Override
	public void startNewGame() {
		gameActionHandler.newGame();

	}

}
