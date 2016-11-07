package info.androidhive.navigationdrawer.other;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.models.Notification;

/**
 * Created by raul on 30/10/2016.
 */

public class NotificationsAdapter extends RecyclerView.Adapter <NotificationsAdapter.ViewHolder> {

    // List of data
    private List<Notification> notificationsArrayList;
    private static final String TAG = "NotifAdapterTAG_";

    public NotificationsAdapter(ArrayList<Notification> notificationsArrayList) {
        this.notificationsArrayList = notificationsArrayList;
    }

    @Override
    public NotificationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate parent layout
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // Inflate item
        View termView = inflater.inflate(R.layout.notification_item, parent, false);
        // Return itself
        return new NotificationsAdapter.ViewHolder(termView);
    }

    @Override
    public void onBindViewHolder(NotificationsAdapter.ViewHolder holder, int position) {
        // Get data based on position
        Notification notification = notificationsArrayList.get(position);

        TextView textViewTitle = holder.textViewTitle;
        textViewTitle.setText(notification.getTitle());

        TextView textViewBody = holder.textViewBody;
        textViewBody.setText(notification.getBody());

        TextView textViewDate = holder.textViewDate;
        textViewDate.setText(notification.getDate());

        holder.myNotification = notification;
    }

    @Override
    public int getItemCount() {
        return this.notificationsArrayList.size();
    }

    // View holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView textViewTitle;
        public final TextView textViewBody;
        public final TextView textViewDate;
        // Data
        public Notification myNotification;

        public ViewHolder(View itemView) {
            super(itemView);

            // New instance of components inside the row
            textViewDate  = (TextView) itemView.findViewById(R.id.r_item_date);
            textViewTitle = (TextView) itemView.findViewById(R.id.r_item_title);
            textViewBody  = (TextView) itemView.findViewById(R.id.r_item_body);
            // Click individual items
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: ");
                }
            });
        }
    }

}
