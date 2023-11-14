package com.aryomtech.dhitifoundation.slider.Model;

import android.content.Context;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.aryomtech.dhitifoundation.R;
import com.astritveliu.boom.Boom;
import com.bumptech.glide.Glide;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImageTranscoderType;
import com.facebook.imagepipeline.core.MemoryChunkType;
import com.facebook.imagepipeline.request.ImageRequest;
import com.google.android.material.snackbar.Snackbar;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class SmooliderAdapter extends PagerAdapter {

    private final Context mContext;
    private final List<ModelSmoolider> feedItemList;

    public SmooliderAdapter(List<ModelSmoolider> feedItemList, Context mContext) {
        this.mContext = mContext;
        this.feedItemList = feedItemList;
        notifyDataSetChanged();
        Fresco.initialize(
                mContext,
                ImagePipelineConfig.newBuilder(mContext)
                        .setMemoryChunkType(MemoryChunkType.BUFFER_MEMORY)
                        .setImageTranscoderType(ImageTranscoderType.JAVA_TRANSCODER)
                        .experiment().setNativeCodeDisabled(true)
                        .build());
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @NonNull
    @Override
    public View instantiateItem(@NonNull ViewGroup container, int position) {
        final ModelSmoolider slider_data = feedItemList.get(position);

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_design, container, false);

        SimpleDraweeView img_slider = view.findViewById(R.id.img_slider);
        TextView txt_details = view.findViewById(R.id.textView110);
        TextView textView_des = view.findViewById(R.id.textView113);
        new Boom(txt_details);//optional

        txt_details.setText(slider_data.getHead_text());
        textView_des.setText(slider_data.getDes_text());

        Uri uri = Uri.parse(slider_data.getImage_url());
        ImageRequest request = ImageRequest.fromUri(uri);

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(img_slider.getController()).build();

        img_slider.setController(controller);
        /*try {
            Glide.with(mContext).asBitmap().load(slider_data.getImage_url())
                    .placeholder(R.drawable.ic_image_holder).into(img_slider);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        img_slider.setOnClickListener(v -> {
            //Slider action
            Toast.makeText(mContext, "Dhiti Foundation", Toast.LENGTH_SHORT).show();
        });

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return feedItemList.size();
    }

    @Override
    public void destroyItem (ViewGroup container, int position, @NonNull Object object){
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }
}
