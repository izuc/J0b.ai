package ai.j0b.view.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ai.j0b.databinding.DialogAboutMeBinding;
import ai.j0b.view.adapter.AboutMeAdapter;
import java.util.ArrayList;

public class AboutMeDialog extends Dialog {

    private final ArrayList<String> aboutMeList;
    private final AppCompatActivity context;
    private DialogAboutMeBinding dialogBinding;
    private final OnAboutMeSelect listener;

    public interface OnAboutMeSelect {
        void onAboutMeSelect(String str);
    }

    public AboutMeDialog(AppCompatActivity context, ArrayList<String> aboutMeList, OnAboutMeSelect listener) {
        super(context);
        this.context = context;
        this.aboutMeList = aboutMeList;
        this.listener = listener;
    }

    public ArrayList<String> getAboutMeList() {
        return aboutMeList;
    }

    public OnAboutMeSelect getListener() {
        return listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dialogBinding = DialogAboutMeBinding.inflate(LayoutInflater.from(context));
        setContentView(dialogBinding.getRoot());

        dialogBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        dialogBinding.recycleView.setAdapter(new AboutMeAdapter(context, aboutMeList, new AboutMeAdapter.OnClick() {
            @Override
            public void onCloseClick(String text) {
                dismiss();
                listener.onAboutMeSelect(text);
            }
        }));

        dialogBinding.recycleView.setNestedScrollingEnabled(false);
        dialogBinding.recycleView.setLayoutManager(new LinearLayoutManager(context));

        setCanceledOnTouchOutside(true);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(layoutParams);

        getWindow().setBackgroundDrawable(new ColorDrawable(0));
    }
}