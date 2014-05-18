package com.net.online.protobuf;

import com.google.protobuf.*;
import com.utils.*;

import net.protocol.*;

public class ProtoFactory {

    public static AbstractMessageLite createProtoObject(byte data[],
                                                        ProtoType type) throws InvalidProtocolBufferException {


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
            case CGETGROUPLIST:
                Protocol.CGetGroupList getGroupList = Protocol.CGetGroupList.parseFrom(data);
                return getGroupList;
            case CCANCELDESIREPLAY:
                Protocol.CCancelDesirePlay cCancelDesirePlay = Protocol.CCancelDesirePlay.parseFrom(data);
                return cCancelDesirePlay;
            case CCHATMESSAGE:
                Protocol.CChatMessage cChatMessage = Protocol.CChatMessage.parseFrom(data);
                return cChatMessage;
            case CGROUPCHATMESSAGE:
                Protocol.CGroupChatMessage cGroupChatMessage = Protocol.CGroupChatMessage.parseFrom(data);
                return cGroupChatMessage;
            case CTOP100:
                Protocol.CTop100Player top100 = Protocol.CTop100Player.parseFrom(data);
                return top100;
            case TIME_FOR_MOVE_FULL_UP:
                Protocol.TimeForMoveFullUp timeForMoveFullUp = Protocol.TimeForMoveFullUp.parseFrom(data);
                return timeForMoveFullUp;
            case APP_NEED_UPDATE_TO_LAST_VERSION:
                Protocol.AppNeedUpdateToLastVersion appNeedUpdateToLastVersion = Protocol.AppNeedUpdateToLastVersion.parseFrom(data);
                return appNeedUpdateToLastVersion;
            default:
                Logger.printLog("Wrong packet BLIADY");
                return null;

        }

    }

}
