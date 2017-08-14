package com.chahat.baking.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chahat.baking.Adapter.RecipeStepAdapter;
import com.chahat.baking.IdlingResource.SimpleIdlingResource;
import com.chahat.baking.Object.RecipeStepObject;
import com.chahat.baking.R;
import com.chahat.baking.utils.JsonUtils;
import com.chahat.baking.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by chahat on 25/7/17.
 */

public class RecipeListFragment extends Fragment implements RecipeStepAdapter.OnItemClick {

    private String id;
    private OnListItemClick mCallback;
    private static final int LOADER_ID = 1;
    private RecipeStepAdapter recipeStepAdapter;
    private RecyclerView recyclerView;
    private Parcelable mRecyclerState;
    private TextView textViewError;

    public RecipeListFragment(){

    }

    private IdlingResource mIdlingResource;


    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource(){

        if (mIdlingResource==null){
            mIdlingResource = new SimpleIdlingResource();
        }

        return mIdlingResource;
    }

    public interface OnListItemClick{
        void onListItemClick(RecipeStepObject recipeStepObject);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnListItemClick) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement OnListItemClickListener");
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (savedInstanceState!=null){
            id = savedInstanceState.getString("Id");
            mRecyclerState = savedInstanceState.getParcelable("RecyclerViewState");
        }

        Log.d("RecipeListFragment",id);

        View view = inflater.inflate(R.layout.recipe_list_fragment,container,false);

        getIdlingResource();

        recyclerView = (RecyclerView) view.findViewById(R.id.recipe_list_recycler_view);
        textViewError = (TextView) view.findViewById(R.id.textViewError);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        recipeStepAdapter = new RecipeStepAdapter(getContext());
        recipeStepAdapter.setOnClick(this);

        recyclerView.setAdapter(recipeStepAdapter);
        Bundle bundle = new Bundle();
        bundle.putString("Id",id);
        getActivity().getSupportLoaderManager().initLoader(LOADER_ID,bundle,recipeListLoader);

        return view;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void onItemClick(RecipeStepObject recipeStepObject) {
        mCallback.onListItemClick(recipeStepObject);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("Id",id);
        mRecyclerState = recyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable("RecyclerViewState",mRecyclerState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = new Bundle();
        bundle.putString("Id",id);
        getActivity().getSupportLoaderManager().restartLoader(LOADER_ID,bundle,recipeListLoader);
    }

    private final LoaderManager.LoaderCallbacks<List<RecipeStepObject>> recipeListLoader = new LoaderManager.LoaderCallbacks<List<RecipeStepObject>>() {
        @Override
        public Loader<List<RecipeStepObject>> onCreateLoader(final int id, final Bundle args) {
            return new AsyncTaskLoader<List<RecipeStepObject>>(getContext()) {

                List<RecipeStepObject> mList=null;

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();

                    ((SimpleIdlingResource)getIdlingResource()).setIdleState(false);

                    if (mList!=null){
                        deliverResult(mList);
                    }else {
                        forceLoad();
                    }
                }

                @Override
                public void deliverResult(List<RecipeStepObject> data) {
                    super.deliverResult(data);
                    mList = data;
                }

                @Override
                public List<RecipeStepObject> loadInBackground() {

                    URL url = NetworkUtils.builtURL();
                    try {
                        Log.d("RecipeListFragment","inLoadInBackground");
                        String response = NetworkUtils.getResponseFromHttpURL(url);
                        return JsonUtils.getRecipeSteps(response,args.getString("Id"));
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<List<RecipeStepObject>> loader, List<RecipeStepObject> data) {

            if (data!=null){
                recipeStepAdapter.setStepList(data);
                ((SimpleIdlingResource)getIdlingResource()).setIdleState(true);
                Log.d("RecipeListFragment","inOnLoadFinished");
                if (mRecyclerState!=null){
                    recyclerView.getLayoutManager().onRestoreInstanceState(mRecyclerState);
                }
            }else {
                showError();
            }
        }

        @Override
        public void onLoaderReset(Loader<List<RecipeStepObject>> loader) {

        }

        public void showError(){
            recyclerView.setVisibility(View.GONE);
            textViewError.setVisibility(View.VISIBLE);
        }
    };
}
