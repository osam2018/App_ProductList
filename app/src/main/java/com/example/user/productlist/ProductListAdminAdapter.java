package com.example.user.productlist;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.helper.PostTask;
import com.example.user.helper.ProductListManager;
import com.example.user.helper.listener.OnDownloadImageListener;
import com.example.user.model.Product;

import java.util.ArrayList;

public class ProductListAdminAdapter extends ArrayAdapter<ProductItemAdmin> {
        private ArrayList<ProductItemAdmin> dataSet;
        Context context;

        private static class ViewHolder {
            TextView tvName;
            TextView tvExplanation;
            ImageView ivProduct;
            Button btnAdd;
            Button btnSoldOut;
            Button btnDelete;
        }

    public ProductListAdminAdapter(ArrayList<ProductItemAdmin> data, Context context) {
        super(context, R.layout.product_item_admin, data);
        this.dataSet = data;
        this.context = context;
    }

    public void changText()
    {
        notifyDataSetChanged();
        //this.notifyDataSetChanged();
        //super.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ProductItemAdmin item = getItem(position);
        ProductListAdminAdapter.ViewHolder viewHolder;
        final View result;

        if (convertView == null) {
            viewHolder = new ProductListAdminAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.product_item_admin, parent, false);

            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.tvExplanation = (TextView) convertView.findViewById(R.id.tvExplanation);
            viewHolder.ivProduct = (ImageView) convertView.findViewById(R.id.ivProduct);
            viewHolder.btnAdd = (Button) convertView.findViewById(R.id.btnAdd);
            viewHolder.btnSoldOut = (Button) convertView.findViewById(R.id.btnSoldOut);
            viewHolder.btnDelete = (Button) convertView.findViewById(R.id.btnDelete);
            viewHolder.btnAdd.setContentDescription(item.getName());
            viewHolder.btnSoldOut.setContentDescription(item.getName());
            viewHolder.btnDelete.setContentDescription(item.getName());


            viewHolder.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (ProductItemAdmin pia: dataSet
                         ) {
                        if (pia.getName().equals(((Button)v).getContentDescription().toString()))
                        {
                            new PostTask().execute(pia.getName(), "Product Added!", "/topics/" + pia.getName() + "Add");

                            new ProductListManager().AddProduct(pia.getName());
                            pia.setExist("yes");
                            changText();
                        }
                    }
                }
            });

            viewHolder.btnSoldOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (ProductItemAdmin pia: dataSet
                            ) {
                        if (pia.getName().equals(((Button)v).getContentDescription().toString()))
                        {
                            new PostTask().execute(pia.getName(), "Product SoldOut!", "/topics/" + pia.getName() + "SoldOut");

                            new ProductListManager().SoldOutProduct(pia.getName());
                            pia.setExist("no");
                            changText();
                        }
                    }



                }
            });

            viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (ProductItemAdmin pia: dataSet
                            ) {
                        if (pia.getName().equals(((Button)v).getContentDescription().toString()))
                        {
                            new ProductListManager().DeleteProduct(pia.getName());

                            new PostTask().execute(pia.getName(), "Product Removed!", "/topics/" + pia.getName() + "Delete");

                            dataSet.remove(pia);
                            changText();
                        }
                    }
                }
            });

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ProductListAdminAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        if (item.getName() != null) {
            viewHolder.tvName.setText(item.getName());
        }

        if (item.getExist() != null) {
            if (item.getExist().equals(Product.EXIST_YES)) {
                viewHolder.tvName.setTextColor(Color.BLACK);
            } else {
                viewHolder.tvName.setTextColor(Color.RED);
                viewHolder.tvName.setText(item.getName() + " (Sold Out)");
            }
        }

        if (item.getExplanation() != null) {
            viewHolder.tvExplanation.setText(item.getExplanation());
        }

        if (item.getImage() != null) {
            viewHolder.ivProduct.setImageURI(item.getImage());
        }

        return convertView;
    }
}