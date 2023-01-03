package com.example.go4lunch;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.example.go4lunch.Utils.FakeData.fakeCurrentUser;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import android.content.Context;
import android.net.Uri;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.go4lunch.activities.MainActivity;
import com.example.go4lunch.dataSource.remoteData.FirebaseServicesClient;
import com.example.go4lunch.injection.Injection;
import com.example.go4lunch.injection.ViewModelFactory;
import com.example.go4lunch.viewModel.UserViewModel;
import com.facebook.login.LoginFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.util.Executors;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

@RunWith(MockitoJUnitRunner.class)
public class FirestoreTest {

    final FirebaseAuth firebaseAuthMock = mock(FirebaseAuth.class);
    FirebaseServicesClient firebaseServicesClient;
    // Firestore
    final FirebaseFirestore firebaseFirestoreMock = mock(FirebaseFirestore.class);
    final CollectionReference usersCollectionMock = mock(CollectionReference.class);
    final DocumentReference userDocumentMock = mock(DocumentReference.class);
    final Task<DocumentSnapshot> userDocSnapshotTask = mock(Task.class);
    final OnCompleteListener onCompleteListenerTask = mock(OnCompleteListener.class);
    final DocumentSnapshot userDocumentSnapshot = mock(DocumentSnapshot.class);
    final Query queryMock = mock(Query.class);
    final Task<QuerySnapshot> querySnapshotTask = mock(Task.class);
    final QuerySnapshot querySnapshot = mock(QuerySnapshot.class);
    final FirebaseUser firebaseUserMock = mock(FirebaseUser.class);


    private Context context;
    UserViewModel userViewModel;
    private static final String NAME_TEST = "Aldo Pizza Neudorf";
    private final String PLACE_ID = "ChIJ3dsMIAzJlkcRavdegCsfEvs";
    private final Double RATING = 4.3;
    private final String REFERENCE = "ChIJ3dsMIAzJlkcRavdegCsfEvs";
    private final String ADDRESS = "137 Route du Polygone, Strasbourg";
/*
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

 */

    @Mock
    Context mMockContext;
/*
    @Rule
    public  ActivityScenarioRule<MainActivity> mainActivityActivityScenarioRule = new ActivityScenarioRule<MainActivity>(MainActivity.class);

 */



    Task<AuthResult> successTask;
    Task<AuthResult> failureTask;

    @Mock
    FirebaseAuth mAuth;


    @Before
    public void onBefore(){
        MockitoAnnotations.initMocks(this);

    }





 /*
    @Test
    public void testLogin(){
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                EditText email = loginActivity.findViewById(com.firebase.ui.auth.R.id.accessibility_custom_action_8);
                EditText pass = loginActivity.findViewById(R.id.password);
                email.setText("email");
                pass.setText("pass");
                Button loginBtn = loginActivity.findViewById(R.id.login);
                loginBtn.performClick();
                assertTrue(loginActivity.isCurUserLoggedIn());
            }
        });
    }

  */
 @Test
 public void should_push_current_user_data_to_firebase() {
     firebaseServicesClient.pushCurrentUserData();

     final ArgumentCaptor<Map<String, Object>> userDataCaptor = ArgumentCaptor.forClass(HashMap.class);
     verify(userDocumentMock).update(userDataCaptor.capture());
     System.out.println("------------------captor-----------"+userDataCaptor.getValue());
     System.out.println("----------------restochoice-----------"+fakeCurrentUser.getRestaurantChoice());
     System.out.println("----------------restofav-----------"+fakeCurrentUser.getUid());
     assertTrue(userDataCaptor.getValue().containsValue(fakeCurrentUser.getUid()));
    // assertTrue(userDataCaptor.getValue().containsValue(fakeCurrentUser.getRestaurantChoice()));

     //assertTrue(userDataCaptor.getValue().containsValue(fakeCurrentUser.getFavouriteRestaurants()));

 }


    @Test
    public void testVerifyUserLoggedWithValidEmailAndPassword(){
        //FirebaseAuth auth = FirebaseAuth.getInstance();
        String email = "smail.omari@laposte.net";
        String password = "1mdp2plUS67";
        Mockito.when(mAuth.signInWithEmailAndPassword(email,password)).thenReturn(successTask);



    }

