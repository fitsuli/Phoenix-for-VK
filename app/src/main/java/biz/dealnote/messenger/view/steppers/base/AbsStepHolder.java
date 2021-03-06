package biz.dealnote.messenger.view.steppers.base;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import biz.dealnote.messenger.R;
import biz.dealnote.messenger.adapter.holder.IdentificableHolder;

public abstract class AbsStepHolder<T extends AbsStepsHost> extends RecyclerView.ViewHolder implements IdentificableHolder {

    private static int nextHolderId;

    public final int index;
    public View counterRoot;
    public TextView counterText;
    public TextView titleText;
    public View line;
    public View contentRoot;
    public ViewGroup content;
    public MaterialButton buttonNext;
    public MaterialButton buttonCancel;
    protected View mContentView;

    public AbsStepHolder(ViewGroup parent, int internalLayoutRes, int stepIndex) {
        super(createVerticalMainHolderView(parent));

        index = stepIndex;
        counterRoot = itemView.findViewById(R.id.counter_root);
        counterText = itemView.findViewById(R.id.counter);
        titleText = itemView.findViewById(R.id.title);
        line = itemView.findViewById(R.id.step_line);
        buttonNext = itemView.findViewById(R.id.buttonNext);
        buttonCancel = itemView.findViewById(R.id.buttonCancel);
        content = itemView.findViewById(R.id.content);
        contentRoot = itemView.findViewById(R.id.content_root);

        mContentView = LayoutInflater.from(itemView.getContext()).inflate(internalLayoutRes, parent, false);
        content.addView(mContentView);

        initInternalView(mContentView);
    }

    private static View createVerticalMainHolderView(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_step, parent, false);
        itemView.setTag(generateHolderId());
        return itemView;
    }

    private static int generateHolderId() {
        nextHolderId++;
        return nextHolderId;
    }

    @Override
    public int getHolderId() {
        return (int) itemView.getTag();
    }

    public abstract void initInternalView(View contentView);

    public final void bindInternalStepViews(T host) {
        bindViews(host);
    }

    protected abstract void bindViews(T host);

    public void setNextButtonAvailable(boolean enable) {
        buttonNext.setEnabled(enable);
    }
}