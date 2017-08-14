package com.chahat.baking.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chahat.baking.Object.RecipeStepObject;
import com.chahat.baking.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chahat on 25/7/17.
 */

public class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.RecipeStepViewHolder> {

    private List<RecipeStepObject> mStepList;
    private OnItemClick mOnClick;
    private final Context mContext;

    public RecipeStepAdapter(Context context){
        mStepList = new ArrayList<>();
        mContext = context;
    }

    public interface OnItemClick{
        void onItemClick(RecipeStepObject recipeStepObject);
    }

    public void setOnClick(OnItemClick itemClick){
        mOnClick = itemClick;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Override
    public RecipeStepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();

        View view = LayoutInflater.from(context).inflate(R.layout.recipe_list_item,parent,false);
        return new RecipeStepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeStepViewHolder holder, int position) {

        RecipeStepObject recipeStepObject = mStepList.get(position);
        holder.textView.setText(recipeStepObject.getShortDescription());
        if (!recipeStepObject.getThumbnailURL().isEmpty()){
            Picasso.with(mContext).load(recipeStepObject.getThumbnailURL()).fit().into(holder.imageView);
        }else {
            holder.imageView.setVisibility(View.GONE);
        }

    }

    public void setStepList(List<RecipeStepObject> mStepList) {
        this.mStepList = mStepList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mStepList!=null) return mStepList.size();
        else return 0;
    }

    public class RecipeStepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView textView;
        private final ImageView imageView;

        public RecipeStepViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.recipe_item_text_view);
            imageView = (ImageView) itemView.findViewById(R.id.recipe_step_image_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClick.onItemClick(mStepList.get(getAdapterPosition()));
        }
    }
}
