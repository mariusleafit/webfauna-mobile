package ch.leafit.webfauna.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import ch.leafit.webfauna.R;

/**
 * Created by marius on 13/08/14.
 */
public class ObservationTopField extends RelativeLayout{



    LayoutInflater mInflater;

    OnClickListener mBtnDateClickListener;
    OnClickListener mBtnLocationClickListener;
    OnClickListener mBtnFilesClickListener;


    public ObservationTopField(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);
        init();
    }
    public ObservationTopField(android.content.Context context, android.util.AttributeSet attrs) {
        super(context,attrs);
        mInflater = LayoutInflater.from(context);
        init();
    }

    public ObservationTopField(Context context, android.util.AttributeSet attrs, int defStyle) {
        super(context,attrs, defStyle);
        mInflater = LayoutInflater.from(context);
        init();
    }

    /**
     * the tag of the clicked view contains the TopField-instance
     * @param onClickListener
     */
    public void setOnBtnDateClickListener(OnClickListener onClickListener) {
        mBtnDateClickListener = onClickListener;
    }

    /**
     * the tag of the clicked view contains the TopField-instance
     * @param onClickListener
     */
    public void setOnBtnLocationClickListener(OnClickListener onClickListener) {
        mBtnLocationClickListener = onClickListener;
    }

    /**
     * the tag of the clicked view contains the TopField-instance
     * @param onClickListener
     */
    public void setOnBtnFilesClickListener(OnClickListener onClickListener) {
        mBtnFilesClickListener = onClickListener;
    }

    public void init() {
        View contentView =  mInflater.inflate(R.layout.observation_top_field,this,true);

        ImageButton btnDate = (ImageButton)contentView.findViewById(R.id.btnDate);
        btnDate.setTag(this);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBtnDateClickListener != null)
                    mBtnDateClickListener.onClick(v);
            }
        });

        ImageButton btnLocation = (ImageButton)contentView.findViewById(R.id.btnLocation);
        btnLocation.setTag(this);
        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBtnLocationClickListener != null)
                    mBtnLocationClickListener.onClick(v);
            }
        });

        ImageButton btnFiles = (ImageButton)contentView.findViewById(R.id.btnFiles);
        btnFiles.setTag(this);
        btnFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBtnFilesClickListener != null)
                    mBtnFilesClickListener.onClick(v);
            }
        });

    }
}
