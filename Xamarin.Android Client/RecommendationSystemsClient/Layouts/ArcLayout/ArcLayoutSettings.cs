using Android.Content;
using Android.Util;

namespace RecommendationSystemsClient.Layouts.ArcLayout
{
    public class ArcLayoutSettings
    {
        public const int CropInside = 0;
        public const int CropOutside = 1;
        
        public bool IsCropInside { get; set; }
        public float ArcHeight { get; set; }
        public float Elevation { get; set; }
        public ArcLayoutPosition Position { get; set; }

        public ArcLayoutSettings(Context context, IAttributeSet attrs)
        {
            var styledAttributes = context.ObtainStyledAttributes(attrs, Resource.Styleable.ArcLayout, 0, 0);
            var sizeInPx = TypedValue.ApplyDimension(ComplexUnitType.Dip, 10, context.Resources.DisplayMetrics);

            ArcHeight = styledAttributes.GetDimension(Resource.Styleable.ArcLayout_arc_height, sizeInPx);
            IsCropInside = styledAttributes.GetInt(Resource.Styleable.ArcLayout_arc_cropDirection, CropInside)
                == CropInside;
            Position = (ArcLayoutPosition) styledAttributes.GetInt(Resource.Styleable.ArcLayout_arc_position, 
                (int) ArcLayoutPosition.PositionBottom);

            styledAttributes.Recycle();
        }
    }
}