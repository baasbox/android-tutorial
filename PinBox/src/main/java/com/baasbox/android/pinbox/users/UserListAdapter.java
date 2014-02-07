package com.baasbox.android.pinbox.users;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.baasbox.android.BaasException;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.pinbox.R;
import com.baasbox.android.pinbox.service.RefreshService;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by eto on 15/01/14.
 */
class UserListAdapter extends BaseAdapter {
    private final List<BaasUser> users;
    private final LayoutInflater inflater;
    private final Context context;

    UserListAdapter(Context context, List<BaasUser> users) {
        this.users = users;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public BaasUser getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder h;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.user_item, parent, false);
            h = new ViewHolder();
            h.view = (TextView) convertView.findViewById(R.id.tv_username);
            h.btn = (Button) convertView.findViewById(R.id.follow_me);
            h.thumb = (ImageView) convertView.findViewById(R.id.user_thumb);
            h.btn.setOnClickListener(click);
            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }
        BaasUser user = users.get(position);
        if (user != null) {

            h.view.setText(user.getName());
            h.btn.setTag(user);
            if (isFriend(user)) {
                h.btn.setText("Unfollow");
            } else {
                h.btn.setText("Follow");
            }
        }


        return convertView;
    }

    private boolean isFriend(BaasUser user) {
        return user.getScope(BaasUser.Scope.FRIEND) != null;
    }

    private static class Refresh implements BaasHandler<BaasUser> {
        private final WeakReference<UserListAdapter> adapter;
        private final Context context;
        private boolean addUsers;

        Refresh(UserListAdapter adapter, boolean addUsers, Context context) {
            this.context = context.getApplicationContext();
            this.adapter = new WeakReference<>(adapter);
            this.addUsers = addUsers;
        }

        @Override
        public void handle(BaasResult<BaasUser> res) {
            BaasUser user = null;
            try {
                user = res.get();
                UserListAdapter ad = adapter.get();
                if (ad != null) {
                    ad.notifyDataSetChanged();
                }
                if (addUsers) {
                    RefreshService.refreshUser(context, user.getName());
                } else {
                    RefreshService.cleanUpUser(context, user.getName());
                }
            } catch (BaasException e) {
                e.printStackTrace();
            }
        }
    }

    private final View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            BaasUser user = (BaasUser) v.getTag();
            if (user != null) {
                if (isFriend(user)) {
                    user.unfollow(new Refresh(UserListAdapter.this, false, context));
                } else {
                    user.follow(new Refresh(UserListAdapter.this, true, context));
                }
            }
        }
    };

    private static class ViewHolder {
        private TextView view;
        private ImageView thumb;
        private Button btn;

    }
}
