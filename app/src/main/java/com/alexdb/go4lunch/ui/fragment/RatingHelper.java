package com.alexdb.go4lunch.ui.fragment;

import android.view.View;
import android.widget.ImageView;

public class RatingHelper {

    //[0,2.5[ No star, [2.5, 3.5[ = 1 stars, [3.5,4.5[ = 2 stars, [4.5,5] = 3 stars
    public static void displayStarsScheme(double rate, ImageView star1, ImageView star2, ImageView star3) {
        int rate_int = (int) Math.round(rate);

        switch (rate_int) {
            case 0:
            case 1:
            case 2:
                star1.setVisibility(View.GONE);
                star2.setVisibility(View.GONE);
                star3.setVisibility(View.GONE);
                break;
            case 3:
                star1.setVisibility(View.VISIBLE);
                star2.setVisibility(View.GONE);
                star3.setVisibility(View.GONE);
                break;
            case 4:
                star1.setVisibility(View.VISIBLE);
                star2.setVisibility(View.VISIBLE);
                star3.setVisibility(View.GONE);
                break;
            case 5:
                star1.setVisibility(View.VISIBLE);
                star2.setVisibility(View.VISIBLE);
                star3.setVisibility(View.VISIBLE);
                break;
        }
    }
}
