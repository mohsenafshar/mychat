package ir.mohsenafshar.android.mychat.mainChat;

import android.graphics.Bitmap;
import android.util.Log;

class ChatPresenter {

    private static final String TAG = "ChatPresenter";

    public ChatPresenter() {
    }

    public void setUserThumbnail(IChatAdapter iChatAdapter, Bitmap bitmap){
        Log.d(TAG, "setUserThumbnail: ChatPresenter");
        iChatAdapter.setUserPicture(bitmap);
    }
}
