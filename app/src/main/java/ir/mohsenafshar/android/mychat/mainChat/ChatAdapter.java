package ir.mohsenafshar.android.mychat.mainChat;

import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ir.mohsenafshar.android.mychat.R;
import ir.mohsenafshar.android.mychat.pojo.Message;

class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ROW_RIGHT = 1;
    private static final int ROW_LEFT = 2;
    private static final String TAG = "ChatAdapter";

    private List<Message> chatMessages;
    private ChatPresenter chatPresenter;

    public ChatAdapter(ChatPresenter chatPresenter, List<Message> messages) {
        this.chatPresenter = chatPresenter;
        this.chatMessages = messages;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        switch (viewType){
            case ROW_RIGHT :
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_right, parent, false);
                return new RightViewHolder(view);

            case ROW_LEFT :
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_left, parent, false);
                return new LeftViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        String username = chatMessages.get(position).getUsername();
        String message = chatMessages.get(position).getMessage();

        if(username == null){
            username = "null";
        }

        if(holder instanceof RightViewHolder){
            //username = ": " + username;
            //((RightViewHolder) holder).usernameTexView.setText(username);
            ((RightViewHolder) holder).textMessage.setText(message);
        } else if(holder instanceof LeftViewHolder){
            /*username = String.format("%s : %s", username, message);
            ((LeftViewHolder) holder).usernameTexView.setText(username);*/
            ((LeftViewHolder) holder).textMessage.setText("");

            String fullMessage = String.format("%s : %s", username, message);
            Log.d(TAG, "onBindViewHolder: " + fullMessage);
            SpannableStringBuilder sb = new SpannableStringBuilder(fullMessage);
            StyleSpan b = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
            sb.setSpan(b, 0, username.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE); // make first 4 characters Bold

            ((LeftViewHolder) holder).usernameTexView.setText(sb);

            //((LeftViewHolder) holder).textMessage.setText(message);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }


    @Override
    public int getItemViewType(int position) {
        if(chatMessages.get(position).getUserId().equals(ChatActivity.USER_ID)){
            return ROW_RIGHT;
        } else {
            return ROW_LEFT;
        }
    }

    public class RightViewHolder extends RecyclerView.ViewHolder {

        private ImageView userImage;
        private TextView usernameTexView;
        private TextView textMessage;

        public RightViewHolder(View itemView) {
            super(itemView);

            userImage = (ImageView) itemView.findViewById(R.id.userImageView);
            //usernameTexView = itemView.findViewById(R.id.usernameRight);
            textMessage = (TextView) itemView.findViewById(R.id.textViewRight);
        }
    }


    public class LeftViewHolder extends RecyclerView.ViewHolder {

        private ImageView userImage;
        private TextView usernameTexView;
        private TextView textMessage;

        public LeftViewHolder(View itemView) {
            super(itemView);

            userImage = (ImageView) itemView.findViewById(R.id.userImageView);
            usernameTexView = (TextView) itemView.findViewById(R.id.usernameLeft);
            textMessage = (TextView) itemView.findViewById(R.id.textViewLeft);
        }
    }

}
