package com.jo.barlauncher.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jo.barlauncher.R;
import com.jo.barlauncher.model.App;
import com.jo.barlauncher.model.DataManager;
import com.jo.barlauncher.view.ItemTouchHelperAdapter;
import com.jo.barlauncher.view.OnStartDragListener;
import com.jo.barlauncher.view.SimpleItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.List;

public class ConfigureRowFragment extends Fragment implements ItemTouchHelperAdapter {
    private static final int MAX_APPS = 8;

    private Context context;

    private long row;

    private DataManager dataManager;

    private AppsAdapter appsAdapter;

    private List<App> apps;

    private ItemTouchHelper itemTouchHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();

        dataManager = new DataManager(context);
        apps = new ArrayList<>(10);
        appsAdapter = new AppsAdapter(apps, true, false);
        appsAdapter.setOnStartDragListener(new OnStartDragListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                itemTouchHelper.startDrag(viewHolder);
            }
        });

        row = getActivity().getIntent().getLongExtra(ConfigureRowActivity.EXTRA_ROW, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_configure_row, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(android.R.id.list);
        recyclerView.setAdapter(appsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(this);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        rootView.findViewById(R.id.addApp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, AddAppsActivity.class));
            }
        });

        /*mAddAppLayout = (FrameLayout) rootView.findViewById(R.id.layout_add_app);

        mAddAppTextView = (AutoCompleteTextView) rootView.findViewById(R.id.text_add_app);
        mAddAppTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mAddAppTextView.setText("");

                apps.add((App) mAddAppTextView.getAdapter().getItem(i));
                dataManager.saveAppList(row, apps);
                appsAdapter.notifyDataSetChanged();

                if (apps.size() >= MAX_APPS) {
                    mAddAppLayout.setVisibility(View.GONE);
                }

                NotificationHelper.getInstance(mContext).toggleNotification(false);
            }
        });

        mListView = (DragSortListView) rootView.findViewById(android.R.id.list);
        mListView.setEmptyView(rootView.findViewById(android.R.id.empty));
        mListView.setDropListener(new DragSortListView.DropListener() {

            @Override
            public void drop(int from, int to) {
                apps.add(to, apps.remove(from));
                dataManager.saveAppList(row, apps);
                appsAdapter.notifyDataSetChanged();

                NotificationHelper.getInstance(mContext).toggleNotification(false);

                getActivity().setResult(Activity.RESULT_OK, resultIntent);
            }
        });

        mListView.setRemoveListener(new DragSortListView.RemoveListener() {

            @Override
            public void remove(int which) {
                apps.remove(which);
                dataManager.saveAppList(row, apps);
                appsAdapter.notifyDataSetChanged();

                if (apps.size() < MAX_APPS) {
                    mAddAppLayout.setVisibility(View.VISIBLE);
                }

                NotificationHelper.getInstance(mContext).toggleNotification(false);

                getActivity().setResult(Activity.RESULT_OK, resultIntent);
            }
        });*/

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        new LoadAppsTask().execute();
//        new LoadInstalledAppsTask(mContext).execute();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return false;
    }

    @Override
    public void onItemDismiss(int position) {

    }

    private class LoadAppsTask extends AsyncTask<Void, Void, List<App>> {
        @Override
        protected List<App> doInBackground(Void... params) {
            return dataManager.loadAppList(row);
        }

        @Override
        protected void onPostExecute(List<App> result) {
            apps.clear();
            apps.addAll(result);
            appsAdapter.notifyDataSetChanged();
/*
            mListView.setAdapter(appsAdapter);

            if (apps.size() < MAX_APPS) {
                mAddAppLayout.setVisibility(View.VISIBLE);
            } else {
                mAddAppLayout.setVisibility(View.GONE);
            }
*/
        }
    }

}
