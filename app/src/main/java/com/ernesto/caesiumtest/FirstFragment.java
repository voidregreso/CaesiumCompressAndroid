package com.ernesto.caesiumtest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ernesto.caesiumtest.databinding.FragmentFirstBinding;

import java.util.*;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private List<String> imgPaths = new ArrayList<>();
    private ImageFragmentPagerAdapter imageFragmentPagerAdapter = null;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void setImagePaths(List<String> imagePaths) {
        if (imageFragmentPagerAdapter == null) {
            imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getChildFragmentManager(), imagePaths);
            binding.vpPicscroll.setAdapter(imageFragmentPagerAdapter);
        } else {
            imageFragmentPagerAdapter.setImagePaths(imagePaths);
            imageFragmentPagerAdapter.notifyDataSetChanged();
        }
        imgPaths = imagePaths;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonCompress.setOnClickListener(view1 -> {
            if(!imgPaths.isEmpty()) {
                CompProcTask task = new CompProcTask(binding.textviewFirst, imgPaths, FirstFragment.this.getContext());
                task.execute();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}