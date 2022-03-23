package com.aryomtech.dhitifoundation;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import soup.neumorphism.NeumorphCardView;


public class about_aryomtech extends Fragment {

    View view;
    ImageView img_back;
    private Context contextNullSafe;
    NeumorphCardView fb_card,twitter_card,linkedin_card,whatsapp_card,insta_card;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_about_aryomtech, container, false);
        if (contextNullSafe == null) getContextNullSafety();
        img_back=view.findViewById(R.id.imageBack);
        fb_card=view.findViewById(R.id.card_fb);
        twitter_card=view.findViewById(R.id.card_twitter);
        linkedin_card=view.findViewById(R.id.card_linkedin);
        whatsapp_card=view.findViewById(R.id.card_whatsapp);
        insta_card=view.findViewById(R.id.card_insta);

        fb_card.setOnClickListener(v->{
            String facebookUrl ="https://www.facebook.com/profile.php?id=118931147057491";
            Intent facebookAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl));
            facebookAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            startActivity(facebookAppIntent);
        });
        twitter_card.setOnClickListener(v->{
            String url = "https://twitter.com/aryomtech?s=08";
            Intent twitterAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            twitterAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            startActivity(twitterAppIntent);
        });
        linkedin_card.setOnClickListener(v->{
            String url = "https://www.linkedin.com/company/aryomtech-developing-to-empower";
            Intent linkedInAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            linkedInAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            startActivity(linkedInAppIntent);
        });
        whatsapp_card.setOnClickListener(v->{
            String url = "https://api.whatsapp.com/send?phone=" +"+91"+ "8917204352";
            try {
                PackageManager pm = v.getContext().getPackageManager();
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                v.getContext().startActivity(i);
            } catch (PackageManager.NameNotFoundException e) {
                v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });
        insta_card.setOnClickListener(v->{
            Intent insta_in;
            String scheme = "http://instagram.com/_u/"+"aryomtech";
            String path = "https://instagram.com/"+"aryomtech";
            String nomPackageInfo ="com.instagram.android";
            try {
                requireContext().getPackageManager().getPackageInfo(nomPackageInfo, 0);
                insta_in = new Intent(Intent.ACTION_VIEW, Uri.parse(scheme));
            } catch (Exception e) {
                insta_in = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
            }
            startActivity(insta_in);
        });

        img_back.setOnClickListener(v->back());
        // This callback will only be called when MyFragment is at least Started.
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contextNullSafe = context;
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