package com.example.mymessengerapp.adapter;

import android.content.Intent;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymessengerapp.ImageViewerActivity;
import com.example.mymessengerapp.R;
import com.example.mymessengerapp.model.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_MEDIA_SENT = 3;
    private static final int VIEW_TYPE_MEDIA_RECEIVED = 4;
    private static final int VIEW_TYPE_AUDIO_SENT = 5;
    private static final int VIEW_TYPE_AUDIO_RECEIVED = 6;

    private List<ChatMessage> chatMessages;
    private String currentUserId;

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
        this.currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        if (chatMessage.getSenderId().equals(currentUserId)) {
            if ("media".equals(chatMessage.getAttachmentType())) {
                return VIEW_TYPE_MEDIA_SENT;
            } else if ("audio".equals(chatMessage.getAttachmentType())) {
                return VIEW_TYPE_AUDIO_SENT;
            } else {
                return VIEW_TYPE_MESSAGE_SENT;
            }
        } else {
            if ("media".equals(chatMessage.getAttachmentType())) {
                return VIEW_TYPE_MEDIA_RECEIVED;
            } else if ("audio".equals(chatMessage.getAttachmentType())) {
                return VIEW_TYPE_AUDIO_RECEIVED;
            } else {
                return VIEW_TYPE_MESSAGE_RECEIVED;
            }
        }
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_MEDIA_SENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_chat_item_sender, parent, false);
                return new MediaSentViewHolder(view);
            case VIEW_TYPE_MEDIA_RECEIVED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_chat_item_receiver, parent, false);
                return new MediaReceivedViewHolder(view);
            case VIEW_TYPE_AUDIO_SENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_sent_item, parent, false);
                return new AudioSentViewHolder(view);
            case VIEW_TYPE_AUDIO_RECEIVED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_received_item, parent, false);
                return new AudioReceivedViewHolder(view);
            case VIEW_TYPE_MESSAGE_SENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_item_sender, parent, false);
                return new ChatViewHolder(view);
            case VIEW_TYPE_MESSAGE_RECEIVED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_item_receiver, parent, false);
                return new ChatViewHolder(view);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                holder.sendMessage.setText(chatMessage.getMessage());
                holder.sendMessageTime.setText(chatMessage.getTime());
                holder.sendMessageTime.setVisibility(View.GONE);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                holder.receiveMessage.setText(chatMessage.getMessage());
                holder.receiveMessageTime.setText(chatMessage.getTime());
                holder.receiveMessageTime.setVisibility(View.GONE);
                break;
            case VIEW_TYPE_MEDIA_SENT:
                ((MediaSentViewHolder)holder).bind(chatMessage);
                break;
            case VIEW_TYPE_MEDIA_RECEIVED:
                ((MediaReceivedViewHolder)holder).bind(chatMessage);
                break;
            case VIEW_TYPE_AUDIO_SENT:
                break;
            case VIEW_TYPE_AUDIO_RECEIVED:
