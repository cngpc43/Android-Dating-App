package com.example.mymessengerapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.media3.common.Player;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymessengerapp.AudioPlayerActivity;
import com.example.mymessengerapp.ImageViewerActivity;
import com.example.mymessengerapp.R;
import com.example.mymessengerapp.model.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.util.List;
import java.util.concurrent.TimeUnit;


import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_MEDIA_SENT = 3;
    private static final int VIEW_TYPE_MEDIA_RECEIVED = 4;

    private static final int VIEW_TYPE_AUDIO_SENT = 5;
    private static final int VIEW_TYPE_AUDIO_RECEIVED = 6;
    private static final int VIEW_TYPE_VIDEO_SENT = 7;
    private static final int VIEW_TYPE_VIDEO_RECEIVED = 8;

    private List<ChatMessage> chatMessages;
    private String currentUserId;
    private ExoPlayer exoPlayer;


    public ChatAdapter(List<ChatMessage> chatMessages, Context context) {
        this.chatMessages = chatMessages;
        this.currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        exoPlayer = new ExoPlayer.Builder(context).build();

    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        if (chatMessage.getSenderId().equals(currentUserId)) {
            if ("media".equals(chatMessage.getAttachmentType())) {
                return VIEW_TYPE_MEDIA_SENT;
            } else if ("audio".equals(chatMessage.getAttachmentType())) {
                return VIEW_TYPE_AUDIO_SENT;
            } else if ("video".equals(chatMessage.getAttachmentType())) {
                return VIEW_TYPE_VIDEO_SENT;
            } else {
                return VIEW_TYPE_MESSAGE_SENT;
            }
        } else {
            if ("media".equals(chatMessage.getAttachmentType())) {
                return VIEW_TYPE_MEDIA_RECEIVED;
            } else if ("audio".equals(chatMessage.getAttachmentType())) {
                return VIEW_TYPE_AUDIO_RECEIVED;
            } else if ("video".equals(chatMessage.getAttachmentType())) {
                return VIEW_TYPE_VIDEO_RECEIVED;
            } else {
                return VIEW_TYPE_MESSAGE_RECEIVED;
            }
        }
    }
    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
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
            case VIEW_TYPE_VIDEO_SENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_chat_item_sender, parent, false);
                return new VideoSentViewHolder(view);
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
                ((MediaSentViewHolder) holder).bind(chatMessage);
                break;
            case VIEW_TYPE_MEDIA_RECEIVED:
                ((MediaReceivedViewHolder) holder).bind(chatMessage);
                break;
            case VIEW_TYPE_AUDIO_SENT:
                ((AudioSentViewHolder) holder).bind(chatMessage);
                break;
            case VIEW_TYPE_AUDIO_RECEIVED:
