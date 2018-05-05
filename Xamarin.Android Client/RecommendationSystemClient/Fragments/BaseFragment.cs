using Android.OS;
using Android.Util;
using Android.Views;
using CheeseBind;
using RecommendationSystemClient.Activities;

namespace RecommendationSystemClient.Fragments
{
    public abstract class BaseFragment : Android.Support.V4.App.Fragment
    {
        protected abstract int LayoutResource { get; }
        protected abstract string Title { get; }

        public override View OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            var root = inflater.Inflate(LayoutResource, container, false);

            /* Bind CheeseKnife */
            Cheeseknife.Bind(this, root);
            
            if (!(Activity is MainActivity activity)) return root;
            if (activity.CollapsingToolbar == null) return root;

            /* Set the Title */
            activity.CollapsingToolbar.Title = Title;
            
            return root;
        }
    }
}