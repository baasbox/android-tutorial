package com.baasbox.android.pinbox.users;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.pinbox.R;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by eto on 15/01/14.
 */
class UserListAdapter extends BaseAdapter {
    private final List<BaasUser> users;
    private final LayoutInflater inflater;

    UserListAdapter(Context context, List<BaasUser> users) {
        this.users = users;
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

        }


        return convertView;
    }

    private static class Refresh implements BaasHandler<BaasUser> {
        private final WeakReference<UserListAdapter> adapter;

        Refresh(UserListAdapter adapter) {
            this.adapter = new WeakReference<>(adapter);
        }

        @Override
        public void handle(BaasResult<BaasUser> baasUserBaasResult) {
            UserListAdapter ad = adapter.get();
            if (ad != null) {
                ad.notifyDataSetChanged();
            }
        }
    }

    private final View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            BaasUser user = (BaasUser) v.getTag();
            if (user != null) {
                user.follow(new Refresh(UserListAdapter.this));
            }
        }
    };

    private static class ViewHolder {
        private TextView view;
        private ImageView thumb;
        private Button btn;

    }
}
