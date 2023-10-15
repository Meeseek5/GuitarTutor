package com.example.guitartutor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.animation.DecelerateInterpolator;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.IOException;


public class FingerPlacementSecondFragment extends Fragment implements View.OnClickListener{

    //FragmentActivity --> AppCompatActivity

    private View thumbView_a, thumbView_am, thumbView_am7, thumbView_am7g,
            thumbView_c, thumbView_c7, thumbView_cadd9, thumbView_cm7,
            thumbView_d, thumbView_d7, thumbView_dm, thumbView_dm7,
            thumbView_dm7g, thumbView_e, thumbView_e7, thumbView_em,
            thumbView_em7, thumbView_em7b, thumbView_f, thumbView_fm7,
            thumbView_g, thumbView_g7;

    private View[] viewArray;

    private FingerPlacementFirstFragment mFragment;
    private FragmentManager mFragmentMgr;

    // Hold A reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator currentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int shortAnimationDuration;

    private final int[] mLargeDrawables = new int[]{R.drawable.a_chord,
            R.drawable.am_chord, R.drawable.am7_chord, R.drawable.am7g_chord,
            R.drawable.c_chord, R.drawable.c7_chord, R.drawable.cadd9_chord,
            R.drawable.cm7_chord, R.drawable.d_chord, R.drawable.d7_chord, R.drawable.dm_chord,
            R.drawable.dm7_chord, R.drawable.dm7g_chord, R.drawable.e_chord, R.drawable.e7_chord,
            R.drawable.em_chord, R.drawable.em7_chord, R.drawable.em7b_chord, R.drawable.f_chord,
            R.drawable.fm7_chord, R.drawable.g_chord, R.drawable.g7_chord};

    // 建立Media Player
    MediaPlayer mp = new MediaPlayer();

