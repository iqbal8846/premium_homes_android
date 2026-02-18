package www.dpremiumhomes.com.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import www.dpremiumhomes.com.MainActivity;
import www.dpremiumhomes.com.R;

public class FAQFragment extends Fragment {

    private int loadingTasks = 0;
    private boolean loaderHidden = false;


    private TextView ans1, ans2, ans3, gen, book, legal, title, titleHeader;
    private ImageView icon1, icon2, icon3;

    // General section
    private LinearLayout generalHeader, generalContent;
    private ImageView generalMenuIcon;

    public FAQFragment() {
        // Required empty public constructor
    }

    public static FAQFragment newInstance() {
        return new FAQFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_f_a_q, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // FAQ answers
        ans1 = view.findViewById(R.id.ans1);
        ans2 = view.findViewById(R.id.ans2);
        ans3 = view.findViewById(R.id.ans3);

        // FAQ icons
        icon1 = view.findViewById(R.id.plus_minus_icon1);
        icon2 = view.findViewById(R.id.plus_minus_icon2);
        icon3 = view.findViewById(R.id.plus_minus_icon3);

        setupToggle(ans1, icon1);
        setupToggle(ans2, icon2);
        setupToggle(ans3, icon3);

        // General section
        generalHeader = view.findViewById(R.id.section_general_header);
        generalContent = view.findViewById(R.id.section_general_content);
        generalMenuIcon = view.findViewById(R.id.menu);

        gen = view.findViewById(R.id.gen);
        book = view.findViewById(R.id.book);
        legal = view.findViewById(R.id.legal);
        title = view.findViewById(R.id.title);
        titleHeader = view.findViewById(R.id.titleHeader);

        setupGeneralToggle();
        clickListener();

        finishLoading();
    }

    private void setupToggle(final TextView answer, final ImageView icon) {
        if (answer == null || icon == null) return;

        View parent = (View) icon.getParent();
        if (parent != null) {
            parent.setOnClickListener(v -> {
                if (answer.getVisibility() == View.GONE) {
                    answer.setVisibility(View.VISIBLE);
                    icon.setImageResource(R.drawable.ic_minus);
                } else {
                    answer.setVisibility(View.GONE);
                    icon.setImageResource(R.drawable.ic_plus);
                }
            });
        }
    }

    private void setupGeneralToggle() {
        generalHeader.setOnClickListener(v -> {
            if (generalContent.getVisibility() == View.GONE) {
                generalContent.setVisibility(View.VISIBLE);
                generalMenuIcon.setImageResource(R.drawable.outline_close_24); // or arrow up
            } else {
                generalContent.setVisibility(View.GONE);
                generalMenuIcon.setImageResource(R.drawable.baseline_menu_24);
            }
        });
    }

    private void clickListener(){

        gen.setOnClickListener(v -> {

            if (ans2.getVisibility() == View.VISIBLE) {
                ans2.setVisibility(View.GONE);
                icon2.setImageResource(R.drawable.ic_plus);

            } else if (ans3.getVisibility() == View.VISIBLE)
            {
                ans3.setVisibility(View.GONE);
                icon3.setImageResource(R.drawable.ic_plus);
            }
            else {

                title.setText("General");
                ans1.setVisibility(View.VISIBLE);
                icon1.setImageResource(R.drawable.ic_minus); // or arrow up

            }

        });

        book.setOnClickListener(v ->{

            if (ans1.getVisibility() == View.VISIBLE) {
                ans1.setVisibility(View.GONE);
                icon1.setImageResource(R.drawable.ic_plus);
            } else if (ans3.getVisibility() == View.VISIBLE){
                ans3.setVisibility(View.GONE);
                icon3.setImageResource(R.drawable.ic_plus);
            }
            else {
                title.setText("Booking & Payment");
                ans2.setVisibility(View.VISIBLE);
                icon2.setImageResource(R.drawable.ic_minus); // or arrow up
            }

        });


        legal.setOnClickListener(v ->{

            if (ans1.getVisibility() == View.VISIBLE) {
                ans1.setVisibility(View.GONE);
                icon1.setImageResource(R.drawable.ic_plus);
            } else if (ans2.getVisibility() == View.VISIBLE){
                ans2.setVisibility(View.GONE);
                icon2.setImageResource(R.drawable.ic_plus);
            }
            else {
                title.setText("Legal & Documentation");
                ans3.setVisibility(View.VISIBLE);
                icon3.setImageResource(R.drawable.ic_minus); // or arrow up
            }

        });
    }

    // Loader helpers
    private void startLoading() {
        loadingTasks++;
        loaderHidden = false;
        if (isAdded()) {
            ((MainActivity) requireActivity()).showLoader();
        }
    }

    private void finishLoading() {
        loadingTasks--;
        if (loadingTasks <= 0 && !loaderHidden) {
            loaderHidden = true;
            if (isAdded()) {
                ((MainActivity) requireActivity()).hideLoader();
            }
        }
    }

    private void failLoading() {
        finishLoading();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isAdded()) ((MainActivity) requireActivity()).hideLoader();
    }
}
