using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.Content.Res;
using Android.Graphics;
using Android.OS;
using Android.Runtime;
using Android.Support.V4.Content;
using Android.Support.V4.View;
using Android.Util;
using Android.Views;
using Android.Views.InputMethods;
using Android.Widget;
using Java.Lang;

namespace RecommendationSystemsClient.Layouts
{
    public class MaterialEditText : FrameLayout
    {
        protected InputMethodManager inputMethodManager;

        protected TextView label;
        protected View card;
        protected ImageView image;
        protected EditText editText;
        protected ViewGroup editTextLayout;

        protected int labelTopMargin = -1;
        protected bool expanded = false;

        protected int ANIMATION_DURATION = -1;
        protected bool OPEN_KEYBOARD_ON_FOCUS = true;
        protected Color labelColor = Color.White;
        protected int imageDrawableId = -1;
        protected int cardCollapsedHeight = -1;
        protected bool hasFocus = false;
        protected Color backgroundColor = Color.White;

        protected float reducedScale = 0.2f;

        protected void Init()
        {
            inputMethodManager = (InputMethodManager)Context.GetSystemService(Context.InputMethodService);
        }

        public void toggle()
        {
            if (expanded)
            {
                Reduce();
            }
            else
            {
                Expand();
            }
        }

        public void Reduce()
        {
            if (expanded)
            {
                var heightInitial = Context.Resources.GetDimensionPixelOffset(Resource.Dimension.met_cardHeight_final);

                ViewCompat.Animate(label)
                    .Alpha(1)
                    .ScaleX(1)
                    .ScaleY(1)
                    .TranslationY(0)
                    .SetDuration(ANIMATION_DURATION);

                ViewCompat.Animate(image)
                    .Alpha(0)
                    .ScaleX(0.4f)
                    .ScaleY(0.4f)
                    .SetDuration(ANIMATION_DURATION);


                ViewCompat.Animate(editText)
                    .Alpha(1f)
                    .SetUpdateListener(new ViewCompatListener
                    {
                        Card = card,
                        HeightInit = heightInitial,
                        CardCollapsedHeight = cardCollapsedHeight
                    })
                    .SetDuration(ANIMATION_DURATION)
                    .SetListener(new ViewCompatAnimationListener
                    {
                        EditText = editText,
                        Expanded = expanded
                    });

                ViewCompat.Animate(card)
                    .ScaleY(reducedScale)
                    .SetDuration(ANIMATION_DURATION);

                if (editText.HasFocus)
                {
                    inputMethodManager.HideSoftInputFromWindow(editText.WindowToken, 0);
                    editText.ClearFocus();
                }

                expanded = false;
            }

        }

        public void Expand()
        {
            if (!expanded)
            {
                ViewCompat.Animate(editText)
                    .Alpha(1f)
                    .SetDuration(ANIMATION_DURATION);

                ViewCompat.Animate(card)
                    .ScaleY(1f)
                    .SetDuration(ANIMATION_DURATION);

                ViewCompat.Animate(label)
                    .Alpha(0.4f)
                    .ScaleX(0.7f)
                    .ScaleY(0.7f)
                    .TranslationY(-labelTopMargin)
                    .SetDuration(ANIMATION_DURATION);

                ViewCompat.Animate(image)
                    .Alpha(1f)
                    .ScaleX(1f)
                    .ScaleY(1f)
                    .SetDuration(ANIMATION_DURATION);

                editText?.RequestFocus();

                if (OPEN_KEYBOARD_ON_FOCUS)
                {
                    inputMethodManager.ShowSoftInput(editText, ShowFlags.Implicit);
                }

                expanded = true;
            }
        }

        protected void HandleAttributes(Context context, IAttributeSet attrs)
        {
            try
            {
                var styledAttrs = context.ObtainStyledAttributes(attrs, Resource.Styleable.MaterialEditText);
                ANIMATION_DURATION = styledAttrs.GetInteger(Resource.Styleable.MaterialEditText_met_animationDuration, 400);
                OPEN_KEYBOARD_ON_FOCUS = styledAttrs.GetBoolean(Resource.Styleable.MaterialEditText_met_openKeyboardOnFocus, false);
                labelColor = styledAttrs.GetColor(Resource.Styleable.MaterialEditText_met_labelColor, -1);
                imageDrawableId = styledAttrs.GetResourceId(Resource.Styleable.MaterialEditText_met_image, -1);
                cardCollapsedHeight = styledAttrs.GetDimensionPixelOffset(Resource.Styleable.MaterialEditText_met_cardCollapsedHeight, 
                    context.Resources.GetDimensionPixelOffset(Resource.Dimension.met_cardHeight_initial));
                hasFocus = styledAttrs.GetBoolean(Resource.Styleable.MaterialEditText_met_hasFocus, false);
                backgroundColor = styledAttrs.GetColor(Resource.Styleable.MaterialEditText_met_backgroundColor, -1);

                styledAttrs.Recycle();
            }
            catch (System.Exception e)
            {
                System.Diagnostics.Debug.WriteLine(e.Message);
            }
        }

