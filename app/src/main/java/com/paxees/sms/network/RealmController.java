package com.paxees.sms.network;/*
package com.hbl.vlinkapp.realmDB;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hbl.survey.realmDB.model.ImagesModelDB;
import com.hbl.survey.realmDB.model.QuestionAnswerDB;
import com.hbl.survey.realmDB.model.QuestionDB;
import com.hbl.survey.realmDB.model.SurveyEventDataDB;

import java.lang.reflect.Type;
import java.util.ArrayList;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;


public class RealmController<T> {
    private static RealmController instance;
//    private final Realm realm;

    public static void resetObject() {
        instance = null;
    }

    */
/*public RealmController() {
        realm = Realm.getDefaultInstance();
    }*//*


    public static RealmController getInstance() {
        if (instance == null) {
            instance = new RealmController();
        }
        return instance;
    }

    public Realm getRealm() {
        Realm realm = Realm.getDefaultInstance();
        return realm;
    }

    public void refresh() {
        Realm realm = Realm.getDefaultInstance();
        realm.refresh();
    }

    public void clearAll() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }

    public void setAllSurveyEvent(ArrayList<SurveyEventData> data) {
        String json = new Gson().toJson(data);
        Type listType = new TypeToken<ArrayList<SurveyEventDataDB>>() {
        }.getType();
        ArrayList<SurveyEventDataDB> list = new Gson().fromJson(json, listType);
        for (SurveyEventDataDB x : list) {
            if (getSurveyEventBySurveyID(x.getSurveyID()) == null) {
                setSurveyEvent_DB(x);
            }
        }
    }

    public SurveyEventDataDB getSurveyEventBySurveyID(String SurveyID) {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(SurveyEventDataDB.class)
                .contains("surveyID", SurveyID, Case.INSENSITIVE).findFirst();
    }

    public void setSurveyEvent_DB(SurveyEventDataDB obj) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insertOrUpdate(obj);
        realm.commitTransaction();
    }

    public void setSurveyEvent(SurveyEventData obj) {
        String json = new Gson().toJson(obj);
        Type listType = new TypeToken<SurveyEventDataDB>() {
        }.getType();
        SurveyEventDataDB obj_db = new Gson().fromJson(json, listType);
        setSurveyEvent_DB(obj_db);
    }

    public void setQuestionsInDB(ArrayList<QuestionData> obj) {
        Realm realm = Realm.getDefaultInstance();
        for (QuestionData questionsCateList : obj) {
            for (Question questionsList : questionsCateList.getQuestions()) {
                String json = new Gson().toJson(questionsList);
                Type listType = new TypeToken<QuestionDB>() {
                }.getType();
                QuestionDB obj_db = new Gson().fromJson(json, listType);
                realm.beginTransaction();
                realm.insertOrUpdate(obj_db);
                realm.commitTransaction();
            }
        }
    }

    public RealmResults<QuestionDB> getQuestionByCategoryFromDB(String surveyCategoryID) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<QuestionDB> questionAnswerDBS = realm.where(QuestionDB.class).contains("surveyCategoryID", surveyCategoryID).findAll();
        return questionAnswerDBS;
    }


    public void setDataOfSpecificSurveyQA(Question data) {
        Realm realm = Realm.getDefaultInstance();
        QuestionAnswerDB questionAnswerDBS = (realm.where(QuestionAnswerDB.class)
                .contains("surveyID", data.getSurveyID(), Case.INSENSITIVE)
                .contains("surveyCategoryID", data.getSurveyCategoryID())
                .contains("questionID", data.getQuestionID()).findFirst());
        if (questionAnswerDBS != null) {
            realm.beginTransaction();
            questionAnswerDBS.setComment(data.getComment());
            questionAnswerDBS.setImagesModel(new RealmList<ImagesModelDB>());
            ImagesModelDB db;
            for (ImagesModel model : data.getImagesModel()) {
                db = new ImagesModelDB();
                db.setImageName(model.getImageName());
                db.setImageUri(model.getImageUri());
                questionAnswerDBS.getImagesModel().add(db);
            }
            questionAnswerDBS.setAnswerTime(data.getAnswerTime());
            questionAnswerDBS.setAnswerValue(data.getAnswerValue());
            questionAnswerDBS.setEndDateTime(data.getEndDateTime());
            questionAnswerDBS.setLatitude(data.getLatitude());
            questionAnswerDBS.setLongitude(data.getLongitude());
            questionAnswerDBS.setUserID(data.getUserID());
            questionAnswerDBS.setToken(data.getToken());
            questionAnswerDBS.setSurveyName(data.getSurveyName());
            questionAnswerDBS.setSurveyCategoryName(data.getSurveyCategoryName());
            realm.commitTransaction();
        }
    }

    public boolean deleteSpecificSurveyQA(String surveyID) {
        boolean isDeleted = false;
        Realm realm = Realm.getDefaultInstance();
        RealmResults<QuestionAnswerDB> questionAnswerDBS = realm.where(QuestionAnswerDB.class)
                .contains("surveyID", surveyID, Case.INSENSITIVE).findAll();
        if (questionAnswerDBS.size() != 0) {
            realm.beginTransaction();
            isDeleted = questionAnswerDBS.deleteAllFromRealm();
            realm.commitTransaction();
        }
        return isDeleted;
    }

    public ArrayList<ImagesModel> getAllImagesFromSpecificSurveyQA(String surveyID, String surveyCategoryID) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<QuestionAnswerDB> questionAnswerDBS = (realm.where(QuestionAnswerDB.class)
                .contains("surveyID", surveyID, Case.INSENSITIVE)
                .contains("surveyCategoryID", surveyCategoryID).findAll());
        ArrayList<ImagesModel> imagesModels = new ArrayList<>();
        ImagesModel imagesModel;
        for (QuestionAnswerDB x : questionAnswerDBS) {
            for (int i = 0; i < x.getImagesModel().size(); i++) {
                imagesModel = new ImagesModel();
                imagesModel.setImageUri(x.getImagesModel().get(i).getImageUri());
                imagesModel.setImageName(x.getImagesModel().get(i).getImageName());
                imagesModels.add(imagesModel);
            }
        }
        return imagesModels;
    }

    public boolean deleteSpecificImageFromSpecificSurveyQA(String uriOfImage, String surveyID) {
        boolean isImageDeleted = false;
        Realm realm = Realm.getDefaultInstance();
        RealmResults<QuestionAnswerDB> questionAnswerDBS = (realm.where(QuestionAnswerDB.class)
                .contains("surveyID", surveyID, Case.INSENSITIVE)
                */
