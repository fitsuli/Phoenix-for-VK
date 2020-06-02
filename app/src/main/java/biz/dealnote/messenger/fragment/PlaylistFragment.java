package biz.dealnote.messenger.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import biz.dealnote.messenger.Extra;
import biz.dealnote.messenger.R;
import biz.dealnote.messenger.adapter.AudioRecyclerAdapter;
import biz.dealnote.messenger.listener.BackPressCallback;
import biz.dealnote.messenger.model.Audio;
import biz.dealnote.messenger.place.PlaceFactory;
import biz.dealnote.messenger.player.MusicPlaybackService;
import biz.dealnote.messenger.player.util.MusicUtils;
import biz.dealnote.messenger.settings.Settings;
import biz.dealnote.messenger.util.PhoenixToast;

import static biz.dealnote.messenger.util.Objects.isNull;

/**
 * Created by golde on 27.09.2016.
 */
public class PlaylistFragment extends BottomSheetDialogFragment implements AudioRecyclerAdapter.ClickListener,
        BackPressCallback {

    private RecyclerView mRecyclerView;
    private AudioRecyclerAdapter mAdapter;
    private ArrayList<Audio> mData;
    private PlaybackStatus mPlaybackStatus;
    private MaterialToolbar mToolbar;

    public static Bundle buildArgs(ArrayList<Audio> playlist) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Extra.AUDIOS, playlist);
        return bundle;
    }

    public static PlaylistFragment newInstance(ArrayList<Audio> playlist) {
        return newInstance(buildArgs(playlist));
    }

    public static PlaylistFragment newInstance(Bundle args) {
        PlaylistFragment fragment = new PlaylistFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            restoreFromSavedInstanceState(savedInstanceState);
        }
        mData = requireArguments().getParcelableArrayList(Extra.AUDIOS);
    }

    private int getAudioPos(Audio audio) {
        if (mData != null && !mData.isEmpty()) {
            int pos = 0;
            for (final Audio i : mData) {
                if (i.getId() == audio.getId() && i.getOwnerId() == audio.getOwnerId()) {
                    i.setAnimationNow(true);
                    mAdapter.notifyDataSetChanged();
                    return pos;
                }
                pos++;
            }
        }
        return -1;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_playlist, container, false);

        mToolbar = root.findViewById(R.id.toolbar);
        mRecyclerView = root.findViewById(R.id.list);
        LinearLayoutManager manager = new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        FloatingActionButton Goto = root.findViewById(R.id.goto_button);

        Goto.setOnLongClickListener(v -> {
            Audio curr = MusicUtils.getCurrentAudio();
            if (curr != null) {
                PlaceFactory.getPlayerPlace(Settings.get().accounts().getCurrent()).tryOpenWith(requireActivity());
            } else
                PhoenixToast.CreatePhoenixToast(requireActivity()).showToastError(R.string.null_audio);
            return false;
        });
        Goto.setOnClickListener(v -> {
            Audio curr = MusicUtils.getCurrentAudio();
            if (curr != null) {
                int index = getAudioPos(curr);
                if (index >= 0) {
                    if (Settings.get().other().isShow_audio_cover())
                        mRecyclerView.scrollToPosition(index);
                    else
                        mRecyclerView.smoothScrollToPosition(index);
                } else
                    PhoenixToast.CreatePhoenixToast(requireActivity()).showToast(R.string.audio_not_found);
            } else
                PhoenixToast.CreatePhoenixToast(requireActivity()).showToastError(R.string.null_audio);
        });
        return root;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new AudioRecyclerAdapter(requireActivity(), mData, false, false);
        mAdapter.setClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void restoreFromSavedInstanceState(@NonNull Bundle state) {
        //this.mData = state.getParcelableArrayList(Extra.AUDIOS);
    }

    @Override
    public void onClick(int position, Audio audio) {
        MusicPlaybackService.startForPlayList(requireActivity(), mData, position, false);
        dismissAllowingStateLoss();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mToolbar != null) {
            mToolbar.setTitle(R.string.playlist);
            mToolbar.setSubtitle(null);
        }

        this.mPlaybackStatus = new PlaybackStatus();
        final IntentFilter filter = new IntentFilter();
        filter.addAction(MusicPlaybackService.PLAYSTATE_CHANGED);
        filter.addAction(MusicPlaybackService.SHUFFLEMODE_CHANGED);
        filter.addAction(MusicPlaybackService.REPEATMODE_CHANGED);
        filter.addAction(MusicPlaybackService.META_CHANGED);
        filter.addAction(MusicPlaybackService.PREPARED);
        filter.addAction(MusicPlaybackService.REFRESH);
        requireActivity().registerReceiver(mPlaybackStatus, filter);
    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

    @Override
    public void onPause() {
        try {
            requireActivity().unregisterReceiver(mPlaybackStatus);
        } catch (final Throwable ignored) {
        }
        super.onPause();
    }

    private final class PlaybackStatus extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (isNull(action)) return;

            if (MusicPlaybackService.PLAYSTATE_CHANGED.equals(action)) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