        protected EditText FindEditTextChild()
        {
            if (ChildCount > 0 && GetChildAt(0) is EditText)
            {
                return (EditText)GetChildAt(0);
            }
            return null;
        }

        protected override void OnFinishInflate()
        {
            base.OnFinishInflate();
            editText = FindEditTextChild();
            if (editText == null)
            {
                return;
            }

            AddView(LayoutInflater.From(Context).Inflate(Resource.Layout.MaterialEditText, this, false));

            editTextLayout = FindViewById<ViewGroup>(Resource.Id.mtf_editTextLayout);
            RemoveView(editText);
            editTextLayout.AddView(editText);

            label = FindViewById<TextView>(Resource.Id.mtf_label);
            label.PivotX = 0;
            label.PivotY = 0;

            if (editText.Hint != null)
            {
                label.Text = editText.Hint;
                editText.Hint = "";
            }

            card = FindViewById(Resource.Id.mtf_card);

            if (backgroundColor != -1)
            {
                card.SetBackgroundColor(backgroundColor);
            }

            var expandedHeight = Context.Resources.GetDimensionPixelOffset(Resource.Dimension.met_cardHeight_final);
            var reducedHeight = cardCollapsedHeight;

            reducedScale = (float)(reducedHeight * 1.0 / expandedHeight);
            card.ScaleY = reducedScale;
            card.PivotY = expandedHeight;

            image = FindViewById<ImageView>(Resource.Id.mtf_image);
            image.Alpha = 0;
            image.ScaleX = 0.4f;
            image.ScaleY = 0.4f;

            editText.Alpha = 0;

            editText.SetBackgroundColor(Color.Transparent);
            labelTopMargin = ((LayoutParams)label.LayoutParameters)?.TopMargin ?? 0;

            CustomizeFromAttributes();

            Click += (o, e) => { toggle(); };

            if (hasFocus) RequestFocus();
        }

        protected void CustomizeFromAttributes()
        {
            if (labelColor != -1)
            {
                label.SetTextColor(labelColor);
            }

            if (imageDrawableId != -1)
            {
                image.SetImageDrawable(ContextCompat.GetDrawable(Context, imageDrawableId));
            }
        }

        protected MaterialEditText(IntPtr javaReference, JniHandleOwnership transfer) : base(javaReference, transfer)
        {
            Init();
        }

        public MaterialEditText(Context context) : base(context)
        {
            Init();
        }

        public MaterialEditText(Context context, IAttributeSet attrs) : base(context, attrs)
        {
            HandleAttributes(context, attrs);
            Init();
        }

        public MaterialEditText(Context context, IAttributeSet attrs, int defStyleAttr) : base(context, attrs, defStyleAttr)
        {
            HandleAttributes(context, attrs);
            Init();
        }

        public MaterialEditText(Context context, IAttributeSet attrs, int defStyleAttr, int defStyleRes) : base(context, attrs, defStyleAttr, defStyleRes)
        {
            HandleAttributes(context, attrs);
            Init();
        }
    }

    public class ViewCompatListener : Java.Lang.Object, IViewPropertyAnimatorUpdateListener
    {
        public View Card { get; set; }
        public int HeightInit { get; set; }
        public int CardCollapsedHeight { get; set; }

        public void OnAnimationUpdate(View view)
        {
            var value = view.Alpha;
            Card.LayoutParameters.Height = (int)(value * (HeightInit - CardCollapsedHeight) + CardCollapsedHeight);
            Card.RequestLayout();
        }
    }

    public class ViewCompatAnimationListener : Java.Lang.Object, IViewPropertyAnimatorListener
    {
        public bool Expanded { get; set; }
        public EditText EditText { get; set; }

        public void OnAnimationEnd(View view)
        {
            if (Expanded)
            {
                EditText.Visibility = ViewStates.Visible;
            }
        }

        public void OnAnimationStart(View view)
        {
            if (!Expanded)
            {
                EditText.Visibility = ViewStates.Invisible;
            }
        }
        
        public void OnAnimationCancel(View view) { }
    }
}