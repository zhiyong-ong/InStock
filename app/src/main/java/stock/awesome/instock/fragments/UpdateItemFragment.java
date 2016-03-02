package stock.awesome.instock.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;

import stock.awesome.instock.DatabaseLauncher;
import stock.awesome.instock.DatabaseReadProduct;
import stock.awesome.instock.DatabaseWriteProduct;
import stock.awesome.instock.misc_classes.Product;
import stock.awesome.instock.misc_classes.StringCalendar;
import stock.awesome.instock.R;
import stock.awesome.instock.exceptions.ProductNotFoundException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UpdateItemFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UpdateItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateItemFragment extends Fragment {

    View aView;
    Context context = getActivity();
    static Activity activity;
    Firebase database;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpdateItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdateItemFragment newInstance(String param1, String param2) {
        UpdateItemFragment fragment = new UpdateItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public UpdateItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        database = DatabaseLauncher.database;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        aView = inflater.inflate(R.layout.fragment_update_item, container, false);
        activity = (Activity)aView.getContext();


        Button searchButton = (Button) aView.findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    final EditText productIDText = (EditText)aView.findViewById(R.id.productSearchEdit);
                    final String productID = productIDText.getText().toString();
                    Log.e("PRODUCT", "------------------- product id: " + productID);
                    DatabaseReadProduct.read(productID, DatabaseReadProduct.ProdUseCase.DISPLAY);
                } catch (ProductNotFoundException e) {
                    //some shit here, ask kabir
                    e.printStackTrace();
                }

            }
        });
        // Inflate the layout for this fragment
        return aView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public static void SearchItem(final Product product) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = LayoutInflater.from(activity);
        final View dialogView = inflater.inflate(R.layout.search_box, null);

        final TextView productIDText = (TextView) dialogView.findViewById(R.id.productView);
        final TextView quantityText = (TextView) dialogView.findViewById(R.id.qtyView);
        final TextView expiryText = (TextView) dialogView.findViewById(R.id.expiryView);
        final TextView nameText = (TextView) dialogView.findViewById(R.id.nameView);
        final EditText newQty = (EditText) dialogView.findViewById(R.id.newUpdateQty);
        //TODO: center the new qty
        Log.e("PRODUCT", "------------------" + product.getId() + "\t" + product.getQuantity());

        nameText.setText(product.getName());
        productIDText.setText(product.getId());

        quantityText.setText(Integer.toString(product.getQuantity()));
        expiryText.setText(StringCalendar.toProperDateString(product.getExpiry()));


        dialogBuilder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //int newQuantity
                Product qtyExpProduct = new Product(product.getId(), 10000, product.getExpiry());
                try {
                    Log.e("PRODUCT", "--------------------- qty: " + newQty.getText().toString());
                    DatabaseWriteProduct.updateQuantityExpiry(qtyExpProduct);
                } catch (ProductNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        dialogBuilder.setView(dialogView);
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
/*
    public static class searchDialogFragment extends DialogFragment {
        Context mContext;

        public searchDialogFragment() {
            mContext = getActivity();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            //View view = getActivity().getLayoutInflater().inflate(R.layout.search_box, new LinearLayout(getActivity()), false);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
            LayoutInflater inflater = LayoutInflater.from(mContext);
            final View dialogView = inflater.inflate(R.layout.search_box, null);
            // Retrieve layout elements
            final TextView productIDText = (TextView) dialogView.findViewById(R.id.productView);
            final TextView quantityText = (TextView) dialogView.findViewById(R.id.qtyView);
            final TextView expiryText = (TextView) dialogView.findViewById(R.id.expiryView);

            dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                    // on success
                }
            });

            dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialogBuilder.setView(dialogView);
            // Build dialog
            return dialogBuilder.create();

        }
    }
    */
}
