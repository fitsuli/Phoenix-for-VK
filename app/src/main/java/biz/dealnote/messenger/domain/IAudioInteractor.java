package biz.dealnote.messenger.domain;

import java.util.Collection;
import java.util.List;

import biz.dealnote.messenger.api.model.VKApiAudioPlaylist;
import biz.dealnote.messenger.fragment.search.criteria.AudioSearchCriteria;
import biz.dealnote.messenger.model.Audio;
import biz.dealnote.messenger.model.IdPair;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.annotations.Nullable;

/**
 * Created by admin on 07.10.2017.
 * Phoenix-for-VK
 */
public interface IAudioInteractor {
    Single<Audio> add(int accountId, Audio audio, Integer groupId, Integer albumId);
    Completable delete(int accountId, int audioId, int ownerId);
    Completable restore(int accountId, int audioId, int ownerId);

    Completable sendBroadcast(int accountId, int audioOwnerId, int audioId, @Nullable Collection<Integer> targetIds);

    Single<List<Audio>> get(int accountId, Integer album_id, int ownerId, int offset, String accessKey);

    Single<List<Audio>> getById(List<IdPair> audios);

    Single<String> getLyrics(int lyrics_id);

    Single<List<Audio>> getPopular(int accountId, int foreign, int genre);

    Single<List<Audio>> getRecommendations(int accountId, int audioOwnerId);

    Single<List<Audio>> getRecommendationsByAudio(int accountId, String audio);

    Single<List<Audio>> search(int accountId, AudioSearchCriteria criteria, int offset);

    Single<List<VKApiAudioPlaylist>> getPlaylists(int accountId, int owner_id, int offset);

    Single<VKApiAudioPlaylist> followPlaylist(int accountId, int playlist_id, int ownerId, String accessKey);

    Single<VKApiAudioPlaylist> getPlaylistById(int accountId, int playlist_id, int ownerId, String accessKey);

    Single<Integer> deletePlaylist(int accountId, int playlist_id, int ownerId);
}