package stock.awesome.instock.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

import stock.awesome.instock.DatabaseWriteProduct;
import stock.awesome.instock.Misc_classes.Product;
import stock.awesome.instock.Misc_classes.StringCalendar;
import stock.awesome.instock.R;
import stock.awesome.instock.exceptions.ProductNotFoundException;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InsertItemFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InsertItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InsertItemFragment extends Fragment {

    Calendar myCalendar;
    static EditText expiryDate;
    EditText inputId;
    EditText inputName;
    EditText inputDesc;
    EditText inputQty;
    EditText inputLocation;

    View aView;

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
     * @return A new instance of fragment InsertItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InsertItemFragment newInstance(String param1, String param2) {
        InsertItemFragment fragment = new InsertItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public InsertItemFragment() {
        // Required empty public constructor
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        aView = inflater.inflate(R.layout.fragment_input_item, container, false);

        myCalendar = Calendar.getInstance();
        expiryDate = (EditText) aView.findViewById(R.id.expiryEdit);

        expiryDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });


        Button submitButton = (Button) aView.findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                inputId = (EditText) aView.findViewById(R.id.productEdit);
                inputName = (EditText) aView.findViewById(R.id.nameEdit);
                inputDesc = (EditText) aView.findViewById(R.id.descriptionEdit);
                inputQty = (EditText) aView.findViewById(R.id.quantityEdit);
                inputLocation = (EditText) aView.findViewById(R.id.locationEdit);

                if (inputId.getText().toString().trim().length() == 0) {
                    Toast.makeText(getActivity(), "No ID entered", Toast.LENGTH_SHORT).show();
                } else if (inputName.getText().toString().trim().length() == 0) {
                    Toast.makeText(getActivity(), "No product name entered", Toast.LENGTH_SHORT).show();
                } else if (inputQty.getText().toString().trim().length() == 0) {
                    Toast.makeText(getActivity(), "No quantity entered", Toast.LENGTH_SHORT).show();
                } else if (inputLocation.getText().toString().trim().length() == 0) {
                    Toast.makeText(getActivity(), "No location entered", Toast.LENGTH_SHORT).show();
                } else {
                    Product inputProd = new Product();
                    onSubmitPress(inputProd);
                    Log.d("Submit successful", inputProd.getName() + " " + inputProd.getQuantity() + " "
                            + StringCalendar.toString(inputProd.getExpiry()));
                    Toast.makeText(getActivity(), "   New Item Added!   ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Log.w("Check for error","-------------------TESTING--------------------");
        // Inflate the layout for this fragment
        return aView;
    }

    // Called when submit button is pressed
    public void onSubmitPress(Product inputProd) {

        inputProd.setId(inputId.getText().toString());

        inputProd.setName(inputName.getText().toString());

        inputProd.setDesc(inputDesc.getText().toString());

        inputProd.setQuantity(Integer.valueOf(inputQty.getText().toString()));

        inputProd.setLocation(inputLocation.getText().toString());

        int inputExpiryYear = myCalendar.get(Calendar.YEAR);
        int inputExpiryMonth = myCalendar.get(Calendar.MONTH);
        int inputExpiryDate = myCalendar.get(Calendar.DATE);
        inputProd.setExpiry(new GregorianCalendar(inputExpiryYear, inputExpiryMonth, inputExpiryDate));

        try {
            DatabaseWriteProduct.updateProduct(inputProd);
        }
        catch (ProductNotFoundException e) {
            //TODO display error msg
        }
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

    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, yy, mm, dd);
        }

        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            populateSetDate(yy, mm+1, dd);
        }
        public void populateSetDate(int year, int month, int day) {
            expiryDate.setText(day+"/"+month+"/"+year);
        }

    }

}
