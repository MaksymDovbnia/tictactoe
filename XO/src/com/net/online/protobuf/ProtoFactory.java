package com.net.online.protobuf;

import net.protocol.Protocol;

import android.R.raw;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.utils.Loger;

public class ProtoFactory {

	public static AbstractMessageLite createProtoObject(byte data[],
			ProtoType type) throws InvalidProtocolBufferException {
		// System.out.println("createHandler for " + type);

		switch (type) {
		case CUPDATEAOBOUTACTIVITYPLAYER:
			Protocol.CUpdateAboutActivityPlayer cAboutActivityPlayer = Protocol.CUpdateAboutActivityPlayer
					.parseFrom(data);
			return cAboutActivityPlayer;

		case CSTARTGAME:

			Protocol.CStartGame cStartGame = Protocol.CStartGame
					.parseFrom(data);
			return cStartGame;

		case CWANTTOPLAY:

			Protocol.CWantToPlay CWantToPlay = Protocol.CWantToPlay
					.parseFrom(data);
			return CWantToPlay;

		case CLOGINTOGAME:

			Protocol.CLoginToGame CLoginToGame = Protocol.CLoginToGame
					.parseFrom(data);
			return CLoginToGame;
		case CDIDMOVE:
			Protocol.CDidMove didMove = Protocol.CDidMove.parseFrom(data);
			return didMove;
		case CEXITFROMGAME:
			Protocol.CExitFromGame exitFromGame = Protocol.CExitFromGame
					.parseFrom(data);
			return exitFromGame;
		case CCONTINUEGAME:
			Protocol.CContinueGame continueGame = Protocol.CContinueGame
					.parseFrom(data);
			return continueGame;
		default:
			Loger.printLog(" Wrong packet BLIADY");
			return null;

		}

	}

}
