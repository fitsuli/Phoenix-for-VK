package biz.dealnote.messenger.domain;

import androidx.annotation.NonNull;

import java.util.List;

import biz.dealnote.messenger.model.AbsModel;
import biz.dealnote.messenger.model.Comment;
import biz.dealnote.messenger.model.CommentIntent;
import biz.dealnote.messenger.model.Commented;
import biz.dealnote.messenger.model.CommentsBundle;
import biz.dealnote.messenger.model.DraftComment;
import biz.dealnote.messenger.model.Owner;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

public interface ICommentsInteractor {

    Single<List<Comment>> getAllCachedData(int accounrId, @NonNull Commented commented);

    Single<CommentsBundle> getCommentsPortion(int accountId, @NonNull Commented commented, int offset,
                                              int count, Integer startCommentId, Integer threadComment, boolean invalidateCache, String sort);

    Maybe<DraftComment> restoreDraftComment(int accountId, @NonNull Commented commented);

    Single<Integer> safeDraftComment(int accountId, @NonNull Commented commented, String body, int replyToCommentId, int replyToUserId);

    Completable like(int accountId, Commented commented, int commentId, boolean add);

    Completable deleteRestore(int accountId, Commented commented, int commentId, boolean delete);

    Single<Comment> send(int accountId, Commented commented, Integer commentThread, CommentIntent intent);

    Single<List<Comment>> getAllCommentsRange(int accountId, Commented commented, int startFromCommentId, int continueToCommentId);

    Single<List<Owner>> getAvailableAuthors(int accountId);

    Single<Comment> edit(int accountId, Commented commented, int commentId, String body, Integer commentThread, List<AbsModel> attachments);

    Single<Integer> reportComment(int accountId, int owner_id, int post_id, int reason);
}