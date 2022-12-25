package com.ernesto.caesiumtest;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ernesto.caesiumtest.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        XXPermissions.with(this)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {

                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            Toast.makeText(MainActivity.this, "Permiso denegado permanentemente, por favor conceda el permiso de almacenamiento manualmente", Toast.LENGTH_LONG).show();
                            XXPermissions.startPermissionActivity(MainActivity.this, permissions);
                        } else {
                            MainActivity.this.finish();
                        }
                    }
                });

        binding.fab.setOnClickListener(view -> {
            PictureSelector.create(this)
                    .openGallery(SelectMimeType.ofImage())
                    .setImageEngine(PicassoEngine.createPicassoEngine())
                    .forResult(new OnResultCallbackListener<LocalMedia>() {
                        @Override
                        public void onResult(ArrayList<LocalMedia> result) {
                            List<String> picPaths = new ArrayList<>();
                            for(LocalMedia lm : result) picPaths.add(lm.getRealPath());
                            Fragment mMainNavFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
                            if(mMainNavFragment != null) {
                                Fragment fr = mMainNavFragment.getChildFragmentManager().getPrimaryNavigationFragment();
                                if(fr instanceof FirstFragment) {
                                    FirstFragment firstFragment = (FirstFragment) fr;
                                    firstFragment.setImagePaths(picPaths);
                                } else Toast.makeText(MainActivity.this, "Failed! " + fr.getClass().toString(), Toast.LENGTH_LONG).show();
                            } else Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCancel() {
                            Snackbar.make(view, "You did not choose any file", Snackbar.LENGTH_LONG).show();
                                    //.setAction("Action", null).show();
                        }
                    });
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}