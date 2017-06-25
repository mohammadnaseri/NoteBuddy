package nl.yoerinijs.nb.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.yoerinijs.nb.R;
import nl.yoerinijs.nb.files.backup.BackupCreator;
import nl.yoerinijs.nb.files.backup.BackupImporter;
import nl.yoerinijs.nb.files.backup.BackupStorageHandler;
import nl.yoerinijs.nb.files.misc.DirectoryReader;
import nl.yoerinijs.nb.files.misc.LocationCentral;
import nl.yoerinijs.nb.files.text.TextfileReader;
import nl.yoerinijs.nb.files.text.TextfileRemover;
import nl.yoerinijs.nb.storage.KeyValueDB;

/**
 * This class displays all notes
 */
public class NotesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String KEY_NOTE = "selected";

    public static final String KEY_NOTE_TITLE = "title";

    private static final String PACKAGE_NAME = LocationCentral.PACKAGE;

    public static final String NOTES_ACTIVITY = LocationCentral.NOTES;

    private static final String LOGIN_ACTIVITY = LocationCentral.LOGIN;

    private static final String CREDITS_ACTIVITY = LocationCentral.CREDITS;

    private static final String EDIT_NOTE_ACTIVITY = LocationCentral.EDIT_NOTE;

    private static final String SETUP_ACTIVITY = LocationCentral.SETUP;

    private static final String WEB_ISSUES = LocationCentral.ISSUES;

    private final Context m_context = this;

    private List<String> m_noteNames;

    private String m_location;

    private String m_password;

    private TextfileReader m_textFileReader;

    private BackupStorageHandler m_backupStorageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        final TextView introText = (TextView) findViewById(R.id.introText);
        final ListView listNotes = (ListView) findViewById(R.id.listNotes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        m_password = getIntent().getStringExtra(LoginActivity.KEY_PASSWORD);
        m_backupStorageHandler = new BackupStorageHandler();
        m_textFileReader = new TextfileReader();

        if(null != getIntent().getStringExtra(MainActivity.KEY_TEXT_TO_SEND)) {
            if(!getIntent().getStringExtra(MainActivity.KEY_TEXT_TO_SEND).isEmpty()) {
                Calendar c = Calendar.getInstance();
                String noteTitle = getString(R.string.title_for_external_note) + " (" + c.get(Calendar.DAY_OF_MONTH) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.YEAR) + ")";
                startActvitiy(EDIT_NOTE_ACTIVITY, true, getIntent().getStringExtra(MainActivity.KEY_TEXT_TO_SEND), noteTitle);
            }
        }

        m_location = getFilesDir().getAbsolutePath();
        try {
            m_noteNames = DirectoryReader.getFileNames(m_location, 0);
            if(null != m_noteNames) {
                if(m_noteNames.size() > 0) {
                    introText.setVisibility(View.GONE);
                }
                listNotes.setAdapter(new StableArrayAdapter(this, android.R.layout.simple_list_item_1, m_noteNames));
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_cannot_read_files) + ".", Toast.LENGTH_SHORT).show();
        }

        listNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedNoteTitle = m_noteNames.get((int) id);
                startActvitiy(EDIT_NOTE_ACTIVITY, true, m_textFileReader.getText(m_location, selectedNoteTitle, m_password, m_context, true), selectedNoteTitle);
            }
        });

        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.addNoteButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActvitiy(EDIT_NOTE_ACTIVITY, true, null, null);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * The navigation menu
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.nav_credits) {
            startActvitiy(CREDITS_ACTIVITY, false, null, null);

        } else if(id == R.id.nav_erase) {
            new AlertDialog.Builder(m_context)
                    .setTitle(getString(R.string.dialog_title_delete_everything))
                    .setMessage(getString(R.string.dialog_question_delete_everything))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            KeyValueDB.clearSharedPreference(m_context);
                            int filesNotDeleted = TextfileRemover.deleteAllFiles(m_location);
                            if(filesNotDeleted <= 0) {
                                Toast.makeText(getApplicationContext(), getString(R.string.success_deleted) + ".", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.error_some_not_deleted) + ": " + filesNotDeleted + ".", Toast.LENGTH_SHORT).show();
                            }
                            startActvitiy(SETUP_ACTIVITY, true, null, null);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {}
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        } else if(id == R.id.nav_lock) {
            startActvitiy(LOGIN_ACTIVITY, true, null, null);

        } else if(id == R.id.nav_backup) {
            final BackupCreator backupCreator = new BackupCreator();
            new AlertDialog.Builder(m_context)
                    .setTitle(getString(R.string.dialog_title_store_encrypted))
                    .setMessage(getString(R.string.dialog_question_store_encrypted))
                    .setPositiveButton(getString(R.string.dialog_answer_encrypt), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            backupCreator.createBackup(m_location, m_password, m_context, false);
                        }
                    })
                    .setNegativeButton(getString(R.string.dialog_answer_decrypt), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            backupCreator.createBackup(m_location, m_password, m_context, true);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        } else if(id == R.id.nav_import) {
            Toast.makeText(getApplicationContext(), getString(R.string.import_info) + ".", Toast.LENGTH_LONG).show();
            BackupImporter backupImporter = new BackupImporter();
            backupImporter.importExternalNotes(m_password, m_context);

        } else if(id == R.id.nav_clear) {
            new AlertDialog.Builder(m_context)
                    .setTitle(getString(R.string.dialog_title_delete_all_notes))
                    .setMessage(getString(R.string.dialog_question_delete_all_notes))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            int filesNotDeleted = TextfileRemover.deleteAllFiles(m_location);
                            if (filesNotDeleted <= 0) {
                                Toast.makeText(getApplicationContext(), getString(R.string.success_deleted) + ".", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.error_some_not_deleted) + ": " + filesNotDeleted + ".", Toast.LENGTH_SHORT).show();
                            }
                            startActvitiy(NOTES_ACTIVITY, true, null, null);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        } else if(id == R.id.nav_feedback) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(WEB_ISSUES));
            startActivity(browserIntent);

        } else if(id == R.id.nav_clear_ext) {
            m_backupStorageHandler.requestWritingPermissions(m_context);
            if(m_backupStorageHandler.isExternalStorageWritable()) {
                new AlertDialog.Builder(m_context)
                        .setTitle(getString(R.string.dialog_title_delete_ext_storage))
                        .setMessage(getString(R.string.dialog_question_delete_ext_storage) + ": " + m_backupStorageHandler.getBackupDirectory() + "?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    if(!m_backupStorageHandler.isStorageDirEmpty(m_context)) {
                                        int filesInStorageDir = m_backupStorageHandler.getNumberOfFilesInStorageDir(m_context);
                                        m_backupStorageHandler.clearStorageDir(m_context);
                                        provideBackupClearedResult(filesInStorageDir);
                                        return;
                                    }
                                    Toast.makeText(getApplicationContext(), getString(R.string.error_nothing_to_delete) + ".", Toast.LENGTH_LONG).show();
                                    return;
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.error_cannot_delete) + ". " +  getString(R.string.error_general) + ".", Toast.LENGTH_LONG).show();
                                }
                                startActvitiy(NOTES_ACTIVITY, true, null, null);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {}
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void provideBackupClearedResult(int numberOfFilesInDir) {
        Toast.makeText(getApplicationContext(), getString(R.string.backup_ext_cleared) + ".", Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), numberOfFilesInDir == 1 ? numberOfFilesInDir + " " + getString(R.string.backup_number_deleted_singular).toLowerCase() + "." :
                numberOfFilesInDir + " " + getString(R.string.backup_number_deleted_plural).toLowerCase() + "." , Toast.LENGTH_LONG).show();
    }

    /**
     * Start a new activity
     * Boolean finish will stop the current activity for security purposes
     * @param activity
     * @param finish
     * @param note
     * @param selectedNoteName
     */
    private void startActvitiy(@NonNull String activity, boolean finish, @Nullable String note, @Nullable String selectedNoteName) {
        Intent intent = new Intent();
        intent.setClassName(m_context, PACKAGE_NAME + "." + activity);

        if(null != note && null != selectedNoteName) {
            intent.putExtra(KEY_NOTE, note);
            intent.putExtra(KEY_NOTE_TITLE, selectedNoteName);
        }

        intent.putExtra(LoginActivity.KEY_PASSWORD, m_password);
        startActivity(intent);

        if(finish) {
            finish();
        }
    }

    /**
     * Stable array adapter for displaying the notes
     */
    private class StableArrayAdapter extends ArrayAdapter<String> {
        Map<String, Integer> map = new HashMap<>();
        StableArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            for(String o : objects) {
                map.put(objects.get(objects.indexOf(o)), objects.indexOf(o));
            }
        }

        @Override
        public long getItemId(int position) {
            return map.get(getItem(position));
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
}