package biz.dealnote.messenger.db.interfaces;

import androidx.annotation.NonNull;

import java.util.List;

import biz.dealnote.messenger.db.AttachToType;
import biz.dealnote.messenger.db.model.entity.Entity;
import biz.dealnote.messenger.util.Pair;
import io.reactivex.Completable;
import io.reactivex.Single;


public interface IAttachmentsStorage extends IStorage {

    Completable remove(int accountId, @AttachToType int attachToType, int attachToDbid, int generatedAttachmentId);

    Single<int[]> attachDbos(int accountId, @AttachToType int attachToType, int attachToDbid, @NonNull List<Entity> entities);

    Single<Integer> getCount(int accountId, @AttachToType int attachToType, int attachToDbid);

    Single<List<Pair<Integer, Entity>>> getAttachmentsDbosWithIds(int accountId, @AttachToType int attachToType, int attachToDbid);

    List<Entity> getAttachmentsDbosSync(int accountId, @AttachToType int attachToType, int attachToDbid, @NonNull Cancelable cancelable);
}