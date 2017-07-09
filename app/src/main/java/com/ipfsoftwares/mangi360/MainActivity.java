package com.ipfsoftwares.mangi360;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.ipfsoftwares.mangi360.adapter.ProductAdapter;
import com.ipfsoftwares.mangi360.model.Product;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener {

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView messageImageView;
        TextView messengerTextView;
        CircleImageView messengerImageView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.message_text_view);
            messengerTextView = (TextView) itemView.findViewById(R.id.account_holder_text_view);
            messengerImageView = (CircleImageView) itemView.findViewById(R.id.account_holder_image_view);
        }
    }

    private static final String TAG = "MainActivity";
    public static final String MESSAGES_CHILD = "messages";
    private static final int REQUEST_INVITE = 1;
    private static final int REQUEST_IMAGE = 2;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 10;
    public static final String ANONYMOUS = "anonymous";
    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private String mDisplayName;
    private String mPhotoUrl;
    private String mPhoneNumber;
    private SharedPreferences mSharedPreferences;
    private GoogleApiClient mGoogleApiClient;
    private static final String MESSAGE_URL = "http://friendlychat.firebase.google.com/message/";

    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;

    // Firebase instance variables
	private FirebaseAuth mFirebaseAuth;
	private FirebaseUser mFirebaseUser;
	private DatabaseReference mFirebaseReference;
	private FirebaseRecyclerAdapter<ProductDAO, MessageViewHolder> mFirebaseAdapter;

	// Product
	private ProductAdapter productAdapter;
	private ArrayList<Product> products = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Set default username is anonymous.
        mDisplayName = ANONYMOUS;

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
        	startActivity(new Intent(this, SignInActivity.class));
        	finish();
        	return;
        } else {
        	mDisplayName = mFirebaseUser.getDisplayName();

        	if(mFirebaseUser.getPhotoUrl() != null) {
        		mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
        	}
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        // Initialize ProgressBar and RecyclerView.
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
		mProgressBar.setVisibility(View.VISIBLE);
        //mLinearLayoutManager.setStackFromEnd(true);

//        // Initialize firebase database instance variables
//        // and add all existing messages.
//        mFirebaseReference = FirebaseDatabase.getInstance().getReference();
//        mFirebaseAdapter = new FirebaseRecyclerAdapter<ProductDAO, MessageViewHolder>(
//        		ProductDAO.class,
//        		R.layout.item_history,
//        		MessageViewHolder.class,
//        		mFirebaseReference.child(MESSAGES_CHILD)) {
//
//			@Override
//			public void populateViewHolder(MessageViewHolder viewHolder, ProductDAO productDAO, int position) {
//				mProgressBar.setVisibility(ProgressBar.INVISIBLE);
//				viewHolder.messageImageView.setVisibility(ImageView.GONE);
//
//				if (productDAO.getText() != null) {
//					viewHolder.messageTextView.setText(productDAO.getText());
//					viewHolder.messageTextView.setVisibility(TextView.VISIBLE);
//				} else {
//					// TODO: Implement fetch and display image messages
//				}
//
//				viewHolder.messengerTextView.setText(productDAO.getName());
//				if (productDAO.getPhotoUrl() != null) {
//					Glide.with(MainActivity.this)
//						.load(productDAO.getPhotoUrl())
//						.into(viewHolder.messengerImageView);
//				}
//			}
//        };

//        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//        	@Override
//        	public void onItemRangeInserted(int positionStart, int itemCount) {
//        		super.onItemRangeInserted(positionStart, itemCount);
//
//				int friendlyMessageCount = mFirebaseAdapter.getItemCount();
//        		int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
//        		if (lastVisiblePosition == -1 ||
//        				(positionStart >= (friendlyMessageCount - 1) &&
//        				lastVisiblePosition == (positionStart - 1))) {
//					mMessageRecyclerView.scrollToPosition(positionStart);
//        		}
//        	}
//        });

		//Setup product adapter
		productAdapter = new ProductAdapter(this,products);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(productAdapter);

		getProducts();

    }

    private void getProducts(){
		products.add(new Product("Mchele",300,1,false));
		products.add(new Product("Maji",100,2,false));
		products.add(new Product("Unga wa ngano",400,3,false));
		products.add(new Product("Matembele",200,4,false));
		products.add(new Product("Sukari guru",100,5,false));
		products.add(new Product("Chumvi",50,6,false));
		products.add(new Product("Kibiriti",70,7,false));
		products.add(new Product("Tango pori",100,8,false));
		products.add(new Product("MAziwa fresh",250,9,false));
		products.add(new Product("Pipi ya kijiti",20,10,false));

		productAdapter.notifyDataSetChanged();
		mProgressBar.setVisibility(View.GONE);
	}

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in.
        // TODO: Add code to check if user is signed in.
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
		setupSearch(menu);
        return true;
    }

    private void setupSearch(Menu menu){
		MenuItem item = menu.findItem(R.id.action_search);
		android.support.v7.widget.SearchView productSearchView = new android.support.v7.widget.SearchView(this.getSupportActionBar().getThemedContext());
		MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
		MenuItemCompat.setActionView(item, productSearchView);
		productSearchView.setQueryHint("Search Product");
		productSearchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
	}

	private void searchProduct(final String searchString){

	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.user_profile_menu:
                showUserProfile();
                return true;
            case R.id.history_menu:
                showTransactionHistory();
                return true;
			case R.id.checkout_menu:
			    checkOutProducts();
				return true;
			case R.id.sign_out_menu:
				mFirebaseAuth.signOut();
				Auth.GoogleSignInApi.signOut(mGoogleApiClient);
				mDisplayName = ANONYMOUS;
				startActivity(new Intent(this, SignInActivity.class));
				return true;
			default:
				return super.onOptionsItemSelected(item);
        }
    }

    private void showTransactionHistory() {
	    Intent intent = new Intent(this, TransactionActivity.class);
	    startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);

    	if (requestCode == RESULT_OK) {
    		String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
    		Log.d(TAG, "Invitations sent: " + ids.length);
    	}
    }

    private void causeCrash() {
    	throw new NullPointerException("Whhhopssies! Something went wrong.");
    }

    private void checkOutProducts() {
        Intent intent = new Intent(this, CheckOutActivity.class);
        intent.putExtra(CheckOutActivity.EXTRA_CHECK_OUT_PRODUCTS, "String representation of products");
        startActivity(intent);
    }

    private void showUserProfile() {
	    Intent intent = new Intent(this, ProfileActivity.class);
	    intent.putExtra("displayName", mDisplayName);
	    intent.putExtra("phoneNumber", mPhoneNumber);
        intent.putExtra("photoUrl", mPhotoUrl);
	    startActivity(intent);
    }

    private void sendInvitation() {
		Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
			.setMessage(getString(R.string.invitation_message))
			.setCallToActionText(getString(R.string.invitation_cta))
			.build();

		startActivityForResult(intent, REQUEST_INVITE);
    }
}
