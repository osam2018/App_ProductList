package com.example.user.productlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.example.user.helper.listener.OnCheckFavorateListener;
import com.example.user.model.Favorate;
import com.example.user.model.Product;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class ProductListAdapter extends ArrayAdapter<ProductItem> {
    String UID = null;

    private ArrayList<ProductItem> dataSet;
    Context context;
    public Boolean notificationTypeAdd;
    public Boolean notificationTypeSoldOut;
    public Boolean notificationTypeDelete;


    private static class ViewHolder {
        TextView tvName;
        TextView tvExplanation;
        ImageView ivProduct;
        Button btnWatch;
    }

    public void changText()
    {
        notifyDataSetChanged();
        //this.notifyDataSetChanged();
        //super.notifyDataSetChanged();
    }


    public ProductListAdapter(ArrayList<ProductItem> data, Context context, String UID, Boolean notificationTypeAdd, Boolean notificationTypeSldOut, Boolean notificationTypeDelete) {
        super(context, R.layout.product_item, data);
        this.dataSet = data;
        this.context = context;

        this.notificationTypeAdd = notificationTypeAdd;
        this.notificationTypeSoldOut = notificationTypeSldOut;
        this.notificationTypeDelete = notificationTypeDelete;
        this.UID = UID;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ProductItem item = getItem(position);
        ViewHolder viewHolder;
        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.product_item, parent, false);

            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.tvExplanation = (TextView) convertView.findViewById(R.id.tvExplanation);
            viewHolder.ivProduct = (ImageView) convertView.findViewById(R.id.ivProduct);
            viewHolder.btnWatch = (Button) convertView.findViewById(R.id.btnWatch);
            viewHolder.btnWatch.setContentDescription(item.getName());

            new ProductListManager().checkFavorate(UID, viewHolder.btnWatch, new OnCheckFavorateListener() {
                @Override
                public void exist(Button button, Favorate favorate) {
                    String text = "★ My Favorate Product! : Notifacation(";
                    if(favorate.getAdd().equals("true")) {
                        text += "Add";
                        if(favorate.getSoldOut().equals("true") || favorate.getDelete().equals("true")) {
                            text += " & ";
                        }
                    }
                    if(favorate.getSoldOut().equals("true")) {
                        text += "SoldOut";
                        if(favorate.getDelete().equals("true")) {
                            text += " & ";
                        }
                    }
                    if(favorate.getDelete().equals("true")) {
                        text += "Delete";
                    }

                    text += ")";
                    button.setText(text);
                    button.setTextColor(Color.RED);
                }

                @Override
                public void notExist(Button button) {
                    button.setText("Add To Favorates?");
                    button.setTextColor(Color.BLACK);
                }
            });

            viewHolder.btnWatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button button = (Button)v;
                    String name = button.getContentDescription().toString();

                    new ProductListManager().checkFavorate(UID, (Button)v, new OnCheckFavorateListener() {
                        @Override
                        public void exist(Button button, Favorate favorate) {
                            //Remove Database Data
                            String name = button.getContentDescription().toString();
                            new ProductListManager().removeFavorate(UID, name);

                            button.setText("Add To Favorates?");
                            button.setTextColor(Color.BLACK);

                            new PostTask().execute(name + " Removed to Favorates!", "Notification Are Disabled", "/topics/" + UID);
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(name + "Add");
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(name + "SoldOut");
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(name + "Delete");

                        }

                        @Override
                        public void notExist(Button button) {
                            String name = button.getContentDescription().toString();
                            new ProductListManager().addFavorate(UID, name, notificationTypeAdd, notificationTypeSoldOut, notificationTypeDelete);

                            String text = "★ My Favorate Product! : Notifacation(";
                            if(notificationTypeAdd) {
                                text += "Add";
                                if(notificationTypeSoldOut || notificationTypeDelete) {
                                    text += " & ";
                                }
                            }
                            if(notificationTypeSoldOut) {
                                text += "SoldOut";
                                if(notificationTypeDelete) {
                                    text += " & ";
                                }
                            }
                            if(notificationTypeDelete) {
                                text += "Delete";
                            }

                            text += ")";

                            button.setText(text);
                            button.setTextColor(Color.RED);

                            String message = "Added Complete!";
                            if(notificationTypeAdd || notificationTypeSoldOut || notificationTypeDelete) {
                                message = "Notification Are Enabled (";

                                if(notificationTypeAdd) {
                                    message += "Added";
                                    if(notificationTypeSoldOut || notificationTypeDelete) {
                                        message += " & ";
                                    }
                                }
                                if(notificationTypeSoldOut) {
                                    message += "SoldOut";
                                    if(notificationTypeDelete) {
                                        message += " & ";
                                    }
                                }
                                if(notificationTypeDelete) {
                                    message += "Delete";
                                }

                                message += ")";
                            }


                            new PostTask().execute(name + " Added to Favorates!", message, "/topics/" + UID);
                            if(notificationTypeAdd)
                            {
                                FirebaseMessaging.getInstance().subscribeToTopic(name + "Add");
                            }
                            if(notificationTypeSoldOut)
                            {
                                FirebaseMessaging.getInstance().subscribeToTopic(name + "SoldOut");
                            }
                            if(notificationTypeDelete)
                            {
                                FirebaseMessaging.getInstance().subscribeToTopic(name + "Delete");
                            }
                        }
                    });




                }
            });

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
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
                viewHolder.tvName.setText(item.getName() + " (SoldOut)");
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