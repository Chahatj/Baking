package com.chahat.baking.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chahat.baking.Object.BakingObject;
import com.chahat.baking.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chahat on 25/7/17.
 */

public class BakingListAdapter extends RecyclerView.Adapter<BakingListAdapter.BakingListViewHolder> {

    private List<BakingObject> mBakingList;
    private final onClickHandler mClickHandler;
    private final Context mContext;

    public BakingListAdapter(Context context,onClickHandler clickHandler){
        mContext = context;
        mBakingList = new ArrayList<>();
        mClickHandler = clickHandler;
    }

    public interface onClickHandler{
         void onItemClick(BakingObject bakingObject);
    }

    @Override
    public BakingListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.baking_list_item,parent,false);

        return new BakingListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BakingListViewHolder holder, int position) {

        BakingObject  bakingDetail = mBakingList.get(position);
        holder.textView.setText(bakingDetail.getName());
        if (!bakingDetail.getImage().isEmpty()){
            Picasso.with(mContext).load(bakingDetail.getImage()).fit().into(holder.imageView);
        }else {
            holder.imageView.setVisibility(View.GONE);
        }
    }

    public void setBakingList(List<BakingObject> list){
        mBakingList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mBakingList!=null) return mBakingList.size();
        else return 0;
    }

    public class BakingListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView textView;
        private final ImageView imageView;

        public BakingListViewHolder(View itemView){
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.baking_item_text_view);
            imageView = (ImageView) itemView.findViewById(R.id.recipe_image_view);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            mClickHandler.onItemClick(mBakingList.get(getAdapterPosition()));
        }
    }
}