/*.contains("surveyCategoryID", surveyCategoryID)*//*
.findAll());
        for (QuestionAnswerDB x : questionAnswerDBS) {
            for (int i = 0; i < x.getImagesModel().size(); i++) {
                if (x.getImagesModel().get(i).getImageUri().equalsIgnoreCase(uriOfImage)) {
                    realm.beginTransaction();
                    x.getImagesModel().remove(i);
                    realm.commitTransaction();
                    isImageDeleted = true;
                    return isImageDeleted;
                }
            }
        }
        return isImageDeleted;
    }

    public ArrayList<Question> getSurveyQuestionsFromDB(String surveyID, String surveyCategoryID) {
        Realm realm = Realm.getDefaultInstance();
        ArrayList<QuestionAnswerDB> questionAnswerDBS = (ArrayList<QuestionAnswerDB>) realm.copyFromRealm(realm.where(QuestionAnswerDB.class)
                .contains("surveyID", surveyID, Case.INSENSITIVE)
                .contains("surveyCategoryID", surveyCategoryID).findAll());
        if (questionAnswerDBS.size() == 0) {
            ArrayList<QuestionDB> questions = (ArrayList<QuestionDB>) realm.copyFromRealm(getQuestionByCategoryFromDB(surveyCategoryID));
            QuestionAnswerDB questionAnswerDB;
            for (QuestionDB questionDB : questions) {
                questionAnswerDB = new QuestionAnswerDB();
                String json = new Gson().toJson(questionDB);
                Type listType = new TypeToken<QuestionAnswerDB>() {
                }.getType();
                questionAnswerDB = new Gson().fromJson(json, listType);
                questionAnswerDB.setSurveyID(surveyID);
                realm.beginTransaction();
                realm.insert(questionAnswerDB);
                realm.commitTransaction();
            }
            questionAnswerDBS = (ArrayList<QuestionAnswerDB>) realm.copyFromRealm(realm.where(QuestionAnswerDB.class).contains("surveyID", surveyID, Case.INSENSITIVE)
                    .contains("surveyCategoryID", surveyCategoryID).findAll());
        }
        ArrayList<Question> questions = new ArrayList<>();
        String json = new Gson().toJson(questionAnswerDBS);
        Type listType = new TypeToken<ArrayList<Question>>() {
        }.getType();
        questions = new Gson().fromJson(json, listType);
        return questions;
    }

    public boolean getIsAnyQuestionAnswerInSurvey(String surveyID) {
        Realm realm = Realm.getDefaultInstance();
        QuestionAnswerDB questionAnswerDBS = realm.where(QuestionAnswerDB.class)
                .isNotEmpty("answerValue")
                .contains("surveyID", surveyID, Case.INSENSITIVE)
                .findFirst();
        if (questionAnswerDBS != null) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<SubmitAnswerRequest> getAllPendingOfflineSurvey() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<SurveyEventDataDB> results = realm.where(SurveyEventDataDB.class)
                .notEqualTo("surveyStatus","1")
                .containsValue("isStarted", 1).findAll();
        ArrayList<SubmitAnswerRequest> listofAllSurveyQuestions=new ArrayList<>();
        for (SurveyEventDataDB x : results) {
            SubmitAnswerRequest submitAnswerRequest=new SubmitAnswerRequest();
            submitAnswerRequest.setQuestions(getSurveyQuestionsFromDB(x.getSurveyID(),x.getSurveyCategoryID()));
            listofAllSurveyQuestions.add(submitAnswerRequest);
        }
        return listofAllSurveyQuestions;
    }


    public ArrayList<SurveyEventDataDB> getSurveyEvent() {
        Realm realm = Realm.getDefaultInstance();
        ArrayList<SurveyEventDataDB> arrayList = new ArrayList<>();
        arrayList.addAll(realm.copyFromRealm(realm.where(SurveyEventDataDB.class).findAll()));
        return arrayList;
    }

    public void updateStatusOfSpecificSurvey(String surveyID) {
        Realm realm = Realm.getDefaultInstance();
        SurveyEventDataDB returnObjs = realm.where(SurveyEventDataDB.class)
                .contains("surveyID", surveyID)
//                .contains("surveyCategoryID", surveyCategoryID)
                .containsValue("surveyStatus", "0").findFirst();
        if (returnObjs != null) {
            realm.beginTransaction();
            returnObjs.setSurveyStatus("1");
            realm.commitTransaction();
        }
    }

    public void newSurveyStarted(String surveyID, String surveyCategoryID) {
        Realm realm = Realm.getDefaultInstance();
        SurveyEventDataDB returnObjs = realm.where(SurveyEventDataDB.class)
                .contains("surveyID", surveyID)
                .contains("surveyCategoryID", surveyCategoryID)
                .containsValue("surveyStatus", "0").findFirst();
        if (returnObjs != null) {
            realm.beginTransaction();
            returnObjs.setStarted(1);
            realm.commitTransaction();
        }
    }


    public void getAllStartedSurveys(boolean isStarted) {
       */