//                loadAudioIntoPlayer(holder.audioPlayer, chatMessage.getAudioUrl());
                break;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.sendMessageTime.getVisibility() == View.GONE)
                    holder.sendMessageTime.setVisibility(View.VISIBLE);
                else if (holder.sendMessageTime.getVisibility() == View.VISIBLE)
                    holder.sendMessageTime.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }
    private void loadMediaIntoImageView(ImageView imageView, String url) {
        Picasso.get().load(url).into(imageView);
    }
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView sendMessage, receiveMessage, sendMessageTime, receiveMessageTime;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            sendMessage = itemView.findViewById(R.id.send_message);
            receiveMessage = itemView.findViewById(R.id.receive_message);
            sendMessageTime = itemView.findViewById(R.id.send_message_time);
            receiveMessageTime = itemView.findViewById(R.id.receive_message_time);
        }
    }
    public class MediaSentViewHolder extends ChatViewHolder {
        FrameLayout SentMediaFrameLayout;
        ImageView SendmediaImageView;
        TextView mediaMessageTime;

        public MediaSentViewHolder(@NonNull View itemView) {
            super(itemView);
            SentMediaFrameLayout = itemView.findViewById(R.id.attachment_frame_sent);
            SendmediaImageView = itemView.findViewById(R.id.attachment_image_send);
            mediaMessageTime = itemView.findViewById(R.id.attachment_message_sent_time);
        }
        public void bind(ChatMessage chatMessage) {
            SentMediaFrameLayout.setVisibility(View.VISIBLE);
            mediaMessageTime.setText(chatMessage.getTime());
            mediaMessageTime.setVisibility(View.GONE);
            Picasso.get().load(chatMessage.getAttachmentUrl()).into(SendmediaImageView);
            SendmediaImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ImageViewerActivity.class);
                    intent.putExtra("imageUrl", chatMessage.getAttachmentUrl());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
    public class VideoSentViewHolder extends ChatViewHolder {
        FrameLayout SentMediaFrameLayout;
        VideoView SendmediaVideoView;
        TextView mediaMessageTime;
        public VideoSentViewHolder(@NonNull View itemView) {
            super(itemView);
            SentMediaFrameLayout = itemView.findViewById(R.id.attachment_frame_sent);
            SendmediaVideoView = itemView.findViewById(R.id.attachment_video_send);
            mediaMessageTime = itemView.findViewById(R.id.attachment_message_sent_time);
        }
        public void bind(ChatMessage chatMessage) {
            SentMediaFrameLayout.setVisibility(View.VISIBLE);
            mediaMessageTime.setText(chatMessage.getTime());
            mediaMessageTime.setVisibility(View.GONE);
            SendmediaVideoView.setVideoPath(chatMessage.getAttachmentUrl());
            SendmediaVideoView.start();
            SendmediaVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    SendmediaVideoView.start();
                }
                });
        }
    }
    public class MediaReceivedViewHolder extends ChatViewHolder {
        FrameLayout MediaFrameLayout;
        TextView mediaMessageTime;
        ImageView mediaImageView;

        public MediaReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            MediaFrameLayout = itemView.findViewById(R.id.attachment_frame_receive);
            mediaMessageTime = itemView.findViewById(R.id.attachment_message_receive_time);
            mediaImageView = itemView.findViewById(R.id.attachment_image_receive);
        }
        public void bind(ChatMessage chatMessage) {
            MediaFrameLayout.setVisibility(View.VISIBLE);
            mediaMessageTime.setText(chatMessage.getTime());
            mediaMessageTime.setVisibility(View.GONE);
            Picasso.get().load(chatMessage.getAttachmentUrl()).into(mediaImageView);
        }
    }
    public class AudioSentViewHolder extends ChatViewHolder {
        Button playAudioButton;
        MediaPlayer mediaPlayer;

        public AudioSentViewHolder(@NonNull View itemView) {
            super(itemView);
            playAudioButton = itemView.findViewById(R.id.play_audio_button);
        }

        public void bind(ChatMessage chatMessage) {
            playAudioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mediaPlayer == null) {
                        mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(chatMessage.getAttachmentUrl());
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = null;
                        } else {
                            mediaPlayer = new MediaPlayer();
                            try {
                                mediaPlayer.setDataSource(chatMessage.getAttachmentUrl());
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
    }

    public class AudioReceivedViewHolder extends ChatViewHolder {
        Button receivedPlayAudioButton;
        MediaPlayer mediaPlayer;

        public AudioReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            receivedPlayAudioButton = itemView.findViewById(R.id.received_play_audio_button);
        }

        public void bind(ChatMessage chatMessage) {
            receivedPlayAudioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mediaPlayer == null) {
                        mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(chatMessage.getAttachmentUrl());
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            mediaPlayer = null;
                        } else {
                            mediaPlayer = new MediaPlayer();
                            try {
                                mediaPlayer.setDataSource(chatMessage.getAttachmentUrl());
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
    }
}