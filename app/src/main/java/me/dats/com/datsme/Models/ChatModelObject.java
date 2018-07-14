package me.dats.com.datsme.Models;

public class ChatModelObject extends ListObject {

    private Messages chatModel;

    public Messages getChatModel() {
        return chatModel;
    }

    public void setChatModel(Messages chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public int getType(String userId) {
        if (this.chatModel.getFrom().equals(userId)) {
            return TYPE_GENERAL_RIGHT;
        } else
            return TYPE_GENERAL_LEFT;
    }
}