    @Test
    public void singInTest() throws Exception {
        String email = "smail.omari@laposte.net";
        String password = "1mdp2plUS";

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener((Executor)this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Assert.assertEquals(true, task.isSuccessful());
            }
        });
    }


    @Before
    public void setup() {

        Context context = mock(Context.class);
/*
        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory(context);
        userViewModel = new ViewModelProvider(new ViewModelStoreOwner() {
            @NonNull
            @Override
            public ViewModelStore getViewModelStore() {
                return new ViewModelStore();
            }
        }.getViewModelStore(), mViewModelFactory).get(UserViewModel.class);

 */

        setUpFirebaseAuthMocks();
        setUpFirebaseFirestoreMocks();
        initFirebaseServicesClient();
    }

    @After
    public void tearDown(){
        clearInvocations(
                firebaseFirestoreMock,
                firebaseAuthMock,
                usersCollectionMock,
                userDocumentMock,
                userDocSnapshotTask,
                userDocumentSnapshot,
                queryMock,
                querySnapshot,
                querySnapshotTask);
    }

    @Test
    public void should_initialize_FirebaseServicesClient_correctly() {
        //    initFirebaseServicesClient(); ---> Called in setUp() method.
        System.out.println("----------------------service client---------"+firebaseServicesClient.getCurrentUser().getUsername());
        assertNotNull(firebaseServicesClient.getCurrentUser());

        //mainActivityActivityScenarioRule.getScenario().getResult();
        MainActivity mainActivity = new MainActivity();
        mainActivity.checkIfUserIsSignedIn();
        //mainActivityActivityScenarioRule.getScenario().onActivity(ne)



    }

    // ----------- UTILS ----------- //

    private void setUpFirebaseAuthMocks() {
        // FirebaseUser mock
        System.out.println("------------mock---------------"+firebaseUserMock.getEmail());
        when(firebaseUserMock.getEmail()).thenReturn(fakeCurrentUser.getUserEmail());
        when(firebaseUserMock.getDisplayName()).thenReturn(fakeCurrentUser.getUsername());
        final Uri photoUriMock = mock(Uri.class);
        when(photoUriMock.toString()).thenReturn(fakeCurrentUser.getUrlPicture());
        when(firebaseUserMock.getPhotoUrl()).thenReturn(photoUriMock);
        when(firebaseUserMock.getUid()).thenReturn(fakeCurrentUser.getUid());
        when(firebaseAuthMock.getCurrentUser()).thenReturn(firebaseUserMock);
    }

    private void setUpFirebaseFirestoreMocks() {
        String CHOSEN_RESTAURANT_ID = "restaurantChoice";
        String CHOSEN_RESTAURANT_NAME = "chosenRestaurantName";
        String WORKPLACE = "workplaceId";
        String CONVERSATIONS = "conversations";
        String LIKED_RESTAURANTS = "favouriteRestaurants";
        String DEFAULT_USER_PHOTO_URL = "https://as2.ftcdn.net/v2/jpg/04/10/43/77/1000_F_410437733_hdq4Q3QOH9uwh0mcqAhRFzOKfrCR24Ta.jpg";
        // Collection
        when(firebaseFirestoreMock.collection("users")).thenReturn(usersCollectionMock);
        when(usersCollectionMock.document(fakeCurrentUser.getUid())).thenReturn(userDocumentMock);
        // Document
        when(userDocumentMock.get()).thenReturn(userDocSnapshotTask);
/*        when(userDocumentSnapshot.getString(CHOSEN_RESTAURANT_ID))
                .thenReturn(fakeCurrentUser.getRestaurantChoice());

 */
        /*
        when(userDocumentSnapshot.getString(UserProvider.CHOSEN_RESTAURANT_NAME))
                .thenReturn(fakeCurrentUser.getChosenRestaurantName());

        when(userDocumentSnapshot.getString(UserProvider.WORKPLACE))
                .thenReturn(fakeCurrentUser.getWorkplaceId());
        when(userDocumentSnapshot.get(UserProvider.CONVERSATIONS))
                .thenReturn(fakeCurrentUser.getConversationsIds());

         */
        when(userDocumentSnapshot.get(LIKED_RESTAURANTS))
                .thenReturn(fakeCurrentUser.getFavouriteRestaurants());
        // Query
        /*
        when(usersCollectionMock.whereEqualTo(anyString(), anyString())).thenReturn(queryMock);
        when(queryMock.whereEqualTo(anyString(), anyString())).thenReturn(queryMock);
        when(queryMock.get()).thenReturn(querySnapshotTask);
        when(querySnapshotTask.addOnSuccessListener(any(OnSuccessListener.class)))
                .thenReturn(querySnapshotTask);
        when(querySnapshot.getDocuments()).thenReturn(Collections.singletonList(userDocumentSnapshot));

         */
    }


    private void initFirebaseServicesClient() {
        firebaseServicesClient = new FirebaseServicesClient(firebaseFirestoreMock, firebaseAuthMock);
        verify(firebaseAuthMock).addAuthStateListener(isA(FirebaseAuth.AuthStateListener.class));
        final ArgumentCaptor<OnSuccessListener<DocumentSnapshot>> onSuccessListenerCaptor =
                ArgumentCaptor.forClass(OnSuccessListener.class);
        verify(userDocSnapshotTask).addOnSuccessListener(onSuccessListenerCaptor.capture());
        onSuccessListenerCaptor.getValue().onSuccess(userDocumentSnapshot);
        System.out.println("-----------userDocumentSnapshot----------"+userDocumentSnapshot.getId());
    }


}
