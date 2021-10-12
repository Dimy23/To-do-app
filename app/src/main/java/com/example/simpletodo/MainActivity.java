package com.example.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String KEY_ITEM_TEXT="item_text";
    public static final String KEY_ITEM_POSITION="item_position";
    public static final int EDIT_TEXT_CODE = 20;
   List<String> items;
   Button btnAdd;
   EditText etiItems;
   RecyclerView recycler;
   ItemsAdapter itemsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnAdd = findViewById(R.id.btnAdd);
        etiItems = findViewById(R.id.etiItems);
        recycler = findViewById(R.id.recyler);
        items= new ArrayList<>();
        loadItems();
        //items.add("By Milk");
       // items.add("Go to the gym");
       // items.add("Have fun!");
        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener(){
            @Override
            public void onItemLongClicked(int position) {
            // delete the item from the model
                items.remove(position);
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Items was remove", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };
        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
            Log.d("MainAtivity", "Single click at position"+ position);
                Intent i = new Intent(MainActivity.this,EditActivity.class);
                i.putExtra(KEY_ITEM_TEXT,items.get(position));
                i.putExtra(KEY_ITEM_POSITION,position);
                startActivityForResult(i,EDIT_TEXT_CODE);

            }
        };
         itemsAdapter = new ItemsAdapter(items, onLongClickListener,onClickListener );
        recycler.setAdapter(itemsAdapter);
        recycler.setLayoutManager(new LinearLayoutManager( this));
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItems = etiItems.getText().toString();
                items.add(todoItems);
                itemsAdapter.notifyItemInserted(items.size()-1);
                etiItems.setText("");
                Toast.makeText(getApplicationContext(), "Items was added", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            String Itemtext= data.getStringExtra(KEY_ITEM_TEXT);
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);
            items.set(position,Itemtext);
            itemsAdapter.notifyItemChanged(position);
            saveItems();
            Toast.makeText(getApplicationContext(),"Item update sucessfully!",Toast.LENGTH_SHORT).show();
        }else{
            Log.w("MainActivity","unknown call to MainActivityResult");
        }

    }
    private File getDataFile(){
        return new File(getFilesDir(),"Data.txt");
    }
    // this function will load items by reading every line of the data file
    private void loadItems(){
        try{
            // changement
    items = new ArrayList(FileUtils.readLines(getDataFile(),Charset.defaultCharset()));
        }catch(IOException e){
            e.printStackTrace();
            Log.e("MainActivity" , "Error reading items",e);
            items = new ArrayList();
        }
    }
    private void saveItems(){
        try {
            FileUtils.writeLines(getDataFile(),items);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MainActivity" , "Error writing items",e);
        }
    }
}