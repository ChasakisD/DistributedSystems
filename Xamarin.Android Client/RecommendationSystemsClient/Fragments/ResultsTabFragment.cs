using Android.OS;
using Android.Support.V4.App;
using Android.Support.V4.View;
using Android.Util;
using Android.Views;
using com.refractored;
using CheeseBind;
using Java.Lang;
using Fragment = Android.Support.V4.App.Fragment;
using FragmentManager = Android.Support.V4.App.FragmentManager;
using String = Java.Lang.String;

#pragma warning disable 649

namespace RecommendationSystemsClient.Fragments
{
    public class ResultsTabFragment : BaseFragment
    {
        [BindView(Resource.Id.pager)]
        private ViewPager _pager;

        [BindView(Resource.Id.tabs)]
        private PagerSlidingTabStrip _tabs;

        protected override int LayoutResource => Resource.Layout.ResultsTabFragment;

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            /* Call BaseFragment to Bind the CheeseKnife :) */
            var root = base.OnCreateView(inflater, container, savedInstanceState);
            
            _pager.Adapter = new ResultsPagerAdapter(Activity.SupportFragmentManager);
            _pager.PageMargin = (int)TypedValue.ApplyDimension(ComplexUnitType.Dip, 4, Resources.DisplayMetrics);

            _tabs.SetViewPager(_pager);
            
            return root;
        }

        public class ResultsPagerAdapter : FragmentPagerAdapter
        {
            private readonly string[] _titles =
            {
                "Poi Results", "Pois in Map"
            };
            
            public override int Count => _titles.Length;

            public ResultsPagerAdapter(FragmentManager fm) : base(fm) { }

            public override ICharSequence GetPageTitleFormatted(int position)
            {
                return new String(_titles[position]);
            }
                       
            public override Fragment GetItem(int position)
            {
                switch (position)
                {
                    case 0:
                        return new SearchUserFragment();
                    default:
                        return new Fragment();
                }
            }
        }
    }
}