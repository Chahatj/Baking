package com.chahat.baking.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chahat.baking.Adapter.BakingListAdapter;
import com.chahat.baking.IdlingResource.SimpleIdlingResource;
import com.chahat.baking.Object.BakingObject;
import com.chahat.baking.R;
import com.chahat.baking.RecipeActivity;
import com.chahat.baking.utils.JsonUtils;
import com.chahat.baking.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by chahat on 25/7/17.
 */

public class BakingListFragment extends Fragment implements BakingListAdapter.onClickHandler {

    private static final int LOADER_ID = 1;
    private BakingListAdapter bakingListAdapter;
    private RecyclerView recyclerView;
    private TextView textViewError;

    private IdlingResource mIdlingResource;


    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource(){

        if (mIdlingResource==null){
            mIdlingResource = new SimpleIdlingResource();
        }

        return mIdlingResource;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.baking_list_fragment,container,false);

        getIdlingResource();

        recyclerView = (RecyclerView) view.findViewById(R.id.baking_list_recycler_view);
        textViewError = (TextView) view.findViewById(R.id.textViewError);

        boolean is_phone = getContext().getResources().getBoolean(R.bool.is_phone);

        if (is_phone){
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
        }else{
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),calculateNoOfColumns(getContext()));
            recyclerView.setLayoutManager(gridLayoutManager);
        }

        bakingListAdapter = new BakingListAdapter(getContext(),this);
        recyclerView.setAdapter(bakingListAdapter);

        getActivity().getSupportLoaderManager().initLoader(LOADER_ID,null,bakingLoader);

        return view;
    }

    private static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 180;
        return (int) (dpWidth / scalingFactor);
    }

    @Override
    public void onItemClick(BakingObject bakingObject) {
        Intent intent = new Intent(getActivity(), RecipeActivity.class);
        intent.putExtra("recipeId",bakingObject.getId());
        intent.putExtra("recipeName",bakingObject.getName());
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().getSupportLoaderManager().restartLoader(LOADER_ID,null,bakingLoader);
    }

    private final LoaderManager.LoaderCallbacks<List<BakingObject>> bakingLoader = new LoaderManager.LoaderCallbacks<List<BakingObject>>() {
        @Override
        public Loader<List<BakingObject>> onCreateLoader(int id, Bundle args) {
            return new AsyncTaskLoader<List<BakingObject>>(getContext()) {

                List<BakingObject> mList = null;

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();

                    ((SimpleIdlingResource)getIdlingResource()).setIdleState(false);


                    if(mList!=null){
                        deliverResult(mList);
                    }else {
                        forceLoad();
                    }
                }

                @Override
                public void deliverResult(List<BakingObject> data) {
                    super.deliverResult(data);
                    mList = data;
                }

                @Override
                public List<BakingObject> loadInBackground() {

                    URL url = NetworkUtils.builtURL();
                    try {
                        String response = NetworkUtils.getResponseFromHttpURL(url);
                        return JsonUtils.getAllRecipeName(response);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<List<BakingObject>> loader, List<BakingObject> data) {

            if (data!=null){
                bakingListAdapter.setBakingList(data);
                ((SimpleIdlingResource)getIdlingResource()).setIdleState(true);
            }else {
                showError();
            }
        }

        @Override
        public void onLoaderReset(Loader<List<BakingObject>> loader) {

        }
    };

    private void showError(){
        recyclerView.setVisibility(View.GONE);
        textViewError.setVisibility(View.VISIBLE);
    }
}
