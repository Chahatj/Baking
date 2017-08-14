package com.chahat.baking.ui;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chahat.baking.Adapter.IngredientsAdapter;
import com.chahat.baking.BakingWidgetProvider;
import com.chahat.baking.IdlingResource.SimpleIdlingResource;
import com.chahat.baking.Object.IngredientObject;
import com.chahat.baking.R;
import com.chahat.baking.data.BakingContract;
import com.chahat.baking.utils.JsonUtils;
import com.chahat.baking.utils.NetworkUtils;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by chahat on 26/7/17.
 */

public class RecipeDetailFragment extends Fragment implements View.OnClickListener {

    private String id;
    private String recipeName;
    private boolean isIngredientShown;
    private String description;
    private String videoURL;
    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mExoPlayerView;
    private OnNextPrevClickListner mClickListner;
    private static final int LOADER_ID = 3;
    private IngredientsAdapter ingredientsAdapter;
    private Parcelable mRecyclerState;
    private RecyclerView recyclerView;
    private long playerPosition;
    private boolean mPlayVideoWhenForegrounded=true;
    private TextView textViewError;

    public interface OnNextPrevClickListner{
        void onNextClick(String id);
        void onPrevClick(String id);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mClickListner = (OnNextPrevClickListner) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()
                    + " must implement OnNextPrevClickListener");
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (savedInstanceState!=null){
            id = savedInstanceState.getString("Id");
            description = savedInstanceState.getString("Description");
            videoURL = savedInstanceState.getString("VideoURL");
            isIngredientShown = savedInstanceState.getBoolean("IngredientShown");
            recipeName = savedInstanceState.getString("recipeName");
            if (isIngredientShown){
                mRecyclerState = savedInstanceState.getParcelable("RecyclerViewState");
            }else {
                playerPosition = savedInstanceState.getLong("PlayerPosition");
            }
        }

        View view = LayoutInflater.from(getContext()).inflate(R.layout.recipe_detail_fragment,container,false);

        getIdlingResource();

        recyclerView = (RecyclerView) view.findViewById(R.id.ingredient_recycler_view);
        textViewError = (TextView) view.findViewById(R.id.textViewError);
        mExoPlayerView = (SimpleExoPlayerView) view.findViewById(R.id.playerView);
        ImageView nextImageView = (ImageView) view.findViewById(R.id.next_image_view);
        nextImageView.setOnClickListener(this);
        ImageView prevImageView = (ImageView) view.findViewById(R.id.prev_image_view);
        prevImageView.setOnClickListener(this);

        if (!getContext().getResources().getBoolean(R.bool.is_phone)){
            nextImageView.setVisibility(View.GONE);
            prevImageView.setVisibility(View.GONE);
        }else {
            nextImageView.setVisibility(View.VISIBLE);
            prevImageView.setVisibility(View.VISIBLE);
        }

        if (isIngredientShown){
            view.findViewById(R.id.scrollView).setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);

            ingredientsAdapter = new IngredientsAdapter();
            recyclerView.setAdapter(ingredientsAdapter);

