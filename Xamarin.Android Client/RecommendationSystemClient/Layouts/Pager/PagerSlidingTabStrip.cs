using System;
using Android.Widget;
using Android.Database;
using Android.Content.Res;
using Android.Graphics;
using Android.Support.V4.View;
using Android.Util;
using Android.Content;
using Android.Views;
using Android.OS;

namespace RecommendationSystemClient.Layouts.Pager
{
    public class PagerSlidingTabStrip : HorizontalScrollView, ViewPager.IOnPageChangeListener, ViewTreeObserver.IOnGlobalLayoutListener
    {
        public ViewPager.IOnPageChangeListener OnPageChangeListener { get; set; }

        public IOnTabReselectedListener OnTabReselectedListener { get; set; }

        private static readonly int[] Attrs = {
            Android.Resource.Attribute.TextColorPrimary,
            Android.Resource.Attribute.TextSize,
            Android.Resource.Attribute.TextColor,
            Android.Resource.Attribute.Padding,
            Android.Resource.Attribute.PaddingLeft,
            Android.Resource.Attribute.PaddingRight
        };

        //These indexes must be related with the ATTR array above
        private const int TextColorPrimaryIndex = 0;
        private const int TextSizeIndex = 1;
        private const int TextColorIndex = 2;
        private const int PaddingIndex = 3;
        private const int PaddingLeftIndex = 4;
        private const int PaddingRightIndex = 5;

        private readonly LinearLayout.LayoutParams _defaultTabLayoutParams;
        private readonly LinearLayout.LayoutParams _expandedTabLayoutParams;

        private readonly LinearLayout _tabsContainer;
        private ViewPager _pager;

        private int _tabCount;

        private int _currentPosition;
        private float _currentPositionOffset;

        private readonly Paint _rectPaint;
        private readonly Paint _dividerPaint;

        private int _indicatorColor;

        public int IndicatorColor
        {
            get => _indicatorColor;
            set
            {
                _indicatorColor = value;
                Invalidate();
            }
        }
        
        private int _indicatorHeight = 2;
        public int IndicatorHeight
        {
            get => _indicatorHeight;
            set
            {
                _indicatorHeight = value;
                Invalidate();
            }
        }

        private int _underlineHeight;
        public int UnderlineHeight
        {
            get => _underlineHeight;
            set
            {
                _underlineHeight = value;
                Invalidate();
            }
        }
        private int _underlineColor;
        public int UnderlineColor
        {
            get => _underlineColor;
            set
            {
                _underlineColor = value;
                Invalidate();
            }
        }

        private int _dividerWidth;
        public int DividerWidth
        {
            get => _dividerWidth;
            set
            {
                _dividerWidth = value;
                Invalidate();
            }
        }

        private int _dividerPadding;
        public int DividerPadding
        {
            get => _dividerPadding;
            set
            {
                _dividerPadding = value;
                Invalidate();
            }
        }
        private int _dividerColor;
        public int DividerColor
        {
            get => _dividerColor;
            set
            {
                _dividerColor = value;
                Invalidate();
            }
        }

        private int _tabPadding = 12;
        public int TabPaddingLeftRight
        {
            get => _tabPadding;
            set
            {
                _tabPadding = value;
                UpdateTabStyles();
            }
        }
        private int _tabTextSize = 14;
        public int TabTextSize
        {
            get => _tabTextSize;
            set
            {
                _tabTextSize = value;
                UpdateTabStyles();
            }
        }

        private int _textAlpha = 150;
        public int TextAlpha
        {
            get => _textAlpha;
            set
            {
                _textAlpha = value;
                Invalidate();
            }
        }

        private ColorStateList _tabTextColorSelected;
        public ColorStateList TabTextColorSelected
        {
            get => _tabTextColorSelected;
            set
            {
                _tabTextColorSelected = value;
                Invalidate();
            }
        }

        private ColorStateList _tabTextColor;
        public ColorStateList TabTextColor
        {
            get => _tabTextColor;
            set
            {
                _tabTextColor = value;
                UpdateTabStyles();
            }
        }

        public void SetTabTextColor(int textColor)
        {
            TabTextColor = GetColorStateList(textColor);
        }

