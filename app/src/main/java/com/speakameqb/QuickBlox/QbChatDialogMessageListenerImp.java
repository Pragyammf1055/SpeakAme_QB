package com.speakameqb.QuickBlox;

import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.model.QBChatMessage;

/**
 * Created by Peter on 10-Oct-17.
 */

public class QbChatDialogMessageListenerImp implements QBChatDialogMessageListener {
    @Override
    public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {

    }

    @Override
    public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

    }
}
