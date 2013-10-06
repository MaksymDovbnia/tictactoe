package com.game.handler;

import java.util.List;

import net.protocol.Protocol;
import android.os.Handler;
import android.os.Message;

import com.entity.OneMove;
import com.entity.Player;
import com.entity.TypeFieldElement;
import com.game.GameLogicHandler;
import com.game.GameType;
import com.game.activity.GameFieldActivityAction;
import com.game.activity.IGameFiledActions;
import com.game.adapters.GameFieldAdapter;
import com.net.online.WorkerOnlineConnection;
import com.net.online.protobuf.ProtoType;
import com.utils.Loger;

public class OnlineGameHandler implements GameHandler {

	private Handler handler;
	private GameLogicHandler gameActionHandler;
	private WorkerOnlineConnection onlineGameWorker;
	private Player player;
	private Player opponet;
	private GameFieldAdapter gameFieldAdapter;
    private  GameFieldActivityAction gameFiledActions;

	public OnlineGameHandler(WorkerOnlineConnection onlineGameWorker,
			Player player, Player opponent, GameFieldActivityAction fieldActivityAction) {
		this.player = player;
		this.opponet = opponent;
		this.onlineGameWorker = onlineGameWorker;
		gameActionHandler = new GameLogicHandler();
        this.gameFiledActions  = fieldActivityAction;

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
					OneMove oneMove = new OneMove(cDidMove.getI(),
							cDidMove.getJ(), typeFieldElement);
					gameFieldAdapter.opponentDidOneMove(oneMove);
					List<OneMove> list = gameActionHandler.oneMove(oneMove);
					if (list != null)
						gameFieldAdapter.drawWinLine(list);
					break;
                case CEXITFROMGAME:
                    OnlineGameHandler.this.gameFiledActions.opponentExitFromGame();
                    break;

				}

				super.handleMessage(msg);
			}

		};

		onlineGameWorker.registerHandler(handler);
	}

	public List<OneMove> oneMove(OneMove oneMove) {
		Protocol.SDidMove sDidMove = Protocol.SDidMove
				.newBuilder()
				.setOpponentId(opponet.getId())
				.setPlayerId(player.getId())				
				.setJ(oneMove.j)
				.setI(oneMove.i)
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

    @Override
    public void exitFromGame() {
       onlineGameWorker.sendPacket(Protocol.SExitFromGame.newBuilder().setPlayerId(player.getId()).setOpponentId(1).build());
       onlineGameWorker.unRegisterHandler(handler);
    }

    @Override
    public void unregisterHandler() {

    }

}
