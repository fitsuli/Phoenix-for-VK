package biz.dealnote.messenger.mvp.presenter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import biz.dealnote.messenger.domain.IFaveInteractor;
import biz.dealnote.messenger.domain.InteractorFactory;
import biz.dealnote.messenger.model.Video;
import biz.dealnote.messenger.mvp.presenter.base.AccountDependencyPresenter;
import biz.dealnote.messenger.mvp.view.IFaveVideosView;
import biz.dealnote.messenger.util.RxUtils;
import biz.dealnote.mvp.reflect.OnGuiCreated;
import io.reactivex.disposables.CompositeDisposable;


public class FaveVideosPresenter extends AccountDependencyPresenter<IFaveVideosView> {

    private static final String TAG = FaveVideosPresenter.class.getSimpleName();
    private static final int COUNT_PER_REQUEST = 25;
    private final IFaveInteractor faveInteractor;
    private final ArrayList<Video> mVideos;
    private final CompositeDisposable cacheDisposable = new CompositeDisposable();
    private final CompositeDisposable netDisposable = new CompositeDisposable();
    private boolean mEndOfContent;
    private boolean cacheLoadingNow;
    private boolean netLoadingNow;

    public FaveVideosPresenter(int accountId, @Nullable Bundle savedInstanceState) {
        super(accountId, savedInstanceState);

        faveInteractor = InteractorFactory.createFaveInteractor();
        mVideos = new ArrayList<>();

        loadCachedData();
    }

    public void LoadTool() {
        requestAtLast();
    }

    @OnGuiCreated
    private void resolveRefreshingView() {
        if (isGuiReady()) {
            getView().showRefreshing(netLoadingNow);
        }
    }

    private void loadCachedData() {
        cacheLoadingNow = true;

        int accoutnId = getAccountId();
        cacheDisposable.add(faveInteractor.getCachedVideos(accoutnId)
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(this::onCachedDataReceived, this::onCacheGetError));
    }

    private void onCacheGetError(Throwable t) {
        cacheLoadingNow = false;
        showError(getView(), t);
    }

    private void onCachedDataReceived(List<Video> videos) {
        cacheLoadingNow = false;

        mVideos.clear();
        mVideos.addAll(videos);
        callView(IFaveVideosView::notifyDataSetChanged);
    }

    @Override
    public void onDestroyed() {
        cacheDisposable.dispose();
        netDisposable.dispose();
        super.onDestroyed();
    }

    private void request(int offset) {
        netLoadingNow = true;
        resolveRefreshingView();

        int accountId = getAccountId();

        netDisposable.add(faveInteractor.getVideos(accountId, COUNT_PER_REQUEST, offset)
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(videos -> onNetDataReceived(offset, videos), this::onNetDataGetError));
    }

    private void onNetDataGetError(Throwable t) {
        netLoadingNow = false;
        resolveRefreshingView();
        showError(getView(), t);
    }

    private void onNetDataReceived(int offset, List<Video> videos) {
        cacheDisposable.clear();
        cacheLoadingNow = false;

        mEndOfContent = videos.isEmpty();
        netLoadingNow = false;

        if (offset == 0) {
            mVideos.clear();
            mVideos.addAll(videos);
            callView(IFaveVideosView::notifyDataSetChanged);
        } else {
            int startSize = mVideos.size();
            mVideos.addAll(videos);
            callView(view -> view.notifyDataAdded(startSize, videos.size()));
        }

        resolveRefreshingView();
    }

    private void requestAtLast() {
        request(0);
    }

    private void requestNext() {
        request(mVideos.size());
    }

    @Override
    public void onGuiCreated(@NonNull IFaveVideosView viewHost) {
        super.onGuiCreated(viewHost);
        viewHost.displayData(mVideos);
    }

    private boolean canLoadMore() {
        return !mVideos.isEmpty() && !cacheLoadingNow && !netLoadingNow && !mEndOfContent;
    }

    public void fireRefresh() {
        cacheDisposable.clear();
        netDisposable.clear();
        netLoadingNow = false;

        requestAtLast();
    }

    public void fireVideoClick(Video video) {
        getView().goToPreview(getAccountId(), video);
    }

    public void fireScrollToEnd() {
        if (canLoadMore()) {
            requestNext();
        }
    }
}