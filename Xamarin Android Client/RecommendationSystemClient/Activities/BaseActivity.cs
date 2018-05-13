using Android.OS;
using Android.Support.V7.App;
using CheeseBind;

namespace RecommendationSystemClient.Activities
{
    public abstract class BaseActivity : AppCompatActivity
    {
        protected abstract int LayoutResource { get; }

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            SetContentView(LayoutResource);

            /* Bind CheeseKnife */
            Cheeseknife.Bind(this);
        }
    }
}