package com.ernesto.caesiumtest;

import android.os.Bundle;
import android.view.*;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.ernesto.caesiumtest.databinding.FragmentImageBinding;

public class ImageFragment extends Fragment {

    private FragmentImageBinding binding;
    private static final String ARG_IMAGE_PATH = "image_path";
    private String imagePath;
    private View view = null;

    public ImageFragment() {
        // Required empty public constructor
    }

    public static ImageFragment newInstance(String imagePath) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_PATH, imagePath);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imagePath = getArguments().getString(ARG_IMAGE_PATH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Load layout and show image
        if(view == null) {
            binding = FragmentImageBinding.inflate(inflater, container, false);
            Glide.with(this).load(imagePath).into(binding.imageView);
            view = binding.getRoot();
        }
        return view;
    }

}