        public void SetTextColorResource(int resId)
        {
            TabTextColor = GetColorStateList(Resources.GetColor(resId));
        }

        public void SetTabTextColorListResource(int resId)
        {
            TabTextColor = Resources.GetColorStateList(resId);
        }


        private ColorStateList GetColorStateList(int textColor)
        {
            return new ColorStateList(new int[][] { new int[] { } }, new int[] { textColor });
        }

        private int _paddingLeft;
        private int _paddingRight;

        private bool _shouldExpand;
        public bool ShouldExpand
        {
            get => _shouldExpand;
            set
            {
                _shouldExpand = value;
                if (_pager != null)
                    RequestLayout();
            }
        }
        private bool _textAllCaps = true;
        public bool TextAllCaps
        {
            get => _textAllCaps;
            set => _textAllCaps = value;
        }
        private bool _isPaddingMiddle;
        public bool IsPaddingMiddle
        {
            get => _isPaddingMiddle;
            set
            {
                _isPaddingMiddle = value;
                Invalidate();
            }
        }

        private Typeface _tabTypeface;
        public void SetTypeface(Typeface typeFace, TypefaceStyle style)
        {
            _tabTypeface = typeFace;
            _tabTypefaceSelectedStyle = style;
            UpdateTabStyles();
        }

        private readonly TypefaceStyle _tabTypefaceStyle;
        private TypefaceStyle _tabTypefaceSelectedStyle;

        private int _scrollOffset;
        public int ScrollOffset
        {
            get => _scrollOffset;
            set
            {
                _scrollOffset = value;
                Invalidate();
            }
        }
        private int _lastScrollX;

        public int TabBackground { get; set; } = Resource.Drawable.tabBackground;

        private readonly PagerAdapterObserver _adapterObserver;


        public PagerSlidingTabStrip(Context context)
            : this(context, null)
        {
        }

        public PagerSlidingTabStrip(Context context, IAttributeSet attrs)
            : this(context, attrs, 0)
        {
        }

