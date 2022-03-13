package com.example.android_projectnoteapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android_projectnoteapp.R;
import com.example.android_projectnoteapp.database.NoteDatabase;
import com.example.android_projectnoteapp.entities.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity {

    private EditText inputNoteTitle, inputNoteSubtitle, inputNoteText;
    private TextView textDateTime;
    private View viewSubtitleIndicator;
    private ImageView imageNote;

    private TextView textWebUrl;
    private LinearLayout layoutWebUrl;

    private String selectNoteColor;
    private String selectImagePath;


    private int REQUEST_CODE_STORAGE_PERMISSION =2;
    private int REQUEST_CODE_SELECT_IMAGE =3;

    private AlertDialog dialogAddUrl;
    private AlertDialog dialogDeleteNote;
    //
    private Note alreadyAvailableNote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        inputNoteTitle= findViewById(R.id.inputNoteTitle);
        inputNoteSubtitle= findViewById(R.id.inputNoteSubtitle);
        inputNoteText= findViewById(R.id.inputNote);
        textDateTime = findViewById(R.id.textDateTime);

        viewSubtitleIndicator = findViewById(R.id.viewSubtitleIndicator);
        imageNote = findViewById(R.id.imageNote);

        textWebUrl = findViewById(R.id.textWebUrl);
        layoutWebUrl = findViewById(R.id.layoutWebUrl);

        textDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a"
                        , Locale.getDefault()).format(new Date())
        );

        // CLICK VÀO DẤU TÍCH V ĐỂ SAVE
        ImageView imageSave = findViewById(R.id.imageSave);
        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });

        selectNoteColor = "#333333";
        selectImagePath="";

        //UPDATE
        if(getIntent().getBooleanExtra("isViewOrUpdate", false)){
            alreadyAvailableNote = (Note) getIntent().getSerializableExtra("note");
            setViewUpdateNote();
        }

        //DELETE
        findViewById(R.id.imageRemoveWebUrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textWebUrl.setText(null);
                layoutWebUrl.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.imageRemoveImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageNote.setImageBitmap(null);
                imageNote.setVisibility(View.GONE);
                findViewById(R.id.imageRemoveImage).setVisibility(View.GONE);
                selectImagePath = "";
            }
        });

        initMixedLayout();
        setViewSubtitleIndicatorColor();
    }

    // VIEW AND UPDATE NOTE
    private void setViewUpdateNote(){
        inputNoteTitle.setText(alreadyAvailableNote.getTitle());
        inputNoteSubtitle.setText(alreadyAvailableNote.getSubtitle());
        inputNoteText.setText(alreadyAvailableNote.getNoteText());
        textDateTime.setText(alreadyAvailableNote.getDateTime());

        if(alreadyAvailableNote.getImagePath() != null && !alreadyAvailableNote.getImagePath().trim().isEmpty() ){
            imageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getImagePath()));
            imageNote.setVisibility(View.VISIBLE);
            //show imageREMOVE
            findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
            selectImagePath = alreadyAvailableNote.getImagePath();
        }

        if(alreadyAvailableNote.getWebLink() != null && !alreadyAvailableNote.getWebLink().trim().isEmpty()){
            textWebUrl.setText(alreadyAvailableNote.getWebLink());
            layoutWebUrl.setVisibility(View.VISIBLE);
        }
    }

    //SAVE NOTE
     private void saveNote(){
        // check input
        if(inputNoteTitle.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Note title can't empty", Toast.LENGTH_SHORT).show();
            return;
        }else if(inputNoteSubtitle.getText().toString().trim().isEmpty()
        &&inputNoteText.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Note can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // set các phần từ vào note
        final Note note = new Note();
        note.setTitle(inputNoteTitle.getText().toString());
        note.setSubtitle(inputNoteSubtitle.getText().toString());
        note.setNoteText(inputNoteText.getText().toString());
        note.setDateTime(textDateTime.getText().toString());

        //SET COLOR
         note.setColor(selectNoteColor);

         // setImage path
         note.setImagePath(selectImagePath);

         // SET WEBURL
         if(layoutWebUrl.getVisibility() == View.VISIBLE){
             note.setWebLink((textWebUrl.getText().toString()));
         }

         //SET UPDATE & VIEW
         // if id of new note already available in database it will replace it => update it
         if(alreadyAvailableNote != null){
             note.setId(alreadyAvailableNote.getId());
         }
        // CÔNG VIỆC SAVE
        class SaveNoteTask extends AsyncTask<Void, Void, Void>{
            @Override
            protected Void doInBackground(Void... voids) {
                // SAVE vào database
                NoteDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                //nhận kết quả
                super.onPostExecute(unused);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        new SaveNoteTask().execute();
     }

     // Click or swipe MixedLayout (co the bo)
     private void initMixedLayout(){
        final LinearLayout layoutMixed = findViewById(R.id.layout_mixed);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMixed);
        final  ImageView imageColor1 = layoutMixed.findViewById(R.id.imageColor1);
        final  ImageView imageColor2 = layoutMixed.findViewById(R.id.imageColor2);
        final  ImageView imageColor3 = layoutMixed.findViewById(R.id.imageColor3);
        final  ImageView imageColor4 = layoutMixed.findViewById(R.id.imageColor4);
        final  ImageView imageColor5 = layoutMixed.findViewById(R.id.imageColor5);

        // CHỌN MÀU CHO SUBTITLE
         // COLOR 1
        layoutMixed.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectNoteColor = "#333333";
                imageColor1.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setViewSubtitleIndicatorColor();
            }
        });
        //COLOR 2
         layoutMixed.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 selectNoteColor = "#FDBE3B";
                 imageColor1.setImageResource(0);
                 imageColor2.setImageResource(R.drawable.ic_done);
                 imageColor3.setImageResource(0);
                 imageColor4.setImageResource(0);
                 imageColor5.setImageResource(0);
                 setViewSubtitleIndicatorColor();
             }
         });
         //COLOR 3
         layoutMixed.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 selectNoteColor = "#FF4842";
                 imageColor1.setImageResource(0);
                 imageColor2.setImageResource(0);
                 imageColor3.setImageResource(R.drawable.ic_done);
                 imageColor4.setImageResource(0);
                 imageColor5.setImageResource(0);
                 setViewSubtitleIndicatorColor();
             }
         });
         //COLOR 4
         layoutMixed.findViewById(R.id.viewColor4).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 selectNoteColor = "#3A52FC";
                 imageColor1.setImageResource(0);
                 imageColor2.setImageResource(0);
                 imageColor3.setImageResource(0);
                 imageColor4.setImageResource(R.drawable.ic_done);
                 imageColor5.setImageResource(0);
                 setViewSubtitleIndicatorColor();
             }
         });
         //COLOR 5
         layoutMixed.findViewById(R.id.viewColor5).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 selectNoteColor = "#000000";
                 imageColor1.setImageResource(0);
                 imageColor2.setImageResource(0);
                 imageColor3.setImageResource(0);
                 imageColor4.setImageResource(0);
                 imageColor5.setImageResource(R.drawable.ic_done);
                 setViewSubtitleIndicatorColor();
             }
         });

         // VIEW AND UPDATE NOTE
         if(alreadyAvailableNote != null && alreadyAvailableNote.getColor() != null
         && !alreadyAvailableNote.getColor().trim().isEmpty()){
             switch (alreadyAvailableNote.getColor()){
                 case "#FDBE3B":
                     layoutMixed.findViewById(R.id.viewColor2).performClick();
                     break;
                 case "#FF4842":
                     layoutMixed.findViewById(R.id.viewColor3).performClick();
                     break;
                 case "#3A52FC":
                     layoutMixed.findViewById(R.id.viewColor4).performClick();
                     break;
                 case "#000000":
                     layoutMixed.findViewById(R.id.viewColor5).performClick();
                     break;
             }
         }


         // ADD IMAGE TO NOTES
         layoutMixed.findViewById(R.id.layoutAddImage).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            CreateNoteActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                    , REQUEST_CODE_STORAGE_PERMISSION
                    );
                }else{
                    selectImage();
                }
             }
         });

         //ADD WEB URL LINK
         layoutMixed.findViewById(R.id.layoutAddUrl).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                 showAddUrlDialog();
             }
         });

         //DELETE NOTE
         // visible layoutDEETE if note had already
         if(alreadyAvailableNote != null){
             layoutMixed.findViewById(R.id.layoutDeleteNote).setVisibility(View.VISIBLE);
             layoutMixed.findViewById(R.id.layoutDeleteNote).setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showDeleteNoteDialog();
                 }
             });
         }
     }
     // SHORW DELETE NOTE DIALOG
    private void showDeleteNoteDialog(){
        if(dialogDeleteNote == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_note,(ViewGroup) findViewById(R.id.layoutDeleteNoteContainer)
            );
            builder.setView(view);
            dialogDeleteNote = builder.create();
            if(dialogDeleteNote.getWindow() != null){
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    @SuppressLint("StaticFieldLeak")
                    class DeleteNoteTask extends AsyncTask<Void, Void, Void>{

                        @Override
                        protected Void doInBackground(Void... voids) {
                            NoteDatabase.getDatabase(getApplicationContext()).noteDao()
                                    .deleteNote(alreadyAvailableNote);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void unused) {
                            super.onPostExecute(unused);
                            Intent intent = new Intent();
                            intent.putExtra("isNoteDeleted",true);
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                    }
                    new DeleteNoteTask().execute();
                }
            });
            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogDeleteNote.dismiss();
                }
            });
        }

        dialogDeleteNote.show();
    }

    // CHON MAU CHO PHAN viewSubtitleIndicator
     private void setViewSubtitleIndicatorColor(){
         GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
         gradientDrawable.setColor(Color.parseColor(selectNoteColor));
     }



                            //IMAGE
     // chọn ảnh từ thư mục
     private void selectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }

     //Khi được cho phép truy cập vào thư mục
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            }
        }else{
            Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
        }
    }

    // THUC THI KET QUA
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK){
            if(data !=null){
                Uri selectImageUri = data.getData();
                if(selectImageUri != null){
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageNote.setImageBitmap(bitmap);
                        imageNote.setVisibility(View.VISIBLE);
                        //DELETE
                        findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
                        //select path image
                        selectImagePath = getPathFromUri(selectImageUri);
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }


    private String getPathFromUri(Uri contentUri){
        String filePath;
        Cursor cursor = getContentResolver()
                .query(contentUri, null, null, null, null);
        if(cursor == null){
            filePath = contentUri.getPath();
        }else{
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }


                            // WEB LINK URL
    private void showAddUrlDialog(){
        if(dialogAddUrl==null){
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_add_url, (ViewGroup) findViewById(R.id.layoutAddUrlContainer)
            );
            builder.setView(view);

            dialogAddUrl = builder.create();
            if(dialogAddUrl.getWindow() != null){
                dialogAddUrl.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            final EditText inputUrl= view.findViewById(R.id.inputUrl);
            inputUrl.requestFocus();

            view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(inputUrl.getText().toString().trim().isEmpty()){
                        Toast.makeText(CreateNoteActivity.this, "Enter URL", Toast.LENGTH_SHORT).show();
                    }else if(!Patterns.WEB_URL.matcher(inputUrl.getText().toString()).matches()){
                        Toast.makeText(CreateNoteActivity.this, "Enter valid URL", Toast.LENGTH_SHORT).show();
                    }else{
                        textWebUrl.setText(inputUrl.getText().toString());
                        layoutWebUrl.setVisibility(View.VISIBLE);
                        dialogAddUrl.dismiss();
                    }
                }
            });
            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogAddUrl.dismiss();
                }
            });
        }
        dialogAddUrl.show();
    }

}