package biz.dealnote.messenger.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import biz.dealnote.messenger.Extra;
import biz.dealnote.messenger.R;
import biz.dealnote.messenger.adapter.VideoAlbumsNewAdapter;
import biz.dealnote.messenger.fragment.base.BaseMvpFragment;
import biz.dealnote.messenger.listener.EndlessRecyclerOnScrollListener;
import biz.dealnote.messenger.listener.PicassoPauseOnScrollListener;
import biz.dealnote.messenger.model.VideoAlbum;
import biz.dealnote.messenger.mvp.presenter.VideoAlbumsPresenter;
import biz.dealnote.messenger.mvp.view.IVideoAlbumsView;
import biz.dealnote.messenger.place.PlaceFactory;
import biz.dealnote.messenger.util.Objects;
import biz.dealnote.messenger.util.ViewUtils;
import biz.dealnote.mvp.core.IPresenterFactory;

public class VideoAlbumsFragment extends BaseMvpFragment<VideoAlbumsPresenter, IVideoAlbumsView>
        implements VideoAlbumsNewAdapter.Listener, IVideoAlbumsView {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private VideoAlbumsNewAdapter mAdapter;
    private TextView mEmpty;

    public static VideoAlbumsFragment newInstance(Bundle args) {
        VideoAlbumsFragment fragment = new VideoAlbumsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static VideoAlbumsFragment newInstance(int accountId, int ownerId, String action) {
        return newInstance(buildArgs(accountId, ownerId, action));
    }

    public static Bundle buildArgs(int aid, int ownerId, String action) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, aid);
        args.putInt(Extra.OWNER_ID, ownerId);
        args.putString(Extra.ACTION, action);
        return args;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_video_albums, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);

        mSwipeRefreshLayout = root.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(() -> getPresenter().fireRefresh());

        ViewUtils.setupSwipeRefreshLayoutWithCurrentTheme(requireActivity(), mSwipeRefreshLayout, true);

        mEmpty = root.findViewById(R.id.empty);

        int columns = requireActivity().getResources().getInteger(R.integer.videos_column_count);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(manager);
        recyclerView.addOnScrollListener(new PicassoPauseOnScrollListener(VideoAlbumsNewAdapter.PICASSO_TAG));
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onScrollToLastElement() {
                getPresenter().fireScrollToLast();
            }
        });

        mAdapter = new VideoAlbumsNewAdapter(requireActivity(), Collections.emptyList());
        mAdapter.setListener(this);
        recyclerView.setAdapter(mAdapter);

        resolveEmptyTextVisibility();
        return root;
    }

    @Override
    public void onClick(VideoAlbum album) {
        getPresenter().fireItemClick(album);
    }

    @Override
    public void displayData(@NonNull List<VideoAlbum> data) {
        if (Objects.nonNull(mAdapter)) {
            mAdapter.setData(data);
            resolveEmptyTextVisibility();
        }
    }

    @Override
    public void notifyDataAdded(int position, int count) {
        if (Objects.nonNull(mAdapter)) {
            mAdapter.notifyItemRangeInserted(position, count);
            resolveEmptyTextVisibility();
        }
    }

    @Override
    public void displayLoading(boolean loading) {
        if (Objects.nonNull(mSwipeRefreshLayout)) {
            mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(loading));
        }
    }

    private void resolveEmptyTextVisibility() {
        if (Objects.nonNull(mEmpty) && Objects.nonNull(mAdapter)) {
            mEmpty.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void openAlbum(int accountId, int ownerId, int albumId, String action, String title) {
        PlaceFactory.getVideoAlbumPlace(accountId, ownerId, albumId, action, title).tryOpenWith(requireActivity());
    }

    @Override
    public void notifyDataSetChanged() {
        if (Objects.nonNull(mAdapter)) {
            mAdapter.notifyDataSetChanged();
            resolveEmptyTextVisibility();
        }
    }

    @NotNull
    @Override
    public IPresenterFactory<VideoAlbumsPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> {
            int ownerId1 = requireArguments().getInt(Extra.OWNER_ID);
            int accountId = requireArguments().getInt(Extra.ACCOUNT_ID);
            String action = requireArguments().getString(Extra.ACTION);
            return new VideoAlbumsPresenter(accountId, ownerId1, action, saveInstanceState);
        };
    }
}