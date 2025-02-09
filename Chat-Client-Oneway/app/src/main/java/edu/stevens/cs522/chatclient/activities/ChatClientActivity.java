package edu.stevens.cs522.chatclient.activities;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.StringWriter;
import java.time.Instant;

import edu.stevens.cs522.base.Datagram;
import edu.stevens.cs522.base.DatagramConnectionFactory;
import edu.stevens.cs522.base.IDatagramConnection;
import edu.stevens.cs522.chatclient.R;
import edu.stevens.cs522.chatclient.location.CurrentLocation;

public class ChatClientActivity extends ComponentActivity implements OnClickListener {

	final static private String TAG = ChatClientActivity.class.getCanonicalName();

	public final static String SENDER_NAME = "name";

	public final static String CHATROOM = "room";

	public final static String MESSAGE_TEXT = "text";

	public final static String TIMESTAMP = "timestamp";

	public final static String LATITUDE = "latitude";

	public final static String LONGITUDE = "longitude";

	/*
	 * Socket used for sending
	 */
//  private DatagramSocket clientSocket;
    private IDatagramConnection clientConnection;

	/*
	 * Widgets for dest address, message text, send button.
	 */
	private EditText destinationAddr;

	private EditText chatName;

	private EditText messageText;

	/*
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.chat_client);

		ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.chat_client), (v, insets) -> {
			Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
			return insets;
		});

		/*
		 * Let's be clear, this is a HACK to allow you to do network communication on the chat_client thread.
		 * This WILL cause an ANR, and is only provided to simplify the pedagogy.  We will see how to do
		 * this right in a future assignment (using a Service managing background threads).
		 */
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); 
		StrictMode.setThreadPolicy(policy);

		destinationAddr = findViewById(R.id.destination_addr);
		chatName = findViewById(R.id.chat_name);
		messageText = findViewById(R.id.message_text);
		Button sendButton = findViewById(R.id.send_button);

		sendButton.setOnClickListener(this);

		try {
			int port = getResources().getInteger(R.integer.app_port);

			DatagramConnectionFactory factory = new DatagramConnectionFactory();
            clientConnection = factory.getUdpConnection(port);

			//debug code
			if (clientConnection == null) {
				Log.e(TAG, "Server connection is null, cannot send on port " + port);
			} else {
				Log.d(TAG, "Server is sending on port " + port);
			}
		} catch (IOException e) {

		    throw new IllegalStateException("Cannot open client connection", e);

		}

	}

	/*
	 * Callback for the SEND button.
	 */
	@Override
	public void onClick(View v) {
		try {

			/*
			 * On the emulator, which does not support WIFI stack, we'll send to
			 * (an AVD alias for) the host loopback interface, with the server
			 * port on the host redirected to the server port on the server AVD.
			 */

			String destAddr = null;

			// int destPort = getResources().getInteger(R.integer.app_port);

			String chatNameText = null;

			String chatRoom = getString(R.string.default_chatroom);

			String text = null;

			Instant timestamp = Instant.now();

			CurrentLocation location = CurrentLocation.getLocation(this);

			destAddr = destinationAddr.getText().toString();
			chatNameText = chatName.getText().toString();
			text = messageText.getText().toString();

			if (destAddr.isEmpty()) {
				return;
			}

			if (chatNameText.isEmpty()) {
				return;
			}

			if (text.isEmpty()) {
				return;
			}

			StringWriter output = new StringWriter();
			JsonWriter wr = new JsonWriter(output);
			wr.beginObject();
			wr.name(SENDER_NAME).value(chatNameText);
			wr.name(CHATROOM).value(chatRoom);
			wr.name(MESSAGE_TEXT).value(text);
			wr.name(TIMESTAMP).value(timestamp.toString());
			wr.name(LATITUDE).value(location.getLatitude());
			wr.name(LONGITUDE).value(location.getLongitude());
			wr.endObject();

			String content = output.toString();

			Log.d(TAG, "Sending message: "+content);

			Datagram sendPacket = new Datagram();
			sendPacket.setAddress(destAddr);
			sendPacket.setData(content);

			clientConnection.send(this, sendPacket);

			Log.d(TAG, "Sent packet!");

		} catch (IOException e) {
            throw new IllegalStateException("IO exception: ", e);
		}

		messageText.setText("");
	}

    @Override
    public void onDestroy() {
	    super.onDestroy();
	    if (clientConnection != null) {
            clientConnection.close();
        }
    }

}