            Bundle bundle = new Bundle();
            bundle.putString("Id",id);
            getActivity().getSupportLoaderManager().initLoader(LOADER_ID,bundle,ingredientLoader);

        }else {
            recyclerView.setVisibility(View.GONE);
            view.findViewById(R.id.scrollView).setVisibility(View.VISIBLE);

            ((TextView)view.findViewById(R.id.description_text_view)).setText(description);

            if (videoURL.isEmpty()){
                mExoPlayerView.setVisibility(View.GONE);
            }else {
                mExoPlayerView.setVisibility(View.VISIBLE);
                initializePlayer();
            }

        }
        return view;
    }

    private void initializePlayer(){
        if (mExoPlayer==null){
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(),
                    new DefaultTrackSelector(), new DefaultLoadControl());

            mExoPlayerView.setPlayer(mExoPlayer);

            mExoPlayer.setPlayWhenReady(true);

            if (videoURL!=null){
                Uri uri = Uri.parse(videoURL);
                MediaSource mediaSource = buildMediaSource(uri);
                mExoPlayer.prepare(mediaSource, true, false);
                mExoPlayer.seekTo(playerPosition);
                mExoPlayer.setPlayWhenReady(mPlayVideoWhenForegrounded);
            }
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new DefaultHttpDataSourceFactory("ua"),
                new DefaultExtractorsFactory(), null, null);
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIngredientShown(boolean ingredientShown) {
        isIngredientShown = ingredientShown;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!isIngredientShown){
            mPlayVideoWhenForegrounded = mExoPlayer.getPlayWhenReady();
            playerPosition = mExoPlayer.getCurrentPosition();
            mExoPlayer.setPlayWhenReady(false);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    private void releasePlayer(){
        if (mExoPlayer!=null){
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("Id",id);
        outState.putString("Description",description);
        outState.putString("VideoURL",videoURL);
        outState.putBoolean("IngredientShown",isIngredientShown);
        outState.putString("recipeName",recipeName);
        if (isIngredientShown){
            mRecyclerState = recyclerView.getLayoutManager().onSaveInstanceState();
        }else {
            outState.putLong("PlayerPosition",mExoPlayer.getCurrentPosition());
        }
        outState.putParcelable("RecyclerViewState",mRecyclerState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.next_image_view:
                mClickListner.onNextClick(id);
                break;
            case R.id.prev_image_view:
                mClickListner.onPrevClick(id);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isIngredientShown){
            Bundle bundle = new Bundle();
            bundle.putString("Id",id);
            getActivity().getSupportLoaderManager().restartLoader(LOADER_ID,bundle,ingredientLoader);
        }else {
            if (mExoPlayer == null) {
                initializePlayer();
            }
        }
    }

    private final LoaderManager.LoaderCallbacks<List<IngredientObject>> ingredientLoader = new LoaderManager.LoaderCallbacks<List<IngredientObject>>() {
        @Override
        public Loader<List<IngredientObject>> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<List<IngredientObject>>(getContext()) {

                List<IngredientObject> mList=null;

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
                public void deliverResult(List<IngredientObject> data) {
                    super.deliverResult(data);
                    mList = data;
                }

                @Override
                public List<IngredientObject> loadInBackground() {

                    URL url = NetworkUtils.builtURL();
                    try {
                        String response = NetworkUtils.getResponseFromHttpURL(url);
                        return JsonUtils.getIngredient(response,args.getString("Id"));
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<List<IngredientObject>> loader, List<IngredientObject> data) {

            if (data!=null){
                ingredientsAdapter.setList(data);
                ((SimpleIdlingResource)getIdlingResource()).setIdleState(true);
                if (mRecyclerState!=null){
                    recyclerView.getLayoutManager().onRestoreInstanceState(mRecyclerState);
                }
                insertDataInDatabase(data);
            }else {
                showError();
            }
        }

        @Override
        public void onLoaderReset(Loader<List<IngredientObject>> loader) {

        }
    };

    private void showError(){
        textViewError.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void insertDataInDatabase(List<IngredientObject> list){

        Cursor cursor = getContext().getContentResolver().query(BakingContract.IngredientEntry.CONTENT_URI,null,
                null,null,null);

        if (cursor!=null){
            if (cursor.getCount()>0){
                getContext().getContentResolver().delete(BakingContract.IngredientEntry.CONTENT_URI,
                        null,null);

                for (int i=0;i<list.size();i++){

                    ContentValues values = new ContentValues();
                    values.put(BakingContract.IngredientEntry.COLUMN_RECIPE_ID,id);
                    values.put(BakingContract.IngredientEntry.COLUMN_RECIPE_NAME,recipeName);
                    values.put(BakingContract.IngredientEntry.COLUMN_INGREDIENT_INGREDIENT,list.get(i).getIngredient());
                    values.put(BakingContract.IngredientEntry.COLUMN_INGREDIENT_MEASURE,list.get(i).getMeasure());
                    values.put(BakingContract.IngredientEntry.COLUMN_INGREDIENT_QUANTITY,list.get(i).getQuantity());

                    getContext().getContentResolver().insert(BakingContract.IngredientEntry.CONTENT_URI,values);
                }

            }else {

                for (int i=0;i<list.size();i++){

                    ContentValues values = new ContentValues();
                    values.put(BakingContract.IngredientEntry.COLUMN_RECIPE_ID,id);
                    values.put(BakingContract.IngredientEntry.COLUMN_RECIPE_NAME,recipeName);
                    values.put(BakingContract.IngredientEntry.COLUMN_INGREDIENT_INGREDIENT,list.get(i).getIngredient());
                    values.put(BakingContract.IngredientEntry.COLUMN_INGREDIENT_MEASURE,list.get(i).getMeasure());
                    values.put(BakingContract.IngredientEntry.COLUMN_INGREDIENT_QUANTITY,list.get(i).getQuantity());

                    getContext().getContentResolver().insert(BakingContract.IngredientEntry.CONTENT_URI,values);
                }
            }
        }
        cursor.close();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getContext());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getActivity(), BakingWidgetProvider.class));
        //Now update all widgets
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds,R.id.widget_ingredient_list);
    }

}
