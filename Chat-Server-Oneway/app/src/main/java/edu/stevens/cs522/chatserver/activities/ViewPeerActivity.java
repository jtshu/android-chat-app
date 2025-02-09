package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.entities.Peer;

public class ViewPeerActivity extends ComponentActivity {

    public static final String PEER_KEY = "peer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_peer);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.view_peer), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Peer peer = getPeer(getIntent());
        if (peer == null) {
            throw new IllegalArgumentException("Expected peer as intent extra");
        }

        TextView viewUserName = findViewById(R.id.view_user_name);
        TextView viewTimestamp = findViewById(R.id.view_timestamp);
        TextView viewLocation = findViewById(R.id.view_location);

        viewUserName.setText(getString(R.string.view_user_name, peer.name));
        viewTimestamp.setText(getString(R.string.view_timestamp, formatTimestamp(peer.timestamp)));
        viewLocation.setText(getString(R.string.view_location, peer.latitude, peer.longitude));
    }

    private static String formatTimestamp(Instant timestamp) {
        LocalDateTime dateTime = timestamp.atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return dateTime.format(formatter);
    }

    private static Peer getPeer(Intent intent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            return intent.getParcelableExtra(PEER_KEY, Peer.class);
        } else {
            return intent.getParcelableExtra(PEER_KEY);
        }
    }

}
