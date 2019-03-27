package com.meglio.albuquerk.rubaink.devilpass;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Arrays;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Tab1 extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private String user_id;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private CircleImageView eUserImage;
    private ImageView eventImage,imvQrCode;
    private TextView evName,evPrice,evSponsor,evLieu,evSite,evDay/*,evGroup*/,eDesc
            ,evLoge,evNum,eUserName;
    private String event_pass_Id,eImage,eImageTh,eDay;

    private OnFragmentInteractionListener mListener;

    public Tab1() {
    }
    public static Tab1 newInstance(String param1, String param2) {
        Tab1 fragment = new Tab1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab1, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        evName = view.findViewById(R.id.eEventName);
        evPrice = view.findViewById(R.id.ePrice);
        imvQrCode = view.findViewById(R.id.eCode);
        evSponsor = view.findViewById(R.id.eponsor);
        evLieu = view.findViewById(R.id.eEventlieu);
        evSite = view.findViewById(R.id.eSiteEvent);
        evDay = view.findViewById(R.id.eDate);
        eUserImage = view.findViewById(R.id.eUserImage);
        eUserName = view.findViewById(R.id.eUserName);
        evLoge = view.findViewById(R.id.eZone);
        eDesc = view.findViewById(R.id.eDesc);
        evNum = view.findViewById(R.id.eNumero);

        //eventImage = view.findViewById(R.id.eImageEvent);
        //evGroup = view.findViewById(R.id.eGroup);
        event_pass_Id = Objects.requireNonNull(getActivity())
                .getIntent().getStringExtra("eventId");
        eImage = getActivity().getIntent().getStringExtra("image");
        eImageTh = getActivity().getIntent().getStringExtra("imageTh");
        eImage = getActivity().getIntent().getStringExtra("name");

        //Toast.makeText(getActivity(), "" + eImageTh, Toast.LENGTH_LONG).show();

        eDay = getActivity().getIntent().getStringExtra("day");
        evDay.setText(eDay);

        if (event_pass_Id!=null){

            firebaseFirestore.collection("USERS/" + user_id + "/PASS").document(event_pass_Id).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                if(Objects.requireNonNull(task.getResult()).exists()){

                                    String price = task.getResult().getString("price");
                                    evPrice.setText(price + " Euro");

                                    String number = task.getResult().getString("number");
                                    evNum.setText("N* " + number);

                                    final String loge = task.getResult().getString("loge");

                                    String eventId = task.getResult().getString("eventId");
                                    if (eventId != null){

                                        firebaseFirestore.collection("EVENTO").document(eventId).get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @SuppressLint("SetTextI18n")
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            if(Objects.requireNonNull(task.getResult()).exists()){

                                                                final String name = task.getResult().getString("name");
                                                                String lieu = task.getResult().getString("lieu");
                                                                String description = task.getResult().getString("description");

                                                                String loge2 = task.getResult().getString("z" + loge);
                                                                String desc_log = task.getResult().getString("d" + loge);

                                                                String spons = task.getResult().getString("sponsor");
                                                                final String web = task.getResult().getString("web");

                                                                evName.setText(name);
                                                                evLieu.setText(lieu);
                                                                eDesc.setText(description);
                                                                evLoge.setText(loge2 + " : " + desc_log);
                                                                evSponsor.setText("Sky'pass , "+ spons);
                                                                evDay.setText(task.getResult().getString("day")
                                                                        + " " + task.getResult().getString("time"));
                                                                evSite.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        Intent t = new Intent(getActivity(), WebActivity.class);
                                                                        t.putExtra("lien",web);t.putExtra("name",name);
                                                                        startActivity(t);
                                                                    }
                                                                });

                                                                //String image = task.getResult().getString("imageUrl");
                                                                //String imageTh = task.getResult().getString("imageThumb");
                                                                /*if (!Objects.requireNonNull(getActivity()).isFinishing()){
                                                                    Glide.with(getActivity()).load(image).thumbnail(
                                                                            Glide.with(getActivity()).load(imageTh)
                                                                    ).into(eventImage);
                                                                }*/
                                                                //String time = task.getResult().getString("time");
                                                                //String day = task.getResult().getString("day");
                                                                //String group = task.getResult().getString("group");
                                                                //evGroup.setText("Sky'Pass " + group);
                                                            }
                                                        } else {
                                                            Toast.makeText(getActivity(),"(Error)" , Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                    }
                                }
                            } else {
                                Toast.makeText(getActivity(),"(Error)" , Toast.LENGTH_LONG).show(); }
                        }
                    });

        }
        firebaseFirestore.collection("USERS").document(user_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            if(Objects.requireNonNull(task.getResult()).exists()){

                                String image = task.getResult().getString("image");
                                String name = task.getResult().getString("name");

                                eUserName.setText(name);
                                if (!Objects.requireNonNull(getActivity()).isFinishing()){
                                    Glide.with(getActivity()).load(image).into(eUserImage); } }
                        } else {
                            Toast.makeText(getActivity(),"(Error)", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        if (event_pass_Id != null){
            Bitmap bitmap = null;
            try {
                bitmap = createBarcodeBitmap(event_pass_Id, 480, 100);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            imvQrCode.setImageBitmap(bitmap);
        }


        return view;
    }

    private Bitmap createBarcodeBitmap(String data, int width, int height) throws WriterException {
        MultiFormatWriter writer = new MultiFormatWriter();
        String finalData = Uri.encode(data);

        // Use 1 as the height of the matrix as this is a 1D Barcode.
        BitMatrix bm = writer.encode(finalData, BarcodeFormat.CODE_128, width, 1);
        int bmWidth = bm.getWidth();

        Bitmap imageBitmap = Bitmap.createBitmap(bmWidth, height, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < bmWidth; i++) {
            // Paint columns of width 1
            int[] column = new int[height];
            Arrays.fill(column, bm.get(i, 0) ? Color.BLACK : Color.WHITE);
            imageBitmap.setPixels(column, 0, 1, i, 0, 1, height);
        }

        return imageBitmap;
    }
    private Bitmap textToImage(String text, int width, int height) throws WriterException, NullPointerException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.DATA_MATRIX.QR_CODE,
                    width, height, null);
        } catch (IllegalArgumentException Illegalargumentexception) {
            return null;
        }

        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        int colorWhite = 0xFFFFFFFF;
        int colorBlack = 0xFF000000;

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;
            for (int x = 0; x < bitMatrixWidth; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ? colorBlack : colorWhite;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, width, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
