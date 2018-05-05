using Android.OS;
using Android.Support.V4.App;
using Android.Support.V4.View;
using Android.Util;
using Android.Views;
using CheeseBind;
using Java.Lang;
using RecommendationSystemClient.Layouts.Pager;

#pragma warning disable 649

namespace RecommendationSystemClient.Fragments
{
    public class ResultsTabFragment : BaseFragment
    {
        [BindView(Resource.Id.pager)]
        private ViewPager _pager;

        [BindView(Resource.Id.tabs)]
        private PagerSlidingTabStrip _tabs;

        protected override int LayoutResource => Resource.Layout.ResultsTabFragment;
        protected override string Title => "Search User";

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            var root = base.OnCreateView(inflater, container, savedInstanceState);

            /* Add the Tabs */
            _pager.Adapter = new ResultsPagerAdapter(Activity.SupportFragmentManager);
            _pager.PageMargin = (int)TypedValue.ApplyDimension(ComplexUnitType.Dip, 4, Resources.DisplayMetrics);

            _tabs.SetViewPager(_pager);

            return root;
        }
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

        public override Android.Support.V4.App.Fragment GetItem(int position)
        {
            switch (position)
            {
                case 0:
                    return new SearchUserFragment();
                default:
                    return new Android.Support.V4.App.Fragment();
            }
        }
    }
}