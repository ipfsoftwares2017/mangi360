package com.ipfsoftwares.mangi360;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.ipfsoftwares.mangi360.adapter.ProductAdapter;
import com.ipfsoftwares.mangi360.database.ProductColumn;
import com.ipfsoftwares.mangi360.database.ProductProvider;
import com.ipfsoftwares.mangi360.model.Product;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener, LoaderManager.LoaderCallbacks<Cursor>, ProductAdapter.ProductDelegate {

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
    private RecyclerView addedRecycler;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
	private TextView empty;
	private TextView amountValue;
	private TextView itemCount;
	private Button doneBtn;
	private ImageButton doneBtnSld;
	private View viewCart;
	private SlidingUpPanelLayout slidingUpPanelLayout;

	private double totalAmount = 0;
	private double totalCount = 0;


	// Firebase instance variables
	private FirebaseAuth mFirebaseAuth;
	private FirebaseUser mFirebaseUser;
	private DatabaseReference mFirebaseReference;
	private FirebaseRecyclerAdapter<ProductDAO, MessageViewHolder> mFirebaseAdapter;

	// Product
	private ProductAdapter productAdapter;
	private ProductAdapter addedAdapter;
	private ArrayList<Product> products = new ArrayList<>();
	private ArrayList<Product> currentProducts = new ArrayList<>();
	private ArrayList<Product> addedProducts = new ArrayList<>();
	private final int PRODUCT_LOADER_ID = 123;
	private final int SEARCH_LOADER_ID = 456;
	private final int COUNT_LOADER_ID = 789;

	private String searchString = "";

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
		slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
		addedRecycler = (RecyclerView) findViewById(R.id.added_recycler);
        mLinearLayoutManager = new LinearLayoutManager(this);
		empty = (TextView) findViewById(R.id.empty);
		amountValue = (TextView) findViewById(R.id.amount_value);
		itemCount = (TextView) findViewById(R.id.item_count);
		doneBtn = (Button) findViewById(R.id.done_btn);
		doneBtnSld = (ImageButton) findViewById(R.id.done_btn_sld);
		viewCart = findViewById(R.id.view_cart);

		slidingUpPanelLayout.setDragView(doneBtn);

		doneBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});

		doneBtnSld.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
			}
		});

		viewCart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
			}
		});


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
		productAdapter.setDelegate(this);
		addedAdapter = new ProductAdapter(this,addedProducts);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(productAdapter);
		addedRecycler.setAdapter(addedAdapter);

		getProducts();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in.
        // TODO: Add code to check if user is signed in.
    }

	@Override
	public void onBackPressed() {
		if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)
			slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
		else
			super.onBackPressed();
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
		productSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
			@Override
			public boolean onClose() {
				searchString = "";
				getLoaderManager().restartLoader(PRODUCT_LOADER_ID,null,MainActivity.this);
				return true;
			}
		});
		productSearchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				searchString = newText;
				if(!TextUtils.isEmpty(newText)){
					searchProduct();
				}else{
					getLoaderManager().restartLoader(PRODUCT_LOADER_ID,null,MainActivity.this);
				}
				return false;
			}
		});
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
			case R.id.action_listing:
				startActivity(new Intent(this, ListingActivity.class));
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

    private void notifyDataChange(){
		productAdapter.notifyDataSetChanged();
	}

	@Override
	public void productAdded(Product product){
		addedProducts.add(product);
		notifyAddedDataChange();
	}

	public void productEdited(Product product,int position){
		addedProducts.remove(position);
		addedProducts.add(position, product);
		notifyAddedDataChange();
	}

	public void productRemoved(int position){
		addedProducts.remove(position);
		notifyAddedDataChange();
	}

	private void calculateTotal(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				for(Product product: addedProducts){
					totalAmount+= product.getTotalAmount();
					totalCount+= product.getQuantity();
				}
			}
		}).start();
	}

    private void notifyAddedDataChange(){
		addedAdapter.notifyDataSetChanged();
	}

	private void getProducts(){
		mProgressBar.setVisibility(View.VISIBLE);
		//getLoaderManager().initLoader(PRODUCT_LOADER_ID,null,this);
		new Thread(new Runnable() {
			@Override
			public void run() {
				Cursor data = LoadData(PRODUCT_LOADER_ID);
				if (data != null && data.getCount() > 0){
					products.clear();
					while(data.moveToNext()){
						products.add(new Product(data));
					}
					currentProducts.clear();
					currentProducts.addAll(products);

					notifyDataChange();
					hideEmpty();
				}
				mProgressBar.setVisibility(View.GONE);
			}
		}).start();
	}


	private void searchProduct(){
		getLoaderManager().restartLoader(SEARCH_LOADER_ID,null,this);
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				Cursor cursor = LoadData(SEARCH_LOADER_ID);
//				products.clear();
//				while(cursor.moveToNext()){
//					products.add(new Product(cursor));
//				}
//			}
//		}).start();
	}

	private Cursor LoadData(int id){
		switch (id) {
			case SEARCH_LOADER_ID:
				return getContentResolver().query(ProductProvider.Products.CONTENT_URI,
					ProductAdapter.PROJECTION,
					ProductColumn.NAME + " LIKE ? ",
					new String[]{"%" + searchString.toLowerCase() + "%"}, null);
			case PRODUCT_LOADER_ID:
				return getContentResolver().query(ProductProvider.Products.CONTENT_URI,
					ProductAdapter.PROJECTION,
					null,null,null);
			default:
				return null;
		}
	}
	//Database content provider
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if(id == PRODUCT_LOADER_ID)
			return new CursorLoader(this,
				ProductProvider.Products.CONTENT_URI,
				ProductAdapter.PROJECTION,
				null,null,null);
