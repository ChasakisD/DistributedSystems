using Android.Content;
using Android.Graphics;
using Android.OS;
using Android.Support.V4.View;
using Android.Util;
using Android.Views;
using Android.Widget;

namespace RecommendationSystemClient.Layouts.ArcLayout
{
    public class CustomOutlineProvider : ViewOutlineProvider
    {
        public Path Path;
        public override void GetOutline(View view, Outline outline)
        {
            outline.SetConvexPath(Path);
        }
    }

    public class ArcLayout : FrameLayout
    {
        private ArcLayoutSettings _settings;
        
        private Path _clipPath;

        public ArcLayout(Context context) : base(context)
        {
            Init(context, null);
        }

        public ArcLayout(Context context, IAttributeSet attrs) : base(context, attrs)
        {
            Init(context, attrs);
        }

        public void Init(Context context, IAttributeSet attrs)
        {
            _settings = new ArcLayoutSettings(context, attrs){ Elevation = ViewCompat.GetElevation(this)};
            if (Build.VERSION.SdkInt < BuildVersionCodes.JellyBeanMr2)
            {
                SetLayerType(LayerType.Software, null);
            }
        }

        protected override void OnLayout(bool changed, int left, int top, int right, int bottom)
        {
            base.OnLayout(changed, left, top, right, bottom);

            if (!changed || _settings == null) return;

            if (MeasuredWidth <= 0 || MeasuredHeight <= 0) return;

            _clipPath = CreateClipPath();
            ViewCompat.SetElevation(this, _settings.Elevation);
            OutlineProvider = new CustomOutlineProvider{Path = _clipPath};
        }

        protected override void DispatchDraw(Canvas canvas)
        {
            var paint = new Paint {AntiAlias = true, Color = Color.White};
            var saveCount = canvas.SaveLayer(0, 0, Width, Height, null);
            base.DispatchDraw(canvas);
            paint.SetXfermode(new PorterDuffXfermode(PorterDuff.Mode.Multiply));
            canvas.DrawPath(_clipPath, paint);
            canvas.RestoreToCount(saveCount);
            paint.SetXfermode(null);
        }

        private Path CreateClipPath()
        {
            var path = new Path();

            var arcHeight = _settings.ArcHeight;
            var halfWidth = MeasuredWidth / 2;
            var halfHeight = MeasuredHeight / 2;

            switch (_settings.Position)
            {
                case ArcLayoutPosition.PositionBottom:
                    if (_settings.IsCropInside)
                    {
                        path.MoveTo(0, 0);
                        path.LineTo(0, MeasuredHeight);
                        path.QuadTo(halfWidth, MeasuredHeight - 2 * arcHeight, MeasuredWidth, MeasuredHeight);
                        path.LineTo(MeasuredWidth, 0);
                        path.Close();
                    }
                    else
                    {
                        path.MoveTo(0, 0);
                        path.LineTo(0, MeasuredHeight - arcHeight);
                        path.QuadTo(halfWidth, MeasuredHeight + arcHeight, MeasuredWidth, MeasuredHeight - arcHeight);
                        path.LineTo(MeasuredWidth, 0);
                        path.Close();
                    }
                    break;
                case ArcLayoutPosition.PositionTop:
                    if (_settings.IsCropInside)
                    {
                        path.MoveTo(0, MeasuredHeight);
                        path.LineTo(0, 0);
                        path.QuadTo(halfWidth, 2 * arcHeight, MeasuredWidth, 0);
                        path.LineTo(MeasuredWidth, MeasuredHeight);
                        path.Close();
                    }
                    else
                    {
                        path.MoveTo(0, arcHeight);
                        path.QuadTo(halfWidth, -arcHeight, MeasuredWidth, arcHeight);
                        path.LineTo(MeasuredWidth, MeasuredHeight);
                        path.LineTo(0, MeasuredHeight);
                        path.Close();
                    }
                    break;
                case ArcLayoutPosition.PositionLeft:
                    if (_settings.IsCropInside)
                    {
                        path.MoveTo(MeasuredWidth, 0);
                        path.LineTo(0, 0);
                        path.QuadTo(arcHeight * 2, halfHeight, 0, MeasuredHeight);
                        path.LineTo(MeasuredWidth, MeasuredHeight);
                        path.Close();
                    }
                    else
                    {
                        path.MoveTo(MeasuredWidth, 0);
                        path.LineTo(arcHeight, 0);
                        path.QuadTo(-arcHeight, halfHeight, arcHeight, MeasuredHeight);
                        path.LineTo(MeasuredWidth, MeasuredHeight);
                        path.Close();
                    }
                    break;
                case ArcLayoutPosition.PositionRight:
                    if (_settings.IsCropInside)
                    {
                        path.MoveTo(0, 0);
                        path.LineTo(MeasuredWidth, 0);
                        path.QuadTo(MeasuredWidth - arcHeight * 2, halfHeight, MeasuredWidth, MeasuredHeight);
                        path.LineTo(0, MeasuredHeight);
                        path.Close();
                    }
                    else
                    {
                        path.MoveTo(0, 0);
                        path.LineTo(MeasuredWidth - arcHeight, 0);
                        path.QuadTo(MeasuredWidth + arcHeight, halfHeight, MeasuredWidth - arcHeight, MeasuredHeight);
                        path.LineTo(0, MeasuredHeight);
                        path.Close();
                    }
                    break;
                default:
                    return null;
            }

            return path;
        }
    }
}