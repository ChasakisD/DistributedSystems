using Java.Lang;
using Android.Support.V4.App;
using RecommendationSystemClient.Fragments;

namespace RecommendationSystemClient.Adapters
{
    public class ResultsViewPagerAdapter : FragmentPagerAdapter
    {
        private readonly string[] _titles =
        {
            "Poi Results", "Pois in Map"
        };

        public override int Count => _titles.Length;

        public ResultsViewPagerAdapter(FragmentManager fm) : base(fm) { }

        public override ICharSequence GetPageTitleFormatted(int position)
        {
            return new String(_titles[position]);
        }

        public override Fragment GetItem(int position)
        {
            switch (position)
            {
                case 0:
                    return new DummyFragment { No = 1 };
                default:
                    return new DummyFragment { No = 2 };
            }
        }
    }
}