//		else if(id == COUNT_LOADER_ID){
//			return new CursorLoader(this,
//				ProductProvider.Products.CONTENT_URI,
//				ProductAdapter.PROJECTION,
//				ProductColumn.STATUS + " = ? ",
//				new String[]{"1"},null);
//		}
		else if(!TextUtils.isEmpty(searchString)) {
			return new CursorLoader(this,
				ProductProvider.Products.CONTENT_URI,
				ProductAdapter.PROJECTION,
				ProductColumn.NAME + " LIKE ? ",
				new String[]{"%" + searchString.toLowerCase() + "%"}, null);
		}
		else
			return null;
	}


	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mProgressBar.setVisibility(View.GONE);
		if(loader.getId() == SEARCH_LOADER_ID ){
			products.clear();
			while(data.moveToNext()){
				products.add(new Product(data));
			}
			hideEmpty();
		} else if(loader.getId() == COUNT_LOADER_ID){
			addedProducts.clear();
			totalAmount = 0;
			while(data.moveToNext()){
				Product newProduct = new Product(data);
				addedProducts.add(newProduct);
				totalAmount+= newProduct.getTotalAmount();
				totalCount+= newProduct.getQuantity();
			}
			hideEmpty();
		}else if (data != null && data.getCount() > 0 && TextUtils.isEmpty(searchString)){
			products.clear();
			while(data.moveToNext()){
				products.add(new Product(data));
			}
			hideEmpty();

			currentProducts = products;
		}

		if(products.size() == 0){
			if (!TextUtils.isEmpty(searchString)){
				empty.setVisibility(View.VISIBLE);
				empty.setText(getString(R.string.no_product) + searchString);
			}else {
				empty.setVisibility(View.VISIBLE);
				empty.setText(getString(R.string.empty_main));
			}
		}

		notifyDataChange();
		notifyAddedDataChange();
		setUpAmount();
	}

	private void setUpAmount(){
		amountValue.setText("Tsh" + String.valueOf(totalAmount));
		itemCount.setText(String.valueOf(totalCount));
	}
	private void hideEmpty(){
		if(empty.getVisibility() == View.VISIBLE)
			empty.setVisibility(View.GONE);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {

	}
}
