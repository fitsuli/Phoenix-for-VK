package biz.dealnote.messenger.mvp.view;

import java.util.List;

import biz.dealnote.messenger.model.AnswerVKOfficial;
import biz.dealnote.messenger.mvp.view.base.IAccountDependencyView;
import biz.dealnote.mvp.core.IMvpView;

/**
 * Created by Ruslan Kolbasa on 11.09.2017.
 * phoenix
 */
public interface IAnswerVKOfficialView extends IAccountDependencyView, IMvpView, IErrorView {
    void displayData(List<AnswerVKOfficial> pages);
    void notifyDataSetChanged();
    void notifyDataAdded(int position, int count);

    void showRefreshing(boolean refreshing);
}