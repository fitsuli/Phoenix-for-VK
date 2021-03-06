package biz.dealnote.messenger.api.interfaces;

import androidx.annotation.CheckResult;

import java.util.Collection;
import java.util.List;

import biz.dealnote.messenger.api.model.GroupSettingsDto;
import biz.dealnote.messenger.api.model.Items;
import biz.dealnote.messenger.api.model.VKApiCommunity;
import biz.dealnote.messenger.api.model.VKApiUser;
import biz.dealnote.messenger.api.model.VkApiBanned;
import biz.dealnote.messenger.api.model.response.GroupLongpollServer;
import io.reactivex.Completable;
import io.reactivex.Single;


public interface IGroupsApi {

    @CheckResult
    Completable editManager(int groupId, int userId, String role, Boolean isContact, String contactPosition, String contactPhone, String contactEmail);

    @CheckResult
    Completable unban(int groupId, int ownerId);

    @CheckResult
    Completable ban(int groupId, int ownerId, Long endDate, Integer reason, String comment, Boolean commentVisible);

    @CheckResult
    Single<GroupSettingsDto> getSettings(int groupId);

    @CheckResult
    Single<Items<VkApiBanned>> getBanned(int groupId, Integer offset, Integer count, String fields, Integer userId);

    @CheckResult
    Single<VKApiCommunity> getWallInfo(String groupId, String fields);

    @CheckResult
    Single<Items<VKApiUser>> getMembers(String groupId, Integer sort, Integer offset,
                                        Integer count, String fields, String filter);

    @CheckResult
    Single<Items<VKApiCommunity>> search(String query, String type, Integer countryId, Integer cityId,
                                         Boolean future, Boolean market, Integer sort, Integer offset, Integer count);

    @CheckResult
    Single<Boolean> leave(int groupId);

    @CheckResult
    Single<Boolean> join(int groupId, Integer notSure);

    @CheckResult
    Single<Items<VKApiCommunity>> get(Integer userId, Boolean extended, String filter,
                                      String fields, Integer offset, Integer count);

    @CheckResult
    Single<List<VKApiCommunity>> getById(Collection<Integer> ids, Collection<String> domains,
                                         String groupId, String fields);

    @CheckResult
    Single<GroupLongpollServer> getLongPollServer(int groupId);
}
