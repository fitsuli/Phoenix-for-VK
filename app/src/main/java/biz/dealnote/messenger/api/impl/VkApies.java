package biz.dealnote.messenger.api.impl;

import android.annotation.SuppressLint;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import biz.dealnote.messenger.api.IServiceProvider;
import biz.dealnote.messenger.api.IVkRetrofitProvider;
import biz.dealnote.messenger.api.RetrofitWrapper;
import biz.dealnote.messenger.api.TokenType;
import biz.dealnote.messenger.api.interfaces.IAccountApi;
import biz.dealnote.messenger.api.interfaces.IAccountApis;
import biz.dealnote.messenger.api.interfaces.IAudioApi;
import biz.dealnote.messenger.api.interfaces.IBoardApi;
import biz.dealnote.messenger.api.interfaces.ICommentsApi;
import biz.dealnote.messenger.api.interfaces.IDatabaseApi;
import biz.dealnote.messenger.api.interfaces.IDocsApi;
import biz.dealnote.messenger.api.interfaces.IFaveApi;
import biz.dealnote.messenger.api.interfaces.IFriendsApi;
import biz.dealnote.messenger.api.interfaces.IGroupsApi;
import biz.dealnote.messenger.api.interfaces.ILikesApi;
import biz.dealnote.messenger.api.interfaces.IMessagesApi;
import biz.dealnote.messenger.api.interfaces.INewsfeedApi;
import biz.dealnote.messenger.api.interfaces.INotificationsApi;
import biz.dealnote.messenger.api.interfaces.IOtherApi;
import biz.dealnote.messenger.api.interfaces.IPagesApi;
import biz.dealnote.messenger.api.interfaces.IPhotosApi;
import biz.dealnote.messenger.api.interfaces.IPollsApi;
import biz.dealnote.messenger.api.interfaces.IStatusApi;
import biz.dealnote.messenger.api.interfaces.IStoreApi;
import biz.dealnote.messenger.api.interfaces.IUsersApi;
import biz.dealnote.messenger.api.interfaces.IUtilsApi;
import biz.dealnote.messenger.api.interfaces.IVideoApi;
import biz.dealnote.messenger.api.interfaces.IWallApi;
import io.reactivex.Single;

import static biz.dealnote.messenger.util.Utils.intValueIn;

class VkApies implements IAccountApis {

    @SuppressLint("UseSparseArrays")
    private static final Map<Integer, VkApies> APIS = new HashMap<>(1);

    private final IMessagesApi messagesApi;
    private final IPhotosApi photosApi;
    private final IFriendsApi friendsApi;
    private final IDocsApi docsApi;
    private final IWallApi wallApi;
    private final INewsfeedApi newsfeedApi;
    private final ICommentsApi commentsApi;
    private final INotificationsApi notificationsApi;
    private final IVideoApi videoApi;
    private final IBoardApi boardApi;
    private final IUsersApi usersApi;
    private final IGroupsApi groupsApi;
    private final IAccountApi accountApi;
    private final IDatabaseApi databaseApi;
    private final IAudioApi audioApi;
    private final IStatusApi statusApi;
    private final ILikesApi likesApi;
    private final IPagesApi pagesApi;
    private final IStoreApi storeApi;
    private final IFaveApi faveApi;
    private final IPollsApi pollsApi;
    private final IUtilsApi utilsApi;
    private final IOtherApi otherApi;

