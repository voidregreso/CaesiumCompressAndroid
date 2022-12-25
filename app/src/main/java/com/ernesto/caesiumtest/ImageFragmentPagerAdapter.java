package com.ernesto.caesiumtest;

import androidx.fragment.app.*;

import java.util.List;

public class ImageFragmentPagerAdapter extends FragmentPagerAdapter {
    private List<String> imagePaths;

    public ImageFragmentPagerAdapter(FragmentManager fm, List<String> imagePaths) {
        super(fm);
        this.imagePaths = imagePaths;
    }

    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }

    @Override
    public Fragment getItem(int position) {
        return ImageFragment.newInstance(imagePaths.get(position));
    }

    @Override
    public int getCount() {
        return imagePaths.size();
    }

}