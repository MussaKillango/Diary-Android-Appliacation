package com.example.Diary2.notes;

import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ActionMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.Diary2.MainActivity;
import com.example.Diary2.R;
import com.example.Diary2.adapters.NotesAdapter;
import com.example.Diary2.callbacks.MainActionModeCallback;
import com.example.Diary2.callbacks.NoteEventListener;
import com.example.Diary2.db.NotesDB;
import com.example.Diary2.db.NotesDao;
import com.example.Diary2.login.Register;
import com.example.Diary2.model.Note;
import com.example.Diary2.utils.NoteUtils;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

import static com.example.Diary2.notes.EditNoteActivity.NOTE_EXTRA_Key;

public class Notes extends AppCompatActivity implements NoteEventListener, Drawer.OnDrawerItemClickListener {
    private RecyclerView recyclerView;
    public ArrayList<Note> notes;
    public NotesAdapter adapter;
    private NotesDao dao;
    private MainActionModeCallback actionModeCallback;
    private int chackedCount = 0;
    private FloatingActionButton fab;
    private SharedPreferences search;
    public static final String THEME_Key = "app_theme";
    public static final String APP_PREFERENCES="notepad_settings";
    private int theme;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        search = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        theme = search.getInt(THEME_Key, R.style.AppTheme);
        setTheme(theme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getIntent();

        setupNavigation(savedInstanceState, toolbar);
        // recyclerView
        recyclerView = findViewById(R.id.notes_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // fab Button
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add new note
                onAddNewNote();
            }
        });

        dao = NotesDB.getInstance(this).notesDao();
    }



    public void filter (String text) {
        ArrayList<Note> filterList = new ArrayList<>();

        for (Note item : notes) {
            if (item.getNoteText().toLowerCase().contains(text.toLowerCase())){
                filterList.add(item);
            }
        }

        notes = filterList;
        adapter.notifyDataSetChanged();
    }

    private void setupNavigation(Bundle savedInstanceState, Toolbar toolbar) {

        // Navigation menu items
        List<IDrawerItem> iDrawerItems = new ArrayList<>();
        iDrawerItems.add(new PrimaryDrawerItem()
                .withName("Home").withIcon(R.drawable.ic_home_black_24dp)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        return false;
                    }
                }));
        iDrawerItems.add(new PrimaryDrawerItem().withName("Notes")
                .withIcon(R.drawable.ic_note_black_24dp)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //Reboot the activity : if it dd not work --Notes.this.recreate();
                        TaskStackBuilder.create(Notes.this)
                                .addNextIntent(new Intent(Notes.this, Notes.class))
                                .addNextIntent(getIntent()).startActivities();
                        return false;
                    }
                }));

        iDrawerItems.add(new SwitchDrawerItem()
                .withName("Dark Theme")
                .withChecked(theme == R.style.AppTheme_Dark)
                .withIcon(R.drawable.ic_dark_theme)
                .withOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
                        // change to dark theme and save it to search
                        if (isChecked) {
                            search.edit().putInt(THEME_Key, R.style.AppTheme_Dark).apply();
                        } else {
                            search.edit().putInt(THEME_Key, R.style.AppTheme).apply();
                        }


                        // this lines means we want to close the app and open it again to change theme
                        TaskStackBuilder.create(Notes.this)
                                .addNextIntent(new Intent(Notes.this, Notes.class))
                                .addNextIntent(getIntent()).startActivities();
                    }
                }));

        iDrawerItems.add(new PrimaryDrawerItem()
                .withName("Change Password")
                .withIcon(R.drawable.ic_lock)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        startActivity(new Intent (getApplicationContext(), Register.class));
                        return false;
                    }
                }));

        // Navigation drawer
        new DrawerBuilder()
                .withActivity(this) // activity main
                .withToolbar(toolbar) // toolbar
                .withSavedInstance(savedInstanceState) // saveInstance of activity
                .withDrawerItems(iDrawerItems) // menu items
                .withTranslucentNavigationBar(true)
                .withOnDrawerItemClickListener(this) // listener for menu items click
                .build();

    }

    private void loadNotes() {
        this.notes = new ArrayList<>();
        List<Note> list = dao.getNotes();// get All notes from DataBase
        this.notes.addAll(list);
        this.adapter = new NotesAdapter(this, this.notes);
        // set listener to adapter
        this.adapter.setListener(this);
        this.recyclerView.setAdapter(adapter);
        showEmptyView();
        // add swipe helper to recyclerView

        swipeToDeleteHelper.attachToRecyclerView(recyclerView);
    }

    private void showEmptyView() {
        if (notes.size() == 0) {
            this.recyclerView.setVisibility(View.GONE);
            findViewById(R.id.empty_notes_view).setVisibility(View.VISIBLE);

        } else {
            this.recyclerView.setVisibility(View.VISIBLE);
            findViewById(R.id.empty_notes_view).setVisibility(View.GONE);
        }
    }
    private void onAddNewNote() {
        startActivity(new Intent(this, EditNoteActivity.class));

    }


    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

    @Override
    public void onNoteClick(Note note) {
        // note clicked : edit note
        Intent edit = new Intent(this, EditNoteActivity.class);
        edit.putExtra(NOTE_EXTRA_Key, note.getId());
        startActivity(edit);

    }

    @Override
    public void onNoteLongClick(Note note) {
        // note long clicked : delete , share ..
        note.setChecked(true);
        chackedCount = 1;
        adapter.setMultiCheckMode(true);

        // set new listener to adapter intend off Notes listener to be implemented
        adapter.setListener(new NoteEventListener() {
            @Override
            public void onNoteClick(Note note) {
                note.setChecked(!note.isChecked()); // inverse selected
                if (note.isChecked())
                    chackedCount++;
                else chackedCount--;

                if (chackedCount > 1) {
                    actionModeCallback.changeShareItemVisible(false);
                } else actionModeCallback.changeShareItemVisible(true);

                if (chackedCount == 0) {
                    //  finish multi select mode wen checked count =0
                    actionModeCallback.getAction().finish();
                }

                actionModeCallback.setCount(chackedCount + "/" + notes.size());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNoteLongClick(Note note) {

            }
        });

        actionModeCallback = new MainActionModeCallback() {
            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_delete_notes)
                    onDeleteMultiNotes();
                else if (menuItem.getItemId() == R.id.action_share_note)
                    onShareNote();

                actionMode.finish();
                return false;
            }

        };



        // start action mode
        startActionMode(actionModeCallback);
        // hide fab button
        fab.setVisibility(View.GONE);
        actionModeCallback.setCount(chackedCount + "/" + notes.size());
    }


    private void onShareNote() {
        // Sharing one note

        Note note = adapter.getCheckedNotes().get(0);
        // sharing to social media
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        String notetext = note.getNoteText() + "\n\n Create on : " +
                NoteUtils.dateFromLong(note.getNoteDate()) + "\n  By :" +
                getString(R.string.app_name);
        share.putExtra(Intent.EXTRA_TEXT, notetext);
        startActivity(share);


    }

    private void onDeleteMultiNotes() {
        // delete multi notes

        List<Note> chackedNotes = adapter.getCheckedNotes();
        if (chackedNotes.size() != 0) {
            for (Note note : chackedNotes) {
                dao.deleteNote(note);
            }
            // refresh Notes
            loadNotes();
            Toast.makeText(this, chackedNotes.size() + " Note(s) Delete successfully !", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(this, "No Note(s) selected", Toast.LENGTH_SHORT).show();

        //adapter.setMultiCheckMode(false);
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);

        adapter.setMultiCheckMode(false); // uncheck the notes
        adapter.setListener(this); // set back the old listener
        fab.setVisibility(View.VISIBLE);
    }

    // swipe to right or to left te delete
    private ItemTouchHelper swipeToDeleteHelper = new ItemTouchHelper(
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    // delete note when swipe

                    if (notes != null) {
                        // get swiped note
                        Note swipedNote = notes.get(viewHolder.getAdapterPosition());
                        if (swipedNote != null) {
                            swipeToDelete(swipedNote, viewHolder);

                        }

                    }
                }
            });

    private void swipeToDelete(final Note swipedNote, final RecyclerView.ViewHolder viewHolder) {
        new AlertDialog.Builder(Notes.this)
                .setMessage("Delete Note?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // delete note
                        dao.deleteNote(swipedNote);
                        notes.remove(swipedNote);
                        adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                        showEmptyView();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Undo swipe and restore swipedNote (deleted by swipe)
                        recyclerView.getAdapter().notifyItemChanged(viewHolder.getAdapterPosition());


                    }
                })
                .setCancelable(false)
                .create().show();

    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        return false;
    }


}