    boolean isZoom = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve and cache the system's default "short" animation time.
        shortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_second_finger_placement, container, false);

        //return super.onCreateView(inflater, container, savedInstanceState);
        //return inflater.inflate(R.layout.fragment_finger_placement, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        thumbView_a = (ImageButton)getView().findViewById(R.id.thumb_button_a);
        thumbView_am = (ImageButton)getView().findViewById(R.id.thumb_button_am);
        thumbView_am7 = (ImageButton)getView().findViewById(R.id.thumb_button_am7);
        thumbView_am7g = (ImageButton)getView().findViewById(R.id.thumb_button_am7g);
        thumbView_c = (ImageButton)getView().findViewById(R.id.thumb_button_c);
        thumbView_c7 = (ImageButton)getView().findViewById(R.id.thumb_button_c7);
        thumbView_cadd9 = (ImageButton)getView().findViewById(R.id.thumb_button_cadd9);
        thumbView_cm7 = (ImageButton)getView().findViewById(R.id.thumb_button_cm7);
        thumbView_d = (ImageButton)getView().findViewById(R.id.thumb_button_d);
        thumbView_d7 = (ImageButton)getView().findViewById(R.id.thumb_button_d7);
        thumbView_dm = (ImageButton)getView().findViewById(R.id.thumb_button_dm);
        thumbView_dm7 = (ImageButton)getView().findViewById(R.id.thumb_button_dm7);
        thumbView_dm7g = (ImageButton)getView().findViewById(R.id.thumb_button_dm7g);
        thumbView_e = (ImageButton)getView().findViewById(R.id.thumb_button_e);
        thumbView_e7 = (ImageButton)getView().findViewById(R.id.thumb_button_e7);
        thumbView_em = (ImageButton)getView().findViewById(R.id.thumb_button_em);
        thumbView_em7 = (ImageButton)getView().findViewById(R.id.thumb_button_em7);
        thumbView_em7b = (ImageButton)getView().findViewById(R.id.thumb_button_em7b);
        thumbView_f = (ImageButton)getView().findViewById(R.id.thumb_button_f);
        thumbView_fm7 = (ImageButton)getView().findViewById(R.id.thumb_button_fm7);
        thumbView_g = (ImageButton)getView().findViewById(R.id.thumb_button_g);
        thumbView_g7 = (ImageButton)getView().findViewById(R.id.thumb_button_g7);

        thumbView_a.setOnClickListener(this);
        thumbView_am.setOnClickListener(this);
        thumbView_am7.setOnClickListener(this);
        thumbView_am7g.setOnClickListener(this);
        thumbView_c.setOnClickListener(this);
        thumbView_c7.setOnClickListener(this);
        thumbView_cadd9.setOnClickListener(this);
        thumbView_cm7.setOnClickListener(this);
        thumbView_d.setOnClickListener(this);
        thumbView_d7.setOnClickListener(this);
        thumbView_dm.setOnClickListener(this);
        thumbView_dm7.setOnClickListener(this);
        thumbView_dm7g.setOnClickListener(this);
        thumbView_e.setOnClickListener(this);
        thumbView_e7.setOnClickListener(this);
        thumbView_em.setOnClickListener(this);
        thumbView_em7.setOnClickListener(this);
        thumbView_em7b.setOnClickListener(this);
        thumbView_f.setOnClickListener(this);
        thumbView_fm7.setOnClickListener(this);
        thumbView_g.setOnClickListener(this);
        thumbView_g7.setOnClickListener(this);

        viewArray = new View[] {
                thumbView_a, thumbView_am, thumbView_am7, thumbView_am7g,
                thumbView_c, thumbView_c7, thumbView_cadd9, thumbView_cm7,
                thumbView_d, thumbView_d7, thumbView_dm, thumbView_dm7,
                thumbView_dm7g, thumbView_e, thumbView_e7, thumbView_em,
                thumbView_em7, thumbView_em7b, thumbView_f, thumbView_fm7,
                thumbView_g, thumbView_g7};

    }

    @Override
    public void onStop() {
        super.onStop();
        mp.release();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.thumb_button_a:
                zoomImageFromThumb(thumbView_a, mLargeDrawables[0]);
                Intent intent = new Intent("CHORD_A_ACTION");
                //intent.putExtra("sender_a","A");
                ((FingerPlacementActivity)getActivity()).sendBroadcast(intent);
                // 播放音訊
                stopAndPlay(R.raw.a_chord, mp);

                // 原本播放方式
                //MediaPlayer mp_a = MediaPlayer.create(getActivity(), R.raw.a_chord);
                //mp_a.start();
                break;
            case R.id.thumb_button_am:
                zoomImageFromThumb(thumbView_am, mLargeDrawables[1]);
                Intent intent_1 = new Intent("CHORD_AM_ACTION");
                ((FingerPlacementActivity)getActivity()).sendBroadcast(intent_1);
                stopAndPlay(R.raw.am_chord, mp);
                break;
            case R.id.thumb_button_am7:
                zoomImageFromThumb(thumbView_am7, mLargeDrawables[2]);
                Intent intent_2 = new Intent("CHORD_AM7_ACTION");
                ((FingerPlacementActivity)getActivity()).sendBroadcast(intent_2);
                stopAndPlay(R.raw.am7_chord, mp);
                break;
            case R.id.thumb_button_am7g:
                zoomImageFromThumb(thumbView_am7g, mLargeDrawables[3]);
                Intent intent_3 = new Intent("CHORD_AM7G_ACTION");
                ((FingerPlacementActivity)getActivity()).sendBroadcast(intent_3);
                stopAndPlay(R.raw.am7g_chord, mp);
                break;
            case R.id.thumb_button_c:
                zoomImageFromThumb(thumbView_c, mLargeDrawables[4]);
                Intent intent_4 = new Intent("CHORD_C_ACTION");
                ((FingerPlacementActivity)getActivity()).sendBroadcast(intent_4);
                stopAndPlay(R.raw.c_chord, mp);
                break;
            case R.id.thumb_button_c7:
                zoomImageFromThumb(thumbView_c7, mLargeDrawables[5]);
                Intent intent_5 = new Intent("CHORD_C7_ACTION");
                ((FingerPlacementActivity)getActivity()).sendBroadcast(intent_5);
                stopAndPlay(R.raw.c7_chord, mp);
                break;
            case R.id.thumb_button_cadd9:
                zoomImageFromThumb(thumbView_cadd9, mLargeDrawables[6]);
                Intent intent_6 = new Intent("CHORD_CADD9_ACTION");
                ((FingerPlacementActivity)getActivity()).sendBroadcast(intent_6);
                stopAndPlay(R.raw.cadd9_chord, mp);
                break;
            case R.id.thumb_button_cm7:
                zoomImageFromThumb(thumbView_cm7, mLargeDrawables[7]);
                Intent intent_7 = new Intent("CHORD_CM7_ACTION");
                ((FingerPlacementActivity)getActivity()).sendBroadcast(intent_7);
                stopAndPlay(R.raw.cm7_chord, mp);
                break;
            case R.id.thumb_button_d:
                zoomImageFromThumb(thumbView_d, mLargeDrawables[8]);
                Intent intent_8 = new Intent("CHORD_D_ACTION");
                ((FingerPlacementActivity)getActivity()).sendBroadcast(intent_8);
                stopAndPlay(R.raw.d_chord, mp);
                break;
            case R.id.thumb_button_d7:
                zoomImageFromThumb(thumbView_d7, mLargeDrawables[9]);
                Intent intent_9 = new Intent("CHORD_D7_ACTION");
                ((FingerPlacementActivity)getActivity()).sendBroadcast(intent_9);
                stopAndPlay(R.raw.d7_chord, mp);
                break;
            case R.id.thumb_button_dm:
                zoomImageFromThumb(thumbView_dm, mLargeDrawables[10]);
                Intent intent_10 = new Intent("CHORD_DM_ACTION");
                ((FingerPlacementActivity)getActivity()).sendBroadcast(intent_10);
                stopAndPlay(R.raw.dm_chord, mp);
                break;
            case R.id.thumb_button_dm7:
                zoomImageFromThumb(thumbView_dm7, mLargeDrawables[11]);
                Intent intent_11 = new Intent("CHORD_DM7_ACTION");
                ((FingerPlacementActivity)getActivity()).sendBroadcast(intent_11);
                stopAndPlay(R.raw.dm7_chord, mp);
                break;
            case R.id.thumb_button_dm7g:
                zoomImageFromThumb(thumbView_dm7g, mLargeDrawables[12]);
                Intent intent_12 = new Intent("CHORD_DM7G_ACTION");
                ((FingerPlacementActivity)getActivity()).sendBroadcast(intent_12);
                stopAndPlay(R.raw.dm7g_chord, mp);
                break;
            case R.id.thumb_button_e:
                zoomImageFromThumb(thumbView_e, mLargeDrawables[13]);
                Intent intent_13 = new Intent("CHORD_E_ACTION");
                ((FingerPlacementActivity)getActivity()).sendBroadcast(intent_13);
                stopAndPlay(R.raw.e_chord, mp);
                break;
            case R.id.thumb_button_e7:
                zoomImageFromThumb(thumbView_e7, mLargeDrawables[14]);
                Intent intent_14 = new Intent("CHORD_E7_ACTION");
                ((FingerPlacementActivity)getActivity()).sendBroadcast(intent_14);
                stopAndPlay(R.raw.e7_chord, mp);
                break;
            case R.id.thumb_button_em:
                zoomImageFromThumb(thumbView_em, mLargeDrawables[15]);
                Intent intent_15 = new Intent("CHORD_EM_ACTION");
                ((FingerPlacementActivity)getActivity()).sendBroadcast(intent_15);
                stopAndPlay(R.raw.em_chord, mp);
                break;
            case R.id.thumb_button_em7:
                zoomImageFromThumb(thumbView_em7, mLargeDrawables[16]);
                Intent intent_16 = new Intent("CHORD_EM7_ACTION");
                ((FingerPlacementActivity)getActivity()).sendBroadcast(intent_16);
                stopAndPlay(R.raw.em7_chord, mp);
                break;
            case R.id.thumb_button_em7b:
                zoomImageFromThumb(thumbView_em7b, mLargeDrawables[17]);
                Intent intent_17 = new Intent("CHORD_EM7B_ACTION");
                ((FingerPlacementActivity)getActivity()).sendBroadcast(intent_17);
                stopAndPlay(R.raw.em7b_chord, mp);
                break;
            case R.id.thumb_button_f:
                zoomImageFromThumb(thumbView_f, mLargeDrawables[18]);
                Intent intent_18 = new Intent("CHORD_F_ACTION");
                ((FingerPlacementActivity)getActivity()).sendBroadcast(intent_18);
                stopAndPlay(R.raw.f_chord, mp);
                break;
            case R.id.thumb_button_fm7:
                zoomImageFromThumb(thumbView_fm7, mLargeDrawables[19]);
                Intent intent_19 = new Intent("CHORD_FM7_ACTION");
                ((FingerPlacementActivity)getActivity()).sendBroadcast(intent_19);
                stopAndPlay(R.raw.fm7_chord, mp);
                break;
            case R.id.thumb_button_g:
                zoomImageFromThumb(thumbView_g, mLargeDrawables[20]);
                Intent intent_20 = new Intent("CHORD_G_ACTION");
                ((FingerPlacementActivity)getActivity()).sendBroadcast(intent_20);
                stopAndPlay(R.raw.g_chord, mp);
                break;
            case R.id.thumb_button_g7:
                zoomImageFromThumb(thumbView_g7, mLargeDrawables[21]);
                Intent intent_21 = new Intent("CHORD_G7_ACTION");
                ((FingerPlacementActivity)getActivity()).sendBroadcast(intent_21);
                stopAndPlay(R.raw.g7_chord, mp);
                break;
        }
    }

    /**
     * 縮圖放大縮回
     */
    private void zoomImageFromThumb(final View thumbView, int imageResId) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        // 取得縮圖
        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) getView().findViewById(
                R.id.expanded_image);
        // 取得放大圖
        expandedImageView.setImageResource(imageResId);

        // 用來計算縮圖和放大圖的座標
        // Calculate the starting and ending bounds for the zoomed-in image.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // 以整個螢幕為基準換算縮圖與放大圖的偏移
        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        getView().findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);

        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);


        // 調整長寬比
        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // 隱藏縮圖
        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        //thumbView.setAlpha(0f);
        for(int i = 0; i < 22; i++) {
            viewArray[i].setAlpha(0f);
        }
        // 顯示放大圖
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                       startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView,
                       View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;

        // 圖片縮小
        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }

                // reset mediaplayer
                mp.reset();

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator.ofFloat(expandedImageView,
                                View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //thumbView.setAlpha(1f);
                        for(int i = 0; i < 22; i++) {
                            viewArray[i].setAlpha(1f);
                        }

                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }
                });
                set.start();
                currentAnimator = set;
            }
        });




    }

    // 多個音檔共用Media Player物件使用的方法，關閉前一個音檔
    private void stopAndPlay(int rawId, MediaPlayer mediaPlayer) {
        mediaPlayer.reset();
        AssetFileDescriptor afd = this.getResources().openRawResourceFd(rawId);
        try {
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();

    }




}