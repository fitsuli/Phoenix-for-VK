package biz.dealnote.messenger.push.message;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

import biz.dealnote.messenger.Extra;
import biz.dealnote.messenger.R;
import biz.dealnote.messenger.activity.MainActivity;
import biz.dealnote.messenger.longpoll.AppNotificationChannels;
import biz.dealnote.messenger.longpoll.NotificationHelper;
import biz.dealnote.messenger.place.PlaceFactory;
import biz.dealnote.messenger.settings.Settings;
import biz.dealnote.messenger.util.Logger;
import biz.dealnote.messenger.util.Utils;

import static biz.dealnote.messenger.push.NotificationUtils.configOtherPushNotification;

public class NewPostPushMessage {

    //key: image_type, value: user, class: class java.lang.String
//key: from_id, value: 216143660, class: class java.lang.String
//key: id, value: new_post_216143660_1137, class: class java.lang.String
//key: url, value: https://vk.com/wall216143660_1137, class: class java.lang.String
//key: body, value: Мы тестируем FCM. Всем здравствуйте.
//
//key: icon, value: followers_24, class: class java.lang.String
//key: time, value: 1529683043, class: class java.lang.String
//key: type, value: post, class: class java.lang.String
//key: badge, value: 1, class: class java.lang.String
//key: image, value: [{"width":200,"url":"https:\/\/pp.userapi.com\/c844520\/v844520706\/71a39\/nc5YPeh1yEI.jpg","height":200},{"width":100,"url":"https:\/\/pp.userapi.com\/c844520\/v844520706\/71a3a\/pZLtq6sleHo.jpg","height":100},{"width":50,"url":"https:\/\/pp.userapi.com\/c844520\/v844520706\/71a3b\/qoFJrYXVFdc.jpg","height":50}], class: class java.lang.String
//key: sound, value: 1, class: class java.lang.String
//key: title, value: Emin Guliev published a post, class: class java.lang.String
//key: to_id, value: 280186075, class: class java.lang.String
//key: group_id, value: posts, class: class java.lang.String
//key: context, value: {"item_id":"1137","owner_id":216143660,"type":"post"}, class: class java.lang.String
    private final int accountId;
    private final long from;
    private final int from_id;
    private final String url;
    private final String body;
    private final long time;
    private final int badge;
    private final String image;
    private final boolean sound;
    private final String title;
    private final int to_id;
    private final String group_id;
    private final int item_id;
    private final int owner_id;
    private final String context_type;

    public NewPostPushMessage(int accountId, RemoteMessage remote) {
        this.accountId = accountId;
        Map<String, String> data = remote.getData();
        from = Long.parseLong(remote.getFrom());
        from_id = Integer.parseInt(data.get("from_id"));
        url = data.get("url");
        body = data.get("body");
        time = Long.parseLong(data.get("time"));
        badge = Integer.parseInt(data.get("badge"));
        image = data.get("image");
        sound = Integer.parseInt(data.get("sound")) == 1;
        title = data.get("title");
        to_id = Integer.parseInt(data.get("to_id"));
        group_id = data.get("group_id");

        PostContext context = new Gson().fromJson(data.get("context"), PostContext.class);
        item_id = context.item_id;
        owner_id = context.owner_id;
        context_type = context.type;
    }

    public void notifyIfNeed(Context context) {
        if (from_id == 0) {
            Logger.wtf("NewPostPushMessage", "from_id is NULL!!!");
            return;
        }

        if (!Settings.get()
                .notifications()
                .isNewPostsNotificationEnabled()) {
            return;
        }

        notifyImpl(context);
    }

    private void notifyImpl(Context context) {
        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Utils.hasOreo()) {
            nManager.createNotificationChannel(AppNotificationChannels.getNewPostChannel(context));
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, AppNotificationChannels.NEW_POST_CHANNEL_ID)
                .setSmallIcon(R.drawable.phoenix_round)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true);

        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Extra.PLACE, PlaceFactory.getPostPreviewPlace(accountId, item_id, from_id));

        intent.setAction(MainActivity.ACTION_OPEN_PLACE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(context, from_id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(contentIntent);
        Notification notification = builder.build();

        configOtherPushNotification(notification);

        nManager.notify(String.valueOf(from_id), NotificationHelper.NOTIFICATION_NEW_POSTS_ID, notification);
    }

    class PostContext {
        @SerializedName("item_id")
        int item_id;

        @SerializedName("owner_id")
        int owner_id;

        @SerializedName("type")
        String type;
    }
}