//                loadAudioIntoPlayer(holder.audioPlayer, chatMessage.getAudioUrl());
                break;
            case VIEW_TYPE_VIDEO_SENT:
                ((VideoSentViewHolder) holder).bind(chatMessage);
                break;
            case VIEW_TYPE_VIDEO_RECEIVED:
                ((VideoReceivedViewHolder) holder).bind(chatMessage);
                break;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.sendMessageTime != null) {
                    if (holder.sendMessageTime.getVisibility() == View.GONE)
                        holder.sendMessageTime.setVisibility(View.VISIBLE);
                    else if (holder.sendMessageTime.getVisibility() == View.VISIBLE)
                        holder.sendMessageTime.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
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
        ProgressBar imageLoadingProgress;

        public MediaSentViewHolder(@NonNull View itemView) {
            super(itemView);
            SentMediaFrameLayout = itemView.findViewById(R.id.attachment_frame_sent);
            SendmediaImageView = itemView.findViewById(R.id.attachment_image_send);
            mediaMessageTime = itemView.findViewById(R.id.attachment_message_sent_time);
            imageLoadingProgress = itemView.findViewById(R.id.image_loading_progress_send);
        }

        public void bind(ChatMessage chatMessage) {
            SentMediaFrameLayout.setVisibility(View.VISIBLE);
            mediaMessageTime.setText(chatMessage.getTime());
            mediaMessageTime.setVisibility(View.GONE);
            Picasso.get().load(chatMessage.getAttachmentUrl()).into(SendmediaImageView, new Callback() {
                @Override
                public void onSuccess() {
                    imageLoadingProgress.setVisibility(View.GONE);
                    SendmediaImageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(Exception e) {
                    imageLoadingProgress.setVisibility(View.GONE);
                }
            });
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
        PlayerView SendmediaPlayerView;
        TextView mediaMessageTime;
        ExoPlayer exoPlayer;

        public VideoSentViewHolder(@NonNull View itemView) {
            super(itemView);
            SentMediaFrameLayout = itemView.findViewById(R.id.video_frame_sent);
            SendmediaPlayerView = itemView.findViewById(R.id.attachment_video_send);
            mediaMessageTime = itemView.findViewById(R.id.video_message_sent_time);
        }

        public void bind(ChatMessage chatMessage) {
            SentMediaFrameLayout.setVisibility(View.VISIBLE);
            mediaMessageTime.setText(chatMessage.getTime());
            mediaMessageTime.setVisibility(View.GONE);
            if (exoPlayer != null) {
                exoPlayer.release();
                exoPlayer = null;
            }
            exoPlayer = new ExoPlayer.Builder(itemView.getContext()).build();
            SendmediaPlayerView.setPlayer(exoPlayer);
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(chatMessage.getAttachmentUrl()));
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            exoPlayer.setPlayWhenReady(true);
        }
    }

    public class VideoReceivedViewHolder extends ChatViewHolder {
        FrameLayout ReceivedMediaFrameLayout;
        PlayerView ReceivedmediaPlayerView;
        TextView mediaMessageTime;
        ExoPlayer exoPlayer;
        public VideoReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            ReceivedMediaFrameLayout = itemView.findViewById(R.id.attachment_frame_receive);
            ReceivedmediaPlayerView = itemView.findViewById(R.id.attachment_video_receive);
            mediaMessageTime = itemView.findViewById(R.id.attachment_message_receive_time);
        }
        public void bind(ChatMessage chatMessage) {
            ReceivedmediaPlayerView.setVisibility(View.VISIBLE);
            mediaMessageTime.setText(chatMessage.getTime());
            mediaMessageTime.setVisibility(View.GONE);
            if (exoPlayer != null) {
                exoPlayer.release();
                exoPlayer = null;
            }
            exoPlayer = new ExoPlayer.Builder(itemView.getContext()).build();
            ReceivedmediaPlayerView.setPlayer(exoPlayer);
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(chatMessage.getAttachmentUrl()));
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            exoPlayer.setPlayWhenReady(true);
        }
    }
    public class MediaReceivedViewHolder extends ChatViewHolder {
        FrameLayout MediaFrameLayout;
        TextView mediaMessageTime;
        ImageView mediaImageView;
        ProgressBar imageLoadingProgress;

        public MediaReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            MediaFrameLayout = itemView.findViewById(R.id.attachment_frame_receive);
            mediaMessageTime = itemView.findViewById(R.id.attachment_message_receive_time);
            mediaImageView = itemView.findViewById(R.id.attachment_image_receive);
            imageLoadingProgress = itemView.findViewById(R.id.image_loading_progress_receive);
        }
        public void bind(ChatMessage chatMessage) {
            MediaFrameLayout.setVisibility(View.VISIBLE);
            mediaMessageTime.setText(chatMessage.getTime());
            mediaMessageTime.setVisibility(View.GONE);
            Picasso.get().load(chatMessage.getAttachmentUrl()).into(mediaImageView, new Callback() {
                @Override
                public void onSuccess() {
                    imageLoadingProgress.setVisibility(View.GONE);
                    mediaImageView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(Exception e) {
                    imageLoadingProgress.setVisibility(View.GONE);
                }
            });
        }
    }

    public class AudioSentViewHolder extends ChatViewHolder {
        ImageButton playAudioButton;
        TextView audioDuration;
        ExoPlayer exoPlayer;
        Handler handler = new Handler();
        Runnable runnable;

        public AudioSentViewHolder(@NonNull View itemView) {
            super(itemView);
            playAudioButton = itemView.findViewById(R.id.play_audio_icon_sent);
            audioDuration = itemView.findViewById(R.id.audio_duration);
        }

        public void bind(ChatMessage chatMessage) {
            playAudioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (exoPlayer == null) {
                        exoPlayer = new ExoPlayer.Builder(itemView.getContext()).build();
                        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(chatMessage.getAttachmentUrl()));
                        exoPlayer.setMediaItem(mediaItem);
                        exoPlayer.prepare();
                        exoPlayer.setPlayWhenReady(true);
                        playAudioButton.setImageResource(R.drawable.ic_pause);
                        updateAudioDuration();
                        exoPlayer.addListener(new Player.Listener() {
                            @Override
                            public void onPlaybackStateChanged(int playbackState) {
                                if (playbackState == Player.STATE_ENDED) {
                                    playAudioButton.setImageResource(R.drawable.ic_play_icon);
                                    exoPlayer.release();
                                    exoPlayer = null;
                                    handler.removeCallbacks(runnable);
                                }
                            }
                        });
                    } else {
                        if (exoPlayer.isPlaying()) {
                            exoPlayer.pause();
                            playAudioButton.setImageResource(R.drawable.ic_play_icon);
                            handler.removeCallbacks(runnable);
                        } else {
                            exoPlayer.play();
                            playAudioButton.setImageResource(R.drawable.ic_pause);
                            updateAudioDuration();
                        }
                    }
                }
            });
        }

        private void updateAudioDuration() {
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (exoPlayer != null) {
                        long mCurrentPosition = exoPlayer.getCurrentPosition();
                        audioDuration.setText(String.format("%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(mCurrentPosition),
                                TimeUnit.MILLISECONDS.toSeconds(mCurrentPosition) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mCurrentPosition))));
                        handler.postDelayed(this, 1000);
                    }
                }
            };
            handler.postDelayed(runnable, 1000);
        }
    }

    public class AudioReceivedViewHolder extends ChatViewHolder {
        ImageButton receivedPlayAudioButton;
        TextView audioDuration;
        ExoPlayer exoPlayer;
        Handler handler = new Handler();
        Runnable runnable;

        public AudioReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            receivedPlayAudioButton = itemView.findViewById(R.id.received_play_audio_button);
            audioDuration = itemView.findViewById(R.id.received_audio_duration);
        }

        public void bind(ChatMessage chatMessage) {
            receivedPlayAudioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (exoPlayer == null) {
                        exoPlayer = new ExoPlayer.Builder(itemView.getContext()).build();
                        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(chatMessage.getAttachmentUrl()));
                        exoPlayer.setMediaItem(mediaItem);
                        exoPlayer.prepare();
                        exoPlayer.setPlayWhenReady(true);
                        receivedPlayAudioButton.setImageResource(R.drawable.ic_pause);
                        updateAudioDuration();
                        exoPlayer.addListener(new Player.Listener() {
                            @Override
                            public void onPlaybackStateChanged(int playbackState) {
                                if (playbackState == Player.STATE_ENDED) {
                                    receivedPlayAudioButton.setImageResource(R.drawable.ic_play_icon);
                                    exoPlayer.release();
                                    exoPlayer = null;
                                    handler.removeCallbacks(runnable);
                                }
                            }
                        });
                    } else {
                        if (exoPlayer.isPlaying()) {
                            exoPlayer.pause();
                            receivedPlayAudioButton.setImageResource(R.drawable.ic_play_icon);
                            handler.removeCallbacks(runnable);
                        } else {
                            exoPlayer.play();
                            receivedPlayAudioButton.setImageResource(R.drawable.ic_pause);
                            updateAudioDuration();
                        }
                    }
                }
            });
        }

        private void updateAudioDuration() {
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (exoPlayer != null) {
                        long mCurrentPosition = exoPlayer.getCurrentPosition();
                        audioDuration.setText(String.format("%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(mCurrentPosition),
                                TimeUnit.MILLISECONDS.toSeconds(mCurrentPosition) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mCurrentPosition))));
                        handler.postDelayed(this, 1000);
                    }
                }
            };
            handler.postDelayed(runnable, 1000);
        }
    }


}



