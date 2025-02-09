package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.entities.Peer;


public class ViewPeersActivity extends ComponentActivity implements AdapterView.OnItemClickListener {

    public static final String PEERS_KEY = "peers";

    ArrayAdapter<Peer> peersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_peers);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.view_peers), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        List<Peer> peers = getPeers(getIntent());
        if (peers == null) {
            throw new IllegalArgumentException("Missing list of peers!");
        }

        peersAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, peers);
        ListView peerList = findViewById(R.id.peer_list);
        peerList.setAdapter(peersAdapter);

        peerList.setOnItemClickListener(this);
    }

    private static List<Peer> getPeers(Intent intent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            return intent.getParcelableArrayListExtra(PEERS_KEY, Peer.class);
        } else {
            return intent.getParcelableArrayListExtra(PEERS_KEY);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
         * Clicking on a peer brings up details
         */
        Peer peer = peersAdapter.getItem(position);
        Intent intent = new Intent(this, ViewPeerActivity.class);
        intent.putExtra(ViewPeerActivity.PEER_KEY, peer);
        startActivity(intent);
    }
}
