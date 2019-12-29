package com.jo.barlauncher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;

import com.jo.barlauncher.legacy.DatabaseMigration;
import com.jo.barlauncher.ui.RowListFragment;
import com.jo.barlauncher.ui.SettingsActivity;
import com.jo.barlauncher.util.NotificationHelper;

public class MainActivity extends AppCompatActivity {
    private static final String DATABASE_NAME = "apps.db";
    /*
        private static final String ROW = "row";

        private DrawerLayout mDrawerLayout;

        private ListView mRowListView;
        private RowList mRowList;
        private RowsAdapter mRowAdapter;
        private Row mRow;

        private Context mContext;

        private NotificationBuilder mBuilder;
        private NotificationManager mNotificationManager;

        private SharedPreferences mPreferences;
        private boolean mEnabled;
    */
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        if(getDatabasePath(DATABASE_NAME).exists()){
            // Migrates the database to shared preferences
            DatabaseMigration.migrate(MainActivity.this);
            deleteDatabase(DATABASE_NAME);
        }

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new RowListFragment()).commit();
        }

//        setContentView(R.layout.activity_main);

//        mContext = this;
//
//        mBuilder = new NotificationBuilder(mContext);
//        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        /*mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEnabled = mPreferences.getBoolean(NotificationBuilder.BAR_LAUNCHER_ENABLED, false);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mRowList = new RowList(mContext);
        mRowAdapter = new RowsAdapter(mContext, mRowList);

        mRowListView = (ListView) findViewById(R.id.row_list);
        mRowListView.setAdapter(mRowAdapter);
        mRowListView.setOnItemClickListener(mOnItemClickListener);
        mRowListView.setOnItemLongClickListener(mOnItemLongClickListener);*/

//		ActionBar actionBar = getActionBar();
//		actionBar.setDisplayHomeAsUpEnabled(true);
//		actionBar.setHomeButtonEnabled(true);

//        mRowList = new RowList(mContext);
//
//        if (mRowList.isEmpty()) {
//            mRowList.add(new Row(0, "Apps", 0));
//            mRowAdapter.notifyDataSetChanged();
//
//            mPreferences.edit().putLong(NotificationBuilder.KEY_SELECTED_ROW_ID, mRowList.get(0).getId()).apply();
//        }

		/*mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close)
        {
			@Override
			public void onDrawerOpened(View view)
			{
				invalidateOptionsMenu();
			}

			@Override
			public void onDrawerClosed(View view)
			{
				invalidateOptionsMenu();
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);*/
//
//        if (savedInstanceState == null) {
//            selectItem(0);
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        SwitchCompat switchCompat = (SwitchCompat) menu.findItem(R.id.action_toggle).getActionView();
        switchCompat.setChecked(sharedPreferences.getBoolean(Settings.BAR_LAUNCHER_ENABLED, false));

        switchCompat.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean(Settings.BAR_LAUNCHER_ENABLED, isChecked).apply();

                NotificationHelper.getInstance(MainActivity.this).toggleNotification(false);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            default:
                return false;
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_list, menu);

        MenuItem menuItemToggle = menu.findItem(R.id.action_toggle);

        Switch toggle = (Switch) menuItemToggle.getActionView();
        toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean checked) {
                if (checked) {
                    mNotificationManager.notify(0, mBuilder.build());
                    mEnabled = true;
                } else {
                    mEnabled = false;
                    mNotificationManager.cancel(0);
                }

                mPreferences.edit().putBoolean(NotificationBuilder.BAR_LAUNCHER_ENABLED, mEnabled).apply();
            }
        });

        if (mEnabled) {
            toggle.setChecked(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: {
                startActivity(new Intent(mContext, SettingsActivity.class));

                return true;
            }
            case R.id.action_add: {
                AddRowDialog dialog = new AddRowDialog();
                dialog.show(getFragmentManager(), null);

                mDrawerLayout.openDrawer(mRowListView);

                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mRowList.open();

        if (mEnabled) {
            mNotificationManager.notify(0, mBuilder.build());
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        mRowList.close();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mRow != null) {
            outState.putParcelable(ROW, mRow);
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey(ROW)) {
            mRow = savedInstanceState.getParcelable(ROW);
        }
    }

    @Override
    public void updateNotification() {
        if (mEnabled) {
            mNotificationManager.notify(0, mBuilder.build());
        }
    }

    private final ListView.OnItemClickListener mOnItemClickListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> av, View view, int position, long id) {
            selectItem(position);
        }
    };

    private final ListView.OnItemLongClickListener mOnItemLongClickListener = new ListView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> av, View view, int position, long id) {
            mRow = mRowList.get(position);

            RowActionsDialog dialog = new RowActionsDialog();

            if (mRowList.size() <= 1) {
                Bundle arguments = new Bundle();
                arguments.putBoolean(RowActionsDialog.KEY_LIST_ONE_ROW, true);

                dialog.setArguments(arguments);
            }

            dialog.show(getFragmentManager(), null);

            return true;
        }
    };

    private void selectItem(int position) {
        mRow = mRowList.get(position);

        Bundle arguments = new Bundle();
        arguments.putLong(AppListFragment.EXTRA_ROW_ID, mRow.getId());

        AppListFragment appListFragment = new AppListFragment();
        appListFragment.setArguments(arguments);

        getFragmentManager().beginTransaction().replace(R.id.container_app_list, appListFragment).commit();

        mRowListView.setItemChecked(position, true);

        mDrawerLayout.closeDrawer(mRowListView);
    }

    public void addRow(String name) {
        mRow = new Row(0, name, 0);
        mRowList.add(mRow);
        mRowAdapter.notifyDataSetChanged();

        invalidateOptionsMenu();
        updateNotification();
    }

    public void renameRow(String name) {
        mRow.setName(name);
        mRowList.set(mRow.getPosition(), mRow);
        mRowAdapter.notifyDataSetChanged();

        updateNotification();
    }

    public void deleteRow() {
        mRowList.remove(mRow.getPosition());
        mRowAdapter.notifyDataSetChanged();

        if (mPreferences.contains(NotificationBuilder.KEY_SELECTED_ROW_ID)) {
            long rowId = mPreferences.getLong(NotificationBuilder.KEY_SELECTED_ROW_ID, 0);

            if (rowId == mRow.getId()) {
                mPreferences.edit().putLong(NotificationBuilder.KEY_SELECTED_ROW_ID, mRowList.get(0).getId()).apply();
            }
        }

        if (mRowListView.getChildAt(mRow.getPosition()).isActivated()) {
            selectItem(0);
        }

        invalidateOptionsMenu();
        updateNotification();
    }

    public void onRenameRow() {
        Bundle arguments = new Bundle();
        arguments.putBoolean(AddRowDialog.KEY_RENAME, true);
        arguments.putString(AddRowDialog.KEY_NAME, mRow.getName());

        AddRowDialog dialog = new AddRowDialog();
        dialog.setArguments(arguments);
        dialog.show(getFragmentManager(), null);
    }

    public void onDeleteRow() {
        Bundle arguments = new Bundle();
        arguments.putString(DeleteConfirmationDialog.KEY_NAME, mRow.getName());

        DeleteConfirmationDialog dialog = new DeleteConfirmationDialog();
        dialog.setArguments(arguments);
        dialog.show(getFragmentManager(), null);
    }*/
}
