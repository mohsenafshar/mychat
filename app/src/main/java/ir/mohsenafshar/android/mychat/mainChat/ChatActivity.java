package ir.mohsenafshar.android.mychat.mainChat;

import android.app.Service;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import ir.mohsenafshar.android.mychat.R;
import ir.mohsenafshar.android.mychat.login.LoginActivity;
import ir.mohsenafshar.android.mychat.pojo.Message;

public class ChatActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String USER_ID = UUID.randomUUID().toString();

    private static final String TAG = "ChatActivity";
    private static final String URI = "http://iran-chat.herokuapp.com/";

    private String username;
    private List<Message> messages;

    private DrawerLayout drawerLayout;
    private EmojiEditText messageInput;
    private RecyclerView recyclerView;
    private ImageView emojiButton;

    private Socket mSocket;
    private ChatAdapter chatAdapter;

    private ChatPresenter chatPresenter;
    private Bitmap bitmap;

    {
        try {
            mSocket = IO.socket(URI);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private final Emitter.Listener onNewMessage = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {

            Log.d(TAG, args[0].toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Message message = new Gson().fromJson(args[0].toString(), Message.class);
                    if (message.getUserId().equals(USER_ID)) {
                        return;
                    }
                    addMessage(message);
                }
            });
        }
    };

    private void addMessage(Message message) {

        /*messageList.append(username + ": " + textMessage + "\n");
        if (messageList.getLayout().getLineTop(messageList.getLineCount()) > messageList.getHeight()) {
            messageList.scrollTo(0, messageList.getLayout().getLineTop(messageList.getLineCount()) - messageList.getHeight());
        }*/

        messages.add(message);
        chatAdapter.notifyItemInserted(messages.size() - 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EmojiManager.install(new IosEmojiProvider());

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        setSupportActionBar(toolbar);

        chatPresenter = new ChatPresenter();

        Uri imageUri = getIntent().getData();
        if(imageUri != null){
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                Log.d(TAG, "onCreate: Start of bitmap set");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        /*if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else if (getActionBar()!= null){
            getActionBar().setDisplayShowHomeEnabled(true);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }*/

        drawerLayout = findViewById(R.id.drawer_layout);
        recyclerView = findViewById(R.id.recycler_view);
        messageInput = findViewById(R.id.input_message);
        emojiButton = findViewById(R.id.emojiButton);

        messages = new ArrayList<>();
        Message[] arrayMessages = new Message[]{
                new Message(username, "message 1", USER_ID),
                new Message(username, "message 2", "USER_ID"),
                new Message(username, "message 3", USER_ID),
                new Message(username, "message 4", "USER_ID")
        };

        Collections.addAll(messages, arrayMessages);

//        messageList.setMovementMethod(new ScrollingMovementMethod());

        if (getIntent().getExtras() != null) {
            username = getIntent().getExtras().getString(LoginActivity.USERNAME);
        }

        mSocket.connect();
        mSocket.on("chat message", onNewMessage);

        chatAdapter = new ChatAdapter(chatPresenter, messages);
        if(bitmap != null) {
            chatAdapter.setUserPicture(bitmap);
        }

        final LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        //linearLayoutManager.setStackFromEnd(false);

        chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                linearLayoutManager.smoothScrollToPosition(recyclerView, null, chatAdapter.getItemCount());
            }
        });

        /*
        * Commit 3
        * */
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(chatAdapter);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);

        //softkeyboardImp();

        /*
        Open or close the soft keyboard easily
        */
        //softKeyboard.openSoftKeyboard();
        //softKeyboard.closeSoftKeyboard();


        /*EmojIconActions emojIcon = new EmojIconActions(this, drawerLayout , messageInput, emojiButton);
        emojIcon.ShowEmojIcon();
        emojIcon.setUseSystemEmoji(false);

        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("Keyboard", "open");
            }

            @Override
            public void onKeyboardClose() {
                Log.e("Keyboard", "close");
            }
        });*/

        final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(drawerLayout).build(messageInput);
        /*emojiPopup.dismiss(); // Dismisses the Popup.
        emojiPopup.isShowing(); // Returns true when Popup is showing.*/

        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiPopup.toggle(); // Toggles visibility of the Popup.
            }
        });

    }

    /*private void softkeyboardImp() {
        ConstraintLayout mainLayout = findViewById(R.id.chat_container); // You must use the layout root
        InputMethodManager im = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);

        *//*
        Instantiate and pass a callback
        *//*
        SoftKeyboard softKeyboard;
        softKeyboard = new SoftKeyboard(mainLayout, im);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
            @Override
            public void onSoftKeyboardHide() {
                Log.d(TAG, "onSoftKeyboardHide: hide");
            }

            @Override
            public void onSoftKeyboardShow() {
                Log.d(TAG, "onSoftKeyboardShow: visible");
            }
        });
    }*/

    public void attemptSend(View view) {
        String textInput = messageInput.getText().toString();
        if (TextUtils.isEmpty(textInput)) {
            return;
        }

        Message message = new Message(username, textInput, USER_ID);
        addMessage(message);
        messageInput.setText("");
        mSocket.emit("chat message", new Gson().toJson(message));
    }

    @Override
    public void onBackPressed() {
        InputMethodManager im = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSocket.close();
    }

    /*@Override
    public boolean onSupportNavigateUp() {
        Toast.makeText(this, "BYE", Toast.LENGTH_SHORT).show();
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.openDrawer(GravityCompat.START);
        return super.onSupportNavigateUp();
    }*/

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
