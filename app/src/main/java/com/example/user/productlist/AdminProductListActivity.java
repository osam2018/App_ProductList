package com.example.user.productlist;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.example.user.helper.ProductListManager;
import com.example.user.helper.listener.OnDownloadImageListener;
import com.example.user.helper.listener.OnGetProductsListener;
import com.example.user.model.Product;

import java.util.ArrayList;

public class AdminProductListActivity extends AppCompatActivity {

    private ArrayList<ProductItemAdmin> dataItems;
    private ListView lvProduct = null;
    private ProductListAdminAdapter productListAdapter = null;
    private ProductListManager productListManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_product_list);

        productListManager = new ProductListManager();

        lvProduct = findViewById(R.id.lvProduct);
        dataItems = new ArrayList<>();

        productListAdapter = new ProductListAdminAdapter(dataItems, getApplicationContext());
        lvProduct.setAdapter(productListAdapter);

        productListManager.getProducts(new OnGetProductsListener() {
            @Override
            public void onSucceed(final ArrayList<Product> products) {

                for (Product p : products
                     ) {
                    ProductItemAdmin productItem = new ProductItemAdmin();
                    productItem.setName(p.getName());
                    productItem.setExist(p.getExist());
                    productItem.setExplanation(p.getExplanation());
                    productItem.setImage(null);

                    dataItems.add(productItem);

                    productListManager.downloadImage(p, p.getImage(), new OnDownloadImageListener() {
                        @Override
                        public void onSucceed(Product p, Uri uri) {
                            for (ProductItemAdmin pia : dataItems
                                    ) {
                                if(pia.getName().equals(p.getName())) {
                                    pia.setImage(uri);
                                    productListAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                        @Override
                        public void onFailed() {

                        }
                    });
                }
            }

            @Override
            public void onFailed() {

            }
        });
    }

}
