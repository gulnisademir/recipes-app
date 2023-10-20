package com.gulnisademir.yemektarifleri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    private static final int ID_TARIFEKLE= Menu.FIRST;
    private static final int ID_ÇIKIS= Menu.FIRST+1;


    public boolean onCreateOptionsMenu(Menu menu)
    {
     super.onCreateOptionsMenu(menu);
     menu.add(Menu.NONE,ID_TARIFEKLE,0,"Tarif Ekle");
     menu.add(Menu.NONE,ID_ÇIKIS,1,"Çıkış");
     return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case ID_TARIFEKLE:

                        Intent intent=new Intent(MainActivity.this,InsertUpdate.class);
                        intent.putExtra("event","insert");//insert ve update için aynı sayfa kullanılıyor.
                        //insert ve update'i ayırt etmek amacıyla.
                        startActivity(intent);

                return true;
            case ID_ÇIKIS:
                Intent setIntent = new Intent(Intent.ACTION_MAIN);
                setIntent.addCategory(Intent.CATEGORY_HOME);
                setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(setIntent);
                finish();
                System.exit(0);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    RecyclerView rcView;
    //ImageButton button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // button=findViewById(R.id.newFood);
        rcView = findViewById(R.id.recyclerView);
        //foodlisti çağırmak
        DB dataBase = new DB(MainActivity.this);
        SQLiteDatabase sqLiteDatabase = dataBase.getReadableDatabase();

        RecyclerViewAdapter rcAdapter = new RecyclerViewAdapter(dataBase.listAll(sqLiteDatabase));
        rcView.setAdapter(rcAdapter);
        rcView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

       /* button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,InsertUpdate.class);
                intent.putExtra("event","insert");//insert ve update için aynı sayfa kullanılıyor.
                //insert ve update'i ayırt etmek amacıyla.
                startActivity(intent);
            }
        });*/
    }
}