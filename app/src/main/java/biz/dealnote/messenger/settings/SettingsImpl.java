package biz.dealnote.messenger.settings;

import android.content.Context;

import static biz.dealnote.messenger.util.Objects.isNull;

public class SettingsImpl implements ISettings {

    private static volatile SettingsImpl instance;
    private final IRecentChats recentChats;
    private final IDrawerSettings drawerSettings;
    private final IPushSettings pushSettings;
    private final ISecuritySettings securitySettings;
    private final IUISettings iuiSettings;
    private final INotificationSettings notificationSettings;
    private final IMainSettings mainSettings;
    private final IAccountsSettings accountsSettings;
    private final IOtherSettings otherSettings;

    private SettingsImpl(Context app) {
        notificationSettings = new NotificationsPrefs(app);
        recentChats = new RecentChatsSettings(app);
        drawerSettings = new DrawerSettings(app);
        pushSettings = new PushSettings(app);
        securitySettings = new SecuritySettings(app);
        iuiSettings = new UISettings(app);
        mainSettings = new MainSettings(app);
        accountsSettings = new AccountsSettings(app);
        otherSettings = new OtherSettings(app);
    }

    public static SettingsImpl getInstance(Context context) {
        if (isNull(instance)) {
            synchronized (SettingsImpl.class) {
                if (isNull(instance)) {
                    instance = new SettingsImpl(context.getApplicationContext());
                }
            }
        }

        return instance;
    }

    @Override
    public IRecentChats recentChats() {
        return recentChats;
    }

    @Override
    public IDrawerSettings drawerSettings() {
        return drawerSettings;
    }

    @Override
    public IPushSettings pushSettings() {
        return pushSettings;
    }

    @Override
    public ISecuritySettings security() {
        return securitySettings;
    }

    @Override
    public IUISettings ui() {
        return iuiSettings;
    }

    @Override
    public INotificationSettings notifications() {
        return notificationSettings;
    }

    @Override
    public IMainSettings main() {
        return mainSettings;
    }

    @Override
    public IAccountsSettings accounts() {
        return accountsSettings;
    }

    @Override
    public IOtherSettings other() {
        return otherSettings;
    }
}
