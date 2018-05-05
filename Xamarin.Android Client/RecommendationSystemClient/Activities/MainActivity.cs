using Android.App;
using Android.Graphics;
using Android.OS;
using Android.Support.Design.Widget;
using Android.Support.V4.View;
using Android.Views;
using CheeseBind;
using Plugin.Iconize;
using Plugin.Iconize.Droid.Controls;
using Plugin.Iconize.Fonts;
using RecommendationSystemClient.Fragments;
using Exception = System.Exception;
using Toolbar = Android.Support.V7.Widget.Toolbar;

#pragma warning disable 649

namespace RecommendationSystemClient.Activities
{
    [Activity(MainLauncher = false)]
    public class MainActivity : BaseActivity
    {
        [BindView(Resource.Id.toolbar)]
        private Toolbar _toolbar;

        [BindView(Resource.Id.drawer_layout)]
        private Android.Support.V4.Widget.DrawerLayout _drawerLayout;

        [BindView(Resource.Id.nav_view)]
        private NavigationView _navigationView;

        [BindView(Resource.Id.collapsingToolBar)]
        public CollapsingToolbarLayout CollapsingToolbar;

        protected override int LayoutResource => Resource.Layout.Main;

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);

            /* Needs to be registered only on the MainLauncher */
            Iconize.With(new MaterialModule());
            
            SetSupportActionBar(_toolbar);
            SupportActionBar.SetHomeButtonEnabled(true);
            SupportActionBar.SetDisplayHomeAsUpEnabled(true);

            /*
             * See Material Font Cheetsheat Here: https://goo.gl/FMCiR9
             */
            var icon = new IconDrawable(this, "md-menu");
            icon.Color(Color.White);
            
            /* Sets the size to the action bar, ready to put it there */
            icon.ActionBarSize();

            /* Add the icon the the ActionBar */
            SupportActionBar.SetHomeAsUpIndicator(icon);
            
            /* Implement the Hamburger Menu Selection Changed */
            _navigationView.NavigationItemSelected += (sender, item) =>
            {
                // Open Fragments Here
                Android.Support.V4.App.Fragment selectedFragment;
                switch (item.MenuItem.ItemId)
                {
                    case Resource.Id.recommendations:
                        selectedFragment = new SearchUserFragment();
                        break;
                    default:
                        selectedFragment = new ResultsTabFragment();
                        break;
                }
                
                try
                {

                    SupportFragmentManager
                        .BeginTransaction()
                        .Replace(Resource.Id.fragmentContainer, selectedFragment)
                        .Commit();

                    item.MenuItem.SetChecked(true);

                    _drawerLayout.CloseDrawers();
                }
                catch (Exception e)
                {
                    System.Diagnostics.Debug.WriteLine(e.Message);
                }
            };

            _navigationView.SetCheckedItem(Resource.Id.recommendations);
            try
            {
                SupportFragmentManager
                    .BeginTransaction()
                    .Replace(Resource.Id.fragmentContainer, new SearchUserFragment())
                    .Commit();
            }
            catch (Exception e)
            {
                System.Diagnostics.Debug.WriteLine(e.Message);
            }
        }

        public override bool OnOptionsItemSelected(IMenuItem item)
        {
            if (item.ItemId != Android.Resource.Id.Home) return base.OnOptionsItemSelected(item);

            _drawerLayout.OpenDrawer(GravityCompat.Start);
            return true;
        }

        
    }
}

