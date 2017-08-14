package com.chahat.baking.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chahat.baking.Object.IngredientObject;
import com.chahat.baking.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chahat on 26/7/17.
 */

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder> {

    private List<IngredientObject> mList;

    public IngredientsAdapter(){
        mList = new ArrayList<>();
    }

    @Override
    public IngredientsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.ingredient_item,parent,false);

        return new IngredientsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IngredientsViewHolder holder, int position) {

        holder.quantityTextView.setText(mList.get(position).getQuantity());
        holder.measureTextView.setText(mList.get(position).getMeasure());
        holder.ingredientTextView.setText(mList.get(position).getIngredient());
    }

    public void setList(List<IngredientObject> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mList!=null) return mList.size();
        else return 0;
    }

    public class IngredientsViewHolder extends RecyclerView.ViewHolder{

        private final TextView quantityTextView;
        private final TextView measureTextView;
        private final TextView ingredientTextView;

        public IngredientsViewHolder(View itemView) {
            super(itemView);

            quantityTextView = (TextView) itemView.findViewById(R.id.quantity_text_view);
            measureTextView = (TextView) itemView.findViewById(R.id.measure_text_view);
            ingredientTextView = (TextView) itemView.findViewById(R.id.ingredient_text_view);
        }
    }
}
