package biz.dealnote.messenger.db.interfaces;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import biz.dealnote.messenger.db.model.BanAction;
import biz.dealnote.messenger.db.model.UserPatch;
import biz.dealnote.messenger.db.model.entity.CommunityDetailsEntity;
import biz.dealnote.messenger.db.model.entity.CommunityEntity;
import biz.dealnote.messenger.db.model.entity.FriendListEntity;
import biz.dealnote.messenger.db.model.entity.OwnerEntities;
import biz.dealnote.messenger.db.model.entity.UserDetailsEntity;
import biz.dealnote.messenger.db.model.entity.UserEntity;
import biz.dealnote.messenger.model.Manager;
import biz.dealnote.messenger.util.Optional;
import biz.dealnote.messenger.util.Pair;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

public interface IOwnersStorage extends IStorage {

    Single<Map<Integer, FriendListEntity>> findFriendsListsByIds(int accountId, int userId, Collection<Integer> ids);

    @CheckResult
    Maybe<String> getLocalizedUserActivity(int accountId, int userId);

    Single<Optional<UserEntity>> findUserDboById(int accountId, int ownerId);

    Single<Optional<CommunityEntity>> findCommunityDboById(int accountId, int ownerId);

    Single<Optional<UserEntity>> findUserByDomain(int accoutnId, String domain);

    Single<Optional<CommunityEntity>> findCommunityByDomain(int accountId, String domain);

    Single<List<UserEntity>> findUserDbosByIds(int accountId, List<Integer> ids);

    Single<List<CommunityEntity>> findCommunityDbosByIds(int accountId, List<Integer> ids);

    Completable storeUserDbos(int accountId, List<UserEntity> users);

    Completable storeCommunityDbos(int accountId, List<CommunityEntity> communityEntities);

    Completable storeOwnerEntities(int accountId, OwnerEntities entities);

    @CheckResult
    Single<Collection<Integer>> getMissingUserIds(int accountId, @NonNull Collection<Integer> ids);

    @CheckResult
    Single<Collection<Integer>> getMissingCommunityIds(int accountId, @NonNull Collection<Integer> ids);

    Completable fireBanAction(BanAction action);

    Observable<BanAction> observeBanActions();

    Completable fireManagementChangeAction(Pair<Integer, Manager> manager);

    Observable<Pair<Integer, Manager>> observeManagementChanges();

    Single<Optional<CommunityDetailsEntity>> getGroupsDetails(int accountId, int groupId);

    Completable storeGroupsDetails(int accountId, int groupId, CommunityDetailsEntity dbo);

    Single<Optional<UserDetailsEntity>> getUserDetails(int accountId, int userId);

    Completable storeUserDetails(int accountId, int userId, UserDetailsEntity dbo);

    Completable applyPathes(int accountId, @NonNull List<UserPatch> patches);
}