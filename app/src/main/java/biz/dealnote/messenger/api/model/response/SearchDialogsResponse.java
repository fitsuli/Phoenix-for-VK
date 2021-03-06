package biz.dealnote.messenger.api.model.response;

import java.util.List;

import biz.dealnote.messenger.api.model.VKApiChat;
import biz.dealnote.messenger.api.model.VKApiCommunity;
import biz.dealnote.messenger.api.model.VKApiUser;

public class SearchDialogsResponse {

    private List<AbsChattable> data;

    public List<AbsChattable> getData() {
        return data;
    }

    public SearchDialogsResponse setData(List<AbsChattable> data) {
        this.data = data;
        return this;
    }

    public abstract static class AbsChattable {

    }

    public static class Chat extends AbsChattable {

        private final VKApiChat chat;

        public Chat(VKApiChat chat) {
            this.chat = chat;
        }

        public VKApiChat getChat() {
            return chat;
        }
    }

    public static class User extends AbsChattable {

        private final VKApiUser user;

        public User(VKApiUser user) {
            this.user = user;
        }

        public VKApiUser getUser() {
            return user;
        }
    }

    public static class Community extends AbsChattable {

        private final VKApiCommunity community;

        public Community(VKApiCommunity community) {
            this.community = community;
        }

        public VKApiCommunity getCommunity() {
            return community;
        }
    }
}
