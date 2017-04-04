package dubsapp.core.widget;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import dubsapp.R;
import dubsapp.core.abs.A;
import dubsapp.core.widget.abs.AbstractWidgetWithText;

public class ButtonWidget extends AbstractWidgetWithText {

    protected final LinearLayout mLayoutView;
    private final ImageView mImageViewLeft;


    protected ButtonWidget(Context context, AttributeSet attrs, @LayoutRes int resLayoutId) {
        super(context, attrs, resLayoutId);
        mImageViewLeft = (ImageView) getView(R.id.core_widget__button__image_left);
        mLayoutView = (LinearLayout) getView(R.id.core_widget__button__layout);
        setOnClickListener(mClickListener);
    }

    public ButtonWidget(Context context, AttributeSet attrs) {
        this(context, attrs, R.layout.core_widget__button);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            log().debug("OnClickListener.viewId={}", v.getId());
            v.startAnimation(getAttrAnimation());
            onUserAction();
        }
    };


    @Override
    protected void onCreate() {
        setWidgetValue(getAttrCaption());
        setColorEx(mLayoutView, getColor(), getBorderColor());

        if (getImageLeftResId() != A.Type.RES_IS_NULL) {
            mImageViewLeft.setVisibility(VISIBLE);
            mImageViewLeft.setImageResource(getImageLeftResId());
            mLayoutView.setOrientation(HORIZONTAL);
        }
        if (getImageTopResId() != A.Type.RES_IS_NULL) {
            setDrawables__Top(getImageTopResId());
        }
    }

    @Override
    protected void onSetWidgetValue(String newValue) {
        if (A.Type.STRING_FALSE_DB.equals(newValue)) {
            log().todo("onSetWidgetValue!!!tmp log!!!");
            setVisibility(GONE);
            return;
        }
        super.onSetWidgetValue(newValue);
    }

    protected void onUserAction() {
        log().debug("onUserAction");
        changeWidgetForce();
        //TODO: ж ;)
        try {
            super.getFrame().change(this);
        } catch (Exception ex) {
            log().error(ex  );
        }
    }

}
