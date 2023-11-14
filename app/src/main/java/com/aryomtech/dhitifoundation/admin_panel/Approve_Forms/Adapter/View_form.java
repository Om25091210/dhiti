package com.aryomtech.dhitifoundation.admin_panel.Approve_Forms.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.aryomtech.dhitifoundation.R;
import com.aryomtech.dhitifoundation.fcm.Specific;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImageTranscoderType;
import com.facebook.imagepipeline.core.MemoryChunkType;
import com.facebook.imagepipeline.request.ImageRequest;
import com.gkemon.XMLtoPDF.PdfGenerator;
import com.gkemon.XMLtoPDF.PdfGeneratorListener;
import com.gkemon.XMLtoPDF.model.FailureResponse;
import com.gkemon.XMLtoPDF.model.SuccessResponse;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import soup.neumorphism.NeumorphButton;
import www.sanju.motiontoast.MotionToast;


public class View_form extends Fragment {

    View view;
    ImageView imageBack,del;
    NeumorphButton submit,download;
    String device_token="";
    NestedScrollView nestedscroll;
    SimpleDraweeView image;
    TextView name,blood,city,contact,address,qualification
            ,profession,email,age,gender,heard_about_dhiti
            ,dedicate_time,suggestion,start_city_dhiti,represent_dhiti
            ,experience_comm,why_dhiti,name_of_school_college,dedicate_talent;
    String name_args,blood_args,city_args,contact_args,address_args,qualification_args
            ,profession_args,email_args,age_args,gender_args,heard_about_dhiti_args
            ,dedicate_time_args,suggestion_args,start_city_dhiti_args,represent_dhiti_args
            ,experience_comm_args,why_dhiti_args,name_of_school_college_args,dedicate_talent_args
            ,approve_args,pushkey_args,uid_args,image_link;
    DatabaseReference forms_ref,users_ref;
    private Context contextNullSafe;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_view_form, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        forms_ref= FirebaseDatabase.getInstance().getReference().child("forms");
        users_ref= FirebaseDatabase.getInstance().getReference().child("users");
        try {
            name_args=getArguments().getString("name_sending_form_2509_01");
            blood_args=getArguments().getString("blood_sending_form_2509_02");
            city_args=getArguments().getString("city_sending_form_2509_03");
            contact_args=getArguments().getString("contact_sending_form_2509_04");
            address_args=getArguments().getString("address_sending_form_2509_05");
            qualification_args=getArguments().getString("qualification_sending_form_2509_06");
            profession_args=getArguments().getString("profession_sending_form_2509_07");
            email_args=getArguments().getString("email_sending_form_2509_08");
            age_args=getArguments().getString("age_sending_form_2509_09");
            gender_args=getArguments().getString("gender_sending_form_2509_10");
            heard_about_dhiti_args=getArguments().getString("heard_about_dhiti_sending_form_2509_11");
            dedicate_time_args=getArguments().getString("dedicate_time_sending_form_2509_12");
            suggestion_args=getArguments().getString("suggestion_sending_form_2509_13");
            start_city_dhiti_args=getArguments().getString("start_dhiti_city_sending_2509_14");
            represent_dhiti_args=getArguments().getString("represent_dhiti_s_c_sending_2509_15");
            experience_comm_args=getArguments().getString("experience_comm_sending_2509_16");
            why_dhiti_args=getArguments().getString("why_dhiti_sending_2509_17");
            name_of_school_college_args=getArguments().getString("name_of_school_college_sending_2509_18");
            dedicate_talent_args=getArguments().getString("dedicate_talent_sending_2509_19");
            pushkey_args=getArguments().getString("pushkey_sending_2509_20");
            uid_args=getArguments().getString("uid_sending_2509_021");
            approve_args=getArguments().getString("approved_or_not_sending");
            image_link=getArguments().getString("image_link_sending");
        } catch (Exception e) {
            e.printStackTrace();
        }
        users_ref.child(uid_args).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("token").exists())
                    device_token=snapshot.child("token").getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        nestedscroll=view.findViewById(R.id.nestedscroll);
        name=view.findViewById(R.id.editTextTextMultiLine);
        blood=view.findViewById(R.id.editTextTextMultiLine1);
        city=view.findViewById(R.id.editTextTextMultiLine2);
        contact=view.findViewById(R.id.editTextTextMultiLine3);
        address=view.findViewById(R.id.editTextTextMultiLine4);
        qualification=view.findViewById(R.id.editTextTextMultiLine5);
        profession=view.findViewById(R.id.editTextTextMultiLine6);
        email=view.findViewById(R.id.editTextTextMultiLine7);
        age=view.findViewById(R.id.editTextTextMultiLine8);
        gender=view.findViewById(R.id.sticky_switch);
        download=view.findViewById(R.id.download);
        heard_about_dhiti=view.findViewById(R.id.editTextTextMultiLine10);
        dedicate_time=view.findViewById(R.id.editTextTextMultiLine103);
        suggestion=view.findViewById(R.id.editTextTextMultiLine12);
        start_city_dhiti=view.findViewById(R.id.editTextTextMultiLine1204);
        represent_dhiti=view.findViewById(R.id.editTextTextMultiLine1205);
        experience_comm=view.findViewById(R.id.editTextTextMultiLine1206);
        why_dhiti=view.findViewById(R.id.editTextTextMultiLine16);
        name_of_school_college=view.findViewById(R.id.editTextTextMultiLine17);
        dedicate_talent=view.findViewById(R.id.editTextTextMultiLine18);
        imageBack=view.findViewById(R.id.imageBack);
        submit=view.findViewById(R.id.submit);
        del=view.findViewById(R.id.del);
        image=view.findViewById(R.id.image);

        Fresco.initialize(
                getContextNullSafety(),
                ImagePipelineConfig.newBuilder(getContextNullSafety())
                        .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY)
                        .setImageTranscoderType(ImageTranscoderType.JAVA_TRANSCODER)
                        .experiment().setNativeCodeDisabled(true)
                        .build());

        if(name_args!=null)
            name.setText(name_args);
        if (blood_args!=null)
            blood.setText(blood_args);
        if (city_args!=null)
            city.setText(city_args);
        if (contact_args!=null)
            contact.setText(contact_args);
        if (address_args!=null)
            address.setText(address_args);
        if (qualification_args!=null)
            qualification.setText(qualification_args);
        if (profession_args!=null)
            profession.setText(profession_args);
        if (email_args!=null)
            email.setText(email_args);
        if (age_args!=null)
            age.setText(age_args);
        if (gender_args!=null)
            gender.setText(gender_args);
        if (heard_about_dhiti_args!=null)
            heard_about_dhiti.setText(heard_about_dhiti_args);
        if (dedicate_time_args!=null)
            dedicate_time.setText(dedicate_time_args);
        if (suggestion_args!=null)
            suggestion.setText(suggestion_args);
        if (start_city_dhiti_args!=null)
            start_city_dhiti.setText(start_city_dhiti_args);
        if (represent_dhiti_args!=null)
            represent_dhiti.setText(represent_dhiti_args);
        if (experience_comm_args!=null)
            experience_comm.setText(experience_comm_args);
        if (why_dhiti_args!=null)
            why_dhiti.setText(why_dhiti_args);
        if (name_of_school_college_args!=null)
            name_of_school_college.setText(name_of_school_college_args);
        if (dedicate_talent_args!=null) {
            String str_talent=dedicate_talent_args.replace("[","");
            String str_talent_final=str_talent.replace("]","");
            dedicate_talent.setText(str_talent_final);
        }
        if (approve_args!=null)
            submit.setVisibility(View.GONE);
        else
            submit.setVisibility(View.VISIBLE);

        del.setOnClickListener(v->{
            Dialog dialog = new Dialog(getContext());
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_for_sure);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            TextView cancel=dialog.findViewById(R.id.textView96);
            TextView yes=dialog.findViewById(R.id.textView95);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();
            cancel.setOnClickListener(vc-> dialog.dismiss());
            yes.setOnClickListener(vy->{
                forms_ref.child(pushkey_args).removeValue();
                dialog.dismiss();
                MotionToast.Companion.darkColorToast(requireActivity(),
                        "Success!",
                        "Form deleted.",
                        MotionToast.TOAST_SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(requireContext(), R.font.helvetica_regular));
            });
        });
        submit.setOnClickListener(v-> {
            Dialog dialog = new Dialog(getContext());
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialog_for_sure);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            TextView cancel=dialog.findViewById(R.id.textView96);
            TextView yes=dialog.findViewById(R.id.textView95);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.show();
            cancel.setOnClickListener(vc-> dialog.dismiss());
            yes.setOnClickListener(vy->{
                if(device_token!=null) {
                    if (!device_token.equals("")) {
                        Specific specific = new Specific();
                        specific.noti(name_args, "Your form request to join as a member has been Approved.Welcome to dhiti family❤", device_token);
                    }
                }
                if (uid_args!=null){
                    users_ref.child(uid_args).child("request").setValue("approved");
                    users_ref.child(uid_args).child("identity").setValue("member");
                }
                forms_ref.child(pushkey_args).child("approve").setValue("true");
                submit.setVisibility(View.GONE);
                dialog.dismiss();
                MotionToast.Companion.darkColorToast(requireActivity(),
                        "Success!",
                        "Form Approved.",
                        MotionToast.TOAST_SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(requireContext(), R.font.helvetica_regular));
            });
        });

        if(image_link!=null) {
            Uri uri = Uri.parse(image_link);
            ImageRequest request = ImageRequest.fromUri(uri);

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(image.getController()).build();

            image.setController(controller);
        }

        download.setOnClickListener(v->{
            PdfGenerator.getBuilder()
                    .setContext(getContextNullSafety())
                    .fromViewSource()
                    .fromView(nestedscroll)
                    .setFileName(name_args+"_dhiti_volunteer"+"_"+"-PDF")
                    .setFolderName("dhiti-PDF-folder")
                    .openPDFafterGeneration(true)
                    .build(new PdfGeneratorListener() {
                        @Override
                        public void onFailure(FailureResponse failureResponse) {
                            super.onFailure(failureResponse);
                            MotionToast.Companion.darkColorToast(getActivity(),
                                    "Failed ☹️",
                                    "pdf generation Failed!!",
                                    MotionToast.TOAST_ERROR,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(getActivity(),R.font.helvetica_regular));
                        }

                        @Override
                        public void showLog(String log) {
                            super.showLog(log);
                        }

                        @Override
                        public void onStartPDFGeneration() {
                            /*When PDF generation begins to start*/
                        }

                        @Override
                        public void onFinishPDFGeneration() {
                            /*When PDF generation is finished*/
                        }

                        @Override
                        public void onSuccess(SuccessResponse response) {
                            super.onSuccess(response);
                            MotionToast.Companion.darkColorToast(getActivity(),
                                    "Success",
                                    "Pdf generated successfully.",
                                    MotionToast.TOAST_SUCCESS,
                                    MotionToast.GRAVITY_BOTTOM,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(getActivity(),R.font.helvetica_regular));
                        }
                    });
        });

        imageBack.setOnClickListener(v->back());
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {

                FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
                FragmentTransaction ft=fm.beginTransaction();
                if(fm.getBackStackEntryCount()>0) {
                    fm.popBackStack();
                }
                ft.commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contextNullSafe = context;
    }
    /**CALL THIS IF YOU NEED CONTEXT*/
    public Context getContextNullSafety() {
        if (getContext() != null) return getContext();
        if (getActivity() != null) return getActivity();
        if (contextNullSafe != null) return contextNullSafe;
        if (getView() != null && getView().getContext() != null) return getView().getContext();
        if (requireContext() != null) return requireContext();
        if (requireActivity() != null) return requireActivity();
        if (requireView() != null && requireView().getContext() != null)
            return requireView().getContext();

        return null;

    }
    private void back() {
        FragmentManager fm=((FragmentActivity) getContextNullSafety()).getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        if(fm.getBackStackEntryCount()>0) {
            fm.popBackStack();
        }
        ft.commit();
    }
}