using System;
using Android.App;
using Android.Graphics;
using Android.OS;
using Android.Support.Design.Widget;
using Android.Support.V4.View;
using Android.Support.V7.App;
using Android.Views;
using Plugin.Iconize;
using Plugin.Iconize.Droid.Controls;
using Plugin.Iconize.Fonts;
using RecommendationSystemsClient.Fragments;
using Toolbar = Android.Support.V7.Widget.Toolbar;

namespace RecommendationSystemsClient.Activities
{
    [Activity(
        Label = "RecommendationSystemsClient", 
        Icon = "@mipmap/ic_launcher",
        MainLauncher = true)]
    public class MainActivity : AppCompatActivity
    {
        private Toolbar _toolbar;
        private Android.Support.V4.Widget.DrawerLayout _drawerLayout;
        private NavigationView _navigationView;
        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            SetContentView(Resource.Layout.Main);
            
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
                /*
                // Open Fragments Here
                switch (item.MenuItem.ItemId)
                {
                        
                }
                */
            };

            _navigationView.SetCheckedItem(Resource.Id.nav_popular);
            try
            {
                SupportFragmentManager
                    .BeginTransaction()
                    .Replace(Resource.Id.selected_fragment, new SecondFragment())
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

