using Android.Views;

namespace RecommendationSystemClient.Layouts.Pager
{
    public interface ICustomTabProvider
    {
        View GetCustomTabView(ViewGroup parent, int position);
    }
}