/* Realm realm = Realm.getDefaultInstance();
        ArrayList<SurveyEventDataDB> arrayList = new ArrayList<>();
        arrayList.addAll(realm.copyFromRealm(
                realm.where(SurveyEventDataDB.class)
                        .containsValue("isStarted",isStarted)
                        .sort("startDateTime", Sort.DESCENDING).findAll()
        ));
        return arrayList;*//*

    }

    public ArrayList<AddSurveyValue> getOfflineSurverys(SharedPreferenceHandler prefs) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<SurveyEventDataDB> returnObjs = realm.where(SurveyEventDataDB.class).containsValue("isUploaded", false).findAll();
        ArrayList<AddSurveyValue> arrayList = new ArrayList<>();
        AddSurveyValue addSurveyValueObj;
        for (SurveyEventDataDB x : returnObjs) {
            addSurveyValueObj = new AddSurveyValue();
            addSurveyValueObj.setToken(prefs.getUser().getToken());
            addSurveyValueObj.setOfficerID(prefs.getUser().getData().get(0).getUserID());
            addSurveyValueObj.setBranchCode(x.getBranchCode());
            addSurveyValueObj.setSurveyCategoryID(x.getSurveyCategoryID());
            addSurveyValueObj.setDescription(x.getDescription());
            addSurveyValueObj.setStartDateTime(x.getStartDateTime());
            addSurveyValueObj.setSurveyName(x.getSurveyName());
            addSurveyValueObj.setSurveyStatus(x.getSurveyStatus());
            addSurveyValueObj.setEndDateTime(x.getEndDateTime());
            addSurveyValueObj.setSurveyID(x.getSurveyID());
            arrayList.add(addSurveyValueObj);
        }
        return arrayList;
    }

    public void offlineUploadedSurveyTrue(ArrayList<AddSurveyValue> offlineSurveysList) {
        Realm realm = Realm.getDefaultInstance();
        SurveyEventDataDB surveyEventDataDB;
        for (AddSurveyValue x : offlineSurveysList) {
            surveyEventDataDB = realm.where(SurveyEventDataDB.class).contains("surveyID", x.getSurveyID(), Case.INSENSITIVE).findFirst();
            if (surveyEventDataDB != null) {
                realm.beginTransaction();
                surveyEventDataDB.setUploaded(true);
                realm.commitTransaction();
            }
        }

    }

    public ArrayList<SurveyEventDataDB> getSurveyListDataByCategory(String surveyCategoryID) {
        Realm realm = Realm.getDefaultInstance();
        ArrayList<SurveyEventDataDB> arrayList = new ArrayList<>();
        arrayList.addAll(realm.copyFromRealm(
                realm.where(SurveyEventDataDB.class)
                        .contains("surveyCategoryID", surveyCategoryID, Case.INSENSITIVE)
                        .sort("startDateTime", Sort.DESCENDING).findAll()
        ));
        return arrayList;
    }

*/
/*

    public RealmResults<InviteContact> getAllInviteContacts() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(InviteContact.class).findAll();
    }

    public void clearAllInviteContacts() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(InviteContact.class);
        realm.commitTransaction();
    }

    public void saveInviteContacts(ArrayList<InviteContact> contactList) {
        Realm realm = Realm.getDefaultInstance();
        clearAllInviteContacts();
        realm.beginTransaction();
        realm.insert(contactList);
        realm.commitTransaction();
    }

    public RealmResults<UserData> getAllUsersOfTAG() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(UserData.class).sort("firstName", Sort.ASCENDING).findAll();
    }

    public UserData getUserOfTAG(String contact) {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(UserData.class)
                .equalTo("contact", contact)
                .findFirst();
    }

    public UserData getUserOfTAG_FCM(String contact) {
        Realm mRealm = Realm.getDefaultInstance();
        return mRealm.where(UserData.class)
                .equalTo("contact", contact)
                .findFirst();
    }

    public RealmResults<UserData> searchUsersOfTAG(String searchValue) {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(UserData.class)
                .contains("firstName", searchValue, Case.INSENSITIVE)
                .sort("firstName", Sort.ASCENDING).findAll();
    }

    public void clearAllUsersOfTAG() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(UserData.class);
        realm.commitTransaction();
    }



    public void setTitleForNonGroupChat(String userContact) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ChatListModel> chatList = getIndividualChatList();
        for (ChatListModel chatObject : chatList) {
            for (MemberObject memberObject : chatObject.getMembers()) {
                if (!userContact.equalsIgnoreCase(memberObject.getContact())) {
                    UserData memberOfTAG = getUserOfTAG(memberObject.getContact());
                    realm.beginTransaction();
                    if (memberOfTAG != null) {
                        chatObject.setTitle(memberOfTAG.getFirstName());
                        chatObject.setImageUrls(memberOfTAG.getImageUrls());
                    } else {
                        chatObject.setTitle(memberObject.getContactCode() + "" + memberObject.getContact());
                        chatObject.setImageUrls(memberObject.getImageUrls());
                    }
                    realm.commitTransaction();
                }
            }

        }
    }

    public RealmResults<ChatListModel> searchChatList(String searchValue) {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(ChatListModel.class)
                .contains("title", searchValue, Case.INSENSITIVE)
                .sort("title", Sort.ASCENDING).findAll();
    }

    public RealmResults<ChatListModel> searchIndividualChatList(String searchValue) {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(ChatListModel.class).equalTo("isGroup", 0)
                .contains("title", searchValue, Case.INSENSITIVE)
                .sort("title", Sort.ASCENDING).findAll();
    }


    public RealmResults<ChatListModel> getFavouriteChatList() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(ChatListModel.class)
                .equalTo("isFavourite", 1)
                .sort("updatedAt", Sort.DESCENDING)
                .findAll();
    }

    public RealmResults<ChatListModel> getChatList() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(ChatListModel.class)
                .sort("updatedAt", Sort.DESCENDING).findAll();
    }

    public RealmResults<ChatListModel> getIndividualChatList() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(ChatListModel.class)
                .equalTo("isGroup", 0)
                .sort("updatedAt", Sort.DESCENDING)
                .findAll();
    }

    public ChatListModel getChat(String groupId) {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(ChatListModel.class)
                .equalTo("id", groupId)
                .findFirst();
    }

    public void clearChatList() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(ChatListModel.class);
        realm.commitTransaction();
    }

    public void saveChatList(ArrayList<ChatListModel> chatList, String userContact) {
        Realm realm = Realm.getDefaultInstance();
        clearChatList();

        for (ChatListModel obj : chatList) {
            if (getMessageSeenByGroupId(obj.getId())) {
                for (MemberObject mObj : obj.getMembers()) {
                    if (mObj.getContact().equalsIgnoreCase(userContact)) {
                        mObj.setUnreadMessages(0);
                    }
                }
            }
            /////////////////////////////////////////////////////
            if (!obj.getIsGroup()) {
                for (MemberObject memberObject : obj.getMembers()) {
                    if (!userContact.equalsIgnoreCase(memberObject.getContact())) {
                        UserData memberOfTAG = getUserOfTAG(memberObject.getContact());
                        if (memberOfTAG != null) {
                            obj.setTitle(memberOfTAG.getFirstName());
                            obj.setImageUrls(memberOfTAG.getImageUrls());
                        } else {
                            obj.setTitle(memberObject.getContactCode() + "" + memberObject.getContact());
                            obj.setImageUrls(memberObject.getImageUrls());
                        }
                    }
                }
            }
            /////////////////////////////////////////////////////


        }

        realm.beginTransaction();
        realm.insertOrUpdate(chatList);
        realm.commitTransaction();
//        setTitleForNonGroupChat(userContact);
    }

    public boolean getMessageSeenByGroupId(String groupId) {
        MessageSeen messageSeen = Realm.getDefaultInstance().where(MessageSeen.class)
                .equalTo("group_id", groupId)
                .findFirst();
        if (messageSeen == null)
            return false;
        return true;
    }

    public void updateChatFavourite(ChatListModel obj) {
        Realm realm = Realm.getDefaultInstance();
        ChatListModel chatListModel = realm.where(ChatListModel.class)
                .equalTo("id", obj.getId())
                .findFirst();
        realm.beginTransaction();
        chatListModel.setIsFavourite(obj.getIsFavourite() ? 0 : 1);
        realm.commitTransaction();
    }

    public void leaveGroup(Activity activity, ChatListModel obj) {
        Realm realm = Realm.getDefaultInstance();
        ChatListModel chatListModel = realm.where(ChatListModel.class)
                .equalTo("id", obj.getId())
                .findFirst();
        realm.beginTransaction();
        for (int i = 0; i < obj.getMembers().size(); i++) {
            if (obj.getMembers().get(i).getId().equalsIgnoreCase(GetUserData.getInstance().getUserData(activity, true).getId())) {
                chatListModel.getMembers().get(i).setIsActive(0);
                break;
            }
        }
        realm.commitTransaction();
    }

    public void deleteGroup(ChatListModel obj) {
        Realm realm = Realm.getDefaultInstance();
        String id = obj.getId();
        ChatListModel chatListModel = realm.where(ChatListModel.class)
                .equalTo("id", id)
                .findFirst();
        realm.beginTransaction();
        chatListModel.deleteFromRealm();
        realm.commitTransaction();

        RealmResults<MessageModel> messageModel = realm.where(MessageModel.class)
                .equalTo("chat_type_id", id)
                .findAll();
        realm.beginTransaction();
        messageModel.deleteAllFromRealm();
        realm.commitTransaction();
    }

    public void updateGroup(ChatListModel obj) {
        Realm realm = Realm.getDefaultInstance();
        *//*

*/
/*ChatListModel chatListModel = realm.where(ChatListModel.class)
                .equalTo("id", obj.getId())
                .findFirst();*//*
*/
/*

        realm.beginTransaction();
        realm.insertOrUpdate(obj);
        realm.commitTransaction();
    }

    public void RemoveMemberFromGroup(ChatListModel obj, String memberId) {
        Realm realm = Realm.getDefaultInstance();
        ChatListModel chatListModel = realm.where(ChatListModel.class)
                .equalTo("id", obj.getId())
                .findFirst();
        realm.beginTransaction();
        for (int i = 0; i < chatListModel.getMembers().size(); i++) {
            if (chatListModel.getMembers().get(i).getId().equalsIgnoreCase(memberId)) {
                chatListModel.getMembers().get(i).setIsActive(0);
                break;
            }
        }
        realm.commitTransaction();
    }

    public void addMemberToGroup(ChatListModel obj, String memberId) {
        Realm realm = Realm.getDefaultInstance();
        ChatListModel chatListModel = realm.where(ChatListModel.class)
                .equalTo("id", obj.getId())
                .findFirst();
        realm.beginTransaction();
        for (int i = 0; i < chatListModel.getMembers().size(); i++) {
            if (chatListModel.getMembers().get(i).getId().equalsIgnoreCase(memberId)) {
                chatListModel.getMembers().get(i).setIsActive(1);
                break;
            }
        }
        realm.commitTransaction();
    }

    public void saveMessageList(ArrayList<MessageModel> messagesList) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insertOrUpdate(messagesList);
        realm.commitTransaction();
    }

    public void favOrUnFavMessage(String messageId) {
        Realm realm = Realm.getDefaultInstance();
        MessageModel messageModel = realm.where(MessageModel.class)
                .equalTo("id", messageId)
                .findFirst();
        realm.beginTransaction();
        messageModel.setIsFavourite(messageModel.getIsFavourite() == 0 ? 1 : 0);
        realm.commitTransaction();
    }

    public RealmResults<MessageModel> getChatList(int conversationId) {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(MessageModel.class).equalTo(MESSAGE_TYPE_ID, conversationId).sort(MESSAGE_TIME, Sort.ASCENDING).findAll();
    }


    public RealmResults<MessageModel> getFavouriteMessagesList() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(MessageModel.class).equalTo("isFavourite", 1)
                .sort(MESSAGE_TIME, Sort.ASCENDING).findAll();
    }

    public String getLastMessageId(String conversationId) {
        Realm realm = Realm.getDefaultInstance();
        MessageModel messageModel = realm.where(MessageModel.class)
                .equalTo(MESSAGE_TYPE_ID, conversationId).equalTo(MESSAGE_STATUS, MSG_STATUS_DELEVIRED)
                .sort(MESSAGE_TIME, Sort.DESCENDING).findFirst();
        if (messageModel != null) {
            return messageModel.getId();
        }
        return "";
    }


    public RealmResults<NotificationListModel> getNotificationList() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(NotificationListModel.class)
                .findAll();
    }

    public void clearNotificationList() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(NotificationListModel.class);
        realm.commitTransaction();
    }

    public void saveNotificationList(ArrayList<NotificationListModel> notificationList) {
        Realm realm = Realm.getDefaultInstance();
        clearNotificationList();
        realm.beginTransaction();
        realm.insert(notificationList);
        realm.commitTransaction();
    }

    public RealmResults<FaqsModel> getFaqList() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(FaqsModel.class)
                .findAll();
    }

    public void clearFaqList() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(FaqsModel.class);
        realm.commitTransaction();
    }

    public void saveFaqList(ArrayList<FaqsModel> faqsList) {
        Realm realm = Realm.getDefaultInstance();
        clearFaqList();
        realm.beginTransaction();
        realm.insert(faqsList);
        realm.commitTransaction();
    }

    public RealmResults<PrivacyModel> getPrivacyList() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(PrivacyModel.class)
                .findAll();
    }

    public void clearPrivacyList() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(PrivacyModel.class);
        realm.commitTransaction();
    }

    public void savePrivacyList(ArrayList<PrivacyModel> privacyList) {
        Realm realm = Realm.getDefaultInstance();
        clearPrivacyList();
        realm.beginTransaction();
        realm.insert(privacyList);
        realm.commitTransaction();
    }

    public RealmResults<TermsOfServicesModel> getTermsOfServicesList() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(TermsOfServicesModel.class)
                .findAll();
    }

    public void clearTermsOfServicesList() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(TermsOfServicesModel.class);
        realm.commitTransaction();
    }

    public void saveTermsOfServicesList(ArrayList<TermsOfServicesModel> termsOfServicesList) {
        Realm realm = Realm.getDefaultInstance();
        clearTermsOfServicesList();
        realm.beginTransaction();
        realm.insert(termsOfServicesList);
        realm.commitTransaction();
    }

    public boolean getIsNotificationAllowed(Context context, String groupId) {
        Realm realm = Realm.getDefaultInstance();
        UserData userData = GetUserData.getInstance().getUserData(context, true);
        ChatListModel chatDetail = realm.where(ChatListModel.class)
                .equalTo("id", groupId)
                .findFirst();
        if (chatDetail != null) {
            if (chatDetail.getIsGroup()) {
                for (int i = 0; i < chatDetail.getMembers().size(); i++) {
                    if (userData.getContact().equalsIgnoreCase(chatDetail.getMembers().get(i).getContact())) {
                        if (chatDetail.getMembers().get(i).getAllowNotification())
                            return true;
                        else return false;
                    }
                }
                return true;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public void saveGroupNotificationId(String groupId, int notificationId) {
        NotificationIdModel obj = new NotificationIdModel();
        obj.setGroupId(groupId);
        obj.setNotificationId(notificationId);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insertOrUpdate(obj);
        realm.commitTransaction();
    }

    public void deleteGroupNotificationId(String groupId, int notificationId) {
        Realm realm = Realm.getDefaultInstance();
        NotificationIdModel obj = realm.where(NotificationIdModel.class)
                .equalTo("groupId", groupId).equalTo("notificationId", notificationId)
                .findFirst();
        if (obj != null) {
            realm.beginTransaction();
            obj.deleteFromRealm();
            realm.commitTransaction();
        }
    }

    public RealmResults<NotificationIdModel> getAllNotificationIdByGroup(String groupId) {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(NotificationIdModel.class)
                .equalTo("groupId", groupId).findAll();
    }

    public void clearBannerList() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(BannerModel.class);
        realm.commitTransaction();
    }

    public void saveBanner(BannerModel bannerModel) {
        clearBannerList();
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insertOrUpdate(bannerModel);
        realm.commitTransaction();
    }

    public BannerModel getBanner() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(BannerModel.class).findFirst();
    }
*//*


}

*/
