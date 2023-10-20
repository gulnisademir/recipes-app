package com.gulnisademir.yemektarifleri;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class InsertUpdate extends AppCompatActivity {


    EditText foodName;
    EditText foodIngredients;
    EditText foodRecipe;
    ImageView imageButton;
    Button save;
    Button delete;
    boolean imageSelected; //resim varsa kaydet resim yoksa kaydetme
    Bitmap image; // Seçtiğimiz resmi bellekte saklamak için

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_update);

        //yukarıda tanımlananların atamasının yapılması.
        foodName = findViewById(R.id.FoodName);
        foodIngredients = findViewById(R.id.FoodIngredients);
        foodRecipe = findViewById(R.id.FoodRecipe);
        imageButton = findViewById(R.id.ImageButton);
        save = findViewById(R.id.saveButton);
        delete = findViewById(R.id.deleteButton);

        //insert işlemi yapılmayacaksa alanların dolu bir şekilde gelmesi gerekir.
        //fill değilse insert işlemi yapar.
        if (!getIntent().getStringExtra("event").equals("insert")) {
            fill();
            imageSelected=true;
        }
        else {
            imageSelected=false;
        }

//Image butonuna tıklandığında yapılması gereken işlemler

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Dosyalara Gözat."), 1);
            }
        });
        //kaydet butonuna basıldığında
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageSelected == true && foodName.getText().toString().length() > 0 && foodRecipe.getText().toString().length() > 0 && foodIngredients.getText().toString().length() > 0) {
                    insertOrUpdate();
                }
                else{
                        Toast.makeText(InsertUpdate.this, "Boşlukları Doldurunuz!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //delete butonuna basıldığında
        //silmek için herhangi bir yemek olmalı
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getIntent().getStringExtra("event").equals("insert")) {
                    delete();
                }
                else {
                    Toast.makeText(InsertUpdate.this, "İlk olarak yemek eklemelisin!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //resmi seçtikten sonraki işlemler
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //seçilme işlemi gerçekleştiyse
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            try {
                //seçilen veriyi almak
                InputStream stream = getContentResolver().openInputStream(data.getData());//seçtiğimiz resmi getir.
                image = BitmapFactory.decodeStream(stream);
                imageButton.setImageBitmap(image);//resim butonda gözükecek
                imageSelected = true;//resim seçildi
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //update yapılacağı zaman textlerin dolu gelmesi
    public void fill() {
        imageSelected = true;//resim seçilmiş gelsin
        DB dataBase = new DB(InsertUpdate.this);
        SQLiteDatabase sqLiteDatabase = dataBase.getWritableDatabase();//yazma erişimi geldi
        //ıd si olan yemeği getirme
        Food food = dataBase.findById(sqLiteDatabase, Integer.parseInt(getIntent().getStringExtra("foodId")));
        byte[] bytes = food.getImage();
        image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imageButton.setImageBitmap(image);
        foodName.setText(food.getName());
        foodIngredients.setText(food.getIngredients());
        foodRecipe.setText(food.getRecipe());
    }

    public void delete() {
        DB dataBase = new DB(InsertUpdate.this);
        SQLiteDatabase sqLiteDatabase = dataBase.getWritableDatabase();
        dataBase.delete(sqLiteDatabase,Integer.parseInt(getIntent().getStringExtra("foodId")));
        Toast.makeText(InsertUpdate.this, "Silindi!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(InsertUpdate.this, MainActivity.class);
        startActivity(intent);
    }

    //mainactivity de insert ise kayıt değilse update işlemi
    public void insertOrUpdate() {
        DB dataBase = new DB(InsertUpdate.this);
        SQLiteDatabase sqLiteDatabase = dataBase.getWritableDatabase();
        Food food;
        if (getIntent().getStringExtra("event").equals("insert")) {
            food = new Food();// yeni nesne oluşturma
        } else {//ıd aynı
            food = dataBase.findById(sqLiteDatabase, Integer.parseInt(getIntent().getStringExtra("foodId")));
        }
        // ekrana yazılanları food nesnesine set etmek.
        food.setName(foodName.getText().toString());
        food.setIngredients(foodIngredients.getText().toString());
        food.setRecipe(foodRecipe.getText().toString());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        food.setImage(stream.toByteArray());

        if (getIntent().getStringExtra("event").equals("insert")) {
            dataBase.insert(sqLiteDatabase, food);
        } else {
            dataBase.update(sqLiteDatabase, food);
        }

        Toast.makeText(InsertUpdate.this, "Kaydedildi!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(InsertUpdate.this, MainActivity.class);
        startActivity(intent);

    }
}