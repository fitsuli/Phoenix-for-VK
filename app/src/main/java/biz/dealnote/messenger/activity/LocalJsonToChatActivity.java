package biz.dealnote.messenger.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import biz.dealnote.messenger.Extra;
import biz.dealnote.messenger.fragment.AudioPlayerFragment;
import biz.dealnote.messenger.fragment.GifPagerFragment;
import biz.dealnote.messenger.fragment.LocalJsonToChatFragment;
import biz.dealnote.messenger.fragment.PhotoPagerFragment;
import biz.dealnote.messenger.fragment.StoryPagerFragment;
import biz.dealnote.messenger.listener.AppStyleable;
import biz.dealnote.messenger.place.Place;
import biz.dealnote.messenger.place.PlaceProvider;
import biz.dealnote.messenger.settings.CurrentTheme;
import biz.dealnote.messenger.settings.ISettings;
import biz.dealnote.messenger.settings.Settings;
import biz.dealnote.messenger.util.Objects;
import biz.dealnote.messenger.util.StatusbarUtil;
import biz.dealnote.messenger.util.Utils;
import biz.dealnote.messenger.util.ViewUtils;

public class LocalJsonToChatActivity extends NoMainActivity implements PlaceProvider, AppStyleable {

    private final FragmentManager.OnBackStackChangedListener mOnBackStackChangedListener = this::keyboardHide;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Objects.isNull(savedInstanceState)) {
            handleIntent(getIntent());
            getSupportFragmentManager().addOnBackStackChangedListener(mOnBackStackChangedListener);
        }
    }

    private void handleIntent(Intent intent) {
        if (intent == null) {
            finish();
        }
        int accountId = Settings.get().accounts().getCurrent();
        if (accountId == ISettings.IAccountsSettings.INVALID_ID) {
            finish();
        }
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            attachInitialFragment(LocalJsonToChatFragment.newInstance(accountId));
        }
    }

    public void keyboardHide() {
        try {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        } catch (Exception ignored) {
        }
    }

    private void attachInitialFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(getMainContainerViewId(), fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void openPlace(Place place) {
        final Bundle args = place.getArgs();
        switch (place.type) {
            case Place.VK_PHOTO_ALBUM_GALLERY:

            case Place.FAVE_PHOTOS_GALLERY:

            case Place.SIMPLE_PHOTO_GALLERY:

            case Place.VK_PHOTO_TMP_SOURCE:
                attachInitialFragment(PhotoPagerFragment.newInstance(place.type, args));
                break;
            case Place.GIF_PAGER:
                attachInitialFragment(GifPagerFragment.newInstance(args));
                break;
            case Place.STORY_PLAYER:
                attachInitialFragment(StoryPagerFragment.newInstance(args));
                break;
            case Place.PLAYER:
                Fragment player = getSupportFragmentManager().findFragmentByTag("audio_player");
                if (player instanceof AudioPlayerFragment)
                    ((AudioPlayerFragment) player).dismiss();
                AudioPlayerFragment.newInstance(args).show(getSupportFragmentManager(), "audio_player");
                break;
            default:
                Intent intent = new Intent(this, SwipebleActivity.class);
                intent.setAction(SwipebleActivity.ACTION_OPEN_PLACE);
                intent.putExtra(Extra.PLACE, place);
                SwipebleActivity.start(this, intent);
                break;
        }
    }

    public void onPause() {
        ViewUtils.keyboardHide(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        getSupportFragmentManager().removeOnBackStackChangedListener(mOnBackStackChangedListener);
        ViewUtils.keyboardHide(this);
        super.onDestroy();
    }

    @Override
    public void hideMenu(boolean hide) {

    }

    @Override
    public void openMenu(boolean open) {

    }

    @Override
    public void setStatusbarColored(boolean colored, boolean invertIcons) {
        int statusbarNonColored = CurrentTheme.getStatusBarNonColored(this);
        int statusbarColored = CurrentTheme.getStatusBarColor(this);

        if (Utils.hasLollipop()) {
            Window w = getWindow();
            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.setStatusBarColor(colored ? statusbarColored : statusbarNonColored);
            int navigationColor = colored ? CurrentTheme.getNavigationBarColor(this) : Color.BLACK;
            w.setNavigationBarColor(navigationColor);
        }

        if (Utils.hasMarshmallow()) {
            int flags = getWindow().getDecorView().getSystemUiVisibility();
            if (invertIcons) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            getWindow().getDecorView().setSystemUiVisibility(flags);

            StatusbarUtil.setCustomStatusbarDarkMode(this, invertIcons);
        }

        if (Utils.hasOreo()) {
            Window w = getWindow();
            int flags = getWindow().getDecorView().getSystemUiVisibility();
            if (invertIcons) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                w.getDecorView().setSystemUiVisibility(flags);
                w.setNavigationBarColor(Color.WHITE);
            } else {
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                w.getDecorView().setSystemUiVisibility(flags);
                @ColorInt
                int navigationColor = colored ?
                        CurrentTheme.getNavigationBarColor(this) : Color.BLACK;
                w.setNavigationBarColor(navigationColor);
            }
        }
    }
}
