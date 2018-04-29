using Android.OS;
using Android.Views;
using CheeseBind;

namespace RecommendationSystemsClient.Fragments
{
    public class SecondFragment : Android.Support.V4.App.Fragment
    {
        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            var root = inflater.Inflate(Resource.Layout.SecondFragment, container, false);

            Cheeseknife.Bind(this, root);

            return root;
        }
    }
}