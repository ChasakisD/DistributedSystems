using Android.OS;
using Android.Views;
using CheeseBind;

namespace RecommendationSystemsClient.Fragments
{
    public abstract class BaseFragment : Android.Support.V4.App.Fragment
    {
        protected abstract int LayoutResource { get; }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            var root = inflater.Inflate(LayoutResource, container, false);

            /* Bind CheeseKnife */
            Cheeseknife.Bind(this, root);

            return root;
        }
    }
}