        public PagerSlidingTabStrip(Context context, IAttributeSet attrs, int defStyle)
            : base(context, attrs, defStyle)
        {
            MyOnGlobalLayoutListner = new MyOnGlobalLayoutListener(this);
            _adapterObserver = new PagerAdapterObserver(this);
            FillViewport = true;
            VerticalScrollBarEnabled = false;
            HorizontalScrollBarEnabled = false;
            SetWillNotDraw(false);
            _tabsContainer = new LinearLayout(context);
            _tabsContainer.Orientation = Android.Widget.Orientation.Horizontal;
            _tabsContainer.LayoutParameters = new LayoutParams(ViewGroup.LayoutParams.MatchParent, ViewGroup.LayoutParams.MatchParent);
            AddView(_tabsContainer);

            var dm = Resources.DisplayMetrics;
            _scrollOffset = (int)TypedValue.ApplyDimension(ComplexUnitType.Dip, _scrollOffset, dm);
            _indicatorHeight = (int)TypedValue.ApplyDimension(ComplexUnitType.Dip, _indicatorHeight, dm);
            _underlineHeight = (int)TypedValue.ApplyDimension(ComplexUnitType.Dip, _underlineHeight, dm);
            _dividerPadding = (int)TypedValue.ApplyDimension(ComplexUnitType.Dip, _dividerPadding, dm);
            _tabPadding = (int)TypedValue.ApplyDimension(ComplexUnitType.Dip, _tabPadding, dm);
            _dividerWidth = (int)TypedValue.ApplyDimension(ComplexUnitType.Dip, _dividerWidth, dm);
            _tabTextSize = (int)TypedValue.ApplyDimension(ComplexUnitType.Sp, _tabTextSize, dm);

            //get system attrs (android:textSize and android:textColor)
            var a = context.ObtainStyledAttributes(attrs, Attrs);
            _tabTextSize = a.GetDimensionPixelSize(TextSizeIndex, _tabTextSize);
            var colorStateList = a.GetColorStateList(TextColorIndex);
            var textPrimaryColor = a.GetColor(TextColorPrimaryIndex, Android.Resource.Color.White);
            
            _underlineColor = textPrimaryColor;
            _dividerColor = textPrimaryColor;
            _indicatorColor = textPrimaryColor;

            var padding = a.GetDimensionPixelSize(PaddingIndex, 0);
            _paddingLeft = padding > 0 ? padding : a.GetDimensionPixelSize(PaddingLeftIndex, 0);
            _paddingRight = padding > 0 ? padding : a.GetDimensionPixelSize(PaddingRightIndex, 0);
            
            a = context.ObtainStyledAttributes(attrs, Resource.Styleable.MaterialPager);
            _indicatorColor = a.GetColor(Resource.Styleable.MaterialPager_pIndicatorColor, _indicatorColor);
            _underlineColor = a.GetColor(Resource.Styleable.MaterialPager_pUnderlineColor, _underlineColor);
            _dividerColor = a.GetColor(Resource.Styleable.MaterialPager_pDividerColor, _dividerColor);
            _dividerWidth = a.GetDimensionPixelSize(Resource.Styleable.MaterialPager_pDividerWidth, _dividerWidth);
            _indicatorHeight = a.GetDimensionPixelSize(Resource.Styleable.MaterialPager_pIndicatorHeight, _indicatorHeight);
            _underlineHeight = a.GetDimensionPixelSize(Resource.Styleable.MaterialPager_pUnderlineHeight, _underlineHeight);
            _dividerPadding = a.GetDimensionPixelSize(Resource.Styleable.MaterialPager_pDividerPadding, _dividerPadding);
            _tabPadding = a.GetDimensionPixelSize(Resource.Styleable.MaterialPager_pTabPaddingLeftRight, _tabPadding);
            TabBackground = a.GetResourceId(Resource.Styleable.MaterialPager_pTabBackground, TabBackground);
            _shouldExpand = a.GetBoolean(Resource.Styleable.MaterialPager_pShouldExpand, _shouldExpand);
            _scrollOffset = a.GetDimensionPixelSize(Resource.Styleable.MaterialPager_pScrollOffset, _scrollOffset);
            _textAllCaps = a.GetBoolean(Resource.Styleable.MaterialPager_pTextAllCaps, _textAllCaps);
            _isPaddingMiddle = a.GetBoolean(Resource.Styleable.MaterialPager_pPaddingMiddle, _isPaddingMiddle);
            _tabTypefaceStyle = (TypefaceStyle)a.GetInt(Resource.Styleable.MaterialPager_pTextStyle, (int)TypefaceStyle.Bold);
            _tabTypefaceSelectedStyle = (TypefaceStyle)a.GetInt(Resource.Styleable.MaterialPager_pTextSelectedStyle, (int)TypefaceStyle.Bold);
            _tabTextColorSelected = a.GetColorStateList(Resource.Styleable.MaterialPager_pTextColorSelected);
            _textAlpha = a.GetInt(Resource.Styleable.MaterialPager_pTextAlpha, _textAlpha);
            a.Recycle();

            _tabTextColor = colorStateList ?? GetColorStateList(Color.Argb(_textAlpha,
                                Color.GetRedComponent(textPrimaryColor),
                                Color.GetGreenComponent(textPrimaryColor),
                                Color.GetBlueComponent(textPrimaryColor)));

            _tabTextColorSelected = _tabTextColorSelected ?? GetColorStateList(textPrimaryColor);
            
            SetMarginBottomTabContainer();

            _rectPaint = new Paint {AntiAlias = true};
            _rectPaint.SetStyle(Paint.Style.Fill);

            _dividerPaint = new Paint
            {
                AntiAlias = true,
                StrokeWidth = _dividerWidth
            };

            _defaultTabLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WrapContent, ViewGroup.LayoutParams.MatchParent);
            _expandedTabLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MatchParent, 1.0f);
        }


        private void SetMarginBottomTabContainer()
        {
            var mlp = (MarginLayoutParams)_tabsContainer.LayoutParameters;
            var bottomMargin = _indicatorHeight >= _underlineHeight ? _indicatorHeight : _underlineHeight;
            mlp.SetMargins(mlp.LeftMargin, mlp.TopMargin, mlp.RightMargin, bottomMargin);
            _tabsContainer.LayoutParameters = mlp;
        }

        public void SetViewPager(ViewPager pager)
        {
            _pager = pager;
            if (pager.Adapter == null)
            {
                throw new ArgumentNullException("ViewPager does not have adapter instance.");
            }

            pager.SetOnPageChangeListener(this);
            pager.Adapter.RegisterDataSetObserver(_adapterObserver);
            _adapterObserver.IsAttached = true;
            NotifyDataSetChanged();
        }

        public void NotifyDataSetChanged()
        {
            _tabsContainer.RemoveAllViews();
            _tabCount = _pager.Adapter.Count;
            for (var i = 0; i < _tabCount; i++)
            {
                View tabView;
                tabView = LayoutInflater.From(Context).Inflate(Resource.Layout.Tab, this, false);

                var title = _pager.Adapter.GetPageTitle(i);

                AddTab(i, title, tabView);
            }

            UpdateTabStyles();

            ViewTreeObserver.AddOnGlobalLayoutListener(MyOnGlobalLayoutListner);

        }

        protected MyOnGlobalLayoutListener MyOnGlobalLayoutListner { get; set; }

        protected class MyOnGlobalLayoutListener : Java.Lang.Object, ViewTreeObserver.IOnGlobalLayoutListener
        {
            readonly PagerSlidingTabStrip _strip;
            public MyOnGlobalLayoutListener(PagerSlidingTabStrip strip)
            {
                _strip = strip;
            }
            #region IOnGlobalLayoutListener implementation
            public void OnGlobalLayout()
            {

                _strip.RemoveGlobals();
            }
            #endregion

        }

        private void RemoveGlobals()
        {
            if ((int)Build.VERSION.SdkInt < 16)
            {
                ViewTreeObserver.RemoveGlobalOnLayoutListener(MyOnGlobalLayoutListner);
            }
            else
            {
                ViewTreeObserver.RemoveOnGlobalLayoutListener(MyOnGlobalLayoutListner);
            }
        }

        private void AddTab(int position, string title, View tabView)
        {
            var textView = tabView.FindViewById<TextView>(Resource.Id.psts_tab_title);
            if (textView != null)
            {
                if (title != null)
                {
                    textView.Text = title;
                }
            }

            tabView.Focusable = true;

            tabView.Click += (object sender, EventArgs e) =>
            {
                if (_pager.CurrentItem != position)
                {
                    var tab = _tabsContainer.GetChildAt(_pager.CurrentItem);
                    NotSelected(tab);
                    _pager.SetCurrentItem(position, true);
                }
                else
                {
                    OnTabReselectedListener?.OnTabReselected(position);
                }
            };

            _tabsContainer.AddView(tabView, position, _shouldExpand ? _expandedTabLayoutParams : _defaultTabLayoutParams);
        }

        private void UpdateTabStyles()
        {
            for (var i = 0; i < _tabCount; i++)
            {
                var v = _tabsContainer.GetChildAt(i);
                if (v == null)
                    continue;
                v.SetBackgroundResource(TabBackground);
                v.SetPadding(_tabPadding, v.PaddingTop, _tabPadding, v.PaddingBottom);
                var tabTitle = v.FindViewById<TextView>(Resource.Id.psts_tab_title);

                if (tabTitle != null)
                {
                    tabTitle.SetTextSize(ComplexUnitType.Px, _tabTextSize);

                    // setAllCaps() is only available from API 14, so the upper case is made manually if we are on a
                    // pre-ICS-build
                    if (_textAllCaps)
                    {
                        if ((int)Build.VERSION.SdkInt >= 14)
                        {
                            tabTitle.SetAllCaps(true);
                        }
                        else
                        {
                            tabTitle.Text = tabTitle.Text.ToUpperInvariant();
                        }
                    }
                }
            }
        }

        private void ScrollToChild(int position, int offset)
        {
            if (_tabCount == 0)
                return;
            var child = _tabsContainer.GetChildAt(position);
            if (child == null)
                return;
            var newScrollX = child.Left + offset;
            if (position > 0 || offset > 0)
            {

                //Half screen offset.
                //- Either tabs start at the middle of the view scrolling straight away
                //- Or tabs start at the begging (no padding) scrolling when indicator gets
                //  to the middle of the view width
                newScrollX -= _scrollOffset;
                GetIndicatorCoordinates(out var first, out var second);
                newScrollX += (int)((second - first) / 2f);
            }

            if (newScrollX != _lastScrollX)
            {
                _lastScrollX = newScrollX;
                ScrollTo(newScrollX, 0);
            }
        }

        private void GetIndicatorCoordinates(out float lineLeft, out float lineRight)
        {
            lineLeft = 0f;
            lineRight = 0f;
            var currentTab = _tabsContainer.GetChildAt(_currentPosition);
            if (currentTab == null)
                return;
            lineLeft = currentTab.Left;
            lineRight = currentTab.Right;

            // if there is an offset, start interpolating left and right coordinates between current and next tab
            if (_currentPositionOffset > 0f && _currentPosition < _tabCount - 1)
            {

                var nextTab = _tabsContainer.GetChildAt(_currentPosition + 1);
                float nextTabLeft = nextTab.Left;
                float nextTabRight = nextTab.Right;

                lineLeft = (_currentPositionOffset * nextTabLeft + (1f - _currentPositionOffset) * lineLeft);
                lineRight = (_currentPositionOffset * nextTabRight + (1f - _currentPositionOffset) * lineRight);
            }
        }

        protected override void OnLayout(bool changed, int left, int top, int right, int bottom)
        {
            if (_isPaddingMiddle || _paddingLeft > 0 || _paddingRight > 0)
            {
                _tabsContainer.SetMinimumWidth(Width);
                SetClipToPadding(false);
            }

            if (_tabsContainer.ChildCount > 0)
            {
                _tabsContainer.GetChildAt(0).ViewTreeObserver.AddOnGlobalLayoutListener(this);
            }
            base.OnLayout(changed, left, top, right, bottom);
        }

        public void OnGlobalLayout()
        {
            var view = _tabsContainer.GetChildAt(0);

            if ((int)Build.VERSION.SdkInt < 16)
            {
                ViewTreeObserver.RemoveGlobalOnLayoutListener(this);
            }
            else
            {
                ViewTreeObserver.RemoveOnGlobalLayoutListener(this);
            }

            if (_isPaddingMiddle)
            {
                var halfWidthFirstTab = view.Width / 2;
                _paddingLeft = _paddingRight = Width / 2 - halfWidthFirstTab;
            }

            SetPadding(_paddingLeft, PaddingTop, _paddingRight, PaddingBottom);
            if (_scrollOffset == 0)
                _scrollOffset = Width / 2 - _paddingLeft;


            _currentPosition = _pager.CurrentItem;
            _currentPositionOffset = 0f;
            ScrollToChild(_currentPosition, 0);
            UpdateSelection(_currentPosition);
        }

        protected override void OnDraw(Canvas canvas)
        {
            base.OnDraw(canvas);
            if (IsInEditMode || _tabCount == 0)
                return;

            var height = Height;
            //draw indicator line
            _rectPaint.Color = new Color(_indicatorColor);
            float first, second = 0f;
            GetIndicatorCoordinates(out first, out second);
            canvas.DrawRect(first + _paddingLeft, height - _indicatorHeight, second + _paddingLeft, height, _rectPaint);

            //draw underline
            _rectPaint.Color = new Color(_underlineColor);
            canvas.DrawRect(_paddingLeft, height - _underlineHeight, _tabsContainer.Width + _paddingRight, height, _rectPaint);

            //draw divider
            if (_dividerWidth <= 0)
                return;

            _dividerPaint.StrokeWidth = _dividerWidth;
            _dividerPaint.Color = new Color(_dividerColor);

            var offset = IsPaddingMiddle ? _paddingLeft : 0F;

            for (var i = 0; i < _tabCount - 1; i++)
            {
                var tab = _tabsContainer.GetChildAt(i);
                if (tab != null)
                {
                    canvas.DrawLine(offset + tab.Right, _dividerPadding, offset + tab.Right, height - _dividerPadding, _dividerPaint);

                }
            }

        }
        
        #region IOnPageChangeListener Implentation
        public void OnPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
            _currentPosition = position;
            _currentPositionOffset = positionOffset;
            var child = _tabsContainer.GetChildAt(position);
            if (child == null)
                return;
            var offset = _tabCount > 0 ? (int)(positionOffset * child.Width) : 0;
            ScrollToChild(position, offset);
            Invalidate();
            if (OnPageChangeListener != null)
            {
                OnPageChangeListener.OnPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        public void OnPageScrollStateChanged(int state)
        {
            if (state == ViewPager.ScrollStateIdle)
            {
                ScrollToChild(_pager.CurrentItem, 0);
            }
            //Full textAlpha for current item
            var currentTab = _tabsContainer.GetChildAt(_pager.CurrentItem);
            Selected(currentTab);
            //Half transparent for prev item
            if (_pager.CurrentItem - 1 >= 0)
            {
                var prevTab = _tabsContainer.GetChildAt(_pager.CurrentItem - 1);
                NotSelected(prevTab);
            }
            //Half transparent for next item
            if (_pager.CurrentItem + 1 <= _pager.Adapter.Count - 1)
            {
                var nextTab = _tabsContainer.GetChildAt(_pager.CurrentItem + 1);
                NotSelected(nextTab);
            }

            if (OnPageChangeListener != null)
            {
                OnPageChangeListener.OnPageScrollStateChanged(state);
            }
        }



        public void OnPageSelected(int position)
        {
            UpdateSelection(position);
            if (OnPageChangeListener != null)
            {
                OnPageChangeListener.OnPageSelected(position);
            }
        }
        #endregion

        private void UpdateSelection(int position)
        {
            for (var i = 0; i < _tabCount; ++i)
            {
                var tv = _tabsContainer.GetChildAt(i);
                if (tv == null)
                    continue;

                var selected = i == position;
                tv.Selected = selected;
                if (selected)
                {
                    Selected(tv);
                }
                else
                {
                    NotSelected(tv);
                }
            }
        }

        private void NotSelected(View tab)
        {
            var title = tab?.FindViewById<TextView>(Resource.Id.psts_tab_title);
            if (title == null)
                return;

            title.SetTypeface(_tabTypeface, _tabTypefaceStyle);
            title.SetTextColor(_tabTextColor);
        }

        private void Selected(View tab)
        {
            var title = tab?.FindViewById<TextView>(Resource.Id.psts_tab_title);
            if (title == null)
                return;

            title.SetTypeface(_tabTypeface, _tabTypefaceSelectedStyle);
            title.SetTextColor(_tabTextColorSelected);
        }

        protected class PagerAdapterObserver : DataSetObserver
        {
            public bool IsAttached { get; set; }
            readonly PagerSlidingTabStrip _strip;
            public PagerAdapterObserver(PagerSlidingTabStrip strip)
            {
                _strip = strip;
            }
            public override void OnChanged()
            {
                _strip.NotifyDataSetChanged();
            }
        }

        protected override void OnAttachedToWindow()
        {
            base.OnAttachedToWindow();
            if (_pager == null || !_adapterObserver.IsAttached)
                return;

            _pager.Adapter.UnregisterDataSetObserver(_adapterObserver);
            _adapterObserver.IsAttached = false;
        }

        protected override void OnRestoreInstanceState(IParcelable state)
        {
            if (state is Bundle bundle)
            {
                if (bundle.GetParcelable("base") is IParcelable superState)
                    base.OnRestoreInstanceState(superState);

                _currentPosition = bundle.GetInt("currentPosition", 0);
                if (_currentPosition != 0 && _tabsContainer.ChildCount > 0)
                {
                    NotSelected(_tabsContainer.GetChildAt(0));
                    Selected(_tabsContainer.GetChildAt(_currentPosition));
                }
            }

            RequestLayout();
        }

        protected override IParcelable OnSaveInstanceState()
        {
            var superState = base.OnSaveInstanceState();
            var state = new Bundle();
            state.PutParcelable("base", superState);
            state.PutInt("currentPosition", _currentPosition);
            return state;
        }

        public class PagerSlidingTabStripState : BaseSavedState
        {
            public int CurrentPosition { get; set; }
            public PagerSlidingTabStripState(IParcelable superState)
              : base(superState)
            {

            }

            public PagerSlidingTabStripState(Parcel source)
              : base(source)
            {
                CurrentPosition = source.ReadInt();
            }

            public override void WriteToParcel(Parcel dest, ParcelableWriteFlags flags)
            {
                base.WriteToParcel(dest, flags);
                dest.WriteInt(CurrentPosition);
            }
        }
    }
}

