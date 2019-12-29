package com.jo.barlauncher.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jo.barlauncher.R;
import com.jo.barlauncher.model.DataManager;
import com.jo.barlauncher.util.NotificationHelper;
import com.jo.barlauncher.view.ItemTouchHelperAdapter;
import com.jo.barlauncher.view.OnStartDragListener;
import com.jo.barlauncher.view.SimpleItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RowListFragment extends Fragment implements ItemTouchHelperAdapter {
    private static final int MAX_ROWS = 4;

    public static final String KEY_CURRENT_ROW_ID = "current_row_id";

    private DataManager dataManager;

    private RowsAdapter rowsAdapter;

    private List<Long> rows;

    private SharedPreferences preferences;

    private View mRootView;

    private ItemTouchHelper itemTouchHelper;

    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();

        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        dataManager = new DataManager(context);
        rows = new ArrayList<>(4);
        rowsAdapter = new RowsAdapter(rows, dataManager);
        rowsAdapter.setOnItemClickListener(new RowsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(context, ConfigureRowActivity.class);
                intent.putExtra(ConfigureRowActivity.EXTRA_ROW, rows.get(position));

                startActivity(intent);
            }
        });

        rowsAdapter.setOnStartDragListener(new OnStartDragListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                itemTouchHelper.startDrag(viewHolder);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_row_list, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(android.R.id.list);
        recyclerView.setAdapter(rowsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(this);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        /*recyclerView.setEmptyView(rootView.findViewById(android.R.id.empty));
        recyclerView.setOnItemClickListener(new DragSortListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(mActivity, ConfigureRowActivity.class);
                intent.putExtra(ConfigureRowFragment.EXTRA_ROW, rows.get(i));

                startActivity(intent);
            }
        });

        recyclerView.setDropListener(new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                rows.add(to, rows.remove(from));
                rowsAdapter.notifyDataSetChanged();
                dataManager.saveRowList(rows);
                NotificationHelper.getInstance(mActivity).toggleNotification(false);
            }
        });

        recyclerView.setRemoveListener(new DragSortListView.RemoveListener() {
            @Override
            public void remove(final int position) {
                final Long row = rows.remove(position);
                rowsAdapter.notifyDataSetChanged();
                mActivity.invalidateOptionsMenu();
                dataManager.saveRowList(rows);
                NotificationHelper.getInstance(mActivity).toggleNotification(false);

                Snackbar snackbar = Snackbar.make(mRootView, "Row deleted", Snackbar.LENGTH_LONG);
                snackbar.setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rows.add(position, row);
                        rowsAdapter.notifyDataSetChanged();
                        mActivity.invalidateOptionsMenu();
                        dataManager.saveRowList(rows);
                        NotificationHelper.getInstance(mActivity).toggleNotification(false);
                    }
                });

                snackbar.show();
            }
        });*/

        mRootView = rootView;

        rootView.findViewById(R.id.addRow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long currentRowId = preferences.getLong(KEY_CURRENT_ROW_ID, 0);

                rows.add(currentRowId++);
                preferences.edit().putLong(KEY_CURRENT_ROW_ID, currentRowId).apply();

                dataManager.saveRowList(rows);
                rowsAdapter.notifyDataSetChanged();
                NotificationHelper.getInstance(context).toggleNotification(false);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        new LoadRowsTask().execute();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(rows, fromPosition, toPosition);
        rowsAdapter.notifyItemMoved(fromPosition, toPosition);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                dataManager.saveRowList(rows);
                NotificationHelper.getInstance(context).toggleNotification(false);
            }
        });

        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        // TODO show undo option

        rows.remove(position);
        rowsAdapter.notifyItemRemoved(position);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                dataManager.saveRowList(rows);
                NotificationHelper.getInstance(context).toggleNotification(false);
            }
        });
    }

    private class LoadRowsTask extends AsyncTask<Void, Void, List<Long>> {
        @Override
        protected List<Long> doInBackground(Void... params) {
            return dataManager.loadRowList();
        }

        @Override
        protected void onPostExecute(List<Long> result) {
            rows.clear();
            rows.addAll(result);
            rowsAdapter.notifyDataSetChanged();
        }
    }
}