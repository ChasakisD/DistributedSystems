using System;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using Android.App;
using Android.Content;
using Android.OS;
using Android.Support.V7.App;
using CheeseBind;
using Com.John.Waveview;

#pragma warning disable 649

namespace RecommendationSystemClient.Activities
{
    [Activity(
        Label = "RecommendationSystemClient",
        Icon = "@mipmap/ic_launcher", 
        MainLauncher = true, 
        NoHistory = true)]
    public class SplashActivity : AppCompatActivity
    {
        [BindView(Resource.Id.waveView)]
        private WaveView _waveView;

        protected override async void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            SetContentView(Resource.Layout.SplashActivity);

            Cheeseknife.Bind(this);
            
            await Task.Run(() =>
            {
                Enumerable.Range(0, 100).ToList().ForEach(i =>
                {
                    RunOnUiThread(() => _waveView.SetProgress(i));
                    Thread.Sleep(TimeSpan.FromMilliseconds(5));

                    if (i != 85) return;

                    new Task(() =>
                    {
                        StartActivity(new Intent(this, typeof(MainActivity)));
                        Finish();
                    })
                    .Start();
                    
                });
            });
            
        }
    }
}