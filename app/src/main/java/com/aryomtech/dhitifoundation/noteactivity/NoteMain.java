package com.aryomtech.dhitifoundation.noteactivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.aryomtech.dhitifoundation.MainFragment;
import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.database.NotesDatabase;
import com.aryomtech.dhitifoundation.entities.Note;
import com.aryomtech.dhitifoundation.listeners.NotesListener;
import com.aryomtech.dhitifoundation.noteactivity.adapters.NotesAdapter;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import me.ibrahimsn.lib.SmoothBottomBar;

import static android.app.Activity.RESULT_OK;

public class NoteMain extends Fragment implements NotesListener {

    View view;
    private Context contextNullSafe;
    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_UPDATE_NOTE = 2;
    public static final int REQUEST_CODE_SHOW_NOTE = 3;
    public static final int REQUEST_CODE_STORAGE_PERMISSION = 4;
    public static final int REQUEST_CODE_SELECT_IMAGE = 5;


    private static final String TAG = "mytag";
    private RecyclerView notesRecyclerView;
    private List<Note> noteList;
    private NotesAdapter notesAdapter;
    private AlertDialog dialogAddURL;
    private ShimmerFrameLayout shimmerFrameLayout;
    SmoothBottomBar smoothBottomBar;
    private int noteClickedPosition = -1;
    Context context;
    public NoteMain(Context context){
        this.context=context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_note_main, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        notesRecyclerView = view.findViewById(R.id.notesRecyclerView);
        LottieAnimationView imageAddNoteMain = view.findViewById(R.id.imageAddNoteMain);
        EditText inputSearch = view.findViewById(R.id.inputSearch);
        ImageView imageAddNote = view.findViewById(R.id.imageAddNote);
        ImageView imageAddImage = view.findViewById(R.id.imageAddImage);
        ImageView imageAddURL = view.findViewById(R.id.imageAddWebLink);
        shimmerFrameLayout = view.findViewById(R.id.shimmerEffect);

        smoothBottomBar=getActivity().findViewById(R.id.bottomBar);
        smoothBottomBar.setItemActiveIndex(0);

        requireActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        imageAddNoteMain.setOnClickListener(v -> startActivityForResult(new Intent(context, CreateNoteActivity.class), REQUEST_CODE_ADD_NOTE));

        notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        noteList = new ArrayList<>();
        notesAdapter = new NotesAdapter(noteList, this);
        notesRecyclerView.setAdapter(notesAdapter);
        getNotes(REQUEST_CODE_SHOW_NOTE,false);

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notesAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (noteList.size() != 0){
                    notesAdapter.searchNotes(s.toString());
                }
            }
        });

        imageAddNote.setOnClickListener(v -> {
            Intent intent = new Intent(context, CreateNoteActivity.class);
            startActivityForResult(intent,REQUEST_CODE_ADD_NOTE);
        });

        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(context, R.color.white));
        window.setNavigationBarColor(ContextCompat.getColor(context, R.color.veryLightGrey));

        imageAddImage.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);

            }
            else{
                selectImage();
            }
        });

        imageAddURL.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            View view1 = LayoutInflater.from(requireContext()).inflate(R.layout.layout_add_url, view.findViewById(R.id.layoutAddUrlContainer),false);
            builder.setView(view1);
            dialogAddURL = builder.create();

            if (dialogAddURL.getWindow() != null){
                dialogAddURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            final EditText urlText = view1.findViewById(R.id.inputURL);

            view1.findViewById(R.id.textAdd).setOnClickListener(v1 -> {
                if (urlText.toString().isEmpty()){
                    Toast.makeText(context, "Add Url", Toast.LENGTH_SHORT).show();
                }
                else if (!Patterns.WEB_URL.matcher(urlText.getText().toString()).matches()){
                    Toast.makeText(context, "Enter a valid URL", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(context, CreateNoteActivity.class);
                    intent.putExtra("inputURL",urlText.getText().toString().trim());
                    intent.putExtra("isQuickURL",true);
                    startActivityForResult(intent,REQUEST_CODE_ADD_NOTE);
                    dialogAddURL.dismiss();
                }
            });

            view1.findViewById(R.id.textCancel).setOnClickListener(v12 -> dialogAddURL.dismiss());

            dialogAddURL.show();
        });
        OnBackPressedCallback callback=new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(((FragmentActivity) getContextNullSafety()).getSupportFragmentManager().findFragmentById(R.id.container) != null) {
                    ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager()
                            .beginTransaction().
                            remove(Objects.requireNonNull(((FragmentActivity) getContextNullSafety()).getSupportFragmentManager().findFragmentById(R.id.container))).commit();
                }
                ((FragmentActivity) getContextNullSafety()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container,new MainFragment())
                        .commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),callback);

        return view;
    }
    /**CALL THIS IF YOU NEED CONTEXT*/
    public Context getContextNullSafety() {
        if (getContext() != null) return getContext();
        if (getActivity() != null) return getActivity();
        if (contextNullSafe != null) return contextNullSafe;
        if (getView() != null && getView().getContext() != null) return getView().getContext();
        if (requireContext() != null) return requireContext();
        if (requireActivity() != null) return requireActivity();
        if (requireView() != null && requireView().getContext() != null)
            return requireView().getContext();

        return null;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contextNullSafe = context;
    }
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(context.getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }

    public void getNotes(final int requestCode, final boolean isNoteDeleted){
        @SuppressLint("StaticFieldLeak")
        class GetNotesTask extends AsyncTask<Void, Void, List<Note>> {
            @Override
            protected List<Note> doInBackground(Void... voids) {
                return  NotesDatabase.getDatabase(context.getApplicationContext()).noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                Log.d(TAG, "onPostExecute: "+ notes.toString());

                if (requestCode == REQUEST_CODE_SHOW_NOTE){
                    noteList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();
                    /*
                    Added code here to stop shimmer because this REQUEST_CODE_SHOE_NOTE will only calls when app starts. If i add this code to somewhere else then
                    shimmer will automatically starts whenever i add or view any note.
                     */
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        shimmerFrameLayout.hideShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        notesRecyclerView.setVisibility(View.VISIBLE);
                    }, 3000);

                }
                else if(requestCode == REQUEST_CODE_ADD_NOTE){
                    noteList.add(0, notes.get(0));
                    notesAdapter.notifyItemInserted(0);
                    notesRecyclerView.smoothScrollToPosition(0);
                }
                else if(requestCode == REQUEST_CODE_UPDATE_NOTE){
                    noteList.remove(noteClickedPosition);

                    if (isNoteDeleted){
                        notesAdapter.notifyItemRemoved(noteClickedPosition);
                    }
                    else{
                        noteList.add(noteClickedPosition, notes.get(noteClickedPosition));
                        notesAdapter.notifyItemChanged(noteClickedPosition);
                    }

                }
            }
        }

        new GetNotesTask().execute();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK ){
            getNotes(REQUEST_CODE_ADD_NOTE,false);
            notesRecyclerView.setAdapter(notesAdapter);
        }
        else if (requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode ==  RESULT_OK){
            if(data != null){
                getNotes(REQUEST_CODE_UPDATE_NOTE,data.getBooleanExtra("isNoteDeleted",false));
            }
            notesRecyclerView.setAdapter(notesAdapter);
        }
        else if(requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK){
            if (data != null){
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null){

                    Intent intent = new Intent(requireContext().getApplicationContext(), CreateNoteActivity.class);
                    intent.putExtra("imageUri",selectedImageUri.toString());
                    intent.putExtra("quickImage",true);
                    startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);

                }
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length>0 ){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }
            else{
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onNoteClicked(Note note, int position) {
        //Hide keyboard
        noteClickedPosition = position;
        Intent intent = new Intent(context.getApplicationContext(), CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("note", note);
        startActivityForResult(intent,REQUEST_CODE_UPDATE_NOTE);

    }
}