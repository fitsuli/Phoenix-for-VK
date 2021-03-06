package biz.dealnote.messenger.adapter.fave;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import biz.dealnote.messenger.Constants;
import biz.dealnote.messenger.R;
import biz.dealnote.messenger.model.FaveLink;
import biz.dealnote.messenger.util.Utils;
import biz.dealnote.messenger.util.ViewUtils;

public class FaveLinksAdapter extends RecyclerView.Adapter<FaveLinksAdapter.Holder> {

    private final Context context;
    private List<FaveLink> data;
    private RecyclerView recyclerView;
    private ClickListener clickListener;

    public FaveLinksAdapter(List<FaveLink> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NotNull
    @Override
    public Holder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(context).inflate(R.layout.item_fave_link, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        FaveLink link = data.get(position);
        holder.title.setText(link.getTitle());
        holder.description.setText(link.getDescription());

        String photo = Utils.firstNonEmptyString(link.getPhoto100(), link.getPhoto50());

        ViewUtils.displayAvatar(holder.image, null, photo, Constants.PICASSO_TAG);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onLinkClick(holder.getAdapterPosition(), link);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<FaveLink> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(@NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NotNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onLinkClick(int index, FaveLink link);

        void onLinkDelete(int index, FaveLink link);
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        ImageView image;
        TextView title;
        TextView description;

        public Holder(View itemView) {
            super(itemView);
            itemView.setOnCreateContextMenuListener(this);

            image = itemView.findViewById(R.id.link_image);
            title = itemView.findViewById(R.id.link_title);
            description = itemView.findViewById(R.id.link_description);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            int position = recyclerView.getChildAdapterPosition(v);
            FaveLink faveLink = data.get(position);
            menu.setHeaderTitle(faveLink.getTitle());

            menu.add(0, v.getId(), 0, R.string.delete).setOnMenuItemClickListener(item -> {
                if (clickListener != null) {
                    clickListener.onLinkDelete(position, faveLink);
                }
                return true;
            });
        }
    }
}