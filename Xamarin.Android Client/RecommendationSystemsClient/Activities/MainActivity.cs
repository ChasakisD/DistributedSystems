using System;
using Android.App;
using Android.Graphics;
using Android.OS;
using Android.Support.Design.Widget;
using Android.Support.V4.View;
using Android.Support.V7.App;
using Android.Views;
using CheeseBind;
using Plugin.Iconize;
using Plugin.Iconize.Droid.Controls;
using Plugin.Iconize.Fonts;
using RecommendationSystemsClient.Fragments;
using Toolbar = Android.Support.V7.Widget.Toolbar;

namespace RecommendationSystemsClient.Activities
{
    [Activity(Label = "RecommendationSystemsClient")]
    public class MainActivity : AppCompatActivity
    {
        private Toolbar _toolbar;
        private Android.Support.V4.Widget.DrawerLayout _drawerLayout;
        private NavigationView _navigationView;
        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            SetContentView(Resource.Layout.Main);

            /* Bind The CheeseKnife */
            Cheeseknife.Bind(this);

            /* Needs to be registered only on the MainLauncher */
            Iconize.With(new MaterialModule());

            _toolbar = FindViewById<Toolbar>(Resource.Id.toolbar);
            _drawerLayout = FindViewById<Android.Support.V4.Widget.DrawerLayout>(Resource.Id.drawer_layout);
            _navigationView = FindViewById<NavigationView>(Resource.Id.nav_view);

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
                        .Replace(Resource.Id.selected_fragment, selectedFragment)
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
                    .Replace(Resource.Id.selected_fragment, new SearchUserFragment())
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

