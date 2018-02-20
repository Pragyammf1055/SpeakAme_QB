package com.speakameqb.QuickBlox;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRoster;
import com.quickblox.chat.QBSystemMessagesManager;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.model.QBPresence;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;
import com.speakameqb.Xmpp.ChatMessage;
import com.speakameqb.utils.AppPreferences;

import org.jivesoftware.smack.SmackException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class DialogsManager {

    public static final String PROPERTY_OCCUPANTS_IDS = "occupants_ids";
    public static final String PROPERTY_DIALOG_TYPE = "dialog_type";
    public static final String PROPERTY_DIALOG_NAME = "dialog_name";
    public static final String PROPERTY_NOTIFICATION_TYPE = "notification_type";
    public static final String CREATING_DIALOG = "creating_dialog";

    private static final String TAG = "DialogsManager";

    private Set<ManagingDialogsCallbacks> managingDialogsCallbackListener = new CopyOnWriteArraySet<>();

    private boolean isMessageCreatingDialog(QBChatMessage systemMessage) {
        return CREATING_DIALOG.equals(systemMessage.getProperty(PROPERTY_NOTIFICATION_TYPE));
    }

    private QBChatMessage buildSystemMessageAboutCreatingGroupDialog(QBChatDialog dialog) {

        Log.v(TAG, "inside buildSystemMessage for creating dialog ");

        QBChatMessage qbChatMessage = new QBChatMessage();
        qbChatMessage.setDialogId(dialog.getDialogId());
        qbChatMessage.setProperty(PROPERTY_OCCUPANTS_IDS, QbDialogUtils.getOccupantsIdsStringFromList(dialog.getOccupants()));
        qbChatMessage.setProperty(PROPERTY_DIALOG_TYPE, String.valueOf(dialog.getType().getCode()));
        qbChatMessage.setProperty(PROPERTY_DIALOG_NAME, String.valueOf(dialog.getName()));
        qbChatMessage.setProperty(PROPERTY_NOTIFICATION_TYPE, "1");

        return qbChatMessage;
    }

    private QBChatDialog buildChatDialogFromSystemMessage(QBChatMessage qbChatMessage) {
        QBChatDialog chatDialog = new QBChatDialog();
        chatDialog.setDialogId(qbChatMessage.getDialogId());
        chatDialog.setOccupantsIds(QbDialogUtils.getOccupantsIdsListFromString((String) qbChatMessage.getProperty(PROPERTY_OCCUPANTS_IDS)));
        chatDialog.setType(QBDialogType.parseByCode(Integer.parseInt(qbChatMessage.getProperty(PROPERTY_DIALOG_TYPE).toString())));
        chatDialog.setName(qbChatMessage.getProperty(PROPERTY_DIALOG_NAME).toString());
        chatDialog.setUnreadMessageCount(0);

        return chatDialog;
    }

    public void sendSystemMessageAboutCreatingDialog(QBSystemMessagesManager systemMessagesManager, QBChatDialog dialog, Context context, String body, ChatMessage chatMessage) {


//        vsddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd

        List<Integer> arrayList = dialog.getOccupants();

        QBChatMessage systemMessageCreatingDialog = buildSystemMessageAboutCreatingGroupDialog(dialog);
        systemMessageCreatingDialog.setBody("You have new Group invitation");
        systemMessageCreatingDialog.setProperty("custom_body", body);
        Log.v(TAG, "QB Chat Message 1 :- " + systemMessageCreatingDialog);
        Log.v(TAG, "dialog.getOccupants() :- " + arrayList);

        try {
            for (Integer recipientId : dialog.getOccupants()) {

                if (!recipientId.equals(AppPreferences.getQBUserId(context))) {

                    Log.v(TAG, "QB Chat Message 2svsdvsv:- " + AppPreferences.getQBUserId(context));
                    Log.v(TAG, "QB Chat Message 2:- " + recipientId);

                    systemMessageCreatingDialog.setRecipientId(recipientId);

                    Log.v(TAG, "QB Chat Message 3 :- " + systemMessageCreatingDialog);

//                csaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaascc

                    String PresenceStatus = checkUserPresence(recipientId, context);
                    Log.v(TAG, "PresenceStatus new findings :- " + PresenceStatus + " ````` of user id :- " + recipientId);

                    List<Integer> occupant_id = new ArrayList<>();
                    occupant_id.add(recipientId);

                    if (!PresenceStatus.equalsIgnoreCase("online")) {
/*
                        List<Integer> occupant_id = new ArrayList<>();
                        occupant_id.add(recipientId);*/

//                        QBChatDialog privateDialog = DialogUtils.buildPrivateDialog(recipientId);
//                        privateDialog.setOccupantsIds(occupant_id);
//                        privateDialog.setUserId(AppPreferences.getQBUserId(context));
//                        Log.v(TAG, "privateDialog 1:-  " + privateDialog);

                        dialog.setType(QBDialogType.PRIVATE);
                        dialog.setOccupantsIds(occupant_id);
                        dialog.setUserId(AppPreferences.getQBUserId(context));

                        QBChatMessage qbChatMessage = new QBChatMessage();
                        qbChatMessage.setBody("You have new Group invitation");
                        qbChatMessage.setProperty("save_to_history", "1");
                        qbChatMessage.setProperty("custom_body", body);
                        qbChatMessage.setDateSent(System.currentTimeMillis() / 1000);
                        qbChatMessage.setMarkable(false);

                        Log.v(TAG, "QBCHAT DIALOG for Group :- " + dialog);
                        Log.v(TAG, "QBCHAT MESSAGE for Group :- " + qbChatMessage);
                        dialog.initForChat(QBChatService.getInstance());

                        dialog.sendMessage(qbChatMessage);
                        sendNotification(occupant_id, systemMessageCreatingDialog.getBody(), chatMessage.senderName, chatMessage.sender);

                    } else {

                        if (systemMessagesManager != null) {
                            systemMessagesManager.sendSystemMessage(systemMessageCreatingDialog); // online user  sendSystemMessage()
                            sendNotification(occupant_id, systemMessageCreatingDialog.getBody(), chatMessage.senderName, chatMessage.sender);
                        }
                    }
                    Log.v(TAG, "QB Send system Message to all occupants :- done ");
                }
            }
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            Log.v(TAG, "  Error in sending system message :- " + e.getMessage());
        }
    }

    private String checkUserPresence(int qbUserId, Context context) {

        String status = "";
        QBRoster qbRoster = QBChatService.getInstance().getRoster();
        if (qbRoster != null) {
            QBPresence qbPresence = qbRoster.getPresence(qbUserId);
            Log.v(TAG, "qbPresence :- " + qbPresence);

            if (qbPresence.getType() == QBPresence.Type.online) {

//                Toast.makeText(context, "User " + qbUserId + " is online", Toast.LENGTH_SHORT).show();
                status = "online";
                return status;
            } else {
                Log.v(TAG, "User " + qbUserId + " is offline");
//                Toast.makeText(getApplicationContext(), "User " + qbUserId + " is offline", Toast.LENGTH_SHORT).show();
                status = "offline";
                return status;
            }
        }
        return status;
    }

    private void sendNotification(List<Integer> occupants, String normal_message, String senderNameNew, String sender) {

        Log.v(TAG, "send Notification occupants :- " + occupants);
        Log.v(TAG, "send Notification senderNameNew :- " + senderNameNew);
        Log.v(TAG, "send Notification senderNo :- " + sender);

        StringifyArrayList<Integer> userIds = new StringifyArrayList<>();

        userIds.addAll(occupants);

        QBEvent qbEvent = new QBEvent();
        qbEvent.setNotificationType(QBNotificationType.PUSH);
        qbEvent.setEnvironment(QBEnvironment.DEVELOPMENT);
        qbEvent.setUserIds(userIds);

        JSONObject json = new JSONObject();
        try {

            json.put("message_new", normal_message);
            json.put("qb_sender", senderNameNew);
            json.put("qb_sender_no", sender); // sender no

        } catch (Exception e) {
            e.printStackTrace();
        }
//        qbEvent.setMessage(json.toString());
        Log.v(TAG, "send Notification qbEvent :- " + qbEvent);
        Log.v(TAG, "send Notification Json  :- " + json.toString());
        qbEvent.setMessage(json.toString());

        QBPushNotifications.createEvent(qbEvent).performAsync(new QBEntityCallback<QBEvent>() {
            @Override
            public void onSuccess(QBEvent qbEvent, Bundle bundle) {
                Log.v(TAG, "onSuccess.............");
                Log.v(TAG, "onSuccess.............qbEvent" + qbEvent);
                Log.v(TAG, "onSuccess.............bundle" + bundle);
                QBSettings.getInstance().setEnablePushNotification(true);
            }

            @Override
            public void onError(QBResponseException e) {


                Log.v(TAG, "onError.............");
                Log.v(TAG, "onError.............e" + e);
            }
        });
    }

    private void loadUsersFromDialog(QBChatDialog chatDialog) {

    }

    public void onGlobalMessageReceived(String dialogId, QBChatMessage chatMessage) {
        if (chatMessage.getBody() != null && chatMessage.isMarkable()) { //for excluding status messages until will be released v.3.1
            if (QbDialogHolder.getInstance().hasDialogWithId(dialogId)) {
                QbDialogHolder.getInstance().updateDialog(dialogId, chatMessage);
                notifyListenersDialogUpdated(dialogId);
            } else {
                ChatHelper.getInstance().getDialogById(dialogId, new QbEntityCallbackImpl<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog chatDialog, Bundle bundle) {
                        loadUsersFromDialog(chatDialog);
                        QbDialogHolder.getInstance().addDialog(chatDialog);
                        notifyListenersNewDialogLoaded(chatDialog);
                    }
                });
            }
        }
    }

    public void onSystemMessageReceived(QBChatMessage systemMessage) {
        if (isMessageCreatingDialog(systemMessage)) {
            QBChatDialog chatDialog = buildChatDialogFromSystemMessage(systemMessage);
            chatDialog.initForChat(QBChatService.getInstance());
            QbDialogHolder.getInstance().addDialog(chatDialog);
            notifyListenersDialogCreated(chatDialog);
        }
    }

    private void notifyListenersDialogCreated(final QBChatDialog chatDialog) {
        for (ManagingDialogsCallbacks listener : getManagingDialogsCallbackListeners()) {
            listener.onDialogCreated(chatDialog);
        }
    }

    private void notifyListenersDialogUpdated(final String dialogId) {
        for (ManagingDialogsCallbacks listener : getManagingDialogsCallbackListeners()) {
            listener.onDialogUpdated(dialogId);
        }
    }

    private void notifyListenersNewDialogLoaded(final QBChatDialog chatDialog) {
        for (ManagingDialogsCallbacks listener : getManagingDialogsCallbackListeners()) {
            listener.onNewDialogLoaded(chatDialog);
        }
    }

    public void addManagingDialogsCallbackListener(ManagingDialogsCallbacks listener) {
        if (listener != null) {
            managingDialogsCallbackListener.add(listener);
        }
    }

    public void removeManagingDialogsCallbackListener(ManagingDialogsCallbacks listener) {
        managingDialogsCallbackListener.remove(listener);
    }

    public Collection<ManagingDialogsCallbacks> getManagingDialogsCallbackListeners() {
        return Collections.unmodifiableCollection(managingDialogsCallbackListener);
    }

    public interface ManagingDialogsCallbacks {

        void onDialogCreated(QBChatDialog chatDialog);

        void onDialogUpdated(String chatDialog);

        void onNewDialogLoaded(QBChatDialog chatDialog);
    }
}
