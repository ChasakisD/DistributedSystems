using Android.OS;
using Android.Support.Design.Widget;
using Android.Support.V4.View;
using Android.Views;
using CheeseBind;
using RecommendationSystemClient.Adapters;

#pragma warning disable 649

namespace RecommendationSystemClient.Fragments
{
    public class ResultsTabFragment : BaseFragment
    {
        [BindView(Resource.Id.pager)]
        private ViewPager _pager;

        [BindView(Resource.Id.tabs)]
        private TabLayout _tabs;

        protected override int LayoutResource => Resource.Layout.ResultsTabFragment;
        protected override string Title => "Search User";

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            var root = base.OnCreateView(inflater, container, savedInstanceState);

            /* Add the Tabs */
            _pager.Adapter = new ResultsViewPagerAdapter(ChildFragmentManager);
            _tabs.SetupWithViewPager(_pager);
            
            return root;
        }
    }
}