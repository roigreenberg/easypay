package com.roigreenberg.easypay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roigreenberg.easypay.R;
import com.roigreenberg.easypay.holders.GroupHolder;
import com.roigreenberg.easypay.models.Group;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private static final String USERS_GROUP = "Group";
    private static final String ANONYMOUS = "anonymous ";

    public static String mUsername;
    private RecyclerView mGroupsRecyclerView;
    private DatabaseReference mGroupsRef;
    private FirebaseRecyclerAdapter mGroupsAdapter;
    private static FirebaseDatabase mFirebaseDatabase = null;
    private static DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        if (mFirebaseDatabase == null) {
            Toast.makeText(this, "here", Toast.LENGTH_LONG);
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mDatabaseReference = mFirebaseDatabase.getReference();
        }

        mFirebaseAuth = FirebaseAuth.getInstance();

        mGroupsRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_group);
        mGroupsRecyclerView.setHasFixedSize(false);
        RecyclerView.LayoutManager ownLayoutManager = new LinearLayoutManager
                (this, LinearLayoutManager.VERTICAL, false);
        ownLayoutManager.setAutoMeasureEnabled(true);
        mGroupsRecyclerView.setLayoutManager(ownLayoutManager);

        //DatabaseReference mUserListsRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    mUsername = user.getDisplayName();
                    Log.d("RROI", mUsername);
                    onSignedInInitialize();

                } else {
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
                                            )
                                    )
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    private void onSignedInInitialize() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mDatabaseReference.child(USERS_GROUP);
        mGroupsRef = mDatabaseReference.child(USERS_GROUP);
        
        Toast.makeText(this, "Set group adapter", Toast.LENGTH_SHORT).show();
        mGroupsAdapter = new FirebaseRecyclerAdapter<Group, GroupHolder>(
                Group.class,
                R.layout.group,
                GroupHolder.class,
                mGroupsRef) {
            @Override
            protected void populateViewHolder(final GroupHolder groupHolder, final Group group, final int position) {

//                final String groupID = group.getListID();
//
//                final DatabaseReference groupRef = mDatabaseReference.child(LISTS).child(groupID);
//                Log.d("RROI", "in pop " + groupRef);

                mGroupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final Group groupData = dataSnapshot.getValue(Group.class);
                        String groupName = groupData.getName();
                        if (groupName == null)
                            groupName = "name is missing";
                        groupHolder.setName(groupName);
//                        groupHolder.setNameSize(mTextSize);
//                        groupHolder.setShareOnClick(new ShareOnClickListener(mUserID, groupID, groupName));
//                        groupHolder.setDeleteOnClick(new DeleteOnClickListener(position, mUserID, groupID, groupName));
                        groupHolder.nameField.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent ListIntent = new Intent(MainActivity.this, GroupActivity.class);
//                                ListIntent.putExtra("EXTRA_REF", groupID);
                                startActivity(ListIntent);
                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mGroupsRecyclerView.setAdapter(mGroupsAdapter);
    }

    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
        if (mGroupsAdapter != null)
            mGroupsAdapter.cleanup();
        //detachDatabaseReadListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