    private VkApies(int accountId, boolean useCustomToken, String customAccessToken, IVkRetrofitProvider provider) {
        IServiceProvider retrofitProvider = new IServiceProvider() {
            @Override
            public <T> Single<T> provideService(int accountId, Class<T> serviceClass, int... tokenTypes) {
                return provideRetrofit(accountId, tokenTypes).map(retrofit -> retrofit.create(serviceClass));
            }

            Single<RetrofitWrapper> provideRetrofit(int aid, int... tokenPolicy) {
                if (useCustomToken) {
                    return provider.provideCustomRetrofit(aid, customAccessToken);
                }

                boolean isCommunity = aid < 0;

                if (isCommunity) {
                    if (intValueIn(TokenType.COMMUNITY, tokenPolicy)) {
                        return provider.provideNormalRetrofit(aid);
                    } else if (intValueIn(TokenType.SERVICE, tokenPolicy)) {
                        return provider.provideServiceRetrofit();
                    } else {
                        return Single.error(new UnsupportedOperationException("Unsupported account_id: " + aid + " with token_policy: " + Arrays.toString(tokenPolicy)));
                    }
                } else {
                    if (intValueIn(TokenType.USER, tokenPolicy)) {
                        return provider.provideNormalRetrofit(aid);
                    } else if (intValueIn(TokenType.SERVICE, tokenPolicy)) {
                        return provider.provideServiceRetrofit();
                    } else {
                        return Single.error(new UnsupportedOperationException("Unsupported account_id: " + aid + " with token_policy: " + Arrays.toString(tokenPolicy)));
                    }
                }
            }
        };

        accountApi = new AccountApi(accountId, retrofitProvider);
        audioApi = new AudioApi(accountId, retrofitProvider);
        boardApi = new BoardApi(accountId, retrofitProvider);
        commentsApi = new CommentsApi(accountId, retrofitProvider);
        databaseApi = new DatabaseApi(accountId, retrofitProvider);
        docsApi = new DocsApi(accountId, retrofitProvider);
        faveApi = new FaveApi(accountId, retrofitProvider);
        friendsApi = new FriendsApi(accountId, retrofitProvider);
        groupsApi = new GroupsApi(accountId, retrofitProvider);
        likesApi = new LikesApi(accountId, retrofitProvider);
        messagesApi = new MessagesApi(accountId, retrofitProvider);
        newsfeedApi = new NewsfeedApi(accountId, retrofitProvider);
        notificationsApi = new NotificationsApi(accountId, retrofitProvider);
        pagesApi = new PagesApi(accountId, retrofitProvider);
        photosApi = new PhotosApi(accountId, retrofitProvider);
        pollsApi = new PollsApi(accountId, retrofitProvider);
        statusApi = new StatusApi(accountId, retrofitProvider);
        storeApi = new StoreApi(accountId, retrofitProvider);
        usersApi = new UsersApi(accountId, retrofitProvider);
        utilsApi = new UtilsApi(accountId, retrofitProvider);
        videoApi = new VideoApi(accountId, retrofitProvider);
        wallApi = new WallApi(accountId, retrofitProvider);
        otherApi = new OtherApi(accountId, provider);
    }

    public static VkApies create(int accountId, String accessToken, IVkRetrofitProvider provider) {
        return new VkApies(accountId, true, accessToken, provider);
    }

    public static synchronized VkApies get(int accountId, IVkRetrofitProvider provider) {
        VkApies apies = APIS.get(accountId);
        if (apies == null) {
            apies = new VkApies(accountId, false, null, provider);
            APIS.put(accountId, apies);
        }

        return apies;
    }

    @Override
    public IMessagesApi messages() {
        return messagesApi;
    }

    @Override
    public IPhotosApi photos() {
        return photosApi;
    }

    @Override
    public IFriendsApi friends() {
        return friendsApi;
    }

    @Override
    public IWallApi wall() {
        return wallApi;
    }

    @Override
    public IDocsApi docs() {
        return docsApi;
    }

    @Override
    public INewsfeedApi newsfeed() {
        return newsfeedApi;
    }

    @Override
    public ICommentsApi comments() {
        return commentsApi;
    }

    @Override
    public INotificationsApi notifications() {
        return notificationsApi;
    }

    @Override
    public IVideoApi video() {
        return videoApi;
    }

    @Override
    public IBoardApi board() {
        return boardApi;
    }

    @Override
    public IUsersApi users() {
        return usersApi;
    }

    @Override
    public IGroupsApi groups() {
        return groupsApi;
    }

    @Override
    public IAccountApi account() {
        return accountApi;
    }

    @Override
    public IDatabaseApi database() {
        return databaseApi;
    }

    @Override
    public IAudioApi audio() {
        return audioApi;
    }

    @Override
    public IStatusApi status() {
        return statusApi;
    }

    @Override
    public ILikesApi likes() {
        return likesApi;
    }

    @Override
    public IPagesApi pages() {
        return pagesApi;
    }

    @Override
    public IStoreApi store() {
        return storeApi;
    }

    @Override
    public IFaveApi fave() {
        return faveApi;
    }

    @Override
    public IPollsApi polls() {
        return pollsApi;
    }

    @Override
    public IUtilsApi utils() {
        return utilsApi;
    }

    @Override
    public IOtherApi other() {
        return otherApi;
    }
}