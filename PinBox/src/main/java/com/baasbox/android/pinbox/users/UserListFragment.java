package com.baasbox.android.pinbox.users;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.baasbox.android.*;
import com.baasbox.android.pinbox.R;
import com.baasbox.android.pinbox.common.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eto on 14/01/14.
 */
public class UserListFragment extends BaseFragment {
    private final static String GET_ALL = "GET_ALL";
    private final static String STORED_USERS = "STORED_USERS";

    public static UserListFragment newInstance() {
        return new UserListFragment();
    }


    private RequestToken fetchAll;
    private boolean firstLaunch;
    private ArrayList<BaasUser> users;
    private ListView listView;
    private UserListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            firstLaunch = true;
            users = new ArrayList<>();
        } else {
            users = savedInstanceState.getParcelableArrayList(STORED_USERS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_list, container, false);
        listView = (ListView) v.findViewById(R.id.user_list);
        adapter = new UserListAdapter(getActivity(), users);
        listView.setAdapter(adapter);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (fetchAll == null) {
            fetchAll = BaasUser.fetchAll(Filter.where("user.name <> ?", BaasUser.current().getName()), usersHandler);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STORED_USERS, users);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (fetchAll != null) {
            fetchAll.suspend();
        }
    }


    private void refreshUsers(List<BaasUser> value) {
        fetchAll = null;
        users.clear();
        users.addAll(value);
        adapter.notifyDataSetChanged();
    }

    private final BaasHandler<List<BaasUser>> usersHandler =
            new BaasHandler<List<BaasUser>>() {
                @Override
                public void handle(BaasResult<List<BaasUser>> res) {
                    fetchAll = null;

                    if (res.isSuccess()) {
                        refreshUsers(res.value());
                    }
                }
            };

    void refreshAdapter() {
        adapter.notifyDataSetChanged();
    }

}
